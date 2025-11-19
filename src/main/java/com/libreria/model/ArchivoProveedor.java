package com.libreria.model;

import java.sql.Timestamp;

/**
 * MODELO: ArchivoProveedor
 * CONCEPTO: Representa un archivo adjunto a un proveedor
 */
public class ArchivoProveedor {

    private int idArchivo;
    private int idProveedor;
    private String nombreArchivo;
    private String rutaArchivo;
    private String tipoArchivo;
    private String descripcion;
    private long tamanioBytes;
    private Timestamp fechaSubida;
    private boolean activo;

    // ===== CONSTRUCTORES =====

    public ArchivoProveedor() {
        this.activo = true;
    }

    public ArchivoProveedor(int idProveedor, String nombreArchivo, String rutaArchivo,
                            String tipoArchivo, long tamanioBytes) {
        this.idProveedor = idProveedor;
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
        this.tipoArchivo = tipoArchivo;
        this.tamanioBytes = tamanioBytes;
        this.activo = true;
    }

    // ===== GETTERS Y SETTERS =====

    public int getIdArchivo() {
        return idArchivo;
    }

    public void setIdArchivo(int idArchivo) {
        this.idArchivo = idArchivo;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getTamanioBytes() {
        return tamanioBytes;
    }

    public void setTamanioBytes(long tamanioBytes) {
        this.tamanioBytes = tamanioBytes;
    }

    public Timestamp getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Timestamp fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // ===== MÉTODOS DE UTILIDAD =====

    /**
     * Obtener tamaño formateado
     */
    public String getTamanioFormateado() {
        return com.libreria.util.FileManager.formatearTamanio(tamanioBytes);
    }

    /**
     * Obtener fecha formateada
     */
    public String getFechaFormateada() {
        if (fechaSubida == null) return "N/A";

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fechaSubida);
    }

    @Override
    public String toString() {
        return String.format("Archivo{id=%d, nombre='%s', tipo='%s', tamaño=%s}",
                idArchivo, nombreArchivo, tipoArchivo, getTamanioFormateado());
    }
}