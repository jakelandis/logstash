package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@JsonSerialize(using = Witness.Serializer.class)
final public class Witness implements SerializableWitness {

    private final ReloadWitness reloadWitness;
    private final EventsWitness eventsWitness;
    private final PipelinesWitness pipelinesWitness;


    private static Witness WITNESS = new Witness();

    public static Witness getInstance() {
        if(WITNESS == null){
            WITNESS = new Witness();
        }
        return WITNESS;
    }


    /**
     * Testing only
     */
    public void resetWitness(){
        WITNESS = new Witness();
    }

    private Witness() {
        this.reloadWitness = new ReloadWitness();
        this.eventsWitness = new EventsWitness();
        this.pipelinesWitness = new PipelinesWitness();

    }


    public ReloadWitness reload() {
        return reloadWitness;
    }


    public EventsWitness event() {
        return eventsWitness;
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    public PipelinesWitness pipelines(){
        return pipelinesWitness;
    }
    /**
     * TODO
     * Shortcut method for pipelines.pipeline(name)
     * @param name
     * @return
     */
    public PipelineWitness pipeline(String name) {
       return  pipelinesWitness.pipeline(name);

    }

    static class Serializer extends StdSerializer<Witness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(Witness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<Witness> t) {
            super(t);
        }

        @Override
        public void serialize(Witness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(Witness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            witness.event().genJson(gen, provider);
            witness.reload().genJson(gen, provider);
            witness.pipelinesWitness.genJson(gen, provider);
        }
    }



}
