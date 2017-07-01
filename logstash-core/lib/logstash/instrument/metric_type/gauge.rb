# encoding: utf-8
require "logstash/instrument/metric_type/base"
require "concurrent/atomic_reference/mutex_atomic"
require "logstash/json"
java_import org.logstash.instrument.metrics.gauge.LazyDelegatingGauge
module LogStash module Instrument module MetricType
  class Gauge < LazyDelegatingGauge
    def initialize(namespaces, key)
      super(namespaces, key.to_java.asJavaString)
    end

    def execute(action, value = nil)
      send(action, value)
    end

  end
end; end; end
