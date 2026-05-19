package ice.finance.api.routes

import cats.effect.IO
import ice.finance.domain.Song
import ice.finance.service.SongService
import io.circe.Json
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.implicits._
import weaver.SimpleIOSuite

object SongRoutesSpec extends SimpleIOSuite {

  private val sample: List[Song] = (1 to 100).toList.map { index =>
    Song.of(index.toLong, s"title-$index", s"author-$index", BigDecimal("0.50")).toOption.get
  }

  private val routes: HttpRoutes[IO] = SongRoutes.routes[IO](SongService.fromList[IO](sample))

  test("GET /songs returns the default page when no query params are supplied") {
    routes.orNotFound.run(Request[IO](Method.GET, uri"/songs")).flatMap { response =>
      response.as[Json].map { body =>
        expect.all(
          response.status == Status.Ok,
          body.hcursor.get[Int]("page").toOption.contains(1),
          body.hcursor.get[Int]("pageSize").toOption.contains(50),
          body.hcursor.get[Int]("total").toOption.contains(100),
          body.hcursor.get[Int]("totalPages").toOption.contains(2),
          body.hcursor.downField("items").as[List[Json]].toOption.exists(_.size == 50)
        )
      }
    }
  }

  test("GET /songs honours explicit page and pageSize") {
    routes.orNotFound.run(Request[IO](Method.GET, uri"/songs?page=2&pageSize=25")).flatMap { response =>
      response.as[Json].map { body =>
        expect.all(
          response.status == Status.Ok,
          body.hcursor.get[Int]("page").toOption.contains(2),
          body.hcursor.get[Int]("pageSize").toOption.contains(25),
          body.hcursor.downField("items").as[List[Json]].toOption.exists(_.size == 25)
        )
      }
    }
  }
}
