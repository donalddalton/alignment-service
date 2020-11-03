package models.daos

import models.entities.User
import scala.concurrent.Future

/**
  * An implementation dependent DAO.
  */
trait UserDAO {

  def lookup(id: Int): Future[Option[User]]

  def all: Future[Seq[User]]

  def update(user: User): Future[Int]

  def delete(id: Int): Future[Int]

  def create(user: User): Future[Int]

  def close(): Future[Unit]
}
