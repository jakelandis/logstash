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


        witness.pipelines().pipeline("foo").reload().failure();
        witness.pipelines().pipeline("bar").event().in();
        witness.pipelines().pipeline("bar").event().out();

        witness.pipelines().pipeline("baz").config().batchSize(20);
        witness.pipelines().pipeline("foo").config().deadLetterQueueEnabled(true);
    }
}