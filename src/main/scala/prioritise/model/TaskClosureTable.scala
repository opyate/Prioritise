



package prioritise.model

import liftweb.mapper._
import mapper._
import common._
import net.liftweb.mapper._

/**
 * http://stackoverflow.com/questions/192220/what-is-the-most-efficient-elegant-way-to-parse-a-flat-table-into-a-tree/192462#192462
 */
class TaskClosureTable extends LongKeyedMapper[TaskClosureTable] with IdPK {

  def getSingleton = TaskClosureTable

  object ancestor extends LongMappedMapper(this, Task) {
    override def dbColumnName = "ancestor_id"

//    override def validSelectValues =
//      Full(Task.findMap(OrderBy(Task.id, Ascending)) {
//        case s: Task => Full(s.id.is -> s.id.is)
//      })
  }

  object descendant extends LongMappedMapper(this, Task) {
    override def dbColumnName = "ancestor_id"

//    override def validSelectValues =
//      Full(Task.findMap(OrderBy(Task.id, Ascending)) {
//        case s: Task => Full(s.id.is -> s.id.is)
//      })
  }

}

object TaskClosureTable extends TaskClosureTable with LongKeyedMetaMapper[TaskClosureTable]