package org.logstash.instrument.metrics.gauge;

import org.jruby.RubyHash;
import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * Created by jake on 6/30/17.
 */
public class RubyHashGauge extends AbstractMetric<RubyHash> implements GaugeMetric<RubyHash> {

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
    public String type() {
        return MetricType.GAUGE_RUBYHASH.asString();
    }


    @Override
    public void set(RubyHash value) {
        this.value = value;

    }
}
