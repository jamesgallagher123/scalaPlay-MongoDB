package controllers

import javax.inject.Inject

import scala.concurrent.Future
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import reactivemongo.api.Cursor
import models._
import models.JsonFormats._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json._
import collection._
import play.api.i18n.{I18nSupport, MessagesApi}

class MongoApplicationController @Inject()(val reactiveMongoApi: ReactiveMongoApi, val messagesApi: MessagesApi) extends Controller
  with MongoController with ReactiveMongoComponents with I18nSupport {

  // TODO - keep in mind you need to have mongod.exe running before attempting to play around

  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("persons"))

  def loadCreateUser = Action { implicit request =>
    Ok(views.html.createUser(User.createUserForm))
  }

  def createUser: Action[AnyContent] = Action.async {
    //case class User(firstName: String, lastName: String, age: Int)
    val user = User()
    val futureResult = collection.flatMap(_.insert(user))

    val formValidationResult = User.createUserForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.createUser(formWithErrors))
    }, { user =>
      //add here

    })

    futureResult.map(_ => Ok("Added user " + user.firstName + " " + user.lastName))

  }












  def findByName: Action[AnyContent] = Action.async {
    val cursor: Future[Cursor[User]] = collection.map {
      _.find(Json.obj("lastName" -> "Lastname"))
        .sort(Json.obj("created" -> -1))
        .cursor[User]
    }
    val futureUsersList: Future[List[User]] = cursor.flatMap(_.collect[List]())
    futureUsersList.map { persons =>
      Ok(persons.head.toString)
    }
  }

}