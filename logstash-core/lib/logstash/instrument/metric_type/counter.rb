# encoding: utf-8
require "logstash/instrument/metric_type/witness_adaptor"

java_import org.logstash.instrument.metrics.counter.LongCounter

module LogStash module Instrument module MetricType
  class Counter < LongCounter

    def initialize(namespaces, key)
      super(key.to_s)
      @namespaces = namespaces
      @key = key
    end

    def execute(action, value = 1)
      if (@namespaces.first.eql? :stats)
        WitnessAdaptor.adapt(@namespaces, @key, value)
      end
      send(action, value)
    end

  end
end; end; end

