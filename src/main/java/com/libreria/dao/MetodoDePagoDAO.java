package com.libreria.dao;

import com.libreria.model.MetodoDePago;
import com.libreria.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO: MetodoDePagoDAO
 * CONCEPTO: Gestión de métodos de pago en la base de datos
 */
public class MetodoDePagoDAO {

    /**
     * Obtener todos los métodos de pago activos
     */
    public List<MetodoDePago> obtenerMetodosDePago() {
        List<MetodoDePago> metodos = new ArrayList<>();

        String sql = """
            SELECT id_metodo_de_pago, nombre, activo
            FROM metodo_de_pago
            WHERE activo = TRUE
            ORDER BY nombre
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MetodoDePago metodo = new MetodoDePago();
                metodo.setIdMetodoDePago(rs.getInt("id_metodo_de_pago"));
                metodo.setNombre(rs.getString("nombre"));
                metodo.setActivo(rs.getBoolean("activo"));

                metodos.add(metodo);
            }

            System.out.println("💳 Métodos de pago cargados: " + metodos.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener métodos de pago: " + e.getMessage());
            e.printStackTrace();
        }

        return metodos;
    }

    /**
     * Obtener todos los métodos (activos e inactivos)
     */
    public List<MetodoDePago> obtenerTodosLosMetodos() {
        List<MetodoDePago> metodos = new ArrayList<>();

        String sql = """
            SELECT id_metodo_de_pago, nombre, activo
            FROM metodo_de_pago
            ORDER BY nombre
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MetodoDePago metodo = new MetodoDePago();
                metodo.setIdMetodoDePago(rs.getInt("id_metodo_de_pago"));
                metodo.setNombre(rs.getString("nombre"));
                metodo.setActivo(rs.getBoolean("activo"));

                metodos.add(metodo);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener todos los métodos: " + e.getMessage());
        }

        return metodos;
    }

    /**
     * Obtener método de pago por ID
     */
    public MetodoDePago obtenerMetodoPorId(int idMetodo) {
        String sql = """
            SELECT id_metodo_de_pago, nombre, activo
            FROM metodo_de_pago
            WHERE id_metodo_de_pago = ?
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMetodo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                MetodoDePago metodo = new MetodoDePago();
                metodo.setIdMetodoDePago(rs.getInt("id_metodo_de_pago"));
                metodo.setNombre(rs.getString("nombre"));
                metodo.setActivo(rs.getBoolean("activo"));

                return metodo;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener método: " + e.getMessage());
        }

        return null;
    }

    /**
     * Crear nuevo método de pago
     */
    public boolean crearMetodo(String nombre) {
        String sql = """
            INSERT INTO metodo_de_pago (nombre, activo)
            VALUES (?, TRUE)
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre.toUpperCase());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Método de pago creado: " + nombre);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al crear método: " + e.getMessage());
        }

        return false;
    }

    /**
     * Activar/Desactivar método de pago
     */
    public boolean cambiarEstado(int idMetodo, boolean activo) {
        String sql = "UPDATE metodo_de_pago SET activo = ? WHERE id_metodo_de_pago = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, activo);
            stmt.setInt(2, idMetodo);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Estado de método actualizado");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al cambiar estado: " + e.getMessage());
        }

        return false;
    }

    /**
     * Actualizar nombre del método
     */
    public boolean actualizarMetodo(int idMetodo, String nuevoNombre) {
        String sql = "UPDATE metodo_de_pago SET nombre = ? WHERE id_metodo_de_pago = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoNombre.toUpperCase());
            stmt.setInt(2, idMetodo);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Método actualizado");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar método: " + e.getMessage());
        }

        return false;
    }

    /**
     * Verificar si un método está en uso
     */
    public boolean estaEnUso(int idMetodo) {
        String sql = """
            SELECT COUNT(*) as total FROM (
                SELECT id_metodo_de_pago FROM venta WHERE id_metodo_de_pago = ?
                UNION ALL
                SELECT id_metodo_de_pago FROM compra WHERE id_metodo_de_pago = ?
            ) AS metodos_usados
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMetodo);
            stmt.setInt(2, idMetodo);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar uso: " + e.getMessage());
        }

        return false;
    }

    /**
     * Contar cuántas veces se usó un método
     */
    public int contarUsos(int idMetodo) {
        String sql = """
            SELECT 
                (SELECT COUNT(*) FROM venta WHERE id_metodo_de_pago = ?) +
                (SELECT COUNT(*) FROM compra WHERE id_metodo_de_pago = ?) as total
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMetodo);
            stmt.setInt(2, idMetodo);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar usos: " + e.getMessage());
        }

        return 0;
    }
}