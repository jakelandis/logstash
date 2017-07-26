package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.counter.LongCounter;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@JsonSerialize(using = EventsWitness.Serializer.class)
final public class EventsWitness implements SerializableWitness{

    private LongCounter filtered;
    private LongCounter out;
    private LongCounter in;
    private LongCounter duration;
    private LongCounter queuePushDuration;
    private List<String> namespaces;
    final String KEY = "events";


    public EventsWitness(final List<String> parentNameSpace) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add(KEY);
        filtered = new LongCounter(namespaces, "filtered");
        out = new LongCounter(namespaces, "in");
        in = new LongCounter(namespaces, "out");
        duration = new LongCounter(namespaces, "duration_in_millis");
        queuePushDuration = new LongCounter(namespaces, "queue_push_duration_in_millis");
    }


    public void filtered() {
        filtered.increment();
    }

    public void out() {
        out.increment();
    }

    public void in() {
        in.increment();
    }

    public void duration(long durationToAdd) {
        duration.increment(durationToAdd);
    }


    @Override
    public void genJson(final JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().serialize(this, gen, provider);
    }

    public void queuePushDuration(long durationToAdd) {
        queuePushDuration.increment(durationToAdd);
    }


    static class Serializer extends StdSerializer<EventsWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(EventsWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<EventsWitness> t) {
            super(t);
        }


        @Override
        public void serialize(EventsWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObjectFieldStart(witness.KEY);
            gen.writeNumberField(witness.in.getKey(), witness.in.getValue());
            gen.writeNumberField(witness.out.getKey(), witness.out.getValue());
            gen.writeNumberField(witness.filtered.getKey(), witness.filtered.getValue());
            gen.writeNumberField(witness.duration.getKey(), witness.duration.getValue());
            gen.writeNumberField(witness.queuePushDuration.getKey(), witness.queuePushDuration.getValue());
            gen.writeEndObject();

        }
    }

}
