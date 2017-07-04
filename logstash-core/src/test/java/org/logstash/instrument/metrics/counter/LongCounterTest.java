package org.logstash.instrument.metrics.counter;

import org.junit.Before;
import org.junit.Test;
import org.logstash.instrument.metrics.MetricType;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit test for {@link LongCounter}
 */
public class LongCounterTest {

    private LongCounter longCounter;
    private final long INITIAL_VALUE = 99l;

    @Before
    public void setup() {
        longCounter = new LongCounter(Collections.singletonList("foo"), "bar", INITIAL_VALUE);
    }

    @Test
    public void getValue() throws Exception {
        assertThat(longCounter.getValue()).isEqualTo(INITIAL_VALUE);
    }

    @Test
    public void increment() throws Exception {

        longCounter.increment();
        assertThat(longCounter.getValue()).isEqualTo(INITIAL_VALUE + 1);
    }

    @Test
    public void incrementByValue() throws Exception {
        longCounter.increment(100l);
        assertThat(longCounter.getValue()).isEqualTo(INITIAL_VALUE + 100);
    }

    @Test
    public void type() throws Exception {
        assertThat(longCounter.type()).isEqualTo(MetricType.COUNTER_LONG.asString());
    }

}