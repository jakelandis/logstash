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
    private final TextGauge id;
    private final TextGauge name;
    private final Snitch snitch;
    private final Map<String, LazyDelegatingGauge> customGauges = new ConcurrentHashMap<>();
    private final Map<String, LongCounter> customCounters = new ConcurrentHashMap<>();

    private static final Serializer SERIALIZER = new Serializer();

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


    public void gauge(RubySymbol key, Object value) {
        gauge(key.asJavaString(), value);
    }

    public void gauge(String key, Object  value) {
        LazyDelegatingGauge gauge = customGauges.get(key);
        if( gauge != null){
            gauge.set(value);
        }else{
            gauge = new LazyDelegatingGauge(key, value);
            customGauges.put(key, gauge);
        }
    }

    //TODO: find actual usages of this
//    public void counter(RubySymbol key, String value) {
//        System.out.println(key.asJavaString() + value);
//
//    }
//
//    public void counter(String key, String value) {
//
//        LongCounter counter = customCounters.get(key);
//        if( counter == null){
//            counter..set(value);
//        }else{
//            gauge = new LazyDelegatingGauge(key, value);
//            customGauges.put(key, gauge);
//        }
//        System.out.println(key + value);
//
//    }

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
            for(LazyDelegatingGauge gauge : witness.customGauges.values()){
                gen.writeObjectField(gauge.getName(), gauge.getValue());
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

        //tODO: return a defensive copy
        public GaugeMetric gauge(String key){
            return customGauges.get(key);
        }

        public  Map<String, LazyDelegatingGauge> gauges(){
            return Collections.unmodifiableMap(customGauges);
        }

    }
}
