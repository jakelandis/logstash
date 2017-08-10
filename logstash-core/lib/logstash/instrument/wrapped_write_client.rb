# encoding: utf-8

java_import org.logstash.instrument.witness.Witness

module LogStash module Instrument
  class WrappedWriteClient
    def initialize(write_client, pipeline, plugin)
      @write_client = write_client

      @pipeline_id = pipeline.pipeline_id.to_s
      plugin_type = plugin.class.plugin_type

      if plugin_type.eql? "input"
        @witness_plugin = Witness.instance.pipeline(@pipeline_id).inputs(plugin.id.to_s)
      elsif plugin_type.eql? "filter"
        @witness_plugin = Witness.instance.pipeline(@pipeline_id).filters(plugin.id.to_s)
      elsif plugin_type.eql? "output"
        @witness_plugin = Witness.instance.pipeline(@pipeline_id).outputs(plugin.id.to_s)
      elsif plugin_type.eql? "codec"
        @witness_plugin = Witness.instance.pipeline(@pipeline_id).codecs(plugin.id.to_s)
      end

      define_initial_metrics_values
    end

    def get_new_batch
      @write_client.get_new_batch
    end

    def push(event)
      increment_counters(1)
      start_time = java.lang.System.current_time_millis
      result = @write_client.push(event)
      report_execution_time(start_time)
      result
    end

    alias_method(:<<, :push)

    def push_batch(batch)
      increment_counters(batch.size)
      start_time = java.lang.System.current_time_millis
      result = @write_client.push_batch(batch)
      report_execution_time(start_time)
      result
    end

    private

    def increment_counters(size)
      Witness.instance.events.in(size)
      Witness.instance.pipeline(@pipeline_id).events.in(size)
      @witness_plugin.events().out(size)
    end

    def report_execution_time(start_time)
      execution_time = java.lang.System.current_time_millis - start_time
      Witness.instance.events.queue_push_duration(execution_time)
      Witness.instance.pipeline(@pipeline_id).events.queue_push_duration(execution_time)
      @witness_plugin.events().queue_push_duration(execution_time)
     end

    def define_initial_metrics_values
      Witness.instance.events.in(0)
      Witness.instance.pipeline(@pipeline_id).events.in(0)
      @witness_plugin.events().out(0)
      Witness.instance.events.queue_push_duration(0)
      Witness.instance.pipeline(@pipeline_id).events.queue_push_duration(0)
      @witness_plugin.events().queue_push_duration(0)
    end

  end
end end
