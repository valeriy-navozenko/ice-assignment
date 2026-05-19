package ice.finance.api.codec

import ice.finance.domain.Page
import ice.finance.domain.Song
import io.circe.Encoder
import io.circe.Json

object SongsCodecs {

  implicit val songEncoder: Encoder[Song] = Encoder.instance { song =>
    Json.obj(
      "id"       -> Json.fromLong(song.id),
      "title"    -> Json.fromString(song.title),
      "author"   -> Json.fromString(song.author),
      "progress" -> Json.fromBigDecimal(song.progress)
    )
  }

  implicit def pageEncoder[T: Encoder]: Encoder[Page[T]] = Encoder.instance { page =>
    Json.obj(
      "items"      -> Json.fromValues(page.items.map(Encoder[T].apply)),
      "page"       -> Json.fromInt(page.page),
      "pageSize"   -> Json.fromInt(page.pageSize),
      "total"      -> Json.fromInt(page.total),
      "totalPages" -> Json.fromInt(page.totalPages)
    )
  }
}
