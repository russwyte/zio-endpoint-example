package client

import zio.*
import zio.http.*
import zio.http.endpoint.*

object Main extends ZIOAppDefault:

  val run =
    val args = getArgs
    val zio = for
      str      <- args.map(_.headOption.getOrElse("dude"))
      greeting <- ZIO.service[Greeting]
      res      <- greeting.sayHello(str)
      _        <- Console.printLine(res)
    yield ()
    ZIO.scoped(zio).provideSome[ZIOAppArgs](liveLayers)
  end run

  val urlLayer = ZLayer { ZIO.fromEither(URL.decode("http://localhost:8080")) }
  val locatorLayer = ZLayer {
    ZIO.serviceWithZIO[URL] { url =>
      ZIO.succeed(EndpointLocator.fromURL(url))
    }
  }
  val executorLayer = ZLayer.fromFunction { (client: Client, locator: EndpointLocator) =>
    EndpointExecutor(client, locator, ZIO.unit)
  }
  val liveLayers =
    urlLayer >>> locatorLayer >+> Client.default >>> executorLayer >>> Greeting.layer
end Main
