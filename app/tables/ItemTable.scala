/*package tables

import slick.driver.JdbcProfile
import models.Item

trait ItemTable {
  protected val driver: JdbcProfile
  import driver.api._

  class Items(tag: Tag) extends Table[Item](tag, "items") {
    val id = column[Long]("id", O.AutoInc)
    val name = column[String]("name")
    val price = column[Double]("price")
    def * = (id, name, price) <> (Item.tupled, Item.unapply _)
  }
}*/
