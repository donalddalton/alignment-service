package models.entities

case class User(
  id: Int,
  username: String,
  password: String // wouldn't do this in practice
)
