package models.entities

import java.util.UUID
import org.joda.time.DateTime

case class AlignmentJob(
  id: UUID,
  username: String,
  query: String,
  createdAt: DateTime,
  startedAt: Option[DateTime] = None,
  completedAt: Option[DateTime] = None,
  targetMatch: Option[String] = None,
  result: Option[String] = None
)
