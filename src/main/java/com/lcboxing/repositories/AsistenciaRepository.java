package com.lcboxing.repositories;

import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.models.Asistencia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AsistenciaRepository {
    private static final Logger logger = LoggerFactory.getLogger(AsistenciaRepository.class);

    // CREATE - Registrar entrada
    public Asistencia create(Asistencia asistencia) throws SQLException {
        String sql = "INSERT INTO asistencias (id_atleta, id_membresia, fecha_asistencia, " +
                "hora_entrada, id_usuario_registro_entrada, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, asistencia.getIdAtleta());
            stmt.setInt(2, asistencia.getIdMembresia());
            stmt.setDate(3, Date.valueOf(asistencia.getFechaAsistencia()));
            stmt.setTime(4, Time.valueOf(asistencia.getHoraEntrada()));
            stmt.setInt(5, asistencia.getIdUsuarioRegistroEntrada());
            stmt.setString(6, asistencia.getObservaciones());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear asistencia");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    asistencia.setIdAsistencia(generatedKeys.getInt(1));
                }
            }

            logger.info("Asistencia creada exitosamente con ID: {}", asistencia.getIdAsistencia());
            return asistencia;

        } catch (SQLException e) {
            logger.error("Error al crear asistencia", e);
            throw e;
        }
    }

    // UPDATE - Registrar salida
    public Asistencia registrarSalida(Integer idAsistencia, Integer idUsuarioSalida, String observaciones) throws SQLException {
        String sql = "UPDATE asistencias SET hora_salida = CURRENT_TIME, " +
                "id_usuario_registro_salida = ?, fecha_registro_salida = CURRENT_TIMESTAMP, " +
                "observaciones = CASE WHEN ? IS NOT NULL THEN ? ELSE observaciones END " +
                "WHERE id_asistencia = ? AND hora_salida IS NULL";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuarioSalida);
            stmt.setString(2, observaciones);
            stmt.setString(3, observaciones);
            stmt.setInt(4, idAsistencia);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al registrar salida. Verifica que la asistencia existe y no tenga salida registrada.");
            }

            logger.info("Salida registrada para asistencia: {}", idAsistencia);
            return findById(idAsistencia).orElseThrow(() -> new SQLException("Asistencia no encontrada"));

        } catch (SQLException e) {
            logger.error("Error al registrar salida", e);
            throw e;
        }
    }

    // READ BY ID
    public Optional<Asistencia> findById(Integer id) throws SQLException {
        String sql = "SELECT ast.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "CONCAT(u_entrada.nombre, ' ', u_entrada.apellido_paterno) as registrado_entrada_por, " +
                "CONCAT(u_salida.nombre, ' ', u_salida.apellido_paterno) as registrado_salida_por, " +
                "CASE WHEN ast.hora_salida IS NULL THEN 'EN GIMNASIO' ELSE 'SALIÓ' END as estado_actual " +
                "FROM asistencias ast " +
                "INNER JOIN atletas a ON ast.id_atleta = a.id_atleta " +
                "INNER JOIN usuarios u_entrada ON ast.id_usuario_registro_entrada = u_entrada.id_usuario " +
                "LEFT JOIN usuarios u_salida ON ast.id_usuario_registro_salida = u_salida.id_usuario " +
                "WHERE ast.id_asistencia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAsistencia(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error("Error al buscar asistencia por ID: {}", id, e);
            throw e;
        }
    }

    // GET TODAY'S ATTENDANCES
    public List<Asistencia> findToday() throws SQLException {
        String sql = "SELECT ast.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "CONCAT(u_entrada.nombre, ' ', u_entrada.apellido_paterno) as registrado_entrada_por, " +
                "CONCAT(u_salida.nombre, ' ', u_salida.apellido_paterno) as registrado_salida_por, " +
                "CASE WHEN ast.hora_salida IS NULL THEN 'EN GIMNASIO' ELSE 'SALIÓ' END as estado_actual " +
                "FROM asistencias ast " +
                "INNER JOIN atletas a ON ast.id_atleta = a.id_atleta " +
                "INNER JOIN usuarios u_entrada ON ast.id_usuario_registro_entrada = u_entrada.id_usuario " +
                "LEFT JOIN usuarios u_salida ON ast.id_usuario_registro_salida = u_salida.id_usuario " +
                "WHERE ast.fecha_asistencia = CURDATE() " +
                "ORDER BY ast.hora_entrada DESC";

        List<Asistencia> asistencias = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                asistencias.add(mapResultSetToAsistencia(rs));
            }

            return asistencias;

        } catch (SQLException e) {
            logger.error("Error al obtener asistencias del día", e);
            throw e;
        }
    }

    // GET CURRENTLY IN GYM
    public List<Asistencia> findCurrentlyInGym() throws SQLException {
        String sql = "SELECT ast.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "CONCAT(u_entrada.nombre, ' ', u_entrada.apellido_paterno) as registrado_entrada_por, " +
                "CONCAT(u_salida.nombre, ' ', u_salida.apellido_paterno) as registrado_salida_por, " +
                "'EN GIMNASIO' as estado_actual " +
                "FROM asistencias ast " +
                "INNER JOIN atletas a ON ast.id_atleta = a.id_atleta " +
                "INNER JOIN usuarios u_entrada ON ast.id_usuario_registro_entrada = u_entrada.id_usuario " +
                "LEFT JOIN usuarios u_salida ON ast.id_usuario_registro_salida = u_salida.id_usuario " +
                "WHERE ast.fecha_asistencia = CURDATE() AND ast.hora_salida IS NULL " +
                "ORDER BY ast.hora_entrada DESC";

        List<Asistencia> asistencias = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                asistencias.add(mapResultSetToAsistencia(rs));
            }

            return asistencias;

        } catch (SQLException e) {
            logger.error("Error al obtener atletas en gimnasio", e);
            throw e;
        }
    }

    // FIND BY ATHLETE
    public List<Asistencia> findByAtleta(Integer idAtleta) throws SQLException {
        String sql = "SELECT ast.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "CONCAT(u_entrada.nombre, ' ', u_entrada.apellido_paterno) as registrado_entrada_por, " +
                "CONCAT(u_salida.nombre, ' ', u_salida.apellido_paterno) as registrado_salida_por, " +
                "CASE WHEN ast.hora_salida IS NULL THEN 'EN GIMNASIO' ELSE 'SALIÓ' END as estado_actual " +
                "FROM asistencias ast " +
                "INNER JOIN atletas a ON ast.id_atleta = a.id_atleta " +
                "INNER JOIN usuarios u_entrada ON ast.id_usuario_registro_entrada = u_entrada.id_usuario " +
                "LEFT JOIN usuarios u_salida ON ast.id_usuario_registro_salida = u_salida.id_usuario " +
                "WHERE ast.id_atleta = ? " +
                "ORDER BY ast.fecha_asistencia DESC, ast.hora_entrada DESC " +
                "LIMIT 50";

        List<Asistencia> asistencias = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAtleta);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    asistencias.add(mapResultSetToAsistencia(rs));
                }
            }

            return asistencias;

        } catch (SQLException e) {
            logger.error("Error al buscar asistencias por atleta", e);
            throw e;
        }
    }

    // FIND BY DATE RANGE
    public List<Asistencia> findByDateRange(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = "SELECT ast.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno, ' ', IFNULL(a.apellido_materno, '')) as nombre_atleta, " +
                "CONCAT(u_entrada.nombre, ' ', u_entrada.apellido_paterno) as registrado_entrada_por, " +
                "CONCAT(u_salida.nombre, ' ', u_salida.apellido_paterno) as registrado_salida_por, " +
                "CASE WHEN ast.hora_salida IS NULL THEN 'EN GIMNASIO' ELSE 'SALIÓ' END as estado_actual " +
                "FROM asistencias ast " +
                "INNER JOIN atletas a ON ast.id_atleta = a.id_atleta " +
                "INNER JOIN usuarios u_entrada ON ast.id_usuario_registro_entrada = u_entrada.id_usuario " +
                "LEFT JOIN usuarios u_salida ON ast.id_usuario_registro_salida = u_salida.id_usuario " +
                "WHERE ast.fecha_asistencia BETWEEN ? AND ? " +
                "ORDER BY ast.fecha_asistencia DESC, ast.hora_entrada DESC";

        List<Asistencia> asistencias = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    asistencias.add(mapResultSetToAsistencia(rs));
                }
            }

            return asistencias;

        } catch (SQLException e) {
            logger.error("Error al buscar asistencias por rango de fechas", e);
            throw e;
        }
    }

    // COUNT TODAY
    public int countToday() throws SQLException {
        String sql = "SELECT COUNT(*) FROM asistencias WHERE fecha_asistencia = CURDATE()";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            logger.error("Error al contar asistencias del día", e);
            throw e;
        }
    }

    // COUNT CURRENTLY IN GYM
    public int countCurrentlyInGym() throws SQLException {
        String sql = "SELECT COUNT(*) FROM asistencias WHERE fecha_asistencia = CURDATE() AND hora_salida IS NULL";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            logger.error("Error al contar atletas en gimnasio", e);
            throw e;
        }
    }

    // MAPPER
    private Asistencia mapResultSetToAsistencia(ResultSet rs) throws SQLException {
        Asistencia asistencia = new Asistencia();
        asistencia.setIdAsistencia(rs.getInt("id_asistencia"));
        asistencia.setIdAtleta(rs.getInt("id_atleta"));
        asistencia.setIdMembresia(rs.getInt("id_membresia"));

        Date fechaAsistencia = rs.getDate("fecha_asistencia");
        if (fechaAsistencia != null) {
            asistencia.setFechaAsistencia(fechaAsistencia.toLocalDate());
        }

        Time horaEntrada = rs.getTime("hora_entrada");
        if (horaEntrada != null) {
            asistencia.setHoraEntrada(horaEntrada.toLocalTime());
        }

        Time horaSalida = rs.getTime("hora_salida");
        if (horaSalida != null) {
            asistencia.setHoraSalida(horaSalida.toLocalTime());
        }

        int duracionMinutos = rs.getInt("duracion_minutos");
        if (!rs.wasNull()) {
            asistencia.setDuracionMinutos(duracionMinutos);
        }

        asistencia.setIdUsuarioRegistroEntrada(rs.getInt("id_usuario_registro_entrada"));

        int idUsuarioSalida = rs.getInt("id_usuario_registro_salida");
        if (!rs.wasNull()) {
            asistencia.setIdUsuarioRegistroSalida(idUsuarioSalida);
        }

        asistencia.setObservaciones(rs.getString("observaciones"));

        Timestamp fechaRegistroEntrada = rs.getTimestamp("fecha_registro_entrada");
        if (fechaRegistroEntrada != null) {
            asistencia.setFechaRegistroEntrada(fechaRegistroEntrada.toLocalDateTime());
        }

        Timestamp fechaRegistroSalida = rs.getTimestamp("fecha_registro_salida");
        if (fechaRegistroSalida != null) {
            asistencia.setFechaRegistroSalida(fechaRegistroSalida.toLocalDateTime());
        }

        asistencia.setNombreAtleta(rs.getString("nombre_atleta"));
        asistencia.setRegistradoEntradaPor(rs.getString("registrado_entrada_por"));
        asistencia.setRegistradoSalidaPor(rs.getString("registrado_salida_por"));
        asistencia.setEstadoActual(rs.getString("estado_actual"));

        return asistencia;
    }
}