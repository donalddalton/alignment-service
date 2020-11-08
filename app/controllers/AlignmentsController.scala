package controllers

import javax.inject.{Inject, Singleton}
import models.services.AlignmentService
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.libs.json.Json
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import scala.concurrent.{ExecutionContext, Future}

object AlignmentsController {
  case class JobForm(query: String)
}

@Singleton
class AlignmentsController @Inject() (
  alignmentService: AlignmentService,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {
  import AlignmentsController._

  private val log = play.api.Logger(this.getClass).logger

  val jobForm: Form[JobForm] = Form(
    mapping(
      "query" -> nonEmptyText
    )(JobForm.apply)(JobForm.unapply)
  )

  def jobsPage = Action.async { implicit request =>
    val username = "steve" // TODO
    alignmentService.jobsForUser(username).map { allJobs =>
      Ok(views.html.userJobs(jobForm, allJobs, routes.AlignmentsController.align()))
    }
  }

  def align = Action.async { implicit request =>
    val jobFormValidationResult: Form[JobForm] = jobForm.bindFromRequest

    jobFormValidationResult.fold(
      { formWithErrors: Form[JobForm] =>
        alignmentService.jobs.map { allJobs =>
          BadRequest(views.html.userJobs(formWithErrors, allJobs, routes.AlignmentsController.jobsPage))
        }
      },
      { job: JobForm =>
        alignmentService
          .createJob(
            "steve", // TODO
            job.query
          )
          .map { j =>
            Redirect(routes.AlignmentsController.jobsPage)
          }
      }
    )
  }
}
