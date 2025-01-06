package com.segence.kafka.connect.kafka.validator;

import java.util.Arrays;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

abstract class SubClassValidator implements ConfigDef.Validator {

    private static final String NOT_A_CLASS_MESSAGE = "Not a class";

    protected abstract String getRequiredInterface();

    @Override
    public void ensureValid(String name, Object value) {

        if (value == null) {
            throw new ConfigException(name, null, "Null value not allowed");
        }

        if (!(value instanceof Class<?> clazz)) {
            throw new ConfigException(name, value, NOT_A_CLASS_MESSAGE);
        }

        final var requiredInterface = getRequiredInterface();
        final var notImplementingMessage = NOT_A_CLASS_MESSAGE + " implementing " + requiredInterface;

        final var interfaces = clazz.getInterfaces();

        if (interfaces.length < 1) {
            throw new ConfigException(name, value, notImplementingMessage);
        }

        if (
            Arrays.stream(interfaces).noneMatch(entry -> entry.getCanonicalName().equals(requiredInterface))
        ) {
            throw new ConfigException(name, value, notImplementingMessage);
        }
    }

    public String toString() {
        return "Kafka Producer callback validator";
    }
}
