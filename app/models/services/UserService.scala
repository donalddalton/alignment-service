package models.services

import models.entities.User
import scala.concurrent.Future

trait UserService {

  def checkUserExists(username: String): Future[Boolean]

  def validateUser(username: String, password: String): Future[Boolean]

  def createUser(user: User): Future[String]
}
