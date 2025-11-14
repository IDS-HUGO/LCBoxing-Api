package com.lcboxing.routes;

import com.lcboxing.models.Pago;
import com.lcboxing.repositories.PagoRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.time.LocalDate;
import java.util.Map;

public class PagoRoutes {
    private final PagoRepository pagoRepository = new PagoRepository();

    public void register(Javalin app) {
        app.get("/api/pagos", this::getAll);
        app.get("/api/pagos/{id}", this::getById);
        app.get("/api/pagos/membresia/{idMembresia}", this::getByMembresia);
        app.get("/api/pagos/reporte/hoy", this::getTodayIncome);
        app.get("/api/pagos/reporte/mes", this::getMonthIncome);
        app.get("/api/pagos/reporte/anio", this::getYearIncome);
        app.post("/api/pagos", this::create);
        app.put("/api/pagos/{id}", this::update);
        app.delete("/api/pagos/{id}", this::delete);
    }

    private void getAll(Context ctx) {
        try {
            var pagos = pagoRepository.findAll();
            ctx.json(Map.of("success", true, "data", pagos));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var pago = pagoRepository.findById(id);
            if (pago.isPresent()) {
                ctx.json(Map.of("success", true, "data", pago.get()));
            } else {
                ctx.status(404).json(Map.of("success", false, "message", "Pago no encontrado"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getByMembresia(Context ctx) {
        try {
            int idMembresia = Integer.parseInt(ctx.pathParam("idMembresia"));
            var pagos = pagoRepository.findByMembresia(idMembresia);
            ctx.json(Map.of("success", true, "data", pagos));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getTodayIncome(Context ctx) {
        try {
            var total = pagoRepository.getTodayIncome();
            ctx.json(Map.of("success", true, "data", Map.of("total", total)));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getMonthIncome(Context ctx) {
        try {
            var total = pagoRepository.getMonthIncome();
            ctx.json(Map.of("success", true, "data", Map.of("total", total)));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void getYearIncome(Context ctx) {
        try {
            var total = pagoRepository.getYearIncome();
            ctx.json(Map.of("success", true, "data", Map.of("total", total)));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void create(Context ctx) {
        try {
            Pago pago = ctx.bodyAsClass(Pago.class);
            Integer userId = ctx.attribute("userId");
            pago.setIdUsuarioRegistro(userId);
            Pago created = pagoRepository.create(pago);
            ctx.status(201).json(Map.of("success", true, "data", created));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Pago pago = ctx.bodyAsClass(Pago.class);
            pago.setIdPago(id);
            Pago updated = pagoRepository.update(pago);
            ctx.json(Map.of("success", true, "data", updated));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            pagoRepository.delete(id);
            ctx.json(Map.of("success", true, "message", "Pago eliminado"));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }
}