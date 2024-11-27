package com.segence.kafka.connect.kafka;

import java.util.Arrays;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public final class CallbackValidator implements ConfigDef.Validator {

    private static final String REQUIRED_INTERFACE = Callback.class.getCanonicalName();
    private static final String NOT_A_CLASS_MESSAGE = "Not a class";
    private static final String NOT_IMPLEMENTING = NOT_A_CLASS_MESSAGE + " implementing " + REQUIRED_INTERFACE;

    public CallbackValidator() {
    }

    @Override
    public void ensureValid(String name, Object value) {

        if (value == null) {
            throw new ConfigException(name, null, "Null value not allowed");
        }

        if (!(value instanceof Class<?> clazz)) {
            throw new ConfigException(name, value, NOT_A_CLASS_MESSAGE);
        }

        final var interfaces = clazz.getInterfaces();

        if (interfaces.length < 1) {
            throw new ConfigException(name, value, NOT_IMPLEMENTING);
        }

        if (
            Arrays.stream(interfaces).noneMatch(entry -> entry.getCanonicalName().equals(REQUIRED_INTERFACE))
        ) {
            throw new ConfigException(name, value, NOT_IMPLEMENTING);
        }
    }

    public String toString() {
        return "Kafka Producer callback";
    }
}
