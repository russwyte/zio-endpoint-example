package endpoints
import zio.json.*
import zio.schema.*
import zio.http.codec.HttpCodec
import zio.http.Status
import zio.http.endpoint.openapi.OpenAPI.SecurityScheme.ApiKey.In

enum Error derives JsonCodec, Schema:
  case InvalidName(error: String)
object Error:
  val codec = HttpCodec
    .error[Error](Status.BadRequest)
    .examples(("invalid name" -> Error.InvalidName("You sent a whack name... try again!")))

enum Internal derives JsonCodec, Schema: // possibly 500 errors that can be returned by the service
  case InternalServerError(cause: String)
object Internal:
  val codec = HttpCodec
    .error[Internal](Status.InternalServerError)
    .examples(("it got borked" -> Internal.InternalServerError("You borked it!")))
