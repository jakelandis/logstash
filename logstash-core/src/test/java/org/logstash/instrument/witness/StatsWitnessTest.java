package org.logstash.instrument.witness;

import org.junit.Test;

public class StatsWitnessTest {

    @Test
    public void test(){
        StatsWitness witness = StatsWitness.getInstance();
        witness.reload().failure();
        witness.event().in();
        witness.event().out();
        witness.event().filtered();


        witness.pipeline("foo").reload().failure();
        witness.pipeline("bar").event().in();
        witness.pipeline("bar").event().out();

        witness.pipeline("baz").config().batchSize(20);
        witness.pipeline("foo").config().deadLetterQueueEnabled(true);
    }
}