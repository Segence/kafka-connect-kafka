package io.github.embeddedkafka.schemaregistry.connect

import io.github.embeddedkafka.schemaregistry.{
  EmbeddedKafkaConfig,
  EmbeddedKafka
}
import io.github.embeddedkafka.connect.EmbeddedKafkaConnectSupport

trait EmbeddedKafkaConnect
  extends EmbeddedKafkaConnectSupport[EmbeddedKafkaConfig]
    with EmbeddedKafka {
  override protected[embeddedkafka] val connectConfig =
    new EmbeddedConnectConfigImpl
}

object EmbeddedKafkaConnect extends EmbeddedKafkaConnect
