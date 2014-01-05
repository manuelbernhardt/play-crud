package controllers.crud

import models.crud.Model

/**
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
trait CRUDStorage[IdType] {

  def findAll(): List[Model[IdType]]

  def findOne(id: IdType): Option[Model[IdType]]

  def save(resource: Model[IdType])

  def update(id: IdType, resource: Model[IdType])

  def delete(id: IdType)
}
