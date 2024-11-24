package com.segence.kafka.connect.kafka;

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
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.KEY_CONVERTER.getConfigKeyName(), "com.converter");
        put(ConnectorConfigurationEntry.VALUE_CONVERTER.getConfigKeyName(), "com.converter");
//        put(ConnectorConfigurationEntry.KEY_SERIALIZER_CLASS.getConfigKeyName(), "com.converter");
//        put(ConnectorConfigurationEntry.VALUE_SERIALIZER_CLASS.getConfigKeyName(), "com.converter");
//        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVER.getConfigKeyName(), "localhost:9092");
//        put(ConnectorConfigurationEntry.SSL_TRUSTSTORE_PASSWORD.getConfigKeyName(), "some password");
    }};

    private static final Map<String, String> CUSTOM_CONFIGURATION = new HashMap<>(VALID_HIGH_PRIORITY_CONFIGURATION){{
        put("custom.entry", "custom value");
    }};

    private static final Properties EXPECTED_HIGH_PRIORITY_CONFIGURATION = new Properties(){{
        put("key.converter", "com.converter");
        put("value.converter", "com.converter");
        put("key.serializer", "com.converter");
        put("value.serializer", "com.converter");
        put("bootstrap.servers", "localhost:9092");
        put("ssl.truststore.password", "some password");
    }};

    private static final Properties EXPECTED_CUSTOM_CONFIGURATION = new Properties(){{
        putAll(EXPECTED_HIGH_PRIORITY_CONFIGURATION);
        put("custom.entry", "custom value");
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
