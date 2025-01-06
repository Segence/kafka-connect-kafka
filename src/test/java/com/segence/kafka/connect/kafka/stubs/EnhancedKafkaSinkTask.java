package com.segence.kafka.connect.kafka.stubs;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;

import com.segence.kafka.connect.kafka.KafkaSinkTask;

public final class EnhancedKafkaSinkTask extends KafkaSinkTask {

    private final KafkaProducer<byte[], byte[]> testProducer;

    public EnhancedKafkaSinkTask(KafkaProducer<byte[], byte[]> kafkaProducer) {
        this.testProducer = kafkaProducer;
    }

    @Override
    protected void initProducer(Properties producerProperties) {
        super.setProducer(testProducer);
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
