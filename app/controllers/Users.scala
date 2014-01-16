package controllers

import controllers.crud.{ SuperDuperStorage, CRUDController }
import models.User
import play.api.data._
import play.api.data.Forms._
import org.joda.time.DateTime
import play.api.data.format.Formats._

/**
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
object Users extends CRUDController[User, Long] with SuperDuperStorage[User] {

  val form = Form(
    mapping(
      "id" -> ignored[Long](-1),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> nonEmptyText,
      "skills" -> seq(text),
      "superPower" -> optional(text),
      "birthDate" -> of[DateTime]
    )(User.apply)(User.unapply _)
  )

}