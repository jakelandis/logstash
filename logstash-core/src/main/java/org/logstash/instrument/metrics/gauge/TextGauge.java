package org.logstash.instrument.metrics.gauge;


import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * A {@link GaugeMetric} that is backed by a {@link String}
 */
public class TextGauge extends AbstractMetric<String> implements GaugeMetric<String> {

    private volatile String value;

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     */
    public TextGauge(String name) {
        this(name, null);
    }

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     * @param initialValue The initial value for this {@link GaugeMetric}, may be null
     */
    public TextGauge(String name, String initialValue) {
        super(name);
        this.value = initialValue;
    }

    @Override
    public MetricType getType() {
        return MetricType.GAUGE_TEXT;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void set(String value) {
        this.value = value;
    }


}