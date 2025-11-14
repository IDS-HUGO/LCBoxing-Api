package com.lcboxing.repositories;

import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.models.Atleta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AtletaRepository {
    private static final Logger logger = LoggerFactory.getLogger(AtletaRepository.class);

    // CREATE
    public Atleta create(Atleta atleta) throws SQLException {
        String sql = "INSERT INTO atletas (nombre, apellido_paterno, apellido_materno, email, " +
                "telefono, fecha_nacimiento, genero, id_usuario_registro, activo, notas) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, atleta.getNombre());
            stmt.setString(2, atleta.getApellidoPaterno());
            stmt.setString(3, atleta.getApellidoMaterno());
            stmt.setString(4, atleta.getEmail());
            stmt.setString(5, atleta.getTelefono());
            stmt.setDate(6, Date.valueOf(atleta.getFechaNacimiento()));
            stmt.setString(7, atleta.getGenero());
            stmt.setInt(8, atleta.getIdUsuarioRegistro());
            stmt.setBoolean(9, atleta.getActivo() != null ? atleta.getActivo() : true);
            stmt.setString(10, atleta.getNotas());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear atleta");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    atleta.setIdAtleta(generatedKeys.getInt(1));
                }
            }

            logger.info("Atleta creado exitosamente con ID: {}", atleta.getIdAtleta());
            return atleta;

        } catch (SQLException e) {
            logger.error("Error al crear atleta", e);
            throw e;
        }
    }

    // READ ALL
    public List<Atleta> findAll() throws SQLException {
        String sql = "SELECT a.*, CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por " +
                "FROM atletas a " +
                "LEFT JOIN usuarios u ON a.id_usuario_registro = u.id_usuario " +
                "ORDER BY a.id_atleta DESC";

        List<Atleta> atletas = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                atletas.add(mapResultSetToAtleta(rs));
            }

            return atletas;

        } catch (SQLException e) {
            logger.error("Error al obtener atletas", e);
            throw e;
        }
    }

    // READ BY ID
    public Optional<Atleta> findById(Integer id) throws SQLException {
        String sql = "SELECT a.*, CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por " +
                "FROM atletas a " +
                "LEFT JOIN usuarios u ON a.id_usuario_registro = u.id_usuario " +
                "WHERE a.id_atleta = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAtleta(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error("Error al buscar atleta por ID: {}", id, e);
            throw e;
        }
    }

    // READ BY EMAIL
    public Optional<Atleta> findByEmail(String email) throws SQLException {
        String sql = "SELECT a.*, CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por " +
                "FROM atletas a " +
                "LEFT JOIN usuarios u ON a.id_usuario_registro = u.id_usuario " +
                "WHERE a.email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAtleta(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error("Error al buscar atleta por email", e);
            throw e;
        }
    }

    // SEARCH BY NAME
    public List<Atleta> searchByName(String searchTerm) throws SQLException {
        String sql = "SELECT a.*, CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por " +
                "FROM atletas a " +
                "LEFT JOIN usuarios u ON a.id_usuario_registro = u.id_usuario " +
                "WHERE a.activo = TRUE AND (" +
                "a.nombre LIKE ? OR a.apellido_paterno LIKE ? OR a.apellido_materno LIKE ? OR " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) LIKE ?) " +
                "ORDER BY a.apellido_paterno, a.nombre";

        List<Atleta> atletas = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    atletas.add(mapResultSetToAtleta(rs));
                }
            }

            return atletas;

        } catch (SQLException e) {
            logger.error("Error al buscar atletas por nombre", e);
            throw e;
        }
    }

    // UPDATE
    public Atleta update(Atleta atleta) throws SQLException {
        String sql = "UPDATE atletas SET nombre = ?, apellido_paterno = ?, apellido_materno = ?, " +
                "email = ?, telefono = ?, fecha_nacimiento = ?, genero = ?, activo = ?, notas = ? " +
                "WHERE id_atleta = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, atleta.getNombre());
            stmt.setString(2, atleta.getApellidoPaterno());
            stmt.setString(3, atleta.getApellidoMaterno());
            stmt.setString(4, atleta.getEmail());
            stmt.setString(5, atleta.getTelefono());
            stmt.setDate(6, Date.valueOf(atleta.getFechaNacimiento()));
            stmt.setString(7, atleta.getGenero());
            stmt.setBoolean(8, atleta.getActivo());
            stmt.setString(9, atleta.getNotas());
            stmt.setInt(10, atleta.getIdAtleta());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar atleta, no existe el ID: " + atleta.getIdAtleta());
            }

            logger.info("Atleta actualizado exitosamente: {}", atleta.getIdAtleta());
            return atleta;

        } catch (SQLException e) {
            logger.error("Error al actualizar atleta", e);
            throw e;
        }
    }

    // DELETE (Soft delete)
    public void delete(Integer id) throws SQLException {
        String sql = "UPDATE atletas SET activo = FALSE WHERE id_atleta = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al eliminar atleta, no existe el ID: " + id);
            }

            logger.info("Atleta desactivado: {}", id);

        } catch (SQLException e) {
            logger.error("Error al eliminar atleta", e);
            throw e;
        }
    }

    // GET ACTIVE ATHLETES
    public List<Atleta> findAllActive() throws SQLException {
        String sql = "SELECT a.*, CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por " +
                "FROM atletas a " +
                "LEFT JOIN usuarios u ON a.id_usuario_registro = u.id_usuario " +
                "WHERE a.activo = TRUE " +
                "ORDER BY a.apellido_paterno, a.nombre";

        List<Atleta> atletas = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                atletas.add(mapResultSetToAtleta(rs));
            }

            return atletas;

        } catch (SQLException e) {
            logger.error("Error al obtener atletas activos", e);
            throw e;
        }
    }

    // COUNT ACTIVE ATHLETES
    public int countActive() throws SQLException {
        String sql = "SELECT COUNT(*) FROM atletas WHERE activo = TRUE";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            logger.error("Error al contar atletas activos", e);
            throw e;
        }
    }

    // MAPPER
    private Atleta mapResultSetToAtleta(ResultSet rs) throws SQLException {
        Atleta atleta = new Atleta();
        atleta.setIdAtleta(rs.getInt("id_atleta"));
        atleta.setNombre(rs.getString("nombre"));
        atleta.setApellidoPaterno(rs.getString("apellido_paterno"));
        atleta.setApellidoMaterno(rs.getString("apellido_materno"));
        atleta.setEmail(rs.getString("email"));
        atleta.setTelefono(rs.getString("telefono"));

        Date fechaNacimiento = rs.getDate("fecha_nacimiento");
        if (fechaNacimiento != null) {
            atleta.setFechaNacimiento(fechaNacimiento.toLocalDate());
        }

        atleta.setGenero(rs.getString("genero"));

        Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            atleta.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }

        atleta.setIdUsuarioRegistro(rs.getInt("id_usuario_registro"));
        atleta.setActivo(rs.getBoolean("activo"));

        Timestamp fechaMod = rs.getTimestamp("fecha_ultima_modificacion");
        if (fechaMod != null) {
            atleta.setFechaUltimaModificacion(fechaMod.toLocalDateTime());
        }

        atleta.setNotas(rs.getString("notas"));
        atleta.setRegistradoPor(rs.getString("registrado_por"));

        return atleta;
    }
}