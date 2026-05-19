package ice.finance.api.routes

import cats.effect.Concurrent
import cats.syntax.all._
import ice.finance.api.PageQuery
import ice.finance.api.codec.SongsCodecs._
import ice.finance.service.SongService
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object SongRoutes {

  private val SongsPath: String = "songs"

  def routes[F[_]: Concurrent](service: SongService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / SongsPath :? PageQuery.Page(maybePage) +& PageQuery.PageSize(maybePageSize) =>
        service.findPage(PageQuery.page(maybePage), PageQuery.pageSize(maybePageSize)).flatMap(Ok(_))
    }
  }
}
