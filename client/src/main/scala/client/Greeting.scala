package client

import zio.*
import zio.http.*
import zio.http.endpoint.*

case class Greeting(ex: EndpointExecutor[Unit]):
  def sayHello(name: String) =
    ex(endpoints.Greeting.sayHello(endpoints.AuthorizedRequest("change me").Greet(name)))

object Greeting:
  val layer            = ZLayer.fromFunction { Greeting.apply }
  private val urlLayer = ZLayer { ZIO.fromEither(URL.decode("http://localhost:8080")) }
  private val locatorLayer = ZLayer {
    ZIO.serviceWithZIO[URL] { url =>
      ZIO.succeed(EndpointLocator.fromURL(url))
    }
  }
  private val executorLayer = ZLayer.fromFunction { (client: Client, locator: EndpointLocator) =>
    EndpointExecutor(client, locator, ZIO.unit)
  }
  val live =
    urlLayer >>> locatorLayer >+> Client.default >>> executorLayer >>> layer

  def sayHello(name: String) = ZIO.serviceWithZIO[Greeting](_.sayHello(name)).provideSomeLayer(live)
end Greeting
