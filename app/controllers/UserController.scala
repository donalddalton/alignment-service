package controllers

import javax.inject.Inject
import models.entities.User
import models.services.UserService
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

object UserController {
  val SESSION_USERNAME_KEY = "username"

  case class RegisterUserForm(
    username: String,
    password: String,
    confirmPassword: String
  )
}

class UserController @Inject() (
  userService: UserService,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {
  import UserController._

  private val home = routes.HomeController.index()
  private val login = routes.UserController.loginPage()
  private val register = routes.UserController.registerPage()

  private val pwdMatchConstraint: Constraint[RegisterUserForm] = Constraint("matching")({ registerForm =>
    if (registerForm.password == registerForm.confirmPassword) {
      Valid
    } else {
      Invalid(Seq(ValidationError("Passwords must match")))
    }
  })

  private val registerForm: Form[RegisterUserForm] = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "confirmPassword" -> nonEmptyText
    )(RegisterUserForm.apply)(RegisterUserForm.unapply).verifying(pwdMatchConstraint)
  )

  private val loginForm: Form[User] = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(User.apply)(User.unapply)
  )

  def loginPage: Action[AnyContent] = Action { implicit request =>
    request.session.get(SESSION_USERNAME_KEY) match {
      case Some(_) =>
        // if already logged in send to jobs page
        Redirect(home)
      case None =>
        // otherwise show login form
        Ok(views.html.userLogin(loginForm, routes.UserController.processLoginAttempt()))
    }
  }

  def logout = Action { implicit request =>
    Redirect(routes.UserController.loginPage()).withNewSession
  }

  def processLoginAttempt: Action[AnyContent] = Action.async { implicit request =>
    val formValidationResult: Form[User] = loginForm.bindFromRequest
    formValidationResult.fold(
      { formWithErrors: Form[User] =>
        Future.successful(
          BadRequest(views.html.userLogin(formWithErrors, routes.UserController.processLoginAttempt()))
        )
      },
      { user: User =>
        // crude user authentication
        val futureFoundUser = userService.validateUser(user.username, user.password)
        futureFoundUser.map { foundUser =>
          if (foundUser) {
            // if login is successful, redirect to jobs page and add the username to the session cookie
            Redirect(home).withSession(SESSION_USERNAME_KEY -> user.username)
          } else {
            // otherwise send back to login page
            Redirect(login)
              .flashing("Error" -> "Invalid credentials")
          }
        }
      }
    )
  }

  def registerPage: Action[AnyContent] = Action { implicit request =>
    request.session.get(SESSION_USERNAME_KEY) match {
      case Some(_) =>
        // if already logged in send to jobs page
        Redirect(home)
      case None =>
        // otherwise show create user form
        Ok(views.html.userRegister(registerForm, routes.UserController.processCreateUser()))
    }
  }

  def processCreateUser: Action[AnyContent] = Action.async { implicit request =>
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
        val futureFoundUser = userService.checkUserExists(user.username)
        futureFoundUser.flatMap { foundUser =>
          if (foundUser) {
            // if username is already taken redirect to register user page
            Future.successful(Redirect(register))
          } else {
            // otherwise create the new user
            userService.createUser(User(user.username, user.password)).map { _ =>
              Redirect(home)
                .withSession(SESSION_USERNAME_KEY -> user.username)
            }
          }
        }
      }
    )
  }
}
