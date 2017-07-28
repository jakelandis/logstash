package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@JsonSerialize(using = PipelinesWitness.Serializer.class)
final public class PipelinesWitness implements SerializableWitness {


    private final Map<String, PipelineWitness> pipelines;

    private final static String KEY = "pipelines";

    public PipelinesWitness() {
        this.pipelines = new ConcurrentHashMap<>();
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


    static class Serializer extends StdSerializer<PipelinesWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(PipelinesWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<PipelinesWitness> t) {
            super(t);
        }

        @Override
        public void serialize(PipelinesWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(PipelinesWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObjectFieldStart(KEY);
            for (Map.Entry<String, PipelineWitness> entry : witness.pipelines.entrySet()) {
                entry.getValue().genJson(gen, provider);
            }
            gen.writeEndObject();
        }
    }

}
