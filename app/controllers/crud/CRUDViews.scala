package controllers.crud

import scalatags._
import models.crud.Model
import play.api.data._
import scala.reflect._
import play.api.templates.Html
import play.api.Logger
import org.joda.time.{ LocalDateTime, DateTime }
import play.api.data.format.Formatter
import play.api.mvc.Flash

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

  def list[IdType](t: String, entities: List[Model[IdType]], flash: Flash, uri: String) = main(t)(
    h1(t),
    flash.get("success").map { s => div(s) } getOrElse div(),
    div(
      table(
        thead(
          th("Title"),
          th("Actions")
        ),
        tbody(
          entities.map { e =>
            tr(
              td(e.toString()),
              td(
                a("Edit").href(uri + "/" + e.id + "/edit"),
                a("Delete").href("#")
              )
            )
          }
        )
      )
    ),
    div(
      a("New").href(uri + "/new")
    )
  )

  def renderForm[IdType, T](t: String, f: Form[T], submitUrl: String, method: String = "POST")(implicit ct: ClassTag[T]) = {

    main(t)(
      h1(t),
      form(
        renderFormFields(f).toString(),
        input.attr("type" -> "submit")
      ).attr("method" -> method, "action" -> submitUrl)
    )
  }

  def renderFormFields[T](f: Form[T])(implicit ct: ClassTag[T]): Html = {

    import views.html.helper._

    val StringFM = classOf[FieldMapping[String]]
    val LongFM = classOf[FieldMapping[Long]]
    val DoubleFM = classOf[FieldMapping[Double]]
    val IntFM = classOf[FieldMapping[Int]]
    val DateFM = classOf[FieldMapping[DateTime]]
    val LocalDateFM = classOf[FieldMapping[LocalDateTime]]

    def mappingToHtml(mappings: Seq[Mapping[_]]): Seq[Html] = mappings.flatMap {

      case fm @ FieldMapping(key, constraints) =>

        Logger.debug(s"Handling FieldMapping with key $key and type ${fm.getClass}")

        val ignored = fm.format.exists(_._1 == "ignored")

        if (ignored) {
          Seq.empty
        } else {
          fm.getClass match {
            case StringFM =>
              Seq(inputText(f.apply(key)))
            case LongFM | DoubleFM | IntFM =>
              Seq(inputText(f.apply(key), 'type -> "number"))
            case DateFM | LocalDateFM =>
              Seq(inputDate(f.apply(key)))
            case _ =>
              println("what?")
              Seq.empty
          }
        }

      case RepeatedMapping(wrapped, key, constraints) =>
        // TODO configurable repetition
        Logger.debug("repeated " + key)
        val howMany = {
          val n = f.data.count(_._1.startsWith(key))
          if (n == 0) 3 else n + 1
        }
        Seq(Html(key)) ++ (0 to howMany).flatMap(i => mappingToHtml(Seq(wrapped.withPrefix(key + s"[$i]"))))

      case WrappedMapping(wrapped, f1, f2, additionalConstraints) =>
        Logger.debug("wrapped")
        mappingToHtml(Seq(wrapped))

      case o =>
        Logger.debug("other type?" + o.toString)
        val innerMappings = o.mappings
        Logger.debug("inner mapppings -> " + innerMappings.toString)
        mappingToHtml(innerMappings)

    }

    /**
     * object YayGenerator {
     *
     *   def generate(arity: Int) = {
     *     val fs = (1 to arity).map { n => s"f$n" }.mkString(", ")
     *     val fields = (1 to arity).map { n => s"om$arity.field$n" }.mkString(", ")
     *     s"""case om$arity @ ObjectMapping$arity(apply, unapply, $fs , key, constraints) => Seq($fields)"""
     *   }
     *
     *   def yay() {
     *     (1 to 18).map(generate).foreach(println)
     *   }
     *
     * }
     */
    val fields = {
      f.mapping.mappings.find(_.isInstanceOf[ObjectMapping]).map {
        case om1 @ ObjectMapping1(apply, unapply, f1, key, constraints) => Seq(om1.field1)
        case om2 @ ObjectMapping2(apply, unapply, f1, f2, key, constraints) => Seq(om2.field1, om2.field2)
        case om3 @ ObjectMapping3(apply, unapply, f1, f2, f3, key, constraints) => Seq(om3.field1, om3.field2, om3.field3)
        case om4 @ ObjectMapping4(apply, unapply, f1, f2, f3, f4, key, constraints) => Seq(om4.field1, om4.field2, om4.field3, om4.field4)
        case om5 @ ObjectMapping5(apply, unapply, f1, f2, f3, f4, f5, key, constraints) => Seq(om5.field1, om5.field2, om5.field3, om5.field4, om5.field5)
        case om6 @ ObjectMapping6(apply, unapply, f1, f2, f3, f4, f5, f6, key, constraints) => Seq(om6.field1, om6.field2, om6.field3, om6.field4, om6.field5, om6.field6)
        case om7 @ ObjectMapping7(apply, unapply, f1, f2, f3, f4, f5, f6, f7, key, constraints) => Seq(om7.field1, om7.field2, om7.field3, om7.field4, om7.field5, om7.field6, om7.field7)
        case om8 @ ObjectMapping8(apply, unapply, f1, f2, f3, f4, f5, f6, f7, f8, key, constraints) => Seq(om8.field1, om8.field2, om8.field3, om8.field4, om8.field5, om8.field6, om8.field7, om8.field8)
        case om9 @ ObjectMapping9(apply, unapply, f1, f2, f3, f4, f5, f6, f7, f8, f9, key, constraints) => Seq(om9.field1, om9.field2, om9.field3, om9.field4, om9.field5, om9.field6, om9.field7, om9.field8, om9.field9)
        case om10 @ ObjectMapping10(apply, unapply, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, key, constraints) => Seq(om10.field1, om10.field2, om10.field3, om10.field4, om10.field5, om10.field6, om10.field7, om10.field8, om10.field9, om10.field10)
        case om11 @ ObjectMapping11(apply, unapply, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, key, constraints) => Seq(om11.field1, om11.field2, om11.field3, om11.field4, om11.field5, om11.field6, om11.field7, om11.field8, om11.field9, om11.field10, om11.field11)
        case om12 @ ObjectMapping12(apply, unapply, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, key, constraints) => Seq(om12.field1, om12.field2, om12.field3, om12.field4, om12.field5, om12.field6, om12.field7, om12.field8, om12.field9, om12.field10, om12.field11, om12.field12)
        case om13 @ ObjectMapping13(apply, unapply, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, key, constraints) => Seq(om13.field1, om13.field2, om13.field3, om13.field4, om13.field5, om13.field6, om13.field7, om13.field8, om13.field9, om13.field10, om13.field11, om13.field12, om13.field13)
        case om14 @ ObjectMapping14(apply, unapply, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, key, constraints) => Seq(om14.field1, om14.field2, om14.field3, om14.field4, om14.field5, om14.field6, om14.field7, om14.field8, om14.field9, om14.field10, om14.field11, om14.field12, om14.field13, om14.field14)
        case om15 @ ObjectMapping15(apply, unapply, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, key, constraints) => Seq(om15.field1, om15.field2, om15.field3, om15.field4, om15.field5, om15.field6, om15.field7, om15.field8, om15.field9, om15.field10, om15.field11, om15.field12, om15.field13, om15.field14, om15.field15)
        case om16 @ ObjectMapping16(apply, unapply, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, key, constraints) => Seq(om16.field1, om16.field2, om16.field3, om16.field4, om16.field5, om16.field6, om16.field7, om16.field8, om16.field9, om16.field10, om16.field11, om16.field12, om16.field13, om16.field14, om16.field15, om16.field16)
        case om17 @ ObjectMapping17(apply, unapply, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, key, constraints) => Seq(om17.field1, om17.field2, om17.field3, om17.field4, om17.field5, om17.field6, om17.field7, om17.field8, om17.field9, om17.field10, om17.field11, om17.field12, om17.field13, om17.field14, om17.field15, om17.field16, om17.field17)
        case om18 @ ObjectMapping18(apply, unapply, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, key, constraints) => Seq(om18.field1, om18.field2, om18.field3, om18.field4, om18.field5, om18.field6, om18.field7, om18.field8, om18.field9, om18.field10, om18.field11, om18.field12, om18.field13, om18.field14, om18.field15, om18.field16, om18.field17, om18.field18)
      } getOrElse {
        Seq.empty
      }
    }

    val toRender = f.mapping.mappings.filterNot(_.isInstanceOf[ObjectMapping])
    val renderedFields: Seq[Html] = mappingToHtml(fields)

    renderedFields.foldLeft(Html.empty) { _ += _ }

  }

}