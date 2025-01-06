/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.segence.kafka.connect.kafka.schema;

// CHECKSTYLE:OFF: checkstyle:
import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;
import java.util.Optional;

@org.apache.avro.specific.AvroGenerated
public class TestMessageValue extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
    private static final long serialVersionUID = -521750054655081512L;


    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"TestMessageValue\",\"namespace\":\"com.segence.kafka.connect.kafka.schema\",\"fields\":[{\"name\":\"tenant\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"organization\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}");
    public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

    private static final SpecificData MODEL$ = new SpecificData();

    private static final BinaryMessageEncoder<TestMessageValue> ENCODER =
        new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<TestMessageValue> DECODER =
        new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<TestMessageValue> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<TestMessageValue> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<TestMessageValue> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Serializes this TestMessageValue to a ByteBuffer.
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /**
     * Deserializes a TestMessageValue from a ByteBuffer.
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a TestMessageValue instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static TestMessageValue fromByteBuffer(
        java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    private java.lang.String tenant;
    private java.lang.String organization;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public TestMessageValue() {}

    /**
     * All-args constructor.
     * @param tenant The new value for tenant
     * @param organization The new value for organization
     */
    public TestMessageValue(java.lang.String tenant, java.lang.String organization) {
        this.tenant = tenant;
        this.organization = organization;
    }

    @Override
    public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

    @Override
    public org.apache.avro.Schema getSchema() { return SCHEMA$; }

    // Used by DatumWriter.  Applications should not call.
    @Override
    public java.lang.Object get(int field$) {
        switch (field$) {
            case 0: return tenant;
            case 1: return organization;
            default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    // Used by DatumReader.  Applications should not call.
    @Override
    @SuppressWarnings(value="unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0: tenant = value$ != null ? value$.toString() : null; break;
            case 1: organization = value$ != null ? value$.toString() : null; break;
            default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    /**
     * Gets the value of the 'tenant' field.
     * @return The value of the 'tenant' field.
     */
    public java.lang.String getTenant() {
        return tenant;
    }


    /**
     * Sets the value of the 'tenant' field.
     * @param value the value to set.
     */
    public void setTenant(java.lang.String value) {
        this.tenant = value;
    }

    /**
     * Gets the value of the 'organization' field.
     * @return The value of the 'organization' field.
     */
    public java.lang.String getOrganization() {
        return organization;
    }


    /**
     * Sets the value of the 'organization' field.
     * @param value the value to set.
     */
    public void setOrganization(java.lang.String value) {
        this.organization = value;
    }

    /**
     * Creates a new TestMessageValue RecordBuilder.
     * @return A new TestMessageValue RecordBuilder
     */
    public static com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder newBuilder() {
        return new com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder();
    }

    /**
     * Creates a new TestMessageValue RecordBuilder by copying an existing Builder.
     * @param other The existing builder to copy.
     * @return A new TestMessageValue RecordBuilder
     */
    public static com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder newBuilder(com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder other) {
        if (other == null) {
            return new com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder();
        } else {
            return new com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder(other);
        }
    }

    /**
     * Creates a new TestMessageValue RecordBuilder by copying an existing TestMessageValue instance.
     * @param other The existing instance to copy.
     * @return A new TestMessageValue RecordBuilder
     */
    public static com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder newBuilder(com.segence.kafka.connect.kafka.schema.TestMessageValue other) {
        if (other == null) {
            return new com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder();
        } else {
            return new com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder(other);
        }
    }

    /**
     * RecordBuilder for TestMessageValue instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<TestMessageValue>
        implements org.apache.avro.data.RecordBuilder<TestMessageValue> {

        private java.lang.String tenant;
        private java.lang.String organization;

        /** Creates a new Builder */
        private Builder() {
            super(SCHEMA$, MODEL$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         * @param other The existing Builder to copy.
         */
        private Builder(com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.tenant)) {
                this.tenant = data().deepCopy(fields()[0].schema(), other.tenant);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.organization)) {
                this.organization = data().deepCopy(fields()[1].schema(), other.organization);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
        }

        /**
         * Creates a Builder by copying an existing TestMessageValue instance
         * @param other The existing instance to copy.
         */
        private Builder(com.segence.kafka.connect.kafka.schema.TestMessageValue other) {
            super(SCHEMA$, MODEL$);
            if (isValidValue(fields()[0], other.tenant)) {
                this.tenant = data().deepCopy(fields()[0].schema(), other.tenant);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.organization)) {
                this.organization = data().deepCopy(fields()[1].schema(), other.organization);
                fieldSetFlags()[1] = true;
            }
        }

        /**
         * Gets the value of the 'tenant' field.
         * @return The value.
         */
        public java.lang.String getTenant() {
            return tenant;
        }


        /**
         * Sets the value of the 'tenant' field.
         * @param value The value of 'tenant'.
         * @return This builder.
         */
        public com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder setTenant(java.lang.String value) {
            validate(fields()[0], value);
            this.tenant = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'tenant' field has been set.
         * @return True if the 'tenant' field has been set, false otherwise.
         */
        public boolean hasTenant() {
            return fieldSetFlags()[0];
        }


        /**
         * Clears the value of the 'tenant' field.
         * @return This builder.
         */
        public com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder clearTenant() {
            tenant = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'organization' field.
         * @return The value.
         */
        public java.lang.String getOrganization() {
            return organization;
        }


        /**
         * Sets the value of the 'organization' field.
         * @param value The value of 'organization'.
         * @return This builder.
         */
        public com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder setOrganization(java.lang.String value) {
            validate(fields()[1], value);
            this.organization = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'organization' field has been set.
         * @return True if the 'organization' field has been set, false otherwise.
         */
        public boolean hasOrganization() {
            return fieldSetFlags()[1];
        }


        /**
         * Clears the value of the 'organization' field.
         * @return This builder.
         */
        public com.segence.kafka.connect.kafka.schema.TestMessageValue.Builder clearOrganization() {
            organization = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public TestMessageValue build() {
            try {
                TestMessageValue record = new TestMessageValue();
                record.tenant = fieldSetFlags()[0] ? this.tenant : (java.lang.String) defaultValue(fields()[0]);
                record.organization = fieldSetFlags()[1] ? this.organization : (java.lang.String) defaultValue(fields()[1]);
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (java.lang.Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<TestMessageValue>
        WRITER$ = (org.apache.avro.io.DatumWriter<TestMessageValue>)MODEL$.createDatumWriter(SCHEMA$);

    @Override public void writeExternal(java.io.ObjectOutput out)
        throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<TestMessageValue>
        READER$ = (org.apache.avro.io.DatumReader<TestMessageValue>)MODEL$.createDatumReader(SCHEMA$);

    @Override public void readExternal(java.io.ObjectInput in)
        throws java.io.IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

    @Override protected boolean hasCustomCoders() { return true; }

    @Override public void customEncode(org.apache.avro.io.Encoder out)
        throws java.io.IOException
    {
        out.writeString(this.tenant);

        out.writeString(this.organization);

    }

    @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
        throws java.io.IOException
    {
        org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
        if (fieldOrder == null) {
            this.tenant = in.readString();

            this.organization = in.readString();

        } else {
            for (int i = 0; i < 2; i++) {
                switch (fieldOrder[i].pos()) {
                    case 0:
                        this.tenant = in.readString();
                        break;

                    case 1:
                        this.organization = in.readString();
                        break;

                    default:
                        throw new java.io.IOException("Corrupt ResolvingDecoder.");
                }
            }
        }
    }
}
// CHECKSTYLE:ON: checkstyle:
