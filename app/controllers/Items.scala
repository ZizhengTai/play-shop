package controllers

import javax.inject.Inject

import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

import play.api.Logger
import play.api.mvc.{ Controller, Action }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._

import reactivemongo.api.Cursor
import reactivemongo.bson.BSONObjectID

import play.modules.reactivemongo.{ MongoController, ReactiveMongoApi, ReactiveMongoComponents }
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

import models.{ Item, CreateItem }

class Items @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends Controller with MongoController with ReactiveMongoComponents {

  def shop: JSONCollection = db.collection[JSONCollection]("items")

  implicit val itemFormat = Json.format[Item]
  implicit val readsCreateItem: Reads[CreateItem] = (
    (__ \ "name").read(Reads.minLength[String](1)) and
    (__ \ "price").read(Reads.min[Double](0))
  )(CreateItem.apply _)

  def list(page: Int) = Action.async { implicit request =>
    val cursor = shop.find(Json.obj()).cursor[Item]()
    val itemsFuture: Future[List[Item]] = cursor.collect[List]()
    itemsFuture.map { items =>
      render {
        case Accepts.Html() => Ok(views.html.list(items))
        case Accepts.Json() => Ok(Json.toJson(items))
      }
    }
  }

  def create = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateItem] match {
      case JsSuccess(CreateItem(name, price), _) =>
        val item = Item(BSONObjectID.generate, name, price)
        val future = shop.insert(item)
        future.map(_ => Ok(Json.toJson(item)))
      case JsError(errors) =>
        Future.successful(BadRequest)
    }
  }
  
  val createForm = Action { NotImplemented }
  
  def details(id: String) = Action.async { implicit request =>
    Try(BSONObjectID(id)) match {
      case Success(id) =>
        shop.find(Json.obj("_id" -> id)).one[Item].map { item =>
          item match {
            case Some(item) =>
              render {
                case Accepts.Html() => Ok(views.html.details(item))
                case Accepts.Json() => Ok(Json.toJson(item))
              }
            case None => NotFound("Item does not exist")
          }
        }
      case Failure(error) => Future.successful(NotFound("Invalid object ID"))
    }
  }
  
  def update(id: String) = Action { NotImplemented }
  def delete(id: String) = Action { NotImplemented }
}

/*
package controllers

import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.{ mapping, nonEmptyText, of }
import play.api.data.format.Formats.doubleFormat
import play.api.i18n.Messages.Implicits._
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

  val createItemFormModel = Form(
    mapping(
      "name" -> nonEmptyText,
      "price" -> of[Double].verifying("Price must be positive", _ > 0)
    )(CreateItem.apply)(CreateItem.unapply)
  )

  def list(page: Int) = Action { implicit request =>
    val items = shop.list

    render {
      case Accepts.Html() => Ok(views.html.list(items))
      case Accepts.Json() => Ok(Json.toJson(items))
    }
  }

  val create = Action { implicit request =>
    createItemFormModel.bindFromRequest.fold(
      formWithErrors => render {
        case Accepts.Html() => BadRequest(views.html.createForm(formWithErrors))
        case Accepts.Json() => BadRequest(formWithErrors.errorsAsJson)
      },
      createItem =>
        shop.create(createItem.name, createItem.price) match {
          case Some(item) => render {
            case Accepts.Html() => Redirect(routes.Items.details(item.id))
            case Accepts.Json() => Ok(Json.toJson(item))
          }
          case None => InternalServerError
        }
    )
  }

  val createForm = Action {
    Ok(views.html.createForm(createItemFormModel))
  }

  def details(id: Long) = Action {
    shop.get(id) match {
      case Some(item) => Ok(views.html.details(item)) // Ok(Json.toJson(item))
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
*/
