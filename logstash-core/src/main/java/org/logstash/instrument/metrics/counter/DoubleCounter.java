package org.logstash.instrument.metrics.counter;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;

/**
 *TODO
 */
public class DoubleCounter extends AbstractMetric<Double> implements CounterMetric<Double> {

    private final DoubleAdder doubleAdder;
    protected DoubleCounter(List<String> nameSpaces, String key, double initialValue) {
        super(nameSpaces, key);
        doubleAdder = new DoubleAdder();
        doubleAdder.add(initialValue);
    }

    @Override
    public Double getValue() {
        return doubleAdder.doubleValue();
    }

    @Override
    public MetricType getType() {
        return MetricType.COUNTER_DOUBLE;
    }

    @Override
    public void increment() {
        increment(1.0);
    }

    @Override
    public void increment(Double by) {
//TODO: ensure only positive by value
        doubleAdder.add(by);

    }

}
