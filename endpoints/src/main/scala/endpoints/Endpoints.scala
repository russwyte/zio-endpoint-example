package endpoints
//import zio.*
import zio.http.endpoint.*
import zio.http.endpoint.openapi.*
import zio.http.codec.PathCodec.*
import zio.http.{Method, Status}
import zio.http.Middleware
import zio.http.Header.Authorization
import zio.http.codec.HeaderCodec
import zio.http.codec.TextCodec
import endpoints.Greeting.apiKey

case class AuthorizedRequest(apiKey: String):
  self =>
  sealed trait Keyed:
    def apiKey: String = self.apiKey
  case class Greet(name: String) extends Keyed

val base = "api" / "v1"
object Greeting:
  val greet  = base / "greet"
  val apiKey = HeaderCodec.name[String]("api-key")
  case class GreetingInput(name: String, apiKey: String)

  val sayHello =
    Endpoint(Method.GET / greet / "hello" / string("name"))
      .out[String](status = Status.Ok)
      .header(apiKey)
      .outError[Error](status = Status.BadRequest)
      .transformIn { case (name, apiKey) =>
        AuthorizedRequest(apiKey).Greet(name)
      } { greet =>
        (greet.name, greet.apiKey)
      }
  val openApi: OpenAPI = OpenAPIGen.fromEndpoints(
    title = "Endpoint Example",
    version = "1.0",
    Greeting.sayHello,
  )
end Greeting
