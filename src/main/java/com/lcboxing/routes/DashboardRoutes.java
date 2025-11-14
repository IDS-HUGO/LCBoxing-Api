package com.lcboxing.routes;

import com.lcboxing.repositories.DashboardRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Map;

public class DashboardRoutes {
    private final DashboardRepository dashboardRepository = new DashboardRepository();

    public void register(Javalin app) {
        app.get("/api/dashboard/stats", this::getStats);
    }

    private void getStats(Context ctx) {
        try {
            var stats = dashboardRepository.getDashboardStats();
            ctx.json(Map.of("success", true, "data", stats));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", e.getMessage()));
        }
    }
}