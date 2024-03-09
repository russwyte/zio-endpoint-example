package client

import zio.*
import zio.http.endpoint.EndpointExecutor

case class Greeting(ex: EndpointExecutor[Unit]):
  def sayHello(name: String) =
    ex(endpoints.Greeting.sayHello(name))

object Greeting:
  val layer = ZLayer.fromFunction { Greeting.apply }
