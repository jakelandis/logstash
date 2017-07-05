package org.logstash.instrument.metrics.gauge;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * A {@link GaugeMetric} that is backed by a {@link Number}
 */
public class NumericGauge extends AbstractMetric<Number> implements GaugeMetric<Number> {

    private volatile Number value;

    /**
     * Constructor
     *
     * @param nameSpace    The namespace for this metric
     * @param key          The key <i>(with in the namespace)</i> for this metric
     * @param initialValue The initial value for this {@link GaugeMetric}
     */
    public NumericGauge(List<String> nameSpace, String key, Number initialValue) {
        super(nameSpace, key);
        this.value = initialValue;
    }

    @Override
    public Number get() {
        return value;
    }

    @Override
    public MetricType getType() {
        return MetricType.GAUGE_NUMERIC;
    }

    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public void set(Number value) {
        this.value = value;
    }

}
