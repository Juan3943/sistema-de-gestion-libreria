package com.libreria.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Venta {

    // ===== ATRIBUTOS BÁSICOS =====
    private int idVenta;
    private String numeroComprobante;
    private LocalDateTime fecha;
    private String estado;  // EN_PROCESO, COMPLETADA, ANULADA

    // ===== MONTOS =====
    private double subtotal;
    private double total;

    // ===== RELACIONES =====
    private Cliente cliente;              // Puede ser null (venta sin cliente)
    private MetodoDePago metodoDePago;    // Puede ser null si está EN_PROCESO
    private Usuario usuario;// Vendedor que hizo la venta

    // ===== ATRIBUTOS AUXILIARES (Strings - para consultas/historial) =====
    private String nombreCliente;   // Usado cuando viene del SP
    private String nombreVendedor;  // Usado cuando viene del SP

    // ===== DETALLES =====
    private List<Producto> productos;     // Productos en el carrito/venta

    // ===== CONSTRUCTORES =====

    /**
     * Constructor vacío - Para crear nuevas ventas
     */
    public Venta() {
        this.productos = new ArrayList<>();
        this.estado = "EN_PROCESO";
        this.fecha = LocalDateTime.now();
    }

    /**
     * Constructor para ventas existentes (desde BD)
     */
    public Venta(int idVenta, String numeroComprobante, LocalDateTime fecha,
                 String estado, double subtotal, double total) {
        this.idVenta = idVenta;
        this.numeroComprobante = numeroComprobante;
        this.fecha = fecha;
        this.estado = estado;
        this.subtotal = subtotal;
        this.total = total;
        this.productos = new ArrayList<>();
    }

    // ===== MÉTODOS DE NEGOCIO =====

    /**
     * CONCEPTO: Calcular subtotal sumando todos los productos
     * Esto mantiene el modelo actualizado sin depender del controller
     */
    public void calcularSubtotal() {
        this.subtotal = productos.stream()
                .mapToDouble(p -> p.getPrecio() * p.getCantidad())
                .sum();
    }

    /**
     * CONCEPTO: Calcular total (en este caso igual al subtotal)
     * Si más adelante agregas IVA o descuentos, lo haces aquí
     */
    public void calcularTotal() {
        calcularSubtotal();
        this.total = this.subtotal;
    }

    /**
     * CONCEPTO: Verificar si la venta puede ser completada
     */
    public boolean puedeCompletarse() {
        return !productos.isEmpty() &&
                metodoDePago != null &&
                estado.equals("EN_PROCESO");
    }

    /**
     * CONCEPTO: Verificar si la venta puede ser anulada
     */
    public boolean puedeAnularse() {
        return estado.equals("COMPLETADA");
    }

    /**
     * CONCEPTO: Agregar producto al carrito
     */
    public void agregarProducto(Producto producto) {
        // Buscar si ya existe el producto
        Producto existente = buscarProducto(producto.getCodigo());

        if (existente != null) {
            // Si existe, aumentar cantidad
            existente.setCantidad(existente.getCantidad() + producto.getCantidad());
        } else {
            // Si no existe, agregar nuevo
            productos.add(producto);
        }

        calcularTotal();
    }

    /**
     * CONCEPTO: Buscar producto en el carrito por código
     */
    public Producto buscarProducto(String codigo) {
        return productos.stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    /**
     * CONCEPTO: Eliminar producto del carrito
     */
    public void eliminarProducto(String codigo) {
        productos.removeIf(p -> p.getCodigo().equals(codigo));
        calcularTotal();
    }

    /**
     * CONCEPTO: Obtener cantidad total de items
     */
    public int getCantidadTotalItems() {
        return productos.stream()
                .mapToInt(Producto::getCantidad)
                .sum();
    }

    /**
     * CONCEPTO: Verificar si el carrito está vacío
     */
    public boolean estaVacio() {
        return productos.isEmpty();
    }

    /**
     * CONCEPTO: Limpiar todos los productos
     */
    public void limpiarProductos() {
        productos.clear();
        calcularTotal();
    }

    // ===== GETTERS Y SETTERS =====

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public MetodoDePago getMetodoDePago() {
        return metodoDePago;
    }

    public void setMetodoDePago(MetodoDePago metodoDePago) {
        this.metodoDePago = metodoDePago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        calcularTotal();
    }




    // ===== MÉTODOS AUXILIARES =====

    /**
     * CONCEPTO: Representación de la venta como String
     * Útil para debugging y logs
     */
    @Override
    public String toString() {
        return "Venta{" +
                "idVenta=" + idVenta +
                ", numeroComprobante='" + numeroComprobante + '\'' +
                ", fecha=" + fecha +
                ", estado='" + estado + '\'' +
                ", total=" + total +
                ", productos=" + productos.size() +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "Sin cliente") +
                '}';
    }

    /**
     * CONCEPTO: Obtener descripción del método de pago
     */
    public String getDescripcionMetodoPago() {
        return metodoDePago != null ? metodoDePago.getNombre() : "No definido";
    }

    public String getNombreCliente() {
        if (cliente != null) {
            return cliente.getNombre();
        }
        return nombreCliente != null ? nombreCliente : "Sin cliente";
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    /**
     * CONCEPTO: Método inteligente para obtener nombre del vendedor
     */
    public String getNombreVendedor() {
        if (usuario != null) {
            return usuario.getNombreUsuario();
        }
        return nombreVendedor != null ? nombreVendedor : "Sistema";
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }
}