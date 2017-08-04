# encoding: utf-8
require 'json'

java_import org.logstash.instrument.witness.stats.StatsWitness

module LogStash
  module Api
    module Modules
      class NodeStats < ::LogStash::Api::Modules::Base

        before do
          @stats = factory.build(:stats)

        end

        get "/pipelines/:id?" do
          payload = pipeline_payload(params["id"])
          halt(404) if payload.empty?
          respond_with(:pipelines => payload)
        end

        get "/v2/pipelines/:id?" do
          payload = pipeline_payload_v2(params["id"])
          halt(404) if payload.empty?
          respond_with(:pipelines => payload)
        end

        get "/?:filter?" do
          payload = {
            :jvm => jvm_payload,
            :process => process_payload,
            :events => events_payload,
            :pipelines => pipeline_payload,
            :reloads => reloads_payload,
            :os => os_payload
          }
          respond_with(payload, {:filter => params["filter"]})
        end

        get "/v2/?:filter?" do
          payload = {
              :jvm => jvm_payload,
              :process => process_payload,
              :events => events_payload_v2,
              :pipelines => pipeline_payload_v2,
              :reloads => reloads_payload_v2,
              :os => os_payload
          }
          respond_with(payload, {:filter => params["filter"]})
        end

        private
        def os_payload
          @stats.os
        end

        def events_payload
          @stats.events
        end

        def events_payload_v2
          JSON.parse(StatsWitness.getInstance.event.as_json)["events"]
        end

        def jvm_payload
          @stats.jvm
        end

        def reloads_payload
          @stats.reloads
        end

        def reloads_payload_v2
          JSON.parse(StatsWitness.getInstance.reload.as_json)["reloads"]
        end

        def process_payload
          @stats.process
        end

        def mem_payload
          @stats.memory
        end

        def pipeline_payload(val = nil)
          @stats.pipeline(val)
        end

        def pipeline_payload_v2(val = nil)
          if val.nil?
            JSON.parse(StatsWitness.getInstance.pipelines.as_json)["pipelines"]
          else
            JSON.parse(StatsWitness.getInstance.pipeline(val).as_json)["pipelines"]
          end
        end
      end
    end
  end
end
