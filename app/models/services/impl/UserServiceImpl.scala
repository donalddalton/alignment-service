package models.services.impl

import javax.inject.{Inject, Singleton}
import models.daos.UserDAO
import models.entities.User
import models.services.UserService
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserServiceImpl @Inject() (userDAO: UserDAO)(implicit ec: ExecutionContext) extends UserService {

  override def createUser(user: User): Future[String] = userDAO.create(user)

  override def checkUserExists(username: String, password: String): Future[Boolean] = {
    userDAO.lookup(username).map { maybeUser =>
      maybeUser.exists(user => user.username == username && user.password == password)
    }
  }

  override def checkUserExists(username: String): Future[Boolean] = {
    userDAO.lookup(username).map { maybeUser =>
      maybeUser.exists(user => user.username == username)
    }
  }
}
