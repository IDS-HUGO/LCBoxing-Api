package com.lcboxing.pagos.routes;

import com.lcboxing.middleware.AuthMiddleware;
import com.lcboxing.pagos.models.Pago;
import com.lcboxing.pagos.services.PagosService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Map;

public class PagosRoutes {
    private final PagosService service;

    public PagosRoutes() {
        this.service = new PagosService();
    }

    public void register(Javalin app) {
        app.get("/api/pagos", this::getAll);
        app.get("/api/pagos/{id}", this::getById);
        app.get("/api/pagos/membresia/{idMembresia}", this::getByMembresia);
        app.post("/api/pagos", this::create);
        app.delete("/api/pagos/{id}", this::delete);
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
            ctx.status(404).json(Map.of("error", "Pago no encontrado"));
        }
    }

    private void getByMembresia(Context ctx) {
        try {
            int idMembresia = Integer.parseInt(ctx.pathParam("idMembresia"));
            ctx.json(service.getByMembresia(idMembresia));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    private void create(Context ctx) {
        try {
            Pago pago = ctx.bodyAsClass(Pago.class);
            int userId = AuthMiddleware.getUserId(ctx);
            ctx.status(201).json(service.create(pago, userId));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (service.delete(id)) {
                ctx.json(Map.of("message", "Pago eliminado correctamente"));
            } else {
                ctx.status(404).json(Map.of("error", "Pago no encontrado"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
}