package controllers

import controllers.UserController.SESSION_USERNAME_KEY
import java.util.UUID
import javax.inject.{Inject, Singleton}
import models.entities.AlignmentJob
import models.services.AlignmentService
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object JobsController {
  case class JobForm(query: String)
}

@Singleton
class JobsController @Inject() (
  alignmentService: AlignmentService,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {
  import JobsController._

  private def userJobs(username: String) = routes.JobsController.jobsPage(username)

  val jobForm: Form[JobForm] = Form(
    mapping(
      "query" -> nonEmptyText(maxLength = 200)
    )(JobForm.apply)(JobForm.unapply)
  )

  def jobResultPage(id: UUID): Action[AnyContent] = Action.async { implicit request =>
    request.session.get(SESSION_USERNAME_KEY) match {
      case Some(username) =>
        alignmentService.jobForId(id).flatMap { maybeJob =>
          maybeJob match {
            case Some(job @ AlignmentJob(_, `username`, _, _, _, _, _, _)) =>
              Future.successful(Ok(views.html.job(job)))
            case _ =>
              Future.successful(Redirect(routes.UserController.loginPage()))
          }
        }
      case _ =>
        Future.successful(Redirect(routes.UserController.loginPage()))
    }
  }

  def jobsPage(username: String): Action[AnyContent] = Action.async { implicit request =>
    request.session.get(SESSION_USERNAME_KEY) match {
      case Some(`username`) =>
        alignmentService.jobsForUser(username).map { allJobs =>
          Ok(views.html.userJobs(jobForm, allJobs, routes.JobsController.processCreateJob(username)))
        }
      case _ =>
        Future.successful(Redirect(routes.UserController.loginPage()))
    }
  }

  def processCreateJob(username: String): Action[AnyContent] = Action.async { implicit request =>
    request.session.get(SESSION_USERNAME_KEY) match {
      case Some(`username`) =>
        val jobFormValidationResult: Form[JobForm] = jobForm.bindFromRequest

        jobFormValidationResult.fold(
          { formWithErrors: Form[JobForm] =>
            alignmentService.jobsForUser(username).map { allJobs =>
              BadRequest(views.html.userJobs(formWithErrors, allJobs, routes.JobsController.processCreateJob(username)))
            }
          },
          { job: JobForm =>
            alignmentService
              .createJob(
                username,
                job.query
              )
              .map { _ =>
                Redirect(userJobs(username))
              }
          }
        )
      case _ =>
        Future.successful(Redirect(routes.UserController.loginPage()))
    }
  }

  def profile(n: Int): Action[AnyContent] = Action.async { implicit request =>
    val alpha = "ATCG"
    def randStr(n: Int) = (1 to n).map(_ => alpha(Random.nextInt(alpha.length))).mkString
    alignmentService
      .createJob(
        "steve",
        randStr(Math.abs(n))
      )
      .map(_ => Ok("Ok"))
  }
}
