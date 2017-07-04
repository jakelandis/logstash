package org.logstash.instrument.metrics.counter;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * Intended only for use with Ruby's duck typing, Java consumers use the specific typed {@link CounterMetric}
 */
public class LazyDelegatingCounter extends AbstractMetric<Object> implements CounterMetric<Object> {

    final String key;
    final List<String> nameSpaces;

    final Object initialValue;
    CounterMetric lazyMetric;

    protected LazyDelegatingCounter(List<String> nameSpaces, String key, Object initialValue) {
        super(nameSpaces, key);
        this.nameSpaces = nameSpaces;
        this.key = key;
        this.initialValue = initialValue;
    }

    @Override
    public Object getValue() {
        return lazyMetric == null ? null : lazyMetric.getValue();
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
    public void increment(Object by) {
        wakeMetric(by);
        lazyMetric.increment(by);
    }

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
            lazyMetric.increment(initialValue);
        }
    }

}
