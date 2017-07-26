package org.logstash.instrument.witness.stats;

import org.logstash.instrument.metrics.counter.LongCounter;

import java.util.ArrayList;
import java.util.List;

final public class ReloadWitness{


    private final LongCounter success;
    private final LongCounter failure;
    private final List<String> namespaces;


    ReloadWitness(final List<String> parentNameSpace) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add("reloads");
        success = new LongCounter(namespaces, "successes");
        failure = new LongCounter(namespaces, "failures");
    }



    public void success() {
        success.increment();
    }


    public void failure() {
        failure.increment();
    }

}
