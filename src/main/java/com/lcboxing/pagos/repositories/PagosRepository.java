package com.lcboxing.pagos.repositories;

import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.pagos.models.Pago;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PagosRepository {

    public List<Pago> findAll() throws SQLException {
        String sql = "SELECT p.*, mp.nombre_metodo, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno) as nombre_atleta " +
                "FROM pagos p " +
                "INNER JOIN metodos_pago mp ON p.id_metodo_pago = mp.id_metodo_pago " +
                "INNER JOIN membresias m ON p.id_membresia = m.id_membresia " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "ORDER BY p.fecha_pago DESC";

        List<Pago> pagos = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                pagos.add(mapResultSetToPago(rs));
            }
        }
        return pagos;
    }

    public Optional<Pago> findById(int id) throws SQLException {
        String sql = "SELECT p.*, mp.nombre_metodo, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno) as nombre_atleta " +
                "FROM pagos p " +
                "INNER JOIN metodos_pago mp ON p.id_metodo_pago = mp.id_metodo_pago " +
                "INNER JOIN membresias m ON p.id_membresia = m.id_membresia " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "WHERE p.id_pago = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToPago(rs));
            }
        }
        return Optional.empty();
    }

    public List<Pago> findByMembresia(int idMembresia) throws SQLException {
        String sql = "SELECT p.*, mp.nombre_metodo, " +
                "CONCAT(a.nombre, ' ', a.apellido_paterno) as nombre_atleta " +
                "FROM pagos p " +
                "INNER JOIN metodos_pago mp ON p.id_metodo_pago = mp.id_metodo_pago " +
                "INNER JOIN membresias m ON p.id_membresia = m.id_membresia " +
                "INNER JOIN atletas a ON m.id_atleta = a.id_atleta " +
                "WHERE p.id_membresia = ? " +
                "ORDER BY p.fecha_pago DESC";

        List<Pago> pagos = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMembresia);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pagos.add(mapResultSetToPago(rs));
            }
        }
        return pagos;
    }

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

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                pago.setIdPago(rs.getInt(1));
            }
            return findById(pago.getIdPago()).orElse(pago);
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pagos WHERE id_pago = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Pago mapResultSetToPago(ResultSet rs) throws SQLException {
        Pago p = new Pago();
        p.setIdPago(rs.getInt("id_pago"));
        p.setIdMembresia(rs.getInt("id_membresia"));
        p.setIdMetodoPago(rs.getInt("id_metodo_pago"));
        p.setMonto(rs.getBigDecimal("monto"));
        p.setReferencia(rs.getString("referencia"));
        p.setFechaPago(rs.getTimestamp("fecha_pago"));
        p.setIdUsuarioRegistro(rs.getInt("id_usuario_registro"));
        p.setConcepto(rs.getString("concepto"));
        p.setComprobanteUrl(rs.getString("comprobante_url"));
        p.setNotas(rs.getString("notas"));
        p.setNombreMetodo(rs.getString("nombre_metodo"));
        p.setNombreAtleta(rs.getString("nombre_atleta"));
        return p;
    }
}
