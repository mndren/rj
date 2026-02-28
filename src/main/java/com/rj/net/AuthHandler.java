package com.rj.net;

import com.rj.utility.RjAuthUtility;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.List;

public class AuthHandler implements HttpHandler {

    private final HttpHandler next;

    private static final List<String> PUBLIC = List.of("/login", "/logout", "/htmx.min.js", "/style.css");

    public AuthHandler(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange e) throws Exception {
        String path = e.getRequestPath();

        boolean isPublic = PUBLIC.stream().anyMatch(path::startsWith);

        if (isPublic || RjAuthUtility.getUtenteId(e) != null) {
            next.handleRequest(e);
        } else {
            e.setStatusCode(302);
            e.getResponseHeaders().put(Headers.LOCATION, "/login");
            e.endExchange();
        }
    }
}