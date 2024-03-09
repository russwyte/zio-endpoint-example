val scala3Version = "3.4.0"

val publishVersion = "0.1.0-SNAPSHOT"

val zioHttpLibs = Seq(
  "dev.zio" %% "zio-http" % "3.0.0-RC4+85-faace697-SNAPSHOT"
)

val zioTestLibs = Seq(
  "dev.zio" %% "zio-test" % "2.1-RC1" % Test,
  "dev.zio" %% "zio-test-sbt" % "2.1-RC1" % Test,
  "dev.zio" %% "zio-test-magnolia" % "2.1-RC1" % Test
)

val zioSharedLibs = Seq(
  "dev.zio" %% "zio" % "2.1-RC1",
  "dev.zio" %% "zio-json" % "0.6.2",
  "dev.zio" %% "zio-streams" % "2.1-RC1",
  "dev.zio" %% "zio-metrics-connectors" % "2.2.1", // core library
  "dev.zio" %% "zio-metrics-connectors-statsd" % "2.2.1" // StatsD client
)

val neotypeLibs = Seq(
  "io.github.kitlangton" %% "neotype" % "0.2.3",
  "io.github.kitlangton" %% "neotype-zio-schema" % "0.2.3",
  "io.github.kitlangton" %% "neotype-zio-json" % "0.2.3"
)

val sharedLibs = zioSharedLibs ++ neotypeLibs ++ zioTestLibs
val clientAndServerLibs = sharedLibs ++ zioHttpLibs

ThisBuild / resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
ThisBuild / scalaVersion := scala3Version
ThisBuild / version := publishVersion

lazy val root = project
  .in(file("."))
  .settings(
    name := "endpoints-example",
    description := "An example of using ZIO-HTTP endpoints with OpenAPI docs and a client"
  )
  .aggregate(service, endpoints, server, client)

lazy val service = project
  .in(file("service"))
  .settings(
    name := "service",
    libraryDependencies ++= sharedLibs
  )

lazy val endpoints = project
  .in(file("endpoints"))
  .settings(
    name := "endpoints",
    libraryDependencies ++= clientAndServerLibs
  )

lazy val server = project
  .in(file("server"))
  .settings(
    name := "server",
    libraryDependencies ++= clientAndServerLibs
  )
  .dependsOn(service, endpoints)

lazy val client = project
  .in(file("client"))
  .settings(
    name := "client",
    libraryDependencies ++= clientAndServerLibs
  )
  .dependsOn(endpoints)
