package com.segence.kafka.connect.kafka

import sttp.client3._
import sttp.model.StatusCode

import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

case class KafkaConnectDeploymentFailure(message: Option[String]) extends Throwable

protected[kafka] object HttpStub {

  private val backend = HttpClientSyncBackend()

  def deployConnector(kafkaConnectPort: Int, connectorConfiguration: String): Try[StatusCode] = {
    val response = basicRequest
      .headers(Map(
        "Content-Type" -> "application/json"
      ))
      .body(connectorConfiguration)
      .post(uri"http://localhost:$kafkaConnectPort/connectors")
      .send(backend)

    response match {
      case response if response.isSuccess               => Success(response.code)
      case Response(Left(errorResponse), _, _, _, _, _) => Failure(KafkaConnectDeploymentFailure(Option(errorResponse)))
      case Response(Right(response), _, _, _, _, _)     => Failure(KafkaConnectDeploymentFailure(Option(response)))
    }
  }

  def getConnectorConfiguration(filename: String): Try[String] =
    Using(
      Source.fromFile(getClass.getResource(s"/$filename").getPath)
    ) { source =>
      source.mkString
    }.toEither.toTry
}
