package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.Metric;
import org.logstash.instrument.metrics.gauge.BooleanGauge;
import org.logstash.instrument.metrics.gauge.GaugeMetric;
import org.logstash.instrument.metrics.gauge.LongGauge;

import java.io.IOException;

final public class ConfigWitness implements SerializableWitness {


    private final BooleanGauge deadLetterQueueEnabled;
    private final BooleanGauge configReloadAutomatic;
    private final LongGauge batchSize;
    private final LongGauge workers;

    private final LongGauge batchDelay;
    private final LongGauge configReloadInterval;
    private final String KEY = "config";


    ConfigWitness() {
        deadLetterQueueEnabled = new BooleanGauge("dead_letter_queue_enabled");
        configReloadAutomatic = new BooleanGauge("config_reload_automatic");
        batchSize = new LongGauge("batch_size");
        workers = new LongGauge("workers");
        batchDelay = new LongGauge("batch_delay");
        configReloadInterval = new LongGauge("config_reload_interval");
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

            MetricSerializer.Get.longSerializer(gen).serialize(witness.batchSize);
            MetricSerializer.Get.longSerializer(gen).serialize(witness.workers);
            MetricSerializer.Get.longSerializer(gen).serialize(witness.batchDelay);
            MetricSerializer.Get.longSerializer(gen).serialize(witness.configReloadInterval);

            MetricSerializer.Get.booleanSerializer(gen).serialize(witness.configReloadAutomatic);
            MetricSerializer.Get.booleanSerializer(gen).serialize(witness.deadLetterQueueEnabled);
        }

    }


}
