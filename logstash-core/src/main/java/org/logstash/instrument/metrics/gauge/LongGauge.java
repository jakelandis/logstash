package org.logstash.instrument.metrics.gauge;

import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

//TODO: TEST
/**
 * A {@link GaugeMetric} that is backed by a {@link Long}
 */
public class LongGauge extends AbstractMetric<Long> implements GaugeMetric<Long> {

    private volatile Long value;

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     */
    public LongGauge(String name) {
        this(name, null);
    }

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     * @param initialValue The initial value for this {@link GaugeMetric}, may be null
     */
    public LongGauge(String name, Long initialValue) {
        super(name);
        this.value = initialValue;
    }

    @Override
    public MetricType getType() {
        return MetricType.GAUGE_NUMERIC;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public void set(Long value) {
        this.value = value;
    }

}
