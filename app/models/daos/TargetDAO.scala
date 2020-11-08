package models.daos

import models.entities.Target
import scala.concurrent.Future

trait TargetDAO {
  def lookup(id: String): Future[Option[Target]]

  def all: Future[Seq[Target]]

  def update(target: Target): Future[String]

  def delete(id: String): Future[String]

  def create(target: Target): Future[String]

  def close(): Future[Unit]
}
