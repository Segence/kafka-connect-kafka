package com.segence.kafka.connect.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

import static com.segence.kafka.connect.kafka.ConnectorConfiguration.getProducerProperties;

public class KafkaSinkTask extends SinkTask {

    private static final Logger log = LoggerFactory.getLogger(KafkaSinkTask.class);

    private KafkaProducer<Object, Object> producer;
    private String topic;
    private boolean exactlyOneSupport;

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> configuration) {

        producer = new KafkaProducer<>(getProducerProperties(configuration));

        topic = configuration.get(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName());

        if (configuration.containsKey(ConnectorConfigurationEntry.EXACTLY_ONCE_SUPPORT.getConfigKeyName())
            && configuration.get(ConnectorConfigurationEntry.EXACTLY_ONCE_SUPPORT.getConfigKeyName()).equals("true")) {
            exactlyOneSupport = true;
        }

        log.info("Successfully started Kafka Sink Task");
    }

    @Override
    public void put(Collection<SinkRecord> collection) {

        log.debug("Received " + collection.size() + " records");

        if (exactlyOneSupport) {
            try {
                producer.beginTransaction();
                collection.forEach(record -> {
                    var producerRecord = new ProducerRecord<>(topic, record.key(), record.value());
                    producer.send(producerRecord);
                });
                producer.commitTransaction();
            } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
                // We can't recover from these exceptions, so our only option is to close the producer and exit.
                stop();
                throw e;
            } catch (KafkaException e) {
                // For all other exceptions, just abort the transaction and try again.
                producer.abortTransaction();
            }
        }
        else {
            collection.forEach(record -> {
                var producerRecord = new ProducerRecord<>(topic, record.key(), record.value());
                producer.send(producerRecord); // FIXME support Future or callback
            });
        }
    }

    @Override
    public void stop() {
        log.info("Closing Kafka producer");
        producer.close();
        log.info("Stopping Kafka Sink Task");
    }
}
