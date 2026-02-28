package com.rj.utility;

import com.rj.db.DataSource;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Base64;

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

        try (Connection c = DataSource.getConnection();
             PreparedStatement pst = c.prepareStatement(
                     "INSERT INTO sessioni (token, utente_id, expires_at) VALUES (?, ?, ?)")) {
            pst.setString(1, token);
            pst.setLong(2, utenteId);
            pst.setObject(3, expires);
            pst.executeUpdate();
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

        try (Connection c = DataSource.getConnection();
             PreparedStatement pst = c.prepareStatement(
                     "SELECT utente_id FROM sessioni WHERE token = ? AND expires_at > NOW()")) {
            pst.setString(1, cookie.getValue());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getLong("utente_id");
            }
        } catch (Exception ex) {
            RjLogger.error(ex, "AuthUtility.getUtenteId");
        }
        return null;
    }

    public static void destroySession(HttpServerExchange e) {
        Cookie cookie = e.getRequestCookie("rj_session");
        if (cookie == null) return;

        try (Connection c = DataSource.getConnection();
             PreparedStatement pst = c.prepareStatement(
                     "DELETE FROM sessioni WHERE token = ?")) {
            pst.setString(1, cookie.getValue());
            pst.executeUpdate();

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
