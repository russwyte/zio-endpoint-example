package service.model

import neotype.Subtype

type GreetingName = GreetingName.Type

object GreetingName extends Subtype[String]:
  override inline def validate(value: String) =
    if value.isEmpty then "Name cannot be empty!" else true
