# encoding: utf-8
#
require "logstash/agent"
require_relative "../../support/helpers"
require_relative "../../support/matchers"
require_relative "../../support/mocks_classes"
require "spec_helper"

java_import org.logstash.instrument.witness.Witness

# Just make the tests a bit shorter to write and
# assert, I will keep theses methods here for easier understanding.
def mval(*path_elements)
  hash = mhash(*path_elements)
  hash[path_elements[-2].to_s][path_elements[-1].to_s]
end

def mhash(*path_elements)
  JSON.parse(path_elements.to_witness.as_json)
end


describe LogStash::Agent do
  # by default no tests uses the auto reload logic
  let(:agent_settings) { mock_settings("config.reload.automatic" => false) }
  let(:pipeline_settings) { { "pipeline.reloadable" => true } }

  let(:pipeline_config) { mock_pipeline_config(:main, "input { generator {} } filter { mutate { id => 'test_filter' add_tag => 'hello world' }} output { null {} }",
                                               pipeline_settings) }
  let(:update_pipeline_config) { mock_pipeline_config(:main, "input { generator { id => 'new' } } output { null {} }", pipeline_settings) }
  let(:bad_update_pipeline_config) { mock_pipeline_config(:main, "hooo }", pipeline_settings) }

  let(:new_pipeline_config) { mock_pipeline_config(:new, "input { generator {} } output { null {} }", pipeline_settings) }
  let(:bad_pipeline_config) { mock_pipeline_config(:bad, "hooo }", pipeline_settings) }
  let(:second_bad_pipeline_config) { mock_pipeline_config(:second_bad, "hooo }", pipeline_settings) }

  let(:source_loader) do
    TestSourceLoader.new([])
  end



  subject { described_class.new(agent_settings, source_loader) }

  before :each do
    # This MUST run first, before `subject` is invoked to ensure clean state
    clear_data_dir

    Witness.getInstance.resetWitness
    # TODO(ph) until we decouple the webserver from the agent
    # we just disable these calls
    allow(subject).to receive(:start_webserver).and_return(false)
    allow(subject).to receive(:stop_webserver).and_return(false)
  end

  # Lets make sure we stop all the running pipeline for every example
  # so we don't have any rogue pipeline in the background
  after :each do
    subject.shutdown
  end

  let(:metric) { subject.metric.collector.snapshot_metric.metric_store }

  context "when starting the agent" do
    it "initialize the instance reload metrics" do
      expect(mval(:stats, :reloads, :successes)).to eq(0)
      expect(mval(:stats, :reloads, :failures)).to eq(0)
    end
  end

  context "when we try to start one pipeline" do
    context "and it succeed" do
      let(:source_loader) do
        TestSourceLoader.new(pipeline_config)
      end

      let(:pipeline_name) { :main }

      it "doesnt changes the global successes" do
        expect { subject.converge_state_and_update }.not_to change { mval(:stats, :reloads, :successes) }
      end

      it "doesn't change the failures" do
        expect { subject.converge_state_and_update }.not_to change { mval(:stats, :reloads, :failures) }
      end

      it "sets the failures to 0" do
        subject.converge_state_and_update
        expect(mval(:stats, :pipelines, pipeline_name, :reloads, :failures)).to eq(0)
      end

      it "sets the successes to 0" do
        subject.converge_state_and_update
        expect(mval(:stats, :pipelines, pipeline_name, :reloads, :successes)).to eq(0)
      end

      it "sets the `last_error` to nil" do
        subject.converge_state_and_update
        expect(mval(:stats, :pipelines, pipeline_name, :reloads, :last_error)).to be_empty
      end

      it "sets the `last_failure_timestamp` to nil" do
        subject.converge_state_and_update
        expect(mval(:stats, :pipelines, :main, :reloads, :last_failure_timestamp)).to be_nil
      end

      it "sets the `last_success_timestamp` to nil" do
        subject.converge_state_and_update
        expect(mval(:stats, :pipelines, pipeline_name, :reloads, :last_success_timestamp)).to be_nil
      end
    end

    context "and it fails" do
      let(:source_loader) do
        TestSourceLoader.new(bad_pipeline_config)
      end

      let(:pipeline_name) { :bad }

      before do
        subject.converge_state_and_update
      end

      it "doesnt changes the global successes" do
        expect { subject.converge_state_and_update }.not_to change { mval(:stats, :reloads, :successes)}
      end

      it "doesn't change the failures" do
        expect { subject.converge_state_and_update }.to change { mval(:stats, :reloads, :failures) }.by(1)
      end

      it "increments the pipeline failures" do
        expect { subject.converge_state_and_update }.to change { mval(:stats, :pipelines, pipeline_name, :reloads, :failures) }.by(1)
      end

      it "sets the successes to 0" do
        subject.converge_state_and_update
        expect(mval(:stats, :pipelines, pipeline_name, :reloads, :successes)).to eq(0)
      end

      it "increase the global failures" do
        expect { subject.converge_state_and_update }.to change { mval(:stats, :reloads, :failures) }
      end

      it "records the `last_error`" do
        expect(mval(:stats, :pipelines, pipeline_name, :reloads, :last_error)).to_not be_nil
      end

      it "records the `message` and the `backtrace`" do
        expect(mval(:stats, :pipelines, pipeline_name, :reloads, :last_error)[:message.to_s]).to_not be_nil
        expect(mval(:stats, :pipelines, pipeline_name, :reloads, :last_error)[:backtrace.to_s]).to_not be_nil
      end

      it "records the time of the last failure" do
        expect(mval(:stats, :pipelines, pipeline_name, :reloads, :last_failure_timestamp)).to_not be_nil
      end

      it "initializes the `last_success_timestamp`" do
        expect(mval(:stats, :pipelines, pipeline_name, :reloads, :last_success_timestamp)).to be_nil
      end
    end

    context "when we try to reload a pipeline" do
      before do
        # Running Pipeline to -> reload pipeline
        expect(subject.converge_state_and_update.success?).to be_truthy
      end

      let(:pipeline_name) { :main }

      context "and it succeed" do
        let(:source_loader) do
          TestSequenceSourceLoader.new(pipeline_config, update_pipeline_config)
        end

        it "increments the global successes" do
          expect { subject.converge_state_and_update }.to change { mval(:stats, :reloads, :successes) }.by(1)
        end

        it "increment the pipeline successes" do
          expect{ subject.converge_state_and_update }.to change { mval(:stats, :pipelines, pipeline_name, :reloads, :successes) }.by(1)
        end

        it "record the `last_success_timestamp`" do
          expect(mval(:stats, :pipelines, pipeline_name, :reloads, :last_success_timestamp)).to be_nil
          subject.converge_state_and_update
          expect(mval(:stats, :pipelines, pipeline_name, :reloads, :last_success_timestamp)).not_to be_nil
        end
      end

      context "and it fails" do
        let(:source_loader) do
          TestSequenceSourceLoader.new(pipeline_config, bad_update_pipeline_config)
        end

        it "increments the global failures" do
          expect { subject.converge_state_and_update }.to change { mval(:stats, :reloads, :failures) }.by(1)
        end

        it "increment the pipeline failures" do
          expect{ subject.converge_state_and_update }.to change { mval(:stats, :pipelines, pipeline_name, :reloads, :failures) }.by(1)
        end
      end
    end

    context "when we successfully reload a pipeline" do
      let(:source_loader) do
        TestSequenceSourceLoader.new(pipeline_config, update_pipeline_config)
      end

      before do
        expect(subject.converge_state_and_update.success?).to be_truthy
      end

      it "it clear previous metrics for removed" do

        expect {mhash(:stats, :pipelines, :main, :plugins, :filters)}.not_to raise_error, "Filters stats should exist"

        # since the pipeline is async, it can actually take some time to have metrics recordings
        # the generator is chugging along in the background, so lets block until an arbitrary large number of events have been counted
        try(20) do
          sleep(1) # to avoid spamming the logs
          expect(mval(:stats, :pipelines, :main, :plugins, :filters, :test_filter, :events, :in)).to be > 100
        end
        expect(subject.converge_state_and_update.success?).to be_truthy


        # TODO: is this acceptable behavior change?  https://github.com/elastic/logstash/issues/7788#issuecomment-320044201
        # TODO: ....or should we support a remove plugin via the witness plugins ?
        # Ensure that the value is not changing
        value = mval(:stats, :pipelines, :main, :plugins, :filters, :test_filter, :events, :in)
        10.times {
          new_value = mval(:stats, :pipelines, :main, :plugins, :filters, :test_filter, :events, :in)
          expect(value).to eq(new_value)
          sleep(0.5)
        }

      end
    end

    context "when we stop a pipeline" do
      let(:source_loader) do
        TestSequenceSourceLoader.new(pipeline_config, [])
      end

      before do
        # Running Pipeline to -> reload pipeline
        expect(subject.converge_state_and_update.success?).to be_truthy
      end

      it "clear pipeline specific metric" do

        expect {mhash(:stats, :pipelines, :main, :events)}.not_to raise_error, "Events pipeline stats should exist"
        expect {mhash(:stats, :pipelines, :main, :plugins)}.not_to raise_error, "Plugins pipeline stats should exist"
        # since the pipeline is async, it can actually take some time to have metrics recordings
        # the generator is chugging along in the background, so lets verify an arbitrary number of events have been counted
        try(20) do
          expect(mval(:stats, :pipelines, :main, :events, :in)).to be > 100
        end

        expect(subject.converge_state_and_update.success?).to be_truthy

        # We do not have to retry here, since stopping a pipeline is a blocking operation
        # Ensure that the value is not changing
        value = mval(:stats, :pipelines, :main, :events, :in)
        10.times {
          new_value = mval(:stats, :pipelines, :main, :events, :in)
          expect(value).to eq(new_value)
          sleep(0.5)
        }
      end
    end
  end
end
