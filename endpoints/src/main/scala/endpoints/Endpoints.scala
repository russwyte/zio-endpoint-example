package endpoints

import zio.http.codec.HeaderCodec
import zio.http.codec.PathCodec.*
import zio.http.endpoint.openapi.*

val base = "api" / "v1"

val apiKey = HeaderCodec.name[String]("api-key").examples(Map("Good" -> "change me", "Bad" -> "Bad"))

val openApi: OpenAPI = OpenAPIGen.fromEndpoints(
  title = "Endpoint Example",
  version = "1.0",
  Greeting.sayHello,
)
