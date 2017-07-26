package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.counter.LongCounter;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonSerialize(using = ReloadWitness.Serializer.class)
final public class ReloadWitness implements SerializableWitness{

    private final LongCounter success;
    private final LongCounter failure;
    private final String KEY = "reloads";

    ReloadWitness() {
        success = new LongCounter("successes");
        failure = new LongCounter("failures");
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    public void success() {
        success.increment();
    }

    public void failure() {
        failure.increment();
    }

    static class Serializer extends StdSerializer<ReloadWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(ReloadWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<ReloadWitness> t) {
            super(t);
        }

        @Override
        public void serialize(ReloadWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(ReloadWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObjectFieldStart(witness.KEY);
            gen.writeNumberField(witness.success.getName(), witness.success.getValue());
            gen.writeNumberField(witness.failure.getName(), witness.failure.getValue());
            gen.writeEndObject();
        }
    }
}
