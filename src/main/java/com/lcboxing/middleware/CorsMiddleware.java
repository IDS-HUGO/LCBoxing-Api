package com.lcboxing.middleware;

import io.javalin.http.Context;

public class CorsMiddleware {

    public static void handle(Context ctx) {
        // Permitir todos los orígenes (para desarrollo)
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        ctx.header("Access-Control-Allow-Credentials", "true");
        ctx.header("Access-Control-Max-Age", "3600");

        // Si es una petición OPTIONS (preflight), responder inmediatamente
        if ("OPTIONS".equals(ctx.method())) {
            ctx.status(200);
        }
    }
}