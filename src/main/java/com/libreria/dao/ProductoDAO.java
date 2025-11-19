package com.libreria.dao;

import com.libreria.model.Producto;
import com.libreria.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProductoDAO {

    /**
     * Obtener todos los productos (limitado a 25)
     */
    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();

        // ⚠️ IMPORTANTE: Incluir id_producto
        String sql = """
            SELECT id_producto, codigo_barras, nombre, precio_venta, stock_actual, tipo_producto
            FROM producto 
            WHERE activo = TRUE
            LIMIT 25
            """;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();

                // ✅ CRÍTICO: Asignar el ID
                producto.setIdProducto(rs.getInt("id_producto"));
                producto.setCodigo(rs.getString("codigo_barras"));
                producto.setNombre(rs.getString("nombre"));
                producto.setPrecio(rs.getDouble("precio_venta"));
                producto.setStock(rs.getInt("stock_actual"));
                producto.setTipoProducto(rs.getString("tipo_producto"));
                producto.setCantidad(1);

                lista.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener productos: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * MÉTODO PRINCIPAL: Buscar por código o nombre
     * ⚠️ IMPORTANTE: Ahora incluye id_producto
     */
    public List<Producto> buscarPorCodigoONombre(String query) {
        List<Producto> lista = new ArrayList<>();

        // Usar query directa en lugar de stored procedure
        String sql = """
            SELECT id_producto, codigo_barras, nombre, descripcion,
                   precio_venta, precio_costo, stock_actual, tipo_producto
            FROM producto 
            WHERE activo = TRUE
              AND (codigo_barras LIKE ? OR nombre LIKE ?)
            ORDER BY nombre
            LIMIT 20
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String patron = "%" + query + "%";
            stmt.setString(1, patron);
            stmt.setString(2, patron);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();

                // ✅ CRÍTICO: Asignar el ID primero
                producto.setIdProducto(rs.getInt("id_producto"));
                producto.setCodigo(rs.getString("codigo_barras"));
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setPrecio(rs.getDouble("precio_venta"));
                producto.setPrecioCosto(rs.getDouble("precio_costo"));
                producto.setStock(rs.getInt("stock_actual"));
                producto.setTipoProducto(rs.getString("tipo_producto"));
                producto.setCantidad(1);

                lista.add(producto);

                System.out.println("✓ Producto: ID=" + producto.getIdProducto() +
                        ", Código=" + producto.getCodigo() +
                        ", Nombre=" + producto.getNombre());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar producto: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Obtener solo el primer resultado
     */
    public Producto buscarPrimero(String query) {
        List<Producto> resultados = buscarPorCodigoONombre(query);
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    /**
     * NUEVO: Buscar productos de un proveedor específico
     * ✅ FILTRA por proveedor
     */
    public List<Producto> buscarProductosDelProveedor(String query, int idProveedor) {
        List<Producto> lista = new ArrayList<>();

        String sql = """
            SELECT DISTINCT 
                p.id_producto, p.codigo_barras, p.nombre, p.descripcion,
                p.precio_venta, p.precio_costo, p.stock_actual, p.tipo_producto
            FROM producto p
            INNER JOIN producto_proveedor pp ON p.id_producto = pp.id_producto
            WHERE pp.id_proveedor = ?
              AND p.activo = TRUE
              AND (p.codigo_barras LIKE ? OR p.nombre LIKE ?)
            ORDER BY p.nombre
            LIMIT 20
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProveedor);
            String patron = "%" + query + "%";
            stmt.setString(2, patron);
            stmt.setString(3, patron);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();

                // ✅ CRÍTICO: ID primero
                producto.setIdProducto(rs.getInt("id_producto"));
                producto.setCodigo(rs.getString("codigo_barras"));
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setPrecio(rs.getDouble("precio_venta"));
                producto.setPrecioCosto(rs.getDouble("precio_costo"));
                producto.setStock(rs.getInt("stock_actual"));
                producto.setTipoProducto(rs.getString("tipo_producto"));
                producto.setCantidad(1);

                lista.add(producto);
            }

            System.out.println("🔍 Productos del proveedor " + idProveedor + ": " + lista.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar productos del proveedor: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Obtener producto por ID
     */
    public Producto obtenerPorId(int idProducto) {
        String sql = """
            SELECT id_producto, codigo_barras, nombre, descripcion,
                   precio_venta, precio_costo, stock_actual, tipo_producto
            FROM producto 
            WHERE id_producto = ?
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Producto producto = new Producto();
                producto.setIdProducto(rs.getInt("id_producto"));
                producto.setCodigo(rs.getString("codigo_barras"));
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setPrecio(rs.getDouble("precio_venta"));
                producto.setPrecioCosto(rs.getDouble("precio_costo"));
                producto.setStock(rs.getInt("stock_actual"));
                producto.setTipoProducto(rs.getString("tipo_producto"));

                return producto;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener producto: " + e.getMessage());
        }

        return null;
    }

    /**
     * Verificar si un producto existe por código
     */
    public boolean existeProducto(String codigoBarras) {
        String sql = "SELECT COUNT(*) FROM producto WHERE codigo_barras = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoBarras);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar producto: " + e.getMessage());
        }

        return false;
    }
}