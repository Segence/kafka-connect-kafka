package com.segence.kafka.connect.kafka;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

class CallbackValidatorTest {

    private static final CallbackValidator UNDER_TEST = new CallbackValidator();

    @Test
    public void validationShouldFailWhenNullGiven() {
        assertThrows(
            ConfigException.class,
            () -> UNDER_TEST.ensureValid("", null),
            "Expecting ensureValid() to throw an exception"
        );
    }

    @Test
    public void validationShouldFailWhenNonStringGiven() {
        assertThrows(
            ConfigException.class,
            () -> UNDER_TEST.ensureValid("", 10),
            "Expecting ensureValid() to throw an exception"
        );
    }

    @Test
    public void validationShouldFailWhenNotClassNotFound() {
        assertThrows(
            ConfigException.class,
            () -> UNDER_TEST.ensureValid("", "com.segence.kafka.connect.kafka.stubs.NonExistentClass"),
            "Expecting ensureValid() to throw an exception"
        );
    }

    @Test
    public void validationShouldSucceedWhenClassFound() {
        UNDER_TEST.ensureValid("", com.segence.kafka.connect.kafka.stubs.TestCallback.class);
    }
}
