package com.lcboxing.membresias.routes;

import com.lcboxing.middleware.AuthMiddleware;
import com.lcboxing.membresias.models.Membresia;
import com.lcboxing.membresias.services.MembresiasService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class MembresiasRoutes {
    private final MembresiasService service;

    public MembresiasRoutes() {
        this.service = new MembresiasService();
    }

    public void register(Javalin app) {
        app.get("/api/membresias", this::getAll);
        app.get("/api/membresias/{id}", this::getById);
        app.get("/api/membresias/atleta/{idAtleta}", this::getByAtleta);
        app.get("/api/membresias/vencimientos/{dias}", this::getVencimientos);
        app.post("/api/membresias", this::create);
        app.put("/api/membresias/{id}", this::update);
        app.delete("/api/membresias/{id}", this::delete);
    }

    private void getAll(Context ctx) {
        try {
            ctx.json(service.getAll());
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    private void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ctx.json(service.getById(id));
        } catch (Exception e) {
            ctx.status(404).json(Map.of("error", "Membresía no encontrada"));
        }
    }

    private void getByAtleta(Context ctx) {
        try {
            int idAtleta = Integer.parseInt(ctx.pathParam("idAtleta"));
            ctx.json(service.getByAtleta(idAtleta));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    private void getVencimientos(Context ctx) {
        try {
            int dias = Integer.parseInt(ctx.pathParam("dias"));
            ctx.json(service.getVencimientos(dias));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    private void create(Context ctx) {
        try {
            Membresia membresia = ctx.bodyAsClass(Membresia.class);
            int userId = AuthMiddleware.getUserId(ctx);
            ctx.status(201).json(service.create(membresia, userId));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Membresia membresia = ctx.bodyAsClass(Membresia.class);
            ctx.json(service.update(id, membresia));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (service.delete(id)) {
                ctx.json(Map.of("message", "Membresía eliminada correctamente"));
            } else {
                ctx.status(404).json(Map.of("error", "Membresía no encontrada"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
}