# encoding: utf-8
java_import org.logstash.instrument.witness.Witness

#WARNING - DO NOT USE THIS CLASS FOR ANYTHING BUT INPUT PLUGIN TYPES.  Using this for other plugin types will result doubly counted metrics.
module LogStash module Instrument
  class WrappedWriteClient #todo: rename InputWrappedWriterClient
    def initialize(write_client, pipeline, metric, plugin, logger)
      @write_client = write_client
      @logger = logger

      pipeline_id = pipeline.pipeline_id.to_s.to_sym
      plugin_type = "#{plugin.class.plugin_type}s".to_sym

      @events_metrics = metric.namespace([:stats, :events])#todo: delete
      @pipeline_metrics = metric.namespace([:stats, :pipelines, pipeline_id, :events]) #todo: delete
      @plugin_events_metrics = metric.namespace([:stats, :pipelines, pipeline_id, :plugins, plugin_type, plugin.id.to_sym, :events]) #todo: delete

      @base_witness = Witness.getInstance()
      @pipeline_witness = Witness.getInstance().pipeline(pipeline_id.to_s)
      @plugin_witness = Witness.getInstance().pipeline(pipeline_id.to_s).input(plugin.id.to_s)


      define_initial_metrics_values
    end

    def get_new_batch
      @write_client.get_new_batch
    end

    def push(event)
      record_metric { @write_client.push(event) }
    end
    alias_method(:<<, :push)

    def push_batch(batch)
      record_metric(batch.size) { @write_client.push_batch(batch) }
    end

    private
    def record_metric(size = 1)
      begin
      @events_metrics.increment(:in, size) #todo: delete
      @pipeline_metrics.increment(:in, size) #todo: delete
      @plugin_events_metrics.increment(:out, size) #todo: delete

      @base_witness.event.in(size)
      @pipeline_witness.event.in(size)
      @plugin_witness.event.out(size)

      start_time = Time.now
      clock = @events_metrics.time(:queue_push_duration_in_millis) #todo: delete

      result = yield

      # Reuse the same values for all the endpoints to make sure we don't have skew in times.
      execution_time = clock.stop
      end_time = Time.now


      @pipeline_metrics.report_time(:queue_push_duration_in_millis, execution_time)
      @plugin_events_metrics.report_time(:queue_push_duration_in_millis, execution_time)

      @pipeline_witness.event.queuePushDuration((end_time - start_time)*1000)
      @plugin_witness.event.queuePushDuration((end_time - start_time)*1000)


      rescue => e
        @logger.error("Logstash failed recording input metrics", :exception => e.message, :backtrace => e.backtrace)
        raise e #testing shows this gets swallowed somewhere
      end

      result
    end

    def define_initial_metrics_values
      @events_metrics.increment(:in, 0) #todo: delete
      @pipeline_metrics.increment(:in, 0) #todo: delete
      @plugin_events_metrics.increment(:out, 0) #todo: delete

      @base_witness.event.in(0)
      @pipeline_witness.event.in(0)
      @plugin_witness.event.out(0)

      @events_metrics.report_time(:queue_push_duration_in_millis, 0)  #todo: delete
      @pipeline_metrics.report_time(:queue_push_duration_in_millis, 0)  #todo: delete
      @plugin_events_metrics.report_time(:queue_push_duration_in_millis, 0)  #todo: delete

      @base_witness.event.queuePushDuration(0)
      @pipeline_witness.event.queuePushDuration(0)
      @plugin_witness.event.queuePushDuration(0)
    end
  end
end end
