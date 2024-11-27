package com.segence.kafka.connect.kafka.stubs;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;

import com.segence.kafka.connect.kafka.KafkaSinkTask;

public final class EnhancedKafkaSinkTask extends KafkaSinkTask {

    @Override
    protected void initProducer(Properties producerProperties) {

    }

    public String getTopic() {
        return super.getTopic();
    }

    public boolean isExactlyOnceSupport() {
        return super.isExactlyOnceSupport();
    }

    public Callback getCallback() {
        return super.getCallback();
    }
}
