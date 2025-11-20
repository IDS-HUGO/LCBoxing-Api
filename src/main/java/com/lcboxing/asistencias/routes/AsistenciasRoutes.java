package com.lcboxing.asistencias.routes;

import com.lcboxing.asistencias.models.Asistencia;
import com.lcboxing.middleware.AuthMiddleware;
import com.lcboxing.asistencias.services.AsistenciasService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.sql.Date;
import java.time.LocalDate;
import java.sql.Time;
import java.util.Map;

public class  AsistenciasRoutes {
    private final AsistenciasService service;

    public AsistenciasRoutes() {
        this.service = new AsistenciasService();
    }

    public void register(Javalin app) {
        app.get("/api/asistencias/fecha/{fecha}", this::getByFecha);
        app.get("/api/asistencias/hoy", this::getHoy);
        app.get("/api/asistencias/atleta/{idAtleta}", this::getByAtleta);
        app.get("/api/asistencias/{id}", this::getById);
        app.post("/api/asistencias/entrada", this::registrarEntrada);
        app.put("/api/asistencias/{id}/salida", this::registrarSalida);
        app.delete("/api/asistencias/{id}", this::delete);
    }

    private void getByFecha(Context ctx) {
        try {
            LocalDate fecha = LocalDate.parse(ctx.pathParam("fecha"));
            ctx.json(service.getByFecha(fecha));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Fecha inválida"));
        }
    }

    private void getHoy(Context ctx) {
        try {
            LocalDate hoy = LocalDate.now();
            ctx.json(service.getByFecha(hoy));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
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

    private void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ctx.json(service.getById(id));
        } catch (Exception e) {
            ctx.status(404).json(Map.of("error", "Asistencia no encontrada"));
        }
    }

    private void registrarEntrada(Context ctx) {
        try {
            Asistencia asistencia = ctx.bodyAsClass(Asistencia.class);
            int userId = AuthMiddleware.getUserId(ctx);
            ctx.status(201).json(service.registrarEntrada(asistencia, userId));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void registrarSalida(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            Time horaSalida = Time.valueOf(body.get("horaSalida"));
            int userId = AuthMiddleware.getUserId(ctx);
            ctx.json(service.registrarSalida(id, horaSalida, userId));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (service.delete(id)) {
                ctx.json(Map.of("message", "Asistencia eliminada correctamente"));
            } else {
                ctx.status(404).json(Map.of("error", "Asistencia no encontrada"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
}
