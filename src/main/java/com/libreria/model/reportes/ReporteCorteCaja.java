package com.libreria.model.reportes;
import java.time.LocalDate;


public class ReporteCorteCaja {
    private String metodoPago;
    private int cantidadVentas;
    private double montoTotal;

    public ReporteCorteCaja() {}

    public ReporteCorteCaja(String metodoPago, int cantidadVentas, double montoTotal) {
        this.metodoPago = metodoPago;
        this.cantidadVentas = cantidadVentas;
        this.montoTotal = montoTotal;
    }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public int getCantidadVentas() { return cantidadVentas; }
    public void setCantidadVentas(int cantidadVentas) { this.cantidadVentas = cantidadVentas; }

    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }
}