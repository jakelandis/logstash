package org.logstash.instrument.metrics.gauge;


import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * A {@link GaugeMetric} that is backed by a {@link String}
 */
public class TextGuage extends AbstractMetric<String> implements GaugeMetric<String> {

    private volatile String value;

    /**
     * Constructor
     *
     * @param nameSpace    The namespace for this metric
     * @param key          The key <i>(with in the namespace)</i> for this metric
     * @param initialValue The initial value for this {@link GaugeMetric}
     */
    public TextGuage(List<String> nameSpace, String key, String initialValue) {
        super(nameSpace, key);
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