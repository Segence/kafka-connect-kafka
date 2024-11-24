package com.segence.kafka.connect.kafka;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KafkaSinkConnector extends SinkConnector {

    private static final Logger log = LoggerFactory.getLogger(KafkaSinkConnector.class);

    private Map<String, String> configuration;

    @Override
    public void start(Map<String, String> configuration) {
        this.configuration = configuration;
        log.info("Starting the Kafka Sink Connector");
    }

    @Override
    public Class<? extends Task> taskClass() {
        return KafkaSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return IntStream.range(0, maxTasks).mapToObj(task -> configuration).collect(Collectors.toList());
    }

    @Override
    public void stop() {
        log.info("Stopping the Kafka Sink Connector");
    }

    @Override
    public ConfigDef config() {
        return ConnectorConfiguration.CONFIG_DEFINITIONS;
    }

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }
}
