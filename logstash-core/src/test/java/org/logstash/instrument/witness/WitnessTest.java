package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;


public class WitnessTest {

    @Test
    public void test() throws IOException {
        Witness witness = Witness.getInstance();

        witness.reload().error().message("foo");
        StackTraceElement[] a = new Throwable().getStackTrace();

        witness.reload().error().backtrace(a);
//[:stats, :pipelines, :main, :events]duration_in_millis
//        witness.pipeline("main").event().duration(100);
//
//
//        EventsWitness eventWitness = witness.pipeline("main").event();
//
//        System.out.println(eventWitness.asJson());
//
//        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(mapper.writeValueAsString(witness));
        //System.out.println(witness.asJson());


//        witness.reload().failure();
//        witness.event().in();
//        witness.event().out();
//        witness.event().filtered();
//
//
//
//        PipelineWitness a = witness.pipeline("foo");
//
//        a.reload().failure();
//        witness.pipeline("bar").event().in();
//        witness.pipeline("bar").event().out();
//
//        witness.pipeline("baz").config().batchSize(20);
//        witness.pipeline("foo").config().deadLetterQueueEnabled(true);
////
//        witness.pipeline("main").input("tcp").event().in();
//        witness.pipeline("main").input("tcp").event().in(2);
//        witness.pipeline("main").output("stdout").event().out(99);
//        witness.pipeline("test").input("pi2").event().in();
//        witness.pipeline("test").input("pi3").event().in();
//        witness.pipeline("test").input("pi").event().duration(100l);
//        witness.pipeline("test").input("pi").event().queuePushDuration(100l);
//
//        witness.pipeline("test").output("pi").addCustom(new CustomWitness());
//        witness.pipeline("test").output("pi").custom(CustomWitness.class).sayHi("hello world");
//        PluginWitness a = witness.pipeline("test").output("pi");
//
//        a.custom(CustomWitness.class).events().in();
        System.out.println(witness.asJson());

    }


    class CustomWitness
            implements SerializableWitness{

        String hiThere = " Hidey HoE!";

        EventsWitness ew = new EventsWitness();
        @Override
        public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStringField("hello", hiThere);
            ew.genJson(gen, provider);
        }

        public void sayHi(String hi){
            this.hiThere = hi;
        }

        public EventsWitness events() {
            return ew;
        }
    }
//    class CustomWitness2  implements SerializableWitness{
//
//        String bye ;
//        @Override
//        public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
//            gen.writeStringField("bye", bye);
//        }
//
//        public void bye(){
//            bye = "goodbye";
//        }
//    }


}