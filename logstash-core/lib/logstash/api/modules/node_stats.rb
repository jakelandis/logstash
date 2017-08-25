# encoding: utf-8
require 'json'

java_import org.logstash.instrument.witness.Witness

#TODO: Add support for os, jvm, and process! (jake)
module LogStash
  module Api
    module Modules
      class NodeStats < ::LogStash::Api::Modules::Base

       get "/pipelines/:id?" do
          payload = pipeline_payload(params["id"])
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

        private
        def os_payload
        #  TODO: jake
        end

        def events_payload
          JSON.parse(Witness.instance.events.as_json)["events"]
        end

        def jvm_payload
       #   TODO: jake
        end

        def reloads_payload
          JSON.parse(Witness.instance.reloads.as_json)["reloads"]
        end

        def process_payload
          JSON.parse(Witness.instance.process.as_json)["process"]
        end

        def mem_payload
          #TODO: jake
        end

        def pipeline_payload(val = nil)
          if val.nil?
            JSON.parse(Witness.instance.pipelines.as_json)["pipelines"]
          else
            JSON.parse(Witness.instance.pipeline(val).as_json)["pipelines"]
          end
        end
      end
    end
  end
end
