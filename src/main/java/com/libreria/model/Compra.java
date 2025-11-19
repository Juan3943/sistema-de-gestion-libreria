package com.libreria.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MODELO: Compra
 * CONCEPTO: Representa una compra de mercadería a un proveedor
 */
public class Compra {

    private int idCompra;
    private String numeroCompra;
    private String numeroFacturaProveedor;

    private int idProveedor;
    private String nombreProveedor;  // Para mostrar en tablas

    private int idUsuario;
    private String nombreUsuario;    // Para mostrar en tablas

    private double subtotal;
    private double descuentos;
    private double total;

    private int idMetodoDePago;
    private String nombreMetodoPago;  // Para mostrar en tablas

    private Timestamp fechaCompra;
    private Timestamp fechaEntrega;

    private String estado;  // PENDIENTE, COMPLETADA, CANCELADA
    private String observaciones;

    // ===== CONSTRUCTORES =====

    public Compra() {
        this.estado = "PENDIENTE";
    }

    public Compra(int idProveedor, int idUsuario, int idMetodoDePago) {
        this.idProveedor = idProveedor;
        this.idUsuario = idUsuario;
        this.idMetodoDePago = idMetodoDePago;
        this.estado = "PENDIENTE";
        this.subtotal = 0.0;
        this.descuentos = 0.0;
        this.total = 0.0;
    }

    // ===== GETTERS Y SETTERS =====

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public String getNumeroCompra() {
        return numeroCompra;
    }

    public void setNumeroCompra(String numeroCompra) {
        this.numeroCompra = numeroCompra;
    }

    public String getNumeroFacturaProveedor() {
        return numeroFacturaProveedor;
    }

    public void setNumeroFacturaProveedor(String numeroFacturaProveedor) {
        this.numeroFacturaProveedor = numeroFacturaProveedor;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(double descuentos) {
        this.descuentos = descuentos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIdMetodoDePago() {
        return idMetodoDePago;
    }

    public void setIdMetodoDePago(int idMetodoDePago) {
        this.idMetodoDePago = idMetodoDePago;
    }

    public String getNombreMetodoPago() {
        return nombreMetodoPago;
    }

    public void setNombreMetodoPago(String nombreMetodoPago) {
        this.nombreMetodoPago = nombreMetodoPago;
    }

    public Timestamp getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Timestamp fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public Timestamp getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Timestamp fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    // ===== MÉTODOS DE UTILIDAD =====

    /**
     * Verificar si está pendiente
     */
    public boolean isPendiente() {
        return "PENDIENTE".equals(estado);
    }

    /**
     * Verificar si está completada
     */
    public boolean isCompletada() {
        return "COMPLETADA".equals(estado);
    }

    /**
     * Verificar si está cancelada
     */
    public boolean isCancelada() {
        return "CANCELADA".equals(estado);
    }

    /**
     * Obtener fecha formateada
     */
    public String getFechaCompraFormateada() {
        if (fechaCompra == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaCompra.toLocalDateTime().format(formatter);
    }

    /**
     * Obtener fecha entrega formateada
     */
    public String getFechaEntregaFormateada() {
        if (fechaEntrega == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaEntrega.toLocalDateTime().format(formatter);
    }

    /**
     * Obtener icono según estado
     */
    public String getIconoEstado() {
        switch (estado) {
            case "PENDIENTE": return "⏳";
            case "COMPLETADA": return "✅";
            case "CANCELADA": return "❌";
            default: return "❓";
        }
    }

    /**
     * Obtener color según estado (para UI)
     */
    public String getColorEstado() {
        switch (estado) {
            case "PENDIENTE": return "#f39c12";   // Naranja
            case "COMPLETADA": return "#27ae60";  // Verde
            case "CANCELADA": return "#e74c3c";   // Rojo
            default: return "#95a5a6";            // Gris
        }
    }

    @Override
    public String toString() {
        return String.format("Compra{id=%d, numero='%s', proveedor='%s', total=%.2f, estado='%s'}",
                idCompra, numeroCompra, nombreProveedor, total, estado);
    }
}
