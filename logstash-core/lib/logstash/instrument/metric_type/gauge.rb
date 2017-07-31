# encoding: utf-8
require "logstash/instrument/metric_type/witness_adaptor"

java_import org.logstash.instrument.metrics.gauge.LazyDelegatingGauge

module LogStash module Instrument module MetricType
  class Gauge < LazyDelegatingGauge

    def initialize(namespaces, key)
      super(namespaces, key.to_s)
      @namespaces = namespaces
      @key = key
    end

    def execute(action, value = nil)
      if (@namespaces.first.eql? :stats)
        WitnessAdaptor.adapt(@namespaces, @key, value)
      end
      send(action, value)
    end

  end
end; end; end

