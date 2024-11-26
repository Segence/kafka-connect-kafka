package com.segence.kafka.connect.kafka.callback;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Kafka Producer Callback that logs the result on completion
 */
public class LoggingCallback implements Callback {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingCallback.class);

    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {

        if (recordMetadata != null) {
            LOGGER.info("recordMetadata: {}", recordMetadata);
        }

        if (e != null) {
            LOGGER.error("Exception while producing message", e);
        }
    }
}
