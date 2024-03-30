package service.model

type Result = String | Boolean
extension (res: Result)
  def combine(that: Result) =
    res match
      case s: String =>
        if that == true then s
        else s + "\n" + that
      case b: Boolean => that
