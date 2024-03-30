package service
import zio.*
import zio.schema.*
import zio.json.*

trait Greeting:
  def greet(name: model.GreetingName): IO[GreetingError, String]
