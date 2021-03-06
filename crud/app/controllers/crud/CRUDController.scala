package controllers.crud

import play.api.mvc._
import play.core.Router
import scala.runtime.AbstractPartialFunction
import scala.reflect.ClassTag
import play.api.data._
import play.api.Logger
import play.api.data.format.Formatter
import models.crud.Model

/**
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
abstract class CRUDController[EntityType <: Model[IdType], IdType](implicit idBindable: PathBindable[IdType], entityTag: ClassTag[EntityType])
    extends Controller with Router.Routes with CRUDControllerDefinition[IdType] { self: CRUDStorage[IdType] =>

  // ~~~ routing
  //
  // Any lines of code appearing in this work are fictitious.
  // Any resemblance to real lines of code, compiling or not, such as the ones described at [1], is purely coincidental.
  //
  // [1] http://jazzy.id.au/default/2013/05/08/advanced_routing_in_play_framework.html

  private var path: String = ""

  private val MaybeSlash = "/?".r
  private val NewScreen = "/new/?".r
  private val Id = "/([^/]+)/?".r
  private val Edit = "/([^/]+)/edit/?".r

  def withId(id: String, action: IdType => EssentialAction) =
    idBindable.bind("id", id).fold(badRequest, action)

  def setPrefix(prefix: String) {
    path = prefix
  }

  def prefix = path
  def documentation = Nil

  def routes = new AbstractPartialFunction[RequestHeader, Handler] {
    override def applyOrElse[A <: RequestHeader, B >: Handler](rh: A, default: A => B) = {
      if (rh.path.startsWith(path)) {
        (rh.method, rh.path.drop(path.length)) match {
          case ("GET", MaybeSlash()) => index
          case ("GET", NewScreen()) => newScreen
          case ("POST", MaybeSlash()) => create
          case ("GET", Id(id)) => withId(id, show)
          case ("GET", Edit(id)) => withId(id, edit)
          case ("POST", Id(id)) => withId(id, update)
          case ("DELETE", Id(id)) => withId(id, destroy)
          case _ => default(rh)
        }
      } else {
        default(rh)
      }
    }

    def isDefinedAt(rh: RequestHeader) = {
      if (rh.path.startsWith(path)) {
        (rh.method, rh.path.drop(path.length)) match {
          case ("GET", MaybeSlash() | NewScreen() | Id(_) | Edit(_)) => true
          case ("POST", Id(_)) => true
          case ("POST", MaybeSlash()) => true
          case ("DELETE", Id(_)) => true
          case _ => false
        }
      } else {
        false
      }
    }
  }

  // ~~~ form definition

  /**
   * Form definition. Needs to be provided by the user for the time being.
   */
  val form: Form[EntityType]

  implicit def ignored[A](value: A): Mapping[A] = play.api.data.Forms.of(new Formatter[A] {
    def bind(key: String, data: Map[String, String]) = Right(value)
    def unbind(key: String, value: A) = Map.empty
    override val format = Some("ignored" -> Seq.empty)
  })

  // ~~~ view related things

  protected val views = new CRUDViews

  val entityName = entityTag.runtimeClass.getSimpleName

  val entityNamePlural = entityTag.runtimeClass.getSimpleName + "s"

  // ~~~ default controller actions implementation

  def index: EssentialAction = Action { request =>
    Ok(views.list("List of " + entityNamePlural, findAll(), request.uri)(request.flash))
  }

  def newScreen: EssentialAction = Action { implicit request =>
    val actionUri = request.uri.substring(0, request.uri.length - "/new".length)
    Ok(views.renderForm("Create new " + entityName, form, actionUri))
  }

  def create: EssentialAction = Action { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.debug("Oops " + formWithErrors.errors.toString)
        BadRequest(views.renderForm("Create new", formWithErrors, request.uri))
      },
      newEntity => {
        Logger.debug("Got new entity to persist: " + newEntity)
        save(newEntity)
        Redirect(request.uri).flashing("success" -> ("Dude, you just submitted " + newEntity.toString()))
      }
    )
  }

  def show(id: IdType): EssentialAction = ???

  def edit(id: IdType): EssentialAction = Action { implicit request =>
    findOne(id).map { value =>
      val actionUri = request.uri.substring(0, request.uri.length - "/edit".length)
      val filledForm = form.fill(value.asInstanceOf[EntityType])
      Ok(views.renderForm("Update " + entityName, filledForm, actionUri))
    } getOrElse {
      NotFound(s"Could not find $entityName with ID $id")
    }
  }

  def update(id: IdType): EssentialAction = Action { implicit request =>
    val baseUri = request.uri.substring(0, request.uri.length - s"/$id".length)
    form.bindFromRequest.fold(
      formWithErrors =>
        BadRequest(views.renderForm("Update", formWithErrors, request.uri)),
      updatedEntity => {
        Logger.debug(request.body.toString)
        Logger.debug(updatedEntity.toString())
        findOne(id).map { value =>
          update(id, updatedEntity)
          Redirect(baseUri).flashing("success" -> ("Dude, you just updated " + updatedEntity.toString()))
        } getOrElse {
          NotFound(s"Could not find $entityName with ID $id")
        }
      }

    )
  }

  def destroy(id: IdType): EssentialAction = ???
}
