package com.lcboxing.services;

import com.lcboxing.models.Usuario;
import com.lcboxing.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsuarioService {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;
    private final EmailService emailService;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
        this.authService = new AuthService();
        this.emailService = new EmailService();
    }

    /**
     * Crea un nuevo usuario y envía email con credenciales
     */
    public Usuario createUsuario(Usuario usuario) throws SQLException {
        // Verificar si el email ya existe
        Optional<Usuario> existente = usuarioRepository.findByEmail(usuario.getEmail());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Generar password temporal
        String passwordTemporal = authService.generateTemporaryPassword();
        String passwordHash = authService.hashPassword(passwordTemporal);
        usuario.setPasswordHash(passwordHash);

        // Crear usuario en BD
        Usuario nuevoUsuario = usuarioRepository.create(usuario);

        // Obtener nombre del rol
        Optional<Usuario> usuarioCompleto = usuarioRepository.findById(nuevoUsuario.getIdUsuario());
        if (usuarioCompleto.isPresent()) {
            nuevoUsuario = usuarioCompleto.get();
        }

        // Enviar email con credenciales
        try {
            emailService.sendCredentialsEmail(
                    nuevoUsuario.getEmail(),
                    nuevoUsuario.getNombre(),
                    nuevoUsuario.getEmail(),
                    passwordTemporal,
                    nuevoUsuario.getNombreRol()
            );
            logger.info("Email de credenciales enviado a: {}", nuevoUsuario.getEmail());
        } catch (Exception e) {
            logger.error("Error al enviar email de credenciales", e);
            // No lanzamos excepción para no revertir la transacción
        }

        return nuevoUsuario;
    }

    public List<Usuario> getAllUsuarios() throws SQLException {
        return usuarioRepository.findAll();
    }

    public List<Usuario> getActiveUsuarios() throws SQLException {
        return usuarioRepository.findAllActive();
    }

    public Optional<Usuario> getUsuarioById(Integer id) throws SQLException {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> getUsuarioByEmail(String email) throws SQLException {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario updateUsuario(Usuario usuario) throws SQLException {
        // Verificar que el usuario existe
        Optional<Usuario> existente = usuarioRepository.findById(usuario.getIdUsuario());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Verificar que el email no esté en uso por otro usuario
        Optional<Usuario> usuarioConEmail = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioConEmail.isPresent() &&
                !usuarioConEmail.get().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        return usuarioRepository.update(usuario);
    }

    public void deleteUsuario(Integer id) throws SQLException {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        usuarioRepository.delete(id);
    }

    public void changePassword(Integer idUsuario, String oldPassword, String newPassword) throws SQLException {
        authService.changePassword(idUsuario, oldPassword, newPassword);
    }

    public String resetPassword(Integer idUsuario) throws SQLException {
        // Obtener usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        String newPassword = authService.resetPassword(idUsuario);

        // Enviar email con nuevo password
        try {
            emailService.sendCredentialsEmail(
                    usuario.getEmail(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    newPassword,
                    usuario.getNombreRol()
            );
        } catch (Exception e) {
            logger.error("Error al enviar email de reseteo de password", e);
        }

        return newPassword;
    }
}