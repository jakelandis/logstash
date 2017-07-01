package org.logstash.instrument.metrics.gauge;

import java.util.List;

/**
 * Created by jake on 6/30/17.
 */
public class DoubleGauge extends GaugeMetric<Double> {
    private volatile double value;

    protected DoubleGauge(List<String> nameSpaces, String key, Double value) {
        super(nameSpaces, key);
        this.value = value;
    }

    @Override
    public Double get() {
        return value;
    }

    @Override
    public void set(Double value) {
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

}
