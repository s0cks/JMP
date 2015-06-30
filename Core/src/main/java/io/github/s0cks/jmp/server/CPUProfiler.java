package io.github.s0cks.jmp.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.vertx.java.core.Handler;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

public final class CPUProfiler{
    private static final int MILLISEC_IN_SEC = 1000;
    private static final int NANOSEC_IM_SEC = 1000000000;

    private final double procTimeAvail;
    private final ThreadMXBean tmxBean = ManagementFactory.getThreadMXBean();
    private final Map<Long, Stat> threadStats = new HashMap<>();
    private final long interval = 1000;

    private boolean started;

    private static CPUProfiler instance;

    public static CPUProfiler instance(){
        return instance == null ? instance = new CPUProfiler() : instance;
    }

    public CPUProfiler(){
        OperatingSystemMXBean osmxBean = ManagementFactory.getOperatingSystemMXBean();
        int procCount = osmxBean.getAvailableProcessors();
        this.procTimeAvail = (double) this.interval / MILLISEC_IN_SEC * procCount;
    }

    public void start(){
        if(!this.started){
            JmpServer.instance().vertx.setPeriodic(1000, new SpyHandler());
            this.started = true;
        }
    }

    private final class SpyHandler
    implements Handler<Long> {
        @Override
        public void handle(Long event) {
            this.updateStats();
            double usedCpu = (double) getUsedCPUTimeForLastPeriod() / NANOSEC_IM_SEC;
            double load = usedCpu * 100 / procTimeAvail;
            JmpServer.instance().publishEvent("stats:cpu", new Spy(load));
        }

        private void updateStats(){
            long[] ids = tmxBean.getAllThreadIds();
            for(long id : ids){
                long time = tmxBean.getThreadCpuTime(id);
                if(time == -1){
                    threadStats.remove(id);
                    continue;
                }

                Stat stat = threadStats.get(id);
                if(stat == null){
                    stat = new Stat();
                    stat.prevCpuTime = time;
                    stat.currCpuTime = time;
                    threadStats.put(id, stat);
                } else{
                    stat.prevCpuTime = stat.currCpuTime;
                    stat.currCpuTime = time;
                }
            }
        }

        private long getUsedCPUTimeForLastPeriod(){
            long time = 0;
            for(Stat stat : threadStats.values()){
                time += stat.currCpuTime - stat.prevCpuTime;
            }
            return time;
        }
    }

    private static final class Spy
    implements JsonSerializable{
        private final double cpuTime;

        public Spy(double cpuTime){
            this.cpuTime = cpuTime;
        }

        @Override
        public void serialize(JsonGenerator jgen, SerializerProvider provider)
        throws IOException {
            jgen.writeStartObject();
            jgen.writeNumberField("perc", this.cpuTime);
            jgen.writeEndObject();
        }

        @Override
        public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException {
            jgen.writeStartObject();
            jgen.writeNumberField("perc", this.cpuTime);
            jgen.writeEndObject();
        }
    }

    private static final class Stat{
        long prevCpuTime;
        long currCpuTime;
    }
}