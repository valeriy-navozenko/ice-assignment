package ice.finance.service

import cats.effect.Sync
import cats.syntax.all._
import ice.finance.domain.Page
import ice.finance.domain.Pagination
import ice.finance.domain.Song
import io.circe.Decoder
import io.circe.parser

import scala.io.Source

trait SongService[F[_]] {
  def findPage(page: Int, pageSize: Int): F[Page[Song]]
  def total: F[Int]
}

object SongService {

  val DefaultResourcePath: String = "songs.json"

  private val JsonCharset: String = "UTF-8"
  private val MinTotalPages: Int  = 1

  private final case class RawSong(id: Long, title: String, author: String, progress: BigDecimal)

  private implicit val rawSongDecoder: Decoder[RawSong] =
    Decoder.forProduct4("id", "title", "author", "progress")(RawSong.apply)

  def loadFromResource[F[_]: Sync](path: String = DefaultResourcePath): F[SongService[F]] =
    Sync[F]
      .delay {
        val stream = Option(getClass.getClassLoader.getResourceAsStream(path))
          .getOrElse(throw new IllegalStateException(s"missing classpath resource: $path"))
        try Source.fromInputStream(stream, JsonCharset).mkString
        finally stream.close()
      }
      .flatMap { jsonText =>
        Sync[F].fromEither(parseAndValidate(jsonText).left.map(new IllegalStateException(_)))
      }
      .map(fromList[F])

  def fromList[F[_]: Sync](source: List[Song]): SongService[F] = new SongService[F] {
    private val all = source

    def findPage(page: Int, pageSize: Int): F[Page[Song]] = Sync[F].pure {
      val safePageSize = math.min(math.max(Pagination.MinPageSize, pageSize), Pagination.Max)
      val total        = all.size
      val totalPages   = math.max(MinTotalPages, math.ceil(total.toDouble / safePageSize).toInt)
      val clampedPage  = math.min(math.max(Pagination.MinPage, page), totalPages)
      val start        = (clampedPage - 1) * safePageSize
      Page(
        items = all.slice(start, start + safePageSize),
        page = clampedPage,
        pageSize = safePageSize,
        total = total,
        totalPages = totalPages
      )
    }

    val total: F[Int] = Sync[F].pure(all.size)
  }

  private def parseAndValidate(json: String): Either[String, List[Song]] =
    for {
      rawSongs <- parser
        .decode[List[RawSong]](json)
        .left
        .map(error => s"songs.json parse error: ${error.getMessage}")
      songs <- rawSongs.zipWithIndex.foldLeft[Either[String, List[Song]]](Right(List.empty)) {
        case (acc, (rawSong, index)) =>
          for {
            previous <- acc
            song <- Song
              .of(rawSong.id, rawSong.title, rawSong.author, rawSong.progress)
              .left
              .map(reason => s"songs.json row $index invalid: $reason")
          } yield previous :+ song
      }
    } yield songs
}
