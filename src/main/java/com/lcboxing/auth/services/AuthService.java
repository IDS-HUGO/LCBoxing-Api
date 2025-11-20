package com.lcboxing.auth.services;

import com.lcboxing.config.JwtConfig;
import com.lcboxing.auth.models.Usuario;
import com.lcboxing.auth.repositories.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Map<String, Object> login(String email, String password) throws SQLException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Credenciales inválidas");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (!BCrypt.checkpw(password, usuario.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Actualizar último acceso
        usuarioRepository.updateLastAccess(usuario.getIdUsuario());

        // Generar token
        String token = JwtConfig.generateToken(
                usuario.getIdUsuario(),
                usuario.getEmail(),
                usuario.getNombreRol()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", sanitizeUsuario(usuario));

        return response;
    }

    public Map<String, Object> register(Usuario usuario, String password) throws SQLException {
        // Validar que el email no exista
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Encriptar password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        usuario.setPasswordHash(hashedPassword);

        // Crear usuario
        Usuario nuevoUsuario = usuarioRepository.create(usuario);

        // Generar token
        String token = JwtConfig.generateToken(
                nuevoUsuario.getIdUsuario(),
                nuevoUsuario.getEmail(),
                nuevoUsuario.getNombreRol()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", sanitizeUsuario(nuevoUsuario));

        return response;
    }

    public Usuario getUsuarioById(int id) throws SQLException {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public List<Usuario> getAllUsuarios() throws SQLException {
        return usuarioRepository.findAll();
    }

    public Usuario updateUsuario(int id, Usuario usuario) throws SQLException {
        Usuario existing = getUsuarioById(id);
        usuario.setIdUsuario(id);
        usuario.setPasswordHash(existing.getPasswordHash()); // Mantener el password actual
        return usuarioRepository.update(usuario);
    }

    public boolean deleteUsuario(int id) throws SQLException {
        return usuarioRepository.delete(id);
    }

    private Map<String, Object> sanitizeUsuario(Usuario usuario) {
        Map<String, Object> sanitized = new HashMap<>();
        sanitized.put("id", usuario.getIdUsuario());
        sanitized.put("nombre", usuario.getNombre());
        sanitized.put("apellidoPaterno", usuario.getApellidoPaterno());
        sanitized.put("apellidoMaterno", usuario.getApellidoMaterno());
        sanitized.put("nombreCompleto", usuario.getNombreCompleto());
        sanitized.put("email", usuario.getEmail());
        sanitized.put("telefono", usuario.getTelefono());
        sanitized.put("rol", usuario.getNombreRol());
        sanitized.put("activo", usuario.getActivo());
        return sanitized;
    }
}