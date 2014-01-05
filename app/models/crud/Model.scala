package models.crud

/**
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
trait Model[IdType] {

  def id: IdType

  def title: String = s"${getClass.getSimpleName}[$id]"

}
