package models.daos

import java.util.UUID
import models.entities.AlignmentJob
import scala.concurrent.Future

trait JobDAO {

  def lookup(id: UUID): Future[Option[AlignmentJob]]

  def all: Future[Seq[AlignmentJob]]

  def create(job: AlignmentJob): Future[UUID]

  def delete(id: UUID): Future[UUID]

  def update(job: AlignmentJob): Future[UUID]

  def close(): Future[Unit]
}
