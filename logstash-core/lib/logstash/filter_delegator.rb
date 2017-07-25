# encoding: utf-8
#
module LogStash
  class FilterDelegator
    extend Forwardable
    DELEGATED_METHODS = [
      :register,
      :close,
      :threadsafe?,
      :do_close,
      :do_stop,
      :periodic_flush,
      :reloadable?
    ]
    def_delegators :@filter, *DELEGATED_METHODS

    attr_reader :id

    def initialize(logger, klass, witness_plugin, execution_context, plugin_args)
      @logger = logger
      @klass = klass
      @id = plugin_args["id"]
      @filter = klass.new(plugin_args)

      @filter.execution_context = execution_context

      @witness_events = witness_plugin.events
      witness_plugin.name(config_name)

      @filter.metric = witness_plugin.custom

      # Not all the filters will do bufferings
      define_flush_method if @filter.respond_to?(:flush)
    end

    def config_name
      @klass.config_name
    end

    def multi_filter(events)
      @witness_events.in(events.size)

      start_time = java.lang.System.current_time_millis
      new_events = @filter.multi_filter(events)
      @witness_events.duration(java.lang.System.current_time_millis - start_time)

      # There is no guarantee in the context of filter
      # that EVENTS_IN == EVENTS_OUT, see the aggregates and
      # the split filter
      c = new_events.count { |event| !event.cancelled? }
      @witness_events.out(c) if c > 0
      new_events
    end

    private
    def define_flush_method
      define_singleton_method(:flush) do |options = {}|
        # we also need to trace the number of events
        # coming from a specific filters.
        new_events = @filter.flush(options)

        # Filter plugins that does buffering or spooling of events like the
        # `Logstash-filter-aggregates` can return `NIL` and will flush on the next flush ticks.
        @witness_events.out(new_events.size) if new_events && new_events.size > 0
        new_events
      end
    end
  end
end
