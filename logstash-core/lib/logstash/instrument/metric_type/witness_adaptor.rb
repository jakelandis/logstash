java_import org.logstash.instrument.witness.Witness

module LogStash
  module Instrument
    class WitnessAdaptor

      def self.adapt(namespaces, key, value)


        witness = namespaces.to_witness
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
        elsif key.eql? :name
          witness.name(value)
        elsif key.eql? :successes
          witness.success(value)
        elsif key.eql? :failures
          witness.failure(value)
        elsif key.eql? :workers
          witness.workers(value)
        elsif key.eql? :batch_size
          witness.batchSize(value)
        elsif key.eql? :batch_delay
          witness.batchDelay(value)
        elsif key.eql? :config_reload_automatic
          witness.configReloadAutomatic(value)
        elsif key.eql? :config_reload_interval
          witness.configReloadInterval(value)
        elsif key.eql? :dead_letter_queue_enabled
          witness.deadLetterQueueEnabled(value)
        elsif key.eql? :type
          witness.type(value)
        else
          puts "HEY! you missed me!!!: " + namespaces.to_s + ":" + key.to_s #todo: replace with real message
        end

      end

    end
  end
end

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
      else #no arguments
        if current.eql? :reloads
          witness = witness.reload
        elsif current.eql? :events
          witness = witness.event
        elsif current.eql? :plugins
          witness = witness.plugins
        elsif current.eql? :config
          witness = witness.config
        elsif current.eql? :queue
          witness = witness.queue
        end
      end
      index += 1
    end
    witness
  end
end
