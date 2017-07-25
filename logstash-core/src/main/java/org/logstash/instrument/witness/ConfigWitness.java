package org.logstash.instrument.witness;

import org.logstash.instrument.metrics.gauge.BooleanGauge;
import org.logstash.instrument.metrics.gauge.NumericGauge;
import org.logstash.instrument.metrics.gauge.TextGauge;

import java.util.ArrayList;
import java.util.List;

public class ConfigWitness {

    private List<String> namespaces;

    private final BooleanGauge deadLetterQueueEnabled;
    private final NumericGauge batchSize;


    ConfigWitness(final List<String> parentNameSpace) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add("config");
        deadLetterQueueEnabled = new BooleanGauge(namespaces, "dead_letter_queue_enabled" );
        batchSize = new NumericGauge(namespaces, "batch_size");
    }

    void deadLetterQueueEnabled(boolean enabled) {
        deadLetterQueueEnabled.set(enabled);
    }

    void batchSize(long size){
        batchSize.set(size);
    }
}
