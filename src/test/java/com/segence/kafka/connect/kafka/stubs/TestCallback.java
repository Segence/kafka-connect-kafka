package com.segence.kafka.connect.kafka.stubs;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

public final class TestCallback implements Callback {
    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {

    }
}
