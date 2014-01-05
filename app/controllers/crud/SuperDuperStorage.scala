package controllers.crud

import models.User
import models.crud.Model

/**
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
trait SuperDuperStorage[ModelType <: Model[Long]] extends CRUDStorage[Long] {

  private val bob = User(1, "Bob", "Marley", "bob@marley.org")
  private val jimmy = User(2, "Jimmy", "Hendrix", "jimmy@hendrix.org")

  def findAll() = List(bob, jimmy)

  def findOne(id: Long): Option[User] = if (id == 1) Some(bob) else if (id == 2) Some(jimmy) else None

  def save(resource: Model[Long]): Unit = {}

  def update(id: Long, resource: Model[Long]): Unit = {}

  def delete(id: Long): Unit = {}

}
