package org.logstash.instrument.witness.stats;

import org.logstash.instrument.metrics.gauge.BooleanGauge;
import org.logstash.instrument.metrics.gauge.NumericGauge;

import java.util.ArrayList;
import java.util.List;

final public class ConfigWitness {

    private final List<String> namespaces;

    private final BooleanGauge deadLetterQueueEnabled;
    private final BooleanGauge configReloadAutomatic;
    private final NumericGauge batchSize;
    private final NumericGauge workers;
    //TODO: Is this a counter ?
    private final NumericGauge batchDelay;
    private final NumericGauge configReloadInterval;



    ConfigWitness(final List<String> parentNameSpace) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add("config");
        deadLetterQueueEnabled = new BooleanGauge(namespaces, "dead_letter_queue_enabled" );
        configReloadAutomatic = new BooleanGauge(namespaces, "config_reload_automatic" );
        batchSize = new NumericGauge(namespaces, "batch_size");
        workers = new NumericGauge(namespaces, "workers");
        batchDelay = new NumericGauge(namespaces, "batch_delay");
        configReloadInterval = new NumericGauge(namespaces, "config_reload_interval");
    }

    public void deadLetterQueueEnabled(boolean enabled) {
        deadLetterQueueEnabled.set(enabled);
    }

    public void batchSize(long size){
        batchSize.set(size);
    }



    public void configReloadAutomatic(boolean isAuto) {
        configReloadAutomatic.set(isAuto);
    }



    public void workers(long workers) {
        this.workers.set(workers);
    }

    public void batchDelay(long delay) {
        batchDelay.set(delay);
    }

    public void configReloadInterval(long interval) {
        configReloadInterval.set(interval);
    }
}
