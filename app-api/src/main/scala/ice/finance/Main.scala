package ice.finance

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import ice.finance.api.routes.CommissionRoutes
import ice.finance.api.routes.SongRoutes
import ice.finance.config.AppConfig
import ice.finance.service.RateService
import ice.finance.service.SongService
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.EntityLimiter

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    server(AppConfig.load).use(_ => IO.never).as(ExitCode.Success)

  def server(config: AppConfig): Resource[IO, Server] =
    for {
      songs <- Resource.eval(SongService.loadFromResource[IO]())
      host  <- Resource.eval(parseHost(config.host))
      port  <- Resource.eval(parsePort(config.port))
      app = CORS.policy.withAllowOriginAll(
        routes(RateService.Default, songs, config.maxRequestBodyBytes).orNotFound
      )
      server <- EmberServerBuilder
        .default[IO]
        .withHost(host)
        .withPort(port)
        .withMaxConnections(config.maxConnections)
        .withHttpApp(app)
        .build
    } yield server

  private def routes(
      rateService: RateService,
      songService: SongService[IO],
      maxBodyBytes: Long
  ): HttpRoutes[IO] = {
    val commissions = EntityLimiter(CommissionRoutes.routes[IO](rateService), maxBodyBytes)
    val songs       = SongRoutes.routes[IO](songService)
    HttpRoutes(request => commissions.run(request).orElse(songs.run(request)))
  }

  private def parseHost(value: String): IO[Host] =
    IO.fromOption(Host.fromString(value))(new IllegalArgumentException(s"invalid host: $value"))

  private def parsePort(value: Int): IO[Port] =
    IO.fromOption(Port.fromInt(value))(new IllegalArgumentException(s"invalid port: $value"))
}
