package com.rj.db;

import com.rj.utility.RjProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public final class DataSource {

    private static final Logger logger = Logger.getLogger(DataSource.class);

    private static HikariDataSource ds;

    private static RjProperties rjProperties;

    private DataSource() {
    }

    public static void init() {
        if (ds != null) return;

        var config = new HikariConfig("datasource.properties");

        config.setMaximumPoolSize(100);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(5000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("rj-pool");

        ds = new HikariDataSource(config);

        try (Connection ignored = ds.getConnection()) {
            logger.info("database connesso correttamente");
        } catch (SQLException e) {
            logger.error("database non disponibile", e);
            throw new RuntimeException(e);
        }

        if (rjProperties == null) {
            rjProperties = new RjProperties();
        }

        if (Boolean.parseBoolean(rjProperties.getProp("sql.import"))) {
            runSqlScript("import.sql");
        }
    }

    public static Connection getConnection() throws SQLException {
        if (ds == null) {
            throw new IllegalStateException("datasource non inizializzato");
        }
        return ds.getConnection();
    }

    public static void shutdown() {
        if (ds != null && !ds.isClosed()) {
            logger.info("chiudo hikariCP...");
            ds.close();
        }
    }

    public static void runSqlScript(String path) {
        try (var conn = getConnection();
             var is = DataSource.class.getClassLoader().getResourceAsStream(path);
             var reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder sql = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }

            try (var stmt = conn.createStatement()) {
                stmt.execute(sql.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException("Errore esecuzione script SQL", e);
        }
    }
}