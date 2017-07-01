package org.logstash.instrument.metrics;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractMetric<T> implements Metric<T> {
    final String key;

    final List<String> nameSpaces;


    protected AbstractMetric(final List<String> nameSpaces, final String key) {
        this.nameSpaces = nameSpaces;
        this.key = key;
    }

    /**
     * Passify ruby
     * @return
     */
    public String inspect() {
        return toString();
    }


    //@JsonValue
    @JsonProperty
    public abstract T getValue();



    @Override
    public String toString() {
        return String.format("%s - namespaces: %s key: %s value:%s", this.getClass().getName(), Arrays.toString(nameSpaces.toArray()), getValue().toString());
    }

    //counter, gauge, etc
    @JsonProperty
    @Override
    public abstract String type();


}
