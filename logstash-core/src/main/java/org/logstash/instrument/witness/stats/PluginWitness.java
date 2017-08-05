package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.gauge.TextGauge;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@JsonSerialize(using = PluginWitness.Serializer.class)
public class PluginWitness implements SerializableWitness {


    private final EventsWitness eventsWitness;
    private final TextGauge id;
    private final TextGauge name;
    private final Map<Class<? extends SerializableWitness>, SerializableWitness > customWitnesses = new ConcurrentHashMap<>(1);

    public PluginWitness(String id) {
        eventsWitness = new EventsWitness();
        this.id = new TextGauge("id", id);
        this.name = new TextGauge("name");
    }

    public EventsWitness events() {
        return eventsWitness;
    }

    public PluginWitness name(String name){
        this.name.set(name);
        return this;
    }


    public <T extends SerializableWitness> void addCustom(T witness) {
        customWitnesses.putIfAbsent(witness.getClass(), witness);
    }

    /**
     *
     * @param clazz
     * @param <T>
     * @return null if a custom {@link SerializableWitness} of the provided type has not been added, else the custom {@link SerializableWitness}
     */
    public <T extends SerializableWitness> T custom(Class<T> clazz) {
        SerializableWitness w = customWitnesses.get(clazz);
        return  w == null ? null : clazz.cast(w);
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
            MetricSerializer.Get.stringSerializer(gen).serialize(witness.id);
            witness.events().genJson(gen, provider);
            MetricSerializer.Get.stringSerializer(gen).serialize(witness.name);
            for (SerializableWitness customWitness : witness.customWitnesses.values()) {
                customWitness.genJson(gen, provider);
            }
        }
    }
}
