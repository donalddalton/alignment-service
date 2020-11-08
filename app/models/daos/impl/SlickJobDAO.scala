package models.daos.impl

import java.util.UUID
import javax.inject.{Inject, Singleton}
import models.daos.JobDAO
import models.entities.AlignmentJob
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile

@Singleton
class SlickJobDAO @Inject() (db: Database)(implicit ec: ExecutionContext) extends JobDAO with Tables {
  override val profile: JdbcProfile = _root_.slick.jdbc.PostgresProfile

  import profile.api._

  private val queryById = Compiled((id: Rep[UUID]) => Jobs.filter(_.id === id))

  override def lookup(id: UUID): Future[Option[AlignmentJob]] = {
    val f: Future[Option[JobsRow]] = db.run(queryById(id).result.headOption)
    f.map(maybeRow => maybeRow.map(jobsRowToJob))
  }

  override def all: Future[Seq[AlignmentJob]] = {
    val f = db.run(Jobs.result)
    f.map(seq => seq.map(jobsRowToJob))
  }

  override def create(job: AlignmentJob): Future[UUID] = {
    db.run(Jobs += jobToJobsRow(job)).map(_ => job.id)
  }

  override def delete(id: UUID): Future[UUID] = {
    db.run(queryById(id).delete).map(_ => id)
  }

  override def update(job: AlignmentJob): Future[UUID] = {
    db.run(queryById(job.id).update(jobToJobsRow(job))).map(_ => job.id)
  }

  override def close(): Future[Unit] = {
    Future.successful(db.close())
  }

  private def jobToJobsRow(job: AlignmentJob): JobsRow = {
    JobsRow(
      job.id,
      job.username,
      job.query,
      job.createdAt,
      job.startedAt,
      job.completedAt,
      job.targetMatch,
      job.result
    )
  }

  private def jobsRowToJob(jobsRow: JobsRow): AlignmentJob = {
    AlignmentJob(
      jobsRow.id,
      jobsRow.username,
      jobsRow.query,
      jobsRow.createdAt,
      jobsRow.startedAt,
      jobsRow.completedAt,
      jobsRow.targetMatchId,
      jobsRow.result
    )
  }
}
