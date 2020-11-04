package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext

class HomeController @Inject() (
  cc: ControllerComponents,
  authedUserAction: AuthedUserAction
)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def index = authedUserAction { implicit request =>
    Ok(views.html.index(request.session("username")))
  }
}
