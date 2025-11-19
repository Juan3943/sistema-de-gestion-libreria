package com.libreria.model;

/**
 * MODELO: TipoUsuario
 * Representa un rol de usuario (ADMIN o EMPLEADO)
 * Corresponde a la tabla 'tipo_usuario' en la BD
 */
public class TipoUsuario {

    // ===== ATRIBUTOS (coinciden con columnas de la tabla) =====
    private int idTipoUsuario;      // id_tipo_usuario
    private String nombre;           // nombre (ej: "ADMIN", "EMPLEADO")
    private String descripcion;      // descripcion
    private boolean activo;          // activo (1=true, 0=false)

    // Por ahora no usamos los campos de permisos específicos
    // (puede_anular_ventas, puede_ver_reportes, etc.)
    // Solo verificaremos si nombre == "ADMIN"

    // ===== CONSTRUCTORES =====

    /**
     * Constructor vacío (necesario para crear objetos sin datos)
     */
    public TipoUsuario() {
    }

    /**
     * Constructor con los campos principales
     */
    public TipoUsuario(int idTipoUsuario, String nombre, String descripcion, boolean activo) {
        this.idTipoUsuario = idTipoUsuario;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    // ===== GETTERS Y SETTERS =====
    // Permiten leer y modificar los atributos privados

    public int getIdTipoUsuario() {
        return idTipoUsuario;
    }

    public void setIdTipoUsuario(int idTipoUsuario) {
        this.idTipoUsuario = idTipoUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // ===== MÉTODOS ÚTILES =====

    /**
     * Verifica si este tipo de usuario es ADMIN
     * @return true si es admin, false si no
     */
    public boolean esAdmin() {
        return "ADMIN".equals(this.nombre);
    }

    /**
     * toString: Representación en texto del objeto
     * Útil para debugging y para mostrar en ComboBox
     */
    @Override
    public String toString() {
        return nombre;  // Retorna solo el nombre (ADMIN o EMPLEADO)
    }
}