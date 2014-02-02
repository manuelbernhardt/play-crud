package crud

import scala.xml.Elem

/**
 * Renders a field of a given type
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
trait Renderer[T] {

  def render(key: String, data: Option[Map[String, String]]): Elem

}
