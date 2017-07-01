package org.logstash.instrument.metrics.gauge;

import org.jruby.RubyHash;

import java.util.List;

/**
 * Created by jake on 6/30/17.
 */
public class RubyHashGauge extends GaugeMetric<RubyHash> {

    private volatile RubyHash value;

    protected RubyHashGauge(List<String> nameSpaces, String key, RubyHash value) {
        super(nameSpaces, key);
        this.value = value;
    }

    @Override
    public RubyHash getValue() {
        return value;
    }


    @Override
    public void set(RubyHash value) {
        this.value = value;

    }
}
