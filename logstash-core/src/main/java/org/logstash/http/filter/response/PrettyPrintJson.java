package org.logstash.http.filter.response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;
import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterInjector;
import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterModifier;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Jax-rs response filter to ensure that JSON is pretty printed if the ?pretty query parameter was requested
 */
@Provider
@Priority(100)
public class PrettyPrintJson implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        boolean shouldPrintPretty = requestContext.getUriInfo().getQueryParameters().containsKey("pretty");
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        Object contentType = headers == null ? "" : headers.getFirst(HttpHeaders.CONTENT_TYPE) == null ? "" : headers.getFirst(HttpHeaders.CONTENT_TYPE);
        boolean isJson = MediaType.APPLICATION_JSON.equals(contentType.toString());

        if (shouldPrintPretty && isJson) {
            ObjectWriterInjector.set(new ObjectWriterModifier() {
                @Override
                public ObjectWriter modify(EndpointConfigBase<?> e, MultivaluedMap<String, Object> r, Object v, ObjectWriter writer, JsonGenerator gen) throws IOException {
                    gen.useDefaultPrettyPrinter();
                    return writer;
                }
            });
        }
    }
}