import play.api.Application
import play.api.GlobalSettings
import models.Shop

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    super.onStart(app)

    /*
    if (Shop.list.isEmpty) {
      Shop.create("Play Framework Essentials", 42)
    }
    */
  }
}
