package models.daos.impl

import javax.inject.Inject
import models.daos.TargetDAO
import models.entities.Target
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile

class SlickTargetDAO @Inject() (db: Database)(implicit ec: ExecutionContext) extends TargetDAO with Tables {
  override val profile: JdbcProfile = _root_.slick.jdbc.PostgresProfile

  import profile.api._

  private val queryById = Compiled((id: Rep[String]) => Targets.filter(_.id === id))

  def lookup(id: String): Future[Option[Target]] = {
    val f: Future[Option[TargetsRow]] = db.run(queryById(id).result.headOption)
    f.map(maybeRow => maybeRow.map(targetsRowToTarget))
  }

  def all: Future[Seq[Target]] = {
    val f = db.run(Targets.result)
    f.map(seq => seq.map(targetsRowToTarget))
  }

  def update(target: Target): Future[String] = {
    db.run(queryById(target.id).update(targetToTargetsRow(target))).map(_ => target.id)
  }

  def delete(id: String): Future[String] = {
    db.run(queryById(id).delete).map(_ => id)
  }

  def create(target: Target): Future[String] = {
    db.run(Targets += targetToTargetsRow(target)).map(_ => target.id)
  }

  def close(): Future[Unit] = {
    Future.successful(db.close())
  }

  private def targetToTargetsRow(target: Target): TargetsRow = {
    TargetsRow(target.id, target.sequence)
  }

  private def targetsRowToTarget(targetsRow: TargetsRow): Target = {
    Target(targetsRow.id, targetsRow.sequence)
  }
}
