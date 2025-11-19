package com.libreria.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * UTILIDAD: Manejo de archivos del sistema
 * CONCEPTO: Centralizar operaciones de subir, eliminar y abrir archivos
 */
public class FileManager {

    private static final String BASE_DIR = "data/proveedores/";
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20 MB en bytes

    /**
     * CONCEPTO: Validar que el archivo cumple requisitos
     */
    public static class ValidationResult {
        public boolean valid;
        public String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
    }

    /**
     * Validar archivo antes de subirlo
     */
    public static ValidationResult validarArchivo(File archivo) {
        if (archivo == null || !archivo.exists()) {
            return new ValidationResult(false, "El archivo no existe");
        }

        if (archivo.length() > MAX_FILE_SIZE) {
            double sizeMB = archivo.length() / (1024.0 * 1024.0);
            return new ValidationResult(false,
                    String.format("El archivo es demasiado grande (%.2f MB). Máximo: 20 MB", sizeMB));
        }

        String nombre = archivo.getName().toLowerCase();
        boolean tipoValido = nombre.endsWith(".pdf") ||
                nombre.endsWith(".xlsx") ||
                nombre.endsWith(".xls") ||
                nombre.endsWith(".csv") ||
                nombre.endsWith(".jpg") ||
                nombre.endsWith(".jpeg") ||
                nombre.endsWith(".png");

        if (!tipoValido) {
            return new ValidationResult(false,
                    "Tipo de archivo no permitido. Solo: PDF, Excel, CSV, Imágenes");
        }

        return new ValidationResult(true, "Archivo válido");
    }

    /**
     * CONCEPTO: Crear carpeta del proveedor si no existe
     */
    private static Path obtenerCarpetaProveedor(int idProveedor) throws IOException {
        Path carpeta = Paths.get(BASE_DIR + idProveedor);

        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
            System.out.println("📁 Carpeta creada: " + carpeta.toAbsolutePath());
        }

        return carpeta;
    }

    /**
     * CONCEPTO: Subir archivo a la carpeta del proveedor
     * Retorna la ruta relativa donde se guardó
     */
    public static String subirArchivo(File archivoOrigen, int idProveedor) throws IOException {
        // Validar
        ValidationResult validacion = validarArchivo(archivoOrigen);
        if (!validacion.valid) {
            throw new IllegalArgumentException(validacion.message);
        }

        // Crear carpeta si no existe
        Path carpetaDestino = obtenerCarpetaProveedor(idProveedor);

        // Generar nombre único si ya existe
        String nombreArchivo = archivoOrigen.getName();
        Path rutaDestino = carpetaDestino.resolve(nombreArchivo);

        int contador = 1;
        while (Files.exists(rutaDestino)) {
            String nombreBase = nombreArchivo.substring(0, nombreArchivo.lastIndexOf('.'));
            String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf('.'));
            nombreArchivo = nombreBase + "_" + contador + extension;
            rutaDestino = carpetaDestino.resolve(nombreArchivo);
            contador++;
        }

        // Copiar archivo
        Files.copy(archivoOrigen.toPath(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("✅ Archivo subido: " + rutaDestino.toAbsolutePath());

        // Retornar ruta relativa
        return BASE_DIR + idProveedor + "/" + nombreArchivo;
    }

    /**
     * CONCEPTO: Eliminar archivo del sistema
     */
    public static boolean eliminarArchivo(String rutaRelativa) {
        try {
            Path archivo = Paths.get(rutaRelativa);

            if (Files.exists(archivo)) {
                Files.delete(archivo);
                System.out.println("🗑️ Archivo eliminado: " + archivo.toAbsolutePath());
                return true;
            } else {
                System.err.println("⚠️ Archivo no encontrado: " + archivo.toAbsolutePath());
                return false;
            }

        } catch (IOException e) {
            System.err.println("❌ Error al eliminar archivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * CONCEPTO: Abrir archivo con la aplicación predeterminada del sistema
     */
    public static boolean abrirArchivo(String rutaRelativa) {
        try {
            File archivo = new File(rutaRelativa);

            if (!archivo.exists()) {
                System.err.println("⚠️ Archivo no encontrado: " + archivo.getAbsolutePath());
                return false;
            }

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(archivo);
                System.out.println("📂 Abriendo archivo: " + archivo.getAbsolutePath());
                return true;
            } else {
                System.err.println("❌ Desktop no soportado en este sistema");
                return false;
            }

        } catch (IOException e) {
            System.err.println("❌ Error al abrir archivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtener tipo de archivo por extensión
     */
    public static String obtenerTipoArchivo(String nombreArchivo) {
        String nombre = nombreArchivo.toLowerCase();

        if (nombre.endsWith(".pdf")) return "PDF";
        if (nombre.endsWith(".xlsx") || nombre.endsWith(".xls")) return "EXCEL";
        if (nombre.endsWith(".csv")) return "CSV";
        if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") || nombre.endsWith(".png")) return "IMAGEN";

        return "OTRO";
    }

    /**
     * Formatear tamaño de archivo
     */
    public static String formatearTamanio(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}