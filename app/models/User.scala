package models

import play.api.data._
import play.api.data.Forms._

case class User(
 firstName: String,
 lastName: String,
 age: Int
)

object User {

  val createUserForm = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "age" -> number(min = 0, max = 120)
    )(User.apply)(User.unapply)
  )

}

object JsonFormats {
  import play.api.libs.json.Json

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val userFormat = Json.format[User]
}