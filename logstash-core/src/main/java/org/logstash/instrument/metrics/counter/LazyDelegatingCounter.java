package org.logstash.instrument.metrics.counter;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * A lazy proxy to a more specific typed {@link CounterMetric}. The metric will only be initialized once an {@code increment} operation is called.
 * <p><strong>Intended only for use with Ruby's duck typing, Java consumers should use the specific typed {@link CounterMetric}</strong></p>
 */
public class LazyDelegatingCounter extends AbstractMetric<Number> implements CounterMetric<Number> {

    private final String key;
    private final List<String> nameSpaces;
    private final Number initialValue;

    CounterMetric lazyMetric;

    /**
     * Constructor - protected so that Ruby may sub class proxy and discourage usage from Java
     * @param nameSpace The namespace for this metric
     * @param key       The key <i>(with in the namespace)</i> for this metric
     * @param initialValue The initial value for this {@link CounterMetric}
     */
    protected LazyDelegatingCounter(List<String> nameSpace, String key, Number initialValue) {
        super(nameSpace, key);
        this.nameSpaces = nameSpace;
        this.key = key;
        this.initialValue = initialValue;
    }

    @Override
    public Number getValue() {
        return lazyMetric == null ? null : (Number) lazyMetric.getValue();
    }

    @Override
    public MetricType getType() {
        return lazyMetric == null ? null : lazyMetric.getType();
    }

    @Override
    public void increment() {
        if (lazyMetric == null) {
            wakeMetric(initialValue);
        } else {
            lazyMetric.increment();
        }
    }

    @Override
    public void increment(Number by) {
        wakeMetric(by);
        lazyMetric.increment(by);
    }

    /**
     * Instantiates the metric based on the type used to initialize or increment the counter
     * @param by The object used to initialize or increment
     */
    private void wakeMetric(Object by) {
        if (lazyMetric == null) {
            //"quack quack"
            if (by instanceof Long) {
                lazyMetric =  new LongCounter(nameSpaces, key, (Long) by);
             } else if (by instanceof Double) {
                lazyMetric = new DoubleCounter(nameSpaces, key, (Double) by);
            } else {
                throw new IllegalStateException("Unsupported type for counter: " + by.getClass().getCanonicalName());
            }
            lazyMetric.increment(initialValue);
        }
    }

}
