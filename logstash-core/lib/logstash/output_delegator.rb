require "logstash/output_delegator_strategy_registry"
require "logstash/output_delegator_strategies/shared"
require "logstash/output_delegator_strategies/single"
require "logstash/output_delegator_strategies/legacy"

module LogStash class OutputDelegator
  attr_reader :strategy, :id

  def initialize(logger, output_class, witness_plugin, execution_context, strategy_registry, plugin_args)
    @logger = logger
    @output_class = output_class
    @id = plugin_args["id"]

    raise ArgumentError, "No strategy registry specified" unless strategy_registry
    raise ArgumentError, "No ID specified! Got args #{plugin_args}" unless id

    witness_plugin.name(config_name)
    @witness_plugin_events = witness_plugin.events
    @strategy = strategy_registry.
                  class_for(self.concurrency).
                  new(@logger, @output_class, witness_plugin, execution_context, plugin_args)
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
    @witness_plugin_events.in(events.length)
    start_time = java.lang.System.current_time_millis
    @strategy.multi_receive(events)
    @witness_plugin_events.duration(java.lang.System.current_time_millis - start_time)
    @witness_plugin_events.out(events.length)
  end

  def do_close
    @strategy.do_close
  end
end; end
