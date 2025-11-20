package com.lcboxing.auth.repositories;

import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.auth.models.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    public Optional<Usuario> findByEmail(String email) throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                "WHERE u.email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUsuario(rs));
            }
            return Optional.empty();
        }
    }

    public Optional<Usuario> findById(int id) throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                "WHERE u.id_usuario = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUsuario(rs));
            }
            return Optional.empty();
        }
    }

    public List<Usuario> findAll() throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                "ORDER BY u.fecha_registro DESC";

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }

        return usuarios;
    }

    public Usuario create(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (id_rol, nombre, apellido_paterno, apellido_materno, " +
                "email, password_hash, telefono, fecha_nacimiento, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, usuario.getIdRol());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getApellidoPaterno());
            stmt.setString(4, usuario.getApellidoMaterno());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getPasswordHash());
            stmt.setString(7, usuario.getTelefono());
            stmt.setDate(8, usuario.getFechaNacimiento());
            stmt.setBoolean(9, usuario.getActivo() != null ? usuario.getActivo() : true);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                usuario.setIdUsuario(rs.getInt(1));
            }

            return findById(usuario.getIdUsuario()).orElse(usuario);
        }
    }

    public Usuario update(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET id_rol = ?, nombre = ?, apellido_paterno = ?, " +
                "apellido_materno = ?, email = ?, telefono = ?, fecha_nacimiento = ?, " +
                "activo = ? WHERE id_usuario = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuario.getIdRol());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getApellidoPaterno());
            stmt.setString(4, usuario.getApellidoMaterno());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getTelefono());
            stmt.setDate(7, usuario.getFechaNacimiento());
            stmt.setBoolean(8, usuario.getActivo());
            stmt.setInt(9, usuario.getIdUsuario());

            stmt.executeUpdate();

            return findById(usuario.getIdUsuario()).orElse(usuario);
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public void updateLastAccess(int userId) throws SQLException {
        String sql = "UPDATE usuarios SET ultimo_acceso = CURRENT_TIMESTAMP WHERE id_usuario = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setIdRol(rs.getInt("id_rol"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidoPaterno(rs.getString("apellido_paterno"));
        usuario.setApellidoMaterno(rs.getString("apellido_materno"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        usuario.setFechaUltimaModificacion(rs.getTimestamp("fecha_ultima_modificacion"));
        usuario.setUltimoAcceso(rs.getTimestamp("ultimo_acceso"));
        usuario.setNombreRol(rs.getString("nombre_rol"));
        return usuario;
    }
}
