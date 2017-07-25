package org.logstash.instrument.witness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PipelinesWitness {

    private List<String> namespaces;

    private Map<String, PipelineWitness> pipelines;

    PipelinesWitness(final List<String> parentNameSpace) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add("pipelines");
    }

    PipelineWitness pipeline(String name) {
        return pipelines.containsKey(name) ? pipelines.get(name) : pipelines.put(name, new PipelineWitness(namespaces, name));
    }

}
