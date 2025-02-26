package com.segence.kafka.connect.kafka;

import java.util.Optional;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.connect.runtime.WorkerConfig;
import org.apache.kafka.connect.storage.StringConverter;
import org.apache.kafka.connect.transforms.util.NonEmptyListValidator;

import com.segence.kafka.connect.kafka.callback.NoOpCallback;
import com.segence.kafka.connect.kafka.validator.CallbackValidator;
import com.segence.kafka.connect.kafka.validator.ConverterValidator;

enum ConnectorConfigurationEntry {

    // CHECKSTYLE:OFF: checkstyle: LineLength
    BOOTSTRAP_SERVERS      ("sink." + ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,   Optional.empty(),                Type.LIST,    Optional.of(new NonEmptyListValidator()), Importance.HIGH,   "A list of host/port pairs used to establish the initial connection to the Kafka cluster. Clients use this list to bootstrap and discover the full set of Kafka brokers. While the order of servers in the list does not matter, we recommend including more than one server to ensure resilience if any servers are down. This list does not need to contain the entire set of brokers, as Kafka clients automatically manage and update connections to the cluster efficiently. This list must be in the form host1:port1,host2:port2,..."),
    SINK_TOPIC             ("sink.topic",                                        Optional.empty(),                Type.STRING,  Optional.empty(),                         Importance.HIGH,   "The sink topic name."),
    EXACTLY_ONCE_SUPPORT   ("sink.exactly.once.support",                         Optional.of(false),              Type.BOOLEAN, Optional.empty(),                         Importance.MEDIUM, "Whether to enable exactly-once support for source connectors in the cluster by using transactions to write source records and their source offsets, and by proactively fencing out old task generations before bringing up new ones."),
    CALLBACK_CLASS         ("sink.callback",                                     Optional.of(NoOpCallback.CLAZZ), Type.CLASS,   Optional.of(new CallbackValidator()),     Importance.LOW,    "The callback that is registered on the Kafka Producer. Must be a class implementing <code>org.apache.kafka.clients.producer.Callback</code> and it must be accessible on the CLASSPATH."),
    KEY_CONVERTER_CLASS    ("sink." + WorkerConfig.KEY_CONVERTER_CLASS_CONFIG,   Optional.of(StringConverter.class.getCanonicalName()),                Type.CLASS,   Optional.of(new ConverterValidator()),    Importance.HIGH,   WorkerConfig.KEY_CONVERTER_CLASS_DOC),
    VALUE_CONVERTER_CLASS  ("sink." + WorkerConfig.VALUE_CONVERTER_CLASS_CONFIG, Optional.of(StringConverter.class.getCanonicalName()),                Type.CLASS,   Optional.of(new ConverterValidator()),    Importance.HIGH,   WorkerConfig.VALUE_CONVERTER_CLASS_DOC);
    // CHECKSTYLE:ON: checkstyle: LineLength

    private final String configKeyName;
    private final Optional<Object> defaultValue;
    private final Type configType;
    private final Optional<ConfigDef.Validator> validator;
    private final Importance importance;
    private final String description;

    ConnectorConfigurationEntry(String configKeyName, Optional<Object> defaultValue, Type configType,
                                Optional<ConfigDef.Validator> validator, Importance importance, String description) {
        this.configKeyName = configKeyName;
        this.defaultValue = defaultValue;

        this.configType = configType;
        this.validator = validator;
        this.importance = importance;
        this.description = description;
    }

    public String getConfigKeyName() {
        return configKeyName;
    }

    public Optional<Object> getDefaultValue() {
        return defaultValue;
    }

    public Type getConfigType() {
        return configType;
    }

    public Optional<ConfigDef.Validator> getValidator() {
        return validator;
    }

    public Importance getImportance() {
        return importance;
    }

    public String getDescription() {
        return description;
    }
}
