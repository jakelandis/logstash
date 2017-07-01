# encoding: utf-8
java_import org.logstash.instrument.metrics.counter.CounterMetric

module LogStash module Instrument module MetricType
  class Counter < CounterMetric

    def initialize(namespaces, key, value = 0)
      super(namespaces, key.to_java.asJavaString, value)
    end

    def execute(action, value = 1)
      send(action, value)
    end

  end
end; end; end
