package io.github.s0cks.jmp.server;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

final class RoutingHandler
implements Handler<HttpServerRequest>{
    private final String path;

    public RoutingHandler(String path){
        this.path = path;
    }

    @Override
    public void handle(HttpServerRequest event) {
        HttpServerResponse resp = event.response();
        resp.putHeader("Content-Type", "text/html;charset=UTF-8");
        resp.sendFile("Web" + this.path);
    }
}