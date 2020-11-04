package models.services

import models.entities.User
import scala.concurrent.Future

// CRUD API for users
trait UserService {

  def checkUserExists(username: String): Future[Boolean]

  def checkUserExists(username: String, password: String): Future[Boolean]

  def createUser(user: User): Future[String]
}
