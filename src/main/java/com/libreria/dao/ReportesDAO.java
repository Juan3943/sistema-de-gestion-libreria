package com.libreria.dao;

import com.libreria.model.reportes.*;
import com.libreria.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportesDAO {


    // 1. VENTAS DIARIAS

    // ==========================================
// 1. VENTAS DIARIAS
// ==========================================
    public List<ReporteVentaDiaria> obtenerVentasDiarias(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ReporteVentaDiaria> reportes = new ArrayList<>();

        String sql = "SELECT " +
                "    DATE(v.fecha) as fecha, " +
                "    COUNT(*) as cantidad_ventas, " +
                "    SUM(v.total) as total_vendido " +
                "FROM venta v " +
                "WHERE v.estado = 'COMPLETADA' " +
                "  AND DATE(v.fecha) BETWEEN ? AND ? " +
                "GROUP BY DATE(v.fecha) " +
                "ORDER BY fecha DESC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ReporteVentaDiaria reporte = new ReporteVentaDiaria(
                        rs.getDate("fecha").toLocalDate(),
                        rs.getInt("cantidad_ventas"),
                        rs.getDouble("total_vendido")
                );
                reportes.add(reporte);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportes;
    }

    public List<VentaDelDia> obtenerVentasDelDia(LocalDate fecha) {
        List<VentaDelDia> ventas = new ArrayList<>();

        String sql = "SELECT " +
                "    v.id_venta, " +
                "    v.numero_comprobante, " +
                "    TIME(v.fecha) as hora, " +
                "    COALESCE(c.nombre, 'Cliente General') as cliente, " +
                "    v.total " +
                "FROM venta v " +
                "LEFT JOIN cliente c ON v.id_cliente = c.id_cliente " +
                "WHERE DATE(v.fecha) = ? " +
                "  AND v.estado = 'COMPLETADA' " +
                "ORDER BY v.fecha ASC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fecha));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                VentaDelDia venta = new VentaDelDia(
                        rs.getInt("id_venta"),
                        rs.getString("numero_comprobante"),
                        rs.getString("hora"),
                        rs.getString("cliente"),
                        rs.getDouble("total")
                );
                ventas.add(venta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ventas;
    }

    // CLASE AUXILIAR

    public static class VentaDelDia {
        public int idVenta;
        public String numeroComprobante;
        public String hora;
        public String cliente;
        public double total;

        public VentaDelDia(int idVenta, String numeroComprobante, String hora,
                           String cliente, double total) {
            this.idVenta = idVenta;
            this.numeroComprobante = numeroComprobante;
            this.hora = hora;
            this.cliente = cliente;
            this.total = total;
        }

        public int getIdVenta() { return idVenta; }
        public String getNumeroComprobante() { return numeroComprobante; }
        public String getHora() { return hora; }
        public String getCliente() { return cliente; }
        public double getTotal() { return total; }
    }

    // ==========================================
    // 2. VENTAS SEMANALES
    // ==========================================
    public List<ReporteVentaSemanal> obtenerVentasSemanales(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ReporteVentaSemanal> reportes = new ArrayList<>();

        String sql = "SELECT " +
                "    CONCAT(YEAR(v.fecha), '-', LPAD(WEEK(v.fecha, 3), 2, '0')) as semana_iso, " +
                "    COUNT(*) as cantidad_ventas, " +
                "    SUM(v.total) as total_vendido " +
                "FROM venta v " +
                "WHERE v.estado = 'COMPLETADA' " +
                "  AND DATE(v.fecha) BETWEEN ? AND ? " +
                "GROUP BY semana_iso " +
                "ORDER BY semana_iso";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));

            ResultSet rs = stmt.executeQuery();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            while (rs.next()) {
                String semanaIso = rs.getString("semana_iso");

                // Calcular lunes y domingo de esa semana usando la primera fecha de la semana
                // Usamos el año y número de semana para calcular las fechas
                String[] partes = semanaIso.split("-");
                int year = Integer.parseInt(partes[0]);
                int weekNum = Integer.parseInt(partes[1]);

                // Calcular el lunes de esa semana ISO
                LocalDate lunes = LocalDate.of(year, 1, 1)
                        .with(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNum)
                        .with(java.time.DayOfWeek.MONDAY);

                // Domingo es 6 días después del lunes
                LocalDate domingo = lunes.plusDays(6);

                // Crear etiqueta legible
                String etiquetaSemana = lunes.format(formatter) + " - " + domingo.format(formatter);

                ReporteVentaSemanal reporte = new ReporteVentaSemanal(
                        etiquetaSemana,
                        lunes,
                        domingo,
                        rs.getInt("cantidad_ventas"),
                        rs.getDouble("total_vendido")
                );
                reportes.add(reporte);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportes;
    }

    // ==========================================
    // 3. PRODUCTOS MÁS VENDIDOS (TOP 10)
    // ==========================================
    public List<ReporteProductoTop> obtenerProductosMasVendidos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ReporteProductoTop> reportes = new ArrayList<>();

        String sql = "SELECT " +
                "    p.codigo_barras as codigo, " +
                "    p.nombre as producto, " +
                "    SUM(vd.cantidad) as cantidad_vendida, " +
                "    SUM(vd.subtotal) as total_generado " +
                "FROM venta_detalle vd " +
                "INNER JOIN producto p ON vd.id_producto = p.id_producto " +
                "INNER JOIN venta v ON vd.id_venta = v.id_venta " +
                "WHERE v.estado = 'COMPLETADA' " +
                "  AND DATE(v.fecha) BETWEEN ? AND ? " +
                "GROUP BY p.id_producto, p.codigo_barras, p.nombre " +
                "ORDER BY cantidad_vendida DESC " +
                "LIMIT 10";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ReporteProductoTop reporte = new ReporteProductoTop(
                        rs.getString("codigo"),
                        rs.getString("producto"),
                        rs.getInt("cantidad_vendida"),
                        rs.getDouble("total_generado")
                );
                reportes.add(reporte);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportes;
    }

    // ==========================================
    // 4. CORTE DE CAJA (por método de pago)
    // ==========================================
    public List<ReporteCorteCaja> obtenerCorteCaja(LocalDate fecha) {
        List<ReporteCorteCaja> reportes = new ArrayList<>();

        String sql = "SELECT " +
                "    mp.nombre as metodo_pago, " +
                "    COUNT(*) as cantidad_ventas, " +
                "    SUM(v.total) as monto_total " +
                "FROM venta v " +
                "INNER JOIN metodo_de_pago mp ON v.id_metodo_de_pago = mp.id_metodo_de_pago " +
                "WHERE v.estado = 'COMPLETADA' " +
                "  AND DATE(v.fecha) = ? " +
                "GROUP BY mp.id_metodo_de_pago, mp.nombre " +
                "ORDER BY monto_total DESC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fecha));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ReporteCorteCaja reporte = new ReporteCorteCaja(
                        rs.getString("metodo_pago"),
                        rs.getInt("cantidad_ventas"),
                        rs.getDouble("monto_total")
                );
                reportes.add(reporte);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportes;
    }

    // ==========================================
    // 5. STOCK CRÍTICO
    // ==========================================
    public List<ReporteStockCritico> obtenerStockCritico() {
        List<ReporteStockCritico> reportes = new ArrayList<>();

        String sql = "SELECT " +
                "    p.codigo_barras as codigo, " +
                "    p.nombre as producto, " +
                "    c.nombre as categoria, " +
                "    p.stock_actual, " +
                "    p.stock_minimo, " +
                "    (p.stock_minimo - p.stock_actual) as cantidad_faltante " +
                "FROM producto p " +
                "INNER JOIN categoria c ON p.id_categoria = c.id_categoria " +
                "WHERE p.tipo_producto = 'FISICO' " +
                "  AND p.activo = 1 " +
                "  AND p.stock_actual <= p.stock_minimo " +
                "ORDER BY cantidad_faltante DESC";

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ReporteStockCritico reporte = new ReporteStockCritico(
                        rs.getString("codigo"),
                        rs.getString("producto"),
                        rs.getString("categoria"),
                        rs.getInt("stock_actual"),
                        rs.getInt("stock_minimo"),
                        rs.getInt("cantidad_faltante")
                );
                reportes.add(reporte);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportes;
    }

    // ==========================================
    // 6. PRODUCTOS SIN MOVIMIENTO
    // ==========================================
    public List<com.libreria.model.reportes.ReporteProductoSinMovimiento> obtenerProductosSinMovimiento(int diasSinVenta) {
        List<com.libreria.model.reportes.ReporteProductoSinMovimiento> reportes = new ArrayList<>();

        String sql = "SELECT " +
                "    p.codigo_barras as codigo, " +
                "    p.nombre as producto, " +
                "    c.nombre as categoria, " +
                "    p.stock_actual, " +
                "    COALESCE(DATEDIFF(NOW(), p.fecha_ultima_venta), 999) as dias_sin_venta, " +
                "    (p.stock_actual * p.precio_costo) as valor_inventario " +
                "FROM producto p " +
                "INNER JOIN categoria c ON p.id_categoria = c.id_categoria " +
                "WHERE p.tipo_producto = 'FISICO' " +
                "  AND p.activo = 1 " +
                "  AND p.stock_actual > 0 " +
                "  AND (p.fecha_ultima_venta IS NULL " +
                "       OR DATEDIFF(NOW(), p.fecha_ultima_venta) >= ?) " +
                "ORDER BY dias_sin_venta DESC, valor_inventario DESC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, diasSinVenta);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                com.libreria.model.reportes.ReporteProductoSinMovimiento reporte = new com.libreria.model.reportes.ReporteProductoSinMovimiento(
                        rs.getString("codigo"),
                        rs.getString("producto"),
                        rs.getString("categoria"),
                        rs.getInt("stock_actual"),
                        rs.getInt("dias_sin_venta"),
                        rs.getDouble("valor_inventario")
                );
                reportes.add(reporte);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportes;
    }
}