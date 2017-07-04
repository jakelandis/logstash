package org.logstash.instrument.metrics;

/**
 * Created by jake on 6/30/17.
 */
public interface Metric<T> {

    T getValue();

    /**
     * @deprecated - Use getValue
     * Pacify Ruby
     * @return
     */
    default T get(){
        return getValue();
    }

    MetricType getType();

    /**
     * Pacify Ruby
     * @return
     */
    default String type(){
        return getType().asString();
    }


    /**
     * Passify ruby
     * @return
     */
    default String inspect() {
        return toString();
    }


}
