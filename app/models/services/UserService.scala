package models.services

import models.entities.User
import scala.concurrent.Future

// CRUD API for users
trait UserService {
  def getUsers: Future[Seq[User]]

  def getUserByUsername(id: Int): Future[User]

  def createUser(user: User): Future[Int]

  def deleteUser(id: Int): Future[Int]
}
