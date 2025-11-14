package com.lcboxing.routes;

import com.lcboxing.models.Usuario;
import com.lcboxing.services.UsuarioService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Map;

public class UsuarioRoutes {
    private final UsuarioService usuarioService = new UsuarioService();

    public void register(Javalin app) {
        app.get("/api/usuarios", this::getAll);
        app.get("/api/usuarios/{id}", this::getById);
        app.post("/api/usuarios", this::create);
        app.put("/api/usuarios/{id}", this::update);
        app.delete("/api/usuarios/{id}", this::delete);
        app.post("/api/usuarios/{id}/reset-password", this::resetPassword);
        app.post("/api/usuarios/change-password", this::changePassword);
    }

    private void getAll(Context ctx) {
        try {
            var usuarios = usuarioService.getActiveUsuarios();
            ctx.json(Map.of("success", true, "data", usuarios));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var usuario = usuarioService.getUsuarioById(id);
            if (usuario.isPresent()) {
                ctx.json(Map.of("success", true, "data", usuario.get()));
            } else {
                ctx.status(404).json(Map.of("success", false, "message", "Usuario no encontrado"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void create(Context ctx) {
        try {
            // Verificar que sea GERENTE
            Integer userRole = ctx.attribute("userRole");
            if (userRole == null || userRole != 1) {
                ctx.status(403).json(Map.of("success", false, "message", "Acceso denegado"));
                return;
            }

            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            Usuario created = usuarioService.createUsuario(usuario);
            ctx.status(201).json(Map.of("success", true, "data", created, "message", "Usuario creado. Credenciales enviadas por email."));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            usuario.setIdUsuario(id);
            Usuario updated = usuarioService.updateUsuario(usuario);
            ctx.json(Map.of("success", true, "data", updated));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void delete(Context ctx) {
        try {
            Integer userRole = ctx.attribute("userRole");
            if (userRole == null || userRole != 1) {
                ctx.status(403).json(Map.of("success", false, "message", "Acceso denegado"));
                return;
            }

            int id = Integer.parseInt(ctx.pathParam("id"));
            usuarioService.deleteUsuario(id);
            ctx.json(Map.of("success", true, "message", "Usuario eliminado"));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void resetPassword(Context ctx) {
        try {
            Integer userRole = ctx.attribute("userRole");
            if (userRole == null || userRole != 1) {
                ctx.status(403).json(Map.of("success", false, "message", "Acceso denegado"));
                return;
            }

            int id = Integer.parseInt(ctx.pathParam("id"));
            usuarioService.resetPassword(id);
            ctx.json(Map.of("success", true, "message", "Password reseteado y enviado por email"));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void changePassword(Context ctx) {
        try {
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            Integer userId = ctx.attribute("userId");
            usuarioService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
            ctx.json(Map.of("success", true, "message", "Password actualizado"));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }
}