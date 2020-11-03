package controllers

import javax.inject.{Inject, Singleton}
import models.entities.User
import models.services.UserService
import play.api.libs.json.{JsError, JsObject, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject() (userService: UserService, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val UserReads = Json.reads[User]
  implicit val UserWrites = Json.writes[User]

  def getUsers = Action.async { implicit request =>
    userService.getUsers.map { users =>
      Ok(JsObject(Map("users" -> Json.toJson(users))))
    }
  }

  def createUser = Action(parse.json).async { implicit request =>
    val expResult = request.body.validate[User]
    expResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      user => {
        userService.createUser(user).map { id =>
          Created(Json.obj("message" -> (s"Saved user: $id")))
        }
      }
    )
  }

  def deleteUser(id: Int) = Action.async { implicit request =>
    userService.deleteUser(id).map { user =>
      Ok(JsObject(Map("deleted" -> Json.toJson(user))))
    }
  }
}
