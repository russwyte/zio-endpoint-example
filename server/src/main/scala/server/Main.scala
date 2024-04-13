package server
import zio.*
object Main extends ZIOAppDefault:
  val run =
    ZIO
      .serviceWithZIO[GreetingServer](_.run)
      .provide(GreetingServer.layer, service.GreetingLive.layer)
