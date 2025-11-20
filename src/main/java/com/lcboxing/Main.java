package com.lcboxing;

import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.config.EnvConfig;
import com.lcboxing.middleware.AuthMiddleware;
import com.lcboxing.auth.routes.AuthRoutes;
import com.lcboxing.atletas.routes.AtletasRoutes;
import com.lcboxing.membresias.routes.MembresiasRoutes;
import com.lcboxing.pagos.routes.PagosRoutes;
import com.lcboxing.asistencias.routes.AsistenciasRoutes;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // Load environment
        EnvConfig.load();

        // Init DB
        DatabaseConfig.initialize();

        // Create app
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.http.defaultContentType = "application/json";
            // Habilitar CORS usando el plugin nativo de Javalin
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                    it.allowCredentials = true;
                });
            });
        });

        // ============================================================
        //                        ERRORES
        // ============================================================
        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Error no manejado: ", e);
            ctx.status(500).json(new ErrorResponse("Error interno del servidor", e.getMessage()));
        });

        app.error(404, ctx -> {
            ctx.json(new ErrorResponse("Ruta no encontrada", "El endpoint solicitado no existe"));
        });

        // Health Check
        app.get("/health", ctx -> ctx.json(new HealthResponse("OK", "API funcionando correctamente")));

        // ============================================================
        //                        RUTAS PÚBLICAS
        // ============================================================
        new AuthRoutes().register(app);

        // ============================================================
        //                    PROTECCIÓN DE RUTAS /api/*
        //          (El plugin CORS maneja OPTIONS automáticamente)
        // ============================================================
        app.before("/api/*", ctx -> {
            // Skip authentication for OPTIONS requests (CORS preflight)
            if (!"OPTIONS".equalsIgnoreCase(ctx.method().toString())) {
                AuthMiddleware.authenticate(ctx);
            }
        });

        // Registrar módulos privados
        new AtletasRoutes().register(app);
        new MembresiasRoutes().register(app);
        new PagosRoutes().register(app);
        new AsistenciasRoutes().register(app);

        // ============================================================
        //                        START SERVER
        // ============================================================
        int port = EnvConfig.getInt("SERVER_PORT", 7000);
        app.start(port);

        logger.info("===========================================");
        logger.info("  LC Boxing API iniciada exitosamente");
        logger.info("  Puerto: {}", port);
        logger.info("  Entorno: {}", EnvConfig.get("ENVIRONMENT", "development"));
        logger.info("===========================================");

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Cerrando aplicación...");
            app.stop();
            DatabaseConfig.close();
            logger.info("Aplicación cerrada correctamente");
        }));
    }

    // =================== RESPUESTAS ===================

    public static class ErrorResponse {
        public String error;
        public String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }

    public static class HealthResponse {
        public String status;
        public String message;

        public HealthResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
