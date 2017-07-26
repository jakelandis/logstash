package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.gauge.BooleanGauge;
import org.logstash.instrument.metrics.gauge.NumericGauge;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final public class ConfigWitness implements SerializableWitness {

    private final List<String> namespaces;

    private final BooleanGauge deadLetterQueueEnabled;
    private final BooleanGauge configReloadAutomatic;
    private final NumericGauge batchSize;
    private final NumericGauge workers;
    //TODO: Is this a counter or gauge ?
    private final NumericGauge batchDelay;
    private final NumericGauge configReloadInterval;
    private final String KEY = "config";


    ConfigWitness(final List<String> parentNameSpace) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add(KEY);
        deadLetterQueueEnabled = new BooleanGauge(namespaces, "dead_letter_queue_enabled");
        configReloadAutomatic = new BooleanGauge(namespaces, "config_reload_automatic");
        batchSize = new NumericGauge(namespaces, "batch_size");
        workers = new NumericGauge(namespaces, "workers");
        batchDelay = new NumericGauge(namespaces, "batch_delay");
        configReloadInterval = new NumericGauge(namespaces, "config_reload_interval");
    }

    public void deadLetterQueueEnabled(boolean enabled) {
        deadLetterQueueEnabled.set(enabled);
    }

    public void batchSize(long size) {
        batchSize.set(size);
    }

    public void configReloadAutomatic(boolean isAuto) {
        configReloadAutomatic.set(isAuto);
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);

    }

    public void workers(long workers) {
        this.workers.set(workers);
    }

    public void batchDelay(long delay) {
        batchDelay.set(delay);
    }

    public void configReloadInterval(long interval) {
        configReloadInterval.set(interval);
    }

    static class Serializer extends StdSerializer<ConfigWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(ConfigWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<ConfigWitness> t) {
            super(t);
        }

        @Override
        public void serialize(ConfigWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(ConfigWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObjectFieldStart(witness.KEY);
            //FIXME: assuming a long value is not correct :( , need a better guage

//            gen.writeNumberField(witness.batchSize.getKey(), witness.batchSize.getValue());
//            gen.writeNumberField(witness.workers.getKey(), witness.workers.getValue());
//            gen.writeNumberField(witness.batchDelay.getKey(), witness.batchDelay.getValue());
//            gen.writeNumberField(witness.configReloadInterval.getKey(), witness.configReloadInterval.getValue());

            Boolean value;
            if ((value = witness.configReloadAutomatic.getValue()) != null) {
                gen.writeBooleanField(witness.configReloadAutomatic.getKey(), value);
            }
            if ((value = witness.deadLetterQueueEnabled.getValue()) != null) {
                gen.writeBooleanField(witness.deadLetterQueueEnabled.getKey(), value);
            }
            gen.writeEndObject();
        }
    }
}
