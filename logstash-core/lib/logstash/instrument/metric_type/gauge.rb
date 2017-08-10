# encoding: utf-8
require "logstash/instrument/metric_type/witness_adaptor"

java_import org.logstash.instrument.metrics.gauge.LazyDelegatingGauge

module LogStash module Instrument module MetricType
  class Gauge < LazyDelegatingGauge

    def initialize(namespaces, key)
      super(namespaces, key.to_s)
      @namespaces = namespaces
    end

    def execute(action, value = nil)
      send(action, value) unless @namespaces.first.eql? :stats
    end

  end
end; end; end

