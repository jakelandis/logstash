package org.logstash.instrument.witness.stats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.logstash.instrument.witness.SerializableWitness;

import java.io.IOException;


@JsonSerialize(using = StatsWitness.Serializer.class)
final public class StatsWitness implements SerializableWitness {

    private final ReloadWitness reloadWitness;
    private final EventsWitness eventsWitness;
    private final PipelinesWitness pipelinesWitness;

    private static StatsWitness WITNESS = new StatsWitness();

    public static StatsWitness getInstance() {
        if(WITNESS == null){
            WITNESS = new StatsWitness();
        }
        return WITNESS;
    }


    /**
     * Todo: delete ... this should not be needed if we are properly cleaning up on pipeline restart
     */
    public void resetWitness(){
        WITNESS = new StatsWitness();
    }

    private StatsWitness() {
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

    static class Serializer extends StdSerializer<StatsWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(StatsWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<StatsWitness> t) {
            super(t);
        }

        @Override
        public void serialize(StatsWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(StatsWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            witness.event().genJson(gen, provider);
            witness.reload().genJson(gen, provider);
            witness.pipelinesWitness.genJson(gen, provider);
        }
    }


}
