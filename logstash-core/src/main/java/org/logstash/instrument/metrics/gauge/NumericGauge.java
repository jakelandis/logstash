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
     * @param name The name of this metric. This value may be used for display purposes.
     */
    public NumericGauge(String name) {
        this(name, null);
    }

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     * @param initialValue The initial value for this {@link GaugeMetric}, may be null
     */
    public NumericGauge(String name, Number initialValue) {
        super(name);
        this.value = initialValue;
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
