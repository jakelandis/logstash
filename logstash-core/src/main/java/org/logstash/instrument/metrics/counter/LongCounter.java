package org.logstash.instrument.metrics.counter;


import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * A {@link CounterMetric} that is backed by a {@link Long} type.
 */
public class LongCounter extends AbstractMetric<Long> implements CounterMetric<Long> {

    private final LongAdder longAdder;

    /**
     * Constructor - initial value set to zero
     * @param nameSpace The namespace for this metric
     * @param key       The key <i>(with in the namespace)</i> for this metric
     */
    public LongCounter(List<String> nameSpace, String key) {
        this(nameSpace, key, 0l);
    }

    /**
     * Constructor
     * @param nameSpace The namespace for this metric
     * @param key       The key <i>(with in the namespace)</i> for this metric
     * @param initialValue The initial value for this {@link CounterMetric}
     */
    public LongCounter(List<String> nameSpace, String key, long initialValue) {
        super(nameSpace, key);
        longAdder = new LongAdder();
        longAdder.add(initialValue);
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
    }

}
