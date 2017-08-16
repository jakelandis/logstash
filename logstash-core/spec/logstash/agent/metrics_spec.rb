# encoding: utf-8
#
require "logstash/agent"
require_relative "../../support/helpers"
require_relative "../../support/matchers"
require_relative "../../support/mocks_classes"
require "spec_helper"

java_import org.logstash.instrument.witness.Witness

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

    Witness.setInstance(Witness.new)

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


  context "when starting the agent" do
    let(:snitch) { Witness.instance.reloads.snitch }
    it "initialize the instance reload metrics" do
      expect(snitch.successes).to eq(0)
      expect(snitch.failures).to eq(0)
    end
  end

  context "when we try to start one pipeline" do
    context "and it succeed" do
      let(:source_loader) do
        TestSourceLoader.new(pipeline_config)
      end

      let(:pipeline_name) { :main }
      let(:snitch) { Witness.instance.pipeline("main").reloads.snitch }

      context "global state" do
        let(:snitch) { Witness.instance.reloads.snitch } # global snitch

        it "success doesnt changes" do
          expect { subject.converge_state_and_update }.not_to change { snitch.successes }
        end

        it "failure doesn't change" do
          expect { subject.converge_state_and_update }.not_to change { snitch.failures }
        end
      end

      it "sets the failures to 0" do
        subject.converge_state_and_update
        expect(snitch.failures).to eq(0)
      end

      it "sets the successes to 0" do
        subject.converge_state_and_update
        expect(snitch.successes).to eq(0)
      end

      it "sets the `last_error` to nil" do
        subject.converge_state_and_update
        expect(snitch.error.snitch.message).to be_nil
        expect(snitch.error.snitch.backtrace).to be_nil
      end

      it "sets the `last_failure_timestamp` to nil" do
        subject.converge_state_and_update
        expect(snitch.last_failure_timestamp).to be_nil
      end

      it "sets the `last_success_timestamp` to nil" do
        subject.converge_state_and_update
        expect(snitch.last_success_timestamp).to be_nil
      end
    end

    context "and it fails" do
      let(:source_loader) do
        TestSourceLoader.new(bad_pipeline_config)
      end

      let(:pipeline_name) { :bad }
      let(:snitch) { Witness.instance.pipeline("bad").reloads.snitch }


      before do
        subject.converge_state_and_update
      end

      context "golbal state" do
        let(:snitch) { Witness.instance.reloads.snitch } # global snitch
        it "doesnt changes the global successes" do
          expect {subject.converge_state_and_update}.not_to change {snitch.successes}
        end

        it "doesn't change the failures" do
          expect {subject.converge_state_and_update}.to change {snitch.failures}.by(1)
        end
      end

      it "increments the pipeline failures" do
        expect { subject.converge_state_and_update }.to change { snitch.failures }.by(1)
      end

      it "sets the successes to 0" do
        subject.converge_state_and_update
        expect(snitch.successes).to eq(0)
      end

      it "records the `last_error`" do
        expect(snitch.error).to_not be_nil
      end

      it "records the `message` and the `backtrace`" do
        expect(snitch.error.snitch.message).to_not be_nil
        expect(snitch.error.snitch.backtrace).to_not be_nil
      end

      it "records the time of the last failure" do
        expect(snitch.last_failure_timestamp).to_not be_nil
      end

      it "initializes the `last_success_timestamp`" do
        expect(snitch.last_success_timestamp).to be_nil
      end
    end

    context "when we try to reload a pipeline" do
      before do
        # Running Pipeline to -> reload pipeline
        expect(subject.converge_state_and_update.success?).to be_truthy
      end

      let(:pipeline_name) { :main }
      let(:snitch) { Witness.instance.pipeline("main").reloads.snitch }

      context "and it succeed" do
        let(:source_loader) do
          TestSequenceSourceLoader.new(pipeline_config, update_pipeline_config)
        end

        context "golbal state" do
          let(:snitch) { Witness.instance.reloads.snitch }
          it "increments successes" do
            expect {subject.converge_state_and_update}.to change {snitch.successes}.by(1)
          end
        end

        it "increment the pipeline successes" do
          expect{ subject.converge_state_and_update }.to change { snitch.successes }.by(1)
        end

        it "record the `last_success_timestamp`" do
          expect(snitch.last_success_timestamp).to be_nil
          subject.converge_state_and_update
          expect(snitch.last_success_timestamp).not_to be_nil
        end
      end

      context "and it fails" do
        let(:source_loader) do
          TestSequenceSourceLoader.new(pipeline_config, bad_update_pipeline_config)
        end

        context "golbal state" do
          it "increments failures" do
            expect {subject.converge_state_and_update}.to change {snitch.failures}.by(1)
          end
        end

        it "increment the pipeline failures" do
          expect{ subject.converge_state_and_update }.to change { snitch.failures }.by(1)
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

      let(:snitch0) {Witness.instance.pipeline("main").events.snitch}
      let(:snitch1) {Witness.instance.pipeline("main").filters("test_filter").events.snitch}

      it "it clear previous metrics for removed" do

        expect(snitch0.in).to be < 1000
        expect(snitch1.in).to be < 1000

        # since the pipeline is async, it can actually take some time to have metrics recordings
        # the generator is chugging along in the background, so lets block until an arbitrary large number of events have been counted
        try(20) do
          sleep(1) # to avoid spamming the logs
          expect(snitch0.in).to be > 1000
          expect(snitch1.in).to be > 1000
        end
        expect(subject.converge_state_and_update.success?).to be_truthy

        try(20) do
          # The input generator is still going in the background, but the filter has been removed from the pipeline. This ensures that the data is flowing in, but not recorded by
          # the filter since it is no longer part of the pipeline.
          expect(snitch0.in).to be > 0
          # need to call the whole chain again since the snitch1 is a cached copy of the now defunct filter
          expect(Witness.instance.pipeline("main").filters("test_filter").events.snitch.in).to eq(0)
        end
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

      let(:snitch) {Witness.instance.pipeline("main").events.snitch}

      it "clear pipeline specific metric" do

        expect(snitch.in).to be < 1000

        # since the pipeline is async, it can actually take some time to have metrics recordings
        # the generator is chugging along in the background, so lets verify an arbitrary number of events have been counted
        try(20) do
          sleep(1) # to avoid spamming the logs
          expect(snitch.in).to be > 1000
        end
        expect(subject.converge_state_and_update.success?).to be_truthy

        expect(snitch.in).to eq(0)
      end
    end
  end
end
