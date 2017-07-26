package org.logstash.instrument.witness;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.logstash.instrument.witness.stats.PipelineWitness;
import org.logstash.instrument.witness.stats.StatsWitness;

import java.io.IOException;

public class StatsSerializableWitnessTest {

    @Test
    public void test() throws IOException {
        StatsWitness witness = StatsWitness.getInstance();
//        witness.reload().failure();
        witness.event().in();
        witness.event().out();
        witness.event().filtered();



        PipelineWitness a = witness.pipeline("foo");

        a.reload().failure();
        witness.pipeline("bar").event().in();
        witness.pipeline("bar").event().out();

        witness.pipeline("baz").config().batchSize(20);
        witness.pipeline("foo").config().deadLetterQueueEnabled(true);

        witness.pipeline("test").plugin("pi").event().in();
        witness.pipeline("test").plugin("pi").event().duration(100l);
        witness.pipeline("test").plugin("pi").event().queuePushDuration(100l);
//
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(witness));
       mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(witness.event()));
        mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(witness.pipeline("foo")));
        System.out.println(witness.event().asJson());
        System.out.println(witness.asJson());
        System.out.println(witness.pipeline("foo").asJson());

        mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(witness.reload()));

        mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(witness.pipeline("test").plugin("pi")));

    }



}