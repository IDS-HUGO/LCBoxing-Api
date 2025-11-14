package com.lcboxing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lcboxing.config.Config;
import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.routes.*;
import com.lcboxing.services.AuthService;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== Iniciando LC Boxing API ===");
        logger.info("Versión: {}", Config.APP_VERSION);
        logger.info("Entorno: {}", Config.APP_ENV);

        // Inicializar base de datos
        try {
            DatabaseConfig.initialize();
            logger.info("✓ Base de datos inicializada");
        } catch (Exception e) {
            logger.error("✗ Error al inicializar base de datos", e);
            System.exit(1);
        }

        // Configurar Jackson para manejo de fechas
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Crear aplicación Javalin
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new io.javalin.json.JavalinJackson(objectMapper));
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                    it.allowCredentials = true;
                });
            });

            config.plugins.enableDevLogging();
            config.http.maxRequestSize = 10_000_000L; // 10MB
        });

        // Middleware de logging
        app.before(ctx -> {
            logger.info("{} {} - IP: {}", ctx.method(), ctx.path(), ctx.ip());
        });

        // Middleware de autenticación
        AuthService authService = new AuthService();
        app.before("/api/*", ctx -> {
            // Rutas públicas (sin autenticación)
            if (ctx.path().equals("/api/auth/login") ||
                    ctx.path().equals("/api/health")) {
                return;
            }

            // Verificar token
            try {
                String authHeader = ctx.header("Authorization");
                if (authHeader == null) {
                    ctx.status(401).json(Map.of(
                            "success", false,
                            "message", "Token no proporcionado"
                    ));
                    return;
                }

                String token = authService.extractTokenFromHeader(authHeader);
                var jwt = authService.verifyToken(token);

                // Agregar información del usuario al contexto
                ctx.attribute("userId", jwt.getClaim("idUsuario").asInt());
                ctx.attribute("userRole", jwt.getClaim("idRol").asInt());
                ctx.attribute("userEmail", jwt.getSubject());

            } catch (Exception e) {
                ctx.status(401).json(Map.of(
                        "success", false,
                        "message", "Token inválido o expirado"
                ));
                return;
            }
        });

        // Registrar rutas
        registerRoutes(app);

        // Manejo de errores
        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Error no manejado", e);
            ctx.status(500).json(Map.of(
                    "success", false,
                    "message", "Error interno del servidor",
                    "error", e.getMessage()
            ));
        });

        // Ruta 404
        app.error(404, ctx -> {
            ctx.json(Map.of(
                    "success", false,
                    "message", "Ruta no encontrada"
            ));
        });

        // Health check
        app.get("/api/health", ctx -> {
            ctx.json(Map.of(
                    "status", "ok",
                    "service", Config.APP_NAME,
                    "version", Config.APP_VERSION,
                    "timestamp", System.currentTimeMillis()
            ));
        });

        // Iniciar servidor
        app.start(Config.SERVER_HOST, Config.SERVER_PORT);

        logger.info("✓ Servidor iniciado en http://{}:{}", Config.SERVER_HOST, Config.SERVER_PORT);
        logger.info("✓ API disponible en http://{}:{}/api", Config.SERVER_HOST, Config.SERVER_PORT);
        logger.info("=== LC Boxing API Lista ===");

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Cerrando aplicación...");
            app.stop();
            DatabaseConfig.close();
            logger.info("Aplicación cerrada correctamente");
        }));
    }

    private static void registerRoutes(Javalin app) {
        // Rutas de autenticación
        new AuthRoutes().register(app);

        // Rutas de usuarios
        new UsuarioRoutes().register(app);

        // Rutas de atletas
        new AtletaRoutes().register(app);

        // Rutas de membresías
        new MembresiaRoutes().register(app);

        // Rutas de tipos de membresía
        new TipoMembresiaRoutes().register(app);

        // Rutas de pagos
        new PagoRoutes().register(app);

        // Rutas de asistencias
        new AsistenciaRoutes().register(app);

        // Rutas de dashboard
        new DashboardRoutes().register(app);

        logger.info("✓ Rutas registradas correctamente");
    }
}