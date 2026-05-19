package ice.finance.domain

sealed trait AppError extends Product with Serializable {
  def message: String
}

object AppError {

  /** Upper bound on accepted service amounts; mirrors the [0, 1,000,000] range from the README. */
  val MaxServiceAmount: BigDecimal = 1_000_000

  final case class InvalidRequest(message: String) extends AppError

  /** Service amount outside the supported range; the raw value is never echoed. */
  final case class AmountOutOfRange(serviceId: Long) extends AppError {
    val message: String =
      s"amount for service $serviceId is outside the supported [0, $MaxServiceAmount] range"
  }

  final case class NoMatchingTier(serviceId: Long, amount: BigDecimal) extends AppError {
    val message: String = "no matching rate tier for one of the supplied service amounts"
  }
}
