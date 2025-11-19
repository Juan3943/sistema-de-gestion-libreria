package com.libreria.dao;

import com.libreria.model.Producto;
import com.libreria.model.Venta;
import com.libreria.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    /**
     * CONCEPTO: Crear nueva venta usando Stored Procedure
     * El SP se encarga de generar el número de comprobante automáticamente
     */
    public int crearVentaNueva() {
        String sql = "{CALL SP_CrearVentaSimple(?)}";
        try (Connection conn = ConexionBD.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.execute();

            int ventaId = stmt.getInt(1);
            System.out.println("Nueva venta creada con ID: " + ventaId);
            return ventaId;

        } catch (SQLException e) {
            System.err.println("Error al crear venta: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * CONCEPTO: Agregar producto usando Stored Procedure con validaciones automáticas
     * El SP valida stock, actualiza inventario y maneja duplicados automáticamente
     */
    public boolean agregarProducto(int idVenta, String codigoBarras, int cantidad) {
        String sql = "{CALL SP_AgregarProductoPorCodigo(?, ?, ?, ?, ?)}";
        try (Connection conn = ConexionBD.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idVenta);
            stmt.setString(2, codigoBarras);
            stmt.setInt(3, cantidad);
            stmt.registerOutParameter(4, Types.BOOLEAN);
            stmt.registerOutParameter(5, Types.VARCHAR);

            stmt.execute();

            boolean success = stmt.getBoolean(4);
            String mensaje = stmt.getString(5);

            System.out.println("Agregar producto - " + mensaje);
            return success;

        } catch (SQLException e) {
            System.err.println("Error al agregar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * CONCEPTO: Obtener carrito desde la Vista optimizada
     * La vista vw_detalle_carrito maneja los JOINs complejos
     */
    public List<Producto> obtenerCarrito(int idVenta) {
        List<Producto> carrito = new ArrayList<>();
        String sql = "SELECT * FROM vw_detalle_carrito WHERE id_venta = ? ORDER BY nombre";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigo(rs.getString("codigo"));
                producto.setNombre(rs.getString("nombre"));
                producto.setCantidad(rs.getInt("cantidad"));
                producto.setPrecio(rs.getDouble("precio"));
                carrito.add(producto);
            }

            System.out.println("Carrito cargado: " + carrito.size() + " items");

        } catch (SQLException e) {
            System.err.println("Error al cargar carrito: " + e.getMessage());
            e.printStackTrace();
        }

        return carrito;
    }

    /**
     * CONCEPTO: Eliminar producto usando Stored Procedure
     * El SP maneja la restauración de stock automáticamente
     */
    public boolean eliminarProducto(int idVenta, String codigoBarras) {
        String sql = "{CALL SP_EliminarPorCodigo(?, ?, ?, ?)}";
        try (Connection conn = ConexionBD.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idVenta);
            stmt.setString(2, codigoBarras);
            stmt.registerOutParameter(3, Types.BOOLEAN);
            stmt.registerOutParameter(4, Types.VARCHAR);

            stmt.execute();

            boolean success = stmt.getBoolean(3);
            String mensaje = stmt.getString(4);

            System.out.println("Eliminar producto - " + mensaje);
            return success;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * CONCEPTO: Modificar cantidad usando Stored Procedure con validaciones
     * El SP valida stock disponible y actualiza inventario
     */
    public boolean modificarCantidad(int idVenta, String codigoBarras, int nuevaCantidad) {
        String sql = "{CALL SP_ModificarCantidadPorCodigo(?, ?, ?, ?, ?)}";
        try (Connection conn = ConexionBD.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idVenta);
            stmt.setString(2, codigoBarras);
            stmt.setInt(3, nuevaCantidad);
            stmt.registerOutParameter(4, Types.BOOLEAN);
            stmt.registerOutParameter(5, Types.VARCHAR);

            stmt.execute();

            boolean success = stmt.getBoolean(4);
            String mensaje = stmt.getString(5);

            if (!success) {
                System.out.println("Error modificar cantidad: " + mensaje);
            }
            return success;

        } catch (SQLException e) {
            System.err.println("Error al modificar cantidad: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * CONCEPTO: Completar venta usando Stored Procedure
     * El SP calcula totales, valida la venta y cambia el estado
     */
    public boolean completarVenta(int idVenta) {
        String sql = "{CALL SP_CompletarVentaSimple(?, ?, ?)}";
        try (Connection conn = ConexionBD.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idVenta);
            stmt.registerOutParameter(2, Types.BOOLEAN);
            stmt.registerOutParameter(3, Types.VARCHAR);

            stmt.execute();

            boolean success = stmt.getBoolean(2);
            String mensaje = stmt.getString(3);

            System.out.println("Completar venta - " + mensaje);
            return success;

        } catch (SQLException e) {
            System.err.println("Error al completar venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * CONCEPTO: Obtener total desde la Vista calculada
     * Más confiable que calcular en JavaFX
     */
    public double obtenerTotal(int idVenta) {
        String sql = "SELECT total_guardado FROM vw_totales_venta WHERE id_venta = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_guardado");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener total: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * CONCEPTO: Limpiar carrito usando Stored Procedure
     * El SP maneja la restauración de stock automáticamente
     */
    public boolean limpiarCarrito(int idVenta) {
        String sql = "{CALL SP_LimpiarCarrito(?, ?, ?)}";
        try (Connection conn = ConexionBD.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idVenta);
            stmt.registerOutParameter(2, Types.BOOLEAN);
            stmt.registerOutParameter(3, Types.VARCHAR);

            stmt.execute();

            boolean success = stmt.getBoolean(2);
            String mensaje = stmt.getString(3);

            System.out.println("Limpiar carrito - " + mensaje);
            return success;

        } catch (SQLException e) {
            System.err.println("Error al limpiar carrito: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean actualizarMetodoPago(int idVenta, int idMetodoPago) {
        String sql = "UPDATE venta SET id_metodo_de_pago = ? WHERE id_venta = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMetodoPago);
            stmt.setInt(2, idVenta);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * CONCEPTO: Anular venta usando Stored Procedure
     * Útil para cancelar ventas en proceso
     */
    public boolean anularVenta(int idVenta) {
        String sql = "{CALL SP_AnularVenta(?, ?, ?)}";
        try (Connection conn = ConexionBD.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idVenta);
            stmt.registerOutParameter(2, Types.BOOLEAN);
            stmt.registerOutParameter(3, Types.VARCHAR);

            stmt.execute();

            boolean success = stmt.getBoolean(2);
            String mensaje = stmt.getString(3);

            System.out.println("Anular venta - " + mensaje);
            return success;

        } catch (SQLException e) {
            System.err.println("Error al anular venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * CONCEPTO: Obtener información completa de la venta
     * Para mostrar detalles en reportes o facturas
     */
    public VentaCompleta obtenerVentaCompleta(int idVenta) {
        String sql = "{CALL SP_ObtenerResumenVenta(?)}";

        try (Connection conn = ConexionBD.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idVenta);
            boolean hasResults = stmt.execute();

            VentaCompleta ventaCompleta = new VentaCompleta();

            if (hasResults) {
                // Primera consulta: datos de cabecera
                ResultSet rs1 = stmt.getResultSet();
                if (rs1.next()) {
                    ventaCompleta.numeroComprobante = rs1.getString("numero_comprobante");
                    ventaCompleta.fecha = rs1.getTimestamp("fecha");
                    ventaCompleta.subtotal = rs1.getDouble("subtotal");
                    ventaCompleta.total = rs1.getDouble("total");
                    ventaCompleta.vendedor = rs1.getString("vendedor");
                    ventaCompleta.cliente = rs1.getString("cliente");
                }

                // Segunda consulta: detalles de productos
                if (stmt.getMoreResults()) {
                    ResultSet rs2 = stmt.getResultSet();
                    while (rs2.next()) {
                        DetalleVenta detalle = new DetalleVenta();
                        detalle.codigo = rs2.getString("codigo");
                        detalle.producto = rs2.getString("producto");
                        detalle.cantidad = rs2.getInt("cantidad");
                        detalle.precioUnitario = rs2.getDouble("precio_unitario");
                        detalle.subtotal = rs2.getDouble("subtotal");
                        ventaCompleta.detalles.add(detalle);
                    }
                }
            }

            return ventaCompleta;

        } catch (SQLException e) {
            System.err.println("Error al obtener resumen de venta: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * CONCEPTO: Verificar estado de una venta
     * Útil para validaciones en la interfaz
     */
    public String obtenerEstadoVenta(int idVenta) {
        String sql = "SELECT estado FROM venta WHERE id_venta = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("estado");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener estado de venta: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * CONCEPTO: Obtener número de comprobante
     * Para mostrar en la interfaz
     */
    public String obtenerNumeroComprobante(int idVenta) {
        String sql = "SELECT numero_comprobante FROM venta WHERE id_venta = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("numero_comprobante");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener número de comprobante: " + e.getMessage());
            e.printStackTrace();
        }

        return "N/A";
    }

    public boolean asociarCliente(int idVenta, int idCliente) {
        String sql = "UPDATE venta SET id_cliente = ? WHERE id_venta = ?";

        System.out.println("📝 Ejecutando UPDATE venta...");
        System.out.println("   - id_cliente = " + idCliente);
        System.out.println("   - id_venta = " + idVenta);

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            stmt.setInt(2, idVenta);

            int rowsUpdated = stmt.executeUpdate();

            System.out.println("   - Filas afectadas: " + rowsUpdated);

            if (rowsUpdated > 0) {
                System.out.println("✅ Cliente asociado a venta ID: " + idVenta);
                return true;
            } else {
                System.err.println("⚠️ No se actualizó ninguna fila (¿venta no existe?)");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error SQL al asociar cliente: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * CONCEPTO: Obtener ventas con paginación usando Stored Procedure
     * Este método llama al SP que hace el trabajo pesado en la BD
     *
     * @param pagina Número de página (1, 2, 3...)
     * @param registrosPorPagina Cuántos registros por página (típicamente 20)
     * @param busqueda Texto a buscar en número de comprobante (null para todos)
     * @param fechaDesde Filtrar desde esta fecha (null para sin filtro)
     * @param fechaHasta Filtrar hasta esta fecha (null para sin filtro)
     * @param idCliente ID del cliente a filtrar (null para todos)
     * @param estado Estado a filtrar: COMPLETADA, ANULADA (null para todos)
     * @return ResultadoPaginado con la lista y el total de registros
     */
    public ResultadoPaginado obtenerVentasPaginadas(
            int pagina,
            int registrosPorPagina,
            String busqueda,
            java.time.LocalDate fechaDesde,
            java.time.LocalDate fechaHasta,
            Integer idCliente,
            String estado
    ) {
        String sql = "{CALL SP_ObtenerVentasPaginadas(?, ?, ?, ?, ?, ?, ?, ?)}";
        List<Venta> ventas = new ArrayList<>();
        int totalRegistros = 0;

        try (Connection conn = ConexionBD.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Parámetros de entrada
            stmt.setInt(1, pagina);
            stmt.setInt(2, registrosPorPagina);

            // Busqueda (puede ser null)
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                stmt.setString(3, busqueda);
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            // Fechas (pueden ser null)
            if (fechaDesde != null) {
                stmt.setDate(4, java.sql.Date.valueOf(fechaDesde));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            if (fechaHasta != null) {
                stmt.setDate(5, java.sql.Date.valueOf(fechaHasta));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            // Cliente (puede ser null)
            if (idCliente != null) {
                stmt.setInt(6, idCliente);
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            // Estado (puede ser null o "TODOS")
            if (estado != null && !estado.equals("TODOS")) {
                stmt.setString(7, estado);
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }

            // Parámetro de salida: total de registros
            stmt.registerOutParameter(8, Types.INTEGER);

            // Ejecutar
            boolean hasResults = stmt.execute();

            if (hasResults) {
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    Venta venta = new Venta();
                    venta.setIdVenta(rs.getInt("id_venta"));
                    venta.setNumeroComprobante(rs.getString("numero_comprobante"));
                    venta.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    venta.setNombreCliente(rs.getString("cliente"));
                    venta.setTotal(rs.getDouble("total"));
                    venta.setEstado(rs.getString("estado"));
                    venta.setNombreVendedor(rs.getString("vendedor"));
                    ventas.add(venta);
                }
            }

            // Obtener total de registros del parámetro OUT
            totalRegistros = stmt.getInt(8);

            System.out.println("✅ Ventas paginadas obtenidas: " + ventas.size() +
                    " de " + totalRegistros + " totales");

        } catch (SQLException e) {
            System.err.println("Error al obtener ventas paginadas: " + e.getMessage());
            e.printStackTrace();
        }

        return new ResultadoPaginado(ventas, totalRegistros);
    }

    /**
     * CONCEPTO: Limpiar ventas antiguas en proceso usando Stored Procedure
     * Esto elimina ventas que nunca se completaron y quedaron "colgadas"
     *
     * @param diasAntiguedad Eliminar ventas con más de X días sin completar
     * @return Número de ventas eliminadas
     */
    public int limpiarVentasEnProceso(int diasAntiguedad) {
        String sql = "{CALL SP_LimpiarVentasEnProceso(?, ?, ?)}";

        try (Connection conn = ConexionBD.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, diasAntiguedad);
            stmt.registerOutParameter(2, Types.INTEGER);
            stmt.registerOutParameter(3, Types.VARCHAR);

            stmt.execute();

            int ventasEliminadas = stmt.getInt(2);
            String mensaje = stmt.getString(3);

            System.out.println("🧹 " + mensaje);
            return ventasEliminadas;

        } catch (SQLException e) {
            System.err.println("Error al limpiar ventas: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    // ===== CLASES AUXILIARES PARA DATOS COMPLEJOS =====


    /**
     * CONCEPTO: Clase interna para manejar resultados paginados
     * Contiene tanto la lista de ventas como el total de registros
     */
    public static class ResultadoPaginado {
        public List<Venta> ventas;
        public int totalRegistros;

        public ResultadoPaginado(List<Venta> ventas, int totalRegistros) {
            this.ventas = ventas;
            this.totalRegistros = totalRegistros;
        }
    }

    /**
     * CONCEPTO: Clase para manejar datos completos de una venta
     * Incluye cabecera y detalles para reportes
     */
    public static class VentaCompleta {
        public String numeroComprobante;
        public java.sql.Timestamp fecha;
        public double subtotal;
        public double total;
        public String vendedor;
        public String cliente;
        public List<DetalleVenta> detalles = new ArrayList<>();
    }

    /**
     * CONCEPTO: Clase para detalles de productos en una venta
     */
    /**
     * CONCEPTO: Clase para detalles de productos en una venta
     * IMPORTANTE: PropertyValueFactory requiere getters para acceder a los datos
     */
    public static class DetalleVenta {
        public String codigo;
        public String producto;
        public int cantidad;
        public double precioUnitario;
        public double subtotal;

        // Constructor vacío
        public DetalleVenta() {}

        // Getters y Setters
        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getProducto() {
            return producto;
        }

        public void setProducto(String producto) {
            this.producto = producto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(double precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(double subtotal) {
            this.subtotal = subtotal;
        }
    }
}