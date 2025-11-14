package com.lcboxing.routes;

import com.lcboxing.services.AuthService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AuthRoutes {
    private static final Logger logger = LoggerFactory.getLogger(AuthRoutes.class);
    private final AuthService authService;

    public AuthRoutes() {
        this.authService = new AuthService();
    }

    public void register(Javalin app) {
        app.post("/api/auth/login", this::login);
        app.post("/api/auth/verify", this::verifyToken);
        app.get("/api/auth/me", this::getCurrentUser);
    }

    /**
     * POST /api/auth/login
     * Login de usuario
     */
    private void login(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String email = (String) body.get("email");
            String password = (String) body.get("password");

            if (email == null || password == null || email.isBlank() || password.isBlank()) {
                ctx.status(400).json(Map.of(
                        "success", false,
                        "message", "Email y password son requeridos"
                ));
                return;
            }

            Map<String, Object> response = authService.login(email, password);

            ctx.status(200).json(Map.of(
                    "success", true,
                    "message", "Login exitoso",
                    "data", response
            ));

        } catch (IllegalArgumentException e) {
            ctx.status(401).json(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Error en login", e);
            ctx.status(500).json(Map.of(
                    "success", false,
                    "message", "Error al iniciar sesión"
            ));
        }
    }

    /**
     * POST /api/auth/verify
     * Verifica si un token es válido
     */
    private void verifyToken(Context ctx) {
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
            authService.verifyToken(token);

            ctx.status(200).json(Map.of(
                    "success", true,
                    "message", "Token válido"
            ));

        } catch (Exception e) {
            ctx.status(401).json(Map.of(
                    "success", false,
                    "message", "Token inválido o expirado"
            ));
        }
    }

    /**
     * GET /api/auth/me
     * Obtiene información del usuario actual basado en el token
     */
    private void getCurrentUser(Context ctx) {
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

            Map<String, Object> usuario = new HashMap<>();
            usuario.put("idUsuario", jwt.getClaim("idUsuario").asInt());
            usuario.put("email", jwt.getSubject());
            usuario.put("nombre", jwt.getClaim("nombre").asString());
            usuario.put("idRol", jwt.getClaim("idRol").asInt());
            usuario.put("nombreRol", jwt.getClaim("nombreRol").asString());

            ctx.status(200).json(Map.of(
                    "success", true,
                    "data", usuario
            ));

        } catch (Exception e) {
            ctx.status(401).json(Map.of(
                    "success", false,
                    "message", "Token inválido"
            ));
        }
    }
}