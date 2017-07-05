package org.logstash.instrument.metrics.counter;

import org.junit.Test;
import org.logstash.instrument.metrics.MetricType;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

/**
 * Unit tests for {@link LazyDelegatingCounter}
 */
public class LazyDelegatingCounterTest {

    private final double INITIAL_DOUBLE_VALUE = 99.0;
    private final long INITIAL_LONG_VALUE = 99l;

    @Test
    public void increment()  {
        //Long
        LazyDelegatingCounter lazyCounter = new LazyDelegatingCounter(Collections.singletonList("foo"), "bar", INITIAL_LONG_VALUE);
        lazyCounter.increment();
        assertThat(lazyCounter.getValue()).isEqualTo(INITIAL_LONG_VALUE + 1l);
        //Double
        lazyCounter = new LazyDelegatingCounter(Collections.singletonList("foo"), "bar", INITIAL_DOUBLE_VALUE);
        lazyCounter.increment();
        assertThat(lazyCounter.getValue()).isEqualTo(INITIAL_DOUBLE_VALUE + 1.0);
        //Null
        LazyDelegatingCounter lazyCounter2 = new LazyDelegatingCounter(Collections.singletonList("foo"), "bar", null);
        Throwable thrown = catchThrowable(() -> {
            lazyCounter2.increment();
        });
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
        assertThat(lazyCounter2.getValue()).isNull();

    }

    @Test
    public void incrementBy() {
        //Long
        LazyDelegatingCounter lazyCounter = new LazyDelegatingCounter(Collections.singletonList("foo"), "bar", INITIAL_LONG_VALUE);
        lazyCounter.increment(2l);
        assertThat(lazyCounter.getValue()).isEqualTo(INITIAL_LONG_VALUE + 2l);
        //Double
        lazyCounter = new LazyDelegatingCounter(Collections.singletonList("foo"), "bar", INITIAL_DOUBLE_VALUE);
        lazyCounter.increment(0.1);
        assertThat(lazyCounter.getValue()).isEqualTo(INITIAL_DOUBLE_VALUE + 0.1);
        //Null - Then Long
        lazyCounter = new LazyDelegatingCounter(Collections.singletonList("foo"), "bar", null);
        lazyCounter.increment(1l);
        assertThat(lazyCounter.getValue()).isEqualTo(1l);
        //Null - Then Double
        lazyCounter = new LazyDelegatingCounter(Collections.singletonList("foo"), "bar", null);
        lazyCounter.increment(0.1);
        assertThat(lazyCounter.getValue()).isEqualTo(0.1);
    }

    @Test
    public void initialize() {
        //Long
        LazyDelegatingCounter lazyCounter = new LazyDelegatingCounter(Collections.singletonList("foo"), "bar", INITIAL_LONG_VALUE);
        assertThat(lazyCounter.getValue()).isEqualTo(INITIAL_LONG_VALUE);
        assertThat(lazyCounter.getType()).isEqualTo(MetricType.COUNTER_LONG);
        //Double
        lazyCounter = new LazyDelegatingCounter(Collections.singletonList("foo"), "bar", INITIAL_DOUBLE_VALUE);
        assertThat(lazyCounter.getValue()).isEqualTo(INITIAL_DOUBLE_VALUE);
        assertThat(lazyCounter.getType()).isEqualTo(MetricType.COUNTER_DOUBLE);
        //Null
        lazyCounter = new LazyDelegatingCounter(Collections.singletonList("foo"), "bar", null);
        assertThat(lazyCounter.getValue()).isNull();
        assertThat(lazyCounter.getType()).isNull();

        //Unsupported
        Throwable thrown = catchThrowable(() -> {
            new LazyDelegatingCounter(Collections.singletonList("foo"), "bar", 2.0f);
        });
        assertThat(thrown).isInstanceOf(IllegalStateException.class);

    }

}