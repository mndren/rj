package com.rj.utility;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RjUtility {

    public static String getIndex() {
        try {
            try (InputStream is = RjUtility.class.getClassLoader().getResourceAsStream("index.html")) {
                if (is == null) return "";
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendHtml(HttpServerExchange ex, int code, String html) {
        ex.setStatusCode(code);
        ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=utf-8");
        ex.getResponseSender().send(html);
    }

    public static void sendJson(HttpServerExchange ex, int code, String json) {
        ex.setStatusCode(code);
        ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        ex.getResponseSender().send(json);
    }

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
