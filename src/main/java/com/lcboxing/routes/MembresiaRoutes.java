package com.lcboxing.routes;

import com.lcboxing.models.Membresia;
import com.lcboxing.repositories.MembresiaRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Map;

public class MembresiaRoutes {
    private final MembresiaRepository membresiaRepository = new MembresiaRepository();

    public void register(Javalin app) {
        app.get("/api/membresias", this::getAll);
        app.get("/api/membresias/{id}", this::getById);
        app.get("/api/membresias/atleta/{idAtleta}", this::getByAtleta);
        app.get("/api/membresias/atleta/{idAtleta}/activa", this::getActiveByAtleta);
        app.get("/api/membresias/por-vencer/{days}", this::getExpiringSoon);
        app.post("/api/membresias", this::create);
        app.put("/api/membresias/{id}", this::update);
        app.patch("/api/membresias/{id}/estado", this::updateStatus);
    }

    private void getAll(Context ctx) {
        try {
            var membresias = membresiaRepository.findAll();
            ctx.json(Map.of("success", true, "data", membresias));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var membresia = membresiaRepository.findById(id);
            if (membresia.isPresent()) {
                ctx.json(Map.of("success", true, "data", membresia.get()));
            } else {
                ctx.status(404).json(Map.of("success", false, "message", "Membresía no encontrada"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getByAtleta(Context ctx) {
        try {
            int idAtleta = Integer.parseInt(ctx.pathParam("idAtleta"));
            var membresias = membresiaRepository.findByAtleta(idAtleta);
            ctx.json(Map.of("success", true, "data", membresias));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getActiveByAtleta(Context ctx) {
        try {
            int idAtleta = Integer.parseInt(ctx.pathParam("idAtleta"));
            var membresia = membresiaRepository.findActiveByAtleta(idAtleta);
            if (membresia.isPresent()) {
                ctx.json(Map.of("success", true, "data", membresia.get()));
            } else {
                ctx.status(404).json(Map.of("success", false, "message", "No hay membresía activa"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getExpiringSoon(Context ctx) {
        try {
            int days = Integer.parseInt(ctx.pathParam("days"));
            var membresias = membresiaRepository.findExpiringSoon(days);
            ctx.json(Map.of("success", true, "data", membresias));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void create(Context ctx) {
        try {
            Membresia membresia = ctx.bodyAsClass(Membresia.class);
            Integer userId = ctx.attribute("userId");
            membresia.setIdUsuarioRegistro(userId);
            Membresia created = membresiaRepository.create(membresia);
            ctx.status(201).json(Map.of("success", true, "data", created));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Membresia membresia = ctx.bodyAsClass(Membresia.class);
            membresia.setIdMembresia(id);
            Membresia updated = membresiaRepository.update(membresia);
            ctx.json(Map.of("success", true, "data", updated));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void updateStatus(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Integer> body = ctx.bodyAsClass(Map.class);
            membresiaRepository.updateStatus(id, body.get("idEstado"));
            ctx.json(Map.of("success", true, "message", "Estado actualizado"));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }
}