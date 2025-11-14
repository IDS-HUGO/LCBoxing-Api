package com.lcboxing.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalTime;

public class Asistencia {
    private Integer idAsistencia;
    private Integer idAtleta;
    private Integer idMembresia;
    private LocalDate fechaAsistencia;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private Integer duracionMinutos;
    private Integer idUsuarioRegistroEntrada;
    private Integer idUsuarioRegistroSalida;
    private String observaciones;
    private LocalDateTime fechaRegistroEntrada;
    private LocalDateTime fechaRegistroSalida;

    // Campos calculados
    private String nombreAtleta;
    private String registradoEntradaPor;
    private String registradoSalidaPor;
    private String estadoActual;

    public Asistencia() {}

    // Getters y Setters
    public Integer getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(Integer idAsistencia) { this.idAsistencia = idAsistencia; }
    public Integer getIdAtleta() { return idAtleta; }
    public void setIdAtleta(Integer idAtleta) { this.idAtleta = idAtleta; }
    public Integer getIdMembresia() { return idMembresia; }
    public void setIdMembresia(Integer idMembresia) { this.idMembresia = idMembresia; }
    public LocalDate getFechaAsistencia() { return fechaAsistencia; }
    public void setFechaAsistencia(LocalDate fechaAsistencia) { this.fechaAsistencia = fechaAsistencia; }
    public LocalTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada = horaEntrada; }
    public LocalTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalTime horaSalida) { this.horaSalida = horaSalida; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public Integer getIdUsuarioRegistroEntrada() { return idUsuarioRegistroEntrada; }
    public void setIdUsuarioRegistroEntrada(Integer idUsuarioRegistroEntrada) { this.idUsuarioRegistroEntrada = idUsuarioRegistroEntrada; }
    public Integer getIdUsuarioRegistroSalida() { return idUsuarioRegistroSalida; }
    public void setIdUsuarioRegistroSalida(Integer idUsuarioRegistroSalida) { this.idUsuarioRegistroSalida = idUsuarioRegistroSalida; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getFechaRegistroEntrada() { return fechaRegistroEntrada; }
    public void setFechaRegistroEntrada(LocalDateTime fechaRegistroEntrada) { this.fechaRegistroEntrada = fechaRegistroEntrada; }
    public LocalDateTime getFechaRegistroSalida() { return fechaRegistroSalida; }
    public void setFechaRegistroSalida(LocalDateTime fechaRegistroSalida) { this.fechaRegistroSalida = fechaRegistroSalida; }
    public String getNombreAtleta() { return nombreAtleta; }
    public void setNombreAtleta(String nombreAtleta) { this.nombreAtleta = nombreAtleta; }
    public String getRegistradoEntradaPor() { return registradoEntradaPor; }
    public void setRegistradoEntradaPor(String registradoEntradaPor) { this.registradoEntradaPor = registradoEntradaPor; }
    public String getRegistradoSalidaPor() { return registradoSalidaPor; }
    public void setRegistradoSalidaPor(String registradoSalidaPor) { this.registradoSalidaPor = registradoSalidaPor; }
    public String getEstadoActual() { return estadoActual; }
    public void setEstadoActual(String estadoActual) { this.estadoActual = estadoActual; }
}