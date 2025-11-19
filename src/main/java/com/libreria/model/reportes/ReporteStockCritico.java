package com.libreria.model.reportes;
import java.time.LocalDate;


public class ReporteStockCritico {
    private String codigo;
    private String producto;
    private String categoria;
    private int stockActual;
    private int stockMinimo;
    private int cantidadFaltante;

    public ReporteStockCritico() {}

    public ReporteStockCritico(String codigo, String producto, String categoria,
                               int stockActual, int stockMinimo, int cantidadFaltante) {
        this.codigo = codigo;
        this.producto = producto;
        this.categoria = categoria;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.cantidadFaltante = cantidadFaltante;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public int getCantidadFaltante() { return cantidadFaltante; }
    public void setCantidadFaltante(int cantidadFaltante) { this.cantidadFaltante = cantidadFaltante; }
}