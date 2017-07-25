# encoding: utf-8
java_import org.logstash.instrument.witness.Witness

module LogStash module Instrument
  class WrappedWriteClient
    def initialize(write_client, pipeline, plugin, logger)

      @logger = logger
      @write_client = write_client

      @pipeline_id = pipeline.pipeline_id.to_s
      plugin_type = plugin.class.plugin_type

      if plugin_type.eql? "input"
        @witness_plugin_events = Witness.instance.pipeline(@pipeline_id).inputs(plugin.id.to_s).events
      elsif plugin_type.eql? "filter"
        @witness_plugin_events = Witness.instance.pipeline(@pipeline_id).filters(plugin.id.to_s).events
      elsif plugin_type.eql? "output"
        @witness_plugin_events = Witness.instance.pipeline(@pipeline_id).outputs(plugin.id.to_s).events
      elsif plugin_type.eql? "codec"
        @witness_plugin_events = Witness.instance.pipeline(@pipeline_id).codecs(plugin.id.to_s).events
      end
      @witness_events = Witness.instance.events
      @witness_pipeline_events = Witness.instance.pipeline(@pipeline_id).events

      define_initial_metrics_values
    end

    def get_new_batch
      []
    end

    def push(event)
      begin
        increment_counters(1)
      start_time = java.lang.System.nano_time
        result = @write_client.push(event)
        report_execution_time(start_time)
      rescue => e
        @logger.error("Error while pushing event", :message => e.message, :backtrace => e.backtrace)
        raise # I don't think this actually gets handled anywhere
      end

      result
    end

    alias_method(:<<, :push)

    def push_batch(batch)
      begin
        increment_counters(batch.size)
      start_time = java.lang.System.nano_time
        result = @write_client.push_batch(batch)
        report_execution_time(start_time)
      rescue => e
        @logger.error("Error while pushing batch", :message => e.message, :backtrace => e.backtrace)
        raise # I don't think this actually gets handled anywhere
      end
      result
    end

    private

    def increment_counters(size)
      @witness_events.in(size)
      @witness_pipeline_events.in(size)
      @witness_plugin_events.out(size)
    end

    def report_execution_time(start_time)
      execution_time = (java.lang.System.nano_time - start_time) / 1_000_000
      @witness_events.queue_push_duration(execution_time)
      @witness_pipeline_events.queue_push_duration(execution_time)
      @witness_plugin_events.queue_push_duration(execution_time)
     end

    def define_initial_metrics_values
      @witness_events.in(0)
      @witness_pipeline_events.in(0)
      @witness_plugin_events.out(0)
      @witness_events.queue_push_duration(0)
      @witness_pipeline_events.queue_push_duration(0)
      @witness_plugin_events.queue_push_duration(0)
    end

  end
end end
