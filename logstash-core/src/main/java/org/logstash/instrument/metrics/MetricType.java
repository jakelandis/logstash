package org.logstash.instrument.metrics;


public enum MetricType {

    COUNTER("counter"),

    GAUGE("gauge");

    //TODO: see how this actally used and see if i can be more specific here, like gauge/text or gauge/double

    private final String type;

    MetricType(final String type) {
        this.type = type;
    }

    public String asString() {
        return type;
    }

}
