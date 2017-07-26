package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonSerialize(using = PipelineWitness.Serializer.class)
final public class PipelineWitness implements SerializableWitness {

    private final List<String> namespaces;

    private final ReloadWitness reloadWitness;
    private final EventsWitness eventsWitness;
    private final ConfigWitness configWitness;
    private final Map<String, PluginWitness> plugins;
    private final String KEY;

    PipelineWitness(final List<String> parentNameSpace, String pipelineName) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add(pipelineName);
        this.KEY = pipelineName;
        this.reloadWitness = new ReloadWitness(namespaces);
        this.eventsWitness = new EventsWitness(namespaces);
        this.configWitness = new ConfigWitness(namespaces);
        this.plugins = new HashMap<>();
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    public ReloadWitness reload() {
        return reloadWitness;
    }

    public EventsWitness event() {
        return eventsWitness;
    }

    public ConfigWitness config() {
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

    static class Serializer extends StdSerializer<PipelineWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(PipelineWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<PipelineWitness> t) {
            super(t);
        }

        @Override
        public void serialize(PipelineWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(PipelineWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObjectFieldStart(witness.KEY);
            witness.event().genJson(gen, provider);
            witness.config().genJson(gen, provider);
            gen.writeObjectFieldStart("plugins");
            for (Map.Entry<String, PluginWitness> entry : witness.plugins.entrySet()) {
                entry.getValue().genJson(gen, provider);
            }
            gen.writeEndObject();
            gen.writeEndObject();
        }
    }
}
