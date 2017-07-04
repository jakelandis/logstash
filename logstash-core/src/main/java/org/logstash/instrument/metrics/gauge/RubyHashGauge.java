package org.logstash.instrument.metrics.gauge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jruby.RubyHash;
import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * TODO: validate that this serializes correctly via Jackson
 */
public class RubyHashGauge extends AbstractMetric<RubyHash> implements GaugeMetric<RubyHash>{

    private final static Logger LOGGER = LogManager.getLogger(RubyHashGauge.class);


    private volatile RubyHash value;

    public RubyHashGauge(List<String> nameSpaces, String key, RubyHash value) {
        super(nameSpaces, key);
        this.value = value;
    }

    @Override
    public RubyHash getValue() {
        return value;
    }

    @Override
    public MetricType getType() {
        return  MetricType.GAUGE_RUBYHASH;
    }


    @Override
    public void set(RubyHash value) {
        this.value = value;

    }
}
