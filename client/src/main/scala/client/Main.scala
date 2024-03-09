package client

import zio.*
import zio.http.*
import zio.http.endpoint.*

object Main extends ZIOAppDefault:
  val app = ZIO.serviceWithZIO[Greeting]: g =>
    for
      r1 <- g.sayHello("World")
      _  <- Console.printLine(r1)
      r2 <- g.sayHello("ZIO")
      _  <- Console.printLine(r2)
    yield ()

  val run =
    ZIO
      .scoped(app)
      .provide(liveLayers)

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
