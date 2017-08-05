package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.IOException;
import java.util.Arrays;


@JsonSerialize(using = StatsWitness.Serializer.class)
final public class StatsWitness implements SerializableWitness {

    private final ReloadWitness reloadWitness;
    private final EventsWitness eventsWitness;
    private final PipelinesWitness pipelinesWitness;

    private static StatsWitness _instance;

    /**
     * <p>THIS IS ONLY TO BE USED BY THE RUBY AGENT</p>
     */
    public StatsWitness() {
        this.reloadWitness = new ReloadWitness();
        this.eventsWitness = new EventsWitness();
        this.pipelinesWitness = new PipelinesWitness();
    }

    /**
     * This is a dirty hack since the {@link StatsWitness} needs to mirror the Ruby agent's lifecycle, but needs to used during the Ruby object construction. Exposing this allows
     * Ruby to create the instance for use in it's constructor, then set it here for all to use as a singleton.
     * <p>THIS IS ONLY TO BE USED BY THE RUBY AGENT</p>
     *
     * @param __instance The instance of the {@link StatsWitness} to use as the singleton instance that mirror's the agent's lifecycle.
     */
    public static void setInstance(StatsWitness __instance) {
        _instance = __instance;
    }

    public static StatsWitness instance() {
        if (_instance == null) {
            throw new IllegalStateException("The stats witness instance must be set before it used. Called from: " + Arrays.toString(new Throwable().getStackTrace()));
        }
        return _instance;
    }

    public ReloadWitness reloads() {
        return reloadWitness;
    }

    public EventsWitness events() {
        return eventsWitness;
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    public PipelinesWitness pipelines() {
        return pipelinesWitness;
    }

    /**
     * TODO
     * Shortcut method for pipelines.pipeline(name)
     *
     * @param name
     * @return
     */
    public PipelineWitness pipeline(String name) {
        return pipelinesWitness.pipeline(name);

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
            witness.events().genJson(gen, provider);
            witness.reloads().genJson(gen, provider);
            witness.pipelinesWitness.genJson(gen, provider);
        }
    }


}
