// Scala 2.13.12 + sbt 1.9.6 are kept from the original scaffold so the reviewer can build with
// the unchanged sbt toolchain. cats / cats-effect / fs2 / weaver-cats were already in the
// scaffold; http4s + circe were added to provide an HTTP/JSON layer in the same effect type.
ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / organization     := "ice.finance"
ThisBuild / organizationName := "ICE"

val http4sVersion  = "0.23.27"
val circeVersion   = "0.14.10"
val logbackVersion = "1.5.6"

lazy val root = (project in file("."))
  .settings(
    name := "commission-api",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-encoding", "utf8"),
    libraryDependencies ++= List(
      // From the original scaffold:
      "org.typelevel"       %% "cats-core"           % "2.10.0",
      "org.typelevel"       %% "cats-effect"         % "3.5.4",
      "co.fs2"              %% "fs2-core"            % "3.9.2",
      "co.fs2"              %% "fs2-io"              % "3.9.2",
      // Added to provide HTTP server + JSON in the same effect type:
      "org.http4s"          %% "http4s-ember-server" % http4sVersion,
      "org.http4s"          %% "http4s-dsl"          % http4sVersion,
      "org.http4s"          %% "http4s-circe"        % http4sVersion,
      "io.circe"            %% "circe-core"          % circeVersion,
      "io.circe"            %% "circe-generic"       % circeVersion,
      "io.circe"            %% "circe-parser"        % circeVersion,
      // SLF4J backend so log lines render at runtime:
      "ch.qos.logback"       % "logback-classic"     % logbackVersion,
      // From the original scaffold (test only):
      "com.disneystreaming" %% "weaver-cats"         % "0.8.3" % Test,
    ),
  )
