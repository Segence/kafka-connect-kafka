package com.segence.kafka.connect.kafka


import EmbeddedKafkaSpecSupport.{Available, NotAvailable}
import HttpStub.{deployConnector, getConnectorConfiguration}
import io.github.embeddedkafka.schemaregistry.EmbeddedKafkaConfig
import io.github.embeddedkafka.connect.EmbeddedKafkaConnect._
import org.apache.kafka.connect.runtime.WorkerConfig
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.nio.file.Files
import scala.util.{Failure, Success}

class ExampleKafkaConnectSpec extends EmbeddedKafkaSpecSupport {

  implicit val kafkaConfig: EmbeddedKafkaConfig = EmbeddedKafkaConfig()

  private val ConnectPort = 6003

  "A Kafka Connect test" should {
//    "start a Connect server on a specified port" in {
//      val connectPort = 7002
//      val offsets     = Files.createTempFile("connect", ".offsets")
//      startConnect(connectPort, offsets) {
//        expectedServerStatus(connectPort, Available)
//      }
//
//      expectedServerStatus(connectPort, NotAvailable)
//    }

    "start a Connect server with custom properties" in {
      val offsets     = Files.createTempFile("connect", ".offsets")
      val extraConfig = Map(
        WorkerConfig.KEY_CONVERTER_CLASS_CONFIG -> "org.apache.kafka.connect.storage.StringConverter",
        WorkerConfig.VALUE_CONVERTER_CLASS_CONFIG -> "org.apache.kafka.connect.storage.StringConverter"
      )
      startConnect(ConnectPort, offsets, extraConfig) {
        (for {
          connectorConfiguration <- getConnectorConfiguration("schemaless.json")
          d <- deployConnector(ConnectPort, connectorConfiguration)
        } yield {
          publishStringMessageToKafka("upstream_non_txn", "message")

        }) match {
          case Success(_) =>
            consumeFirstStringMessageFrom("downstream_non_txn") shouldBe "message"
          case Failure(exception) =>
            fail(exception)
        }
      }
    }

//    "fail to start a Connect server with invalid properties" in {
//      val connectPort = 7002
//      val offsets     = Files.createTempFile("connect", ".offsets")
//      val extraConfig = Map(
//        WorkerConfig.KEY_CONVERTER_CLASS_CONFIG -> "InvalidKeyConverter"
//      )
//      a[ConfigException] shouldBe thrownBy {
//        startConnect(connectPort, offsets, extraConfig) {}
//      }
//
//      expectedServerStatus(connectPort, NotAvailable)
//    }
  }
}
