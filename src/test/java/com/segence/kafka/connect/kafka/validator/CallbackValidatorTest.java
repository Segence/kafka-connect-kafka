package com.segence.kafka.connect.kafka.validator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
    public void validationShouldFailWhenClassImplementsNoInterfaces() {
        assertThrows(
            ConfigException.class,
            () -> UNDER_TEST.ensureValid("", com.segence.kafka.connect.kafka.stubs.NoInterface.class),
            "Expecting ensureValid() to throw an exception"
        );
    }

    @Test
    public void validationShouldFailWhenClassImplementsInvalidInterface() {
        assertThrows(
            ConfigException.class,
            () -> UNDER_TEST.ensureValid("", com.segence.kafka.connect.kafka.stubs.InvalidInterface.class),
            "Expecting ensureValid() to throw an exception"
        );
    }

    @Test
    public void validationShouldSucceedWhenClassFound() {
        UNDER_TEST.ensureValid("", com.segence.kafka.connect.kafka.stubs.TestCallback.class);
    }

    @Test
    public void stringRepresentationShouldBeCorrect() {
        assertThat(UNDER_TEST.toString(), is(equalTo("Kafka Producer callback validator")));
    }
}
