package org.logstash.instrument.metrics;

/**
 * Created by jake on 6/30/17.
 */
public interface IMetric<T> {

    T getValue();

    default T get(){
        return getValue();
    }


    void set(T value);

    default void increment() {
       throw new UnsupportedOperationException(String.format("%s class does not support incrementing", this.getClass().getName()));
    }

    default void increment(long by) {
        throw new UnsupportedOperationException(String.format("%s class does not support incrementing", this.getClass().getName()));
    }

    default void decrement() {
        throw new UnsupportedOperationException(String.format("%s class does not support incrementing", this.getClass().getName()));
    }

    default void decrement(long by) {
        throw new UnsupportedOperationException(String.format("%s class does not support incrementing", this.getClass().getName()));
    }

}
