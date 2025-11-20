package com.lcboxing.atletas.models;

import java.sql.Date;
import java.sql.Timestamp;

public class Atleta {
    private Integer idAtleta;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String telefono;
    private Date fechaNacimiento;
    private String genero;
    private Timestamp fechaRegistro;
    private Integer idUsuarioRegistro;
    private Boolean activo;
    private Timestamp fechaUltimaModificacion;
    private String notas;

    // Getters y Setters
    public Integer getIdAtleta() { return idAtleta; }
    public void setIdAtleta(Integer idAtleta) { this.idAtleta = idAtleta; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Integer getIdUsuarioRegistro() { return idUsuarioRegistro; }
    public void setIdUsuarioRegistro(Integer idUsuarioRegistro) { this.idUsuarioRegistro = idUsuarioRegistro; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Timestamp getFechaUltimaModificacion() { return fechaUltimaModificacion; }
    public void setFechaUltimaModificacion(Timestamp fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public String getNombreCompleto() {
        return nombre + " " + apellidoPaterno +
                (apellidoMaterno != null ? " " + apellidoMaterno : "");
    }
}
