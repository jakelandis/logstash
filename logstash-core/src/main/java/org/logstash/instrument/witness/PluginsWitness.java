package org.logstash.instrument.witness;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A Witness for the set of plugins.
 */
public class PluginsWitness implements SerializableWitness {

    private final Map<String, PluginWitness> inputs;
    private final Map<String, PluginWitness> outputs;
    private final Map<String, PluginWitness> filters;
    private final Forgetter forgetter;
    private final static String KEY = "plugins";

    /**
     * Constructor.
     */
    public PluginsWitness() {

        this.inputs = new HashMap<>();
        this.outputs = new HashMap<>();
        this.filters = new HashMap<>();
        this.forgetter = new Forgetter(this);
    }

    /**
     * Gets the {@link PluginWitness} for the given id, creates the associated {@link PluginWitness} if needed
     * @param id the id of the input
     * @return the associated {@link PluginWitness} (for method chaining)
     */
    public PluginWitness inputs(String id) {
        return getPlugin(inputs, id);
    }

    /**
     * Gets the {@link PluginWitness} for the given id, creates the associated {@link PluginWitness} if needed
     * @param id the id of the output
     * @return the associated {@link PluginWitness} (for method chaining)
     */
    public PluginWitness outputs(String id) {
        return getPlugin(outputs, id);
    }

    /**
     * Gets the {@link PluginWitness} for the given id, creates the associated {@link PluginWitness} if needed
     * @param id the id of the filter
     * @return the associated {@link PluginWitness} (for method chaining)
     */
    public PluginWitness filters(String id) {
        return getPlugin(filters, id);
    }

    /**
     * Gets the {@link Forgetter} to help reset underlying metrics
     * @return The associated {@link Forgetter}
     */
    public Forgetter forget() {
        return forgetter;
    }

    /**
     * Gets or creates the {@link PluginWitness}
     * @param plugin the map of the plugin type.
     * @param id the id of the plugin
     * @return existing or new {@link PluginWitness}
     */
    private PluginWitness getPlugin(Map<String, PluginWitness> plugin, String id) {
        if (plugin.containsKey(id)) {
            return plugin.get(id);
        } else {
            PluginWitness pluginWitness = new PluginWitness(id);
            plugin.put(id, pluginWitness);
            return pluginWitness;
        }
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        new Serializer().innerSerialize(this, gen, provider);
    }

    /**
     * The Jackson serializer.
     */
    public static class Serializer extends StdSerializer<PluginsWitness> {

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

    /**
     * The forgetter for the Plugins witness.
     */
    public static class Forgetter {

        private final PluginsWitness witness;

        Forgetter(PluginsWitness witness) {
            this.witness = witness;
        }

        /**
         * Reset inputs, outputs, and filters.
         */
        public void all() {
            witness.inputs.clear();
            witness.outputs.clear();
            witness.filters.clear();
        }

    }
}
