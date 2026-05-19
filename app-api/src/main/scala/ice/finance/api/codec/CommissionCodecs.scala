package ice.finance.api.codec

import ice.finance.domain._
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json

object CommissionCodecs {

  implicit val serviceItemDecoder: Decoder[ServiceItem] =
    Decoder.forProduct2("id", "amount")(ServiceItem.apply)

  implicit val commissionRequestDecoder: Decoder[CommissionRequest] =
    Decoder.forProduct2("clientId", "services")(CommissionRequest.apply)

  implicit val commissionResponseEncoder: Encoder[CommissionResponse] = Encoder.instance { response =>
    Json.obj(
      "clientId" -> Json.fromString(response.clientId),
      "lines"    -> Json.fromValues(response.lines.map(encodeLine)),
      "total"    -> Json.fromBigDecimal(response.total)
    )
  }

  private def encodeLine(line: CommissionLine): Json =
    Json.obj(
      "id"         -> Json.fromLong(line.serviceId),
      "amount"     -> Json.fromBigDecimal(line.amount),
      "rate"       -> Json.fromBigDecimal(line.rate),
      "commission" -> Json.fromBigDecimal(line.commission)
    )
}
