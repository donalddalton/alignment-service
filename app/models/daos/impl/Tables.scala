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

  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Users.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Users
    *  @param id Database column id SqlType(int4), AutoInc, PrimaryKey
    *  @param username Database column username SqlType(varchar), Length(255,true)
    *  @param password Database column password SqlType(varchar), Length(255,true) */
  case class UsersRow(id: Int, username: String, password: String)

  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Int], e1: GR[String]): GR[UsersRow] = GR { prs =>
    import prs._
    UsersRow.tupled((<<[Int], <<[String], <<[String]))
  }

  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends profile.api.Table[UsersRow](_tableTag, "users") {
    def * = (id, username, password) <> (UsersRow.tupled, UsersRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(username), Rep.Some(password))).shaped.<>(
      { r => import r._; _1.map(_ => UsersRow.tupled((_1.get, _2.get, _3.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(int4), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

    /** Database column username SqlType(varchar), Length(255,true) */
    val username: Rep[String] = column[String]("username", O.Length(255, varying = true))

    /** Database column password SqlType(varchar), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255, varying = true))

    /** Uniqueness Index over (username) (database name users_username_key) */
    val index1 = index("users_username_key", username, unique = true)
  }

  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}
