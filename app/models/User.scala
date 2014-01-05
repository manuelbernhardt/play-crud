package models

import models.crud.Model

/**
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
case class User(id: Long, firstName: String, lastName: String, email: String) extends Model[Long]
