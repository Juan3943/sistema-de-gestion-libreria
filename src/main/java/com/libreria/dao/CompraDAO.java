package com.libreria.dao;

import com.libreria.model.Compra;
import com.libreria.model.Producto;
import com.libreria.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CompraDAO {

    // ===== CREAR COMPRA =====

    /**
     * CONCEPTO: Crear una nueva compra en estado PENDIENTE
     * Genera el número de compra automáticamente
     * Retorna el ID de la compra creada o -1 si falla
     */
    public int crearCompraNueva(int idProveedor, int idUsuario, int idMetodoPago) {
        Connection conn = null;

        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);  // ← TRANSACCIÓN

            // 1. Generar número de compra
            String numeroCompra = generarNumeroCompra(conn);

            // 2. Insertar compra
            String sql = """
                INSERT INTO compra (
                    numero_compra, id_proveedor, id_usuario, id_metodo_de_pago,
                    subtotal, total, estado
                ) VALUES (?, ?, ?, ?, 0.00, 0.00, 'PENDIENTE')
                """;

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, numeroCompra);
            stmt.setInt(2, idProveedor);
            stmt.setInt(3, idUsuario);
            stmt.setInt(4, idMetodoPago);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int idCompra = keys.getInt(1);
                    conn.commit();
                    System.out.println("✅ Compra creada: " + numeroCompra + " (ID: " + idCompra + ")");
                    return idCompra;
                }
            }

            conn.rollback();
            return -1;

        } catch (SQLException e) {
            System.err.println("❌ Error al crear compra: " + e.getMessage());
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            return -1;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Generar número de compra único (C00000001, C00000002, etc.)
     */
    private String generarNumeroCompra(Connection conn) throws SQLException {
        String sql = """
            SELECT COALESCE(MAX(CAST(SUBSTRING(numero_compra, 2) AS UNSIGNED)), 0) + 1 
            FROM compra 
            WHERE numero_compra LIKE 'C%'
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int siguiente = rs.getInt(1);
                return String.format("C%08d", siguiente);
            }
        }

        return "C00000001";  // Primer compra
    }

    // ===== AGREGAR/QUITAR PRODUCTOS =====

    /**
     * CONCEPTO: Agregar producto al detalle de la compra
     */
    public boolean agregarProductoACompra(int idCompra, int idProducto, int cantidad, double precioUnitario) {
        Connection conn = null;

        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);

            double subtotal = cantidad * precioUnitario;

            // 1. Insertar o actualizar detalle
            String sql = """
                INSERT INTO compra_detalle (id_compra, id_producto, cantidad, precio_unitario, subtotal)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE 
                    cantidad = VALUES(cantidad),
                    precio_unitario = VALUES(precio_unitario),
                    subtotal = VALUES(subtotal)
                """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCompra);
            stmt.setInt(2, idProducto);
            stmt.setInt(3, cantidad);
            stmt.setDouble(4, precioUnitario);
            stmt.setDouble(5, subtotal);

            stmt.executeUpdate();

            // 2. Actualizar totales
            actualizarTotalesCompra(conn, idCompra);

            conn.commit();
            System.out.println("✅ Producto agregado a compra");
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al agregar producto: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * CONCEPTO: Eliminar producto del detalle
     */
    public boolean quitarProductoDeCompra(int idCompra, int idProducto) {
        Connection conn = null;

        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);

            // 1. Eliminar detalle
            String sql = "DELETE FROM compra_detalle WHERE id_compra = ? AND id_producto = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCompra);
            stmt.setInt(2, idProducto);
            stmt.executeUpdate();

            // 2. Actualizar totales
            actualizarTotalesCompra(conn, idCompra);

            conn.commit();
            System.out.println("✅ Producto eliminado de compra");
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al quitar producto: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Actualizar totales de la compra
     */
    private void actualizarTotalesCompra(Connection conn, int idCompra) throws SQLException {
        String sql = """
            UPDATE compra 
            SET subtotal = (
                SELECT COALESCE(SUM(subtotal), 0.00)
                FROM compra_detalle 
                WHERE id_compra = ?
            ),
            total = subtotal - COALESCE(descuentos, 0)
            WHERE id_compra = ?
            """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idCompra);
        stmt.setInt(2, idCompra);
        stmt.executeUpdate();
    }

    // ===== ACTUALIZAR COMPRA =====

    /**
     * Actualizar datos generales de la compra (solo si está PENDIENTE)
     */
    public boolean actualizarCompra(Compra compra) {
        String sql = """
            UPDATE compra 
            SET numero_factura_proveedor = ?,
                id_metodo_de_pago = ?,
                descuentos = ?,
                observaciones = ?
            WHERE id_compra = ? AND estado = 'PENDIENTE'
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, compra.getNumeroFacturaProveedor());
            stmt.setInt(2, compra.getIdMetodoDePago());
            stmt.setDouble(3, compra.getDescuentos());
            stmt.setString(4, compra.getObservaciones());
            stmt.setInt(5, compra.getIdCompra());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Recalcular total considerando descuentos
                actualizarTotalesCompra(conn, compra.getIdCompra());
                System.out.println("✅ Compra actualizada");
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar compra: " + e.getMessage());
            return false;
        }
    }

    // ===== COMPLETAR COMPRA (CRÍTICO) =====

    /**
     * CONCEPTO: Completar compra - Actualiza stock y registra movimientos
     * Esta es la operación MÁS CRÍTICA del sistema
     * Usa transacción para garantizar atomicidad
     */
    public boolean completarCompra(int idCompra) {
        Connection conn = null;

        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);  // ← TRANSACCIÓN CRÍTICA

            // 1. Verificar que tiene productos
            String sqlVerificar = "SELECT COUNT(*) FROM compra_detalle WHERE id_compra = ?";
            PreparedStatement stmtVerificar = conn.prepareStatement(sqlVerificar);
            stmtVerificar.setInt(1, idCompra);
            ResultSet rs = stmtVerificar.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                throw new SQLException("No se puede completar una compra sin productos");
            }

            // 2. Obtener datos de la compra
            String sqlCompra = "SELECT numero_compra, id_usuario, id_proveedor FROM compra WHERE id_compra = ?";
            PreparedStatement stmtCompra = conn.prepareStatement(sqlCompra);
            stmtCompra.setInt(1, idCompra);
            ResultSet rsCompra = stmtCompra.executeQuery();

            if (!rsCompra.next()) {
                throw new SQLException("Compra no encontrada");
            }

            String numeroCompra = rsCompra.getString("numero_compra");
            int idUsuario = rsCompra.getInt("id_usuario");
            int idProveedor = rsCompra.getInt("id_proveedor");

            // 3. Actualizar stock de productos FÍSICOS
            String sqlStock = """
                UPDATE producto p
                INNER JOIN compra_detalle cd ON p.id_producto = cd.id_producto
                SET p.stock_actual = p.stock_actual + cd.cantidad,
                    p.fecha_ultima_compra = NOW()
                WHERE cd.id_compra = ? 
                  AND p.tipo_producto = 'FISICO'
                """;

            PreparedStatement stmtStock = conn.prepareStatement(sqlStock);
            stmtStock.setInt(1, idCompra);
            int productosActualizados = stmtStock.executeUpdate();

            System.out.println("📦 Stock actualizado: " + productosActualizados + " productos");

            // 4. Registrar movimientos de stock
            String sqlMovimientos = """
                INSERT INTO movimiento (
                    id_producto, id_tipo_movimiento, cantidad, 
                    stock_anterior, stock_nuevo, motivo, referencia, id_usuario
                )
                SELECT 
                    cd.id_producto,
                    2,
                    cd.cantidad,
                    p.stock_actual - cd.cantidad,
                    p.stock_actual,
                    'Compra completada',
                    ?,
                    ?
                FROM compra_detalle cd
                INNER JOIN producto p ON cd.id_producto = p.id_producto
                WHERE cd.id_compra = ? 
                  AND p.tipo_producto = 'FISICO'
                """;

            PreparedStatement stmtMovimientos = conn.prepareStatement(sqlMovimientos);
            stmtMovimientos.setString(1, "Compra_" + numeroCompra);
            stmtMovimientos.setInt(2, idUsuario);
            stmtMovimientos.setInt(3, idCompra);
            int movimientos = stmtMovimientos.executeUpdate();

            System.out.println("📝 Movimientos registrados: " + movimientos);

            // 5. Actualizar precio_costo si es proveedor principal
            actualizarPrecioCostoSiEsPrincipal(conn, idCompra, idProveedor);

            // 6. Cambiar estado a COMPLETADA
            String sqlEstado = """
                UPDATE compra 
                SET estado = 'COMPLETADA',
                    fecha_entrega = NOW()
                WHERE id_compra = ?
                """;

            PreparedStatement stmtEstado = conn.prepareStatement(sqlEstado);
            stmtEstado.setInt(1, idCompra);
            stmtEstado.executeUpdate();

            conn.commit();
            System.out.println("✅ Compra completada exitosamente: " + numeroCompra);
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al completar compra: " + e.getMessage());
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("⚠️ Transacción revertida");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Actualizar precio_costo si el proveedor es principal
     */
    private void actualizarPrecioCostoSiEsPrincipal(Connection conn, int idCompra, int idProveedor) throws SQLException {
        String sql = """
            UPDATE producto p
            INNER JOIN compra_detalle cd ON p.id_producto = cd.id_producto
            INNER JOIN producto_proveedor pp ON p.id_producto = pp.id_producto
            SET p.precio_costo = cd.precio_unitario
            WHERE cd.id_compra = ?
              AND pp.id_proveedor = ?
              AND pp.es_proveedor_principal = TRUE
            """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idCompra);
        stmt.setInt(2, idProveedor);
        int actualizados = stmt.executeUpdate();

        if (actualizados > 0) {
            System.out.println("💰 Precio costo actualizado: " + actualizados + " productos (proveedor principal)");
        }
    }

    // ===== CANCELAR COMPRA (CRÍTICO) =====

    /**
     * CONCEPTO: Cancelar compra - Revierte stock si estaba COMPLETADA
     * Muestra advertencia al usuario antes de ejecutar
     */
    public boolean cancelarCompra(int idCompra) {
        Connection conn = null;

        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);

            // 1. Verificar estado actual
            String sqlEstado = "SELECT estado, numero_compra, id_usuario FROM compra WHERE id_compra = ?";
            PreparedStatement stmtEstado = conn.prepareStatement(sqlEstado);
            stmtEstado.setInt(1, idCompra);
            ResultSet rs = stmtEstado.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Compra no encontrada");
            }

            String estadoActual = rs.getString("estado");
            String numeroCompra = rs.getString("numero_compra");
            int idUsuario = rs.getInt("id_usuario");

            if ("CANCELADA".equals(estadoActual)) {
                throw new SQLException("La compra ya está cancelada");
            }

            // 2. Si estaba COMPLETADA, revertir stock
            if ("COMPLETADA".equals(estadoActual)) {
                // Restar stock
                String sqlRevertirStock = """
                    UPDATE producto p
                    INNER JOIN compra_detalle cd ON p.id_producto = cd.id_producto
                    SET p.stock_actual = p.stock_actual - cd.cantidad
                    WHERE cd.id_compra = ? 
                      AND p.tipo_producto = 'FISICO'
                    """;

                PreparedStatement stmtRevertir = conn.prepareStatement(sqlRevertirStock);
                stmtRevertir.setInt(1, idCompra);
                int revertidos = stmtRevertir.executeUpdate();

                System.out.println("🔄 Stock revertido: " + revertidos + " productos");

                // Registrar movimientos de reversión
                String sqlMovimientos = """
                    INSERT INTO movimiento (
                        id_producto, id_tipo_movimiento, cantidad, 
                        stock_anterior, stock_nuevo, motivo, referencia, id_usuario
                    )
                    SELECT 
                        cd.id_producto,
                        6,
                        cd.cantidad,
                        p.stock_actual + cd.cantidad,
                        p.stock_actual,
                        'Compra cancelada - stock revertido',
                        ?,
                        ?
                    FROM compra_detalle cd
                    INNER JOIN producto p ON cd.id_producto = p.id_producto
                    WHERE cd.id_compra = ? 
                      AND p.tipo_producto = 'FISICO'
                    """;

                PreparedStatement stmtMovimientos = conn.prepareStatement(sqlMovimientos);
                stmtMovimientos.setString(1, "Cancelacion_" + numeroCompra);
                stmtMovimientos.setInt(2, idUsuario);
                stmtMovimientos.setInt(3, idCompra);
                stmtMovimientos.executeUpdate();
            }

            // 3. Cambiar estado a CANCELADA
            String sqlCancelar = "UPDATE compra SET estado = 'CANCELADA' WHERE id_compra = ?";
            PreparedStatement stmtCancelar = conn.prepareStatement(sqlCancelar);
            stmtCancelar.setInt(1, idCompra);
            stmtCancelar.executeUpdate();

            conn.commit();
            System.out.println("✅ Compra cancelada exitosamente: " + numeroCompra);
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al cancelar compra: " + e.getMessage());

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ===== CONSULTAS =====

    /**
     * Obtener todas las compras
     */
    public List<Compra> obtenerCompras() {
        return obtenerComprasPorEstado(null);
    }

    /**
     * Obtener compras por estado
     */
    public List<Compra> obtenerComprasPorEstado(String estado) {
        List<Compra> compras = new ArrayList<>();

        String sql = """
            SELECT 
                c.id_compra, c.numero_compra, c.numero_factura_proveedor,
                c.id_proveedor, prov.nombre as nombre_proveedor,
                c.id_usuario, u.nombre as nombre_usuario,
                c.subtotal, c.descuentos, c.total,
                c.id_metodo_de_pago, mp.nombre as nombre_metodo_pago,
                c.fecha_compra, c.fecha_entrega,
                c.estado, c.observaciones
            FROM compra c
            INNER JOIN proveedor prov ON c.id_proveedor = prov.id_proveedor
            INNER JOIN usuario u ON c.id_usuario = u.id_usuario
            INNER JOIN metodo_de_pago mp ON c.id_metodo_de_pago = mp.id_metodo_de_pago
            """ + (estado != null ? "WHERE c.estado = ?" : "") + """
            ORDER BY c.fecha_compra DESC
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (estado != null) {
                stmt.setString(1, estado);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Compra compra = new Compra();
                compra.setIdCompra(rs.getInt("id_compra"));
                compra.setNumeroCompra(rs.getString("numero_compra"));
                compra.setNumeroFacturaProveedor(rs.getString("numero_factura_proveedor"));

                compra.setIdProveedor(rs.getInt("id_proveedor"));
                compra.setNombreProveedor(rs.getString("nombre_proveedor"));

                compra.setIdUsuario(rs.getInt("id_usuario"));
                compra.setNombreUsuario(rs.getString("nombre_usuario"));

                compra.setSubtotal(rs.getDouble("subtotal"));
                compra.setDescuentos(rs.getDouble("descuentos"));
                compra.setTotal(rs.getDouble("total"));

                compra.setIdMetodoDePago(rs.getInt("id_metodo_de_pago"));
                compra.setNombreMetodoPago(rs.getString("nombre_metodo_pago"));

                compra.setFechaCompra(rs.getTimestamp("fecha_compra"));
                compra.setFechaEntrega(rs.getTimestamp("fecha_entrega"));

                compra.setEstado(rs.getString("estado"));
                compra.setObservaciones(rs.getString("observaciones"));

                compras.add(compra);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener compras: " + e.getMessage());
        }

        return compras;
    }

    /**
     * Obtener detalle de productos de una compra
     */
    public List<Map<String, Object>> obtenerDetalleCompra(int idCompra) {
        List<Map<String, Object>> detalle = new ArrayList<>();

        String sql = """
            SELECT 
                cd.id_producto,
                p.codigo_barras,
                p.nombre as nombre_producto,
                cd.cantidad,
                cd.precio_unitario,
                cd.subtotal
            FROM compra_detalle cd
            INNER JOIN producto p ON cd.id_producto = p.id_producto
            WHERE cd.id_compra = ?
            ORDER BY p.nombre
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCompra);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new java.util.HashMap<>();
                item.put("idProducto", rs.getInt("id_producto"));
                item.put("codigoBarras", rs.getString("codigo_barras"));
                item.put("nombreProducto", rs.getString("nombre_producto"));
                item.put("cantidad", rs.getInt("cantidad"));
                item.put("precioUnitario", rs.getDouble("precio_unitario"));
                item.put("subtotal", rs.getDouble("subtotal"));

                detalle.add(item);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener detalle: " + e.getMessage());
        }

        return detalle;
    }

    /**
     * Obtener compra por ID
     */
    public Compra obtenerCompraPorId(int idCompra) {
        List<Compra> compras = obtenerCompras();
        return compras.stream()
                .filter(c -> c.getIdCompra() == idCompra)
                .findFirst()
                .orElse(null);
    }

    /**
     * Contar compras por estado
     */
    public int contarComprasPorEstado(String estado) {
        String sql = "SELECT COUNT(*) FROM compra WHERE estado = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar compras: " + e.getMessage());
        }

        return 0;
    }
}