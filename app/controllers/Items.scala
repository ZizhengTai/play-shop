package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.{ Json, JsSuccess, JsError, Reads, __ }
import play.api.mvc.{ Controller, Action }
import models.{ Item, CreateItem }

class Items extends Controller {

  val shop = models.Shop // Refer to your Shop implementation

  implicit val writesItem = Json.writes[Item]
  implicit val readsCreateItem: Reads[CreateItem] = (
    (__ \ "name").read(Reads.minLength[String](1)) and
    (__ \ "price").read(Reads.min[Double](0))
  )(CreateItem.apply _)

  def list(page: Int) = Action {
    Ok(views.html.list.render(shop.list))
  }

  val create = Action(parse.json) { implicit request =>
    request.body.validate[CreateItem] match {
      case JsSuccess(CreateItem(name, price), _) =>
        shop.create(name, price) match {
          case Some(item) => Ok(Json.toJson(item))
          case None => InternalServerError
        }
      case JsError(errors) => BadRequest
    }
  }

  def details(id: Long) = Action {
    shop.get(id) match {
      case Some(item) => Ok(views.html.details.render(item)) // Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  def update(id: Long) = Action(parse.json) { implicit request =>
    request.body.validate[CreateItem] match {
      case JsSuccess(CreateItem(name, price), _) =>
        shop.update(id, name, price) match {
          case Some(item) => Ok(Json.toJson(item))
          case None => NotFound 
        }
      case JsError(errors) => BadRequest
    }
  }

  def delete(id: Long) = Action {
    if (shop.delete(id)) Ok
    else NotFound
  }

}
