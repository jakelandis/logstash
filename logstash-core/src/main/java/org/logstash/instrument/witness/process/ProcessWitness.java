package org.logstash.instrument.witness.process;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.sun.management.UnixOperatingSystemMXBean;
import org.logstash.instrument.metrics.Metric;
import org.logstash.instrument.metrics.gauge.BooleanGauge;
import org.logstash.instrument.metrics.gauge.NumberGauge;
import org.logstash.instrument.witness.MetricSerializer;
import org.logstash.instrument.witness.SerializableWitness;
import org.logstash.instrument.witness.pipeline.ErrorWitness;
import org.logstash.instrument.witness.pipeline.QueueWitness;
import org.logstash.instrument.witness.schedule.ScheduledWitness;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProcessWitness implements SerializableWitness, ScheduledWitness {

    private static final OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
    private final static String KEY = "process";
    private static final Serializer SERIALIZER = new Serializer();
    private final boolean isUnix;
    private final NumberGauge openFileDescriptors;
    private final NumberGauge peakOpenFileDescriptors;
    private final NumberGauge maxFileDescriptors;
    private final UnixOperatingSystemMXBean unixOsBean;
    private final Cpu cpu;
    private final Memory memory;

    public ProcessWitness() {
        this.openFileDescriptors = new NumberGauge("open_file_descriptors", -1);
        this.maxFileDescriptors = new NumberGauge("max_file_descriptors", -1);
        this.peakOpenFileDescriptors = new NumberGauge("peak_open_file_descriptors", -1);
        this.isUnix = osMxBean instanceof UnixOperatingSystemMXBean;
        this.unixOsBean = (UnixOperatingSystemMXBean) osMxBean;
        this.cpu = new Cpu();
        this.memory = new Memory();
    }

    @Override
    public void refresh() {
        if (isUnix) {
            long currentOpen = unixOsBean.getOpenFileDescriptorCount();
            openFileDescriptors.set(currentOpen);
            if (maxFileDescriptors.getValue() == null || maxFileDescriptors.getValue().longValue() < currentOpen) {
                peakOpenFileDescriptors.set(currentOpen);
            }
            maxFileDescriptors.set(unixOsBean.getMaxFileDescriptorCount());
        }
        cpu.refresh();
        memory.refresh();
    }

    public class Cpu implements ScheduledWitness {
        private final static String KEY = "cpu";
        private final NumberGauge processPercent;
        private final NumberGauge totalInMillis;

        private Cpu() {
            this.processPercent = new NumberGauge("percent", -1);
            this.totalInMillis = new NumberGauge("total_in_millis", -1);
        }

        @Override
        public void refresh() {
            processPercent.set(scaleLoadToPercent(unixOsBean.getProcessCpuLoad()));
            totalInMillis.set(TimeUnit.MILLISECONDS.convert(unixOsBean.getProcessCpuTime(), TimeUnit.NANOSECONDS));
        }
    }

    public class Memory implements ScheduledWitness {
        private final static String KEY = "mem";
        private final NumberGauge totalVirtualInBytes;

        private Memory() {
            totalVirtualInBytes = new NumberGauge("total_virtual_in_bytes", -1);
        }

        @Override
        public void refresh() {
            totalVirtualInBytes.set(unixOsBean.getCommittedVirtualMemorySize());
        }
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        SERIALIZER.innerSerialize(this, gen, provider);
    }

    /**
     * The Jackson serializer.
     */
    static class Serializer extends StdSerializer<ProcessWitness> {
        /**
         * Default constructor - required for Jackson
         */
        public Serializer() {
            this(ProcessWitness.class);
        }

        /**
         * Constructor
         *
         * @param t the type to serialize
         */
        protected Serializer(Class<ProcessWitness> t) {
            super(t);
        }

        @Override
        public void serialize(ProcessWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            innerSerialize(witness, gen, provider);
            gen.writeEndObject();
        }

        void innerSerialize(ProcessWitness witness, JsonGenerator gen, SerializerProvider provider) throws IOException {
            MetricSerializer<Metric<Number>> numberSerializer = MetricSerializer.Get.numberSerializer(gen);
            gen.writeObjectFieldStart(KEY);
            numberSerializer.serialize(witness.openFileDescriptors);
            numberSerializer.serialize(witness.peakOpenFileDescriptors);
            numberSerializer.serialize(witness.maxFileDescriptors);
            //memory
            gen.writeObjectFieldStart(Memory.KEY);
            numberSerializer.serialize(witness.memory.totalVirtualInBytes);
            gen.writeEndObject();
            //cpu
            gen.writeObjectFieldStart(Cpu.KEY);
            numberSerializer.serialize(witness.cpu.totalInMillis);
            numberSerializer.serialize(witness.cpu.processPercent);
            //TODO: jake load average

            gen.writeEndObject();
            gen.writeEndObject();
        }
    }

    //TODO: add snitch
    private short scaleLoadToPercent(double load) {
        if (isUnix) {
            if (load >= 0) {
                return (short) (load * 100);
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
}
