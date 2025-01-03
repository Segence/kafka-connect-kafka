package com.segence.kafka.connect.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class KafkaSinkConnectorTest {

    private static final Map<String, String> EMPTY_MAP = new HashMap<>();

    private final KafkaSinkConnector underTest = new KafkaSinkConnector();

    @Test
    void shouldGetTaskClass() {
        var result = underTest.taskClass();
        assertEquals(KafkaSinkTask.class, result);
    }

    @Test
    void shouldGetEmptyTaskConfigurationsWhenStartNotCalled() {

        var expectation = new ArrayList<Map<String, String>>();
        expectation.add(null);
        expectation.add(null);

        var result = underTest.taskConfigs(2);

        assertEquals(expectation, result);
    }

    @Test
    void shouldGetTaskConfigurationsWhenStartCalled() {

        var expectation = List.of(EMPTY_MAP, EMPTY_MAP);

        underTest.start(EMPTY_MAP);
        var result = underTest.taskConfigs(2);

        assertEquals(expectation, result);
    }

    @Test
    void shouldGetConnectorConfigurations() {
        assertEquals(ConnectorConfiguration.CONFIG_DEFINITIONS, underTest.config());
    }
}
