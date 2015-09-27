import play.api.test.{ WithBrowser, PlaySpecification }

class UISpec extends PlaySpecification {

  "A user" should {
    "add a new item to the item list" in new WithBrowser {
      // browser.goTo(controllers.routes.Items.list().url)
      // browser.$("ul").isEmpty must be True

      val formUrl = controllers.routes.Items.createForm().url
      browser.$(s"""a[href="$formUrl"]""").click()
      browser.submit("form",
        "name" -> "Play Framework Essentials",
        "price" -> "42"
      )
      browser.$("body").getText must contain ("Play Framework Essentials: 42.00€")
    }
  }

}
