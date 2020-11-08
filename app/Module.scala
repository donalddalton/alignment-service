import com.google.inject.AbstractModule
import com.typesafe.config.Config
import javax.inject.{Inject, Provider, Singleton}
import models.daos.{JobDAO, TargetDAO, UserDAO}
import models.daos.impl.{SlickJobDAO, SlickTargetDAO, SlickUserDAO}
import models.services.{AlignmentService, UserService}
import models.services.impl.{AlignmentServiceImpl, UserServiceImpl}
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future
import slick.jdbc.JdbcBackend.Database

/**
  * This module handles the bindings for the API to the Slick implementation.
  */
class Module() extends AbstractModule {
  // wire the DB, DAO's, and services
  override def configure(): Unit = {
    bind(classOf[Database]).toProvider(classOf[DatabaseProvider])
    bind(classOf[UserDAO]).to(classOf[SlickUserDAO])
    bind(classOf[JobDAO]).to(classOf[SlickJobDAO])
    bind(classOf[TargetDAO]).to(classOf[SlickTargetDAO])
    bind(classOf[UserService]).to(classOf[UserServiceImpl])
    bind(classOf[AlignmentService]).to(classOf[AlignmentServiceImpl])
    bind(classOf[JobDAOCloseHook]).asEagerSingleton()
    bind(classOf[UserDAOCloseHook]).asEagerSingleton()
    bind(classOf[TargetDAOCloseHook]).asEagerSingleton()
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

class TargetDAOCloseHook @Inject() (dao: TargetDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(dao.close())
  }
}

class JobDAOCloseHook @Inject() (dao: JobDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(dao.close())
  }
}
