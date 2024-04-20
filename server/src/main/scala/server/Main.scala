package server
import zio.*
object Main extends ZIOAppDefault:
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = Runtime.enableLoomBasedExecutor
  val run                                              = GreetingServer.run
