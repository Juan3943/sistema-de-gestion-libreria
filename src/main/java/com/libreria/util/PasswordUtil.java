package com.libreria.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * UTILIDAD: PasswordUtil
 * Maneja el hasheo y verificación de contraseñas usando BCrypt
 *
 * CONCEPTO BCrypt:
 * - BCrypt es un algoritmo de encriptación de una sola vía
 * - NO se puede "desencriptar" una contraseña hasheada
 * - Solo se puede VERIFICAR si una contraseña coincide con un hash
 * - Incluye "salt" automáticamente (no necesitas guardarlo por separado)
 * - Es LENTO a propósito (dificulta ataques de fuerza bruta)
 */
public class PasswordUtil {

    /**
     * Hashea (encripta) una contraseña en texto plano
     *
     * EJEMPLO:
     * String passwordPlano = "admin123";
     * String hash = PasswordUtil.hashear(passwordPlano);
     * // hash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
     *
     * @param passwordPlano La contraseña en texto plano
     * @return El hash BCrypt (String de ~60 caracteres)
     */
    public static String hashear(String passwordPlano) {
        // BCrypt.gensalt() genera un salt aleatorio automáticamente
        // BCrypt.hashpw() crea el hash combinando password + salt
        return BCrypt.hashpw(passwordPlano, BCrypt.gensalt());
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash
     *
     * CONCEPTO: Compara sin "desencriptar"
     * - BCrypt hashea la contraseña nuevamente
     * - Compara ambos hashes
     * - Si coinciden → contraseña correcta
     *
     * EJEMPLO:
     * String passwordIngresado = "admin123";
     * String hashGuardadoEnBD = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
     *
     * boolean esCorrecta = PasswordUtil.verificar(passwordIngresado, hashGuardadoEnBD);
     * // esCorrecta = true
     *
     * @param passwordPlano La contraseña que el usuario ingresó
     * @param hash El hash guardado en la base de datos
     * @return true si coinciden, false si no
     */
    public static boolean verificar(String passwordPlano, String hash) {
        try {
            // BCrypt.checkpw() hace toda la magia internamente
            return BCrypt.checkpw(passwordPlano, hash);
        } catch (Exception e) {
            // Si el hash es inválido o hay algún error, retornar false
            System.err.println("❌ Error al verificar contraseña: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método de prueba (opcional, para testing)
     * Puedes ejecutar este main para probar que BCrypt funciona
     */
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE BCRYPT ===\n");

        // 1. Hashear una contraseña
        String password = "admin123";
        String hash = hashear(password);

        System.out.println("Contraseña original: " + password);
        System.out.println("Hash generado: " + hash);
        System.out.println();

        // 2. Verificar contraseña correcta
        boolean correcta = verificar("admin123", hash);
        System.out.println("¿'admin123' coincide? " + correcta);  // true

        // 3. Verificar contraseña incorrecta
        boolean incorrecta = verificar("wrongpassword", hash);
        System.out.println("¿'wrongpassword' coincide? " + incorrecta);  // false

        // 4. Nota importante: cada vez que hasheas, el resultado es diferente
        String hash2 = hashear(password);
        System.out.println("\nHasheando la misma contraseña otra vez:");
        System.out.println("Hash 1: " + hash);
        System.out.println("Hash 2: " + hash2);
        System.out.println("¿Son iguales? " + hash.equals(hash2));  // false!
        System.out.println("Pero ambos verifican correctamente: " + verificar(password, hash2));  // true
    }
}