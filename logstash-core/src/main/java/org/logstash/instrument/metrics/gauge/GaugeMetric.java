package org.logstash.instrument.metrics.gauge;

import org.logstash.instrument.metrics.BaseMetric;
import org.logstash.instrument.metrics.MetricType;

import java.util.List;

/**
 * Created by jake on 6/30/17.
 */
public abstract class GaugeMetric<T>  extends BaseMetric<T> {

    protected GaugeMetric(List<String> nameSpaces, String key) {
        super(nameSpaces, key);
    }

    @Override
    public String type() {
        return MetricType.GAUGE.asString();
    }

}
