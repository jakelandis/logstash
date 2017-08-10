# encoding: utf-8
require "logstash/instrument/metric_type/witness_adaptor"

java_import org.logstash.instrument.metrics.counter.LongCounter

module LogStash module Instrument module MetricType
  class Counter < LongCounter

    def initialize(namespaces, key)
      super(key.to_s)
      @namespaces = namespaces
    end

    def execute(action, value = 1)
      send(action, value) unless @namespaces.first.eql? :stats
    end

  end
end; end; end

