package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.counter.LongCounter;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.IOException;

@JsonSerialize(using = EventsWitness.Serializer.class)
final public class EventsWitness implements SerializableWitness {

    private LongCounter filtered;
    private LongCounter out;
    private LongCounter in;
    private LongCounter duration;
    private LongCounter queuePushDuration;
    final String KEY = "events";
    private final Snitch snitch;
    private boolean dirty; //here for passivity with legacy Ruby implementation


    public EventsWitness() {
        filtered = new LongCounter("filtered");
        out = new LongCounter("out");
        in = new LongCounter("in");
        duration = new LongCounter("duration_in_millis");
        queuePushDuration = new LongCounter("queue_push_duration_in_millis");
        snitch = new Snitch(this);
        dirty = false;
    }


    public void filtered() {
        filtered.increment();
        dirty = true;
    }

    public void out() {
        out.increment();
        dirty = true;
    }

    public void in(long count) {
        in.increment(count);
        dirty = true;
    }

    public void filtered(long count) {
        filtered.increment(count);
        dirty = true;
    }

    public void out(long count) {
        out.increment(count);
        dirty = true;
    }

    public void in() {
        in.increment();
        dirty = true;
    }

    public Snitch snitch(){
        return snitch;
    }

    public void duration(long durationToAdd) {
        duration.increment(durationToAdd);
        dirty = true;
    }


    @Override
    public void genJson(final JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    public void queuePushDuration(long durationToAdd) {
        queuePushDuration.increment(durationToAdd);
        dirty = true;
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
            if(witness.dirty) {
                gen.writeStartObject();
                innerSerialize(witness, gen, provider);
                gen.writeEndObject();
            }
        }

        void innerSerialize(EventsWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if(witness.dirty) {
                gen.writeObjectFieldStart(witness.KEY);
                MetricSerializer.Get.longSerializer(gen).serialize(witness.duration);
                MetricSerializer.Get.longSerializer(gen).serialize(witness.in);
                MetricSerializer.Get.longSerializer(gen).serialize(witness.filtered);
                MetricSerializer.Get.longSerializer(gen).serialize(witness.out);
                MetricSerializer.Get.longSerializer(gen).serialize(witness.queuePushDuration);
                gen.writeEndObject();
            }
        }
    }

    static class Snitch{

        private final EventsWitness witness;

        Snitch(EventsWitness witness) {
            this.witness = witness;
        }

        public long filtered() {
            return witness.filtered.getValue();

        }

        public long out() {
            return witness.out.getValue();
        }

        public long in() {
            return witness.in.getValue();
        }

        public long duration() {
           return witness.duration.getValue();
        }

        public long queuePushDuration() {
            return witness.queuePushDuration.getValue();
        }


    }

}
