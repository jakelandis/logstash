package org.logstash.instrument.witness;

import java.util.Collections;
import java.util.List;

public class StatsWitness {

    private final ReloadWitness reloadWitness;
    private final EventsWitness eventsWitness;
    private final PipelinesWitness pipelinesWitness;
    private final List<String> NAME_SPACE = Collections.singletonList("stats");

    private static final StatsWitness statsWitness = new StatsWitness();

    public static StatsWitness getInstance() {
        return statsWitness;
    }

    private StatsWitness() {
        this.reloadWitness = new ReloadWitness(NAME_SPACE);
        this.eventsWitness = new EventsWitness(NAME_SPACE);
        this.pipelinesWitness = new PipelinesWitness(NAME_SPACE);
    }

    public ReloadWitness reload() {
        return reloadWitness;
    }

    public EventsWitness event() {
        return eventsWitness;
    }

    public PipelinesWitness pipelines() {
        return pipelinesWitness;
    }
}
