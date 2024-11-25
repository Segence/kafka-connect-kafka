package com.segence.kafka.connect.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.segence.kafka.connect.kafka.ConnectorConfiguration.getProducerProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectorConfigurationTest {

    private static final Map<String, String> UNDEFINED_HIGH_PRIORITY_CONFIGURATION = new HashMap<>(){{
        put("", "");
    }};

    private static final Map<String, String> VALID_HIGH_PRIORITY_CONFIGURATION = new HashMap<>(){{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
    }};

    private static final Map<String, String> CUSTOM_CONFIGURATION = new HashMap<>(VALID_HIGH_PRIORITY_CONFIGURATION){{
        put("invalid.custom.entry", "custom value 1");
        put("sink.custom.entry", "custom value 2");
    }};

    private static final Properties EXPECTED_HIGH_PRIORITY_CONFIGURATION = new Properties(){{
        put("bootstrap.servers", "localhost:9092");
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    }};

    private static final Properties EXPECTED_CUSTOM_CONFIGURATION = new Properties(){{
        putAll(EXPECTED_HIGH_PRIORITY_CONFIGURATION);
        put("custom.entry", "custom value 2");
    }};


    @Test
    public void shouldFailWhenHighPriorityConfigurationMissing() {
        assertThrows(
            IllegalArgumentException.class,
            () -> getProducerProperties(UNDEFINED_HIGH_PRIORITY_CONFIGURATION),
            "Expecting getProducerProperties() to throw an exception"
        );
    }

    @Test
    public void shouldSucceedWhenHighPriorityConfigurationSet() {
        var result = getProducerProperties(VALID_HIGH_PRIORITY_CONFIGURATION);
        assertEquals(EXPECTED_HIGH_PRIORITY_CONFIGURATION, result);
    }


    @Test
    public void shouldSupportCustomConfiguration() {
        var result = getProducerProperties(CUSTOM_CONFIGURATION);
        assertEquals(EXPECTED_CUSTOM_CONFIGURATION, result);
    }
}
