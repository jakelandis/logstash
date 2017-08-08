package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.metrics.gauge.TextGauge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Witness for errors.
 */
@JsonSerialize(using = ErrorWitness.Serializer.class)
public class ErrorWitness implements SerializableWitness {

    private final TextGauge message;
    private final TextGauge backtrace;
    private final Snitch snitch;

    private final String KEY = "last_error";

    public ErrorWitness() {
        message = new TextGauge("message");
        backtrace = new TextGauge("backtrace");
        snitch = new Snitch(this);
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
     * The message of the error.
     *
     * @param message human readable error message.
     */
    public void message(String message) {
        this.message.set(message);
    }

    /**
     * Get a reference to associated snitch to get discrete metric values.
     *
     * @return the associate {@link Snitch}
     */
    public Snitch snitch() {
        return this.snitch;
    }

    /**
     * Stacktrace for Java.
     *
     * @param throwable The Java {@link Throwable} that contains the stacktrace to output
     */
    public void backtrace(Throwable throwable) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(byteArrayOutputStream)) {

            throwable.printStackTrace(printStream);
            String backtrace = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
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

    /**
     * The Jackson serializer.
     */
    public static class Serializer extends StdSerializer<ErrorWitness> {

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

    /**
     * The snitch for the errors. Used to retrieve discrete metric values.
     */
    public static class Snitch {
        private final ErrorWitness witness;

        Snitch(ErrorWitness witness) {
            this.witness = witness;
        }

        /**
         * Gets the error message
         * @return the error message
         */
        public String message() {
            return witness.message.getValue();
        }

        /**
         * Gets the error stack/back trace
         * @return the backtrace as a String
         */
        public String backtrace() {
            return witness.backtrace.getValue();
        }
    }
}
