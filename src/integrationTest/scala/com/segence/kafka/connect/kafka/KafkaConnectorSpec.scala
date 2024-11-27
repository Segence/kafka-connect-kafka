package com.segence.kafka.connect.kafka

import com.segence.kafka.connect.kafka.HttpStub.{deployConnector, getConnectorConfiguration}
import io.github.embeddedkafka.connect.EmbeddedKafkaConnect._
import io.github.embeddedkafka.schemaregistry.EmbeddedKafkaConfig

import java.nio.file.Files
import scala.util.{Failure, Success}

class KafkaConnectorSpec extends EmbeddedKafkaSpecSupport {

  implicit val kafkaConfig: EmbeddedKafkaConfig = EmbeddedKafkaConfig()

  private val ConnectPort = 6003

  "The Kafka connector" should {
    "produce a message with non-transactional delivery" in {
      val offsets     = Files.createTempFile("connect", ".offsets")
      startConnect(ConnectPort, offsets) {
        (for {
          nonTransactionalConnectorConfiguration <- getConnectorConfiguration("non-transactional-string-converter.json")
          _ <- deployConnector(ConnectPort, nonTransactionalConnectorConfiguration)

          transactionalConnectorConfiguration <- getConnectorConfiguration("transactional-string-converter.json")
          _ <- deployConnector(ConnectPort, transactionalConnectorConfiguration)
        } yield {
          publishStringMessageToKafka("upstream_non_txn", "message")
          publishStringMessageToKafka("upstream_txn", "message")
        }) match {
          case Success(_) =>
            consumeFirstStringMessageFrom("downstream_non_txn") shouldBe "message"
            consumeFirstStringMessageFrom("downstream_txn") shouldBe "message"
          case Failure(exception) =>
            fail(exception)
        }
      }
    }
  }
}
