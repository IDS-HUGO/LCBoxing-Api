package com.lcboxing.repositories;

import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.models.TipoMembresia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TipoMembresiaRepository {
    private static final Logger logger = LoggerFactory.getLogger(TipoMembresiaRepository.class);

    // CREATE
    public TipoMembresia create(TipoMembresia tipo) throws SQLException {
        String sql = "INSERT INTO tipos_membresia (nombre_tipo, descripcion, duracion_dias, " +
                "precio_base, sesiones_incluidas, activo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, tipo.getNombreTipo());
            stmt.setString(2, tipo.getDescripcion());
            stmt.setInt(3, tipo.getDuracionDias());
            stmt.setBigDecimal(4, tipo.getPrecioBase());

            if (tipo.getSesionesIncluidas() != null) {
                stmt.setInt(5, tipo.getSesionesIncluidas());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setBoolean(6, tipo.getActivo() != null ? tipo.getActivo() : true);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear tipo de membresía");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tipo.setIdTipoMembresia(generatedKeys.getInt(1));
                }
            }

            logger.info("Tipo de membresía creado con ID: {}", tipo.getIdTipoMembresia());
            return tipo;

        } catch (SQLException e) {
            logger.error("Error al crear tipo de membresía", e);
            throw e;
        }
    }

    // READ ALL
    public List<TipoMembresia> findAll() throws SQLException {
        String sql = "SELECT * FROM tipos_membresia ORDER BY nombre_tipo";

        List<TipoMembresia> tipos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tipos.add(mapResultSetToTipoMembresia(rs));
            }

            return tipos;

        } catch (SQLException e) {
            logger.error("Error al obtener tipos de membresía", e);
            throw e;
        }
    }

    // READ ACTIVE
    public List<TipoMembresia> findAllActive() throws SQLException {
        String sql = "SELECT * FROM tipos_membresia WHERE activo = TRUE ORDER BY nombre_tipo";

        List<TipoMembresia> tipos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tipos.add(mapResultSetToTipoMembresia(rs));
            }

            return tipos;

        } catch (SQLException e) {
            logger.error("Error al obtener tipos de membresía activos", e);
            throw e;
        }
    }

    // READ BY ID
    public Optional<TipoMembresia> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM tipos_membresia WHERE id_tipo_membresia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTipoMembresia(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error("Error al buscar tipo de membresía por ID: {}", id, e);
            throw e;
        }
    }

    // UPDATE
    public TipoMembresia update(TipoMembresia tipo) throws SQLException {
        String sql = "UPDATE tipos_membresia SET nombre_tipo = ?, descripcion = ?, " +
                "duracion_dias = ?, precio_base = ?, sesiones_incluidas = ?, activo = ? " +
                "WHERE id_tipo_membresia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipo.getNombreTipo());
            stmt.setString(2, tipo.getDescripcion());
            stmt.setInt(3, tipo.getDuracionDias());
            stmt.setBigDecimal(4, tipo.getPrecioBase());

            if (tipo.getSesionesIncluidas() != null) {
                stmt.setInt(5, tipo.getSesionesIncluidas());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setBoolean(6, tipo.getActivo());
            stmt.setInt(7, tipo.getIdTipoMembresia());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar tipo de membresía");
            }

            logger.info("Tipo de membresía actualizado: {}", tipo.getIdTipoMembresia());
            return tipo;

        } catch (SQLException e) {
            logger.error("Error al actualizar tipo de membresía", e);
            throw e;
        }
    }

    // DELETE (Soft delete)
    public void delete(Integer id) throws SQLException {
        String sql = "UPDATE tipos_membresia SET activo = FALSE WHERE id_tipo_membresia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al eliminar tipo de membresía");
            }

            logger.info("Tipo de membresía desactivado: {}", id);

        } catch (SQLException e) {
            logger.error("Error al eliminar tipo de membresía", e);
            throw e;
        }
    }

    // MAPPER
    private TipoMembresia mapResultSetToTipoMembresia(ResultSet rs) throws SQLException {
        TipoMembresia tipo = new TipoMembresia();
        tipo.setIdTipoMembresia(rs.getInt("id_tipo_membresia"));
        tipo.setNombreTipo(rs.getString("nombre_tipo"));
        tipo.setDescripcion(rs.getString("descripcion"));
        tipo.setDuracionDias(rs.getInt("duracion_dias"));
        tipo.setPrecioBase(rs.getBigDecimal("precio_base"));

        int sesiones = rs.getInt("sesiones_incluidas");
        if (!rs.wasNull()) {
            tipo.setSesionesIncluidas(sesiones);
        }

        tipo.setActivo(rs.getBoolean("activo"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            tipo.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp fechaMod = rs.getTimestamp("fecha_modificacion");
        if (fechaMod != null) {
            tipo.setFechaModificacion(fechaMod.toLocalDateTime());
        }

        return tipo;
    }
}