package controllers.crud

import play.api.mvc.EssentialAction

/**
 * @author Manuel Bernhardt <manuel@bernhardt.io>
 */
trait CRUDControllerDefinition[IdType] {

  def index: EssentialAction
  def newScreen: EssentialAction
  def create: EssentialAction
  def show(id: IdType): EssentialAction
  def edit(id: IdType): EssentialAction
  def update(id: IdType): EssentialAction
  def destroy(id: IdType): EssentialAction

}
