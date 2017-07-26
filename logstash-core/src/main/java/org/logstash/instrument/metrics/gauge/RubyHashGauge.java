package org.logstash.instrument.metrics.gauge;

import org.jruby.RubyHash;
import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;


import java.util.List;

/**
 * A {@link GaugeMetric} that is backed by a {@link RubyHash}.  Note - This should not be used directly from Java code and exists for passivity with legacy Ruby code. Depending
 * on the types in in the {@link RubyHash} there are no guarantees serializing properly.
 */
public class RubyHashGauge extends AbstractMetric<RubyHash> implements GaugeMetric<RubyHash,RubyHash> {

    private volatile RubyHash value;

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     */
    protected RubyHashGauge(String name) {
        this(name, null);
    }

    /**
     * Constructor - protected so that Ruby may sub class proxy and discourage usage from Java
     *
     * @param name The name of this metric. This value may be used for display purposes.
     * @param initialValue The initial value for this {@link GaugeMetric}, may be null
     */
    protected RubyHashGauge(String name, RubyHash initialValue) {
        super(name);
        this.value = initialValue;
    }

    @Override
    public MetricType getType() {
        return MetricType.GAUGE_RUBYHASH;
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
