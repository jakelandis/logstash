package org.logstash.instrument.metrics.gauge;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * Created by jake on 6/30/17.
 */
public class NumericGauge extends AbstractMetric<Number> implements GaugeMetric<Number> {
    private volatile Number value;

    protected NumericGauge(List<String> nameSpaces, String key, Number value) {
        super(nameSpaces, key);
        this.value = value;
    }

//TODO: implement increment and decrement

    @Override
    public Number get() {
        return value;
    }

    @Override
    public void set(Number value) {
        this.value = value;
    }

    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public String type() {
        return MetricType.GAUGE_NUMERIC.asString();
    }

}
