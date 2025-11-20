package com.lcboxing.auth.routes;

import com.lcboxing.middleware.AuthMiddleware;
import com.lcboxing.auth.models.Usuario;
import com.lcboxing.auth.services.AuthService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class AuthRoutes {
    private final AuthService authService;

    public AuthRoutes() {
        this.authService = new AuthService();
    }

    public void register(Javalin app) {
        app.post("/auth/login", this::login);
        app.post("/auth/register", this::registerUser);
        app.get("/auth/me", this::getCurrentUser);

        // CRUD de usuarios (protegido)
        app.get("/api/usuarios", this::getAllUsuarios);
        app.get("/api/usuarios/{id}", this::getUsuarioById);
        app.put("/api/usuarios/{id}", this::updateUsuario);
        app.delete("/api/usuarios/{id}", this::deleteUsuario);
    }

    private void login(Context ctx) {
        try {
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String email = body.get("email");
            String password = body.get("password");

            if (email == null || password == null) {
                ctx.status(400).json(Map.of("error", "Email y password son requeridos"));
                return;
            }

            Map<String, Object> response = authService.login(email, password);
            ctx.json(response);

        } catch (Exception e) {
            ctx.status(401).json(Map.of("error", e.getMessage()));
        }
    }

    private void registerUser(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            Usuario usuario = new Usuario();
            usuario.setIdRol(((Number) body.get("idRol")).intValue());
            usuario.setNombre((String) body.get("nombre"));
            usuario.setApellidoPaterno((String) body.get("apellidoPaterno"));
            usuario.setApellidoMaterno((String) body.get("apellidoMaterno"));
            usuario.setEmail((String) body.get("email"));
            usuario.setTelefono((String) body.get("telefono"));

            if (body.get("fechaNacimiento") != null) {
                usuario.setFechaNacimiento(java.sql.Date.valueOf((String) body.get("fechaNacimiento")));
            }

            String password = (String) body.get("password");

            if (password == null || password.length() < 6) {
                ctx.status(400).json(Map.of("error", "La contraseña debe tener al menos 6 caracteres"));
                return;
            }

            Map<String, Object> response = authService.register(usuario, password);
            ctx.status(201).json(response);

        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void getCurrentUser(Context ctx) {
        try {
            int userId = AuthMiddleware.getUserId(ctx);
            Usuario usuario = authService.getUsuarioById(userId);
            ctx.json(usuario);
        } catch (Exception e) {
            ctx.status(404).json(Map.of("error", "Usuario no encontrado"));
        }
    }

    private void getAllUsuarios(Context ctx) {
        try {
            ctx.json(authService.getAllUsuarios());
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    private void getUsuarioById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario usuario = authService.getUsuarioById(id);
            ctx.json(usuario);
        } catch (Exception e) {
            ctx.status(404).json(Map.of("error", "Usuario no encontrado"));
        }
    }

    private void updateUsuario(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            Usuario updated = authService.updateUsuario(id, usuario);
            ctx.json(updated);
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void deleteUsuario(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean deleted = authService.deleteUsuario(id);
            if (deleted) {
                ctx.json(Map.of("message", "Usuario eliminado correctamente"));
            } else {
                ctx.status(404).json(Map.of("error", "Usuario no encontrado"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
}