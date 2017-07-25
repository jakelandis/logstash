package org.logstash.http.metrics;

import org.logstash.instrument.witness.EventsWitness;
import org.logstash.instrument.witness.PipelinesWitness;
import org.logstash.instrument.witness.ReloadWitness;
import org.logstash.instrument.witness.Witness;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Jax-Rs resource. Handles http requests at the _node/stats and it's children.
 */
@Path("_node/stats")
public class StatsHttp {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Witness stats() {
        return Witness.instance();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("reloads")
    public ReloadWitness reloads() {
        return Witness.instance().reloads();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("events")
    public EventsWitness events() {
        return Witness.instance().events();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("pipelines")
    public PipelinesWitness pipelines() {
        return Witness.instance().pipelines();
    }



}
