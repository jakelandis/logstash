package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PluginsWitness implements SerializableWitness  {

    private final Map<String, PluginWitness> inputs;
    private final Map<String, PluginWitness> outputs;
    private final Map<String, PluginWitness> filters;
    private final static String KEY = "plugins";


    public PluginsWitness() {

        this.inputs = new HashMap<>();
        this.outputs = new HashMap<>();
        this.filters = new HashMap<>();
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    public PluginWitness input(String name) {
        return getPlugin(inputs, name);
    }

    public PluginWitness output(String name) {
        return getPlugin(outputs, name);
    }

    public PluginWitness filter(String name) {
        return getPlugin(filters, name);
    }

    private PluginWitness getPlugin(Map<String, PluginWitness> plugin, String name){
        if (plugin.containsKey(name)) {
            return plugin.get(name);
        } else {
            PluginWitness pluginWitness = new PluginWitness(name);
            plugin.put(name, pluginWitness);
            return pluginWitness;
        }
    }


    static class Serializer extends StdSerializer<PluginsWitness> {

        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(PluginsWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<PluginsWitness> t) {
            super(t);
        }

        @Override
        public void serialize(PluginsWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(PluginsWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObjectFieldStart(KEY);

            serializePlugins("inputs", witness.inputs, gen, provider);
            serializePlugins("filters", witness.filters, gen, provider);
            serializePlugins("outputs", witness.outputs, gen, provider);

            gen.writeEndObject();
        }

        private void serializePlugins(String key, Map<String, PluginWitness> plugin, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeArrayFieldStart(key);
            for (Map.Entry<String, PluginWitness> entry : plugin.entrySet()) {
                gen.writeStartObject();
                entry.getValue().genJson(gen, provider);
                gen.writeEndObject();
            }
            gen.writeEndArray();

        }
    }
}
