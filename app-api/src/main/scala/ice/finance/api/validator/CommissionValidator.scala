package ice.finance.api.validator

import cats.syntax.all._
import ice.finance.domain._

object CommissionValidator {

  private def invalid(message: String): AppError = AppError.InvalidRequest(message)

  def validate(request: CommissionRequest): Either[AppError, CommissionRequest] =
    for {
      _        <- guardServicesNonEmpty(request.services)
      _        <- guardDuplicateIds(request.services)
      clientId <- guardClientId(request.clientId)
      _        <- request.services.traverse_(guardServiceItem)
    } yield request.copy(clientId = clientId)

  private def guardServicesNonEmpty(services: List[ServiceItem]): Either[AppError, Unit] =
    if (services.isEmpty) Left(invalid("services must be non-empty")) else Right(())

  private def guardDuplicateIds(services: List[ServiceItem]): Either[AppError, Unit] = {
    val ids        = services.map(_.id)
    val duplicates = ids.diff(ids.distinct).distinct
    if (duplicates.isEmpty) Right(())
    else Left(invalid(s"duplicate service ids: ${duplicates.sorted.mkString(", ")}"))
  }

  private def guardClientId(value: String): Either[AppError, String] = {
    val trimmed = value.trim
    if (trimmed.isEmpty) Left(invalid("clientId must not be blank")) else Right(trimmed)
  }

  private def guardServiceItem(item: ServiceItem): Either[AppError, Unit] =
    if (item.id <= 0) Left(invalid(s"service id must be a positive integer, got ${item.id}"))
    else if (item.amount < 0 || item.amount > AppError.MaxServiceAmount)
      Left(AppError.AmountOutOfRange(item.id))
    else Right(())
}
