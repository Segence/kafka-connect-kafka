package io.github.embeddedkafka.schemaregistry.connect

import java.nio.file.Path

import io.github.embeddedkafka.schemaregistry.EmbeddedKafkaConfig
import io.github.embeddedkafka.connect.EmbeddedConnectConfig

final class EmbeddedConnectConfigImpl
  extends EmbeddedConnectConfig[EmbeddedKafkaConfig] {
  override def config(connectPort: Int, offsets: Path, extraConfig: Map[String, String])(
    implicit kafkaConfig: EmbeddedKafkaConfig
  ): Map[String, String] =
    baseConnectConfig(connectPort, offsets) ++ extraConfig
}
