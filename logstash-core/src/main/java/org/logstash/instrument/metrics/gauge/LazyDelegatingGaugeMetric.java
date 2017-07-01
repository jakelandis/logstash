package org.logstash.instrument.metrics.gauge;

import org.jruby.RubyHash;
import org.logstash.instrument.metrics.IMetric;
import org.logstash.instrument.metrics.counter.CounterMetric;

import java.util.List;

/**
 *
 */
public class LazyDelegatingGaugeMetric implements IMetric<Object> {

    final String key;
    final List<String> nameSpaces;

    IMetric lazyMetric;


    protected LazyDelegatingGaugeMetric(final List<String> nameSpaces, final String key) {
        this.nameSpaces = nameSpaces;
        this.key = key;
    }


    @Override
    public Object getValue() {
        return lazyMetric == null ? null : lazyMetric.getValue();
    }

    @Override
    public Object get() {
        return lazyMetric == null ? null : lazyMetric.get();
    }

    @Override
    public void set(Object value) {
        if(value == null){
            //TODO: debug log
            return;
        }
        if (value instanceof Double) {
            lazyMetric = new DoubleGauge(nameSpaces, key, (Double) value);
        } else if (value instanceof String) {
            lazyMetric = new TextGuage(nameSpaces, key, (String) value);
        } else if (value instanceof Long) {
            lazyMetric = new CounterMetric(nameSpaces, key, (Long) value);
        } else if (value instanceof Boolean){
            lazyMetric = new BooleanGauge(nameSpaces, key, (Boolean) value);
        } else if (value instanceof RubyHash){
            lazyMetric = new RubyHashGauge(nameSpaces, key, (RubyHash) value);
        }    else {

            System.out.println("********** UNKOWN TYPE ************" + value.getClass().getCanonicalName());
            lazyMetric = new UnknownGauge(nameSpaces, key, value);
            //throw new IllegalStateException("Unsupported type of metric value: " + value.getClass().getCanonicalName());
        }
    }

    @Override
    public void increment() {
        validate();
        lazyMetric.increment();
    }

    @Override
    public void increment(long by) {
        validate();
        lazyMetric.increment(by);
    }

    @Override
    public void decrement() {

        lazyMetric.decrement();
    }

    @Override
    public void decrement(long by) {
        validate();
        lazyMetric.decrement(by);
    }

    private void validate() {
        if (lazyMetric == null) {
            throw new IllegalStateException("The set method MUST be called before any other operation.");
        }

    }
}
