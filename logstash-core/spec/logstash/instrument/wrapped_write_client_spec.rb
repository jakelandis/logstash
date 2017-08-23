# encoding: utf-8
require "logstash/instrument/wrapped_write_client"
require "logstash/util/wrapped_synchronous_queue"
require "logstash/event"
require_relative "../../support/mocks_classes"
require "spec_helper"

describe LogStash::Instrument::WrappedWriteClient do

  before do
    Witness.setInstance(Witness.new)
  end

  let!(:write_client) { queue.write_client }
  let!(:read_client) { queue.read_client }
  let(:pipeline) { double("pipeline", :pipeline_id => :main) }
  let(:collector)   { LogStash::Instrument::Collector.new }
  let(:events_witness) {Witness.instance.events}
  let(:pipeline_events_witness) {Witness.instance.pipeline("main").events}
  let(:plugin_input_events_witness) {Witness.instance.pipeline("main").inputs(myid.to_s).events}
  let(:plugin) { LogStash::Inputs::DummyInput.new({ "id" => myid }) }
  let(:event) { LogStash::Event.new }
  let(:myid) { "1234myid" }

  subject { described_class.new(write_client, pipeline, plugin, nil) }

  def threaded_read_client
    Thread.new do
      started_at = Time.now

      batch_size = 0
      loop {
        if Time.now - started_at > 60
          raise "Took too much time to read from the queue"
        end
        batch_size = read_client.read_batch.size

        break if batch_size > 0
      }
      expect(batch_size).to eq(1)
    end
  end

  shared_examples "queue tests" do
    it "pushes single event to the `WriteClient`" do
      pusher_thread = Thread.new(subject, event) do |_subject, _event|
        _subject.push(_event)
      end

      reader_thread = threaded_read_client

      [pusher_thread, reader_thread].collect(&:join)
    end

    it "pushes batch to the `WriteClient`" do
      batch = write_client.get_new_batch
      batch << event

      pusher_thread = Thread.new(subject, batch) do |_subject, _batch|
        _subject.push_batch(_batch)
      end

      reader_thread = threaded_read_client
      [pusher_thread, reader_thread].collect(&:join)
    end

    context "recorded metrics" do
      before do
        pusher_thread = Thread.new(subject, event) do |_subject, _event|
          _subject.push(_event)
        end

        reader_thread = threaded_read_client
        [pusher_thread, reader_thread].collect(&:join)
      end

      it "records instance level events `in`" do
        expect(events_witness.snitch.in).to eq(1)
      end

      it "records pipeline level `in`" do
        expect(pipeline_events_witness.snitch.in).to eq(1)
      end

      it "record input `out`" do
        expect(plugin_input_events_witness.snitch.out).to eq(1)
      end

      context "recording of the duration of pushing to the queue" do
        it "records at the `global events` level" do
          expect(events_witness.snitch.in).to be_kind_of(Integer)
        end

        it "records at the `pipeline` level" do
          expect(pipeline_events_witness.snitch.in).to be_kind_of(Integer)
        end

        it "records at the `plugin level" do
          expect(plugin_input_events_witness.snitch.out).to  be_kind_of(Integer)
        end
      end
    end
  end

  context "WrappedSynchronousQueue" do
    let(:queue) { LogStash::Util::WrappedSynchronousQueue.new(1024) }

    include_examples "queue tests"
  end

  context "AckedMemoryQueue" do
    let(:queue) { LogStash::Util::WrappedAckedQueue.create_memory_based("", 1024, 10, 4096) }

    after do
      queue.close
    end

    include_examples "queue tests"
  end
end
