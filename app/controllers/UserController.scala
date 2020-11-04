package controllers

import javax.inject.Inject
import models.entities.User
import models.services.UserService
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject() (
  userService: UserService,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val loginForm: Form[User] = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(User.apply)(User.unapply)
  )

  private val home = routes.HomeController.index()
  private val login = routes.UserController.loginPage()
  private val register = routes.UserController.registerPage()

  def loginPage = Action { implicit request =>
    request.session.get("username") match {
      case Some(_) =>
        Redirect(home)
      case None =>
        Ok(views.html.userLogin(loginForm, routes.UserController.processLoginAttempt()))
    }
  }

  def processLoginAttempt = Action.async { implicit request =>
    val formValidationResult: Form[User] = loginForm.bindFromRequest
    formValidationResult.fold(
      { formWithErrors: Form[User] =>
        Future.successful(
          BadRequest(views.html.userLogin(formWithErrors, routes.UserController.processLoginAttempt()))
        )
      },
      { user: User =>
        val futureFoundUser = userService.checkUserExists(user.username, user.password)
        futureFoundUser.map { foundUser =>
          if (foundUser) {
            Redirect(home)
              .withSession("username" -> user.username)
          } else {
            Redirect(login)
          }
        }
      }
    )
  }

  val pwdMatchConstraint: Constraint[RegisterUserForm] = Constraint("matching")({ registerForm =>
    if (registerForm.password == registerForm.confirmPassword) {
      Valid
    } else {
      Invalid(Seq(ValidationError("Passwords must match")))
    }
  })

  val registerForm: Form[RegisterUserForm] = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "confirmPassword" -> nonEmptyText
    )(RegisterUserForm.apply)(RegisterUserForm.unapply).verifying(pwdMatchConstraint)
  )

  def registerPage = Action { implicit request =>
    request.session.get("username") match {
      case Some(_) =>
        Redirect(home)
      case None =>
        Ok(views.html.userRegister(registerForm, routes.UserController.processCreateUser()))
    }
  }

  def processCreateUser = Action.async { implicit request =>
    val formValidationResult: Form[RegisterUserForm] = registerForm.bindFromRequest
    formValidationResult.fold(
      { formWithErrors: Form[RegisterUserForm] =>
        Future.successful(
          BadRequest(
            views.html.userRegister(
              formWithErrors,
              routes.UserController.processCreateUser()
            )
          )
        )
      },
      { user: RegisterUserForm =>
        val futureFoundUser = userService.checkUserExists(user.username, user.password)
        futureFoundUser.flatMap { foundUser =>
          if (foundUser) {
            Future.successful(Redirect(register))
          } else {
            userService.createUser(User(user.username, user.password)).map { _ =>
              Redirect(home)
                .withSession("username" -> user.username)
            }
          }
        }
      }
    )
  }
}

case class RegisterUserForm(
  username: String,
  password: String,
  confirmPassword: String
)
