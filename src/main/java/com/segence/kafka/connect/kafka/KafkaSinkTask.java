package com.segence.kafka.connect.kafka;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.segence.kafka.connect.kafka.callback.NoOpCallback;

public class KafkaSinkTask extends SinkTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSinkTask.class);

    private KafkaProducer<Object, Object> producer;
    private String topic;
    private boolean exactlyOnceSupport;
    private Callback callback;

    /**
     * Returns the Kafka topic set
     *
     * @return Kafkat topic name
     */
    protected String getTopic() {
        return topic;
    }

    /**
     * Whether exactly once delivery (transactions) is used
     *
     * @return A boolean indicating whether exactly once delivery is used
     */
    protected boolean isExactlyOnceSupport() {
        return exactlyOnceSupport;
    }

    /**
     * The Kafka Producer callback instance registered
     *
     * @return An instance of {@link org.apache.kafka.clients.producer.Callback} or null
     */
    protected Callback getCallback() {
        return callback;
    }

    /**
     * Sets the Kafka Producer
     *
     * @param kafkaProducer The Kafka Producer instance
     */
    protected void setProducer(KafkaProducer<Object, Object> kafkaProducer) {
        producer = kafkaProducer;
    }

    @Override
    public String version() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public void start(Map<String, String> configuration) {

        if (!configuration.containsKey(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName())) {
            throw new IllegalArgumentException("No sink topic configured");
        }

        topic = configuration.get(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName());

        if (configuration.containsKey(ConnectorConfigurationEntry.EXACTLY_ONCE_SUPPORT.getConfigKeyName())
            && configuration.get(ConnectorConfigurationEntry.EXACTLY_ONCE_SUPPORT.getConfigKeyName()).equals("true")) {
            exactlyOnceSupport = true;
        }

        if (
            configuration.containsKey(ConnectorConfigurationEntry.CALLBACK_CLASS.getConfigKeyName())
            && !configuration.get(
                    ConnectorConfigurationEntry.CALLBACK_CLASS.getConfigKeyName()
                ).equals(NoOpCallback.CLAZZ)
        ) {

            try {
                final var clazz = getClass().getClassLoader().loadClass(
                    configuration.get(ConnectorConfigurationEntry.CALLBACK_CLASS.getConfigKeyName())
                );
                callback = (Callback) clazz.getDeclaredConstructor().newInstance();
                LOGGER.info("Instantiated Callback class: {}", clazz);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
                     | InstantiationException | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            LOGGER.info("No callback class registered");
        }

        final var producerProperties = ConnectorConfiguration.getProducerProperties(configuration);

        if (exactlyOnceSupport) {
            final var transactionalId = "kafka-sink-" + UUID.randomUUID();
            producerProperties.setProperty(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId);
            LOGGER.info("Using producer with transactional id {}", transactionalId);
        }

        initProducer(producerProperties);
    }

    /**
     * Initialization of the Kafka Producer.
     * Useful to override this method for testing.
     *
     * @param producerProperties An instance of {@link java.util.Properties}
     */
    protected void initProducer(Properties producerProperties) {
        setProducer(new KafkaProducer<>(producerProperties));

        if (exactlyOnceSupport) {
            producer.initTransactions();
        }

        LOGGER.info("Successfully started Kafka Sink Task");
    }

    @Override
    public void put(Collection<SinkRecord> collection) {

        LOGGER.debug("Received {} records", collection.size());

        if (exactlyOnceSupport) {
            try {
                producer.beginTransaction();
                collection.forEach(record -> {
                    final var producerRecord = new ProducerRecord<>(topic, record.key(), record.value());
                    producer.send(producerRecord, callback);
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
        } else {
            collection.forEach(record -> {
                final var producerRecord = new ProducerRecord<>(topic, record.key(), record.value());
                producer.send(producerRecord, callback);
            });
        }
    }

    @Override
    public void stop() {
        LOGGER.info("Closing Kafka producer");
        producer.close();
        LOGGER.info("Stopping Kafka Sink Task");
    }
}
