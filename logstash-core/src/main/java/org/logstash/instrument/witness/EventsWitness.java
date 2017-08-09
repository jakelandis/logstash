package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.counter.LongCounter;

import java.io.IOException;

/**
 * Witness for events.
 */
@JsonSerialize(using = EventsWitness.Serializer.class)
final public class EventsWitness implements SerializableWitness {

    private LongCounter filtered;
    private LongCounter out;
    private LongCounter in;
    private LongCounter duration;
    private LongCounter queuePushDuration;
    final String KEY = "events";
    private final Snitch snitch;
    private final Forgetter forgetter;
    private boolean dirty; //here for passivity with legacy Ruby implementation

    /**
     * Constructor.
     */
    public EventsWitness() {
        filtered = new LongCounter("filtered");
        out = new LongCounter("out");
        in = new LongCounter("in");
        duration = new LongCounter("duration_in_millis");
        queuePushDuration = new LongCounter("queue_push_duration_in_millis");
        snitch = new Snitch(this);
        forgetter = new Forgetter(this);
        dirty = false;
    }

    /**
     * Add to the existing duration
     *
     * @param durationToAdd the amount to add to the existing duration.
     */
    public void duration(long durationToAdd) {
        duration.increment(durationToAdd);
        dirty = true;
    }

    /**
     * increment the filtered count by 1
     */
    public void filtered() {
        filtered.increment();
        dirty = true;
    }

    /**
     * increment the filtered count
     *
     * @param count the count to increment by
     */
    public void filtered(long count) {
        filtered.increment(count);
        dirty = true;
    }

    /**
     * Get a reference to associated forgetter.
     *
     * @return the associate {@link Forgetter}
     */
    public Forgetter forget() {
        return forgetter;
    }


    /**
     * increment the in count by 1
     */
    public void in() {
        in.increment();
        dirty = true;
    }

    /**
     * increment the in count
     *
     * @param count the number to increment by
     */
    public void in(long count) {
        in.increment(count);
        dirty = true;
    }

    /**
     * increment the out count by 1
     */
    public void out() {
        out.increment();
        dirty = true;
    }

    /**
     * increment the count
     *
     * @param count the number by which to increment by
     */
    public void out(long count) {
        out.increment(count);
        dirty = true;
    }

    /**
     * Get a reference to associated snitch to get discrete metric values.
     *
     * @return the associate {@link Snitch}
     */
    public Snitch snitch() {
        return snitch;
    }

    /**
     * Add to the existing queue push duration
     *
     * @param durationToAdd the duration to add
     */
    public void queuePushDuration(long durationToAdd) {
        queuePushDuration.increment(durationToAdd);
        dirty = true;
    }

    @Override
    public String asJson() throws IOException {
        return dirty ? SerializableWitness.super.asJson() : "";
    }

    @Override
    public void genJson(final JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    /**
     * The Jackson serializer.
     */
    public static class Serializer extends StdSerializer<EventsWitness> {

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
            if (witness.dirty) {
                gen.writeStartObject();
                innerSerialize(witness, gen, provider);
                gen.writeEndObject();
            }
        }

        void innerSerialize(EventsWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (witness.dirty) {
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

    /**
     * The snitch for the {@link EventsWitness}. Allows to read discrete metrics values.
     */
    public static class Snitch {

        private final EventsWitness witness;

        Snitch(EventsWitness witness) {
            this.witness = witness;
        }

        /**
         * Gets the duration of the events.
         *
         * @return the events duration.
         */
        public long duration() {
            return witness.duration.getValue();
        }

        /**
         * Gets the filtered events count.
         *
         * @return the count of the filtered events.
         */
        public long filtered() {
            return witness.filtered.getValue();

        }

        /**
         * Gets the in events count.
         *
         * @return the count of the events in.
         */
        public long in() {
            return witness.in.getValue();
        }

        /**
         * Gets the out events count.
         *
         * @return the count of the events out.
         */
        public long out() {
            return witness.out.getValue();
        }

        /**
         * Gets the duration of the queue push
         *
         * @return the queue push duration.
         */
        public long queuePushDuration() {
            return witness.queuePushDuration.getValue();
        }

    }

    /**
     * The forgetter for events.
     */
    public static class Forgetter {
        private final EventsWitness witness;

        Forgetter(EventsWitness witness) {
            this.witness = witness;
        }

        /**
         * Forgets all of the events metrics.
         */
        public void all() {
            witness.filtered.reset();
            witness.out.reset();
            witness.in.reset();
            witness.duration.reset();
            witness.queuePushDuration.reset();
            witness.dirty = false;
        }
    }

}
