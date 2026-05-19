package ice.finance.domain

final case class ServiceItem(id: Long, amount: BigDecimal)

final case class CommissionRequest(clientId: String, services: List[ServiceItem])

final case class CommissionLine(serviceId: Long, amount: BigDecimal, rate: BigDecimal, commission: BigDecimal)

final case class CommissionResponse(
    clientId: String,
    lines: List[CommissionLine],
    total: BigDecimal
)
