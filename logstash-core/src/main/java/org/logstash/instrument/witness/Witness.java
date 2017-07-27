package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@JsonSerialize(using = Witness.Serializer.class)
final public class Witness implements SerializableWitness {

    private final ReloadWitness reloadWitness;
    private final EventsWitness eventsWitness;
    private final Map<String, PipelineWitness> pipelines;

    private static final Witness WITNESS = new Witness();

    public static Witness getInstance() {
        return WITNESS;
    }

    private Witness() {
        this.reloadWitness = new ReloadWitness();
        this.eventsWitness = new EventsWitness();
        this.pipelines = new ConcurrentHashMap<>();
    }


    public ReloadWitness reload() {
        return reloadWitness;
    }


    public EventsWitness event() {
        return eventsWitness;
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    public PipelineWitness pipeline(String name) {
        if (pipelines.containsKey(name)) {
            return pipelines.get(name);
        } else {
            PipelineWitness pipeline = new PipelineWitness(name);
            pipelines.put(name, pipeline);
            return pipeline;
        }
    }

    static class Serializer extends StdSerializer<Witness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(Witness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<Witness> t) {
            super(t);
        }

        @Override
        public void serialize(Witness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(Witness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            witness.event().genJson(gen, provider);
            witness.reload().genJson(gen, provider);
            gen.writeObjectFieldStart("pipelines");
            for (Map.Entry<String, PipelineWitness> entry : witness.pipelines.entrySet()) {
                entry.getValue().genJson(gen, provider);
            }
            gen.writeEndObject();
        }
    }
}
