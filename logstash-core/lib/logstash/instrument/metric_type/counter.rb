#encoding: utf-8
java_import org.logstash.instrument.metrics.counter.LongCounter
java_import org.logstash.instrument.witness.Witness
module LogStash
  module Instrument
    module MetricType
      class Counter < LongCounter


        def initialize(namespaces, key)
          super(key.to_s)
          @namespaces = namespaces
          @key = key
        end

        def execute(action, value = 1)
          adapt(@namespaces, @key, value)
          send(action, value)

        end

        def adapt(namespaces, key, value)

          puts "******************"


          witness = namespaces.to_witness
          puts witness.class.to_s
          puts key
          if key.eql? :in
            witness.in(value)
          elsif key.eql? :out
            witness.out(value)
          elsif key.eql? :filtered
            witness.filtered(value)
          elsif key.eql? :duration_in_millis
            witness.duration(value)
          elsif key.eql? :queue_push_duration_in_millis
            witness.queuePushDuration(value)
          end


        end

      end
    end;
  end;
end

# Returns an array of hashes, where the keys map can be mapped to witness methods and the value the argument
# [{:stats=>nil}, {:pipelines=>:main}, {:plugins=>nil}, {:inputs=>:"84edf87760a339a724b53cc86902480be4d60b94-1"}, {:events=>nil}]
class Array
  def to_witness

    index = 0
    witness = Witness.getInstance
    while index <= self.size-1
      current = self[index]
      # one argument
      if [:pipelines, :inputs, :outputs, :filters].include? current
        arg = self[index+1]
        if current.eql? :pipelines
          witness = witness.pipelines.pipeline(arg)
        elsif current.eql? :inputs
          witness = witness.input(arg)
        elsif current.eql? :outputs
          witness = witness.output(arg)
        elsif current.eql? :filters
          witness = witness.filters(arg)
        end
        index += 1
      else
        if current.eql? :reloads
          witness = witness.reload
        elsif current.eql? :events
          witness = witness.event
        elsif current.eql? :plugins
          witness = witness.plugins
        end
      end
      index += 1
    end
    witness

  end
end