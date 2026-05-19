package ice.finance.service

import ice.finance.domain._

trait RateService {
  def rates: List[Rate]
}

object RateService {

  // README rate table:
  //   0      .. 1,000              -> 10 %
  //   1,000  .. 3,000              ->  5 %
  //   3,000  .. MaxServiceAmount+1 ->  1 %
  private val DefaultRates: List[Rate] = List(
    Rate(BigDecimal(0), BigDecimal(1_000), BigDecimal("0.10")),
    Rate(BigDecimal(1_000), BigDecimal(3_000), BigDecimal("0.05")),
    Rate(BigDecimal(3_000), AppError.MaxServiceAmount + 1, BigDecimal("0.01"))
  )

  val Default: RateService = new RateService { val rates: List[Rate] = DefaultRates }
}
