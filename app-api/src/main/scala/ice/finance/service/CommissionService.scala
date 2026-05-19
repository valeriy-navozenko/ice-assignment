package ice.finance.service

import cats.syntax.all._
import ice.finance.domain._

import scala.math.BigDecimal.RoundingMode

object CommissionService {

  /** Scale and rounding applied to every monetary value the API returns. */
  private val CurrencyScale: Int                   = 2
  private val CurrencyRounding: RoundingMode.Value = RoundingMode.HALF_UP

  def calculate(
      rates: List[Rate],
      clientId: String,
      services: List[ServiceItem]
  ): Either[AppError, CommissionResponse] =
    services.traverse(lineFor(rates, _)).map { lines =>
      val total = lines.foldLeft(BigDecimal(0))(_ + _.commission)
      CommissionResponse(clientId, lines, total.setScale(CurrencyScale, CurrencyRounding))
    }

  private def lineFor(rates: List[Rate], item: ServiceItem): Either[AppError, CommissionLine] =
    rates.find(_.contains(item.amount)) match {
      case Some(rate) =>
        val amount     = item.amount.setScale(CurrencyScale, CurrencyRounding)
        val commission = (item.amount * rate.fraction).setScale(CurrencyScale, CurrencyRounding)
        Right(CommissionLine(item.id, amount, rate.fraction, commission))
      case None =>
        Left(AppError.NoMatchingTier(item.id, item.amount))
    }
}
