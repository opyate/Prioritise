package prioritise.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb.sitemap.Loc.{LocParam, If, Template}
import net.liftweb.http.{SessionVar, S}
import xml.{NodeSeq, Text}

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
             <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
  locale, timezone, password, textArea)

  // comment this line out to require email validations
  override def skipEmailValidation = true
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] with OneToMany[Long, User] {
  def getSingleton = User // what's the "meta" server

  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }
  
  object tasks extends MappedOneToMany(Task, Task.user,
    OrderBy(Task.id, Descending))
          with Owned[Task]
          with Cascade[Task]

  /**
   * All tasks, newest first
   */
  def myTasks: List[Task] = Task.findAll(By(Task.user, this.id), OrderBy(Task.createdAt, Descending))

  /**
   * All top-level (root) tasks, newest first
   */
  def myRootTasks: List[Task] = Task.findAll(By(Task.user, this.id),
    ByRef(Task.parent_task, Task.id), OrderBy(Task.createdAt, Descending))

}
