package models.daos.impl
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.PostgresProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._
  import org.joda.time.DateTime
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Jobs.schema ++ Targets.schema ++ Users.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Jobs
    *  @param id Database column id SqlType(uuid), PrimaryKey
    *  @param username Database column username SqlType(varchar), Length(255,true)
    *  @param query Database column query SqlType(text)
    *  @param createdAt Database column created_at SqlType(timestamp)
    *  @param startedAt Database column started_at SqlType(timestamp), Default(None)
    *  @param completedAt Database column completed_at SqlType(timestamp), Default(None)
    *  @param targetMatchId Database column target_match_id SqlType(varchar), Length(255,true), Default(None)
    *  @param result Database column result SqlType(text), Default(None) */
  case class JobsRow(
    id: java.util.UUID,
    username: String,
    query: String,
    createdAt: DateTime,
    startedAt: Option[DateTime] = None,
    completedAt: Option[DateTime] = None,
    targetMatchId: Option[String] = None,
    result: Option[String] = None
  )

  /** GetResult implicit for fetching JobsRow objects using plain SQL queries */
  implicit def GetResultJobsRow(
    implicit e0: GR[java.util.UUID],
    e1: GR[String],
    e2: GR[DateTime],
    e3: GR[Option[DateTime]],
    e4: GR[Option[String]]
  ): GR[JobsRow] = GR { prs =>
    import prs._
    JobsRow.tupled(
      (<<[java.util.UUID], <<[String], <<[String], <<[DateTime], <<?[DateTime], <<?[DateTime], <<?[String], <<?[String])
    )
  }

  /** Table description of table jobs. Objects of this class serve as prototypes for rows in queries. */
  class Jobs(_tableTag: Tag) extends profile.api.Table[JobsRow](_tableTag, "jobs") {
    def * = (
      id,
      username,
      query,
      createdAt,
      startedAt,
      completedAt,
      targetMatchId,
      result
    ) <> (JobsRow.tupled, JobsRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (
      (
        Rep.Some(id),
        Rep.Some(username),
        Rep.Some(query),
        Rep.Some(createdAt),
        startedAt,
        completedAt,
        targetMatchId,
        result
      )
    ).shaped.<>(
      { r => import r._; _1.map(_ => JobsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6, _7, _8))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)

    /** Database column username SqlType(varchar), Length(255,true) */
    val username: Rep[String] = column[String]("username", O.Length(255, varying = true))

    /** Database column query SqlType(text) */
    val query: Rep[String] = column[String]("query")

    /** Database column created_at SqlType(timestamp) */
    val createdAt: Rep[DateTime] = column[DateTime]("created_at")

    /** Database column started_at SqlType(timestamp), Default(None) */
    val startedAt: Rep[Option[DateTime]] = column[Option[DateTime]]("started_at", O.Default(None))

    /** Database column completed_at SqlType(timestamp), Default(None) */
    val completedAt: Rep[Option[DateTime]] = column[Option[DateTime]]("completed_at", O.Default(None))

    /** Database column target_match_id SqlType(varchar), Length(255,true), Default(None) */
    val targetMatchId: Rep[Option[String]] =
      column[Option[String]]("target_match_id", O.Length(255, varying = true), O.Default(None))

    /** Database column result SqlType(text), Default(None) */
    val result: Rep[Option[String]] = column[Option[String]]("result", O.Default(None))

    /** Foreign key referencing Targets (database name fk_target_id) */
    lazy val targetsFk = foreignKey("fk_target_id", targetMatchId, Targets)(
      r => Rep.Some(r.id),
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.Cascade
    )

    /** Foreign key referencing Users (database name fk_username) */
    lazy val usersFk = foreignKey("fk_username", username, Users)(
      r => r.username,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.Cascade
    )
  }

  /** Collection-like TableQuery object for table Jobs */
  lazy val Jobs = new TableQuery(tag => new Jobs(tag))

  /** Entity class storing rows of table Targets
    *  @param id Database column id SqlType(varchar), PrimaryKey, Length(255,true)
    *  @param sequence Database column sequence SqlType(text) */
  case class TargetsRow(id: String, sequence: String)

  /** GetResult implicit for fetching TargetsRow objects using plain SQL queries */
  implicit def GetResultTargetsRow(implicit e0: GR[String]): GR[TargetsRow] = GR { prs =>
    import prs._
    TargetsRow.tupled((<<[String], <<[String]))
  }

  /** Table description of table targets. Objects of this class serve as prototypes for rows in queries. */
  class Targets(_tableTag: Tag) extends profile.api.Table[TargetsRow](_tableTag, "targets") {
    def * = (id, sequence) <> (TargetsRow.tupled, TargetsRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(sequence))).shaped.<>(
      { r => import r._; _1.map(_ => TargetsRow.tupled((_1.get, _2.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(varchar), PrimaryKey, Length(255,true) */
    val id: Rep[String] = column[String]("id", O.PrimaryKey, O.Length(255, varying = true))

    /** Database column sequence SqlType(text) */
    val sequence: Rep[String] = column[String]("sequence")
  }

  /** Collection-like TableQuery object for table Targets */
  lazy val Targets = new TableQuery(tag => new Targets(tag))

  /** Entity class storing rows of table Users
    *  @param username Database column username SqlType(varchar), PrimaryKey, Length(255,true)
    *  @param password Database column password SqlType(varchar), Length(255,true) */
  case class UsersRow(username: String, password: String)

  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[String]): GR[UsersRow] = GR { prs =>
    import prs._
    UsersRow.tupled((<<[String], <<[String]))
  }

  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends profile.api.Table[UsersRow](_tableTag, "users") {
    def * = (username, password) <> (UsersRow.tupled, UsersRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(username), Rep.Some(password))).shaped.<>(
      { r => import r._; _1.map(_ => UsersRow.tupled((_1.get, _2.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column username SqlType(varchar), PrimaryKey, Length(255,true) */
    val username: Rep[String] = column[String]("username", O.PrimaryKey, O.Length(255, varying = true))

    /** Database column password SqlType(varchar), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255, varying = true))
  }

  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}
