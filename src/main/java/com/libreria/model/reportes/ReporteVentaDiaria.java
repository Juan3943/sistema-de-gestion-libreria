package com.libreria.model.reportes;

import java.time.LocalDate;

/**
 * MODELO: Representa UNA FILA del reporte de ventas diarias
 *
 * ¿Qué representa?
 * - Cada objeto = resumen de ventas de UN DÍA específico
 *
 * Ejemplo de datos:
 * - fecha: 2025-10-08
 * - cantidadVentas: 12 (se hicieron 12 ventas ese día)
 * - totalVendido: 45000.50 (se vendió $45,000.50 en total)
 */
public class ReporteVentaDiaria {

    // ==========================================
    // ATRIBUTOS (datos que guarda cada objeto)
    // ==========================================

    /**
     * La fecha del día que estamos reportando
     * Tipo LocalDate = fecha sin hora (solo día/mes/año)
     */
    private LocalDate fecha;

    /**
     * Cuántas ventas COMPLETADAS se hicieron ese día
     * (No cuenta ventas EN_PROCESO ni ANULADAS)
     */
    private int cantidadVentas;

    /**
     * Suma total del dinero vendido ese día
     * Ejemplo: si hubo 3 ventas de $1000, $2000 y $500
     * entonces totalVendido = 3500.00
     */
    private double totalVendido;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    /**
     * Constructor vacío
     * OBLIGATORIO para JavaFX TableView
     * JavaFX crea objetos vacíos y luego llama a los setters
     */
    public ReporteVentaDiaria() {
        // Vacío intencionalmente
    }

    /**
     * Constructor con parámetros
     * Útil para crear objetos cuando leemos la base de datos
     *
     * Ejemplo de uso:
     * ReporteVentaDiaria reporte = new ReporteVentaDiaria(
     *     LocalDate.of(2025, 10, 8),
     *     12,
     *     45000.50
     * );
     */
    public ReporteVentaDiaria(LocalDate fecha, int cantidadVentas, double totalVendido) {
        this.fecha = fecha;
        this.cantidadVentas = cantidadVentas;
        this.totalVendido = totalVendido;
    }

    // ==========================================
    // GETTERS (métodos para LEER los valores)
    // ==========================================

    /**
     * IMPORTANTE: El nombre del getter debe ser get + NombreAtributo
     * JavaFX busca estos métodos por nombre para llenar las columnas
     *
     * Para el atributo "fecha", el getter DEBE llamarse "getFecha()"
     */
    public LocalDate getFecha() {
        return fecha;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public double getTotalVendido() {
        return totalVendido;
    }

    // ==========================================
    // SETTERS (métodos para CAMBIAR los valores)
    // ==========================================

    /**
     * Los setters permiten modificar el valor después de crear el objeto
     * Ejemplo:
     * reporte.setFecha(LocalDate.now());
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setCantidadVentas(int cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }

    public void setTotalVendido(double totalVendido) {
        this.totalVendido = totalVendido;
    }

    // ==========================================
    // MÉTODO ÚTIL PARA DEBUGGING
    // ==========================================

    /**
     * toString() permite imprimir el objeto de forma legible
     * Útil para hacer System.out.println(reporte);
     */
    @Override
    public String toString() {
        return "ReporteVentaDiaria{" +
                "fecha=" + fecha +
                ", cantidadVentas=" + cantidadVentas +
                ", totalVendido=" + totalVendido +
                '}';
    }
}