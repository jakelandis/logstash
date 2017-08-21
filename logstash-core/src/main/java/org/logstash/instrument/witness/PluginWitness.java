package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.jruby.RubySymbol;
import org.logstash.instrument.metrics.Metric;
import org.logstash.instrument.metrics.counter.CounterMetric;
import org.logstash.instrument.metrics.counter.LongCounter;
import org.logstash.instrument.metrics.gauge.GaugeMetric;
import org.logstash.instrument.metrics.gauge.LazyDelegatingGauge;
import org.logstash.instrument.metrics.gauge.TextGauge;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Witness for a single plugin.
 */
@JsonSerialize(using = PluginWitness.Serializer.class)
public class PluginWitness implements SerializableWitness {

    private final EventsWitness eventsWitness;
    private final CustomWitness customWitness;
    private final TextGauge id;
    private final TextGauge name;
    private final Snitch snitch;


    private static final Serializer SERIALIZER = new Serializer();

    /**
     * Constructor.
     *
     * @param id The unique identifier for this plugin.
     */
    public PluginWitness(String id) {
        eventsWitness = new EventsWitness();
        customWitness = new CustomWitness();
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

    public CustomWitness custom() {
        return this.customWitness;
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
        SERIALIZER.innerSerialize(this, gen, provider);
    }

    /**
     * The Jackson JSON serializer.
     */
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
            MetricSerializer<Metric<String>> stringSerializer = MetricSerializer.Get.stringSerializer(gen);
            stringSerializer.serialize(witness.id);
            witness.events().genJson(gen, provider);
            stringSerializer.serialize(witness.name);
            for (GaugeMetric<Object, Object> gauge : witness.customWitness.gauges.values()) {
                gen.writeObjectField(gauge.getName(), gauge.getValue());
            }
        }
    }

    /**
     * A custom witness that we can hand off to plugin's to contribute to the metrics
     */
    public class CustomWitness {

        private final Snitch snitch;

        /**
         * private Constructor - not for external instantiation
         */
        private CustomWitness() {
            this.snitch = new Snitch(this);
        }

        private final Map<String, GaugeMetric<Object, Object>> gauges = new ConcurrentHashMap<>();
        private final Map<String, CounterMetric<Long>> counters = new ConcurrentHashMap<>();

        public void gauge(RubySymbol key, Object value) {
            gauge(key.asJavaString(), value);
        }

        public void gauge(String key, Object value) {
            GaugeMetric<Object, Object> gauge = gauges.get(key);
            if (gauge != null) {
                gauge.set(value);
            } else {
                gauge = new LazyDelegatingGauge(key, value);
                gauges.put(key, gauge);
            }
        }

        public void increment(RubySymbol key) {
            increment(key.asJavaString());
        }

        public void increment(String key) {
            increment(key, 0);
        }

        public void increment(RubySymbol key, long by) {
            increment(key.asJavaString(), by);
        }

        public void increment(String key, long by) {
            CounterMetric<Long> counter = counters.get(key);
            if (counter != null) {
                counter.increment(by);
            } else {
                counter = new LongCounter(key, by);
                counters.put(key, counter);
            }
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
         * Snitch for a plugin. Provides discrete metric values.
         */
        public class Snitch {

            private final CustomWitness witness;

            private Snitch(CustomWitness witness) {
                this.witness = witness;
            }

            public GaugeMetric gauge(String key) {
                return witness.gauges.get(key);
            }

            public Map<String, GaugeMetric<?, ?>> gauges() {
                return Collections.unmodifiableMap(witness.gauges);
            }

            public CounterMetric<?> counter(String key) {
                return witness.counters.get(key);
            }

            public Map<String, CounterMetric<?>> counters() {
                return Collections.unmodifiableMap(witness.counters);
            }

        }

    }

    /**
     * Snitch for a plugin. Provides discrete metric values.
     */
    public class Snitch {

        private final PluginWitness witness;

        private Snitch(PluginWitness witness) {
            this.witness = witness;
        }

        /**
         * Gets the id for this plugin.
         *
         * @return the id
         */
        public String id() {
            return witness.id.getValue();
        }

        /**
         * Gets the name of this plugin
         *
         * @return the name
         */
        public String name() {
            return witness.name.getValue();
        }

        public CustomWitness custom() {
            return customWitness;
        }

    }
}
