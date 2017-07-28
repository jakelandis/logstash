package org.logstash.instrument.metrics.gauge;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

//TODO: TEST
/**
 * A {@link GaugeMetric} that is backed by a {@link Double}
 */
public class DoubleGauge extends AbstractMetric<Double> implements GaugeMetric<Double> {

    private volatile Double value;

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     */
    public DoubleGauge(String name) {
        this(name, null);
    }

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     * @param initialValue The initial value for this {@link GaugeMetric}, may be null
     */
    public DoubleGauge(String name, Double initialValue) {
        super(name);
        this.value = initialValue;
    }

    @Override
    public MetricType getType() {
        return MetricType.GAUGE_NUMERIC;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void set(Double value) {
        this.value = value;
    }

}

