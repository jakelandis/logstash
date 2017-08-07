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
    private final Forgetter forgetter;
    private final String KEY;

    public PipelineWitness(String pipelineName) {
        this.KEY = pipelineName;
        this.reloadWitness = new ReloadWitness();
        this.eventsWitness = new EventsWitness();
        this.configWitness = new ConfigWitness();
        this.pluginsWitness = new PluginsWitness();
        this.queueWitness = new QueueWitness();
        this.forgetter = new Forgetter(this);
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    public ReloadWitness reloads() {
        return reloadWitness;
    }

    public EventsWitness events() {
        return eventsWitness;
    }

    public ConfigWitness config() {
        return configWitness;
    }

    public QueueWitness queue() {
        return queueWitness;
    }

    public PluginWitness inputs(String name) {
        return pluginsWitness.inputs(name);
    }

    public PluginWitness outputs(String name) {
        return pluginsWitness.outputs(name);
    }

    public PluginWitness filters(String name) {
        return pluginsWitness.filters(name);
    }

    public PluginsWitness plugins() {
        return pluginsWitness;
    }

    public Forgetter forget() {
        return forgetter;
    }


    public static class Serializer extends StdSerializer<PipelineWitness> {

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
            witness.events().genJson(gen, provider);
            witness.plugins().genJson(gen, provider);
            witness.reloads().genJson(gen, provider);
            witness.queue().genJson(gen, provider);
            //TODO: implement for https://github.com/elastic/logstash/issues/7870 (need to support via Sinatra too)
            //witness.config().genJson(gen, provider);
            gen.writeEndObject();
        }


    }

    public static class Forgetter {
        private final PipelineWitness witness;

        Forgetter(PipelineWitness witness) {
            this.witness = witness;
        }

        /**
         * Forgets (removes) the plugins and events data for the given pipeline, but keeps everything else
         */
        void partial() {
            witness.plugins().forget().all();
            witness.events().forget().all();
        }
    }
}
