import com.google.inject.AbstractModule
import com.typesafe.config.Config
import javax.inject.{Inject, Provider, Singleton}
import models.daos.UserDAO
import models.daos.impl.SlickUserDAO
import models.services.UserService
import models.services.impl.UserServiceImpl
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future
import slick.jdbc.JdbcBackend.Database

/**
  * This module handles the bindings for the API to the Slick implementation.
  */
class Module() extends AbstractModule {
  override def configure(): Unit = {
    // wire the DB, DAO's, and services
    bind(classOf[Database]).toProvider(classOf[DatabaseProvider])
    bind(classOf[UserDAO]).to(classOf[SlickUserDAO])
    bind(classOf[UserDAOCloseHook]).asEagerSingleton()
    bind(classOf[UserService]).to(classOf[UserServiceImpl])
  }
}

@Singleton
class DatabaseProvider @Inject() (config: Config) extends Provider[Database] {
  lazy val get = Database.forConfig("database", config)
}

/** Closes database connections. */
class UserDAOCloseHook @Inject() (dao: UserDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(dao.close())
  }
}
