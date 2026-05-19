package ice.finance.api.routes

import cats.effect.IO
import ice.finance.domain._
import ice.finance.service._
import io.circe.Json
import io.circe.parser._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.headers.`Content-Type`
import org.http4s.implicits._
import weaver.SimpleIOSuite

object CommissionRoutesSpec extends SimpleIOSuite {

  private val routes: HttpRoutes[IO] = CommissionRoutes.routes[IO](RateService.Default)

  private val constrainedRoutes: HttpRoutes[IO] = {
    val singleTier = new RateService {
      val rates: List[Rate] = List(Rate(BigDecimal(0), BigDecimal(500), BigDecimal("0.10")))
    }
    CommissionRoutes.routes[IO](singleTier)
  }

  private val readmeJson = parse(
    """{ "clientId": "client-001", "services": [
      |  {"id": 1, "amount": 900},
      |  {"id": 2, "amount": 2000},
      |  {"id": 3, "amount": 4000}
      |] }""".stripMargin
  ).toOption.get

  test("POST /commissions returns the README example commissions and total") {
    routes.orNotFound
      .run(Request[IO](Method.POST, uri"/commissions").withEntity(readmeJson))
      .flatMap(response => response.as[Json].map(response -> _))
      .map { case (response, body) =>
        expect.all(
          response.status == Status.Ok,
          body.hcursor.get[BigDecimal]("total").toOption.contains(BigDecimal("230.00")),
          body.hcursor.downField("lines").as[List[Json]].toOption.exists(_.size == 3)
        )
      }
  }

  test("POST /commissions rejects an out-of-range amount with 422 (no value echoed)") {
    val payload = parse(
      """{ "clientId": "client-001", "services": [ {"id": 1, "amount": 1000001} ] }"""
    ).toOption.get
    routes.orNotFound
      .run(Request[IO](Method.POST, uri"/commissions").withEntity(payload))
      .flatMap(response => response.as[Json].map(response -> _))
      .map { case (response, body) =>
        expect.all(
          response.status == Status.UnprocessableEntity,
          body.hcursor.get[String]("code").toOption.contains("amount_out_of_range"),
          !body.hcursor.get[String]("error").toOption.exists(_.contains("1000001"))
        )
      }
  }

  test("POST /commissions rejects an empty services array with 400") {
    val payload = parse("""{ "clientId": "client-001", "services": [] }""").toOption.get
    routes.orNotFound
      .run(Request[IO](Method.POST, uri"/commissions").withEntity(payload))
      .map(response => expect(response.status == Status.BadRequest))
  }

  test("POST /commissions returns 422 no_matching_tier when the rate table cannot span the amount") {
    val payload = parse(
      """{ "clientId": "client-001", "services": [ {"id": 1, "amount": 600} ] }"""
    ).toOption.get
    constrainedRoutes.orNotFound
      .run(Request[IO](Method.POST, uri"/commissions").withEntity(payload))
      .flatMap(response => response.as[Json].map(response -> _))
      .map { case (response, body) =>
        expect.all(
          response.status == Status.UnprocessableEntity,
          body.hcursor.get[String]("code").toOption.contains("no_matching_tier")
        )
      }
  }

  test("POST /commissions rejects a malformed JSON body with a generic 400 error") {
    val malformed = Request[IO](Method.POST, uri"/commissions")
      .withEntity("this is not json")
      .withContentType(`Content-Type`(MediaType.application.json))
    routes.orNotFound
      .run(malformed)
      .flatMap(response => response.as[Json].map(response -> _))
      .map { case (response, body) =>
        expect.all(
          response.status == Status.BadRequest,
          body.hcursor.get[String]("code").toOption.contains("invalid_request"),
          !body.hcursor.get[String]("error").toOption.exists(_.contains("DownField"))
        )
      }
  }
}
