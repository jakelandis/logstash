package org.logstash.instrument.metrics.gauge;

import org.logstash.instrument.metrics.AbstractMetric;

public abstract class AbstractGaugeMetric<T> extends AbstractMetric<T> implements GaugeMetric<T>{

    private volatile boolean dirty;

    private volatile T value;

    /**
     * Constructor
     *
     * @param name The name of this metric. This value may be used for display purposes.
     */
    protected AbstractGaugeMetric(String name) {
        super(name);
    }

    /**
     * Constructor
     *
     * @param name         The name of this metric. This value may be used for display purposes.
     * @param initialValue The initial value for this {@link GaugeMetric}, may be null
     */
    public AbstractGaugeMetric(String name, T initialValue) {
        super(name);
        this.value = initialValue;
        setDirty(true);

    }

    @Override
    public void reset() {
        this.value = null;
        setDirty(false);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty){
        this.dirty = dirty;
    }

    @Override
    public T getValue() {
        return value;
    }


    @Override
    public void set(T value) {
        this.value = value;
        setDirty(true);
    }

}
