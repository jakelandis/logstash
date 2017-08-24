package org.logstash.instrument.witness.jvm;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.logstash.instrument.witness.SerializableWitness;
import org.logstash.instrument.witness.schedule.ScheduledWitness;

import java.io.IOException;

public class JvmWitness implements ScheduledWitness, SerializableWitness {


    @Override
    public void refresh() {
        System.out.println("Refreshing!!");
        System.out.println(Thread.currentThread().getName());
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {

    }

}
