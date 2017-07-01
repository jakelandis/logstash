package org.logstash.instrument.metrics.counter;


import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public class LongCounter extends AbstractMetric<Long> implements CounterMetric<Long> {

    private final LongAdder longAdder;

    public LongCounter(List<String> nameSpace, String key, long initialValue) {
        super(nameSpace, key);
        longAdder = new LongAdder();
        longAdder.add(initialValue);
    }

    @Override
    public void increment() {
        increment(1l);
    }

    @Override
    public void increment(Long by) {
        //TODO: ensure only positive by value
        longAdder.add(by);
    }


    @Override
    public Long getValue() {
        return longAdder.longValue();
    }


    @Override
    public String type() {
        return MetricType.COUNTER_LONG.asString();
    }
}
