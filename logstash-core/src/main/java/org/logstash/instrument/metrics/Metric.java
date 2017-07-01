package org.logstash.instrument.metrics;

/**
 * Created by jake on 6/30/17.
 */
public interface Metric<T> {

    T getValue();

    default T get(){
        return getValue();
    }

    String type();




}
