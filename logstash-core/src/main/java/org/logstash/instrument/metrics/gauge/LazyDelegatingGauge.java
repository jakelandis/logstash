package org.logstash.instrument.metrics.gauge;

import org.jruby.RubyHash;
import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * A lazy proxy to a more specific typed {@link GaugeMetric}. The metric will only be initialized once the {@code set} operation is called.
 * <p><strong>Intended only for use with Ruby's duck typing, Java consumers should use the specific typed {@link GaugeMetric}</strong></p>
 */
public class LazyDelegatingGauge extends AbstractMetric<Object> implements GaugeMetric<Object> {

    protected final String key;
    protected final List<String> nameSpaces;

    private GaugeMetric lazyMetric;

    /**
     * Constructor - protected so that Ruby may sub class proxy and discourage usage from Java
     *
     * @param nameSpace The namespace for this metric
     * @param key       The key <i>(with in the namespace)</i> for this metric
     */
    public LazyDelegatingGauge(final List<String> nameSpace, final String key) {
        super(nameSpace, key);
        this.nameSpaces = nameSpace;
        this.key = key;
    }

    @Override
    public Object get() {
        return lazyMetric == null ? null : lazyMetric.get();
    }

    @Override
    public MetricType getType() {
        return lazyMetric == null ? null : lazyMetric.getType();
    }

    @Override
    public Object getValue() {
        return lazyMetric == null ? null : lazyMetric.getValue();
    }

    @Override
    public void set(Object value) {
        if (value == null) {
            return;
        }
        wakeMetric(value);
    }

    /**
     * Instantiates the metric based on the type used to set this Gauge
     *
     * @param value The object used to set this value
     */
    private void wakeMetric(Object value) {
        if (lazyMetric == null) {
            //"quack quack"
            if (value instanceof Number) {
                lazyMetric = new NumericGauge(nameSpaces, key, (Number) value);
            } else if (value instanceof String) {
                lazyMetric = new TextGuage(nameSpaces, key, (String) value);
            } else if (value instanceof Boolean) {
                lazyMetric = new BooleanGauge(nameSpaces, key, (Boolean) value);
            } else if (value instanceof RubyHash) {
                lazyMetric = new RubyHashGauge(nameSpaces, key, (RubyHash) value);
            } else {
                lazyMetric = new UnknownGauge(nameSpaces, key, value);
            }
        }
    }

}
