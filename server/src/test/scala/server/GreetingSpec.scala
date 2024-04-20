package server

import zio.*
import zio.test.*

object GreetingSpec extends ZIOSpecDefault:
  val serverZIO = ZIO.service[GreetingServer]
  val auth      = endpoints.AuthorizedRequest("change me")
  import auth.*
  val y  = endpoints.Greeting.sayHello.input.encodeRequest(Greet("World"))
  val y2 = endpoints.Greeting.sayHello.input.encodeRequest(Greet("wor1ld"))

  def spec = suite("GreetingSpec")(
    test("Greeting should return a greeting") {
      for
        greeting <- serverZIO
        r        <- greeting.core(y)
        res      <- r.body.asString
        r1       <- greeting.core(y2)
        res2     <- r1.body.asString.debug
      yield assertTrue(res.contains("World"))
    }
  ).provideShared(service.GreetingLive.layer >>> GreetingServer.layer)
end GreetingSpec
