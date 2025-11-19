package com.libreria.util;
import com.libreria.util.PasswordUtil;

public class GenerarHashAdmin {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = PasswordUtil.hashear(password);

        System.out.println("=== HASH PARA ADMIN ===");
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("\n=== SQL PARA ACTUALIZAR ===");
        System.out.println("UPDATE usuario");
        System.out.println("SET contraseña_hash = '" + hash + "',");
        System.out.println("    salt = ''");
        System.out.println("WHERE nombre_usuario = 'admin';");
    }
}