package server

import zio.*
import zio.http.*
import zio.http.endpoint.openapi.SwaggerUI
import zio.http.codec.PathCodec.*
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
  val coreApp =
    Routes(sayHelloRoute).toHttpApp @@ zio.http.Middleware.customAuth(_.headers.get("api-key").contains("change me"))
  val swaggerApp =
    SwaggerUI
      .routes(
        "docs" / "openapi",
        endpoints.Greeting.openApi,
      )
      .toHttpApp
  val app = coreApp ++ swaggerApp
  val run = Server.serve(app).provide(Server.default)
end GreetingServer

object GreetingServer:
  val layer = ZLayer.fromFunction(GreetingServer.apply)
