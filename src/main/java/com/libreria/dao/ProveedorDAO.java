package com.libreria.dao;

import com.libreria.model.ArchivoProveedor;
import com.libreria.model.Proveedor;
import com.libreria.model.ProductoProveedor;
import com.libreria.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProveedorDAO {

    // ===== GESTIÓN DE PROVEEDORES =====


    public List<Proveedor> obtenerProveedores() {
        List<Proveedor> proveedores = new ArrayList<>();

        String sql = """
            SELECT 
                id_proveedor, nombre, cuit, telefono, email, 
                direccion, activo, fecha_creacion
            FROM proveedor
            ORDER BY nombre
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setIdProveedor(rs.getInt("id_proveedor"));
                proveedor.setNombre(rs.getString("nombre"));
                proveedor.setCuit(rs.getString("cuit"));
                proveedor.setTelefono(rs.getString("telefono"));
                proveedor.setEmail(rs.getString("email"));
                proveedor.setDireccion(rs.getString("direccion"));
                proveedor.setActivo(rs.getBoolean("activo"));
                proveedor.setFechaCreacion(rs.getTimestamp("fecha_creacion"));

                proveedores.add(proveedor);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores: " + e.getMessage());
            e.printStackTrace();
        }

        return proveedores;
    }

    /**
     * Crear nuevo proveedor
     * CONCEPTO: INSERT simple con validaciones
     */
    public boolean crearProveedor(String nombre, String cuit, String telefono,
                                  String email, String direccion) {

        // Validación básica
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        String sql = """
            INSERT INTO proveedor (nombre, cuit, telefono, email, direccion, activo)
            VALUES (?, ?, ?, ?, ?, TRUE)
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre.trim());
            stmt.setString(2, cuit == null || cuit.trim().isEmpty() ? null : cuit.trim());
            stmt.setString(3, telefono == null || telefono.trim().isEmpty() ? null : telefono.trim());
            stmt.setString(4, email == null || email.trim().isEmpty() ? null : email.trim());
            stmt.setString(5, direccion == null || direccion.trim().isEmpty() ? null : direccion.trim());

            int filasAfectadas = stmt.executeUpdate();
            System.out.println("Proveedor creado: " + nombre);

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear proveedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizar proveedor existente
     * CONCEPTO: UPDATE por ID
     */
    public boolean actualizarProveedor(int idProveedor, String nombre, String cuit,
                                       String telefono, String email, String direccion) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        String sql = """
            UPDATE proveedor 
            SET nombre = ?, cuit = ?, telefono = ?, email = ?, direccion = ?
            WHERE id_proveedor = ?
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre.trim());
            stmt.setString(2, cuit == null || cuit.trim().isEmpty() ? null : cuit.trim());
            stmt.setString(3, telefono == null || telefono.trim().isEmpty() ? null : telefono.trim());
            stmt.setString(4, email == null || email.trim().isEmpty() ? null : email.trim());
            stmt.setString(5, direccion == null || direccion.trim().isEmpty() ? null : direccion.trim());
            stmt.setInt(6, idProveedor);

            int filasAfectadas = stmt.executeUpdate();
            System.out.println("Proveedor actualizado: " + nombre);

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambiar estado del proveedor (activar/desactivar)
     * CONCEPTO: Borrado lógico
     */
    public boolean cambiarEstado(int idProveedor, boolean activo) {
        String sql = "UPDATE proveedor SET activo = ? WHERE id_proveedor = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, activo);
            stmt.setInt(2, idProveedor);

            int filasAfectadas = stmt.executeUpdate();
            System.out.println("Estado del proveedor cambiado a: " + (activo ? "Activo" : "Inactivo"));

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }

    // ===== GESTIÓN DE PRODUCTOS DEL PROVEEDOR =====

    /**
     * Obtener productos de un proveedor específico
     * CONCEPTO: JOIN entre producto_proveedor y producto
     */
    public List<ProductoProveedor> obtenerProductosProveedor(int idProveedor) {
        List<ProductoProveedor> productos = new ArrayList<>();

        String sql = """
            SELECT 
                pp.id_producto,
                pp.id_proveedor,
                p.codigo_barras,
                p.nombre as nombre_producto,
                pp.codigo_proveedor,
                pp.precio_compra,
                pp.precio_compra_anterior,
                pp.fecha_ultima_compra,
                pp.es_proveedor_principal,
                pp.activo
            FROM producto_proveedor pp
            INNER JOIN producto p ON pp.id_producto = p.id_producto
            WHERE pp.id_proveedor = ? AND pp.activo = TRUE
            ORDER BY p.nombre
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProveedor);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProductoProveedor pp = new ProductoProveedor();
                pp.setIdProducto(rs.getInt("id_producto"));
                pp.setIdProveedor(rs.getInt("id_proveedor"));
                pp.setCodigoProducto(rs.getString("codigo_barras"));
                pp.setNombreProducto(rs.getString("nombre_producto"));
                pp.setCodigoProveedor(rs.getString("codigo_proveedor"));
                pp.setPrecioCompra(rs.getDouble("precio_compra"));
                pp.setPrecioCompraAnterior(rs.getDouble("precio_compra_anterior"));
                pp.setFechaUltimaCompra(rs.getTimestamp("fecha_ultima_compra"));
                pp.setEsPrincipal(rs.getBoolean("es_proveedor_principal"));
                pp.setActivo(rs.getBoolean("activo"));

                productos.add(pp);
            }

            System.out.println("Productos del proveedor: " + productos.size());

        } catch (SQLException e) {
            System.err.println("Error al obtener productos del proveedor: " + e.getMessage());
            e.printStackTrace();
        }

        return productos;
    }

    /**
     * Vincular producto a proveedor
     * CONCEPTO: INSERT con transacción para manejar proveedor principal
     */
    public boolean vincularProducto(int idProducto, int idProveedor, String codigoProveedor,
                                    double precioCompra, boolean esPrincipal) {

        String sql = """
            INSERT INTO producto_proveedor 
            (id_producto, id_proveedor, codigo_proveedor, precio_compra, 
             es_proveedor_principal, activo)
            VALUES (?, ?, ?, ?, ?, TRUE)
            """;

        try (Connection conn = ConexionBD.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transacción

            try {
                // Si es principal, primero desmarcar otros
                if (esPrincipal) {
                    desmarcarPrincipal(conn, idProducto);
                }

                // Insertar la nueva relación
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, idProducto);
                    stmt.setInt(2, idProveedor);
                    stmt.setString(3, codigoProveedor == null || codigoProveedor.trim().isEmpty()
                            ? null : codigoProveedor.trim());
                    stmt.setDouble(4, precioCompra);
                    stmt.setBoolean(5, esPrincipal);

                    int filasAfectadas = stmt.executeUpdate();

                    if (filasAfectadas > 0) {
                        conn.commit();
                        System.out.println("Producto vinculado al proveedor");
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error al vincular producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizar precio de compra
     * CONCEPTO: UPDATE que guarda el precio anterior
     */
    public boolean actualizarPrecioCompra(int idProducto, int idProveedor, double nuevoPrecio) {

        String sql = """
            UPDATE producto_proveedor 
            SET precio_compra_anterior = precio_compra,
                precio_compra = ?,
                fecha_ultima_compra = NOW()
            WHERE id_producto = ? AND id_proveedor = ?
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, nuevoPrecio);
            stmt.setInt(2, idProducto);
            stmt.setInt(3, idProveedor);

            int filasAfectadas = stmt.executeUpdate();
            System.out.println("Precio de compra actualizado");

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar precio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Marcar proveedor como principal
     * CONCEPTO: Transacción que desmarca otros y marca el nuevo
     */
    public boolean marcarComoPrincipal(int idProducto, int idProveedor) {

        try (Connection conn = ConexionBD.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Desmarcar todos los proveedores del producto
                desmarcarPrincipal(conn, idProducto);

                // 2. Marcar el nuevo como principal
                String sqlMarcar = """
                    UPDATE producto_proveedor 
                    SET es_proveedor_principal = TRUE
                    WHERE id_producto = ? AND id_proveedor = ?
                    """;

                try (PreparedStatement stmt = conn.prepareStatement(sqlMarcar)) {
                    stmt.setInt(1, idProducto);
                    stmt.setInt(2, idProveedor);

                    int filasAfectadas = stmt.executeUpdate();

                    if (filasAfectadas > 0) {
                        conn.commit();
                        System.out.println("Proveedor marcado como principal");
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error al marcar como principal: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método auxiliar: Desmarcar todos los proveedores principales de un producto
     * CONCEPTO: Reutilizable en transacciones
     */
    private void desmarcarPrincipal(Connection conn, int idProducto) throws SQLException {
        String sql = """
            UPDATE producto_proveedor 
            SET es_proveedor_principal = FALSE
            WHERE id_producto = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            stmt.executeUpdate();
        }
    }

    /**
     * Desvincular producto de proveedor
     * CONCEPTO: Borrado lógico (marcar como inactivo)
     */
    public boolean desvincularProducto(int idProducto, int idProveedor) {
        String sql = """
            UPDATE producto_proveedor 
            SET activo = FALSE
            WHERE id_producto = ? AND id_proveedor = ?
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);
            stmt.setInt(2, idProveedor);

            int filasAfectadas = stmt.executeUpdate();
            System.out.println("Producto desvinculado del proveedor");

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al desvincular producto: " + e.getMessage());
            return false;
        }
    }

    // ===== MÉTODOS AUXILIARES =====

    /**
     * Obtener ID de producto por código de barras
     */
    public Integer obtenerIdProductoPorCodigo(String codigoBarras) {
        String sql = "SELECT id_producto FROM producto WHERE codigo_barras = ? AND activo = TRUE";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoBarras);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_producto");
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar producto: " + e.getMessage());
        }

        return null;
    }

    /**
     * Verificar si ya existe relación producto-proveedor
     */
    public boolean existeRelacion(int idProducto, int idProveedor) {
        String sql = """
            SELECT COUNT(*) as total 
            FROM producto_proveedor 
            WHERE id_producto = ? AND id_proveedor = ? AND activo = TRUE
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);
            stmt.setInt(2, idProveedor);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar relación: " + e.getMessage());
        }

        return false;
    }

    // ===== GESTIÓN DE ARCHIVOS =====

    /**
     * Obtener archivos de un proveedor
     */
    public List<ArchivoProveedor> obtenerArchivosProveedor(int idProveedor) {
        List<ArchivoProveedor> archivos = new ArrayList<>();

        String sql = """
        SELECT 
            id_archivo, id_proveedor, nombre_archivo, ruta_archivo,
            tipo_archivo, descripcion, tamanio_bytes, fecha_subida, activo
        FROM proveedor_archivo
        WHERE id_proveedor = ? AND activo = TRUE
        ORDER BY fecha_subida DESC
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProveedor);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ArchivoProveedor archivo = new ArchivoProveedor();
                archivo.setIdArchivo(rs.getInt("id_archivo"));
                archivo.setIdProveedor(rs.getInt("id_proveedor"));
                archivo.setNombreArchivo(rs.getString("nombre_archivo"));
                archivo.setRutaArchivo(rs.getString("ruta_archivo"));
                archivo.setTipoArchivo(rs.getString("tipo_archivo"));
                archivo.setDescripcion(rs.getString("descripcion"));
                archivo.setTamanioBytes(rs.getLong("tamanio_bytes"));
                archivo.setFechaSubida(rs.getTimestamp("fecha_subida"));
                archivo.setActivo(rs.getBoolean("activo"));

                archivos.add(archivo);
            }

            System.out.println("📁 Archivos cargados: " + archivos.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener archivos: " + e.getMessage());
            e.printStackTrace();
        }

        return archivos;
    }

    /**
     * Registrar archivo en la base de datos
     */
    public boolean registrarArchivo(ArchivoProveedor archivo) {
        String sql = """
        INSERT INTO proveedor_archivo 
        (id_proveedor, nombre_archivo, ruta_archivo, tipo_archivo, 
         descripcion, tamanio_bytes, activo)
        VALUES (?, ?, ?, ?, ?, ?, TRUE)
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, archivo.getIdProveedor());
            stmt.setString(2, archivo.getNombreArchivo());
            stmt.setString(3, archivo.getRutaArchivo());
            stmt.setString(4, archivo.getTipoArchivo());
            stmt.setString(5, archivo.getDescripcion() == null || archivo.getDescripcion().trim().isEmpty()
                    ? null : archivo.getDescripcion());
            stmt.setLong(6, archivo.getTamanioBytes());

            int filasAfectadas = stmt.executeUpdate();
            System.out.println("✅ Archivo registrado en BD: " + archivo.getNombreArchivo());

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al registrar archivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Eliminar archivo (borrado lógico)
     */
    public boolean eliminarArchivo(int idArchivo) {
        String sql = "UPDATE proveedor_archivo SET activo = FALSE WHERE id_archivo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idArchivo);
            int filasAfectadas = stmt.executeUpdate();

            System.out.println("🗑️ Archivo marcado como eliminado");
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar archivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualizar descripción de archivo
     */
    public boolean actualizarDescripcionArchivo(int idArchivo, String nuevaDescripcion) {
        String sql = "UPDATE proveedor_archivo SET descripcion = ? WHERE id_archivo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevaDescripcion == null || nuevaDescripcion.trim().isEmpty()
                    ? null : nuevaDescripcion);
            stmt.setInt(2, idArchivo);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar descripción: " + e.getMessage());
            return false;
        }
    }

    /**
     * CONCEPTO: Obtener todos los proveedores que tienen un producto específico
     * Retorna lista con información completa para comparación
     */
    public List<ProductoProveedor> obtenerProveedoresDeProducto(int idProducto) {
        List<ProductoProveedor> proveedores = new ArrayList<>();

        String sql = """
        SELECT 
            pp.id_producto,
            pp.id_proveedor,
            pp.codigo_proveedor,
            pp.precio_compra,
            pp.es_proveedor_principal,
            prov.nombre as nombre_proveedor,
            p.codigo_barras as codigo_producto,
            p.nombre as nombre_producto
        FROM producto_proveedor pp
        INNER JOIN proveedor prov ON pp.id_proveedor = prov.id_proveedor
        INNER JOIN producto p ON pp.id_producto = p.id_producto
        WHERE pp.id_producto = ? 
          AND pp.activo = TRUE
        ORDER BY pp.precio_compra ASC
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProductoProveedor pp = new ProductoProveedor();
                pp.setIdProducto(rs.getInt("id_producto"));
                pp.setIdProveedor(rs.getInt("id_proveedor"));
                pp.setCodigoProveedor(rs.getString("codigo_proveedor"));
                pp.setPrecioCompra(rs.getDouble("precio_compra"));
                pp.setEsPrincipal(rs.getBoolean("es_proveedor_principal"));
                pp.setNombreProveedor(rs.getString("nombre_proveedor"));
                pp.setCodigoProducto(rs.getString("codigo_producto"));
                pp.setNombreProducto(rs.getString("nombre_producto"));

                proveedores.add(pp);
            }

            System.out.println("💰 Proveedores del producto: " + proveedores.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener proveedores del producto: " + e.getMessage());
            e.printStackTrace();
        }

        return proveedores;
    }

}