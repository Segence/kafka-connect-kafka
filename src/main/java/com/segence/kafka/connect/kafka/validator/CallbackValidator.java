package com.segence.kafka.connect.kafka.validator;

import org.apache.kafka.clients.producer.Callback;

public final class CallbackValidator extends SubClassValidator {

    public CallbackValidator() { }

    @Override
    protected String getRequiredInterface() {
        return Callback.class.getCanonicalName();
    }
}
