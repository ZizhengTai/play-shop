package models

import reactivemongo.bson.BSONObjectID

case class Item(_id: BSONObjectID, name: String, price: Double)

trait Shop {
  def list: Seq[Item]
  def create(name: String, price: Double): Option[Item]
  def get(id: String): Option[Item]
  def update(id: String, name: String, price: Double): Option[Item]
  def delete(id: String): Boolean
}
