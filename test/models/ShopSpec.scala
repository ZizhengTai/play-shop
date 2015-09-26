package models

import play.api.test.{ PlaySpecification, WithApplication }

class ShopSpec extends PlaySpecification {

  "A shop" should {
    "add items" in new WithApplication {
      /*
      Shop.create("Play Framework Essentials", 42) must
        beSome[Item].which { item =>
          item.name == "Play Framework Essentials" && item.price == 42
        }
      */
    }
  }

}
