package prioritise.model

import net.liftweb._
import mapper._
import common._
import sitemap.Loc._
import xml.NodeSeq

/**
 * See http://stackoverflow.com/questions/192220/what-is-the-most-efficient-elegant-way-to-parse-a-flat-table-into-a-tree/192462#192462
 * for ancestor/descendant discussion.
 */
class Task extends LongKeyedMapper[Task] with CreatedUpdated with IdPK with OneToMany[Long, Task] {

  def getSingleton = Task

  override lazy val createdAt: MappedDateTime[MapperType] = new MyCreatedAt(this) {
    override def dbDisplay_? = false
  }
  override lazy val updatedAt: MyUpdatedAt = new MyUpdatedAt(this) {
    override def dbDisplay_? = false
  }

  object title extends MappedString(this, 32)

  object description extends MappedTextarea(this, 8192)

//  object dueDateTime extends MappedDateTime(this)
//
//  object startDateTime extends MappedDateTime(this)

  object user extends LongMappedMapper(this, User) {
    override def dbColumnName = "user_id"

    override def defaultValue = User.currentUser.map(_.id.is) openOr 0L

    override def validSelectValues =
      Full(User.findMap(OrderBy(User.email, Ascending)) {
        case s: User => Full(s.id.is -> s.email.is)
      })

    // TODO for now, the app is single user, but later on allow
    // higher ROLEd persons to create tasks for the inferior masses.
    override def dbDisplay_? = false
  }

  object priority_ancestor extends LongMappedMapper(this, Task) {
    override def dbColumnName = "priority_ancestor_id"
    override def dbDisplay_? = false
  }

  object priority_descendant extends LongMappedMapper(this, Task) {
    override def dbColumnName = "priority_descendant_id"
    override def dbDisplay_? = false
  }

  // TODO guard against circular dependencies, e.g. t1 -> t2 -> t3 -> t1
  object parent_task extends LongMappedMapper(this, Task) {
    override def dbColumnName = "parent_task_id"

    override def validSelectValues =
      Full((this.fieldOwner.id.is, "None") :: Task.findMap(OrderBy(Task.updatedAt, Descending)) {
        case s: Task => Full(s.id.is -> s.title.is)
      })
  }

  object dependencies extends MappedOneToMany(Task, Task.parent_task,
    OrderBy(Task.id, Descending))
          with Owned[Task]
          with Cascade[Task]

  lazy val hasParent = {
    this.parent_task.is != this.id.is
  }
}

object Task extends Task with LongKeyedMetaMapper[Task] with CRUDify[Long, Task] {

  override lazy val fieldOrder = List(title, description, parent_task)

  override def calcPrefix = List(_dbTableNameLC)

  override def displayName = "Task"

  override def showAllMenuLocParams = LocGroup("public") :: Nil

  override def createMenuLocParams = LocGroup("public") :: Nil

  override def viewMenuLocParams = LocGroup("public") :: Nil

  override def editMenuLocParams = LocGroup("public") :: Nil

  override def deleteMenuLocParams = LocGroup("public") :: Nil

  override def afterSave = List((task: Task) => {
      if (!task.parent_task.defined_?) {
        task.parent_task(task.id).save
      }
    })
}