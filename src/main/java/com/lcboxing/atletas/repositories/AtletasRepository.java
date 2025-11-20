package com.lcboxing.atletas.repositories;

import com.lcboxing.config.DatabaseConfig;
import com.lcboxing.atletas.models.Atleta;
import com.lcboxing.atletas.models.ContactoEmergencia;
import com.lcboxing.atletas.models.DatosMedicos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AtletasRepository {

    public List<Atleta> findAll() throws SQLException {
        String sql = "SELECT * FROM atletas ORDER BY fecha_registro DESC";
        List<Atleta> atletas = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                atletas.add(mapResultSetToAtleta(rs));
            }
        }
        return atletas;
    }

    public List<Atleta> findActivos() throws SQLException {
        String sql = "SELECT * FROM atletas WHERE activo = TRUE ORDER BY apellido_paterno, nombre";
        List<Atleta> atletas = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                atletas.add(mapResultSetToAtleta(rs));
            }
        }
        return atletas;
    }

    public Optional<Atleta> findById(int id) throws SQLException {
        String sql = "SELECT * FROM atletas WHERE id_atleta = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToAtleta(rs));
            }
        }
        return Optional.empty();
    }

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
            stmt.setDate(6, atleta.getFechaNacimiento());
            stmt.setString(7, atleta.getGenero());
            stmt.setInt(8, atleta.getIdUsuarioRegistro());
            stmt.setBoolean(9, atleta.getActivo() != null ? atleta.getActivo() : true);
            stmt.setString(10, atleta.getNotas());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                atleta.setIdAtleta(rs.getInt(1));
            }

            return findById(atleta.getIdAtleta()).orElse(atleta);
        }
    }

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
            stmt.setDate(6, atleta.getFechaNacimiento());
            stmt.setString(7, atleta.getGenero());
            stmt.setBoolean(8, atleta.getActivo());
            stmt.setString(9, atleta.getNotas());
            stmt.setInt(10, atleta.getIdAtleta());

            stmt.executeUpdate();
            return findById(atleta.getIdAtleta()).orElse(atleta);
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM atletas WHERE id_atleta = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Datos Médicos
    public Optional<DatosMedicos> findDatosMedicosByAtleta(int idAtleta) throws SQLException {
        String sql = "SELECT * FROM datos_medicos WHERE id_atleta = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAtleta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                DatosMedicos datos = new DatosMedicos();
                datos.setIdAtleta(rs.getInt("id_atleta"));
                datos.setTipoSangre(rs.getString("tipo_sangre"));
                datos.setAlergias(rs.getString("alergias"));
                datos.setCondicionesMedicas(rs.getString("condiciones_medicas"));
                datos.setFechaUltimaActualizacion(rs.getTimestamp("fecha_ultima_actualizacion"));
                return Optional.of(datos);
            }
        }
        return Optional.empty();
    }

    public DatosMedicos saveDatosMedicos(DatosMedicos datos) throws SQLException {
        String sql = "INSERT INTO datos_medicos (id_atleta, tipo_sangre, alergias, condiciones_medicas) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE tipo_sangre = ?, alergias = ?, condiciones_medicas = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, datos.getIdAtleta());
            stmt.setString(2, datos.getTipoSangre());
            stmt.setString(3, datos.getAlergias());
            stmt.setString(4, datos.getCondicionesMedicas());
            stmt.setString(5, datos.getTipoSangre());
            stmt.setString(6, datos.getAlergias());
            stmt.setString(7, datos.getCondicionesMedicas());

            stmt.executeUpdate();
            return findDatosMedicosByAtleta(datos.getIdAtleta()).orElse(datos);
        }
    }

    // Contactos de Emergencia
    public List<ContactoEmergencia> findContactosByAtleta(int idAtleta) throws SQLException {
        String sql = "SELECT * FROM contactos_emergencia WHERE id_atleta = ?";
        List<ContactoEmergencia> contactos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAtleta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ContactoEmergencia contacto = new ContactoEmergencia();
                contacto.setIdContactoEmergencia(rs.getInt("id_contacto_emergencia"));
                contacto.setIdAtleta(rs.getInt("id_atleta"));
                contacto.setNombreContacto(rs.getString("nombre_contacto"));
                contacto.setTelefonoContacto(rs.getString("telefono_contacto"));
                contacto.setRelacion(rs.getString("relacion"));
                contacto.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                contactos.add(contacto);
            }
        }
        return contactos;
    }

    public ContactoEmergencia createContacto(ContactoEmergencia contacto) throws SQLException {
        String sql = "INSERT INTO contactos_emergencia (id_atleta, nombre_contacto, telefono_contacto, relacion) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, contacto.getIdAtleta());
            stmt.setString(2, contacto.getNombreContacto());
            stmt.setString(3, contacto.getTelefonoContacto());
            stmt.setString(4, contacto.getRelacion());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                contacto.setIdContactoEmergencia(rs.getInt(1));
            }

            return contacto;
        }
    }

    public boolean deleteContacto(int idContacto) throws SQLException {
        String sql = "DELETE FROM contactos_emergencia WHERE id_contacto_emergencia = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idContacto);
            return stmt.executeUpdate() > 0;
        }
    }

    private Atleta mapResultSetToAtleta(ResultSet rs) throws SQLException {
        Atleta atleta = new Atleta();
        atleta.setIdAtleta(rs.getInt("id_atleta"));
        atleta.setNombre(rs.getString("nombre"));
        atleta.setApellidoPaterno(rs.getString("apellido_paterno"));
        atleta.setApellidoMaterno(rs.getString("apellido_materno"));
        atleta.setEmail(rs.getString("email"));
        atleta.setTelefono(rs.getString("telefono"));
        atleta.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        atleta.setGenero(rs.getString("genero"));
        atleta.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        atleta.setIdUsuarioRegistro(rs.getInt("id_usuario_registro"));
        atleta.setActivo(rs.getBoolean("activo"));
        atleta.setFechaUltimaModificacion(rs.getTimestamp("fecha_ultima_modificacion"));
        atleta.setNotas(rs.getString("notas"));
        return atleta;
    }
}