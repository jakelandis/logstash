package org.logstash.instrument.metrics.gauge;


import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

public class TextGuage extends AbstractMetric<String> implements GaugeMetric<String> {
    private volatile String value;

    public TextGuage(List<String> nameSpaces, String key, String value) {
        super(nameSpaces, key);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String type() {
        return MetricType.GAUGE_TEXT.asString();
    }

    @Override
    public void set(String value) {
        this.value = value;
    }


}