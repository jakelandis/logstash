# encoding: utf-8
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

# # encoding: utf-8
# require "logstash/instrument/metric_type/base"
# require "concurrent/atomic_reference/mutex_atomic"
# require "logstash/json"
#
# module LogStash module Instrument module MetricType
#   class Gauge < Base
#     def initialize(namespaces, key)
#       super(namespaces, key)
#
#       @gauge = Concurrent::MutexAtomicReference.new()
#     end
#
#     def execute(action, value=nil)
#       @gauge.set(value)
#     end
#
#     def set(value)
#       @gauge.set(value)
#     end
#
#     def value
#       @gauge.get
#     end
#
#     def java_metric
#       @gauge
#     end
#   end
# end; end; end
