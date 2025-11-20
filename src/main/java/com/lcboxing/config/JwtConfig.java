package com.lcboxing.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtConfig {
    private static final Algorithm algorithm = Algorithm.HMAC256(
            EnvConfig.get("JWT_SECRET", "default_secret_change_in_production")
    );

    private static final String ISSUER = EnvConfig.get("JWT_ISSUER", "lcboxing-api");
    private static final long EXPIRATION_TIME = EnvConfig.getInt("JWT_EXPIRATION_HOURS", 24) * 3600 * 1000L;

    public static String generateToken(int userId, String email, String role) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(String.valueOf(userId))
                .withClaim("email", email)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    public static DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    public static int getUserId(DecodedJWT jwt) {
        return Integer.parseInt(jwt.getSubject());
    }

    public static String getEmail(DecodedJWT jwt) {
        return jwt.getClaim("email").asString();
    }

    public static String getRole(DecodedJWT jwt) {
        return jwt.getClaim("role").asString();
    }
}