package com.getjenny.starchat.services

/**
  * Created by Angelo Leto <angelo@getjenny.com> on 23/01/19.
  */

import akka.actor.{Actor, Props}
import akka.event.{Logging, LoggingAdapter}
import com.getjenny.starchat.SCActorSystem

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

object CronCleanDeadNodesService {
  implicit val executionContext: ExecutionContext =
    SCActorSystem.system.dispatchers.lookup("starchat.blocking-dispatcher")

  private[this] val log: LoggingAdapter = Logging(SCActorSystem.system, this.getClass.getCanonicalName)
  private[this] val clusterNodesService: ClusterNodesService.type = ClusterNodesService
  private[this] val systemIndexManagementService: SystemIndexManagementService.type = SystemIndexManagementService

  val tickMessage = "tick"

  class CleanDeadNodesTickActor extends Actor {
    def receive: PartialFunction[Any, Unit] = {
      case `tickMessage` =>
        val cleanedNodes = clusterNodesService.cleanDeadNodes
        if (cleanedNodes.deleted > 0) {
          log.debug("Cleaned " + cleanedNodes.deleted + " nodes: " + cleanedNodes.message)
        }
      case _ =>
        log.error("Unknown error cleaning cluster nodes tables")
    }
  }

  def scheduleAction: Unit = {
    val actorRef =
      SCActorSystem.system.actorOf(Props(new CleanDeadNodesTickActor))
    SCActorSystem.system.scheduler.schedule(
      0 seconds,
      systemIndexManagementService.elasticClient.clusterNodeCleanDeadInterval seconds,
      actorRef,
      tickMessage)
  }

}