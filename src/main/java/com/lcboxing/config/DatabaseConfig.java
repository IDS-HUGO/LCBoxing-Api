
// ==================== DatabaseConfig.java ====================
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
        try {
            HikariConfig config = new HikariConfig();

            String host = EnvConfig.get("DB_HOST", "localhost");
            String port = EnvConfig.get("DB_PORT", "3306");
            String database = EnvConfig.get("DB_NAME", "lcboxing_normalizada");
            String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    host, port, database);

            config.setJdbcUrl(url);
            config.setUsername(EnvConfig.get("DB_USER", "root"));
            config.setPassword(EnvConfig.get("DB_PASSWORD", ""));
            config.setMaximumPoolSize(EnvConfig.getInt("DB_POOL_SIZE", 10));
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);

            logger.info("Pool de conexiones a base de datos inicializado correctamente");
        } catch (Exception e) {
            logger.error("Error al inicializar base de datos: ", e);
            throw new RuntimeException("No se pudo conectar a la base de datos", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Pool de conexiones cerrado");
        }
    }
}

