package com.rj.utility;

import com.rj.models.Sessioni;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
public class RjAuthUtility {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static void createSession(HttpServerExchange e, Long utenteId, String username) {
        String token = generateToken();
        LocalDateTime expires = LocalDateTime.now().plusHours(8);
        
        try {
            Sessioni s = new Sessioni();
            s.setToken(token);
            s.setUtenteId(utenteId);
            s.setExpiresAt(expires);
            s.insert();
        } catch (Exception ex) {
            RjLogger.error(ex, "AuthUtility.createSession");
        }

        Cookie session = new CookieImpl("rj_session", token)
                .setHttpOnly(true)
                .setPath("/")
                .setMaxAge(60 * 60 * 8);
        Cookie user = new CookieImpl("rj_user", username)
                .setHttpOnly(true)
                .setPath("/")
                .setMaxAge(60 * 60 * 8);

        e.setResponseCookie(session);
        e.setResponseCookie(user);

    }

    public static Long getUtenteId(HttpServerExchange e) {
        Cookie cookie = e.getRequestCookie("rj_session");
        if (cookie == null) return null;
        String token = cookie.getValue();
        if (token == null) return null;

        try {
            Sessioni s = new Sessioni();
            return s.getUtenteIdByToken(token);

        } catch (Exception ex) {
            RjLogger.error(ex, "AuthUtility.getUtenteId");
        }
        return null;
    }

    public static void destroySession(HttpServerExchange e) {
        Cookie cookie = e.getRequestCookie("rj_session");
        if (cookie == null) return;
        String token = cookie.getValue();
        if (token == null) return;
        try {
            Sessioni s = new Sessioni();
            s.destroy(token);
        } catch (Exception ex) {
            RjLogger.error(ex, "AuthUtility.destroySession");
        }
        e.setResponseCookie(new CookieImpl("rj_session", "")
                .setHttpOnly(true)
                .setPath("/")
                .setMaxAge(0));
        e.setResponseCookie(new CookieImpl("rj_user", "")
                .setHttpOnly(true)
                .setPath("/")
                .setMaxAge(0));
    }

    public static boolean requireAuth(HttpServerExchange e) {
        if (getUtenteId(e) == null) {
            e.setStatusCode(302);
            e.getResponseHeaders().put(io.undertow.util.Headers.LOCATION, "/login");
            e.endExchange();
            return false;
        }
        return true;
    }

}
