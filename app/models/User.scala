package models

import models.crud.Model
import org.joda.time.DateTime

/**
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
case class User(
    id: Long,
    firstName: String,
    lastName: String,
    email: String,
    skills: Seq[String],
    superPower: Option[String],
    birthDate: DateTime) extends Model[Long] {

  override def title: String = s"$firstName $lastName"

}
