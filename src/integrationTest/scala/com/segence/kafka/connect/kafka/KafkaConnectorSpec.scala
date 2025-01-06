package com.segence.kafka.connect.kafka

import com.segence.kafka.connect.kafka.HttpStub.{deployConnector, getConnectorConfiguration}
import com.segence.kafka.connect.kafka.schema.{TestMessageKey, TestMessageValue}
import io.confluent.kafka.serializers.{AbstractKafkaSchemaSerDeConfig, KafkaAvroDeserializer, KafkaAvroDeserializerConfig, KafkaAvroSerializer}
import io.github.embeddedkafka.schemaregistry.connect.EmbeddedKafkaConnect._
import io.github.embeddedkafka.schemaregistry.EmbeddedKafkaConfig
import org.apache.kafka.common.serialization.{Deserializer, Serializer}

import java.nio.file.Files
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.language.postfixOps

class KafkaConnectorSpec extends EmbeddedKafkaSpecSupport {

  implicit val kafkaConfig: EmbeddedKafkaConfig = EmbeddedKafkaConfig(
    schemaRegistryPort = 6002
  )

  private val ConnectPort = 6003

  "The Kafka connector" should {
    "produce a message with non-transactional delivery" in {

      implicit val testMessageKeySerializer: Serializer[TestMessageKey] = {
        val props = Map(
          AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> s"http://localhost:${kafkaConfig.schemaRegistryPort}"
        )

        val ser = new KafkaAvroSerializer
        ser.configure(props.asJava, true)
        ser.asInstanceOf[Serializer[TestMessageKey]]
      }

      implicit val testMessageValueSerializer: Serializer[TestMessageValue] = {
        val props = Map(
          AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> s"http://localhost:${kafkaConfig.schemaRegistryPort}"
        )

        val ser = new KafkaAvroSerializer
        ser.configure(props.asJava, false)
        ser.asInstanceOf[Serializer[TestMessageValue]]
      }

      implicit val testMessageKeyDeserializer: Deserializer[TestMessageKey] = {
        val props = Map(
          AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> s"http://localhost:${kafkaConfig.schemaRegistryPort}",
          KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> true.toString
        )

        val ser = new KafkaAvroDeserializer
        ser.configure(props.asJava, true)
        ser.asInstanceOf[Deserializer[TestMessageKey]]
      }

      implicit val testMessageValueDeserializer: Deserializer[TestMessageValue] = {
        val props = Map(
          AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> s"http://localhost:${kafkaConfig.schemaRegistryPort}",
          KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> true.toString
        )

        val ser = new KafkaAvroDeserializer
        ser.configure(props.asJava, false)
        ser.asInstanceOf[Deserializer[TestMessageValue]]
      }

      val avroMessageKey = TestMessageKey.newBuilder().setTenant("key").setOrganization("sample org").build()
      val avroMessageValue = TestMessageValue.newBuilder().setTenant("payload").setOrganization("sample org").build()

      val offsets = Files.createTempFile("connect", ".offsets")

      startConnect(ConnectPort, offsets) {
        (for {
          nonTransactionalStringConnectorConfiguration <- getConnectorConfiguration("non-transactional-string-converter.json")
          _ <- deployConnector(ConnectPort, nonTransactionalStringConnectorConfiguration)

          transactionalStringConnectorConfiguration <- getConnectorConfiguration("transactional-string-converter.json")
          _ <- deployConnector(ConnectPort, transactionalStringConnectorConfiguration)

          nonTransactionalAvroConnectorConfiguration <- getConnectorConfiguration("non-transactional-avro-converter.json")
          _ <- deployConnector(ConnectPort, nonTransactionalAvroConnectorConfiguration)
        } yield {
          publishStringMessageToKafka("upstream_non_txn_string", "message")
          publishStringMessageToKafka("upstream_txn_string", "message")
          publishToKafka("upstream_non_txn_avro", avroMessageKey, avroMessageValue)
        }) match {
          case Success(_) =>
            consumeFirstStringMessageFrom("downstream_non_txn_string") shouldBe "message"
            consumeFirstStringMessageFrom("downstream_txn_string") shouldBe "message"
            consumeFirstKeyedMessageFrom[TestMessageKey, TestMessageValue]("downstream_non_txn_avro", timeout = 30 seconds) shouldBe (avroMessageKey, avroMessageValue)
          case Failure(exception) =>
            fail(exception)
        }
      }
    }
  }
}
