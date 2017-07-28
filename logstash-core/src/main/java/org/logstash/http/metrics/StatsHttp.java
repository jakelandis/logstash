package org.logstash.http.metrics;

import org.logstash.instrument.witness.EventsWitness;
import org.logstash.instrument.witness.PipelinesWitness;
import org.logstash.instrument.witness.ReloadWitness;
import org.logstash.instrument.witness.Witness;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("_node/stats")
public class StatsHttp {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Witness stats() {
        return Witness.getInstance();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("reloads")
    public ReloadWitness reloads() {
        return Witness.getInstance().reload();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("events")
    public EventsWitness events() {
        return Witness.getInstance().event();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("pipelines")
    public PipelinesWitness pipelines() {
        return Witness.getInstance().pipelines();
    }



}
