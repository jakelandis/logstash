package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.io.StringWriter;

public interface SerializableWitness {

    void genJson(final JsonGenerator gen, SerializerProvider provider) throws IOException; 

    default String asJson() throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        StringWriter sw = new StringWriter();
        JsonGenerator gen = jsonFactory.createGenerator(sw);
        ObjectMapper mapper = new ObjectMapper(jsonFactory);
        gen.writeStartObject();
        genJson(gen, mapper.getSerializerProvider());
        gen.writeEndObject();
        gen.flush();
        sw.flush();
        return sw.toString();
    }
}
