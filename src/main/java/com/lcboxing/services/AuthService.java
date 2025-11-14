package com.lcboxing.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lcboxing.config.Config;
import com.lcboxing.models.Usuario;
import com.lcboxing.repositories.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UsuarioRepository usuarioRepository;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public AuthService() {
        this.usuarioRepository = new UsuarioRepository();
        this.algorithm = Algorithm.HMAC256(Config.JWT_SECRET);
        this.verifier = JWT.require(algorithm)
                .withIssuer("lcboxing-api")
                .build();
    }

    /**
     * Genera un password temporal aleatorio
     */
    public String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    /**
     * Hashea un password usando BCrypt
     */
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Verifica un password contra su hash
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            logger.error("Error al verificar password", e);
            return false;
        }
    }

    /**
     * Autentica un usuario y retorna un JWT
     */
    public Map<String, Object> login(String email, String password) throws SQLException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.getActivo()) {
            throw new IllegalArgumentException("Usuario inactivo");
        }

        if (!verifyPassword(password, usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Actualizar último acceso
        usuarioRepository.updateLastAccess(usuario.getIdUsuario());

        // Generar token
        String token = generateToken(usuario);

        // Preparar respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", sanitizeUsuario(usuario));

        logger.info("Usuario autenticado exitosamente: {}", email);
        return response;
    }

    /**
     * Genera un JWT para un usuario
     */
    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();
        Instant expiration = now.plus(Config.JWT_EXPIRATION_HOURS, ChronoUnit.HOURS);

        return JWT.create()
                .withIssuer("lcboxing-api")
                .withSubject(usuario.getEmail())
                .withClaim("idUsuario", usuario.getIdUsuario())
                .withClaim("idRol", usuario.getIdRol())
                .withClaim("nombreRol", usuario.getNombreRol())
                .withClaim("nombre", usuario.getNombreCompleto())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiration))
                .sign(algorithm);
    }

    /**
     * Valida un JWT y retorna el token decodificado
     */
    public DecodedJWT verifyToken(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            logger.error("Token inválido: {}", e.getMessage());
            throw new IllegalArgumentException("Token inválido o expirado");
        }
    }

    /**
     * Extrae el ID de usuario de un token
     */
    public Integer getUserIdFromToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getClaim("idUsuario").asInt();
    }

    /**
     * Extrae el rol de usuario de un token
     */
    public Integer getUserRoleFromToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getClaim("idRol").asInt();
    }

    /**
     * Verifica si un token pertenece a un GERENTE
     */
    public boolean isManager(String token) {
        try {
            Integer roleId = getUserRoleFromToken(token);
            return roleId == 1; // 1 = GERENTE
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cambia el password de un usuario
     */
    public void changePassword(Integer idUsuario, String oldPassword, String newPassword) throws SQLException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (!verifyPassword(oldPassword, usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Password actual incorrecto");
        }

        String newHash = hashPassword(newPassword);
        usuarioRepository.updatePassword(idUsuario, newHash);

        logger.info("Password cambiado para usuario: {}", idUsuario);
    }

    /**
     * Resetea el password de un usuario (solo GERENTE)
     */
    public String resetPassword(Integer idUsuario) throws SQLException {
        String newPassword = generateTemporaryPassword();
        String newHash = hashPassword(newPassword);
        usuarioRepository.updatePassword(idUsuario, newHash);

        logger.info("Password reseteado para usuario: {}", idUsuario);
        return newPassword;
    }

    /**
     * Remueve información sensible del objeto Usuario antes de enviarlo al cliente
     */
    private Map<String, Object> sanitizeUsuario(Usuario usuario) {
        Map<String, Object> sanitized = new HashMap<>();
        sanitized.put("idUsuario", usuario.getIdUsuario());
        sanitized.put("nombre", usuario.getNombre());
        sanitized.put("apellidoPaterno", usuario.getApellidoPaterno());
        sanitized.put("apellidoMaterno", usuario.getApellidoMaterno());
        sanitized.put("nombreCompleto", usuario.getNombreCompleto());
        sanitized.put("email", usuario.getEmail());
        sanitized.put("telefono", usuario.getTelefono());
        sanitized.put("idRol", usuario.getIdRol());
        sanitized.put("nombreRol", usuario.getNombreRol());
        sanitized.put("activo", usuario.getActivo());
        return sanitized;
    }

    /**
     * Extrae el token del header Authorization
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Token no proporcionado o formato inválido");
    }
}