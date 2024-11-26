package com.segence.kafka.connect.kafka.callback;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * A Kafka Producer Callback that does nothing
 */
public class NoOpCallback implements Callback {

    public static final String CLAZZ = "com.segence.kafka.connect.kafka.callback.NoOpCallback";

    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        // No-op
    }
}
