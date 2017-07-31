package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

@JsonSerialize(using = PipelineWitness.Serializer.class)
final public class PipelineWitness implements SerializableWitness {

    private final ReloadWitness reloadWitness;
    private final EventsWitness eventsWitness;
    private final ConfigWitness configWitness;
    private final PluginsWitness pluginsWitness;
    private final QueueWitness queueWitness;
    private final String KEY;

    PipelineWitness(String pipelineName) {
        this.KEY = pipelineName;
        this.reloadWitness = new ReloadWitness();
        this.eventsWitness = new EventsWitness();
        this.configWitness = new ConfigWitness();
        this.pluginsWitness = new PluginsWitness();
        this.queueWitness = new QueueWitness();
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

    public QueueWitness queue() {
        return queueWitness;
    }

    public PluginWitness input(String name) {
        return pluginsWitness.input(name);
    }

    public PluginWitness output(String name) {
        return pluginsWitness.output(name);
    }

    public PluginWitness filter(String name) {
        return pluginsWitness.filter(name);
    }

    public PluginsWitness plugins() {
        return pluginsWitness;
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
            witness.plugins().genJson(gen, provider);
            witness.reload().genJson(gen,provider);
            witness.queue().genJson(gen, provider);
            gen.writeEndObject();
        }


    }
}
