package org.logstash.instrument.witness.stats;

import java.util.ArrayList;
import java.util.List;

final public class PluginWitness{

    private final List<String> namespaces;

    private final EventsWitness eventsWitness;


    PluginWitness(final List<String> parentNameSpace, String pluginName) {
        namespaces = new ArrayList<>(parentNameSpace);
        namespaces.add(pluginName);
        this.eventsWitness = new EventsWitness(namespaces);
    }

    public EventsWitness event() {
        return eventsWitness;
    }


}
