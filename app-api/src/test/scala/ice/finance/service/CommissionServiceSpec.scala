package ice.finance.service

import ice.finance.domain._
import ice.finance.fixtures.Fixtures
import weaver.FunSuite

object CommissionServiceSpec extends FunSuite {

  private val defaultRates: List[Rate]     = RateService.Default.rates
  private val constrainedRates: List[Rate] = List(Rate(BigDecimal(0), BigDecimal(500), BigDecimal("0.10")))

  test("calculates the README example: 900 -> 90, 2000 -> 100, 4000 -> 40, total 230") {
    val result = CommissionService.calculate(defaultRates, Fixtures.DefaultClientId, Fixtures.ReadmeServices)
    val response = result.toOption.get
    expect.all(
      response.lines.map(_.commission) == List(
        BigDecimal("90.00"),
        BigDecimal("100.00"),
        BigDecimal("40.00")
      ),
      response.total == BigDecimal("230.00")
    )
  }

  test("amount on a tier boundary uses the upper tier (half-open intervals)") {
    val result = CommissionService.calculate(
      defaultRates,
      Fixtures.DefaultClientId,
      List(ServiceItem(1L, BigDecimal(1000)))
    )
    expect(result.toOption.get.lines.head.rate == BigDecimal("0.05"))
  }

  test("returns NoMatchingTier when an amount falls outside the configured rate table") {
    val result = CommissionService.calculate(
      constrainedRates,
      Fixtures.DefaultClientId,
      List(ServiceItem(7L, BigDecimal(600)))
    )
    expect(result.left.toOption.exists(_.isInstanceOf[AppError.NoMatchingTier]))
  }
}
