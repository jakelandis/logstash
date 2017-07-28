package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.Test;

import java.io.IOException;

public class WitnessTest {

    @Test
    public void test() throws IOException {
        Witness witness = Witness.getInstance();
        witness.reload().failure();
        witness.event().in();
        witness.event().out();
        witness.event().filtered();



        PipelineWitness a = witness.pipeline("foo");

        a.reload().failure();
        witness.pipeline("bar").event().in();
        witness.pipeline("bar").event().out();

        witness.pipeline("baz").config().batchSize(20);
        witness.pipeline("foo").config().deadLetterQueueEnabled(true);

        witness.pipeline("a").reload().reset();
        witness.pipeline("test").input("pi").event().in();
        witness.pipeline("test").input("pi").event().in(4);
        witness.pipeline("test").input("pi2").event().in();
        witness.pipeline("test").input("pi3").event().in();
        witness.pipeline("test").input("pi").event().duration(100l);
        witness.pipeline("test").input("pi").event().queuePushDuration(100l);

        witness.pipeline("test").output("pi").addCustom(new CustomWitness());
        witness.pipeline("test").output("pi").custom(CustomWitness.class).hiThere();
        witness.pipeline("test").filter("pi").addCustom(new CustomWitness2());
        witness.pipeline("test").filter("pi").custom(CustomWitness2.class).bye();
        witness.pipeline("test").filter("pi").id("fsadfadsfadsf");

      //  pipeline_scoped_metric = metric.namespace([:stats, :pipelines, pipeline_id.to_s.to_sym, :plugins]) #todo: delete

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
        System.out.println(mapper.writeValueAsString(witness.pipeline("test").output("pi")));
        System.out.println(mapper.writeValueAsString(witness.pipelines().pipeline("test").output("pi")));
        System.out.println(mapper.writeValueAsString(witness.pipelines().pipeline("test").plugins().output("pi")));

    }


    class CustomWitness
            implements SerializableWitness{

        String hiThere = " Hidey HoE!";
        @Override
        public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStringField("hello", hiThere);
        }

        public void hiThere(){
            hiThere = "I am here!";
        }
    }
    class CustomWitness2  implements SerializableWitness{

        String bye ;
        @Override
        public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStringField("bye", bye);
        }

        public void bye(){
            bye = "goodbye";
        }
    }


}