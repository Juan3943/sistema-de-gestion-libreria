package com.libreria.model;

import java.time.LocalDateTime;

/**
 * MODELO: Usuario
 * Representa un usuario del sistema
 * Corresponde a la tabla 'usuario' en la BD
 */
public class Usuario {

    // ===== ATRIBUTOS (coinciden con columnas de la tabla) =====
    private int idUsuario;                  // id_usuario
    private String nombreUsuario;           // nombre_usuario (username para login)
    private String contrasenaHash;          // contraseña_hash (hash BCrypt)
    private String nombre;                  // nombre (nombre real)
    private String apellido;                // apellido
    private TipoUsuario tipoUsuario;        // Objeto TipoUsuario (relación FK)
    private boolean activo;                 // activo (1=true, 0=false)
    private LocalDateTime fechaCreacion;    // fecha_creacion
    private LocalDateTime ultimoAcceso;     // ultimo_acceso

    // Nota: NO incluimos 'salt' porque BCrypt no lo necesita

    // ===== CONSTRUCTORES =====

    /**
     * Constructor vacío
     */
    public Usuario() {
    }

    /**
     * Constructor completo
     */
    public Usuario(int idUsuario, String nombreUsuario, String contrasenaHash,
                   String nombre, String apellido, TipoUsuario tipoUsuario,
                   boolean activo, LocalDateTime fechaCreacion, LocalDateTime ultimoAcceso) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasenaHash = contrasenaHash;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoUsuario = tipoUsuario;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.ultimoAcceso = ultimoAcceso;
    }

    /**
     * Constructor simplificado (para crear nuevos usuarios)
     */
    public Usuario(String nombreUsuario, String contrasenaHash, String nombre,
                   String apellido, TipoUsuario tipoUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.contrasenaHash = contrasenaHash;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoUsuario = tipoUsuario;
        this.activo = true;  // Por defecto activo
    }

    // ===== GETTERS Y SETTERS =====

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

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
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

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    // ===== MÉTODOS ÚTILES =====

    /**
     * Obtiene el nombre completo del usuario
     * @return "Nombre Apellido"
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Verifica si este usuario es ADMIN
     * @return true si es admin, false si no
     */
    public boolean esAdmin() {
        return tipoUsuario != null && tipoUsuario.esAdmin();
    }

    /**
     * toString: Para debugging
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + idUsuario +
                ", username='" + nombreUsuario + '\'' +
                ", nombre='" + getNombreCompleto() + '\'' +
                ", tipo=" + (tipoUsuario != null ? tipoUsuario.getNombre() : "null") +
                ", activo=" + activo +
                '}';
    }
}