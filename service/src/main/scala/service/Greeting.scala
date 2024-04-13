package service
import zio.*

trait Greeting:
  def greet(name: model.GreetingName): IO[GreetingError, String]
