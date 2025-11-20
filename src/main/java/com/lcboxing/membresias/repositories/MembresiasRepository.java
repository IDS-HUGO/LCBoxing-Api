package com.lcboxing.membresias.repositories;

import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.membresias.models.Membresia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembresiasRepository {

    public List<Membresia> findAll() throws SQLException {
        String sql = "SELECT m.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "tm.nombre_tipo, em.nombre_estado " +
                "FROM membresias m " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN estados_membresia em ON m.id_estado_membresia = em.id_estado_membresia " +
                "ORDER BY m.fecha_registro DESC";

        List<Membresia> membresias = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                membresias.add(mapResultSetToMembresia(rs));
            }
        }
        return membresias;
    }

    public List<Membresia> findByAtleta(int idAtleta) throws SQLException {
        String sql = "SELECT m.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "tm.nombre_tipo, em.nombre_estado " +
                "FROM membresias m " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN estados_membresia em ON m.id_estado_membresia = em.id_estado_membresia " +
                "WHERE m.id_atleta = ? " +
                "ORDER BY m.fecha_registro DESC";

        List<Membresia> membresias = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAtleta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                membresias.add(mapResultSetToMembresia(rs));
            }
        }
        return membresias;
    }

    public Optional<Membresia> findById(int id) throws SQLException {
        String sql = "SELECT m.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "tm.nombre_tipo, em.nombre_estado " +
                "FROM membresias m " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN estados_membresia em ON m.id_estado_membresia = em.id_estado_membresia " +
                "WHERE m.id_membresia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToMembresia(rs));
            }
        }
        return Optional.empty();
    }

    public Membresia create(Membresia membresia) throws SQLException {
        String sql = "INSERT INTO membresias (id_atleta, id_tipo_membresia, id_estado_membresia, " +
                "fecha_inicio, fecha_vencimiento, precio_pagado, sesiones_restantes, " +
                "id_usuario_registro, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, membresia.getIdAtleta());
            stmt.setInt(2, membresia.getIdTipoMembresia());
            stmt.setInt(3, membresia.getIdEstadoMembresia() != null ? membresia.getIdEstadoMembresia() : 1);
            stmt.setDate(4, membresia.getFechaInicio());
            stmt.setDate(5, membresia.getFechaVencimiento());
            stmt.setBigDecimal(6, membresia.getPrecioPagado());

            if (membresia.getSesionesRestantes() != null) {
                stmt.setInt(7, membresia.getSesionesRestantes());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setInt(8, membresia.getIdUsuarioRegistro());
            stmt.setString(9, membresia.getObservaciones());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                membresia.setIdMembresia(rs.getInt(1));
            }

            return findById(membresia.getIdMembresia()).orElse(membresia);
        }
    }

    public Membresia update(Membresia membresia) throws SQLException {
        String sql = "UPDATE membresias SET id_estado_membresia = ?, fecha_inicio = ?, " +
                "fecha_vencimiento = ?, precio_pagado = ?, sesiones_restantes = ?, " +
                "observaciones = ? WHERE id_membresia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, membresia.getIdEstadoMembresia());
            stmt.setDate(2, membresia.getFechaInicio());
            stmt.setDate(3, membresia.getFechaVencimiento());
            stmt.setBigDecimal(4, membresia.getPrecioPagado());

            if (membresia.getSesionesRestantes() != null) {
                stmt.setInt(5, membresia.getSesionesRestantes());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setString(6, membresia.getObservaciones());
            stmt.setInt(7, membresia.getIdMembresia());

            stmt.executeUpdate();
            return findById(membresia.getIdMembresia()).orElse(membresia);
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM membresias WHERE id_membresia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Membresia> findVencimientos(int dias) throws SQLException {
        String sql = "SELECT m.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "tm.nombre_tipo, em.nombre_estado " +
                "FROM membresias m " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN estados_membresia em ON m.id_estado_membresia = em.id_estado_membresia " +
                "WHERE m.id_estado_membresia = 1 " +
                "AND m.fecha_vencimiento BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                "AND a.activo = TRUE " +
                "ORDER BY m.fecha_vencimiento ASC";

        List<Membresia> membresias = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dias);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                membresias.add(mapResultSetToMembresia(rs));
            }
        }
        return membresias;
    }

    private Membresia mapResultSetToMembresia(ResultSet rs) throws SQLException {
        Membresia m = new Membresia();
        m.setIdMembresia(rs.getInt("id_membresia"));
        m.setIdAtleta(rs.getInt("id_atleta"));
        m.setIdTipoMembresia(rs.getInt("id_tipo_membresia"));
        m.setIdEstadoMembresia(rs.getInt("id_estado_membresia"));
        m.setFechaInicio(rs.getDate("fecha_inicio"));
        m.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
        m.setPrecioPagado(rs.getBigDecimal("precio_pagado"));
        m.setSesionesRestantes(rs.getObject("sesiones_restantes") != null ? rs.getInt("sesiones_restantes") : null);
        m.setIdUsuarioRegistro(rs.getInt("id_usuario_registro"));
        m.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        m.setFechaUltimaModificacion(rs.getTimestamp("fecha_ultima_modificacion"));
        m.setObservaciones(rs.getString("observaciones"));
        m.setNotificacionEnviada(rs.getBoolean("notificacion_enviada"));
        m.setFechaNotificacion(rs.getTimestamp("fecha_notificacion"));
        m.setNombreAtleta(rs.getString("nombre_atleta"));
        m.setNombreTipo(rs.getString("nombre_tipo"));
        m.setNombreEstado(rs.getString("nombre_estado"));
        return m;
    }
}
