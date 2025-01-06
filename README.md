Kafka Connect Kafka connector
=============================

![Workflow Status](https://github.com/segence/kafka-connect-kafka/actions/workflows/test.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/Segence/kafka-connect-kafka/badge.svg?branch=main)](https://coveralls.io/github/Segence/kafka-connect-kafka?branch=main)
![Sonatype Nexus (Repository)](https://img.shields.io/maven-central/v/com.segence.kafka.connect/kafka-connect-kafka)

## Summary

A connector with a Kafka sink.

Why?

Because there are use cases to use Kafka Connect for Kafka-to-Kafka traffic while transforming messages.

This connector helps with that use case.

## Installation

1. Download the JAR file containing the connector from the [Maven repository location](https://repo1.maven.org/maven2/com/segence/kafka/connect/kafka-connect-kafka).

   Use the version of your choice.

   e.g. `curl -O https://repo1.maven.org/maven2/com/segence/kafka/connect/kafka-connect-kafka/0.1.0-dev.8/kafka-connect-kafka-0.1.0-dev.8.jar`

2. Copy it under the CLASSPATH of the Kafka Connect installation so that the plugin can be loaded.

    e.g. copy it under the path `/usr/share/confluent-hub-components`

    Additional help: [Install a connector manually](https://docs.confluent.io/platform/current/connect/install.html#install-a-connector-manually) from Confluent.

## Configuration

| **Name**                    | **Description**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | **Default value**                                  |
|:----------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------|
| `sink.bootstrap.servers`    | A list of host/port pairs used to establish the initial connection to the Kafka cluster. Clients use this list to bootstrap and discover the full set of Kafka brokers. While the order of servers in the list does not matter, we recommend including more than one server to ensure resilience if any servers are down. This list does not need to contain the entire set of brokers, as Kafka clients automatically manage and update connections to the cluster efficiently. This list must be in the form host1:port1,host2:port2,... | *(none)*                                           |
| `sink.topic`                | The sink topic name.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | *(none)*                                           |
| `sink.key.converter`        | Converter class for message key that implements the <code>org.apache.kafka.connect.storage.Converter</code> interface.                                                                                                                                                                                                                                                                                                                                                                                                                     | `org.apache.kafka.connect.storage.StringConverter` |
| `sink.value.converter`      | Converter class for message value that implements the <code>org.apache.kafka.connect.storage.Converter</code> interface.                                                                                                                                                                                                                                                                                                                                                                                                                   | `org.apache.kafka.connect.storage.StringConverter` |
| `sink.exactly.once.support` | Whether to enable exactly-once support for source connectors in the cluster by using transactions to write source records and their source offsets, and by proactively fencing out old task generations before bringing up new ones.                                                                                                                                                                                                                                                                                                       | false                                              |
| `sink.callback`             | The callback that is registered on the Kafka Producer. Must be a class implementing <code>org.apache.kafka.clients.producer.Callback</code> and it must be accessible on the CLASSPATH.                                                                                                                                                                                                                                                                                                                                                    | *(none)*                                           |

Converter-specific properties can be passed with the respective `sink.key.converter` or `sink.value.converter` prefix.

Example: to produce Avro messages using Confluent Schema Registry, use the following configuration:

```
"sink.key.converter": "io.confluent.connect.avro.AvroConverter",
"sink.key.converter.schema.registry.url": "http://localhost:8081",
"sink.value.converter": "io.confluent.connect.avro.AvroConverter",
"sink.value.converter.schema.registry.url": "http://localhost:8081"
```

## Example configurations

- [Non-transactional Kafka producer with producer callback class](src/integrationTest/resources/non-transactional-string-converter.json)
- [Transactional producer](src/integrationTest/resources/transactional-string-converter.json)
- [Non-transactional Avro message producer](src/integrationTest/resources/non-transactional-avro-converter.json)

## Building and running locally

### Requirements

- JDK 1.7
- GNU Make

### Building and running

- `make build`
- `docker compose up -d`
- Create topics:
    ```sh
    docker exec -it broker kafka-topics --create --bootstrap-server localhost:9092 --topic upstream-avro
    docker exec -it broker kafka-topics --create --bootstrap-server localhost:9092 --topic downstream-avro
    ```
- Submit the a connector configuration:
    ```shell
    curl --header "Content-Type: application/json" \
    --request POST --data-binary @- \
    http://localhost:8083/connectors <<EOF
    {
      "name": "avro-converter",
      "config": {
        "connector.class": "com.segence.kafka.connect.kafka.KafkaSinkConnector",
        "bootstrap.servers": "PLAINTEXT://broker:29092",
        "topics": "upstream-avro",
        "tasks.max": "1",
        "batch.max.rows": 1,
        "key.converter": "io.confluent.connect.avro.AvroConverter",
        "value.converter": "io.confluent.connect.avro.AvroConverter",
        "key.converter.schema.registry.url": "http://schema-registry:8081",
        "value.converter.schema.registry.url": "http://schema-registry:8081",
        "sink.bootstrap.servers": "PLAINTEXT://broker:29092",
        "sink.topic": "downstream-avro",
        "sink.key.converter": "io.confluent.connect.avro.AvroConverter",
        "sink.value.converter": "io.confluent.connect.avro.AvroConverter",
        "sink.key.converter.schema.registry.url": "http://schema-registry:8081",
        "sink.value.converter.schema.registry.url": "http://schema-registry:8081"
      }
    }
    EOF
    ```
- Send Avro message:
    ```shell
    docker compose exec -it schema-registry bash

    echo '{"f1":"mykey"},{"f2":"myvalue"}' | kafka-avro-console-producer \
    --broker-list PLAINTEXT://broker:29092 \
    --topic upstream-avro \
    --property schema.registry.url=http://localhost:8081 \
    --property key.schema='{"type":"record","name":"mykey","fields":[{"name":"f1","type":"string"}]}' \
    --property value.schema='{"type":"record","name":"myvalue","fields":[{"name":"f2","type":"string"}]}' \
    --property parse.key=true \
    --property key.separator=,
    ```
