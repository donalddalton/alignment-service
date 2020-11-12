package models.entities

import java.util.UUID
import org.joda.time.DateTime

/**
  * Alignment job record
  *
  * @param id Unique identifier
  * @param username the user that created the job.
  * @param query the query sequence e.g. `ATGC`.
  * @param createdAt job creation time.
  * @param startedAt time job was picked up and started.
  * @param completedAt job completion time.
  * @param targetMatch the [[Target.id]] of the matching sequence if a match is found, otherwise none
  * @param result match summary if a match is found, otherwise none
  */
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
