package org.logstash.instrument.metrics.counter;


import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * A {@link CounterMetric} that is backed by a {@link Long} type.
 */
public class LongCounter extends AbstractMetric<Long> implements CounterMetric<Long> {

    private LongAdder longAdder;
    private volatile boolean dirty;

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     */
    public LongCounter(String name) {
        super(name);
        longAdder = new LongAdder();
    }

    @Override
    public MetricType getType() {
        return MetricType.COUNTER_LONG;
    }

    @Override
    public Long getValue() {
        return longAdder.longValue();
    }

    @Override
    public void reset() {
        longAdder = new LongAdder();
        dirty = false;
    }

    @Override
    public void increment() {
        increment(1l);
    }

    /**
     * {@inheritDoc}
     * throws {@link UnsupportedOperationException} if attempt is made to increment by a negative value
     */
    @Override
    public void increment(Long by) {
        if (by < 0) {
            throw new UnsupportedOperationException("Counters can not be incremented by negative values");
        }
        longAdder.add(by);
        dirty = true;
    }

    /**
     * Optimized version of {@link #increment(Long)} to avoid auto-boxing.
     * throws {@link UnsupportedOperationException} if attempt is made to increment by a negative value
     */
    public void increment(long by) {
        if (by < 0) {
            throw new UnsupportedOperationException("Counters can not be incremented by negative values");
        }
        longAdder.add(by);
        dirty = true;
    }

}
