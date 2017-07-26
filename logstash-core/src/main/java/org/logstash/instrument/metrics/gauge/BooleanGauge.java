package org.logstash.instrument.metrics.gauge;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;
import org.logstash.instrument.metrics.counter.CounterMetric;

import java.util.List;

/**
 * A {@link GaugeMetric} that is backed by a {@link Boolean}
 */
public class BooleanGauge extends AbstractMetric<Boolean> implements GaugeMetric<Boolean> {

    private volatile Boolean value;

    /**
     * Constructor - null initial value
     *
     * @param name The name of this metric. This value may be used for display purposes.
     */
    public BooleanGauge(String name) {
        this(name, null);
    }

    /**
     * Constructor
     *
     * @param name         The name of this metric. This value may be used for display purposes.
     * @param initialValue The initial value for this {@link GaugeMetric}, may be null
     */
    public BooleanGauge(String name, Boolean initialValue) {
        super(name);
        this.value = initialValue;
    }

    @Override
    public MetricType getType() {
        return MetricType.GAUGE_BOOLEAN;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void set(Boolean value) {
        this.value = value;
    }

}
