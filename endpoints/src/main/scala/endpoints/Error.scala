package endpoints
import zio.json.*
import zio.schema.*

enum Error derives JsonCodec, Schema:
  case InvalidName(error: String)
  case InternalError
