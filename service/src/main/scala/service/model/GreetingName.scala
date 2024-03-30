package service.model

import neotype.Subtype

type GreetingName = GreetingName.Type
object GreetingName extends Subtype[String]:
  type Result = String | Boolean
  extension (res: Result)
    def combine(that: Result) =
      res match
        case s: String =>
          if that == true then s
          else s + "\n" + that
        case b: Boolean => that
  override inline def validate(value: String) =
    List(
      if value.isEmpty then "Name cannot be empty." else true,
      if value.length > 100 then "Name is too long." else true,
      if value.length < 3 then "Name is too short." else true,
      if value.matches(".*\\d.*") then "Name cannot contain numbers." else true,
      if value.charAt(0).isLower then "Name must start with a capital letter." else true,
    ).reduce((a, b) => a.combine(b))
  end validate
end GreetingName
