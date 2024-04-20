package client

import zio.*

object Main extends ZIOAppDefault:

  override val bootstrap = Runtime.enableLoomBasedExecutor

  val run =
    for
      str <- getArgs.map(_.headOption.getOrElse("Dude"))
      res <- Greeting.sayHello(str)
      _   <- Console.printLine(res)
    yield ()

end Main
