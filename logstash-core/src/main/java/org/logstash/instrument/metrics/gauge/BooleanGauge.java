package org.logstash.instrument.metrics.gauge;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * Created by jake on 6/30/17.
 */
public class BooleanGauge extends AbstractMetric<Boolean> implements GaugeMetric<Boolean> {

    private volatile Boolean value;

    public BooleanGauge(List<String> nameSpaces, String key, Boolean value) {
        super(nameSpaces, key);
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public MetricType getType() {
        return MetricType.GAUGE_BOOLEAN;
    }

    @Override
    public void set(Boolean value) {
        this.value = value;
    }


}
