package com.segence.kafka.connect.kafka;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.segence.kafka.connect.kafka.callback.NoOpCallback;
import com.segence.kafka.connect.kafka.stubs.EnhancedKafkaSinkTask;

@ExtendWith(MockitoExtension.class)
class KafkaSinkTaskTest {

    @Mock
    private SinkRecord sinkRecord1;

    @Mock
    private SinkRecord sinkRecord2;

    @Mock
    private KafkaProducer<byte[], byte[]> mockProducer;

    @Captor
    private ArgumentCaptor<ProducerRecord<byte[], byte[]>> producerRecordCaptor;

    @Captor
    private ArgumentCaptor<Callback> callbackCaptor;

    private EnhancedKafkaSinkTask UNDER_TEST;

    @BeforeEach
    public void setup() {
        UNDER_TEST = new EnhancedKafkaSinkTask(mockProducer);
    }

    // CHECKSTYLE:OFF: checkstyle: NeedBraces
    private static final Map<String, String> CONFIGURATION_HAVING_BOOTSTRAP_SERVERS_AND_TOPIC = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.KEY_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
        put(ConnectorConfigurationEntry.VALUE_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
    }};

    private static final Map<String, String> CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_OFF = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.EXACTLY_ONCE_SUPPORT.getConfigKeyName(), "false");
        put(ConnectorConfigurationEntry.KEY_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
        put(ConnectorConfigurationEntry.VALUE_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
    }};

    private static final Map<String, String> CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_ON = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.EXACTLY_ONCE_SUPPORT.getConfigKeyName(), "true");
        put(ConnectorConfigurationEntry.KEY_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
        put(ConnectorConfigurationEntry.VALUE_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
    }};

    private static final Map<String, String> CONFIGURATION_HAVING_NO_OP_CALLBACK = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.CALLBACK_CLASS.getConfigKeyName(), NoOpCallback.CLAZZ);
        put(ConnectorConfigurationEntry.KEY_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
        put(ConnectorConfigurationEntry.VALUE_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
    }};

    private static final Map<String, String> CONFIGURATION_HAVING_NON_EXISTENT_CALLBACK = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.CALLBACK_CLASS.getConfigKeyName(),
            "com.segence.kafka.connect.kafka.stubs.NonExistent");
        put(ConnectorConfigurationEntry.KEY_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
        put(ConnectorConfigurationEntry.VALUE_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
    }};

    private static final Map<String, String> CONFIGURATION_HAVING_VALID_CALLBACK = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.CALLBACK_CLASS.getConfigKeyName(),
            "com.segence.kafka.connect.kafka.stubs.TestCallback");
        put(ConnectorConfigurationEntry.KEY_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
        put(ConnectorConfigurationEntry.VALUE_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
    }};

    private static final
        Map<String, String> CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_ON_AND_HAVING_VALID_CALLBACK =
            new HashMap<>() {{
                put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
                put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
                put(ConnectorConfigurationEntry.EXACTLY_ONCE_SUPPORT.getConfigKeyName(), "true");
                put(ConnectorConfigurationEntry.CALLBACK_CLASS.getConfigKeyName(),
                    "com.segence.kafka.connect.kafka.stubs.TestCallback");
                put(ConnectorConfigurationEntry.KEY_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
                put(ConnectorConfigurationEntry.VALUE_CONVERTER_CLASS.getConfigKeyName(), "org.apache.kafka.connect.storage.StringConverter");
            }};
    // CHECKSTYLE:ON: checkstyle: NeedBraces

    @Test
    public void shouldFailOnMissingTopic() {
        assertThrows(
            IllegalArgumentException.class,
            () -> UNDER_TEST.start(new HashMap<>()),
            "Expecting getProducerProperties() to throw an exception"
        );
    }

    @Test
    public void shouldSetTopic() {
        UNDER_TEST.start(CONFIGURATION_HAVING_BOOTSTRAP_SERVERS_AND_TOPIC);
        assertThat(UNDER_TEST.getTopic(), is("test-topic"));
    }

    @Test
    public void shouldNotSetExactlyOnceSupportWhenItIsSetToFalse() {
        UNDER_TEST.start(CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_OFF);
        assertThat(UNDER_TEST.isExactlyOnceSupport(), is(false));
    }

    @Test
    public void shouldSetExactlyOnceSupport() {
        UNDER_TEST.start(CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_ON);
        assertThat(UNDER_TEST.isExactlyOnceSupport(), is(true));
    }

    @Test
    public void shouldNotSetNoOpCallback() {
        UNDER_TEST.start(CONFIGURATION_HAVING_NO_OP_CALLBACK);
        assertThat(UNDER_TEST.getCallback(), is(nullValue()));
    }

    @Test
    public void shouldFailOnNonExistentCallback() {
        assertThrows(
            IllegalArgumentException.class,
            () -> UNDER_TEST.start(CONFIGURATION_HAVING_NON_EXISTENT_CALLBACK),
            "Expecting getProducerProperties() to throw an exception"
        );
    }

    @Test
    public void shouldSetValidCallback() {
        UNDER_TEST.start(CONFIGURATION_HAVING_VALID_CALLBACK);
        assertThat(UNDER_TEST.getCallback(), is(instanceOf(com.segence.kafka.connect.kafka.stubs.TestCallback.class)));
    }

    @Test
    public void shouldProduceNonTransactionalMessageAndNotUseCallbackWhenNotDefined() {

        when(sinkRecord1.key()).thenReturn("key1");
        when(sinkRecord1.value()).thenReturn("value1");

        when(sinkRecord2.key()).thenReturn("key2");
        when(sinkRecord2.value()).thenReturn("value2");

        UNDER_TEST.start(CONFIGURATION_HAVING_BOOTSTRAP_SERVERS_AND_TOPIC);
        UNDER_TEST.put(List.of(sinkRecord1, sinkRecord2));

        verify(mockProducer, times(2)).send(producerRecordCaptor.capture(), isNull());

        final var capturedProducerRecords = producerRecordCaptor.getAllValues();

        assertThat(capturedProducerRecords.size(), is(2));

        assertThat(capturedProducerRecords.get(0).key(), is("key1".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(0).value(), is("value1".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(1).key(), is("key2".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(1).value(), is("value2".getBytes(Charset.defaultCharset())));
    }

    @Test
    public void shouldProduceNonTransactionalMessageAndUseCallback() {

        when(sinkRecord1.key()).thenReturn("key1");
        when(sinkRecord1.value()).thenReturn("value1");

        when(sinkRecord2.key()).thenReturn("key2");
        when(sinkRecord2.value()).thenReturn("value2");

        UNDER_TEST.start(CONFIGURATION_HAVING_VALID_CALLBACK);
        UNDER_TEST.put(List.of(sinkRecord1, sinkRecord2));

        verify(mockProducer, times(2)).send(producerRecordCaptor.capture(), callbackCaptor.capture());

        final var capturedProducerRecords = producerRecordCaptor.getAllValues();
        final var capturedCallbacks = callbackCaptor.getAllValues();

        assertThat(capturedProducerRecords.size(), is(2));

        assertThat(capturedProducerRecords.get(0).key(), is("key1".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(0).value(), is("value1".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(1).key(), is("key2".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(1).value(), is("value2".getBytes(Charset.defaultCharset())));

        assertThat(capturedCallbacks.size(), is(2));

        assertThat(capturedCallbacks.get(0), is(instanceOf(com.segence.kafka.connect.kafka.stubs.TestCallback.class)));
        assertThat(capturedCallbacks.get(1), is(instanceOf(com.segence.kafka.connect.kafka.stubs.TestCallback.class)));
    }

    @Test
    public void shouldProduceTransactionalMessageAndNotUseCallbackWhenNotDefined() {

        when(sinkRecord1.key()).thenReturn("key1");
        when(sinkRecord1.value()).thenReturn("value1");

        when(sinkRecord2.key()).thenReturn("key2");
        when(sinkRecord2.value()).thenReturn("value2");

        UNDER_TEST.start(CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_ON);
        UNDER_TEST.put(List.of(sinkRecord1, sinkRecord2));

        verify(mockProducer, atMostOnce()).beginTransaction();
        verify(mockProducer, times(2)).send(producerRecordCaptor.capture(), isNull());
        verify(mockProducer, atMostOnce()).commitTransaction();

        final var capturedProducerRecords = producerRecordCaptor.getAllValues();

        assertThat(capturedProducerRecords.size(), is(2));

        assertThat(capturedProducerRecords.get(0).key(), is("key1".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(0).value(), is("value1".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(1).key(), is("key2".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(1).value(), is("value2".getBytes(Charset.defaultCharset())));
    }

    @Test
    public void shouldProduceTransactionalMessageAndUseCallback() {

        when(sinkRecord1.key()).thenReturn("key1");
        when(sinkRecord1.value()).thenReturn("value1");

        when(sinkRecord2.key()).thenReturn("key2");
        when(sinkRecord2.value()).thenReturn("value2");

        UNDER_TEST.start(CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_ON_AND_HAVING_VALID_CALLBACK);
        UNDER_TEST.put(List.of(sinkRecord1, sinkRecord2));

        verify(mockProducer, atMostOnce()).beginTransaction();
        verify(mockProducer, times(2)).send(producerRecordCaptor.capture(), callbackCaptor.capture());
        verify(mockProducer, atMostOnce()).commitTransaction();

        final var capturedProducerRecords = producerRecordCaptor.getAllValues();
        final var capturedCallbacks = callbackCaptor.getAllValues();

        assertThat(capturedProducerRecords.size(), is(2));

        assertThat(capturedProducerRecords.get(0).key(), is("key1".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(0).value(), is("value1".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(1).key(), is("key2".getBytes(Charset.defaultCharset())));
        assertThat(capturedProducerRecords.get(1).value(), is("value2".getBytes(Charset.defaultCharset())));

        assertThat(capturedCallbacks.size(), is(2));

        assertThat(capturedCallbacks.get(0), is(instanceOf(com.segence.kafka.connect.kafka.stubs.TestCallback.class)));
        assertThat(capturedCallbacks.get(1), is(instanceOf(com.segence.kafka.connect.kafka.stubs.TestCallback.class)));
    }

    @Test
    public void shouldAbortTransactionWhenProducingAMessageFails() {

        when(mockProducer.send(any(ProducerRecord.class), isNull())).thenThrow(KafkaException.class);

        UNDER_TEST.start(CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_ON);
        UNDER_TEST.put(List.of(sinkRecord1, sinkRecord2));

        verify(mockProducer, atMostOnce()).beginTransaction();
        verify(mockProducer, never()).commitTransaction();
        verify(mockProducer, atMostOnce()).abortTransaction();
        verify(mockProducer, never()).close();
    }

    @Test
    public void shouldAbortTransactionWhenBeginningTheTransactionFails() {

        doThrow(ProducerFencedException.class).when(mockProducer).beginTransaction();

        UNDER_TEST.start(CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_ON);

        assertThrows(
            ProducerFencedException.class,
            () -> UNDER_TEST.put(List.of(sinkRecord1, sinkRecord2)),
            "Expecting getProducerProperties() to throw an exception"
        );

        verify(mockProducer, never()).commitTransaction();
        verify(mockProducer, never()).abortTransaction();
        verify(mockProducer, atMostOnce()).close();
    }

    @Test
    public void shouldAbortTransactionWhenCommittingTheTransactionFails() {

        doThrow(ProducerFencedException.class).when(mockProducer).commitTransaction();

        UNDER_TEST.start(CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_ON);

        assertThrows(
            ProducerFencedException.class,
            () -> UNDER_TEST.put(List.of(sinkRecord1, sinkRecord2)),
            "Expecting getProducerProperties() to throw an exception"
        );

        verify(mockProducer, never()).abortTransaction();
        verify(mockProducer, atMostOnce()).close();
    }
}
