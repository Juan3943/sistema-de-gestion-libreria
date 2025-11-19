package com.libreria.model.reportes;


/**
 * MODELO: Representa un producto en el Top 10 más vendidos
 *
 * ¿Qué representa?
 * - Cada objeto = UN producto con sus estadísticas de venta
 *
 * Ejemplo de datos:
 * - codigo: "7790001001011"
 * - nombre: "Cuaderno Rivadavia 48 hojas"
 * - cantidadVendida: 45 (se vendieron 45 unidades)
 * - totalGenerado: 67500.00 (generó $67,500 en ventas)
 */
public class ReporteProductoTop {

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    /**
     * Código de barras del producto
     * Ejemplo: "7790001001011"
     */
    private String codigo;

    /**
     * Nombre completo del producto
     * Ejemplo: "Cuaderno Rivadavia 48 hojas rayado"
     */
    private String nombre;

    /**
     * Total de unidades vendidas en el período
     * Suma de todas las cantidades vendidas
     */
    private int cantidadVendida;

    /**
     * Dinero total generado por este producto
     * Suma de todos los subtotales (cantidad × precio)
     */
    private double totalGenerado;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    /**
     * Constructor vacío
     * Requerido por JavaFX
     */
    public ReporteProductoTop() {
    }

    /**
     * Constructor con parámetros
     * Útil al leer de la base de datos
     */
    public ReporteProductoTop(String codigo, String nombre,
                              int cantidadVendida, double totalGenerado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidadVendida = cantidadVendida;
        this.totalGenerado = totalGenerado;
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public double getTotalGenerado() {
        return totalGenerado;
    }

    // ==========================================
    // SETTERS
    // ==========================================

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public void setTotalGenerado(double totalGenerado) {
        this.totalGenerado = totalGenerado;
    }

    // ==========================================
    // GETTER CALCULADO (BONUS)
    // ==========================================

    /**
     * Calcula el precio promedio por unidad vendida
     * No es un atributo guardado, se calcula on-the-fly
     *
     * Ejemplo:
     * - totalGenerado: 67500
     * - cantidadVendida: 45
     * - resultado: 1500.00 (precio promedio)
     */
    public double getPrecioPromedio() {
        if (cantidadVendida > 0) {
            return totalGenerado / cantidadVendida;
        }
        return 0.0;
    }

    // ==========================================
    // MÉTODO PARA DEBUGGING
    // ==========================================

    @Override
    public String toString() {
        return "ReporteProductoTop{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", cantidadVendida=" + cantidadVendida +
                ", totalGenerado=" + totalGenerado +
                '}';
    }
}