package org.logstash.instrument.metrics.counter;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * A lazy proxy to a more specific typed {@link CounterMetric}. The metric will only be initialized if an initial value is provided, or once an {@code increment} operation is
 * called.
 * <p><strong>Intended only for use with Ruby's duck typing, Java consumers should use the specific typed {@link CounterMetric}</strong></p>
 */
public class LazyDelegatingCounter extends AbstractMetric<Number> implements CounterMetric<Number> {

    private final String key;
    private final List<String> nameSpaces;
    CounterMetric lazyMetric;

    /**
     * Constructor - protected so that Ruby may sub class proxy and discourage usage from Java
     *
     * @param nameSpace    The namespace for this metric
     * @param key          The key <i>(with in the namespace)</i> for this metric
     * @param initialValue The initial value for this {@link CounterMetric}, may be null
     */
    protected LazyDelegatingCounter(List<String> nameSpace, String key, Number initialValue) {
        super(nameSpace, key);
        this.nameSpaces = nameSpace;
        this.key = key;
        if (initialValue != null) {
            wakeMetric(initialValue);
        }
    }

    @Override
    public MetricType getType() {
        return lazyMetric == null ? null : lazyMetric.getType();
    }

    @Override
    public Number getValue() {
        return lazyMetric == null ? null : (Number) lazyMetric.getValue();
    }

    @Override
    public void increment() {
        if (lazyMetric == null) {
            throw new UnsupportedOperationException("Lazy metric requires increment by a specific value, or an initial value to infer the proper type to increment.");
        } else {
            lazyMetric.increment();
        }
    }

    @Override
    public void increment(Number by) {
        if (lazyMetric == null) {
            wakeMetric(by);
        } else {
            lazyMetric.increment(by);
        }
    }

    /**
     * Instantiates the metric based on the type used to initialize or increment the counter
     *
     * @param by The object used to initialize or increment
     */
    private void wakeMetric(Object by) {
        if (lazyMetric == null) {
            //"quack quack"
            if (by instanceof Long) {
                lazyMetric = new LongCounter(nameSpaces, key, (Long) by);
            } else if (by instanceof Double) {
                lazyMetric = new DoubleCounter(nameSpaces, key, (Double) by);
            } else {
                throw new IllegalStateException("Unsupported type for counter: " + by.getClass().getCanonicalName());
            }
        }
    }

}
