package endpoints

import zio.http.Method
import zio.http.Status
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

object Greeting:
  val greet = base / "greet"

  val sayHello: Endpoint[String, AuthorizedRequest#Greet, Error | Internal, String, EndpointMiddleware.None.type] =
    Endpoint(Method.GET / greet / "hello" / string("name").example("Good", "Russ").example("Bad", "ru55"))
      .header(apiKey)
      .transformIn { case (name, apiKey) =>
        AuthorizedRequest(apiKey).Greet(name)
      } { greet =>
        (greet.name, greet.apiKey)
      }
      .out[String](status = Status.Ok)
      .examplesOut(("Good" -> "Hello, Russ!"))
      .outErrors(
        Error.codec,
        Internal.codec,
      )

end Greeting
