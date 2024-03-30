package endpoints

case class AuthorizedRequest(apiKey: String):
  self =>
  sealed trait Keyed:
    def apiKey: String = self.apiKey
  case class Greet(name: String) extends Keyed
