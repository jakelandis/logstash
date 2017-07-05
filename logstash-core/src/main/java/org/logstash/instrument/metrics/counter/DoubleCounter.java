package org.logstash.instrument.metrics.counter;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * A {@link CounterMetric} that is backed by a {@link Double} type.
 */
public class DoubleCounter extends AbstractMetric<Double> implements CounterMetric<Double> {

    private final DoubleAdder doubleAdder;

    /**
     * Constructor - initial value set to zero
     *
     * @param nameSpace    The namespace for this metric
     * @param key          The key <i>(with in the namespace)</i> for this metric
     */
    public DoubleCounter(List<String> nameSpace, String key) {
        this(nameSpace, key, 0.0);
    }

    /**
     * Constructor
     *
     * @param nameSpace    The namespace for this metric
     * @param key          The key <i>(with in the namespace)</i> for this metric
     * @param initialValue The initial value for this {@link CounterMetric}
     */
    public DoubleCounter(List<String> nameSpace, String key, double initialValue) {
        super(nameSpace, key);
        doubleAdder = new DoubleAdder();
        doubleAdder.add(initialValue);
    }

    @Override
    public MetricType getType() {
        return MetricType.COUNTER_DOUBLE;
    }

    @Override
    public Double getValue() {
        return doubleAdder.doubleValue();
    }

    @Override
    public void increment() {
        increment(1.0);
    }

    /**
     * {@inheritDoc}
     * throws {@link UnsupportedOperationException} if attempt is made to increment by a negative value
     */
    @Override
    public void increment(Double by) {
        if (by < 0) {
            throw new UnsupportedOperationException("Counters can not be incremented by negative values");
        }
        doubleAdder.add(by);

    }

}
