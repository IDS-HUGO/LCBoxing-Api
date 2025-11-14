package com.lcboxing.repositories;

import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.models.Membresia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembresiaRepository {
    private static final Logger logger = LoggerFactory.getLogger(MembresiaRepository.class);

    // CREATE
    public Membresia create(Membresia membresia) throws SQLException {
        String sql = "INSERT INTO membresias (id_atleta, id_tipo_membresia, id_estado_membresia, " +
                "fecha_inicio, fecha_vencimiento, precio_pagado, sesiones_restantes, " +
                "id_usuario_registro, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, membresia.getIdAtleta());
            stmt.setInt(2, membresia.getIdTipoMembresia());
            stmt.setInt(3, membresia.getIdEstadoMembresia() != null ? membresia.getIdEstadoMembresia() : 1);
            stmt.setDate(4, Date.valueOf(membresia.getFechaInicio()));
            stmt.setDate(5, Date.valueOf(membresia.getFechaVencimiento()));
            stmt.setBigDecimal(6, membresia.getPrecioPagado());

            if (membresia.getSesionesRestantes() != null) {
                stmt.setInt(7, membresia.getSesionesRestantes());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setInt(8, membresia.getIdUsuarioRegistro());
            stmt.setString(9, membresia.getObservaciones());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear membresía");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    membresia.setIdMembresia(generatedKeys.getInt(1));
                }
            }

            logger.info("Membresía creada exitosamente con ID: {}", membresia.getIdMembresia());
            return membresia;

        } catch (SQLException e) {
            logger.error("Error al crear membresía", e);
            throw e;
        }
    }

    // READ ALL
    public List<Membresia> findAll() throws SQLException {
        String sql = "SELECT m.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "tm.nombre_tipo as tipo_membresia, " +
                "em.nombre_estado as estado_membresia, " +
                "CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por, " +
                "DATEDIFF(m.fecha_vencimiento, CURDATE()) as dias_restantes " +
                "FROM membresias m " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN estados_membresia em ON m.id_estado_membresia = em.id_estado_membresia " +
                "LEFT JOIN usuarios u ON m.id_usuario_registro = u.id_usuario " +
                "ORDER BY m.id_membresia DESC";

        List<Membresia> membresias = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                membresias.add(mapResultSetToMembresia(rs));
            }

            return membresias;

        } catch (SQLException e) {
            logger.error("Error al obtener membresías", e);
            throw e;
        }
    }

    // READ BY ID
    public Optional<Membresia> findById(Integer id) throws SQLException {
        String sql = "SELECT m.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "tm.nombre_tipo as tipo_membresia, " +
                "em.nombre_estado as estado_membresia, " +
                "CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por, " +
                "DATEDIFF(m.fecha_vencimiento, CURDATE()) as dias_restantes " +
                "FROM membresias m " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN estados_membresia em ON m.id_estado_membresia = em.id_estado_membresia " +
                "LEFT JOIN usuarios u ON m.id_usuario_registro = u.id_usuario " +
                "WHERE m.id_membresia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMembresia(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error("Error al buscar membresía por ID: {}", id, e);
            throw e;
        }
    }

    // FIND BY ATLETA
    public List<Membresia> findByAtleta(Integer idAtleta) throws SQLException {
        String sql = "SELECT m.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "tm.nombre_tipo as tipo_membresia, " +
                "em.nombre_estado as estado_membresia, " +
                "CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por, " +
                "DATEDIFF(m.fecha_vencimiento, CURDATE()) as dias_restantes " +
                "FROM membresias m " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN estados_membresia em ON m.id_estado_membresia = em.id_estado_membresia " +
                "LEFT JOIN usuarios u ON m.id_usuario_registro = u.id_usuario " +
                "WHERE m.id_atleta = ? " +
                "ORDER BY m.fecha_inicio DESC";

        List<Membresia> membresias = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAtleta);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membresias.add(mapResultSetToMembresia(rs));
                }
            }

            return membresias;

        } catch (SQLException e) {
            logger.error("Error al buscar membresías por atleta", e);
            throw e;
        }
    }

    // FIND ACTIVE BY ATLETA
    public Optional<Membresia> findActiveByAtleta(Integer idAtleta) throws SQLException {
        String sql = "SELECT m.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "tm.nombre_tipo as tipo_membresia, " +
                "em.nombre_estado as estado_membresia, " +
                "CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por, " +
                "DATEDIFF(m.fecha_vencimiento, CURDATE()) as dias_restantes " +
                "FROM membresias m " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN estados_membresia em ON m.id_estado_membresia = em.id_estado_membresia " +
                "LEFT JOIN usuarios u ON m.id_usuario_registro = u.id_usuario " +
                "WHERE m.id_atleta = ? AND m.id_estado_membresia = 1 " +
                "AND m.fecha_vencimiento >= CURDATE() " +
                "ORDER BY m.fecha_vencimiento DESC LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAtleta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMembresia(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error("Error al buscar membresía activa por atleta", e);
            throw e;
        }
    }

    // FIND EXPIRING SOON (próximas a vencer en N días)
    public List<Membresia> findExpiringSoon(int days) throws SQLException {
        String sql = "SELECT m.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "tm.nombre_tipo as tipo_membresia, " +
                "em.nombre_estado as estado_membresia, " +
                "CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por, " +
                "DATEDIFF(m.fecha_vencimiento, CURDATE()) as dias_restantes " +
                "FROM membresias m " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN estados_membresia em ON m.id_estado_membresia = em.id_estado_membresia " +
                "LEFT JOIN usuarios u ON m.id_usuario_registro = u.id_usuario " +
                "WHERE m.id_estado_membresia = 1 " +
                "AND m.fecha_vencimiento BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                "AND a.activo = TRUE " +
                "ORDER BY m.fecha_vencimiento ASC";

        List<Membresia> membresias = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, days);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membresias.add(mapResultSetToMembresia(rs));
                }
            }

            return membresias;

        } catch (SQLException e) {
            logger.error("Error al buscar membresías por vencer", e);
            throw e;
        }
    }

    // UPDATE
    public Membresia update(Membresia membresia) throws SQLException {
        String sql = "UPDATE membresias SET id_tipo_membresia = ?, id_estado_membresia = ?, " +
                "fecha_inicio = ?, fecha_vencimiento = ?, precio_pagado = ?, " +
                "sesiones_restantes = ?, observaciones = ? WHERE id_membresia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, membresia.getIdTipoMembresia());
            stmt.setInt(2, membresia.getIdEstadoMembresia());
            stmt.setDate(3, Date.valueOf(membresia.getFechaInicio()));
            stmt.setDate(4, Date.valueOf(membresia.getFechaVencimiento()));
            stmt.setBigDecimal(5, membresia.getPrecioPagado());

            if (membresia.getSesionesRestantes() != null) {
                stmt.setInt(6, membresia.getSesionesRestantes());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, membresia.getObservaciones());
            stmt.setInt(8, membresia.getIdMembresia());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar membresía");
            }

            logger.info("Membresía actualizada: {}", membresia.getIdMembresia());
            return membresia;

        } catch (SQLException e) {
            logger.error("Error al actualizar membresía", e);
            throw e;
        }
    }

    // UPDATE STATUS
    public void updateStatus(Integer idMembresia, Integer idEstado) throws SQLException {
        String sql = "UPDATE membresias SET id_estado_membresia = ? WHERE id_membresia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEstado);
            stmt.setInt(2, idMembresia);

            stmt.executeUpdate();
            logger.info("Estado de membresía actualizado: {}", idMembresia);

        } catch (SQLException e) {
            logger.error("Error al actualizar estado de membresía", e);
            throw e;
        }
    }

    // COUNT ACTIVE
    public int countActive() throws SQLException {
        String sql = "SELECT COUNT(*) FROM membresias " +
                "WHERE id_estado_membresia = 1 AND fecha_vencimiento >= CURDATE()";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            logger.error("Error al contar membresías activas", e);
            throw e;
        }
    }

    // MAPPER
    private Membresia mapResultSetToMembresia(ResultSet rs) throws SQLException {
        Membresia membresia = new Membresia();
        membresia.setIdMembresia(rs.getInt("id_membresia"));
        membresia.setIdAtleta(rs.getInt("id_atleta"));
        membresia.setIdTipoMembresia(rs.getInt("id_tipo_membresia"));
        membresia.setIdEstadoMembresia(rs.getInt("id_estado_membresia"));

        Date fechaInicio = rs.getDate("fecha_inicio");
        if (fechaInicio != null) {
            membresia.setFechaInicio(fechaInicio.toLocalDate());
        }

        Date fechaVencimiento = rs.getDate("fecha_vencimiento");
        if (fechaVencimiento != null) {
            membresia.setFechaVencimiento(fechaVencimiento.toLocalDate());
        }

        membresia.setPrecioPagado(rs.getBigDecimal("precio_pagado"));

        int sesionesRestantes = rs.getInt("sesiones_restantes");
        if (!rs.wasNull()) {
            membresia.setSesionesRestantes(sesionesRestantes);
        }

        membresia.setIdUsuarioRegistro(rs.getInt("id_usuario_registro"));

        Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            membresia.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }

        Timestamp fechaMod = rs.getTimestamp("fecha_ultima_modificacion");
        if (fechaMod != null) {
            membresia.setFechaUltimaModificacion(fechaMod.toLocalDateTime());
        }

        membresia.setObservaciones(rs.getString("observaciones"));
        membresia.setNotificacionEnviada(rs.getBoolean("notificacion_enviada"));

        Timestamp fechaNotif = rs.getTimestamp("fecha_notificacion");
        if (fechaNotif != null) {
            membresia.setFechaNotificacion(fechaNotif.toLocalDateTime());
        }

        membresia.setNombreAtleta(rs.getString("nombre_atleta"));
        membresia.setTipoMembresia(rs.getString("tipo_membresia"));
        membresia.setEstadoMembresia(rs.getString("estado_membresia"));
        membresia.setRegistradoPor(rs.getString("registrado_por"));
        membresia.setDiasRestantes(rs.getInt("dias_restantes"));

        return membresia;
    }
}