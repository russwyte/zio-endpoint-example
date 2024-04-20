package server

import zio.*
import zio.http.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.openapi.SwaggerUI
import zio.json.*

case class GreetingServer(greeter: service.Greeting):
  val sayHelloRoute = endpoints.Greeting.sayHello
    .implement(Handler.fromFunctionZIO[endpoints.AuthorizedRequest#Greet] { in =>
      val res = for
        _ <- Console.printLine(s"Received request to $in").orDie
        name <- ZIO
          .fromEither(service.model.GreetingName.make(in.name))
          .mapError(error => service.GreetingError.InvalidName(error))
        g <- greeter.greet(name)
      yield g
      res.mapError(e => endpoints.Error.InvalidName(e.toJsonPretty))
    })
  val core =
    Routes(sayHelloRoute)
  val swagger =
    SwaggerUI
      .routes(
        "docs" / "openapi",
        endpoints.openApi,
      )
  val all = core @@ zio.http.Middleware.customAuth(_.headers.get("api-key").contains("change me")) ++ swagger
  val run = Server.serve(all.toHttpApp).provide(Server.default)
end GreetingServer

object GreetingServer:
  val layer = ZLayer.fromFunction(GreetingServer.apply)
  val live  = service.GreetingLive.layer >>> layer
  val run   = ZIO.serviceWithZIO[GreetingServer](_.run).provide(live)
