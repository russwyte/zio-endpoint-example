package server

import zio.*
import zio.http.*
import zio.http.endpoint.openapi.SwaggerUI
import zio.http.codec.PathCodec.*

case class GreetingServer(greeter: service.Greeting):
  val sayHelloRoute = endpoints.Greeting.sayHello
    .implement(Handler.fromFunctionZIO[String] { case (name) =>
      val res = for
        _ <- Console.printLine(s"Received request to greet $name").orDie
        name <- ZIO
          .fromEither(service.model.GreetingName.make(name))
          .mapError(_ => service.GreetingError.InvalidName)
          .debug
        g <- greeter.greet(name)
      yield g
      res.mapError(_.toString())
    })
  val routes: Routes[Any, Response] =
    Routes(sayHelloRoute) ++ SwaggerUI.routes(
      "docs" / "openapi",
      endpoints.Greeting.openApi
    )
  val app = routes.toHttpApp
  val run = Server.serve(app).provide(Server.default)

object GreetingServer:
  val layer = ZLayer.fromFunction(GreetingServer.apply)
