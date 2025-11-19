package com.libreria.dao;

import com.libreria.model.Producto;
import com.libreria.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class StockDAO {

    /**
     * CLASE AUXILIAR SIMPLE para ComboBox con Foreign Keys
     * CONCEPTO: Objeto que muestra nombre pero guarda ID
     */
    public static class OpcionCombo {
        private int id;
        private String nombre;

        public OpcionCombo(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }

        // IMPORTANTE: toString() define lo que muestra el ComboBox
        @Override
        public String toString() {
            return nombre; // El usuario ve solo el nombre
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            OpcionCombo that = (OpcionCombo) obj;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }

    // ===== MÉTODOS PARA TABLA PRINCIPAL =====

    /**
     * Obtener productos con información completa para la tabla
     */
    public List<Producto> obtenerProductosParaStock() {
        List<Producto> productos = new ArrayList<>();

        String sql = """
            SELECT 
                p.codigo_barras,
                p.nombre,
                p.descripcion,
                p.stock_actual,
                p.stock_minimo,
                p.precio_costo,
                p.precio_venta,
                p.tipo_producto,
                c.nombre as categoria,
                m.nombre as marca,
                p.activo
            FROM producto p
            LEFT JOIN categoria c ON p.id_categoria = c.id_categoria
            LEFT JOIN marca m ON p.id_marca = m.id_marca
            WHERE p.tipo_producto = 'FISICO'
            ORDER BY p.nombre
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigo(rs.getString("codigo_barras"));
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setStock(rs.getInt("stock_actual"));
                producto.setStockMinimo(rs.getInt("stock_minimo"));
                producto.setPrecioCosto(rs.getDouble("precio_costo"));
                producto.setPrecio(rs.getDouble("precio_venta"));
                producto.setTipoProducto(rs.getString("tipo_producto"));
                producto.setCategoria(rs.getString("categoria"));
                producto.setMarca(rs.getString("marca"));
                producto.setActivo(rs.getBoolean("activo"));

                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }

        return productos;
    }

    /**
     * Obtener productos con stock bajo para alertas
     */
    public List<Producto> obtenerProductosStockBajo() {
        List<Producto> productos = new ArrayList<>();

        String sql = """
            SELECT codigo_barras, nombre, stock_actual, stock_minimo
            FROM producto 
            WHERE activo = TRUE 
              AND tipo_producto = 'FISICO'
              AND stock_actual <= stock_minimo
            ORDER BY stock_actual ASC
            LIMIT 10
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigo(rs.getString("codigo_barras"));
                producto.setNombre(rs.getString("nombre"));
                producto.setStock(rs.getInt("stock_actual"));
                producto.setStockMinimo(rs.getInt("stock_minimo"));
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener stock bajo: " + e.getMessage());
        }

        return productos;
    }

    // ===== MÉTODOS PARA COMBOBOX (SOLUCIÓN SIMPLE) =====

    /**
     * CONCEPTO: Obtener categorías como objetos simples
     * Cada objeto tiene ID y nombre, ComboBox muestra nombre automáticamente
     */
    public List<OpcionCombo> obtenerCategorias() {
        List<OpcionCombo> categorias = new ArrayList<>();

        String sql = """
            SELECT id_categoria, nombre 
            FROM categoria 
            WHERE activo = TRUE 
            ORDER BY nombre
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categorias.add(new OpcionCombo(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre")
                ));
            }

            System.out.println("Cargadas " + categorias.size() + " categorías");

        } catch (SQLException e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
        }

        return categorias;
    }

    /**
     * CONCEPTO: Obtener marcas con opción especial "Sin marca"
     * ID=0 representa NULL en la base de datos
     */
    public List<OpcionCombo> obtenerMarcas() {
        List<OpcionCombo> marcas = new ArrayList<>();

        // Opción especial para sin marca (ID=0 se convierte en NULL)
        marcas.add(new OpcionCombo(0, "Sin marca"));

        String sql = """
            SELECT id_marca, nombre 
            FROM marca 
            WHERE activo = TRUE 
            ORDER BY nombre
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                marcas.add(new OpcionCombo(
                        rs.getInt("id_marca"),
                        rs.getString("nombre")
                ));
            }

            System.out.println("Cargadas " + (marcas.size()-1) + " marcas + opción 'Sin marca'");

        } catch (SQLException e) {
            System.err.println("Error al cargar marcas: " + e.getMessage());
        }

        return marcas;
    }

    // ===== MÉTODOS PARA GESTIÓN DE PRODUCTOS =====

    /**
     * CONCEPTO: Validar si código ya existe antes de crear
     */
    public boolean existeCodigoBarras(String codigoBarras) {
        String sql = "SELECT COUNT(*) FROM producto WHERE codigo_barras = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoBarras);
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
     * CONCEPTO: Crear producto con parámetros directos (sin clases auxiliares)
     * Maneja Foreign Keys de forma simple
     */
    public boolean crearProductoSimple(String codigo, String nombre, String descripcion,
                                       int idCategoria, Integer idMarca, String tipoProducto,
                                       double precioCosto, double precioVenta,
                                       int stockActual, int stockMinimo) {

        // Validar código único
        if (existeCodigoBarras(codigo)) {
            throw new IllegalArgumentException("El código de barras ya existe");
        }

        String sql = """
            INSERT INTO producto (
                codigo_barras, nombre, descripcion, id_categoria, id_marca,
                tipo_producto, stock_actual, stock_minimo, precio_costo, precio_venta,
                activo
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE)
            """;

        try (Connection conn = ConexionBD.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transacción

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, codigo);
                stmt.setString(2, nombre);
                stmt.setString(3, descripcion == null || descripcion.trim().isEmpty() ? null : descripcion);
                stmt.setInt(4, idCategoria);

                // CONCEPTO: Manejar FK opcional (marca)
                if (idMarca == null || idMarca == 0) {
                    stmt.setNull(5, Types.INTEGER);
                } else {
                    stmt.setInt(5, idMarca);
                }

                stmt.setString(6, tipoProducto);
                stmt.setInt(7, stockActual);
                stmt.setInt(8, stockMinimo);
                stmt.setDouble(9, precioCosto);
                stmt.setDouble(10, precioVenta);

                int filasAfectadas = stmt.executeUpdate();

                if (filasAfectadas > 0) {
                    // Registrar movimiento de stock inicial si corresponde
                    if (stockActual > 0) {
                        registrarMovimientoStock(conn, codigo, stockActual, "Stock inicial del producto");
                    }

                    conn.commit();
                    System.out.println("Producto creado: " + nombre);
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al crear producto: " + e.getMessage());
            throw new RuntimeException("Error en base de datos: " + e.getMessage());
        }
    }

    /**
     * CONCEPTO: Ajustar stock con registro de movimiento
     */
    public boolean ajustarStock(String codigoBarras, int cantidadCambio, String motivo) {
        String sqlUpdate = """
            UPDATE producto 
            SET stock_actual = stock_actual + ? 
            WHERE codigo_barras = ? AND activo = TRUE
            """;

        try (Connection conn = ConexionBD.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Actualizar stock
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                stmtUpdate.setInt(1, cantidadCambio);
                stmtUpdate.setString(2, codigoBarras);
                int filasAfectadas = stmtUpdate.executeUpdate();

                if (filasAfectadas == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 2. Registrar movimiento
            registrarMovimientoStock(conn, codigoBarras, cantidadCambio, motivo);

            conn.commit();
            System.out.println("Stock ajustado: " + codigoBarras + " (" + cantidadCambio + ")");
            return true;

        } catch (SQLException e) {
            System.err.println("Error al ajustar stock: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarProducto(String codigoOriginal, String nombre, String descripcion,
                                      int idCategoria, Integer idMarca, String tipoProducto,
                                      double precioCosto, double precioVenta, int stockMinimo) {

        String sql = """
        UPDATE producto SET 
            nombre = ?, 
            descripcion = ?, 
            id_categoria = ?, 
            id_marca = ?, 
            tipo_producto = ?, 
            precio_costo = ?, 
            precio_venta = ?, 
            stock_minimo = ?
        WHERE codigo_barras = ? AND activo = TRUE
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, descripcion == null || descripcion.trim().isEmpty() ? null : descripcion);
            stmt.setInt(3, idCategoria);

            // Manejar marca opcional
            if (idMarca == null || idMarca == 0) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, idMarca);
            }

            stmt.setString(5, tipoProducto);
            stmt.setDouble(6, precioCosto);
            stmt.setDouble(7, precioVenta);
            stmt.setInt(8, stockMinimo);
            stmt.setString(9, codigoOriginal); // WHERE clause

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Producto actualizado: " + nombre);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            throw new RuntimeException("Error en base de datos: " + e.getMessage());
        }

        return false;
    }

    /**
     * CONCEPTO: Registrar movimiento de stock (método privado)
     * Usa la conexión existente para mantener transacción
     */
    private void registrarMovimientoStock(Connection conn, String codigoBarras,
                                          int cantidadCambio, String motivo) throws SQLException {
        String sql = """
            INSERT INTO movimiento (
                id_producto, id_tipo_movimiento, cantidad,
                stock_anterior, stock_nuevo, motivo, referencia, id_usuario
            ) SELECT 
                p.id_producto,
                CASE 
                    WHEN ? > 0 THEN 3  -- AJUSTE_POSITIVO
                    WHEN ? < 0 THEN 4  -- AJUSTE_NEGATIVO
                    ELSE 9             -- INICIAL
                END,
                ABS(?),
                p.stock_actual - ?,
                p.stock_actual,
                ?,
                CONCAT('AJUSTE_', NOW()),
                1
            FROM producto p
            WHERE p.codigo_barras = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cantidadCambio);
            stmt.setInt(2, cantidadCambio);
            stmt.setInt(3, cantidadCambio);
            stmt.setInt(4, cantidadCambio);
            stmt.setString(5, motivo);
            stmt.setString(6, codigoBarras);

            stmt.executeUpdate();
            System.out.println("Movimiento registrado: " + motivo);
        }
    }

    public boolean cambiarEstadoProducto(String codigoBarras, boolean activo) {
        String sql = """
        UPDATE producto 
        SET activo = ? 
        WHERE codigo_barras = ? 
          AND tipo_producto = 'FISICO'
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, activo);
            stmt.setString(2, codigoBarras);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                String estado = activo ? "activado" : "desactivado";
                System.out.println("Producto " + estado + ": " + codigoBarras);
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }
}
