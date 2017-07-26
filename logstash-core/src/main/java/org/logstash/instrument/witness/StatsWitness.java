package org.logstash.instrument.witness;

import java.util.*;

public class StatsWitness {

    private final ReloadWitness reloadWitness;
    private final EventsWitness eventsWitness;
    private final Map<String, PipelineWitness> pipelines = new HashMap<>();
    private final List<String> NAME_SPACE = Collections.singletonList("stats");
    private final List<String> PIPELINES_NAME_SPACE = Arrays.asList("stats", "pipelines");

    private static final StatsWitness statsWitness = new StatsWitness();

    public static StatsWitness getInstance() {
        return statsWitness;
    }

    private StatsWitness() {
        this.reloadWitness = new ReloadWitness(NAME_SPACE);
        this.eventsWitness = new EventsWitness(NAME_SPACE);
    }

    public ReloadWitness reload() {
        return reloadWitness;
    }

    public EventsWitness event() {
        return eventsWitness;
    }


    PipelineWitness pipeline(String name) {
        return pipelines.containsKey(name) ? pipelines.get(name) : pipelines.put(name, new PipelineWitness(PIPELINES_NAME_SPACE, name));
    }
}
