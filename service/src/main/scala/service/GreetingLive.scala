package service

import zio.*

object GreetingLive extends Greeting:
  override def greet(name: model.GreetingName): IO[GreetingError, String] =
    ZIO.succeed(s"Hello, ${name}!")
  val layer = ZLayer.succeed(GreetingLive)
