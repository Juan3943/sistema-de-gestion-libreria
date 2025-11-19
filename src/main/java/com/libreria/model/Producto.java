package com.libreria.model;


public class Producto {

    // Campos principales desde BD
    private int idProducto;
    private String codigo;        // codigo_barras
    private String nombre;
    private String descripcion;
    private double precio;        // precio_venta
    private int stock;           // stock_actual
    private String tipoProducto; // FISICO o SERVICIO

    // Campos para la interfaz
    private int cantidad = 1;    // Para el spinner (cantidad a agregar al carrito)

    // Campos adicionales (opcionales)
    private String categoria;
    private String marca;
    private double precioCosto;
    private int stockMinimo;
    private boolean activo = true;

    // ===== CONSTRUCTORES =====

    /**
     * Constructor vacío para JavaFX
     */
    public Producto() {}

    /**
     * Constructor básico para compatibilidad con código existente
     */
    public Producto(String codigo, String nombre, double precio, int stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.tipoProducto = "FISICO"; // Por defecto
        this.cantidad = 1;
    }

    /**
     * Constructor completo para productos desde BD
     */
    public Producto(String codigo, String nombre, String descripcion, double precio,
                    int stock, String tipoProducto) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.tipoProducto = tipoProducto != null ? tipoProducto : "FISICO";
        this.cantidad = 1;
    }

    // ===== GETTERS Y SETTERS =====

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }
    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    /**
     * CONCEPTO: Cantidad para el carrito/spinner
     * Este valor NO se guarda en BD, es solo para la interfaz
     */
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        // Validación para evitar valores inválidos
        this.cantidad = Math.max(1, cantidad);
    }

    // Campos adicionales (opcionales)
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPrecioCosto() {
        return precioCosto;
    }
    public void setPrecioCosto(double precioCosto) {
        this.precioCosto = precioCosto;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // ===== MÉTODOS DE UTILIDAD =====

    /**
     * CONCEPTO: Verificar si es producto físico
     * Los productos físicos tienen stock limitado
     */
    public boolean esFisico() {
        return "FISICO".equals(tipoProducto);
    }

    /**
     * CONCEPTO: Verificar si es servicio
     * Los servicios tienen stock ilimitado
     */
    public boolean esServicio() {
        return "SERVICIO".equals(tipoProducto);
    }

    /**
     * CONCEPTO: Verificar si hay stock suficiente
     * Para productos físicos verifica stock real, para servicios siempre true
     */
    public boolean hayStockSuficiente(int cantidadRequerida) {
        if (esServicio()) {
            return true; // Los servicios tienen stock ilimitado
        }
        return stock >= cantidadRequerida;
    }

    /**
     * CONCEPTO: Obtener stock disponible para mostrar
     * Para servicios muestra 999 (stock "ilimitado")
     */
    public int getStockDisponible() {
        return esServicio() ? 999 : stock;
    }

    /**
     * CONCEPTO: Calcular subtotal para el carrito
     * Precio × cantidad seleccionada
     */
    public double calcularSubtotal() {
        return precio * cantidad;
    }

    /**
     * CONCEPTO: Verificar si el producto está disponible
     * Debe estar activo y tener stock (para físicos)
     */

    public boolean estaActivo(){
        return (this.activo);
    }

    public boolean estaDisponible() {
        // Debe estar activo primero
        if (!activo) {
            return false;
        }

        if (esServicio()) {
            return true;
        }
        return stock > 0;
    }
    /**
     * CONCEPTO: Obtener descripción para mostrar en UI
     * Con información de stock y tipo
     */
    public String getDescripcionCompleta() {
        StringBuilder desc = new StringBuilder();
        desc.append(nombre);

        if (descripcion != null && !descripcion.trim().isEmpty()) {
            desc.append(" - ").append(descripcion);
        }

        desc.append(" (").append(tipoProducto).append(")");

        if (esFisico()) {
            desc.append(" - Stock: ").append(stock);
        }

        return desc.toString();
    }

    // ===== MÉTODOS PARA DEBUGGING =====

    @Override
    public String toString() {
        return String.format("Producto{codigo='%s', nombre='%s', precio=%.2f, stock=%d, tipo='%s', cantidad=%d, activo=%b}",
                codigo, nombre, precio, stock, tipoProducto, cantidad, activo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Producto producto = (Producto) obj;
        return codigo != null ? codigo.equals(producto.codigo) : producto.codigo == null;
    }

    @Override
    public int hashCode() {
        return codigo != null ? codigo.hashCode() : 0;
    }

    // ===== MÉTODOS PARA REPORTES =====

    /**
     * CONCEPTO: Formatear precio como string
     */
    public String getPrecioFormateado() {
        return String.format("$%.2f", precio);
    }

    /**
     * CONCEPTO: Obtener estado del stock
     */
    public String getEstadoStock() {
        if (esServicio()) {
            return "ILIMITADO";
        }

        if (stock <= 0) {
            return "SIN STOCK";
        } else if (stockMinimo > 0 && stock <= stockMinimo) {
            return "STOCK BAJO";
        } else {
            return "DISPONIBLE";
        }
    }

    /**
     * CONCEPTO: Validar datos del producto
     */
    public boolean esValido() {
        return codigo != null && !codigo.trim().isEmpty() &&
                nombre != null && !nombre.trim().isEmpty() &&
                precio >= 0 &&
                stock >= 0 &&
                tipoProducto != null &&
                activo; // Solo válido si está activo
    }
}