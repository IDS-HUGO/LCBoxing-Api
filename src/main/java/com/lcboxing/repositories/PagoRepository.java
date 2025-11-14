package com.lcboxing.repositories;

import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.models.Pago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PagoRepository {
    private static final Logger logger = LoggerFactory.getLogger(PagoRepository.class);

    // CREATE
    public Pago create(Pago pago) throws SQLException {
        String sql = "INSERT INTO pagos (id_membresia, id_metodo_pago, monto, referencia, " +
                "id_usuario_registro, concepto, comprobante_url, notas) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pago.getIdMembresia());
            stmt.setInt(2, pago.getIdMetodoPago());
            stmt.setBigDecimal(3, pago.getMonto());
            stmt.setString(4, pago.getReferencia());
            stmt.setInt(5, pago.getIdUsuarioRegistro());
            stmt.setString(6, pago.getConcepto());
            stmt.setString(7, pago.getComprobanteUrl());
            stmt.setString(8, pago.getNotas());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear pago");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pago.setIdPago(generatedKeys.getInt(1));
                }
            }

            logger.info("Pago creado exitosamente con ID: {}", pago.getIdPago());
            return pago;

        } catch (SQLException e) {
            logger.error("Error al crear pago", e);
            throw e;
        }
    }

    // READ ALL
    public List<Pago> findAll() throws SQLException {
        String sql = "SELECT p.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno) as nombre_atleta, " +
                "tm.nombre_tipo as tipo_membresia, " +
                "mp.nombre_metodo as metodo_pago, " +
                "CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por " +
                "FROM pagos p " +
                "INNER JOIN membresias m ON p.id_membresia = m.id_membresia " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN metodos_pago mp ON p.id_metodo_pago = mp.id_metodo_pago " +
                "LEFT JOIN usuarios u ON p.id_usuario_registro = u.id_usuario " +
                "ORDER BY p.fecha_pago DESC";

        List<Pago> pagos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pagos.add(mapResultSetToPago(rs));
            }

            return pagos;

        } catch (SQLException e) {
            logger.error("Error al obtener pagos", e);
            throw e;
        }
    }

    // READ BY ID
    public Optional<Pago> findById(Integer id) throws SQLException {
        String sql = "SELECT p.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno) as nombre_atleta, " +
                "tm.nombre_tipo as tipo_membresia, " +
                "mp.nombre_metodo as metodo_pago, " +
                "CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por " +
                "FROM pagos p " +
                "INNER JOIN membresias m ON p.id_membresia = m.id_membresia " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN metodos_pago mp ON p.id_metodo_pago = mp.id_metodo_pago " +
                "LEFT JOIN usuarios u ON p.id_usuario_registro = u.id_usuario " +
                "WHERE p.id_pago = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPago(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error("Error al buscar pago por ID: {}", id, e);
            throw e;
        }
    }

    // FIND BY MEMBRESIA
    public List<Pago> findByMembresia(Integer idMembresia) throws SQLException {
        String sql = "SELECT p.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno) as nombre_atleta, " +
                "tm.nombre_tipo as tipo_membresia, " +
                "mp.nombre_metodo as metodo_pago, " +
                "CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por " +
                "FROM pagos p " +
                "INNER JOIN membresias m ON p.id_membresia = m.id_membresia " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN metodos_pago mp ON p.id_metodo_pago = mp.id_metodo_pago " +
                "LEFT JOIN usuarios u ON p.id_usuario_registro = u.id_usuario " +
                "WHERE p.id_membresia = ? " +
                "ORDER BY p.fecha_pago DESC";

        List<Pago> pagos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMembresia);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pagos.add(mapResultSetToPago(rs));
                }
            }

            return pagos;

        } catch (SQLException e) {
            logger.error("Error al buscar pagos por membresía", e);
            throw e;
        }
    }

    // FIND BY DATE RANGE
    public List<Pago> findByDateRange(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = "SELECT p.*, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno) as nombre_atleta, " +
                "tm.nombre_tipo as tipo_membresia, " +
                "mp.nombre_metodo as metodo_pago, " +
                "CONCAT(u.nombre, ' ', u.apellido_paterno) as registrado_por " +
                "FROM pagos p " +
                "INNER JOIN membresias m ON p.id_membresia = m.id_membresia " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "INNER JOIN tipos_membresia tm ON m.id_tipo_membresia = tm.id_tipo_membresia " +
                "INNER JOIN metodos_pago mp ON p.id_metodo_pago = mp.id_metodo_pago " +
                "LEFT JOIN usuarios u ON p.id_usuario_registro = u.id_usuario " +
                "WHERE DATE(p.fecha_pago) BETWEEN ? AND ? " +
                "ORDER BY p.fecha_pago DESC";

        List<Pago> pagos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pagos.add(mapResultSetToPago(rs));
                }
            }

            return pagos;

        } catch (SQLException e) {
            logger.error("Error al buscar pagos por rango de fechas", e);
            throw e;
        }
    }

    // GET TODAY'S INCOME
    public BigDecimal getTodayIncome() throws SQLException {
        String sql = "SELECT IFNULL(SUM(monto), 0) FROM pagos WHERE DATE(fecha_pago) = CURDATE()";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;

        } catch (SQLException e) {
            logger.error("Error al obtener ingresos del día", e);
            throw e;
        }
    }

    // GET MONTH INCOME
    public BigDecimal getMonthIncome() throws SQLException {
        String sql = "SELECT IFNULL(SUM(monto), 0) FROM pagos " +
                "WHERE MONTH(fecha_pago) = MONTH(CURDATE()) AND YEAR(fecha_pago) = YEAR(CURDATE())";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;

        } catch (SQLException e) {
            logger.error("Error al obtener ingresos del mes", e);
            throw e;
        }
    }

    // GET YEAR INCOME
    public BigDecimal getYearIncome() throws SQLException {
        String sql = "SELECT IFNULL(SUM(monto), 0) FROM pagos WHERE YEAR(fecha_pago) = YEAR(CURDATE())";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;

        } catch (SQLException e) {
            logger.error("Error al obtener ingresos del año", e);
            throw e;
        }
    }

    // UPDATE
    public Pago update(Pago pago) throws SQLException {
        String sql = "UPDATE pagos SET id_metodo_pago = ?, monto = ?, referencia = ?, " +
                "concepto = ?, comprobante_url = ?, notas = ? WHERE id_pago = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pago.getIdMetodoPago());
            stmt.setBigDecimal(2, pago.getMonto());
            stmt.setString(3, pago.getReferencia());
            stmt.setString(4, pago.getConcepto());
            stmt.setString(5, pago.getComprobanteUrl());
            stmt.setString(6, pago.getNotas());
            stmt.setInt(7, pago.getIdPago());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar pago");
            }

            logger.info("Pago actualizado: {}", pago.getIdPago());
            return pago;

        } catch (SQLException e) {
            logger.error("Error al actualizar pago", e);
            throw e;
        }
    }

    // DELETE
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM pagos WHERE id_pago = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al eliminar pago, no existe el ID: " + id);
            }

            logger.info("Pago eliminado: {}", id);

        } catch (SQLException e) {
            logger.error("Error al eliminar pago", e);
            throw e;
        }
    }

    // MAPPER
    private Pago mapResultSetToPago(ResultSet rs) throws SQLException {
        Pago pago = new Pago();
        pago.setIdPago(rs.getInt("id_pago"));
        pago.setIdMembresia(rs.getInt("id_membresia"));
        pago.setIdMetodoPago(rs.getInt("id_metodo_pago"));
        pago.setMonto(rs.getBigDecimal("monto"));
        pago.setReferencia(rs.getString("referencia"));

        Timestamp fechaPago = rs.getTimestamp("fecha_pago");
        if (fechaPago != null) {
            pago.setFechaPago(fechaPago.toLocalDateTime());
        }

        pago.setIdUsuarioRegistro(rs.getInt("id_usuario_registro"));
        pago.setConcepto(rs.getString("concepto"));
        pago.setComprobanteUrl(rs.getString("comprobante_url"));
        pago.setNotas(rs.getString("notas"));
        pago.setNombreAtleta(rs.getString("nombre_atleta"));
        pago.setTipoMembresia(rs.getString("tipo_membresia"));
        pago.setMetodoPago(rs.getString("metodo_pago"));
        pago.setRegistradoPor(rs.getString("registrado_por"));

        return pago;
    }
}