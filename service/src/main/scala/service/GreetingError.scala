package service

import zio.schema.*
import zio.json.*

enum GreetingError derives JsonCodec, Schema:
  case InvalidName(error: String)
  case InternalError
