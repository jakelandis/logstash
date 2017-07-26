package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final public class PipelineWitness implements SerializableWitness {

    private final List<String> namespaces;

    private final ReloadWitness reloadWitness;
    private final EventsWitness eventsWitness;
    private final ConfigWitness configWitness;
    private final Map<String, PluginWitness> plugins;
    private final String key;

    PipelineWitness(final List<String> parentNameSpace, String pipelineName) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add(pipelineName);
        this.key = pipelineName;
        this.reloadWitness = new ReloadWitness(namespaces);
        this.eventsWitness = new EventsWitness(namespaces);
        this.configWitness = new ConfigWitness(namespaces);
        this.plugins = new HashMap<>();
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeObjectFieldStart(key);
        eventsWitness.genJson(gen, provider);

        gen.writeEndObject();
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


    public PluginWitness plugin(String name) {
        if (plugins.containsKey(name)) {
            return plugins.get(name);
        } else {
            PluginWitness pluginWitness = new PluginWitness(namespaces, name);
            plugins.put(name, pluginWitness);
            return pluginWitness;
        }

    }



}
