package org.logstash.instrument.witness;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.logstash.instrument.metrics.MetricType;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PluginWitness}
 */
public class PluginWitnessTest {

    private PluginWitness witness;

    @Before
    public void setup() {
        witness = new PluginWitness("123");
        assertThat(witness.snitch().id()).isEqualTo("123");
    }

    @Test
    public void testName() {
        assertThat(witness.name("abc")).isEqualTo(witness);
        assertThat(witness.snitch().name()).isEqualTo("abc");
    }

    @Test
    public void testCustomGauge() {
        witness.custom().gauge("a", "foo");
        witness.custom().gauge("b", 1);
        witness.custom().gauge("c", true);
        witness.custom().gauge("d", URI.create("unknown"));
        assertThat(witness.custom().snitch().gauges().size()).isEqualTo(4);
        assertThat(witness.custom().snitch().gauge("a").getValue()).isEqualTo("foo");
        assertThat(witness.custom().snitch().gauge("a").getType()).isEqualTo(MetricType.GAUGE_TEXT);
        assertThat(witness.custom().snitch().gauge("b").getValue()).isEqualTo(1);
        assertThat(witness.custom().snitch().gauge("b").getType()).isEqualTo(MetricType.GAUGE_NUMBER);
        assertThat(witness.custom().snitch().gauge("c").getValue()).isEqualTo(Boolean.TRUE);
        assertThat(witness.custom().snitch().gauge("c").getType()).isEqualTo(MetricType.GAUGE_BOOLEAN);
        assertThat(witness.custom().snitch().gauge("d").getValue()).isEqualTo(URI.create("unknown"));
        assertThat(witness.custom().snitch().gauge("d").getType()).isEqualTo(MetricType.GAUGE_UNKNOWN);
    }

    //TODO: Test counter

    @Test
    public void testEvents() {
        assertThat(witness.events()).isNotNull();
    }

    @Test
    public void testAsJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        assertThat(mapper.writeValueAsString(witness)).isEqualTo(witness.asJson());
    }

    @Test
    public void testSerializationEmpty() throws Exception {
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"id\":\"123\",\"events\":{\"duration_in_millis\":0,\"in\":0,\"out\":0,\"filtered\":0,\"queue_push_duration_in_millis\":0},\"name\":null}");
    }

    @Test
    public void testSerializationName() throws Exception {
        witness.name("abc");
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"id\":\"123\",\"events\":{\"duration_in_millis\":0,\"in\":0,\"out\":0,\"filtered\":0,\"queue_push_duration_in_millis\":0},\"name\":\"abc\"}");
    }

    @Test
    public void testSerializationEvents() throws Exception {
        witness.events().in();
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"id\":\"123\",\"events\":{\"duration_in_millis\":0,\"in\":1,\"out\":0,\"filtered\":0,\"queue_push_duration_in_millis\":0},\"name\":null}");
    }

    @Test
    public void testSerializationCustomGauge() throws Exception {
        witness.custom().gauge("a", "foo");
        witness.custom().gauge("b", 1);
        witness.custom().gauge("c", true);

        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"id\":\"123\",\"events\":{\"duration_in_millis\":0,\"in\":0,\"out\":0,\"filtered\":0,\"queue_push_duration_in_millis\":0},\"name\":null," +
                "\"a\":\"foo\",\"b\":1,\"c\":true}");
    }

    @Test
    public void testSerializationCustomGaugeNumericTypes() throws Exception {
        short a = 1;
        int b = 1;
        float c = 1;
        double d = 1;
        BigDecimal e = new BigDecimal(1);

        witness.custom().gauge("a", a);
        witness.custom().gauge("b", b);
        witness.custom().gauge("c", c);
        witness.custom().gauge("d", d);
        witness.custom().gauge("e", e);

        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"id\":\"123\",\"events\":{\"duration_in_millis\":0,\"in\":0,\"out\":0,\"filtered\":0,\"queue_push_duration_in_millis\":0},\"name\":null," +
                "\"a\":1,\"b\":1,\"c\":1.0,\"d\":1.0,\"e\":1}");
    }

    @Test(expected = IllegalStateException.class)
    public void testSerializationUnknownCustomGauge() throws Exception {
        //There are not default Jackson serializers for UUID
        witness.custom().gauge("a", UUID.randomUUID());
        witness.asJson();
    }
}