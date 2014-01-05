package controllers.crud

import scalatags._
import models.crud.Model
import play.api.data.{ ObjectMapping, FieldMapping, Mapping, Form }
import scala.reflect._
import play.api.templates.Html
import play.api.Logger

/**
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
class CRUDViews {

  def main(t: String)(b: STag*) = html(
    head(
      title(t),
      body(b)
    )
  )

  def list[IdType](t: String, entities: List[Model[IdType]]) = main(t)(
    h1(t),
    div(
      table(
        thead(
          th("Title"),
          th("Actions")
        ),
        tbody(
          entities.map { e =>
            tr(
              td(e.title),
              td(
                a("Edit").href("#"),
                a("Delete").href("#")
              )
            )
          }
        )
      )
    )
  )

  def newForm[IdType, T](t: String, f: Form[T])(implicit ct: ClassTag[T]) = {

    main(t)(
      h1(t),
      renderFormFields(f).toString(),
      input.attr("type" -> "submit")
    )
  }

  def renderFormFields[T](f: Form[T])(implicit ct: ClassTag[T]): Html = {

    import views.html.helper._

    val StringClassTag = classTag[String]

    val StringFieldMapping = classOf[FieldMapping[String]]

    def mappingToHtml(mappings: Seq[Mapping[_]]): Seq[Html] = mappings.flatMap {

      case fm @ FieldMapping(key, constraints) =>

        fm.getClass match {
          case StringFieldMapping =>
            Seq(inputText(f.apply(key)))

        }

      case o =>
        mappingToHtml(o.mappings.filterNot(_.isInstanceOf[ObjectMapping]))

    }

    val renderedFields: Seq[Html] = mappingToHtml(f.mapping.mappings)

    renderedFields.foldLeft(Html.empty) { _ += _ }

  }

}
