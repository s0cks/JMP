package io.github.s0cks.jmp.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.vertx.java.core.Handler;

import java.io.IOException;

public final class MemorySpy{
    private static MemorySpy instance;

    private boolean started = false;

    public static MemorySpy instance(){
        return instance == null ? instance = new MemorySpy() : instance;
    }

    public void start(){
        if(!this.started){
            JmpServer.instance().vertx.setPeriodic(1000, new SpyHandler());
            this.started = true;
        }
    }

    private static final class SpyHandler
    implements Handler<Long>{
        @Override
        public void handle(Long event) {
            JmpServer.instance().publishEvent("stats:mem", new Spy());
        }
    }

    private static final class Spy
    implements JsonSerializable {
        private final long maxRam;
        private final long usedRam;

        private Spy(){
            this.maxRam = Runtime.getRuntime().maxMemory();
            this.usedRam = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        }

        @Override
        public void serialize(JsonGenerator jgen, SerializerProvider provider)
        throws IOException {
            jgen.writeStartObject();
            jgen.writeNumberField("maxRam", this.maxRam);
            jgen.writeNumberField("usedRam", this.usedRam);
            jgen.writeEndObject();
        }

        @Override
        public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException {
            jgen.writeStartObject();
            jgen.writeNumberField("maxRam", this.maxRam);
            jgen.writeNumberField("usedRam", this.usedRam);
            jgen.writeEndObject();
        }
    }
}