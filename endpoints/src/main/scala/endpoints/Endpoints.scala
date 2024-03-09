package endpoints
import zio.*
import zio.http.endpoint.*
import zio.http.endpoint.openapi.*
import zio.http.codec.PathCodec.*
import zio.http.{Method, Status}

val base = "api" / "v1"
object Greeting:
  val greet = base / "greet"
  val sayHello =
    Endpoint(Method.GET / greet / "hello" / string("name"))
      .out[String](status = Status.Ok)
      .outError[String](status = Status.BadRequest)
  val openApi: OpenAPI = OpenAPIGen.fromEndpoints(
    title = "Endpoint Example",
    version = "1.0",
    Greeting.sayHello
  )
