package com.lcboxing.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TipoMembresia {
    private Integer idTipoMembresia;
    private String nombreTipo;
    private String descripcion;
    private Integer duracionDias;
    private BigDecimal precioBase;
    private Integer sesionesIncluidas;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public TipoMembresia() {
        this.activo = true;
    }

    // Getters y Setters
    public Integer getIdTipoMembresia() { return idTipoMembresia; }
    public void setIdTipoMembresia(Integer idTipoMembresia) { this.idTipoMembresia = idTipoMembresia; }
    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getDuracionDias() { return duracionDias; }
    public void setDuracionDias(Integer duracionDias) { this.duracionDias = duracionDias; }
    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }
    public Integer getSesionesIncluidas() { return sesionesIncluidas; }
    public void setSesionesIncluidas(Integer sesionesIncluidas) { this.sesionesIncluidas = sesionesIncluidas; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}