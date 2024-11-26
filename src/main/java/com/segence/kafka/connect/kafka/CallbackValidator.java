package com.segence.kafka.connect.kafka;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Arrays;

public class CallbackValidator implements ConfigDef.Validator {


    public CallbackValidator() {
    }

    @Override
    public void ensureValid(String name, Object value) {

        if (value == null) {
            throw new ConfigException(name, value, "Null value not allowed");
        }

        if (!(value instanceof Class<?> clazz)) {
            throw new ConfigException(name, value, "Not a class");
        }

        var interfaces = clazz.getInterfaces();

        if (interfaces.length < 1) {
            throw new ConfigException(name, value, "Not a class implementing org.apache.kafka.clients.producer.Callback");
        }

        if (
            Arrays.stream(interfaces).noneMatch(entry -> entry.getCanonicalName().equals("org.apache.kafka.clients.producer.Callback"))
        ) {
            throw new ConfigException(name, value, "Not a class implementing org.apache.kafka.clients.producer.Callback");
        }



    }

    public String toString() {
        return "Kafka Producer callback";
    }
}
