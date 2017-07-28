package org.logstash.instrument.metrics.gauge;

import org.logstash.instrument.metrics.MetricType;

/**
 * A {@link GaugeMetric} that is backed by a {@link Object}.  Note - A stronger typed {@link GaugeMetric} should be used since this makes no guarantees of serializing properly.
 */
public class UnknownGauge extends AbstractMetric<Object> implements GaugeMetric<Object, Object> {

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     */
    public UnknownGauge(String name) {
        this(name, null);
    }

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     * @param initialValue The initial value for this {@link GaugeMetric}, may be null
     */
    public UnknownGauge(String name, Object initialValue) {
        super(name, initialValue);
    }

    @Override
    public MetricType getType() {
        return MetricType.GAUGE_UNKNOWN;
    }
}
