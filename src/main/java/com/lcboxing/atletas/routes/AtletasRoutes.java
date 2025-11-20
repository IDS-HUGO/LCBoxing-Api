package com.lcboxing.atletas.routes;

import com.lcboxing.middleware.AuthMiddleware;
import com.lcboxing.atletas.models.Atleta;
import com.lcboxing.atletas.models.ContactoEmergencia;
import com.lcboxing.atletas.models.DatosMedicos;
import com.lcboxing.atletas.services.AtletasService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class AtletasRoutes {
    private final AtletasService service;

    public AtletasRoutes() {
        this.service = new AtletasService();
    }

    public void register(Javalin app) {
        app.get("/api/atletas", this::getAll);
        app.get("/api/atletas/activos", this::getActivos);
        app.get("/api/atletas/{id}", this::getById);
        app.post("/api/atletas", this::create);
        app.put("/api/atletas/{id}", this::update);
        app.delete("/api/atletas/{id}", this::delete);

        // Datos médicos
        app.get("/api/atletas/{id}/datos-medicos", this::getDatosMedicos);
        app.post("/api/atletas/{id}/datos-medicos", this::saveDatosMedicos);

        // Contactos de emergencia
        app.get("/api/atletas/{id}/contactos", this::getContactos);
        app.post("/api/atletas/{id}/contactos", this::addContacto);
        app.delete("/api/contactos/{id}", this::deleteContacto);
    }

    private void getAll(Context ctx) {
        try {
            ctx.json(service.getAllAtletas());
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    private void getActivos(Context ctx) {
        try {
            ctx.json(service.getAtletasActivos());
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    private void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ctx.json(service.getAtletaById(id));
        } catch (Exception e) {
            ctx.status(404).json(Map.of("error", "Atleta no encontrado"));
        }
    }

    private void create(Context ctx) {
        try {
            Atleta atleta = ctx.bodyAsClass(Atleta.class);
            int userId = AuthMiddleware.getUserId(ctx);
            Atleta created = service.createAtleta(atleta, userId);
            ctx.status(201).json(created);
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Atleta atleta = ctx.bodyAsClass(Atleta.class);
            ctx.json(service.updateAtleta(id, atleta));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (service.deleteAtleta(id)) {
                ctx.json(Map.of("message", "Atleta eliminado correctamente"));
            } else {
                ctx.status(404).json(Map.of("error", "Atleta no encontrado"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    private void getDatosMedicos(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            DatosMedicos datos = service.getDatosMedicos(id);
            if (datos != null) {
                ctx.json(datos);
            } else {
                ctx.status(404).json(Map.of("message", "No hay datos médicos registrados"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    private void saveDatosMedicos(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            DatosMedicos datos = ctx.bodyAsClass(DatosMedicos.class);
            ctx.json(service.saveDatosMedicos(id, datos));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void getContactos(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ctx.json(service.getContactos(id));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    private void addContacto(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ContactoEmergencia contacto = ctx.bodyAsClass(ContactoEmergencia.class);
            ctx.status(201).json(service.addContacto(id, contacto));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void deleteContacto(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (service.deleteContacto(id)) {
                ctx.json(Map.of("message", "Contacto eliminado correctamente"));
            } else {
                ctx.status(404).json(Map.of("error", "Contacto no encontrado"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
}