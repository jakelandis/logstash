package org.logstash.instrument.witness;

import org.logstash.instrument.metrics.gauge.GaugeMetric;

import java.io.IOException;

/**
 * Similar to the {@link java.util.function.Consumer} functional interface this is expected to operate via side effects. Differs from {@link java.util.function.Consumer} in that
 * this is stricter typed, and allows for a checked {@link IOException}.
 * @param <T> The type of {@link GaugeMetric} to serialize
 */
@FunctionalInterface
public interface GaugeSerializer <T extends GaugeMetric<?>>{

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void serialize(T t) throws IOException;

}
