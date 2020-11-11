package models.services

import java.util.UUID
import models.entities.AlignmentJob
import scala.concurrent.Future

trait AlignmentService {
  def createJob(
    username: String,
    query: String
  ): Future[AlignmentJob]

  def jobs: Future[Seq[AlignmentJob]]

  def jobsForUser(username: String): Future[Seq[AlignmentJob]]

  def jobForId(id: UUID): Future[Option[AlignmentJob]]
}
