

package bootstrap.liftweb

import net.liftweb._
import http.{LiftRules, NotFoundAsTemplate, ParsePath}
import sitemap.{SiteMap, Menu}
import sitemap.Loc._
import util.NamedPF

import mapper.{Schemifier, DB, StandardDBVendor, DefaultConnectionIdentifier}
import util.Props
import common.Full
import http.S
import prioritise.model._



class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
          Props.get("db.url") openOr
            "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE;TRACE_LEVEL_FILE=2;TRACE_LEVEL_SYSTEM_OUT=2",
          Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User, Task)

    // where to search snippet
    LiftRules.addToPackages("prioritise")

    // build sitemap
    val entries = List(
      Menu("Home") / "index",
      Menu("Tasks") / "tasks" >> LocGroup("public") submenus(Task.menus : _*)
    ) :::
      // the User management menu items
      User.sitemap :::
      Nil
    
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })
    
    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    // What is the function to test if a user is logged in?
     LiftRules.loggedInTest = Full(() => User.loggedIn_?)
 
     // Make a transaction span the whole HTTP request
     S.addAround(DB.buildLoanWrapper)

  }
}