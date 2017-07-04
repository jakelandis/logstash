package org.logstash.instrument.metrics;


import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractMetric<T> implements Metric<T> {
    final protected String key;

    final protected List<String> nameSpace;


    protected AbstractMetric(final List<String> nameSpace, final String key) {
        this.nameSpace = nameSpace;
        this.key = key;
    }



    //FIXME: make this the single JSon value
    @JsonValue
//    @JsonProperty
    public abstract T getValue();



    @Override
    public String toString() {
        return String.format("%s - namespaces: %s key: %s value:%s", this.getClass().getName(), Arrays.toString(nameSpace.toArray()), getValue().toString());
    }

    //counter, gauge, etc
//    @JsonProperty
    @Override
    public abstract MetricType getType();


}
