package com.segence.kafka.connect.kafka.validator;

import org.apache.kafka.connect.storage.Converter;

public final class ConverterValidator extends SubClassValidator {

    public ConverterValidator() { }

    @Override
    protected String getRequiredInterface() {
        return Converter.class.getCanonicalName();
    }
}
