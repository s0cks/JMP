package io.github.s0cks.jmp.server;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

public final class JmpServer{
    private static final int PORT = Integer.parseInt(System.getProperty("jmp.server.defaultPort", "8080"));

    private static JmpServer instance;

    public static JmpServer instance(){
        return instance == null ? instance = new JmpServer() : instance;
    }

    private final Vertx vertx;

    private JmpServer(){
        this.vertx = VertxFactory.newVertx();
    }

    public void start(){
        HttpServer server = this.vertx.createHttpServer()
                .requestHandler(this.buildRoutingMathcer());
        JsonObject config = new JsonObject().putString("prefix", "/eventbus");
        JsonArray noPermitted = new JsonArray().add(new JsonObject());
        this.vertx.createSockJSServer(server).bridge(config, noPermitted, noPermitted);
        server.listen(PORT);
    }

    private RouteMatcher buildRoutingMathcer(){
        RouteMatcher matcher = new RouteMatcher();
        matcher.all("/", new RoutingHandler("/index.html"));
        matcher.noMatch(new RoutingHandler("/404.html"));
        return matcher;
    }
}