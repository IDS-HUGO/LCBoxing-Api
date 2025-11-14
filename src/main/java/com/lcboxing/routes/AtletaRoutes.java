package com.lcboxing.routes;

import com.lcboxing.models.Atleta;
import com.lcboxing.repositories.AtletaRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Map;

public class AtletaRoutes {
    private final AtletaRepository atletaRepository = new AtletaRepository();

    public void register(Javalin app) {
        app.get("/api/atletas", this::getAll);
        app.get("/api/atletas/{id}", this::getById);
        app.get("/api/atletas/search/{term}", this::search);
        app.post("/api/atletas", this::create);
        app.put("/api/atletas/{id}", this::update);
        app.delete("/api/atletas/{id}", this::delete);
    }

    private void getAll(Context ctx) {
        try {
            var atletas = atletaRepository.findAllActive();
            ctx.json(Map.of("success", true, "data", atletas));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var atleta = atletaRepository.findById(id);
            if (atleta.isPresent()) {
                ctx.json(Map.of("success", true, "data", atleta.get()));
            } else {
                ctx.status(404).json(Map.of("success", false, "message", "Atleta no encontrado"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void search(Context ctx) {
        try {
            String term = ctx.pathParam("term");
            var atletas = atletaRepository.searchByName(term);
            ctx.json(Map.of("success", true, "data", atletas));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void create(Context ctx) {
        try {
            Atleta atleta = ctx.bodyAsClass(Atleta.class);
            Integer userId = ctx.attribute("userId");
            atleta.setIdUsuarioRegistro(userId);
            Atleta created = atletaRepository.create(atleta);
            ctx.status(201).json(Map.of("success", true, "data", created));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Atleta atleta = ctx.bodyAsClass(Atleta.class);
            atleta.setIdAtleta(id);
            Atleta updated = atletaRepository.update(atleta);
            ctx.json(Map.of("success", true, "data", updated));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            atletaRepository.delete(id);
            ctx.json(Map.of("success", true, "message", "Atleta eliminado"));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
