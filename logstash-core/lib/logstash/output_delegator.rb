require "logstash/output_delegator_strategy_registry"

require "logstash/output_delegator_strategies/shared"
require "logstash/output_delegator_strategies/single"
require "logstash/output_delegator_strategies/legacy"

java_import org.logstash.instrument.witness.Witness

module LogStash class OutputDelegator
  attr_reader :metric, :metric_events, :strategy, :namespaced_metric, :metric_events, :id

  def initialize(logger, output_class, metric, execution_context, strategy_registry, plugin_args, pipeline_name)
    @logger = logger
    @output_class = output_class
    @metric = metric
    @id = plugin_args["id"]


    raise ArgumentError, "No strategy registry specified" unless strategy_registry
    raise ArgumentError, "No ID specified! Got args #{plugin_args}" unless id

    @namespaced_metric = metric.namespace(id.to_sym) #todo: delete
    @namespaced_metric.gauge(:name, config_name) #todo: delete
    @metric_events = @namespaced_metric.namespace(:events) #todo: delete
    @plugin_witness = Witness.getInstance().pipeline(pipeline_name).output(config_name).id(@id);

    @strategy = strategy_registry.
                  class_for(self.concurrency).
                  new(@logger, @output_class, @namespaced_metric, execution_context, plugin_args)
  end

  def config_name
    @output_class.config_name
  end

  def reloadable?
    @output_class.reloadable?
  end

  def concurrency
    @output_class.concurrency
  end

  def register
    @strategy.register
  end

  def multi_receive(events)
    @metric_events.increment(:in, events.length) #todo: delete
    @plugin_witness.event.in(events.length)
    start_time = Time.now
    clock = @metric_events.time(:duration_in_millis) #todo: delete
    @strategy.multi_receive(events)
    clock.stop #todo: delete
    end_time = Time.now
    @plugin_witness.event.duration((end_time - start_time)*1000)
    @metric_events.increment(:out, events.length) #todo: delete
    @plugin_witness.event.out(events.length)
  end

  def do_close
    @strategy.do_close
  end
end; end
