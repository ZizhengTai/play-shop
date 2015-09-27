package models

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import play.api.Play
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfig }
import slick.driver.JdbcProfile
import tables.ItemTable

case class Item(id: Long, name: String, price: Double)

trait Shop {
  def list: Seq[Item]
  def create(name: String, price: Double): Option[Item]
  def get(id: Long): Option[Item]
  def update(id: Long, name: String, price: Double): Option[Item]
  def delete(id: Long): Boolean
}

object Shop extends Shop with ItemTable with HasDatabaseConfig[JdbcProfile] {

  import driver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  private val items = TableQuery[Items]

  def list(): Seq[Item] = Await.result(db.run(items.result), Duration.Inf)

  def create(name: String, price: Double): Option[Item] = {
    val item =
      (items returning items.map(_.id)
             into ((item, id) => item.copy(id = id))
      ) += Item(-1, name, price) // The AutoInc field will be ignored
    Some(Await.result(db.run(item), Duration.Inf))
  }

  def get(id: Long): Option[Item] = {
    Await.result(db.run(items.filter(_.id === id).result.headOption), Duration.Inf)
  }

  def update(id: Long, name: String, price: Double): Option[Item] = {
    val item = Item(id, name, price)
    if (Await.result(db.run(items.filter(_.id === id).update(item)), Duration.Inf) > 0) Some(item)
    else None
  }

  def delete(id: Long): Boolean = {
    Await.result(db.run(items.filter(_.id === id).delete), Duration.Inf) > 0
  }
}
