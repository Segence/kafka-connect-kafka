package com.segence.kafka.connect.kafka;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.segence.kafka.connect.kafka.callback.NoOpCallback;
import com.segence.kafka.connect.kafka.stubs.EnhancedKafkaSinkTask;

class KafkaSinkTaskTest {

    private static final EnhancedKafkaSinkTask UNDER_TEST = new EnhancedKafkaSinkTask();

    // CHECKSTYLE:OFF: checkstyle: NeedBraces
    private static final Map<String, String> CONFIGURATION_HAVING_BOOTSTRAP_SERVERS_AND_TOPIC = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
    }};

    private static final Map<String, String> CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_OFF = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.EXACTLY_ONCE_SUPPORT.getConfigKeyName(), "false");
    }};

    private static final Map<String, String> CONFIGURATION_HAVING_EXACTLY_ONCE_SUPPORT_TURNED_ON = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.EXACTLY_ONCE_SUPPORT.getConfigKeyName(), "true");
    }};

    private static final Map<String, String> CONFIGURATION_HAVING_NO_OP_CALLBACK = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.CALLBACK_CLASS.getConfigKeyName(), NoOpCallback.CLAZZ);
    }};

    private static final Map<String, String> CONFIGURATION_HAVING_NON_EXISTENT_CALLBACK = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.CALLBACK_CLASS.getConfigKeyName(),
            "com.segence.kafka.connect.kafka.stubs.NonExistent");
    }};

    private static final Map<String, String> CONFIGURATION_HAVING_VALID_CALLBACK = new HashMap<>() {{
        put(ConnectorConfigurationEntry.BOOTSTRAP_SERVERS.getConfigKeyName(), "localhost:9092");
        put(ConnectorConfigurationEntry.SINK_TOPIC.getConfigKeyName(), "test-topic");
        put(ConnectorConfigurationEntry.CALLBACK_CLASS.getConfigKeyName(),
            "com.segence.kafka.connect.kafka.stubs.TestCallback");
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
        assertThat(UNDER_TEST.getTopic(), is(equalTo("test-topic")));
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
}
