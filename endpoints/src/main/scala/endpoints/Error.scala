package endpoints
import zio.http.Status
import zio.http.Status.BadRequest
import zio.http.codec.HttpCodec
import zio.schema.*

enum Error derives Schema:
  case InvalidName(message: String)
object Error:
  val codec = HttpCodec
    .error[Error](Status.BadRequest)
    .examples(("invalid name" -> Error.InvalidName("You sent a whack name... try again!")))

enum Internal derives Schema: // possibly 500 errors that can be returned by the service
  case InternalServerError(message: String)
object Internal:
  val codec = HttpCodec
    .error[Internal](Status.InternalServerError)
    .examples(("it got borked" -> Internal.InternalServerError("You borked it!")))
