package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.IOException;

@JsonSerialize(using = PluginWitness.Serializer.class)
final public class PluginWitness implements SerializableWitness {


    private final EventsWitness eventsWitness;
    private final String KEY;
    //TODO: inputs, output and filters


    PluginWitness(String pluginName) {
        KEY = pluginName;
        this.eventsWitness = new EventsWitness();
    }

    public EventsWitness event() {
        return eventsWitness;
    }


    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    static class Serializer extends StdSerializer<PluginWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(PluginWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<PluginWitness> t) {
            super(t);
        }

        @Override
        public void serialize(PluginWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(PluginWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObjectFieldStart(witness.KEY);
            witness.event().genJson(gen, provider);
            gen.writeEndObject();
        }
    }
}
