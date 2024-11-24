package com.segence.kafka.connect.kafka

import sttp.client3._

import scala.io.Source
import scala.util.{Failure, Success, Using}

case class KafkaConnectDeploymentFailure(message: Option[String]) extends Throwable

object HttpStub {

  private val backend = HttpClientSyncBackend()

  def deployConnector(kafkaConnectPort: Int, connectorConfiguration: String) = {
    val response = basicRequest
      .headers(Map(
        "Content-Type" -> "application/json"
      ))
      .body(connectorConfiguration)
      .post(uri"http://localhost:$kafkaConnectPort/connectors")
      .send(backend)

    response match {
      case response if response.isSuccess               => Success()
      case Response(Left(errorResponse), _, _, _, _, _) => Failure(KafkaConnectDeploymentFailure(Option(errorResponse)))
      case Response(Right(response), _, _, _, _, _)     => Failure(KafkaConnectDeploymentFailure(Option(response)))
    }
  }

  def getConnectorConfiguration(filename: String) = {
    Using(
      Source.fromFile(getClass.getResource(s"/$filename").getPath)
    ) { source =>
      source.mkString
    }.toEither.toTry
  }
}
