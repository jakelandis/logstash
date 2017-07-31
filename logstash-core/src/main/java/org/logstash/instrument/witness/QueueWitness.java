package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.counter.LongCounter;
import org.logstash.instrument.metrics.gauge.TextGauge;

import java.io.IOException;

@JsonSerialize(using = QueueWitness.Serializer.class)
final public class QueueWitness implements SerializableWitness{

    private final TextGauge type;
    private final static String KEY = "queue";

    QueueWitness() {
        type = new TextGauge("type");
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    public void type(String type) {
        this.type.set(type);
    }


    static class Serializer extends StdSerializer<QueueWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(QueueWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<QueueWitness> t) {
            super(t);
        }

        @Override
        public void serialize(QueueWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(QueueWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObjectFieldStart(witness.KEY);
            MetricSerializer.Get.stringSerializer(gen).serialize(witness.type);
            gen.writeEndObject();
        }
    }
}
