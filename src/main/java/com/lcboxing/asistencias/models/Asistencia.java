package com.lcboxing.asistencias.models;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDate;

public class Asistencia {
    private Integer idAsistencia;
    private Integer idAtleta;
    private Integer idMembresia;
    private LocalDate fechaAsistencia;  // Cambiado de Date a LocalDate
    private Time horaEntrada;
    private Time horaSalida;
    private Integer duracionMinutos;
    private Integer idUsuarioRegistroEntrada;
    private Integer idUsuarioRegistroSalida;
    private String observaciones;
    private Timestamp fechaRegistroEntrada;
    private Timestamp fechaRegistroSalida;
    private String nombreAtleta;

    // Getters y Setters
    public Integer getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(Integer idAsistencia) { this.idAsistencia = idAsistencia; }
    public Integer getIdAtleta() { return idAtleta; }
    public void setIdAtleta(Integer idAtleta) { this.idAtleta = idAtleta; }
    public Integer getIdMembresia() { return idMembresia; }
    public void setIdMembresia(Integer idMembresia) { this.idMembresia = idMembresia; }
    public LocalDate getFechaAsistencia() { return fechaAsistencia; }
    public void setFechaAsistencia(LocalDate fechaAsistencia) { this.fechaAsistencia = fechaAsistencia; }
    public Time getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(Time horaEntrada) { this.horaEntrada = horaEntrada; }
    public Time getHoraSalida() { return horaSalida; }
    public void setHoraSalida(Time horaSalida) { this.horaSalida = horaSalida; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public Integer getIdUsuarioRegistroEntrada() { return idUsuarioRegistroEntrada; }
    public void setIdUsuarioRegistroEntrada(Integer idUsuarioRegistroEntrada) { this.idUsuarioRegistroEntrada = idUsuarioRegistroEntrada; }
    public Integer getIdUsuarioRegistroSalida() { return idUsuarioRegistroSalida; }
    public void setIdUsuarioRegistroSalida(Integer idUsuarioRegistroSalida) { this.idUsuarioRegistroSalida = idUsuarioRegistroSalida; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Timestamp getFechaRegistroEntrada() { return fechaRegistroEntrada; }
    public void setFechaRegistroEntrada(Timestamp fechaRegistroEntrada) { this.fechaRegistroEntrada = fechaRegistroEntrada; }
    public Timestamp getFechaRegistroSalida() { return fechaRegistroSalida; }
    public void setFechaRegistroSalida(Timestamp fechaRegistroSalida) { this.fechaRegistroSalida = fechaRegistroSalida; }
    public String getNombreAtleta() { return nombreAtleta; }
    public void setNombreAtleta(String nombreAtleta) { this.nombreAtleta = nombreAtleta; }
}


