package org.logstash.instrument.metrics;


import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;

public abstract class BaseMetric<T> implements IMetric<T> {
    final String key;

    final List<String> nameSpaces;


    protected BaseMetric(final List<String> nameSpaces, final String key) {
        this.nameSpaces = nameSpaces;
        this.key = key;
    }

    public String inspect() {
        return toString();
    }


    @JsonValue
    public abstract T getValue();



    @Override
    public String toString() {
        return String.format("%s - namespaces: %s key: %s value:%s", this.getClass().getName(), Arrays.toString(nameSpaces.toArray()), getValue().toString());
    }

    //counter, gauge, etc
    public abstract String type();


}
