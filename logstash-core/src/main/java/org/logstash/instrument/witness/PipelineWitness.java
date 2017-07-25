package org.logstash.instrument.witness;

import java.util.ArrayList;
import java.util.List;

public class PipelineWitness {

    private List<String> namespaces;

    private final ReloadWitness reloadWitness;
    private final EventsWitness eventsWitness;
    private final ConfigWitness configWitness;

    PipelineWitness(final List<String> parentNameSpace, String pipelineName) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add(pipelineName);
        this.reloadWitness = new ReloadWitness(namespaces);
        this.eventsWitness = new EventsWitness(namespaces);
        this.configWitness = new ConfigWitness(namespaces);

    }

    public ReloadWitness reload() {
        return reloadWitness;
    }

    public EventsWitness event() {
        return eventsWitness;
    }

    public ConfigWitness config(){
        return configWitness;
    }


}
