package endpoints

import zio.http.endpoint.*
import zio.http.endpoint.openapi.*
import zio.http.codec.PathCodec.*
import zio.http.{Method, Status}
import zio.http.Middleware
import zio.http.Header.Authorization
import zio.http.codec.HeaderCodec
import zio.http.codec.TextCodec
import zio.prelude.Invariant
import zio.http.endpoint.openapi.OpenAPI.SecurityScheme.ApiKey.In
import zio.http.codec.HttpCodec

object Greeting:
  val greet = base / "greet"

  val sayHello =
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
