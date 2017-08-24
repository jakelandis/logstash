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

    public ProcessWitness() {
        this.openFileDescriptors = new NumberGauge("open_file_descriptors", -1);
        this.maxFileDescriptors = new NumberGauge("max_file_descriptors", -1);
        this.peakOpenFileDescriptors = new NumberGauge("peak_open_file_descriptors", -1);
        this.isUnix = osMxBean instanceof UnixOperatingSystemMXBean;
    }


    @Override
    public void refresh() {
        // Defaults are -1
        if (this.isUnix) {
            UnixOperatingSystemMXBean unixOsBean = (UnixOperatingSystemMXBean) osMxBean;;
            long currentOpen = unixOsBean.getOpenFileDescriptorCount();
            openFileDescriptors.set(currentOpen);
            if(maxFileDescriptors.getValue() == null || maxFileDescriptors.getValue().longValue() < currentOpen){
                peakOpenFileDescriptors.set(currentOpen);
            }
            maxFileDescriptors.set(unixOsBean.getMaxFileDescriptorCount());
        }
    }

    public class Cpu implements SerializableWitness, ScheduledWitness {
        private final static String KEY = "cpu";
//              metric.gauge(cpu_path, :percent, cpu_metrics["process_percent"])
//                metric.gauge(cpu_path, :total_in_millis, cpu_metrics["total_in_millis"])

        //       cpuMap.put("total_in_millis", this.cpuMillisTotal);
        //        cpuMap.put("process_percent", this.cpuProcessPercent);
//        cpuMap.put("system_percent", this.cpuSystemPercent);
//
        private Cpu(){

        }

        @Override
        public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {

        }

        @Override
        public void refresh() {
            this.cpuProcessPercent = scaleLoadToPercent(unixOsBean.getProcessCpuLoad());
            this.cpuSystemPercent = scaleLoadToPercent(unixOsBean.getSystemCpuLoad());
            this.cpuMillisTotal = TimeUnit.MILLISECONDS.convert(
                    unixOsBean.getProcessCpuTime(), TimeUnit.NANOSECONDS
            );
        }
    }
    public class Memory implements SerializableWitness, ScheduledWitness {
        private final static String KEY = "mem";
        //    metric.gauge(path + [:mem], :total_virtual_in_bytes, process_metrics["mem"]["total_virtual_in_bytes"])

        //        Map<String, Object> memoryMap = new HashMap<>();
//        map.put("mem", memoryMap);
//        memoryMap.put("total_virtual_in_bytes", this.memTotalVirtualInBytes);
        private Memory(){

        }

        @Override
        public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {

        }

        @Override
        public void refresh() {
            this.memTotalVirtualInBytes = unixOsBean.getCommittedVirtualMemorySize();

        }
    }

    public Map<String, Object> toMap() {

//

//
//        return map;
    }

    @Override
    public void genJson(JsonGenerator gen, SerializerProvider provider) throws IOException {

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
            gen.writeObjectFieldStart(KEY);

            gen.writeEndObject();
        }
    }



    private static short scaleLoadToPercent(double load) {
        if (osMxBean instanceof UnixOperatingSystemMXBean) {
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
