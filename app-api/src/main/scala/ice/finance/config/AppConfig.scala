package ice.finance.config

final case class AppConfig(
    host: String,
    port: Int,
    maxConnections: Int,
    maxRequestBodyBytes: Long
)

object AppConfig {

  /** 64 KiB body cap; sized for up to 1,000 service items, protects against payload DoS. */
  private val DefaultMaxBodyBytes: Long = 65_536L

  val Default: AppConfig = AppConfig(
    host = "0.0.0.0",
    port = 8080,
    maxConnections = 1_024,
    maxRequestBodyBytes = DefaultMaxBodyBytes
  )

  def load: AppConfig = AppConfig(
    host = stringEnv("APP_HOST", Default.host),
    port = intEnv("APP_PORT", Default.port, 1 to 65_535),
    maxConnections = intEnv("APP_MAX_CONNECTIONS", Default.maxConnections, 1 to Int.MaxValue),
    maxRequestBodyBytes = longEnv("APP_MAX_BODY_BYTES", Default.maxRequestBodyBytes, 1L)
  )

  private def stringEnv(key: String, fallback: String): String =
    sys.env.get(key).map(_.trim).filter(_.nonEmpty).getOrElse(fallback)

  private def intEnv(key: String, fallback: Int, validRange: Range): Int =
    sys.env.get(key).flatMap(_.trim.toIntOption).filter(validRange.contains).getOrElse(fallback)

  private def longEnv(key: String, fallback: Long, minimum: Long): Long =
    sys.env.get(key).flatMap(_.trim.toLongOption).filter(_ >= minimum).getOrElse(fallback)
}
