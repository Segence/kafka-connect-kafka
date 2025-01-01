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

## Configuration

| **Name**                    | **Description**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | **Default value**                                      |
|:----------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------|
| `sink.bootstrap.servers`    | A list of host/port pairs used to establish the initial connection to the Kafka cluster. Clients use this list to bootstrap and discover the full set of Kafka brokers. While the order of servers in the list does not matter, we recommend including more than one server to ensure resilience if any servers are down. This list does not need to contain the entire set of brokers, as Kafka clients automatically manage and update connections to the cluster efficiently. This list must be in the form host1:port1,host2:port2,... | *(none)*                                               |
| `sink.topic`                | The sink topic name.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | *(none)*                                               |
| `sink.key.serializer`       | Serializer class for key that implements the <code>org.apache.kafka.common.serialization.Serializer</code> interface.                                                                                                                                                                                                                                                                                                                                                                                                                      | org.apache.kafka.common.serialization.StringSerializer |
| `sink.value.serializer`     | Serializer class for value that implements the <code>org.apache.kafka.common.serialization.Serializer</code> interface.                                                                                                                                                                                                                                                                                                                                                                                                                    | org.apache.kafka.common.serialization.StringSerializer |
| `sink.exactly.once.support` | Whether to enable exactly-once support for source connectors in the cluster by using transactions to write source records and their source offsets, and by proactively fencing out old task generations before bringing up new ones.                                                                                                                                                                                                                                                                                                       | false                                                  |
| `sink.callback`             | The callback that is registered on the Kafka Producer. Must be a class implementing <code>org.apache.kafka.clients.producer.Callback</code> and it must be accessible on the CLASSPATH.                                                                                                                                                                                                                                                                                                                                                    | *(none)*                                               |

