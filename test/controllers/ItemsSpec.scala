package controllers

import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.test.{ PlaySpecification, FakeRequest, WithApplication }

class ItemsSpec extends PlaySpecification {

  "Items controller" should {
    "list items" in new WithApplication {
      val contentTypes = Seq(MimeTypes.HTML, MimeTypes.JSON)
      contentTypes foreach { contentType =>
        route(FakeRequest(controllers.routes.Items.list()).withHeaders(ACCEPT -> contentType)) match {
          case Some(response) => status(response) must equalTo (OK)
          case None => failure
        }
      }
    }
  }

}
