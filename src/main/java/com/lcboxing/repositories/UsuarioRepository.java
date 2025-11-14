package com.lcboxing.repositories;

import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioRepository.class);

    // CREATE
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
            stmt.setDate(8, usuario.getFechaNacimiento() != null ? Date.valueOf(usuario.getFechaNacimiento()) : null);
            stmt.setBoolean(9, usuario.getActivo() != null ? usuario.getActivo() : true);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear usuario, no se insertó ningún registro");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setIdUsuario(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Error al crear usuario, no se obtuvo el ID");
                }
            }

            logger.info("Usuario creado exitosamente con ID: {}", usuario.getIdUsuario());
            return usuario;

        } catch (SQLException e) {
            logger.error("Error al crear usuario", e);
            throw e;
        }
    }

    // READ ALL
    public List<Usuario> findAll() throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                "ORDER BY u.id_usuario DESC";

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }

            return usuarios;

        } catch (SQLException e) {
            logger.error("Error al obtener usuarios", e);
            throw e;
        }
    }

    // READ BY ID
    public Optional<Usuario> findById(Integer id) throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                "WHERE u.id_usuario = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error("Error al buscar usuario por ID: {}", id, e);
            throw e;
        }
    }

    // READ BY EMAIL
    public Optional<Usuario> findByEmail(String email) throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                "WHERE u.email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error("Error al buscar usuario por email: {}", email, e);
            throw e;
        }
    }

    // UPDATE
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
            stmt.setDate(7, usuario.getFechaNacimiento() != null ? Date.valueOf(usuario.getFechaNacimiento()) : null);
            stmt.setBoolean(8, usuario.getActivo());
            stmt.setInt(9, usuario.getIdUsuario());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar usuario, no existe el ID: " + usuario.getIdUsuario());
            }

            logger.info("Usuario actualizado exitosamente: {}", usuario.getIdUsuario());
            return usuario;

        } catch (SQLException e) {
            logger.error("Error al actualizar usuario", e);
            throw e;
        }
    }

    // UPDATE PASSWORD
    public void updatePassword(Integer idUsuario, String newPasswordHash) throws SQLException {
        String sql = "UPDATE usuarios SET password_hash = ? WHERE id_usuario = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, idUsuario);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar password, no existe el usuario");
            }

            logger.info("Password actualizado para usuario: {}", idUsuario);

        } catch (SQLException e) {
            logger.error("Error al actualizar password", e);
            throw e;
        }
    }

    // UPDATE LAST ACCESS
    public void updateLastAccess(Integer idUsuario) throws SQLException {
        String sql = "UPDATE usuarios SET ultimo_acceso = CURRENT_TIMESTAMP WHERE id_usuario = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error al actualizar último acceso", e);
            throw e;
        }
    }

    // DELETE (Soft delete)
    public void delete(Integer id) throws SQLException {
        String sql = "UPDATE usuarios SET activo = FALSE WHERE id_usuario = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al eliminar usuario, no existe el ID: " + id);
            }

            logger.info("Usuario desactivado: {}", id);

        } catch (SQLException e) {
            logger.error("Error al eliminar usuario", e);
            throw e;
        }
    }

    // GET ACTIVE USERS
    public List<Usuario> findAllActive() throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                "WHERE u.activo = TRUE " +
                "ORDER BY u.id_usuario DESC";

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }

            return usuarios;

        } catch (SQLException e) {
            logger.error("Error al obtener usuarios activos", e);
            throw e;
        }
    }

    // MAPPER
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

        Date fechaNacimiento = rs.getDate("fecha_nacimiento");
        if (fechaNacimiento != null) {
            usuario.setFechaNacimiento(fechaNacimiento.toLocalDate());
        }

        usuario.setActivo(rs.getBoolean("activo"));

        Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            usuario.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }

        Timestamp fechaMod = rs.getTimestamp("fecha_ultima_modificacion");
        if (fechaMod != null) {
            usuario.setFechaUltimaModificacion(fechaMod.toLocalDateTime());
        }

        Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
        if (ultimoAcceso != null) {
            usuario.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
        }

        usuario.setNombreRol(rs.getString("nombre_rol"));

        return usuario;
    }
}