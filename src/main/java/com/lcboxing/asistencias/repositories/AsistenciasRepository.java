package com.lcboxing.asistencias.repositories;

import com.lcboxing.asistencias.models.Asistencia;
import com.lcboxing.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AsistenciasRepository {

    public List<Asistencia> findByFecha(LocalDate fecha) throws SQLException {
        String sql = "SELECT a.*, " +
                "CONCAT(at.nombre, ' ', at.apellido_paterno) as nombre_atleta " +
                "FROM asistencias a " +
                "INNER JOIN atletas at ON a.id_atleta = at.id_atleta " +
                "WHERE a.fecha_asistencia = ? " +
                "ORDER BY a.hora_entrada DESC";

        List<Asistencia> asistencias = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(fecha));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                asistencias.add(mapResultSetToAsistencia(rs));
            }
        }
        return asistencias;
    }

    public List<Asistencia> findByAtleta(int idAtleta) throws SQLException {
        String sql = "SELECT a.*, " +
                "CONCAT(at.nombre, ' ', at.apellido_paterno) as nombre_atleta " +
                "FROM asistencias a " +
                "INNER JOIN atletas at ON a.id_atleta = at.id_atleta " +
                "WHERE a.id_atleta = ? " +
                "ORDER BY a.fecha_asistencia DESC, a.hora_entrada DESC " +
                "LIMIT 50";

        List<Asistencia> asistencias = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAtleta);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                asistencias.add(mapResultSetToAsistencia(rs));
            }
        }
        return asistencias;
    }

    public Optional<Asistencia> findById(int id) throws SQLException {
        String sql = "SELECT a.*, " +
                "CONCAT(at.nombre, ' ', at.apellido_paterno) as nombre_atleta " +
                "FROM asistencias a " +
                "INNER JOIN atletas at ON a.id_atleta = at.id_atleta " +
                "WHERE a.id_asistencia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToAsistencia(rs));
            }
        }
        return Optional.empty();
    }

    public Asistencia create(Asistencia asistencia) throws SQLException {
        String sql = "INSERT INTO asistencias (id_atleta, id_membresia, fecha_asistencia, " +
                "hora_entrada, id_usuario_registro_entrada, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, asistencia.getIdAtleta());
            stmt.setInt(2, asistencia.getIdMembresia());
            stmt.setDate(3, java.sql.Date.valueOf(asistencia.getFechaAsistencia()));
            stmt.setTime(4, asistencia.getHoraEntrada());
            stmt.setInt(5, asistencia.getIdUsuarioRegistroEntrada());
            stmt.setString(6, asistencia.getObservaciones());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                asistencia.setIdAsistencia(rs.getInt(1));
            }
            return findById(asistencia.getIdAsistencia()).orElse(asistencia);
        }
    }

    public Asistencia registrarSalida(int idAsistencia, Time horaSalida, int idUsuarioSalida) throws SQLException {
        String sql = "UPDATE asistencias SET hora_salida = ?, id_usuario_registro_salida = ?, " +
                "fecha_registro_salida = CURRENT_TIMESTAMP WHERE id_asistencia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTime(1, horaSalida);
            stmt.setInt(2, idUsuarioSalida);
            stmt.setInt(3, idAsistencia);
            stmt.executeUpdate();
            return findById(idAsistencia).orElse(null);
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM asistencias WHERE id_asistencia = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Asistencia mapResultSetToAsistencia(ResultSet rs) throws SQLException {
        Asistencia a = new Asistencia();
        a.setIdAsistencia(rs.getInt("id_asistencia"));
        a.setIdAtleta(rs.getInt("id_atleta"));
        a.setIdMembresia(rs.getInt("id_membresia"));
        a.setFechaAsistencia(rs.getDate("fecha_asistencia").toLocalDate());
        a.setHoraEntrada(rs.getTime("hora_entrada"));
        a.setHoraSalida(rs.getTime("hora_salida"));
        a.setDuracionMinutos(rs.getObject("duracion_minutos") != null ? rs.getInt("duracion_minutos") : null);
        a.setIdUsuarioRegistroEntrada(rs.getInt("id_usuario_registro_entrada"));
        a.setIdUsuarioRegistroSalida(rs.getObject("id_usuario_registro_salida") != null ? rs.getInt("id_usuario_registro_salida") : null);
        a.setObservaciones(rs.getString("observaciones"));
        a.setFechaRegistroEntrada(rs.getTimestamp("fecha_registro_entrada"));
        a.setFechaRegistroSalida(rs.getTimestamp("fecha_registro_salida"));
        a.setNombreAtleta(rs.getString("nombre_atleta"));
        return a;
    }
}

