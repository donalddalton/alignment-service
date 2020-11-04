package models.daos.impl

import javax.inject.{Inject, Singleton}
import models.daos.UserDAO
import models.entities.User
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SlickUserDAO @Inject() (db: Database)(implicit ec: ExecutionContext) extends UserDAO with Tables {
  override val profile: JdbcProfile = _root_.slick.jdbc.PostgresProfile

  import profile.api._

  private val queryByUsername = Compiled((username: Rep[String]) => Users.filter(_.username === username))

  def lookup(username: String): Future[Option[User]] = {
    val f: Future[Option[UsersRow]] = db.run(queryByUsername(username).result.headOption)
    f.map(maybeRow => maybeRow.map(usersRowToUser))
  }

  def all: Future[Seq[User]] = {
    val f = db.run(Users.result)
    f.map(seq => seq.map(usersRowToUser))
  }

  def update(user: User): Future[String] = {
    db.run(queryByUsername(user.username).update(userToUsersRow(user))).map(_ => user.username)
  }

  def delete(username: String): Future[String] = {
    db.run(queryByUsername(username).delete).map(_ => username)
  }

  def create(user: User): Future[String] = {
    db.run(Users += userToUsersRow(user)).map(_ => user.username)
  }

  def close(): Future[Unit] = {
    Future.successful(db.close())
  }

  private def userToUsersRow(user: User): UsersRow = {
    UsersRow(user.username, user.password)
  }

  private def usersRowToUser(usersRow: UsersRow): User = {
    User(usersRow.username, usersRow.password)
  }
}
