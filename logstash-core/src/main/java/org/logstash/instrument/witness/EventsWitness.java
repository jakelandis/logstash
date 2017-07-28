package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.counter.LongCounter;

import java.io.IOException;

@JsonSerialize(using = EventsWitness.Serializer.class)
final public class EventsWitness implements SerializableWitness {

    private LongCounter filtered;
    private LongCounter out;
    private LongCounter in;
    private LongCounter duration;
    private LongCounter queuePushDuration;
    final String KEY = "events";


    public EventsWitness() {
        filtered = new LongCounter("filtered");
        out = new LongCounter("in");
        in = new LongCounter("out");
        duration = new LongCounter("duration_in_millis");
        queuePushDuration = new LongCounter("queue_push_duration_in_millis");
    }


    public void filtered() {
        filtered.increment();
    }

    public void out() {
        out.increment();
    }

    public void in(long count) {
        in.increment(count);
    }

    public void filtered(long count) {
        filtered.increment(count);
    }

    public void out(long count) {
        out.increment(count);
    }

    public void in() {
        in.increment();
    }

    public void duration(long durationToAdd) {
        duration.increment(durationToAdd);
    }


    @Override
    public void genJson(final JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
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
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(EventsWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObjectFieldStart(witness.KEY);
            gen.writeNumberField(witness.in.getName(), witness.in.getValue());
            gen.writeNumberField(witness.filtered.getName(), witness.filtered.getValue());
            gen.writeNumberField(witness.out.getName(), witness.out.getValue());
            gen.writeNumberField(witness.duration.getName(), witness.duration.getValue());
            gen.writeNumberField(witness.queuePushDuration.getName(), witness.queuePushDuration.getValue());
            gen.writeEndObject();
        }
    }

}
