package ice.finance.api.codec

import ice.finance.domain.AppError
import io.circe.Json

object ErrorEncoder {

  private val CodeInvalidRequest   = "invalid_request"
  private val CodeAmountOutOfRange = "amount_out_of_range"
  private val CodeNoMatchingTier   = "no_matching_tier"

  def encode(error: AppError): Json = {
    val code = error match {
      case _: AppError.InvalidRequest   => CodeInvalidRequest
      case _: AppError.AmountOutOfRange => CodeAmountOutOfRange
      case _: AppError.NoMatchingTier   => CodeNoMatchingTier
    }
    Json.obj("code" -> Json.fromString(code), "error" -> Json.fromString(error.message))
  }
}
