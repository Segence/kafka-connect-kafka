package com.segence.kafka.connect.kafka;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

class ConnectorConfiguration extends AbstractConfig {

    static final ConfigDef CONFIG_DEFINITIONS =
        Arrays.stream(ConnectorConfigurationEntry.values()).reduce(
            new ConfigDef(),
            (configDef, configEntry) -> {
                if (configEntry.getDefaultValue().isPresent()) {
                    if (configEntry.getValidator().isPresent()) {
                        return configDef.define(configEntry.getConfigKeyName(),
                            configEntry.getConfigType(),
                            configEntry.getDefaultValue().get(),
                            configEntry.getValidator().get(),
                            configEntry.getImportance(),
                            configEntry.getDescription());
                    } else {
                        return configDef.define(configEntry.getConfigKeyName(),
                            configEntry.getConfigType(),
                            configEntry.getDefaultValue().get(),
                            configEntry.getImportance(),
                            configEntry.getDescription());
                    }
                }
                return configDef.define(configEntry.getConfigKeyName(),
                    configEntry.getConfigType(),
                    configEntry.getImportance(),
                    configEntry.getDescription());
            },
            (a, b) -> b
        );

    ConnectorConfiguration(ConfigDef definition, Map<?, ?> originals, Map<String, ?> configProviderProps, boolean doLog) {
        super(definition, originals, configProviderProps, doLog);
    }

    static Properties getProducerProperties(Map<String, String> configuration) {
        var undefinedHighImportanceConfigurationEntries = Arrays.stream(ConnectorConfigurationEntry.values()).filter(entry ->
            entry.getImportance() == ConfigDef.Importance.HIGH && entry.getDefaultValue().isEmpty()
        ).map(
            ConnectorConfigurationEntry::getConfigKeyName
        ).filter(configKeyName ->
            !configuration.containsKey(configKeyName)
        ).collect(Collectors.toSet());

        if (!undefinedHighImportanceConfigurationEntries.isEmpty()) {
            throw new IllegalArgumentException("No config entry found for: " + String.join(",", undefinedHighImportanceConfigurationEntries));
        }

        var result = new Properties();

        var producerConfigurationEntries = Arrays.stream(
            ConnectorConfigurationEntry.values()
        ).collect(Collectors.toMap(
            ConnectorConfigurationEntry::getConfigKeyName,
            entry -> entry
        ));

        for (var configurationEntry : configuration.entrySet()) {
            if (
                !Set.of(
                    ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(),
                    ConnectorConfigurationEntry.EXACTLY_ONCE_SUPPORT.getConfigKeyName()
                ).contains(configurationEntry.getKey())
            ) {
                if (producerConfigurationEntries.containsKey(configurationEntry.getKey())) {
                    result.put(configurationEntry.getKey().substring(5), configurationEntry.getValue());
                } else {
                    result.put(configurationEntry.getKey(), configurationEntry.getValue());
                }
            }
        }

        return result;
    }
}
