package models.services.impl

import java.util.UUID
import java.util.concurrent.{Executors, LinkedBlockingQueue}
import javax.inject.{Inject, Singleton}
import lib.src.main.scala.BioJava
import lib.src.main.scala.BioJava.AlignmentResult
import models.daos.{JobDAO, TargetDAO}
import models.entities.AlignmentJob
import models.services.AlignmentService
import org.joda.time.Instant
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Random

@Singleton
class AlignmentServiceImpl @Inject() (
  jobsDAO: JobDAO,
  targetDAO: TargetDAO
)(implicit ec: ExecutionContext)
  extends AlignmentService {

  private val log = play.api.Logger(this.getClass).logger

  private def uuid: UUID = java.util.UUID.randomUUID

  /**
    * Buffer alignment jobs in a [[LinkedBlockingQueue]]. TODO
    */
  private val queue = new LinkedBlockingQueue[AlignmentJob]()

  // Create ((# CPU cores) / 2) alignment workers for blocking computations
  private val cores = Runtime.getRuntime.availableProcessors() / 2
  private val pool = Executors.newFixedThreadPool(cores)
  for (_ <- 1 to cores) {
    pool.submit(new Consumer(queue))
  }

  class Consumer(q: LinkedBlockingQueue[AlignmentJob]) extends Runnable {
    private val timeout = Duration.apply(2, "second")

    def run(): Unit = {
      try {
        while (true) {
          // BLOCKING calls OK in here
          val job = q
            .take()
            .copy(startedAt = Some(Instant.now().toDateTime))

          val targetIds: Iterator[String] = Random
            .shuffle( // randomly shuffle targets
              Await.result(
                targetDAO.all.map(_.map(_.id)),
                timeout
              )
            )
            .iterator

          // iterate until we either find a matching target OR run out of targets to test
          var targetMatch: Option[AlignmentResult] = None
          while (
            //  targetMatch.isEmpty &&  TODO uncomment this
            targetIds.hasNext
          ) {
            val id = targetIds.next()
            val t = Await.result(targetDAO.lookup(id).map(_.get), timeout)
            targetMatch = BioJava.pairwiseAlignment(job.query, t.sequence, id)
          }

          val updatedJob = targetMatch match {
            case Some(result) =>
              // match has been found
              job.copy(
                completedAt = Some(Instant.now().toDateTime),
                targetMatch = Some(result.targetMatchId),
                result = Some(result.result)
              )
            case None =>
              // we have iterated through all targets and no match was found
              job.copy(
                completedAt = Some(Instant.now().toDateTime),
                result = Some("No match found")
              )
          }

          jobsDAO.update(updatedJob)
        }
      } catch {
        // catch everything to ensure this thread never dies
        case ex: Throwable =>
          log.error("An error occurred", ex)
      }
    }
  }

  override def createJob(
    username: String,
    query: String
  ): Future[AlignmentJob] = {
    val job = AlignmentJob(
      uuid,
      username,
      query,
      Instant.now().toDateTime
    )

    jobsDAO
      .create(job)
      .map { _ =>
        queue.put(job)

        job
      }
  }

  override def jobs: Future[Seq[AlignmentJob]] = {
    jobsDAO.all.map(_.sortBy(_.createdAt).reverse)
  }

  override def jobsForUser(username: String): Future[Seq[AlignmentJob]] = {
    jobsDAO.all.map(jobs => jobs.filter(_.username == username).sortBy(_.createdAt).reverse)
  }
}
