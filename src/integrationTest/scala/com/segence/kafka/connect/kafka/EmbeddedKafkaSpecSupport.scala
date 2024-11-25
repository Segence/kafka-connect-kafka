package com.segence.kafka.connect.kafka

import com.segence.kafka.connect.kafka.EmbeddedKafkaSpecSupport.{Available, NotAvailable, ServerStatus}
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Milliseconds, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpecLike

import java.net.{InetAddress, Socket}
import scala.util.{Failure, Success, Try}

trait EmbeddedKafkaSpecSupport
  extends AnyWordSpecLike
    with Matchers
    with Eventually
    with IntegrationPatience {

  implicit val config: PatienceConfig =
    PatienceConfig(Span(1, Seconds), Span(100, Milliseconds))

  private def status(port: Int): ServerStatus = {
    Try(new Socket(InetAddress.getByName("localhost"), port)) match {
      case Failure(_) => NotAvailable
      case Success(_) => Available
    }
  }
}

object EmbeddedKafkaSpecSupport {
  sealed trait ServerStatus
  case object Available    extends ServerStatus
  case object NotAvailable extends ServerStatus
}
