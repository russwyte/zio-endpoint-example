package server

import zio.*
import zio.test.*

object GreetingSpec extends ZIOSpecDefault:
  val serverZIO = ZIO.service[GreetingServer]
  val auth      = endpoints.AuthorizedRequest("change me")
  val y         = endpoints.Greeting.sayHello.input.encodeRequest(auth.Greet("World"))
  val y2        = endpoints.Greeting.sayHello.input.encodeRequest(auth.Greet("wor1ld"))

  def spec = suite("GreetingSpec")(
    test("Greeting should return a greeting") {
      for
        greeting <- serverZIO
        r        <- greeting.coreApp(y)
        res      <- r.body.asString
        r1       <- greeting.coreApp(y2)
        res2     <- r1.body.asString.debug
      yield assertTrue(res.contains("World"))
    }
  ).provideShared(service.GreetingLive.layer >>> GreetingServer.layer)
end GreetingSpec
