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

        witness.pipeline("test").plugin("foo").event().in();
        witness.pipeline("test").plugin("foo").event().duration(100l);
        witness.pipeline("test").plugin("foo").event().queuePushDuration(100l);
//
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(witness));
        System.out.println(witness.event().asJson());
        System.out.println(witness.asJson());
    }



}