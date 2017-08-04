package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.gauge.TextGauge;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@JsonSerialize(using = ErrorWitness.Serializer.class)
public class ErrorWitness implements SerializableWitness {

    private final TextGauge message;
    private final TextGauge backtrace;

    private final String KEY = "last_error";

    public ErrorWitness() {
        message = new TextGauge("message");
        backtrace = new TextGauge("backtrace");
    }

    public void message(String message) {
        this.message.set(message);
    }

    /**
     * Stacktrace as a {@link String}
     *
     * @param stackTrace The stack trace already formatted for output.
     */
    public void backtrace(String stackTrace) {
        this.backtrace.set(stackTrace);
    }

    /**
     * Stacktrace for Java. For example: {@link Throwable#getStackTrace()}
     *
     * @param stackTrace The Java stacktrace to output
     */
    public void backtrace(StackTraceElement[] stackTrace) {
        Throwable t = new Throwable();
        t.setStackTrace(stackTrace);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(byteArrayOutputStream)) {

            t.printStackTrace(printStream);
            String backtrace = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
            System.out.println(backtrace);
            this.backtrace.set(backtrace);

        } catch (IOException e) {
            //A checked exception due to a the close on a ByteArrayOutputStream is simply annoying since it is an empty method.  This will never be called.
            throw new IllegalStateException("Unknown error", e);
        }
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    static class Serializer extends StdSerializer<ErrorWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(ErrorWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<ErrorWitness> t) {
            super(t);
        }

        @Override
        public void serialize(ErrorWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(ErrorWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObjectFieldStart(witness.KEY);
            MetricSerializer.Get.stringSerializer(gen).serialize(witness.message);
            MetricSerializer.Get.stringSerializer(gen).serialize(witness.backtrace);

            gen.writeEndObject();
        }
    }
}
