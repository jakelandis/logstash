package org.logstash.instrument.witness;

import org.logstash.instrument.metrics.counter.LongCounter;

import java.util.ArrayList;
import java.util.List;

public class EventsWitness {

    private final LongCounter filtered;
    private final LongCounter out;
    private final LongCounter in;
    private List<String> namespaces;


    EventsWitness(final List<String> parentNameSpace) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add("events");
        filtered = new LongCounter(namespaces, "filtered");
        out = new LongCounter(namespaces, "in");
        in = new LongCounter(namespaces, "out");
    }


    void filtered() {
        filtered.increment();
    }


    void out() {
        out.increment();
    }

    void in() {
        in.increment();
    }

}
