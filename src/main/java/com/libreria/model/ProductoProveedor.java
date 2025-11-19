package com.libreria.model;

import java.sql.Timestamp;

/**
 * MODELO: ProductoProveedor
 * CONCEPTO: Representa la relación entre un producto y un proveedor
 * Incluye precio de compra y si es proveedor principal
 */
public class ProductoProveedor {

    // IDs de la relación
    private int idProducto;
    private int idProveedor;

    // Datos del producto (para mostrar en tabla)
    private String codigoProducto;
    private String nombreProducto;

    // Datos del proveedor (para mostrar en tabla)
    private String nombreProveedor;

    // Datos de la relación
    private String codigoProveedor;      // Código que usa el proveedor
    private Double precioCompra;
    private Double precioCompraAnterior;
    private Timestamp fechaUltimaCompra;
    private boolean esPrincipal;
    private boolean activo;

    // ===== CONSTRUCTORES =====

    public ProductoProveedor() {
        this.activo = true;
        this.esPrincipal = false;
    }

    /**
     * Constructor para crear nueva relación
     */
    public ProductoProveedor(int idProducto, int idProveedor, String codigoProveedor,
                             Double precioCompra, boolean esPrincipal) {
        this.idProducto = idProducto;
        this.idProveedor = idProveedor;
        this.codigoProveedor = codigoProveedor;
        this.precioCompra = precioCompra;
        this.esPrincipal = esPrincipal;
        this.activo = true;
    }

    // ===== GETTERS Y SETTERS =====

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Double getPrecioCompraAnterior() {
        return precioCompraAnterior;
    }

    public void setPrecioCompraAnterior(Double precioCompraAnterior) {
        this.precioCompraAnterior = precioCompraAnterior;
    }

    public Timestamp getFechaUltimaCompra() {
        return fechaUltimaCompra;
    }

    public void setFechaUltimaCompra(Timestamp fechaUltimaCompra) {
        this.fechaUltimaCompra = fechaUltimaCompra;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // ===== MÉTODOS DE UTILIDAD =====

    /**
     * Icono para mostrar si es proveedor principal
     */
    public String getIconoPrincipal() {
        return esPrincipal ? "⭐" : "";
    }

    /**
     * Calcular variación de precio
     */
    public String getVariacionPrecio() {
        if (precioCompraAnterior == null || precioCompraAnterior == 0) {
            return "N/A";
        }

        double diferencia = precioCompra - precioCompraAnterior;
        double porcentaje = (diferencia / precioCompraAnterior) * 100;

        String signo = diferencia > 0 ? "+" : "";
        return String.format("%s%.2f%%", signo, porcentaje);
    }

    @Override
    public String toString() {
        return String.format("ProductoProveedor{producto='%s', proveedor='%s', precio=%.2f, principal=%s}",
                codigoProducto, nombreProveedor, precioCompra, esPrincipal);
    }
}