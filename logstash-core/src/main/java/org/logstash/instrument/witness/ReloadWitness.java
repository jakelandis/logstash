package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.Timestamp;
import org.logstash.ext.JrubyTimestampExtLibrary;
import org.logstash.instrument.metrics.counter.LongCounter;
import org.logstash.instrument.metrics.gauge.RubyTimeStampGauge;
import org.logstash.instrument.metrics.gauge.TextGauge;

import java.io.IOException;

@JsonSerialize(using = ReloadWitness.Serializer.class)
final public class ReloadWitness implements SerializableWitness {

    private final LongCounter success;
    private final LongCounter failure;
    private final ErrorWitness lastError;
    private final RubyTimeStampGauge lastSuccessTimestamp;
    private final RubyTimeStampGauge lastFailureTimestamp;
    private final Snitch snitch;

    private final static String KEY = "reloads";

    ReloadWitness() {
        success = new LongCounter("successes");
        failure = new LongCounter("failures");
        lastError = new ErrorWitness();
        lastSuccessTimestamp = new RubyTimeStampGauge("last_success_timestamp");
        lastFailureTimestamp = new RubyTimeStampGauge("last_failure_timestamp");
        snitch = new Snitch(this);
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    public void success(long count) {
        success.increment(count);
    }

    public void failure(long count) {
        failure.increment(count);
    }

    public void success() {
        success.increment();
    }

    public void failure() {
        failure.increment();
    }

    public Snitch snitch() {
        return snitch;
    }

    public ErrorWitness error() {
        return this.lastError;
    }

    public void lastSuccessTimestamp(JrubyTimestampExtLibrary.RubyTimestamp timestamp){
        lastSuccessTimestamp.set(timestamp);
    }

    public void lastFailureTimestamp(JrubyTimestampExtLibrary.RubyTimestamp timestamp){
        lastFailureTimestamp.set(timestamp);
    }

    static class Serializer extends StdSerializer<ReloadWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(ReloadWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<ReloadWitness> t) {
            super(t);
        }

        @Override
        public void serialize(ReloadWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(ReloadWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObjectFieldStart(witness.KEY);
            witness.lastError.genJson(gen, provider);
            MetricSerializer.Get.longSerializer(gen).serialize(witness.success);
            MetricSerializer.Get.timestampSerializer(gen).serialize(witness.lastSuccessTimestamp);
            MetricSerializer.Get.timestampSerializer(gen).serialize(witness.lastFailureTimestamp);
            MetricSerializer.Get.longSerializer(gen).serialize(witness.failure);
            gen.writeEndObject();
        }
    }

    static class Snitch{

        private final ReloadWitness witness;
        public Snitch(ReloadWitness witness) {
            this.witness = witness;
        }

        public long success() {
            return witness.success.getValue();
        }

        public long failure() {
            return witness.failure.getValue();
        }


        public Timestamp lastSuccessTimestamp() {
            return witness.lastSuccessTimestamp.getValue();
        }

        public Timestamp lastFailureTimestamp() {
            return  witness.lastFailureTimestamp.getValue();
        }

    }
}
