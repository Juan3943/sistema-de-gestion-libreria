package com.libreria.model.reportes;

public class ReporteProductoSinMovimiento {

    private String codigo;
    private String producto;
    private String categoria;
    private int stockActual;
    private int diasSinVenta;
    private double valorInventario;

    public ReporteProductoSinMovimiento() {}

    public ReporteProductoSinMovimiento(String codigo, String producto, String categoria,
                                        int stockActual, int diasSinVenta, double valorInventario) {
        this.codigo = codigo;
        this.producto = producto;
        this.categoria = categoria;
        this.stockActual = stockActual;
        this.diasSinVenta = diasSinVenta;
        this.valorInventario = valorInventario;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public int getDiasSinVenta() { return diasSinVenta; }
    public void setDiasSinVenta(int diasSinVenta) { this.diasSinVenta = diasSinVenta; }

    public double getValorInventario() { return valorInventario; }
    public void setValorInventario(double valorInventario) { this.valorInventario = valorInventario; }
}