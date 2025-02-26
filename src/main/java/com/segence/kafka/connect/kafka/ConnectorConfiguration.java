package com.segence.kafka.connect.kafka;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

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
            (ignore, configDef) -> configDef
        );

    static final Set<String> NON_KAFKA_PRODUCER_CONFIGURATION = Set.of(
        ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(),
        ConnectorConfigurationEntry.EXACTLY_ONCE_SUPPORT.getConfigKeyName(),
        ConnectorConfigurationEntry.CALLBACK_CLASS.getConfigKeyName(),
        ConnectorConfigurationEntry.KEY_CONVERTER_CLASS.getConfigKeyName(),
        ConnectorConfigurationEntry.VALUE_CONVERTER_CLASS.getConfigKeyName()
    );

    static final String KEY_CONVERTER_PREFIX = ConnectorConfigurationEntry.KEY_CONVERTER_CLASS.getConfigKeyName();
    static final String VALUE_CONVERTER_PREFIX = ConnectorConfigurationEntry.VALUE_CONVERTER_CLASS.getConfigKeyName();

    private final static int CONFIGURATION_PREFIX_LENGTH = 5;

    ConnectorConfiguration(ConfigDef definition, Map<?, ?> originals,
                           Map<String, ?> configProviderProps, boolean doLog) {
        super(definition, originals, configProviderProps, doLog);
    }

    private static Map<String, String> getPropertiesHavingPrefix(Map<String, String> configuration, String prefix) {
        final Map<String, String> result = new HashMap<>();

        for (var configurationEntry : configuration.entrySet()) {
            if (
                !configurationEntry.getKey().equals(prefix)
                && configurationEntry.getKey().startsWith(prefix)
                && configurationEntry.getKey().length() > prefix.length() + 2
            ) {
                result.put(
                    configurationEntry.getKey().substring(prefix.length() + 1),
                    configurationEntry.getValue()
                );
            }
        }

        return result;
    }

    static Map<String, String> getKeyConverterProperties(Map<String, String> configuration) {
        return getPropertiesHavingPrefix(configuration, KEY_CONVERTER_PREFIX);
    }

    static Map<String, String> getValueConverterProperties(Map<String, String> configuration) {
        return getPropertiesHavingPrefix(configuration, VALUE_CONVERTER_PREFIX);
    }

    // CHECKSTYLE:OFF: checkstyle: NeedBraces
    static Properties getProducerProperties(Map<String, String> configuration) {
        final var undefinedHighImportanceConfigurationEntries =
            Arrays.stream(ConnectorConfigurationEntry.values()).filter(entry ->
                entry.getImportance() == ConfigDef.Importance.HIGH && entry.getDefaultValue().isEmpty()
            ).map(
                ConnectorConfigurationEntry::getConfigKeyName
            ).filter(configKeyName ->
                !configuration.containsKey(configKeyName)
            ).collect(Collectors.toSet());

        if (!undefinedHighImportanceConfigurationEntries.isEmpty()) {
            throw new IllegalArgumentException("No config entry found for: "
                + String.join(",", undefinedHighImportanceConfigurationEntries));
        }

        final var result = new Properties();

        final var producerConfigurationEntries = Arrays.stream(
            ConnectorConfigurationEntry.values()
        ).filter(entry ->
            !NON_KAFKA_PRODUCER_CONFIGURATION.contains(entry.getConfigKeyName())
        ).collect(Collectors.toMap(
            ConnectorConfigurationEntry::getConfigKeyName,
            entry -> entry
        ));

        for (var configurationEntry : configuration.entrySet()) {
            if (!NON_KAFKA_PRODUCER_CONFIGURATION.contains(configurationEntry.getKey())) {
                if (
                    configurationEntry.getKey().startsWith("sink.") &&
                    !configurationEntry.getKey().startsWith(KEY_CONVERTER_PREFIX) &&
                    !configurationEntry.getKey().startsWith(VALUE_CONVERTER_PREFIX)
                ) {
                    result.put(
                        configurationEntry.getKey().substring(CONFIGURATION_PREFIX_LENGTH),
                        configurationEntry.getValue()
                    );
                }
            }
        }

        for (var producerConfigurationEntry : producerConfigurationEntries.entrySet()) {
            if (!result.containsKey(producerConfigurationEntry.getKey().substring(CONFIGURATION_PREFIX_LENGTH))) {
                final var maybeDefaultValue = producerConfigurationEntry.getValue().getDefaultValue();
                maybeDefaultValue.ifPresent(defaultValue ->
                    result.put(producerConfigurationEntry.getKey().substring(CONFIGURATION_PREFIX_LENGTH), defaultValue)
                );
            }
        }

        return result;
    }
    // CHECKSTYLE:ON: checkstyle: NeedBraces
}
