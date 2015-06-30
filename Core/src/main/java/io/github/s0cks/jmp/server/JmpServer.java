package io.github.s0cks.jmp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;

public final class JmpServer{
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int PORT = Integer.parseInt(System.getProperty("jmp.server.defaultPort", "8080"));
    private static final int EB_PORT = Integer.parseInt(System.getProperty("jmp.eventbus.defaultPort", "8081"));

    private static JmpServer instance;

    public static JmpServer instance(){
        return instance == null ? instance = new JmpServer() : instance;
    }

    protected final Vertx vertx;

    private boolean started = false;

    private JmpServer(){
        this.vertx = VertxFactory.newVertx();
    }

    public void start(){
        if(!this.started){
            HttpServer server = this.vertx.createHttpServer()
                    .requestHandler(new Handler<HttpServerRequest>() {
                                        @Override
                                        public void handle(HttpServerRequest event) {
                                            event.response().end("JMP");
                                        }
                                    });
            JsonObject config = new JsonObject()
                                        .putString("prefix", "/eventbus")
                    .putNumber("port", EB_PORT)
                    .putString("host", "localhost")
                    .putBoolean("bridge", true);
            JsonArray noPermitted = new JsonArray().add(new JsonObject());
            SockJSServer sockjs = vertx.createSockJSServer(server);
            sockjs.bridge(config, noPermitted, noPermitted);
            server.listen(PORT, "localhost");
            System.out.println(">> [SERVER] Listening on " + PORT);
            System.out.println(">> [EVENTBUS] Listening on " + EB_PORT);
            this.started = true;
        }
    }

    public void publishEvent(String channel, Object message){
        try{
            this.vertx.eventBus().publish(channel, new JsonObject().putObject("data", new JsonObject(mapper.writeValueAsString(message))));
        } catch(Exception e){
            throw new PublicationException("Error publishing (" + message + ") to channel " + channel, e);
        }
    }

    public void publishMessage(String msg){
        try{
            this.vertx.eventBus().publish("messages", new JsonObject().putString("data", msg));
        } catch(Exception e){
            throw new PublicationException("Error publishing (" + msg + ") to channel messages", e);
        }
    }

    public void debug(){
        this.vertx.setPeriodic(
                                      5000, new Handler<Long>() {
                    @Override
                    public void handle(Long event) {
                        publishMessage("Hello World");
                    }
                }
        );
    }
}