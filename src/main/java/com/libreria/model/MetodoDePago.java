package com.libreria.model;

/**
 * MODELO: MetodoDePago
 * CONCEPTO: Representa un método de pago (Efectivo, Transferencia, etc.)
 */
public class MetodoDePago {

    private int idMetodoDePago;
    private String nombre;
    private boolean activo;

    // ===== CONSTRUCTORES =====

    public MetodoDePago() {
        this.activo = true;
    }

    public MetodoDePago(int idMetodoDePago, String nombre) {
        this.idMetodoDePago = idMetodoDePago;
        this.nombre = nombre;
        this.activo = true;
    }

    // ===== GETTERS Y SETTERS =====

    public int getIdMetodoDePago() {
        return idMetodoDePago;
    }

    public void setIdMetodoDePago(int idMetodoDePago) {
        this.idMetodoDePago = idMetodoDePago;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // ===== MÉTODOS DE UTILIDAD =====

    /**
     * Obtener nombre formateado (reemplazar guiones bajos por espacios)
     */
    public String getNombreFormateado() {
        return nombre.replace("_", " ");
    }

    /**
     * Obtener icono según tipo de método de pago
     */
    public String getIcono() {
        switch (nombre) {
            case "EFECTIVO": return "💵";
            case "TRANSFERENCIA": return "🏦";
            case "TARJETA_DEBITO": return "💳";
            case "TARJETA_CREDITO": return "💳";
            case "CUENTA_CORRIENTE": return "📋";
            default: return "💰";
        }
    }

    /**
     * Para mostrar en ComboBox y ListCell
     */
    @Override
    public String toString() {
        return getNombreFormateado();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MetodoDePago that = (MetodoDePago) obj;
        return idMetodoDePago == that.idMetodoDePago;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idMetodoDePago);
    }
}