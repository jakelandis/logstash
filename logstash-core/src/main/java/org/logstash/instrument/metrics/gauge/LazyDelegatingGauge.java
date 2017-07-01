package org.logstash.instrument.metrics.gauge;

import org.jruby.RubyHash;
import org.logstash.instrument.metrics.AbstractMetric;
import org.logstash.instrument.metrics.Metric;
import org.logstash.instrument.metrics.counter.LongCounter;

import java.util.List;

/**
 * Intended only for use with Ruby's duck typing, Java consumers use the specific typed {@link GaugeMetric}
 */
public class LazyDelegatingGauge extends AbstractMetric<Object> implements GaugeMetric<Object> {

    final String key;
    final List<String> nameSpaces;

    Metric lazyMetric;


    protected LazyDelegatingGauge(final List<String> nameSpaces, final String key) {
        super(nameSpaces, key);
        this.nameSpaces = nameSpaces;
        this.key = key;
    }


    @Override
    public Object getValue() {
        return lazyMetric == null ? null : lazyMetric.getValue();
    }

    @Override
    public String type() {
        return lazyMetric == null ? null : lazyMetric.type();
    }

    @Override
    public Object get() {
        return lazyMetric == null ? null : lazyMetric.get();
    }

    @Override
    public void set(Object value) {
        if(value == null){
            //TODO: debug log
            return;
        }
        //"quack quack"
        if (value instanceof Double) {
            lazyMetric = new NumericGauge(nameSpaces, key, (Double) value);
        } else if (value instanceof String) {
            lazyMetric = new TextGuage(nameSpaces, key, (String) value);
        } else if (value instanceof Long) {
            lazyMetric = new LongCounter(nameSpaces, key, (Long) value);
        } else if (value instanceof Boolean){
            lazyMetric = new BooleanGauge(nameSpaces, key, (Boolean) value);
        } else if (value instanceof RubyHash){
            lazyMetric = new RubyHashGauge(nameSpaces, key, (RubyHash) value);
        }    else {

            //TODO: log a warning about may not serialize correctly, please log an issue
            System.out.println("********** UNKOWN TYPE ************" + value.getClass().getCanonicalName());
            lazyMetric = new UnknownGauge(nameSpaces, key, value);
            //throw new IllegalStateException("Unsupported type of metric value: " + value.getClass().getCanonicalName());
        }
    }



}
