val scala3Version = "3.4.1"

val publishVersion = "0.1.0-SNAPSHOT"

val zioHttpLibs = Seq(
  "dev.zio" %% "zio-http" % "3.0.0-RC6"
)

val zioVersion = "2.1.0-RC3"

val zioTestLibs = Seq(
  "dev.zio" %% "zio-test"          % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt"      % zioVersion % Test,
  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
)

val zioSharedLibs = Seq(
  "dev.zio" %% "zio"                   % zioVersion,
  "dev.zio" %% "zio-json"              % "0.6.2",
  "dev.zio" %% "zio-schema"            % "1.1.0",
  "dev.zio" %% "zio-schema-derivation" % "1.1.0",
)

val neotypeLibs = Seq(
  "io.github.kitlangton" %% "neotype"            % "0.2.5",
  "io.github.kitlangton" %% "neotype-zio-schema" % "0.2.5",
  "io.github.kitlangton" %% "neotype-zio-json"   % "0.2.5",
)

val sharedLibs          = zioSharedLibs ++ neotypeLibs ++ zioTestLibs
val clientAndServerLibs = sharedLibs ++ zioHttpLibs

ThisBuild / resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
ThisBuild / scalaVersion := scala3Version
ThisBuild / version      := publishVersion
ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalacOptions ++= Seq(
  "-Wunused:all",
  "-deprecation",
)

lazy val root = project
  .in(file("."))
  .settings(
    name        := "endpoints-example",
    description := "An example of using ZIO-HTTP endpoints with OpenAPI docs and a client",
  )
  .aggregate(service, endpoints, server, client)

lazy val service = project
  .in(file("service"))
  .settings(
    name := "service",
    libraryDependencies ++= sharedLibs,
  )

lazy val endpoints = project
  .in(file("endpoints"))
  .settings(
    name := "endpoints",
    libraryDependencies ++= clientAndServerLibs,
  )

lazy val server = project
  .in(file("server"))
  .settings(
    name := "server",
    libraryDependencies ++= clientAndServerLibs,
    assembly / assemblyOutputPath := file("./server.jar"),
    assembly / mainClass          := Some("server.Main"),
    assemblyMergeStrategy := {
      case s if s endsWith ".properties"           => MergeStrategy.first
      case s if s startsWith "buildinfo/BuildInfo" => MergeStrategy.first
      case x =>
        val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
        oldStrategy(x)
    },
  )
  .dependsOn(service, endpoints)

lazy val client = project
  .in(file("client"))
  .settings(
    name := "client",
    libraryDependencies ++= clientAndServerLibs,
  )
  .dependsOn(endpoints)
