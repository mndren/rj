package com.rj.utility;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

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

    public static String getHtmx() {
        try {
            try (InputStream is = RjUtility.class.getClassLoader().getResourceAsStream("htmx.min.js")) {
                if (is == null) return "";
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStyle() {
        try {
            try (InputStream is = RjUtility.class.getClassLoader().getResourceAsStream("style.css")) {
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

    public static void sendHtmx(HttpServerExchange ex, String htmx) {
        ex.setStatusCode(200);
        ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/javascript; charset=utf-8");
        ex.getResponseSender().send(htmx);
    }

    public static void sendCss(HttpServerExchange ex, String css) {
        ex.setStatusCode(200);
        ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/css; charset=utf-8");
        ex.getResponseSender().send(css);
    }


    public static boolean redirectIfDirect(HttpServerExchange e) {
        if (!e.getRequestHeaders().contains("HX-Request")) {
            sendHtml(e, 200, getIndex());
            return true;
        }
        return false;
    }

    public static Map<String, String> parseForm(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isBlank()) return map;

        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                map.put(key, value);
            }
        }
        return map;
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

    public static String queryParam(HttpServerExchange e, String name) {
        var params = e.getQueryParameters().get(name);
        return (params != null && !params.isEmpty()) ? params.getFirst() : "";
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes(StandardCharsets.UTF_8));
            return new String(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
