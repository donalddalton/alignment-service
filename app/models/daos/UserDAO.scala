package models.daos

import models.entities.User
import scala.concurrent.Future

/**
  * An implementation dependent DAO.
  */
trait UserDAO {

  def lookup(username: String): Future[Option[User]]

  def all: Future[Seq[User]]

  def update(user: User): Future[String]

  def delete(id: String): Future[String]

  def create(user: User): Future[String]

  def close(): Future[Unit]
}
