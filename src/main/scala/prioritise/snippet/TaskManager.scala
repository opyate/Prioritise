package prioritise.snippet

import xml.NodeSeq
import prioritise.model.{Task, User}
import net.liftweb.http.{TemplateFinder, SHtml}
import net.liftweb.util.BindHelpers._
import net.liftweb.http.js.JsCmds._

class TaskManager {
  private lazy val rootTasks: List[Task] =
    User.currentUser.map(_.myRootTasks).openOr(Nil)

  /**
   * Chooses between 'empty' and 'full' template sections in _tasks.html
   */
  def chooser(xhtml: NodeSeq): NodeSeq = {
    val template = if (rootTasks.isEmpty) "empty" else "full"
    bind("b", chooseTemplate("tasks", template, xhtml)
    )
  }

  def add_button(xhtml: NodeSeq): NodeSeq = {
    bind("c", bind("d", xhtml, "add_task" -> <a href="/task_t/create">Add a new task</a>))
  }

  /**
   * This will be called if chooser chooses "full"
   */
  def items(xhtml: NodeSeq): NodeSeq = {
    println(rootTasks)
    rootTasks.flatMap(t =>
      bind("a", task(t, xhtml))
    )
  }

  /**
   * Renders an individual task.
   */
  private def task(t: Task, xhtml: NodeSeq): NodeSeq = {
    bind("t", xhtml,
      "title" -> t.title.is,
      "description" -> t.description.is,
      "add_dependency" -> SHtml.ajaxButton("This is blocked by something else", () => {Alert("Not yet implemented")})
    )
  }
}
