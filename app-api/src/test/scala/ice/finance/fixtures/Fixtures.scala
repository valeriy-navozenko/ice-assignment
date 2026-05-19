package ice.finance.fixtures

import ice.finance.domain._

object Fixtures {

  val DefaultClientId: String = "client-001"

  val NinetyHundred: BigDecimal = BigDecimal(900)
  val TwoThousand: BigDecimal   = BigDecimal(2000)
  val FourThousand: BigDecimal  = BigDecimal(4000)

  val ReadmeServices: List[ServiceItem] = List(
    ServiceItem(1L, NinetyHundred),
    ServiceItem(2L, TwoThousand),
    ServiceItem(3L, FourThousand)
  )

  val ReadmeExampleRequest: CommissionRequest = CommissionRequest(DefaultClientId, ReadmeServices)
}
