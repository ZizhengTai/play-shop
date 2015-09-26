package controllers

import play.api._
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok("Just Play Scala")
    //Ok(views.html.index("Your new application is ready."))
  }

}
