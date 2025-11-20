package com.lcboxing.membresias.models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Membresia {
    private Integer idMembresia;
    private Integer idAtleta;
    private Integer idTipoMembresia;
    private Integer idEstadoMembresia;
    private Date fechaInicio;
    private Date fechaVencimiento;
    private BigDecimal precioPagado;
    private Integer sesionesRestantes;
    private Integer idUsuarioRegistro;
    private Timestamp fechaRegistro;
    private Timestamp fechaUltimaModificacion;
    private String observaciones;
    private Boolean notificacionEnviada;
    private Timestamp fechaNotificacion;

    // Campos adicionales de joins
    private String nombreAtleta;
    private String nombreTipo;
    private String nombreEstado;

    // Getters y Setters
    public Integer getIdMembresia() { return idMembresia; }
    public void setIdMembresia(Integer idMembresia) { this.idMembresia = idMembresia; }

    public Integer getIdAtleta() { return idAtleta; }
    public void setIdAtleta(Integer idAtleta) { this.idAtleta = idAtleta; }

    public Integer getIdTipoMembresia() { return idTipoMembresia; }
    public void setIdTipoMembresia(Integer idTipoMembresia) { this.idTipoMembresia = idTipoMembresia; }

    public Integer getIdEstadoMembresia() { return idEstadoMembresia; }
    public void setIdEstadoMembresia(Integer idEstadoMembresia) { this.idEstadoMembresia = idEstadoMembresia; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public BigDecimal getPrecioPagado() { return precioPagado; }
    public void setPrecioPagado(BigDecimal precioPagado) { this.precioPagado = precioPagado; }

    public Integer getSesionesRestantes() { return sesionesRestantes; }
    public void setSesionesRestantes(Integer sesionesRestantes) { this.sesionesRestantes = sesionesRestantes; }

    public Integer getIdUsuarioRegistro() { return idUsuarioRegistro; }
    public void setIdUsuarioRegistro(Integer idUsuarioRegistro) { this.idUsuarioRegistro = idUsuarioRegistro; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Timestamp getFechaUltimaModificacion() { return fechaUltimaModificacion; }
    public void setFechaUltimaModificacion(Timestamp fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Boolean getNotificacionEnviada() { return notificacionEnviada; }
    public void setNotificacionEnviada(Boolean notificacionEnviada) { this.notificacionEnviada = notificacionEnviada; }

    public Timestamp getFechaNotificacion() { return fechaNotificacion; }
    public void setFechaNotificacion(Timestamp fechaNotificacion) { this.fechaNotificacion = fechaNotificacion; }

    public String getNombreAtleta() { return nombreAtleta; }
    public void setNombreAtleta(String nombreAtleta) { this.nombreAtleta = nombreAtleta; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }

    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }
}
