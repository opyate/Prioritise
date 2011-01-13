



package prioritise.model

import net.liftweb._
import mapper._
import util._
import common._
import sitemap.Loc._

class Task extends LongKeyedMapper[Task] with CreatedUpdated with IdPK {

  def getSingleton = Task

  object title extends MappedString(this, 32)

  object description extends MappedTextarea(this, 8192)

  object dueDateTime extends MappedDateTime(this)

  object startDateTime extends MappedDateTime(this)

  object user extends LongMappedMapper(this, User) {
    override def dbColumnName = "user_id"

    override def validSelectValues =
      Full(User.findMap(OrderBy(User.email, Ascending)) {
        case s: User => Full(s.id.is -> s.email.is)
      })
  }

  object rank extends MappedInt(this)
}
object Task extends Task with LongKeyedMetaMapper[Task] with CRUDify[Long, Task] {

  override def calcPrefix = List(_dbTableNameLC)

  override def displayName = "Task"

  override def showAllMenuLocParams = LocGroup("public") :: Nil

  override def createMenuLocParams = LocGroup("public") :: Nil

  override def viewMenuLocParams = LocGroup("public") :: Nil

  override def editMenuLocParams = LocGroup("public") :: Nil

  override def deleteMenuLocParams = LocGroup("public") :: Nil
}