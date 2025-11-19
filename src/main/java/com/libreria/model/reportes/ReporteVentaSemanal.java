package com.libreria.model.reportes;

import java.time.LocalDate;

// ==========================================
// MODELO: Ventas Semanales
// ==========================================
public class ReporteVentaSemanal {
    private String semana;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int cantidadVentas;
    private double totalVendido;

    public ReporteVentaSemanal() {}

    public ReporteVentaSemanal(String semana, LocalDate fechaInicio, LocalDate fechaFin,
                               int cantidadVentas, double totalVendido) {
        this.semana = semana;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadVentas = cantidadVentas;
        this.totalVendido = totalVendido;
    }

    public String getSemana() { return semana; }
    public void setSemana(String semana) { this.semana = semana; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public int getCantidadVentas() { return cantidadVentas; }
    public void setCantidadVentas(int cantidadVentas) { this.cantidadVentas = cantidadVentas; }

    public double getTotalVendido() { return totalVendido; }
    public void setTotalVendido(double totalVendido) { this.totalVendido = totalVendido; }
}