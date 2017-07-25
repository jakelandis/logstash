package org.logstash.instrument.witness;

import org.logstash.instrument.metrics.counter.LongCounter;

import java.util.ArrayList;
import java.util.List;

public class ReloadWitness {


    private final LongCounter success;
    private final LongCounter failure;
    private List<String> namespaces;


    ReloadWitness(final List<String> parentNameSpace) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add("reloads");
        success = new LongCounter(namespaces, "successes");
        failure = new LongCounter(namespaces, "failures");
    }


    void success() {
        success.increment();
    }


    void failure() {
        failure.increment();
    }

}
