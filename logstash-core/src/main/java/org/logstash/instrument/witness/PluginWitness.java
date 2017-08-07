package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.gauge.TextGauge;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Witness for a single plugin.
 */
@JsonSerialize(using = PluginWitness.Serializer.class)
public class PluginWitness implements SerializableWitness {

    private final EventsWitness eventsWitness;
    private final TextGauge id;
    private final TextGauge name;
    private final Snitch snitch;
    private final Map<Class<? extends SerializableWitness>, SerializableWitness> customWitnesses = new ConcurrentHashMap<>(1);

    /**
     * Constructor.
     *
     * @param id The unique identifier for this plugin.
     */
    public PluginWitness(String id) {
        eventsWitness = new EventsWitness();
        this.id = new TextGauge("id", id);
        this.name = new TextGauge("name");
        this.snitch = new Snitch(this);
    }

    /**
     * Get a reference to the associated events witness.
     *
     * @return the associated {@link EventsWitness}
     */
    public EventsWitness events() {
        return eventsWitness;
    }

    /**
     * Sets the name of this plugin.
     *
     * @param name the name of this plugin.
     * @return an instance of this witness (to allow method chaining)
     */
    public PluginWitness name(String name) {
        this.name.set(name);
        return this;
    }

    /**
     * Get a reference to associated snitch to get discrete metric values.
     *
     * @return the associate {@link Snitch}
     */
    public Snitch snitch() {
        return snitch;
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    /**
     * The Jackson JSON serializer.
     */
    public static class Serializer extends StdSerializer<PluginWitness> {

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
            MetricSerializer.Get.stringSerializer(gen).serialize(witness.id);
            witness.events().genJson(gen, provider);
            MetricSerializer.Get.stringSerializer(gen).serialize(witness.name);
            for (SerializableWitness customWitness : witness.customWitnesses.values()) {
                customWitness.genJson(gen, provider);
            }
        }
    }

    /**
     * Snitch for a plugin. Provides discrete metric values.
     */
    public static class Snitch{

        private final PluginWitness witness;

        Snitch(PluginWitness witness) {
            this.witness = witness;
        }

        /**
         * Gets the id for this plugin.
         * @return the id
         */
        public String id(){
            return witness.id.getValue();
        }

        /**
         * Gets the name of this plugin
         * @return the name
         */
        public String name(){
            return witness.name.getValue();
        }

    }
}
