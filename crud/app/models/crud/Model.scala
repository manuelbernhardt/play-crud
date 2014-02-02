package models.crud

/**
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
trait Model[IdType] {

  def id: IdType

  override def toString(): String = s"${getClass.getSimpleName}[$id]"

}
