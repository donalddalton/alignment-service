package models.services.impl

import javax.inject.Inject
import models.daos.UserDAO
import models.entities.User
import models.services.UserService
import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject() (userDAO: UserDAO)(implicit ec: ExecutionContext) extends UserService {
  override def getUsers: Future[Seq[User]] = userDAO.all

  override def getUserByUsername(id: Int): Future[User] = {
    userDAO.lookup(id).map {
      case Some(user) =>
        user
      case None =>
        // TODO map exceptions onto the various play.api.mvc.Results http responses, punting for now
        throw new RuntimeException("foo")
    }
  }

  override def createUser(user: User): Future[Int] = userDAO.create(user)

  override def deleteUser(id: Int): Future[Int] = userDAO.delete(id)
}
