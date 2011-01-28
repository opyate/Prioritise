package prioritise.snippet

import xml.NodeSeq
import prioritise.model.{Task, User}
import net.liftweb.http.{TemplateFinder, SHtml}
import net.liftweb.util.BindHelpers._

class TaskManager {
//  def render(xhtml: NodeSeq): NodeSeq = {
//    User.currentUser.map(_.myTasks.flatMap((task: Task) => {
//      TemplateFinder.findAnyTemplate(List("templates")).map(xhtml =>
//        bind("task", xhtml,
//          "description" -> task.description.asHtml)
//      ) openOr NodeSeq.Empty
//    })).openOr(<span><a href="/user_mgt/login">Sign in</a> to see your tasks</span>)
//  }

  private lazy val tasks: List[Task] =
    User.currentUser.map(_.myTasks).openOr(Nil)

  /**
   * This will be called if chooser chooses "full"
   */
  def items(xhtml: NodeSeq): NodeSeq = tasks.flatMap(t =>
    bind("a", task(t, xhtml))
  )

  def chooser(xhtml: NodeSeq): NodeSeq = {
    val template = if (tasks.isEmpty) "empty" else "full"
    bind("b", chooseTemplate("tasks", template, xhtml)
    )
  }

  def add_button(xhtml: NodeSeq): NodeSeq = {
    bind("c", bind("d", xhtml, "add_task" -> <a href="/task_t/create">Add a new task</a>))
  }

  private def task(t: Task, xhtml: NodeSeq): NodeSeq = {
    bind("t", xhtml,
      "title" -> t.title.is,
      "description" -> t.description.is,
      "add_dependency" -> <a href="#">This task is blocked...</a>
    )
  }
}
