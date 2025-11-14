package com.lcboxing.routes;

import com.lcboxing.models.Asistencia;
import com.lcboxing.repositories.AsistenciaRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class AsistenciaRoutes {
    private final AsistenciaRepository asistenciaRepository = new AsistenciaRepository();

    public void register(Javalin app) {
        app.get("/api/asistencias/hoy", this::getToday);
        app.get("/api/asistencias/en-gimnasio", this::getCurrentlyInGym);
        app.get("/api/asistencias/atleta/{idAtleta}", this::getByAtleta);
        app.get("/api/asistencias/{id}", this::getById);
        app.post("/api/asistencias/entrada", this::registrarEntrada);
        app.patch("/api/asistencias/{id}/salida", this::registrarSalida);
        app.get("/api/asistencias/reporte/conteo", this::getCountStats);
    }

    private void getToday(Context ctx) {
        try {
            var asistencias = asistenciaRepository.findToday();
            ctx.json(Map.of("success", true, "data", asistencias));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getCurrentlyInGym(Context ctx) {
        try {
            var asistencias = asistenciaRepository.findCurrentlyInGym();
            ctx.json(Map.of("success", true, "data", asistencias));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var asistencia = asistenciaRepository.findById(id);
            if (asistencia.isPresent()) {
                ctx.json(Map.of("success", true, "data", asistencia.get()));
            } else {
                ctx.status(404).json(Map.of("success", false, "message", "Asistencia no encontrada"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getByAtleta(Context ctx) {
        try {
            int idAtleta = Integer.parseInt(ctx.pathParam("idAtleta"));
            var asistencias = asistenciaRepository.findByAtleta(idAtleta);
            ctx.json(Map.of("success", true, "data", asistencias));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void registrarEntrada(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            Integer userId = ctx.attribute("userId");

            Asistencia asistencia = new Asistencia();
            asistencia.setIdAtleta((Integer) body.get("idAtleta"));
            asistencia.setIdMembresia((Integer) body.get("idMembresia"));
            asistencia.setFechaAsistencia(LocalDate.now());
            asistencia.setHoraEntrada(LocalTime.now());
            asistencia.setIdUsuarioRegistroEntrada(userId);
            asistencia.setObservaciones((String) body.get("observaciones"));

            Asistencia created = asistenciaRepository.create(asistencia);
            ctx.status(201).json(Map.of(
                    "success", true,
                    "data", created,
                    "message", "Entrada registrada exitosamente"
            ));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void registrarSalida(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Integer userId = ctx.attribute("userId");
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String observaciones = body != null ? body.get("observaciones") : null;

            Asistencia updated = asistenciaRepository.registrarSalida(id, userId, observaciones);
            ctx.json(Map.of(
                    "success", true,
                    "data", updated,
                    "message", "Salida registrada exitosamente"
            ));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getCountStats(Context ctx) {
        try {
            int countToday = asistenciaRepository.countToday();
            int countInGym = asistenciaRepository.countCurrentlyInGym();

            ctx.json(Map.of(
                    "success", true,
                    "data", Map.of(
                            "asistenciasHoy", countToday,
                            "enGimnasio", countInGym
                    )
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }
}