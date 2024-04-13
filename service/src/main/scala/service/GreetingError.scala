package service

import zio.json.*
import zio.schema.*

enum GreetingError derives JsonCodec, Schema:
  case InvalidName(error: String)
  case InternalError
