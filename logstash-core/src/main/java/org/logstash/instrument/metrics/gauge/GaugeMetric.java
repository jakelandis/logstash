package org.logstash.instrument.metrics.gauge;


import org.logstash.instrument.metrics.Metric;

public interface GaugeMetric<T> extends Metric<T> {


    void set(T value);

}
