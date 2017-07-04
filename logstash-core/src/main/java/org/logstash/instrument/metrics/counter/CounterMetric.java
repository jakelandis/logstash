package org.logstash.instrument.metrics.counter;

import org.logstash.instrument.metrics.Metric;

/**
 * Created by jake on 7/1/17.
 */
public interface CounterMetric<T> extends Metric<T> {

    void increment();

    void increment(T by) ;
}
