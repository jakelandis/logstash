package org.logstash.instrument.metrics.gauge;


import java.util.List;

public class TextGuage extends GaugeMetric<String> {
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
    public void set(String value) {
        this.value = value;
    }


}