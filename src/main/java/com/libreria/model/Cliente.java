package com.libreria.model;

import java.time.LocalDateTime;

/**
 * Modelo para clientes (personas o empresas)
 * Usado para ventas especiales que requieren registro del comprador
 */
public class Cliente {

    private int idCliente;
    private String tipoCliente;  // PERSONA o EMPRESA
    private String nombre;       // Nombre completo o Razón social
    private String cuit;         // DNI, CUIT o CUIL
    private String condicionIva; // Condición frente a IVA
    private String direccion;
    private String telefono;
    private String email;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    // Constructores
    public Cliente() {
        this.activo = true;
        this.condicionIva = "CONSUMIDOR_FINAL";
    }

    public Cliente(String tipoCliente, String nombre) {
        this();
        this.tipoCliente = tipoCliente;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
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

    public String getCondicionIva() {
        return condicionIva;
    }

    public void setCondicionIva(String condicionIva) {
        this.condicionIva = condicionIva;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Métodos de utilidad
    public boolean esEmpresa() {
        return "EMPRESA".equals(tipoCliente);
    }

    public boolean esPersona() {
        return "PERSONA".equals(tipoCliente);
    }

    public String getEstadoTexto() {
        return activo ? "Activo" : "Inactivo";
    }

    // Para mostrar en ComboBox
    @Override
    public String toString() {
        if (cuit != null && !cuit.trim().isEmpty()) {
            return nombre + " (" + cuit + ")";
        }
        return nombre;
    }
}