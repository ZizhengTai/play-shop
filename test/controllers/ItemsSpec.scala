package controllers

import play.api.test.{ PlaySpecification, FakeRequest, WithApplication }
import play.api.libs.json.Json

class ItemsSpec extends PlaySpecification {

  "Items controller" should {
    "list items" in new WithApplication {
      route(FakeRequest(controllers.routes.Items.list())) match {
        case Some(response) =>
          status(response) must equalTo (OK)
          //contentAsJson(response) must equalTo (Json.arr())
        case None => failure
      }
    }
  }

}
