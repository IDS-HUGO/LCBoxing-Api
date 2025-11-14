package com.lcboxing.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Membresia {
    private Integer idMembresia;
    private Integer idAtleta;
    private Integer idTipoMembresia;
    private Integer idEstadoMembresia;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private BigDecimal precioPagado;
    private Integer sesionesRestantes;
    private Integer idUsuarioRegistro;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaUltimaModificacion;
    private String observaciones;
    private Boolean notificacionEnviada;
    private LocalDateTime fechaNotificacion;

    // Campos calculados/joins
    private String nombreAtleta;
    private String tipoMembresia;
    private String estadoMembresia;
    private String registradoPor;
    private Integer diasRestantes;

    public Membresia() {
        this.idEstadoMembresia = 1; // ACTIVA por defecto
        this.notificacionEnviada = false;
    }

    // Getters y Setters
    public Integer getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(Integer idMembresia) {
        this.idMembresia = idMembresia;
    }

    public Integer getIdAtleta() {
        return idAtleta;
    }

    public void setIdAtleta(Integer idAtleta) {
        this.idAtleta = idAtleta;
    }

    public Integer getIdTipoMembresia() {
        return idTipoMembresia;
    }

    public void setIdTipoMembresia(Integer idTipoMembresia) {
        this.idTipoMembresia = idTipoMembresia;
    }

    public Integer getIdEstadoMembresia() {
        return idEstadoMembresia;
    }

    public void setIdEstadoMembresia(Integer idEstadoMembresia) {
        this.idEstadoMembresia = idEstadoMembresia;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getPrecioPagado() {
        return precioPagado;
    }

    public void setPrecioPagado(BigDecimal precioPagado) {
        this.precioPagado = precioPagado;
    }

    public Integer getSesionesRestantes() {
        return sesionesRestantes;
    }

    public void setSesionesRestantes(Integer sesionesRestantes) {
        this.sesionesRestantes = sesionesRestantes;
    }

    public Integer getIdUsuarioRegistro() {
        return idUsuarioRegistro;
    }

    public void setIdUsuarioRegistro(Integer idUsuarioRegistro) {
        this.idUsuarioRegistro = idUsuarioRegistro;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }

    public void setFechaUltimaModificacion(LocalDateTime fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getNotificacionEnviada() {
        return notificacionEnviada;
    }

    public void setNotificacionEnviada(Boolean notificacionEnviada) {
        this.notificacionEnviada = notificacionEnviada;
    }

    public LocalDateTime getFechaNotificacion() {
        return fechaNotificacion;
    }

    public void setFechaNotificacion(LocalDateTime fechaNotificacion) {
        this.fechaNotificacion = fechaNotificacion;
    }

    public String getNombreAtleta() {
        return nombreAtleta;
    }

    public void setNombreAtleta(String nombreAtleta) {
        this.nombreAtleta = nombreAtleta;
    }

    public String getTipoMembresia() {
        return tipoMembresia;
    }

    public void setTipoMembresia(String tipoMembresia) {
        this.tipoMembresia = tipoMembresia;
    }

    public String getEstadoMembresia() {
        return estadoMembresia;
    }

    public void setEstadoMembresia(String estadoMembresia) {
        this.estadoMembresia = estadoMembresia;
    }

    public String getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(String registradoPor) {
        this.registradoPor = registradoPor;
    }

    public Integer getDiasRestantes() {
        return diasRestantes;
    }

    public void setDiasRestantes(Integer diasRestantes) {
        this.diasRestantes = diasRestantes;
    }
}