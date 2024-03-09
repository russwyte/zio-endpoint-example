package server
import zio.*
import zio.http.*
import zio.http.endpoint.openapi.SwaggerUI
import zio.http.codec.PathCodec.*
object Main extends ZIOAppDefault:
  val run =
    ZIO
      .serviceWithZIO[GreetingServer](_.run)
      .provide(GreetingServer.layer, service.GreetingLive.layer)
