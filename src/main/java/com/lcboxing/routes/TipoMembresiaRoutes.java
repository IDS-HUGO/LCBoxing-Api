package com.lcboxing.routes;

import com.lcboxing.models.TipoMembresia;
import com.lcboxing.repositories.TipoMembresiaRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Map;

public class TipoMembresiaRoutes {
    private final TipoMembresiaRepository tipoMembresiaRepository = new TipoMembresiaRepository();

    public void register(Javalin app) {
        app.get("/api/tipos-membresia", this::getAll);
        app.get("/api/tipos-membresia/activos", this::getAllActive);
        app.get("/api/tipos-membresia/{id}", this::getById);
        app.post("/api/tipos-membresia", this::create);
        app.put("/api/tipos-membresia/{id}", this::update);
        app.delete("/api/tipos-membresia/{id}", this::delete);
    }

    private void getAll(Context ctx) {
        try {
            var tipos = tipoMembresiaRepository.findAll();
            ctx.json(Map.of("success", true, "data", tipos));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getAllActive(Context ctx) {
        try {
            var tipos = tipoMembresiaRepository.findAllActive();
            ctx.json(Map.of("success", true, "data", tipos));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var tipo = tipoMembresiaRepository.findById(id);
            if (tipo.isPresent()) {
                ctx.json(Map.of("success", true, "data", tipo.get()));
            } else {
                ctx.status(404).json(Map.of("success", false, "message", "Tipo de membresía no encontrado"));
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

            TipoMembresia tipo = ctx.bodyAsClass(TipoMembresia.class);
            TipoMembresia created = tipoMembresiaRepository.create(tipo);
            ctx.status(201).json(Map.of("success", true, "data", created));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void update(Context ctx) {
        try {
            // Verificar que sea GERENTE
            Integer userRole = ctx.attribute("userRole");
            if (userRole == null || userRole != 1) {
                ctx.status(403).json(Map.of("success", false, "message", "Acceso denegado"));
                return;
            }

            int id = Integer.parseInt(ctx.pathParam("id"));
            TipoMembresia tipo = ctx.bodyAsClass(TipoMembresia.class);
            tipo.setIdTipoMembresia(id);
            TipoMembresia updated = tipoMembresiaRepository.update(tipo);
            ctx.json(Map.of("success", true, "data", updated));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void delete(Context ctx) {
        try {
            // Verificar que sea GERENTE
            Integer userRole = ctx.attribute("userRole");
            if (userRole == null || userRole != 1) {
                ctx.status(403).json(Map.of("success", false, "message", "Acceso denegado"));
                return;
            }

            int id = Integer.parseInt(ctx.pathParam("id"));
            tipoMembresiaRepository.delete(id);
            ctx.json(Map.of("success", true, "message", "Tipo de membresía eliminado"));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }
}