package com.lcboxing.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Atleta {
    private Integer idAtleta;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String genero;
    private LocalDateTime fechaRegistro;
    private Integer idUsuarioRegistro;
    private Boolean activo;
    private LocalDateTime fechaUltimaModificacion;
    private String notas;

    // Campos calculados
    private String nombreCompleto;
    private Integer edad;
    private String registradoPor;

    // Constructores
    public Atleta() {
        this.activo = true;
    }

    public Atleta(Integer idAtleta, String nombre, String apellidoPaterno, String apellidoMaterno,
                  String email, String telefono, LocalDate fechaNacimiento, String genero,
                  Integer idUsuarioRegistro, String notas) {
        this.idAtleta = idAtleta;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.idUsuarioRegistro = idUsuarioRegistro;
        this.notas = notas;
        this.activo = true;
    }

    // Getters y Setters
    public Integer getIdAtleta() {
        return idAtleta;
    }

    public void setIdAtleta(Integer idAtleta) {
        this.idAtleta = idAtleta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getIdUsuarioRegistro() {
        return idUsuarioRegistro;
    }

    public void setIdUsuarioRegistro(Integer idUsuarioRegistro) {
        this.idUsuarioRegistro = idUsuarioRegistro;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }

    public void setFechaUltimaModificacion(LocalDateTime fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getNombreCompleto() {
        if (nombreCompleto == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(nombre).append(" ").append(apellidoPaterno);
            if (apellidoMaterno != null && !apellidoMaterno.isEmpty()) {
                sb.append(" ").append(apellidoMaterno);
            }
            nombreCompleto = sb.toString();
        }
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Integer getEdad() {
        if (edad == null && fechaNacimiento != null) {
            edad = LocalDate.now().getYear() - fechaNacimiento.getYear();
        }
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(String registradoPor) {
        this.registradoPor = registradoPor;
    }
}