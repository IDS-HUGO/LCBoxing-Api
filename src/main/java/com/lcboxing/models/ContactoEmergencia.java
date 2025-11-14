package com.lcboxing.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

class ContactoEmergencia {
    private Integer idContactoEmergencia;
    private Integer idAtleta;
    private String nombreContacto;
    private String telefonoContacto;
    private String relacion;
    private LocalDateTime fechaRegistro;

    public ContactoEmergencia() {}

    // Getters y Setters
    public Integer getIdContactoEmergencia() { return idContactoEmergencia; }
    public void setIdContactoEmergencia(Integer idContactoEmergencia) { this.idContactoEmergencia = idContactoEmergencia; }
    public Integer getIdAtleta() { return idAtleta; }
    public void setIdAtleta(Integer idAtleta) { this.idAtleta = idAtleta; }
    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }
    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }
    public String getRelacion() { return relacion; }
    public void setRelacion(String relacion) { this.relacion = relacion; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}