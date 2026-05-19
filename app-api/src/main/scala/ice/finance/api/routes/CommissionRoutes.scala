package ice.finance.api.routes

import cats.effect.Concurrent
import cats.syntax.all._
import ice.finance.api.codec.CommissionCodecs._
import ice.finance.api.codec.ErrorEncoder
import ice.finance.api.validator.CommissionValidator
import ice.finance.domain._
import ice.finance.service.CommissionService
import ice.finance.service.RateService
import io.circe.Json
import org.http4s.HttpRoutes
import org.http4s.Response
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object CommissionRoutes {

  private val HealthPath      = "health"
  private val CommissionsPath = "commissions"

  def routes[F[_]: Concurrent](rateService: RateService): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / HealthPath =>
        Ok(Json.obj("status" -> Json.fromString("ok")))

      case request @ POST -> Root / CommissionsPath =>
        request.attemptAs[CommissionRequest].value.flatMap {
          case Left(_) =>
            BadRequest(ErrorEncoder.encode(AppError.InvalidRequest("malformed request body")))
          case Right(domainRequest) =>
            CommissionValidator.validate(domainRequest) match {
              case Left(error) => respondWithError(dsl, error)
              case Right(validated) =>
                CommissionService.calculate(rateService.rates, validated.clientId, validated.services) match {
                  case Right(response) => Ok(response)
                  case Left(error)     => respondWithError(dsl, error)
                }
            }
        }
    }
  }

  private def respondWithError[F[_]: Concurrent](
      dsl: Http4sDsl[F],
      error: AppError
  ): F[Response[F]] = {
    import dsl._
    val payload = ErrorEncoder.encode(error)
    error match {
      case _: AppError.InvalidRequest   => BadRequest(payload)
      case _: AppError.AmountOutOfRange => UnprocessableEntity(payload)
      case _: AppError.NoMatchingTier   => UnprocessableEntity(payload)
    }
  }
}
