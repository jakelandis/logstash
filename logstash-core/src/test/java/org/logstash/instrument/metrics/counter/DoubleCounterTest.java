package org.logstash.instrument.metrics.counter;

import org.junit.Before;
import org.junit.Test;
import org.logstash.instrument.metrics.MetricType;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link DoubleCounter}
 */
public class DoubleCounterTest {

    private final double INITIAL_VALUE = 99.0;
    private DoubleCounter doubleCounter;

    @Before
    public void _setup() {
        doubleCounter = new DoubleCounter(Collections.singletonList("foo"), "bar", INITIAL_VALUE);
    }

    @Test
    public void getValue() {
        assertThat(doubleCounter.getValue()).isEqualTo(INITIAL_VALUE);
    }

    @Test
    public void increment() {
        doubleCounter.increment();
        assertThat(doubleCounter.getValue()).isEqualTo(INITIAL_VALUE + 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void incrementByNegativeValue() {
        doubleCounter.increment(-.1);
        assertThat(doubleCounter.getValue()).isEqualTo(INITIAL_VALUE + 100);
    }

    @Test
    public void incrementByValue() {
        doubleCounter.increment(.1);
        assertThat(doubleCounter.getValue()).isEqualTo(INITIAL_VALUE + .1);
    }

    @Test
    public void noInitialValue() {
        DoubleCounter counter = new DoubleCounter(Collections.singletonList("foo"), "bar");
        counter.increment();
        assertThat(counter.getValue()).isEqualTo(1.0);
    }

    @Test
    public void type() {
        assertThat(doubleCounter.type()).isEqualTo(MetricType.COUNTER_DOUBLE.asString());
    }
}