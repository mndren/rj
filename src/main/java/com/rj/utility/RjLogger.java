package com.rj.utility;

import com.rj.db.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class RjLogger {

    public enum Level {INFO, WARN, ERROR}

    private static final String SQL =
            "INSERT INTO rj_logs (level, error, ctx) VALUES (?, ?, ?)";

    public static void info(String message, String context) {
        log(Level.INFO, message, context);
    }

    public static void warn(String message, String context) {
        log(Level.WARN, message, context);
    }

    public static void error(String message, String context) {
        log(Level.ERROR, message, context);
    }

    public static void error(Exception ex, String context) {
        log(Level.ERROR, ex.getClass().getSimpleName() + ": " + ex.getMessage(), context);
    }

    private static void log(Level level, String message, String context) {
        System.out.printf("[%s] [%s] %s — %s%n", LocalDateTime.now(), level, context, message);

        Thread.ofVirtual().start(() -> {
            try (Connection c = DataSource.getConnection();
                 PreparedStatement pst = c.prepareStatement(SQL)) {
                pst.setString(1, level.name());
                pst.setString(2, message);
                pst.setString(3, context);
                pst.executeUpdate();
            } catch (Exception e) {
                System.err.println("RjLogger DB error: " + e.getMessage());
            }
        });
    }
}

