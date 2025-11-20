package com.lcboxing.middleware;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.lcboxing.config.JwtConfig;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

public class AuthMiddleware {

    public static void authenticate(Context ctx) {
        String authHeader = ctx.header("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedResponse("Token no proporcionado");
        }

        String token = authHeader.substring(7);

        try {
            DecodedJWT jwt = JwtConfig.verifyToken(token);

            // Agregar información del usuario al contexto
            ctx.attribute("userId", JwtConfig.getUserId(jwt));
            ctx.attribute("userEmail", JwtConfig.getEmail(jwt));
            ctx.attribute("userRole", JwtConfig.getRole(jwt));

        } catch (Exception e) {
            throw new UnauthorizedResponse("Token inválido o expirado");
        }
    }

    public static void requireRole(Context ctx, String... allowedRoles) {
        String userRole = ctx.attribute("userRole");

        if (userRole == null) {
            throw new UnauthorizedResponse("No autorizado");
        }

        for (String role : allowedRoles) {
            if (role.equals(userRole)) {
                return;
            }
        }

        throw new UnauthorizedResponse("No tienes permisos para realizar esta acción");
    }

    public static int getUserId(Context ctx) {
        Integer userId = ctx.attribute("userId");
        if (userId == null) {
            throw new UnauthorizedResponse("Usuario no autenticado");
        }
        return userId;
    }

    public static String getUserRole(Context ctx) {
        String role = ctx.attribute("userRole");
        if (role == null) {
            throw new UnauthorizedResponse("Usuario no autenticado");
        }
        return role;
    }
}

