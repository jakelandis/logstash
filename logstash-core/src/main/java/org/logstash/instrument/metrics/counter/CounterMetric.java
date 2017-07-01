package org.logstash.instrument.metrics.counter;


import org.logstash.instrument.metrics.BaseMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public class CounterMetric extends BaseMetric<Long> {

    private final LongAdder longAdder;

    public CounterMetric(List<String> nameSpace, String key, long initialValue) {
        super(nameSpace, key);
        longAdder = new LongAdder();
        longAdder.add(initialValue);
    }

    @Override
    public void increment() {
        increment(1);
    }

    @Override
    public void increment(long by) {
        //TODO: validate by > 0
        longAdder.add(by);
    }


    @Override
    public Long getValue() {
        return longAdder.longValue();
    }

    @Override
    public void set(Long value) {
        longAdder.reset();
        longAdder.add(value);
    }

    @Override
    public String type() {
        return MetricType.COUNTER.asString();
    }
}
