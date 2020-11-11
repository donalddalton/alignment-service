package controllers

import controllers.UserController.SESSION_USERNAME_KEY
import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}

class HomeController @Inject() (cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def ping: Action[AnyContent] = Action { implicit request =>
    Ok("pong")
  }

  def index: Action[AnyContent] = Action.async { implicit request =>
    // if cookie contains username redirect to jobs page, otherwise redirect to login
    request.session.get(SESSION_USERNAME_KEY) match {
      case Some(username) =>
        Future.successful(Redirect(routes.JobsController.jobsPage(username)))
      case _ =>
        Future.successful(Redirect(routes.UserController.loginPage()))
    }
  }
}
