package org.logstash.http.metrics;

import org.logstash.instrument.witness.stats.EventsWitness;
import org.logstash.instrument.witness.stats.PipelinesWitness;
import org.logstash.instrument.witness.stats.ReloadWitness;
import org.logstash.instrument.witness.stats.StatsWitness;

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
    public StatsWitness stats() {
        return StatsWitness.instance();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("reloads")
    public ReloadWitness reloads() {
        return StatsWitness.instance().reloads();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("events")
    public EventsWitness events() {
        return StatsWitness.instance().events();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("pipelines")
    public PipelinesWitness pipelines() {
        return StatsWitness.instance().pipelines();
    }



}
