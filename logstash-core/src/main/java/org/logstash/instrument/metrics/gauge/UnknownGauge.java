package org.logstash.instrument.metrics.gauge;

import java.util.List;

/**
 * Created by jake on 6/30/17.
 */
public class UnknownGauge extends GaugeMetric<Object> {
    volatile Object value;
    protected UnknownGauge(List<String> nameSpaces, String key, Object value) {
        super(nameSpaces, key);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }


    @Override
    public void set(Object value) {
        this.value = value;
    }
}
