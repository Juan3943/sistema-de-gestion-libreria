package com.libreria.model;

import java.sql.Timestamp;

/**
 * MODELO: Proveedor
 * CONCEPTO: Representa un proveedor de productos para la librería
 * Similar a Producto.java pero para proveedores
 */
public class Proveedor {

    // Atributos principales
    private int idProveedor;
    private String nombre;
    private String cuit;
    private String telefono;
    private String email;
    private String direccion;
    private boolean activo;
    private Timestamp fechaCreacion;

    // ===== CONSTRUCTORES =====

    /**
     * Constructor vacío para JavaFX
     */
    public Proveedor() {
        this.activo = true;
    }

    /**
     * Constructor con datos básicos
     */
    public Proveedor(String nombre, String cuit, String telefono, String email, String direccion) {
        this.nombre = nombre;
        this.cuit = cuit;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.activo = true;
    }

    /**
     * Constructor completo (para cargar desde BD)
     */
    public Proveedor(int idProveedor, String nombre, String cuit, String telefono,
                     String email, String direccion, boolean activo, Timestamp fechaCreacion) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.cuit = cuit;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    // ===== GETTERS Y SETTERS =====

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // ===== MÉTODOS DE UTILIDAD =====

    /**
     * Estado legible para mostrar en tabla
     */
    public String getEstadoTexto() {
        return activo ? "Activo" : "Inactivo";
    }

    /**
     * Validar si el proveedor tiene datos mínimos
     */
    public boolean esValido() {
        return nombre != null && !nombre.trim().isEmpty();
    }

    /**
     * Formatear CUIT para mostrar (con guiones)
     */
    public String getCuitFormateado() {
        if (cuit == null || cuit.length() < 11) {
            return cuit;
        }
        // Formato: XX-XXXXXXXX-X
        return cuit.substring(0, 2) + "-" +
                cuit.substring(2, 10) + "-" +
                cuit.substring(10);
    }

    @Override
    public String toString() {
        return String.format("Proveedor{id=%d, nombre='%s', cuit='%s', activo=%s}",
                idProveedor, nombre, cuit, activo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Proveedor that = (Proveedor) obj;
        return idProveedor == that.idProveedor;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idProveedor);
    }
}