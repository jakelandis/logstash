package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@JsonSerialize(using = StatsWitness.Serializer.class)
final public class StatsWitness implements SerializableWitness {

    private final ReloadWitness reloadWitness;
    private final EventsWitness eventsWitness;
    private final Map<String, PipelineWitness> pipelines;
    private final List<String> NAME_SPACE = Collections.singletonList("stats");
    private final List<String> PIPELINES_NAME_SPACE = Arrays.asList("stats", "pipelines");

    private static final StatsWitness statsWitness = new StatsWitness();

    public static StatsWitness getInstance() {
        return statsWitness;
    }

    private StatsWitness() {
        this.reloadWitness = new ReloadWitness(NAME_SPACE);
        this.eventsWitness = new EventsWitness(NAME_SPACE);
        this.pipelines = new ConcurrentHashMap<>();
    }


//    public ReloadWitness reload() {
//        return reloadWitness;
//    }


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
            PipelineWitness pipeline = new PipelineWitness(PIPELINES_NAME_SPACE, name);
            pipelines.put(name, pipeline);
            return pipeline;
        }
    }



    static class Serializer extends StdSerializer<StatsWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(StatsWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<StatsWitness> t) {
            super(t);
        }

        @Override
        public void serialize(StatsWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(StatsWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            witness.event().genJson(gen, provider);
            for (Map.Entry<String, PipelineWitness> entry : witness.pipelines.entrySet()) {
                entry.getValue().genJson(gen, provider);
            }
        }


    }


}
