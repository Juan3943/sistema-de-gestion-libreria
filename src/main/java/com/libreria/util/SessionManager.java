package com.libreria.util;

import com.libreria.model.Usuario;

/**
 * UTILIDAD: SessionManager
 * Maneja la sesión del usuario actual (quién está logueado)
 *
 * CONCEPTO: Variable Estática (static)
 * - Una variable static existe UNA SOLA VEZ en toda la aplicación
 * - Todas las clases comparten la misma copia
 * - Perfecta para guardar "el usuario actual"
 *
 * EJEMPLO DE USO:
 *
 * // En LoginController (después de verificar credenciales):
 * SessionManager.login(usuarioEncontrado);
 *
 * // En cualquier otra parte del código:
 * Usuario actual = SessionManager.getUsuarioActual();
 * System.out.println("Logueado como: " + actual.getNombreCompleto());
 *
 * // Para verificar permisos:
 * if (SessionManager.esAdmin()) {
 *     // Mostrar opciones de admin
 * }
 *
 * // Al cerrar sesión:
 * SessionManager.logout();
 */
public class SessionManager {

    // ===== VARIABLE ESTÁTICA =====
    // Esta variable existe UNA SOLA VEZ para toda la aplicación
    // Guarda el usuario que está actualmente logueado
    private static Usuario usuarioActual = null;

    // ===== MÉTODOS ESTÁTICOS =====
    // Se llaman sin crear instancia: SessionManager.login(...)

    /**
     * Guarda el usuario que acaba de hacer login
     *
     * @param usuario El usuario que se logueó exitosamente
     */
    public static void login(Usuario usuario) {
        usuarioActual = usuario;
        System.out.println("✅ Sesión iniciada: " + usuario.getNombreCompleto() +
                " (" + usuario.getTipoUsuario().getNombre() + ")");
    }

    /**
     * Obtiene el usuario actualmente logueado
     *
     * @return El usuario actual, o null si nadie está logueado
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Verifica si hay alguien logueado
     *
     * @return true si hay sesión activa, false si no
     */
    public static boolean hayUsuarioLogueado() {
        return usuarioActual != null;
    }

    /**
     * Verifica si el usuario actual es ADMIN
     *
     * CONCEPTO: Verificación de permisos
     * Este método lo usaremos en MainApp para mostrar/ocultar botones
     *
     * @return true si es admin, false si no (o si no hay nadie logueado)
     */
    public static boolean esAdmin() {
        // Verificar que haya usuario Y que sea admin
        return usuarioActual != null && usuarioActual.esAdmin();
    }

    /**
     * Verifica si el usuario actual es EMPLEADO
     *
     * @return true si es empleado, false si no
     */
    public static boolean esEmpleado() {
        return usuarioActual != null &&
                !usuarioActual.esAdmin() &&
                usuarioActual.getTipoUsuario() != null;
    }

    /**
     * Cierra la sesión actual (logout)
     * Borra el usuario de memoria
     */
    public static void logout() {
        if (usuarioActual != null) {
            System.out.println("👋 Sesión cerrada: " + usuarioActual.getNombreCompleto());
            usuarioActual = null;
        }
    }

    /**
     * Obtiene el ID del usuario actual
     * Útil para registrar quién hizo cada operación
     *
     * @return El ID del usuario, o -1 si no hay nadie logueado
     */
    public static int getIdUsuarioActual() {
        return (usuarioActual != null) ? usuarioActual.getIdUsuario() : -1;
    }

    /**
     * Obtiene el nombre completo del usuario actual
     *
     * @return El nombre completo, o "Sin usuario" si no hay nadie logueado
     */
    public static String getNombreUsuarioActual() {
        return (usuarioActual != null) ? usuarioActual.getNombreCompleto() : "Sin usuario";
    }

    /**
     * Método para debugging (ver quién está logueado)
     */
    public static void mostrarSesionActual() {
        if (usuarioActual == null) {
            System.out.println("⚪ No hay sesión activa");
        } else {
            System.out.println("🟢 Sesión activa:");
            System.out.println("   Usuario: " + usuarioActual.getNombreUsuario());
            System.out.println("   Nombre: " + usuarioActual.getNombreCompleto());
            System.out.println("   Tipo: " + usuarioActual.getTipoUsuario().getNombre());
            System.out.println("   Admin: " + (esAdmin() ? "Sí" : "No"));
        }
    }
}