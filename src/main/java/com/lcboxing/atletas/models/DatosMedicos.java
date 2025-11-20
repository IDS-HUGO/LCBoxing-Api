package com.lcboxing.atletas.models;

import java.sql.Timestamp;

public class DatosMedicos {
    private Integer idAtleta;
    private String tipoSangre;
    private String alergias;
    private String condicionesMedicas;
    private Timestamp fechaUltimaActualizacion;

    public Integer getIdAtleta() { return idAtleta; }
    public void setIdAtleta(Integer idAtleta) { this.idAtleta = idAtleta; }

    public String getTipoSangre() { return tipoSangre; }
    public void setTipoSangre(String tipoSangre) { this.tipoSangre = tipoSangre; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public String getCondicionesMedicas() { return condicionesMedicas; }
    public void setCondicionesMedicas(String condicionesMedicas) { this.condicionesMedicas = condicionesMedicas; }

    public Timestamp getFechaUltimaActualizacion() { return fechaUltimaActualizacion; }
    public void setFechaUltimaActualizacion(Timestamp fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }
}