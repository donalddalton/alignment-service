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

  private val queryById = Compiled((id: Rep[Int]) => Users.filter(_.id === id))

  def lookup(id: Int): Future[Option[User]] = {
    val f: Future[Option[UsersRow]] = db.run(queryById(id).result.headOption)
    f.map(maybeRow => maybeRow.map(usersRowToUser))
  }

  def all: Future[Seq[User]] = {
    val f = db.run(Users.result)
    f.map(seq => seq.map(usersRowToUser))
  }

  def update(user: User): Future[Int] = {
    db.run(queryById(user.id).update(userToUsersRow(user)))
  }

  def delete(id: Int): Future[Int] = {
    db.run(queryById(id).delete)
  }

  def create(user: User): Future[Int] = {
    db.run(
      Users += userToUsersRow(user)
    )
  }

  def close(): Future[Unit] = {
    Future.successful(db.close())
  }

  private def userToUsersRow(user: User): UsersRow = {
    UsersRow(user.id, user.username, user.password)
  }

  private def usersRowToUser(usersRow: UsersRow): User = {
    User(usersRow.id, usersRow.username, usersRow.password)
  }
}
