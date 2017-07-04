package org.logstash.instrument.metrics.gauge;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * Created by jake on 6/30/17.
 */
public class UnknownGauge extends AbstractMetric<Object> implements GaugeMetric<Object> {
    volatile Object value;
    public UnknownGauge(List<String> nameSpaces, String key, Object value) {
        super(nameSpaces, key);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public MetricType getType() {
        return MetricType.GAUGE_UNKNOWN;
    }

    @Override
    public void set(Object value) {
        this.value = value;
    }

}
