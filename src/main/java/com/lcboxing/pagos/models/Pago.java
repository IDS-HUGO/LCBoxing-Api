package com.lcboxing.pagos.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Pago {
    private Integer idPago;
    private Integer idMembresia;
    private Integer idMetodoPago;
    private BigDecimal monto;
    private String referencia;
    private Timestamp fechaPago;
    private Integer idUsuarioRegistro;
    private String concepto;
    private String comprobanteUrl;
    private String notas;
    private String nombreMetodo;
    private String nombreAtleta;

    // Getters y Setters omitidos por brevedad
    public Integer getIdPago() { return idPago; }
    public void setIdPago(Integer idPago) { this.idPago = idPago; }
    public Integer getIdMembresia() { return idMembresia; }
    public void setIdMembresia(Integer idMembresia) { this.idMembresia = idMembresia; }
    public Integer getIdMetodoPago() { return idMetodoPago; }
    public void setIdMetodoPago(Integer idMetodoPago) { this.idMetodoPago = idMetodoPago; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public Timestamp getFechaPago() { return fechaPago; }
    public void setFechaPago(Timestamp fechaPago) { this.fechaPago = fechaPago; }
    public Integer getIdUsuarioRegistro() { return idUsuarioRegistro; }
    public void setIdUsuarioRegistro(Integer idUsuarioRegistro) { this.idUsuarioRegistro = idUsuarioRegistro; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public String getComprobanteUrl() { return comprobanteUrl; }
    public void setComprobanteUrl(String comprobanteUrl) { this.comprobanteUrl = comprobanteUrl; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public String getNombreMetodo() { return nombreMetodo; }
    public void setNombreMetodo(String nombreMetodo) { this.nombreMetodo = nombreMetodo; }
    public String getNombreAtleta() { return nombreAtleta; }
    public void setNombreAtleta(String nombreAtleta) { this.nombreAtleta = nombreAtleta; }
}


