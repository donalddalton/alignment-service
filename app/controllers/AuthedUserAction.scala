package controllers

import javax.inject.Inject
import play.api.mvc.Results._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

class AuthedUserAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  private val logger = play.api.Logger(this.getClass)

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    logger.debug("Authorizing user")
    val maybeUsername = request.session.get("username")
    maybeUsername match {
      case Some(_) =>
        block(request)
      case None =>
        Future.successful(Redirect(routes.UserController.loginPage()))
    }
  }
}
