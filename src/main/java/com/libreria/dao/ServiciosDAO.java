package com.libreria.dao;

import com.libreria.model.Producto;
import com.libreria.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO EXCLUSIVO PARA SERVICIOS
 * CONCEPTO: Maneja solo productos tipo SERVICIO (sin stock)
 */
public class ServiciosDAO {

    /**
     * Obtener todos los servicios
     * CONCEPTO: Query con WHERE tipo_producto = 'SERVICIO'
     */
    public List<Producto> obtenerServicios() {
        List<Producto> servicios = new ArrayList<>();

        String sql = """
            SELECT 
                p.codigo_barras,
                p.nombre,
                p.descripcion,
                p.precio_venta,
                p.activo,
                c.nombre as categoria
            FROM producto p
            LEFT JOIN categoria c ON p.id_categoria = c.id_categoria
            WHERE p.tipo_producto = 'SERVICIO'
            ORDER BY p.nombre
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto servicio = new Producto();
                servicio.setCodigo(rs.getString("codigo_barras"));
                servicio.setNombre(rs.getString("nombre"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setPrecio(rs.getDouble("precio_venta"));
                servicio.setCategoria(rs.getString("categoria"));
                servicio.setActivo(rs.getBoolean("activo"));

                servicios.add(servicio);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener servicios: " + e.getMessage());
        }

        return servicios;
    }

    /**
     * Crear servicio nuevo
     * CONCEPTO: INSERT sin campos de stock (siempre tipo_producto = 'SERVICIO')
     */
    public boolean crearServicio(String codigo, String nombre, String descripcion,
                                 int idCategoria, double precioVenta) {

        String sql = """
            INSERT INTO producto (
                codigo_barras, nombre, descripcion, id_categoria,
                tipo_producto, precio_venta,
                stock_actual, stock_minimo, precio_costo, activo
            ) VALUES (?, ?, ?, ?, 'SERVICIO', ?, 50, 0, 0, TRUE)
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            stmt.setString(2, nombre);
            stmt.setString(3, descripcion == null || descripcion.trim().isEmpty() ? null : descripcion);
            stmt.setInt(4, idCategoria);
            stmt.setDouble(5, precioVenta);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear servicio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualizar servicio
     * CONCEPTO: UPDATE solo de campos relevantes para servicios (sin stock)
     */
    public boolean actualizarServicio(String codigoOriginal, String nombre, String descripcion,
                                      int idCategoria, double precioVenta) {

        String sql = """
            UPDATE producto SET 
                nombre = ?, 
                descripcion = ?, 
                id_categoria = ?, 
                precio_venta = ?
            WHERE codigo_barras = ? 
              AND tipo_producto = 'SERVICIO'
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, descripcion == null || descripcion.trim().isEmpty() ? null : descripcion);
            stmt.setInt(3, idCategoria);
            stmt.setDouble(4, precioVenta);
            stmt.setString(5, codigoOriginal);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar servicio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Activar/Desactivar servicio
     * CONCEPTO: Cambiar campo activo sin eliminar de BD
     */
    public boolean cambiarEstadoServicio(String codigo, boolean activo) {
        String sql = "UPDATE producto SET activo = ? WHERE codigo_barras = ? AND tipo_producto = 'SERVICIO'";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, activo);
            stmt.setString(2, codigo);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validar si código existe
     */
    public boolean existeCodigoServicio(String codigo) {
        String sql = "SELECT COUNT(*) FROM producto WHERE codigo_barras = ? AND tipo_producto = 'SERVICIO'";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al validar código: " + e.getMessage());
        }

        return false;
    }

    /**
     * Reutilizar OpcionCombo de StockDAO
     * CONCEPTO: Mismo enfoque para categorías
     */
    public List<StockDAO.OpcionCombo> obtenerCategorias() {
        List<StockDAO.OpcionCombo> categorias = new ArrayList<>();

        String sql = "SELECT id_categoria, nombre FROM categoria WHERE activo = TRUE ORDER BY nombre";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categorias.add(new StockDAO.OpcionCombo(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
        }

        return categorias;
    }
}