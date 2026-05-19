package ice.finance.domain

import weaver.FunSuite

object RateSpec extends FunSuite {

  private val rate = Rate(BigDecimal(0), BigDecimal(1000), BigDecimal("0.10"))

  test("Rate.contains uses a half-open [from, to) interval") {
    expect.all(
      rate.contains(BigDecimal(0)),
      rate.contains(BigDecimal(999.99)),
      !rate.contains(BigDecimal(1000)),
      !rate.contains(BigDecimal(-1))
    )
  }
}
