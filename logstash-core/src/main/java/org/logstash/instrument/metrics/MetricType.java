package org.logstash.instrument.metrics;


import java.util.EnumSet;

public enum MetricType {

    COUNTER_LONG("counter/long"),
    COUNTER_DOUBLE("counter/double"),

    GAUGE_TEXT("gauge/text"),
    GAUGE_BOOLEAN("gauge/boolean"),
    GAUGE_NUMERIC("gauge/numeric"),
    GAUGE_UNKNOWN("gauge/unknown"),
    GAUGE_RUBYHASH("gauge/rubyhash")


    ;

    //TODO: see how this actally used and see if i can be more specific here, like gauge/text or gauge/double

    private final String type;

    MetricType(final String type) {
        this.type = type;
    }

    public String asString() {
        return type;
    }

    public static MetricType fromString(String s){
        return EnumSet.allOf(MetricType.class).stream().filter(e -> e.asString().equalsIgnoreCase(s)).findFirst().orElse(null);
    }

}
