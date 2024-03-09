package service
import zio.*

trait Greeting:
  def greet(name: model.GreetingName): IO[GreetingError, String]

trait ServiceError

enum GreetingError extends ServiceError:
  case InvalidName
  case InternalError
