package controllers

import models.User
import models.crud.Model
import scala.collection.mutable.ArrayBuffer
import org.joda.time.DateTime

/**
 * Demo storage, for users only
 *
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
trait SuperDuperStorage[ModelType <: Model[Long]] extends crud.CRUDStorage[Long] {

  private val bob = User(1, "Bob", "Marley", "bob@marley.org", Seq("music"), None, new DateTime(1976, 2, 12, 0, 0))
  private val jimmy = User(2, "Jimmy", "Hendrix", "jimmy@hendrix.org", Seq("music"), Some("flying"), new DateTime(1965, 4, 3, 0, 0))

  private val users = new ArrayBuffer[User]()

  users += bob
  users += jimmy

  def findAll() = users.toList.map(_.asInstanceOf[Model[Long]])

  def findOne(id: Long): Option[ModelType] = users.find(_.id == id).map(_.asInstanceOf[ModelType])

  def save(resource: Model[Long]): Unit = users += resource.asInstanceOf[User].copy(id = users.size + 1)

  def update(id: Long, resource: Model[Long]): Unit = {
    val idx = users.indexWhere(_.id == id)
    users.remove(idx)
    users += resource.asInstanceOf[User].copy(id = id)
  }

  def delete(id: Long): Unit = users.filterNot(_.id == id)

}
