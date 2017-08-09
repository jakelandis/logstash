package org.logstash.instrument.witness;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Unit tests for {@link PipelineWitness}
 */
public class PipelineWitnessTest {

    private PipelineWitness witness;

    @Before
    public void setup(){
        witness = new PipelineWitness("default");
    }


    @Test
    public void testNotNull(){
        assertThat(witness.inputs("123")).isNotNull();
        assertThat(witness.filters("456")).isNotNull();
        assertThat(witness.outputs("789")).isNotNull();
        assertThat(witness.events()).isNotNull();
        assertThat(witness.plugins()).isNotNull();
        assertThat(witness.queue()).isNotNull();
        assertThat(witness.config()).isNotNull();
        assertThat(witness.reloads()).isNotNull();
    }

    @Test
    public void testForget(){
        witness.inputs("123").events().in(99);
        witness.filters("456").events().in(98);
        witness.outputs("789").events().in(97);
        assertThat(witness.inputs("123").events().snitch().in()).isEqualTo(99);
        assertThat(witness.filters("456").events().snitch().in()).isEqualTo(98);
        assertThat(witness.outputs("789").events().snitch().in()).isEqualTo(97);

        witness.events().in(99);
        witness.events().filtered(98);
        witness.events().out(97);
        assertThat(witness.events().snitch().in()).isEqualTo(99);
        assertThat(witness.events().snitch().filtered()).isEqualTo(98);
        assertThat(witness.events().snitch().out()).isEqualTo(97);

        witness.queue().type("memory");

        witness.forget().partial();

        assertThat(witness.inputs("123").events().snitch().in())
                .isEqualTo(witness.filters("456").events().snitch().in())
                .isEqualTo(witness.outputs("789").events().snitch().in())
                .isEqualTo(witness.events().snitch().in())
                .isEqualTo(witness.events().snitch().filtered())
                .isEqualTo(witness.events().snitch().out())
                .isEqualTo(0);

        assertThat(witness.queue().snitch().type()).isEqualTo("memory");
    }

    @Test
    public void testAsJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        assertThat(mapper.writeValueAsString(witness)).isEqualTo(witness.asJson());
    }

    @Test
    public void testSerializeEmpty() throws Exception {
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"default\":{\"plugins\":{\"inputs\":[],\"filters\":[],\"outputs\":[]},\"reloads\":{\"last_error\":{},\"last_success_timestamp\":null," +
                "\"last_failure_timestamp\":null},\"queue\":{}}}");
    }

    @Test
    public void testSerializeEvents() throws Exception{
        witness.events().in(99);
        String json = witness.asJson();
        assertThat(json).contains("99");
        witness.forget().partial();
        json = witness.asJson();
        //events are forgotten
        assertThat(json).doesNotContain("99");
    }

    @Test
    public void testSerializePlugins() throws Exception{
        witness.inputs("aaa");
        witness.filters("bbb");
        witness.outputs("ccc");
        String json = witness.asJson();
        assertThat(json).contains("aaa").contains("bbb").contains("ccc");
        witness.forget().partial();
        json = witness.asJson();
        //plugins are forgotten
        assertThat(json).doesNotContain("aaa").doesNotContain("bbb").doesNotContain("ccc");
    }

    @Test
    public void testSerializeReloads() throws Exception{
        witness.reloads().successes(98);
        String json = witness.asJson();
        assertThat(json).contains("98");
        witness.forget().partial();
        json = witness.asJson();
        //reloads should not be forgotten
        assertThat(json).contains("98");
    }


    @Test
    public void testSerializeQueue() throws Exception{
        witness.queue().type("quantum");
        String json = witness.asJson();
        assertThat(json).contains("quantum");
        witness.forget().partial();
        json = witness.asJson();
        //queue's are not forgotten
        assertThat(json).contains("quantum");
    }

}