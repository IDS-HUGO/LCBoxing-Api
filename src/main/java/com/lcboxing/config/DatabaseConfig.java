package com.lcboxing.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    public static void initialize() {
        if (dataSource != null) {
            logger.warn("DataSource ya está inicializado");
            return;
        }

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(Config.getDatabaseUrl());
            config.setUsername(Config.DB_USER);
            config.setPassword(Config.DB_PASSWORD);
            config.setMaximumPoolSize(Config.DB_POOL_SIZE);
            config.setConnectionTimeout(Config.DB_CONNECTION_TIMEOUT);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            // Configuraciones adicionales
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            dataSource = new HikariDataSource(config);
            logger.info("Pool de conexiones a base de datos inicializado correctamente");

            // Test de conexión
            try (Connection conn = dataSource.getConnection()) {
                logger.info("Conexión a base de datos validada exitosamente");
            }
        } catch (Exception e) {
            logger.error("Error al inicializar el pool de conexiones", e);
            throw new RuntimeException("No se pudo conectar a la base de datos", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource no está inicializado");
        }
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource no está inicializado");
        }
        return dataSource;
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Pool de conexiones cerrado correctamente");
        }
    }
}