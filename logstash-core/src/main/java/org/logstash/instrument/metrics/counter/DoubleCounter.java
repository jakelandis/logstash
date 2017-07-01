package org.logstash.instrument.metrics.counter;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 *TODO
 */
public class DoubleCounter extends AbstractMetric<Double> implements CounterMetric<Double> {

    protected DoubleCounter(List<String> nameSpaces, String key, double initialValue) {
        super(nameSpaces, key);
    }

    @Override
    public Double getValue() {
        return null;
    }

    @Override
    public String type() {
        return MetricType.COUNTER_DOUBLE.asString();
    }

    @Override
    public void increment() {
 System.out.println("******************** Double incrementor **********************");
    }

    @Override
    public void increment(Double by) {
 System.out.println("******************** Double incrementor BY **********************");

    }
}
