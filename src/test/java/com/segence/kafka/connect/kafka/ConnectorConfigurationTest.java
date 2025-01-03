package com.segence.kafka.connect.kafka;

import static com.segence.kafka.connect.kafka.ConnectorConfiguration.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class ConnectorConfigurationTest {

    // CHECKSTYLE:OFF: checkstyle: NeedBraces
    private static final Map<String, String> UNDEFINED_HIGH_PRIORITY_CONFIGURATION = new HashMap<>() {{
        put("", "");
    }};

    private static final Map<String, String> VALID_HIGH_PRIORITY_CONFIGURATION = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.KEY_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
        put(ConnectorConfigurationEntry.VALUE_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
    }};

    private static final Map<String, String> CONFIGURATION_WITH_INVALID_PREFIX =
        new HashMap<>(VALID_HIGH_PRIORITY_CONFIGURATION) {{
            put("short", "custom value 1");
        }};

    private static final Map<String, String> CUSTOM_CONFIGURATION = new HashMap<>(VALID_HIGH_PRIORITY_CONFIGURATION) {{
        put("invalid.custom.entry", "custom value 1");
        put("sink.custom.entry", "custom value 2");
    }};

    private static final Map<String, String> CONVERTER_CONFIGURATION = new HashMap<>(VALID_HIGH_PRIORITY_CONFIGURATION) {{
        put("sink.key.converter.custom.entry", "custom value 2");
        put("sink.value.converter.custom.entry", "custom value 3");
    }};

    private static final Map<String, String> ALL_CONFIGURATION = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.KEY_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
        put(ConnectorConfigurationEntry.VALUE_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
    }};

    private static final Properties EXPECTED_HIGH_PRIORITY_CONFIGURATION = new Properties() {{
        put("bootstrap.servers", "localhost:9092");
    }};

    private static final Properties EXPECTED_CUSTOM_CONFIGURATION = new Properties() {{
        putAll(EXPECTED_HIGH_PRIORITY_CONFIGURATION);
        put("custom.entry", "custom value 2");
    }};

    private static final Properties EXPECTED_ALL_CONFIGURATION = new Properties() {{
        put("bootstrap.servers", "localhost:9092");
    }};

    private static final Map<String, String> EXPECTED_KEY_CONVERTER_CONFIGURATION = new HashMap<>() {{
        put("custom.entry", "custom value 2");
    }};

    private static final Map<String, String> EXPECTED_VALUE_CONVERTER_CONFIGURATION = new HashMap<>() {{
        put("custom.entry", "custom value 3");
    }};

    // CHECKSTYLE:ON: checkstyle: NeedBraces

    @Test
    public void getProducerPropertiesShouldFailWhenHighPriorityConfigurationMissing() {
        assertThrows(
            IllegalArgumentException.class,
            () -> getProducerProperties(UNDEFINED_HIGH_PRIORITY_CONFIGURATION),
            "Expecting getProducerProperties() to throw an exception"
        );
    }

    @Test
    public void getProducerPropertiesShouldIgnoreConfigurationEntriesWithNotSupportedPrefix() {
        final var result = getProducerProperties(CONFIGURATION_WITH_INVALID_PREFIX);
        assertEquals(EXPECTED_HIGH_PRIORITY_CONFIGURATION, result);
    }

    @Test
    public void getProducerPropertiesShouldSucceedWhenHighPriorityConfigurationSet() {
        final var result = getProducerProperties(VALID_HIGH_PRIORITY_CONFIGURATION);
        assertEquals(EXPECTED_HIGH_PRIORITY_CONFIGURATION, result);
    }

    @Test
    public void getProducerPropertiesShouldSupportCustomConfiguration() {
        final var result = getProducerProperties(CUSTOM_CONFIGURATION);
        assertEquals(EXPECTED_CUSTOM_CONFIGURATION, result);
    }

    @Test
    public void getProducerPropertiesShouldNotAddProducerConfigurationWhenAllDefined() {
        final var result = getProducerProperties(ALL_CONFIGURATION);
        assertEquals(EXPECTED_ALL_CONFIGURATION, result);
    }

    @Test
    public void getProducerPropertiesShouldNotAddKeyAndValueConverterProperties() {
        final var result = getProducerProperties(CONVERTER_CONFIGURATION);
        assertEquals(EXPECTED_ALL_CONFIGURATION, result);
    }

    @Test
    public void getKeyConverterPropertiesShouldOnlyReturnValidEntries() {
        final var result = getKeyConverterProperties(CONVERTER_CONFIGURATION);
        assertEquals(EXPECTED_KEY_CONVERTER_CONFIGURATION, result);
    }

    @Test
    public void getValueConverterPropertiesShouldOnlyReturnValidEntries() {
        final var result = getValueConverterProperties(CONVERTER_CONFIGURATION);
        assertEquals(EXPECTED_VALUE_CONVERTER_CONFIGURATION, result);
    }
}
