package com.libreria.controller;

import com.libreria.dao.ProductoDAO;
import com.libreria.dao.VentaDAO;
import com.libreria.model.MetodoDePago;
import com.libreria.model.Producto;
import com.libreria.model.Venta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.libreria.dao.ClienteDAO;
import com.libreria.model.Cliente;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class VentaController {

    // Elementos de la interfaz
    @FXML private TabPane tabPane;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiarBusqueda;

    // Tabla productos
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, Integer> colCantidad;
    @FXML private TableColumn<Producto, Void> colAccion;

    // Tabla carrito
    @FXML private TableView<Producto> tablaCarrito;
    @FXML private TableColumn<Producto, String> colCarritoCodigo;
    @FXML private TableColumn<Producto, String> colCarritoNombre;
    @FXML private TableColumn<Producto, Double> colCarritoPrecio;
    @FXML private TableColumn<Producto, Integer> colCarritoCantidad;
    @FXML private TableColumn<Producto, Double> colCarritoSubtotal;
    @FXML private TableColumn<Producto, Void> colCarritoAcciones;

    // Labels y controles
    @FXML private Label lblItemsCarrito;
    @FXML private Label lblTotal;
    @FXML private Label lblTotalItems;
    @FXML private Label lblSubtotal;
    @FXML private Label lblIVA;
    @FXML private Label lblTotalFinal;
    @FXML private Label lblEstado;
    @FXML private Label lblFecha;

    // Botones
    @FXML private Button btnLimpiarCarrito;
    @FXML private Button btnContinuarComprando;
    //@FXML private Button btnProcesarVenta;
    @FXML private Button btnNuevaVenta;
    @FXML private Button btnVolverHistorial;

    // Resumen
    @FXML private TextArea txtResumenVenta;

    @FXML private VBox seccionMetodoPago;
    @FXML private ComboBox<String> cbMetodoPago; // String simple, no objeto
    @FXML private Button btnSeleccionarPago;
    @FXML private Button btnConfirmarVenta;

    //Seccion venta cliente
    @FXML private VBox seccionVentaCliente;
    @FXML private CheckBox chkVentaConCliente;
    @FXML private HBox panelSeleccionCliente;
    @FXML private ComboBox<Cliente> cbClientes;
    @FXML private Button btnNuevoClienteRapido;

    // DAOs y datos
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final ObservableList<Producto> productosData = FXCollections.observableArrayList();
    private final ObservableList<Producto> carritoData = FXCollections.observableArrayList();
    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private Venta ventaActual;
    private StackPane contenedorPrincipal;


    @FXML
    public void initialize() {
        System.out.println("🚀 Inicializando VentaController...");

        configurarColumnasProductos();
        configurarColumnasCarrito();

        // CREAR NUEVA VENTA
        crearNuevaVenta();

        cargarProductos();
        actualizarFecha();
        actualizarEstado("Sistema inicializado - Venta ID: " + ventaActual.getIdVenta());

        inicializarMetodosPago();


        // Listener para actualizar totales cuando cambie el carrito
        carritoData.addListener((javafx.collections.ListChangeListener<Producto>) change -> {
            actualizarTotales();
        });

        // Cargar clientes activos
        cargarClientes();

        // Listener del checkbox
        chkVentaConCliente.selectedProperty().addListener((obs, oldVal, newVal) -> {
            panelSeleccionCliente.setVisible(newVal);
            panelSeleccionCliente.setManaged(newVal);

            if (!newVal) {
                // Si desmarca, limpiar selección
                cbClientes.getSelectionModel().clearSelection();
                ventaActual.setCliente(null);
            }
        });

        // Listener de selección de cliente
        cbClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            ventaActual.setCliente(newVal);
            if (newVal != null) {
                System.out.println("Cliente seleccionado: " + newVal.getNombre());
            }
        });

        // Botón nuevo cliente rápido
        btnNuevoClienteRapido.setOnAction(e -> abrirDialogoNuevoClienteRapido());

    }

    public void setContenedorPrincipal(StackPane contenedor) {
        this.contenedorPrincipal = contenedor;
    }

    // ===== GESTIÓN DE VENTA =====

    /**
     * CONCEPTO: Crear nueva venta usando Stored Procedure
     * Cada sesión de trabajo necesita una venta "EN_PROCESO" donde agregar productos
     */
    private void crearNuevaVenta() {
        System.out.println("📝 Creando nueva venta...");

        // Crear objeto Venta
        ventaActual = new Venta();

        // Crear venta en BD y obtener ID
        int idGenerado = ventaDAO.crearVentaNueva();

        if (idGenerado == -1) {
            mostrarAlerta("Error Crítico", "No se pudo crear la venta. Revise la conexión a BD.");
            System.err.println("❌ Error: No se pudo crear venta");
        } else {
            ventaActual.setIdVenta(idGenerado);
            System.out.println("✅ Venta creada con ID: " + ventaActual.getIdVenta());

            // Limpiar carrito visual
            carritoData.clear();
            actualizarTotales();
        }
    }

    // ===== CONFIGURACIÓN DE COLUMNAS =====

    private void configurarColumnasProductos() {
        System.out.println("🏗️ Configurando columnas de productos...");

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Formato de precio con símbolo de pesos
        colPrecio.setCellFactory(column -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty || precio == null ? null : "$" + df.format(precio));
            }
        });

        configurarColumnaCantidadProductos();
        configurarColumnaAccionProductos();

        tablaProductos.setItems(productosData);
    }

    /**
     * CONCEPTO: CellFactory personalizada para Spinners
     * Cada fila tiene su propio Spinner independiente vinculado al modelo
     */
    private void configurarColumnaCantidadProductos() {
        colCantidad.setCellFactory(column -> new TableCell<Producto, Integer>() {
            private final Spinner<Integer> spinner = new Spinner<>(1, 50, 1);

            {
                spinner.setPrefWidth(80);
                spinner.setEditable(true);

                // BINDING: Vincular spinner con modelo
                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    Producto producto = getTableRow().getItem();
                    if (producto != null && newVal != null) {
                        producto.setCantidad(newVal);
                    }
                });
            }

            @Override
            protected void updateItem(Integer cantidad, boolean empty) {
                super.updateItem(cantidad, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Producto producto = getTableRow().getItem();

                    // Configurar límites dinámicos basados en stock
                    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                            (SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory();

                    int maxCantidad = producto.getTipoProducto().equals("FISICO") ?
                            Math.min(producto.getStock(), 50) : 50;
                    valueFactory.setMax(maxCantidad);

                    // Sincronizar valor con el modelo
                    if (producto.getCantidad() != spinner.getValue()) {
                        spinner.getValueFactory().setValue(
                                Math.max(1, Math.min(producto.getCantidad(), maxCantidad))
                        );
                    }

                    setGraphic(spinner);
                }
            }
        });
    }

    private void configurarColumnaAccionProductos() {
        colAccion.setCellFactory(column -> new TableCell<Producto, Void>() {
            private final Button btnAgregar = new Button("🛒 Agregar");

            {
                btnAgregar.setPrefWidth(100);
                btnAgregar.setOnAction(event -> {
                    Producto producto = getTableRow().getItem();
                    if (producto != null) {
                        agregarAlCarrito(producto);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Producto producto = getTableRow().getItem();

                    // Deshabilitar si no hay stock (solo para productos físicos)
                    boolean sinStock = producto.getTipoProducto().equals("FISICO") && producto.getStock() <= 0;
                    btnAgregar.setDisable(sinStock);
                    btnAgregar.setText(sinStock ? "Sin Stock" : "🛒 Agregar");

                    setGraphic(btnAgregar);
                }
            }
        });
    }

    private void configurarColumnasCarrito() {
        System.out.println("🛒 Configurando columnas de carrito...");

        colCarritoCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCarritoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCarritoPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCarritoCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        // Formato precio
        colCarritoPrecio.setCellFactory(column -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty || precio == null ? null : "$" + df.format(precio));
            }
        });

        // Subtotal calculado dinámicamente
        colCarritoSubtotal.setCellValueFactory(cellData -> {
            Producto producto = cellData.getValue();
            double subtotal = producto.getPrecio() * producto.getCantidad();
            return new javafx.beans.property.SimpleDoubleProperty(subtotal).asObject();
        });

        colCarritoSubtotal.setCellFactory(column -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                setText(empty || subtotal == null ? null : "$" + df.format(subtotal));
            }
        });

        configurarColumnaAccionesCarrito();
        tablaCarrito.setItems(carritoData);
    }

    private void configurarColumnaAccionesCarrito() {
        colCarritoAcciones.setCellFactory(column -> new TableCell<Producto, Void>() {
            private final HBox botones = new HBox(5);
            private final Button btnEditar = new Button("Editar");    // ← Cambio aquí
            private final Button btnEliminar = new Button("Eliminar");  // ← Cambio aquí

            {
                btnEditar.setPrefWidth(55);
                btnEditar.setPrefHeight(25);

                btnEliminar.setPrefWidth(55);
                btnEliminar.setPrefHeight(25);

                btnEditar.setStyle(
                        "-fx-background-color: #3498db; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 10px; " +
                                "-fx-font-weight: bold;"
                );

                btnEliminar.setStyle(
                        "-fx-background-color: #e74c3c; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 10px; " +
                                "-fx-font-weight: bold;"
                );

                btnEditar.setOnAction(event -> {
                    Producto producto = getTableRow().getItem();
                    if (producto != null) {
                        editarCantidadCarrito(producto);
                    }
                });

                btnEliminar.setOnAction(event -> {
                    Producto producto = getTableRow().getItem();
                    if (producto != null) {
                        eliminarDelCarrito(producto);
                    }
                });

                botones.getChildren().addAll(btnEditar, btnEliminar);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : botones);
            }
        });
    }

    // ===== LÓGICA DE CARRITO =====

    /**
     * CONCEPTO: Agregar producto usando Stored Procedure
     * La BD se encarga de validar stock y actualizar automáticamente
     */
    private void agregarAlCarrito(Producto producto) {
        if (ventaActual == null || ventaActual.getIdVenta() <= 0) {
            mostrarAlerta("Error", "No hay una venta activa");
            return;
        }

        System.out.println("➕ Agregando: " + producto.getNombre() + " x" + producto.getCantidad());

        // Usar stored procedure para agregar a BD
        boolean success = ventaDAO.agregarProducto(
                ventaActual.getIdVenta(),
                producto.getCodigo(),
                producto.getCantidad()
        );

        if (success) {
            // Recargar carrito desde BD
            recargarCarritoDesdeDB();

            // Resetear cantidad en la tabla productos
            producto.setCantidad(1);
            tablaProductos.refresh();

            actualizarEstado("✅ Producto agregado: " + producto.getNombre());
        } else {
            mostrarAlerta("Error", "No se pudo agregar el producto al carrito");
            actualizarEstado("❌ Error al agregar producto");
        }
    }

    /**
     * CONCEPTO: Sincronizar carrito visual con base de datos
     * La BD es la fuente de verdad, el JavaFX solo muestra los datos
     */
    private void recargarCarritoDesdeDB() {
        List<Producto> carritoActual = ventaDAO.obtenerCarrito(ventaActual.getIdVenta());

        // Actualizar modelo
        ventaActual.setProductos(carritoActual);

        // Actualizar UI (ObservableList)
        carritoData.setAll(carritoActual);

        System.out.println("🔄 Carrito recargado: " + carritoActual.size() + " items");
    }

    private void editarCantidadCarrito(Producto producto) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(producto.getCantidad()));
        dialog.setTitle("Editar Cantidad");
        dialog.setHeaderText("Producto: " + producto.getNombre());
        dialog.setContentText("Nueva cantidad:");

        dialog.showAndWait().ifPresent(resultado -> {
            try {
                int nuevaCantidad = Integer.parseInt(resultado);
                if (nuevaCantidad <= 0) {
                    mostrarAlerta("Cantidad Inválida", "La cantidad debe ser mayor a 0");
                    return;
                }

                // Usar stored procedure para modificar en BD
                boolean success = ventaDAO.modificarCantidad(
                        ventaActual.getIdVenta(),
                        producto.getCodigo(),
                        nuevaCantidad
                );

                if (success) {
                    recargarCarritoDesdeDB();
                    actualizarEstado("Cantidad actualizada: " + producto.getNombre());
                } else {
                    mostrarAlerta("Error", "No se pudo modificar la cantidad");
                }

            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Ingrese un número válido");
            }
        });
    }

    private void eliminarDelCarrito(Producto producto) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar producto del carrito?");
        confirmacion.setContentText(producto.getNombre() + " (x" + producto.getCantidad() + ")");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Usar stored procedure para eliminar de BD
                boolean success = ventaDAO.eliminarProducto(ventaActual.getIdVenta(), producto.getCodigo());

                if (success) {
                    recargarCarritoDesdeDB();
                    actualizarEstado("Producto eliminado: " + producto.getNombre());
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el producto");
                }
            }
        });
    }

    // ===== BÚSQUEDA Y CARGA DE PRODUCTOS =====

    @FXML
    private void buscarProducto() {
        String query = txtBuscar.getText().trim();
        System.out.println("🔍 Buscando: " + query);

        if (query.isEmpty()) {
            cargarProductos();
            return;
        }

        // Usar método mejorado que devuelve lista
        List<Producto> resultados = productoDAO.buscarPorCodigoONombre(query);

        if (!resultados.isEmpty()) {
            productosData.setAll(resultados);
            actualizarEstado("Encontrados " + resultados.size() + " productos");
            System.out.println("✅ Productos encontrados: " + resultados.size());
        } else {
            productosData.clear();
            mostrarAlerta("Búsqueda", "No se encontraron productos con: " + query);
            actualizarEstado("Sin resultados para: " + query);
        }
    }

    @FXML
    private void cargarProductos() {
        System.out.println("📦 Cargando productos..");
        try {
            List<Producto> productos = productoDAO.obtenerTodos();
            productosData.setAll(productos);
            txtBuscar.clear();
            actualizarEstado("Productos cargados: " + productos.size());
            System.out.println("✅ " + productos.size() + " productos cargados");

        } catch (Exception e) {
            mostrarAlerta("Error de Carga", "No se pudieron cargar los productos: " + e.getMessage());
            actualizarEstado("Error al cargar productos");
            e.printStackTrace();
        }
    }

    // ===== CÁLCULOS Y TOTALES =====

    private void actualizarTotales() {
        if (ventaActual == null || ventaActual.getIdVenta() <= 0) return;

        // Obtener total desde BD (más confiable)
        double totalBD = ventaDAO.obtenerTotal(ventaActual.getIdVenta());

        // Actualizar modelo
        ventaActual.setTotal(totalBD);
        ventaActual.calcularSubtotal();

        // Actualizar labels de UI
        lblTotal.setText("$" + df.format(ventaActual.getTotal()));
        lblTotalFinal.setText("$" + df.format(ventaActual.getTotal()));
        lblSubtotal.setText("$" + df.format(ventaActual.getSubtotal()));

        // Contadores
        int totalItems = ventaActual.getCantidadTotalItems();
        lblItemsCarrito.setText(String.valueOf(totalItems));
        lblTotalItems.setText(String.valueOf(totalItems));

        // Habilitar/deshabilitar botones
        btnSeleccionarPago.setDisable(ventaActual.estaVacio());
        btnLimpiarCarrito.setDisable(ventaActual.estaVacio());

        System.out.println("💰 Total actualizado: $" + df.format(ventaActual.getTotal()));
    }

    // ===== ACCIONES DE BOTONES =====

    @FXML
    private void limpiarCarrito() {
        if (carritoData.isEmpty()) {
            mostrarAlerta("Carrito Vacío", "No hay productos en el carrito");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Limpiar todo el carrito?");
        confirmacion.setContentText("Esta acción no se puede deshacer");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Usar stored procedure para limpiar carrito en BD
                boolean success = ventaDAO.limpiarCarrito(ventaActual.getIdVenta());

                if (success) {
                    carritoData.clear();
                    actualizarTotales();
                    actualizarEstado("Carrito limpiado");
                } else {
                    mostrarAlerta("Error", "No se pudo limpiar el carrito");
                }
            }
        });
    }

    @FXML
    private void irAPestanaProductos() {
        tabPane.getSelectionModel().select(0);
    }


    @FXML
    private void inicializarMetodosPago() {
        // Hardcodear los métodos (sin BD)
        cbMetodoPago.setItems(FXCollections.observableArrayList(
                "EFECTIVO", "TRANSFERENCIA", "TARJETA"
        ));

        // Mostrar botón confirmar cuando seleccione
        cbMetodoPago.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            btnConfirmarVenta.setVisible(newVal != null);
            btnConfirmarVenta.setManaged(newVal != null);
        });
    }

    @FXML
    private void mostrarMetodosPago() {
        if (carritoData.isEmpty()) {
            mostrarAlerta("Error", "No hay productos en el carrito");
            return;
        }

        // Mostrar sección de venta con cliente
        seccionVentaCliente.setVisible(true);
        seccionVentaCliente.setManaged(true);

        // Mostrar sección método de pago
        seccionMetodoPago.setVisible(true);
        seccionMetodoPago.setManaged(true);

        // Ocultar botón "Seleccionar Pago"
        btnSeleccionarPago.setVisible(false);
        btnSeleccionarPago.setManaged(false);

        actualizarEstado("Seleccione cliente (opcional) y método de pago");
    }

    @FXML
    private void limpiarMetodoPago() {
        cbMetodoPago.getSelectionModel().clearSelection();
        seccionMetodoPago.setVisible(false);
        seccionMetodoPago.setManaged(false);
        btnConfirmarVenta.setVisible(false);
        btnConfirmarVenta.setManaged(false);
        btnSeleccionarPago.setVisible(true);
        btnSeleccionarPago.setManaged(true);
    }


    @FXML
    private void confirmarVenta() {
        String metodo = cbMetodoPago.getSelectionModel().getSelectedItem();

        if (metodo == null) {
            mostrarAlerta("Error", "Debe seleccionar un método de pago");
            return;
        }

        // Actualizar modelo con cliente seleccionado
        if (chkVentaConCliente.isSelected() && cbClientes.getValue() != null) {
            ventaActual.setCliente(cbClientes.getValue());
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Venta");
        confirmacion.setHeaderText("¿Procesar la venta?");

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Método: ").append(metodo).append("\n");
        mensaje.append("Total: $").append(df.format(ventaActual.getTotal()));

        if (ventaActual.getCliente() != null) {
            mensaje.append("\n\nCliente: ").append(ventaActual.getNombreCliente());
        }

        confirmacion.setContentText(mensaje.toString());

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Mapear string a ID
                int idMetodo = metodo.equals("EFECTIVO") ? 1 :
                        metodo.equals("TRANSFERENCIA") ? 2 : 3;

                // Actualizar método de pago
                boolean metodoPagoOK = ventaDAO.actualizarMetodoPago(
                        ventaActual.getIdVenta(), idMetodo
                );

                if (!metodoPagoOK) {
                    mostrarAlerta("Error", "No se pudo actualizar el método de pago");
                    return;
                }

                // Asociar cliente si existe
                if (ventaActual.getCliente() != null) {
                    ventaDAO.asociarCliente(
                            ventaActual.getIdVenta(),
                            ventaActual.getCliente().getIdCliente()
                    );
                }

                // Completar venta
                boolean success = ventaDAO.completarVenta(ventaActual.getIdVenta());

                if (success) {
                    ventaActual.setEstado("COMPLETADA");
                    generarResumenVenta();

                    // Deshabilitar pestañas anteriores
                    tabPane.getTabs().get(0).setDisable(true);
                    tabPane.getTabs().get(1).setDisable(true);
                    tabPane.getTabs().get(2).setDisable(false);
                    tabPane.getSelectionModel().select(2);

                    limpiarMetodoPago();
                    limpiarVentaConCliente();

                    actualizarEstado("✅ Venta procesada exitosamente");
                } else {
                    mostrarAlerta("Error", "No se pudo completar la venta");
                }
            }
        });
    }

    private void procesarVentaFinal(MetodoDePago metodo) {
        System.out.println("Procesando venta con método: " + metodo);

        // Actualizar método de pago
        boolean metodoPagoOK = ventaDAO.actualizarMetodoPago(ventaActual.getIdVenta(), metodo.getIdMetodoDePago());

        if (!metodoPagoOK) {
            mostrarAlerta("Error", "No se pudo actualizar el método de pago");
            return;
        }

        // Si hay cliente seleccionado, asociarlo a la venta
        if (ventaActual.getCliente() != null) {
            boolean clienteOK = ventaDAO.asociarCliente(ventaActual.getIdVenta(), ventaActual.getCliente().getIdCliente());
            if (!clienteOK) {
                System.err.println("⚠️ No se pudo asociar cliente a la venta");
            } else {
                System.out.println("✅ Cliente asociado: " + ventaActual.getCliente().getNombre());
            }
        }

        // Completar venta
        boolean success = ventaDAO.completarVenta(ventaActual.getIdVenta());

        if (success) {
            generarResumenVenta();
            tabPane.getTabs().get(0).setDisable(true);
            tabPane.getTabs().get(1).setDisable(true);
            tabPane.getTabs().get(2).setDisable(false);
            tabPane.getSelectionModel().select(2);

            limpiarMetodoPago();
            limpiarVentaConCliente(); // Nuevo método

            actualizarEstado("Venta procesada exitosamente");
        } else {
            mostrarAlerta("Error", "No se pudo completar la venta");
        }
    }


    @FXML
    private void nuevaVenta() {
        carritoData.clear();
        txtResumenVenta.clear();

        tabPane.getTabs().get(0).setDisable(false);
        tabPane.getTabs().get(1).setDisable(false);
        tabPane.getTabs().get(2).setDisable(true);

        tabPane.getSelectionModel().select(0);

        limpiarMetodoPago();
        limpiarVentaConCliente();

        // Crear nueva venta (nuevo objeto)
        crearNuevaVenta();

        actualizarEstado("Lista para nueva venta - ID: " + ventaActual.getIdVenta());
    }
    // ===== GENERACIÓN DE RESUMEN =====

    private void generarResumenVenta() {
        System.out.println("📄 Generando resumen de venta...");

        StringBuilder resumen = new StringBuilder();
        resumen.append("=".repeat(50)).append("\n");
        resumen.append("           📚 LIBRERÍA PAPELITOS\n");
        resumen.append("=".repeat(50)).append("\n");
        resumen.append("Venta ID: ").append(ventaActual.getIdVenta()).append("\n");
        resumen.append("Comprobante: ").append(ventaActual.getNumeroComprobante()).append("\n");
        resumen.append("Fecha: ").append(ventaActual.getFecha().format(timeFormatter)).append("\n");
        resumen.append("Vendedor: ").append(ventaActual.getNombreVendedor()).append("\n");

        // INFO DE CLIENTE
        if (ventaActual.getCliente() != null) {
            resumen.append("-".repeat(50)).append("\n");
            resumen.append("CLIENTE: ").append(ventaActual.getNombreCliente()).append("\n");
            Cliente cliente = ventaActual.getCliente();
            if (cliente.getCuit() != null && !cliente.getCuit().isEmpty()) {
                resumen.append("CUIT: ").append(cliente.getCuit()).append("\n");
            }
            resumen.append("Tipo: ").append(cliente.getTipoCliente()).append("\n");
        }

        resumen.append("-".repeat(50)).append("\n");

        // Productos
        resumen.append(String.format("%-12s %-20s %8s %4s %10s\n",
                "CÓDIGO", "PRODUCTO", "PRECIO", "CANT", "SUBTOTAL"));
        resumen.append("-".repeat(50)).append("\n");

        for (Producto producto : ventaActual.getProductos()) {
            double subtotalProducto = producto.getPrecio() * producto.getCantidad();

            String nombreCorto = producto.getNombre().length() > 20 ?
                    producto.getNombre().substring(0, 17) + "..." : producto.getNombre();

            resumen.append(String.format("%-12s %-20s $%7.2f %4d $%9.2f\n",
                    producto.getCodigo(),
                    nombreCorto,
                    producto.getPrecio(),
                    producto.getCantidad(),
                    subtotalProducto
            ));
        }

        resumen.append("-".repeat(50)).append("\n");
        resumen.append(String.format("%-40s $%9.2f\n", "TOTAL:", ventaActual.getTotal()));
        resumen.append("=".repeat(50)).append("\n\n");
        resumen.append("¡Gracias por su compra!\n");

        if (ventaActual.getCliente() != null) {
            resumen.append("Cliente: ").append(ventaActual.getNombreCliente()).append("\n");
        }

        resumen.append("Sistema desarrollado con JavaFX + MySQL\n");

        txtResumenVenta.setText(resumen.toString());
        System.out.println("✅ Resumen generado correctamente");
    }

    private void cargarClientes() {
        List<Cliente> clientes = clienteDAO.obtenerActivos();
        cbClientes.setItems(FXCollections.observableArrayList(clientes));
        System.out.println("Clientes activos cargados: " + clientes.size());
    }

    // 6. DIALOG PARA CREAR CLIENTE RÁPIDO (versión simplificada)
    private void abrirDialogoNuevoClienteRapido() {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Cliente");
        dialog.setHeaderText("Crear cliente rápidamente");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Tipo de cliente
        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.setItems(FXCollections.observableArrayList("PERSONA", "EMPRESA"));
        cbTipo.setValue("EMPRESA");

        // Nombre
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Razón Social");

        // CUIT con validación y formato automático
        TextField txtCuit = new TextField();
        txtCuit.setPromptText("XX-XXXXXXXX-X");

        // Formateo automático del CUIT
        txtCuit.textProperty().addListener((obs, oldVal, newVal) -> {
            String numeros = newVal.replaceAll("[^0-9]", "");
            if (numeros.length() > 11) {
                numeros = numeros.substring(0, 11);
            }

            String formateado = "";
            if (numeros.length() > 0) {
                formateado = numeros.substring(0, Math.min(2, numeros.length()));
                if (numeros.length() > 2) {
                    formateado += "-" + numeros.substring(2, Math.min(10, numeros.length()));
                }
                if (numeros.length() > 10) {
                    formateado += "-" + numeros.substring(10);
                }
            }

            if (!txtCuit.getText().equals(formateado)) {
                txtCuit.setText(formateado);
                txtCuit.positionCaret(formateado.length());
            }
        });

        // Condición IVA
        ComboBox<String> cbCondicionIva = new ComboBox<>();
        cbCondicionIva.setItems(FXCollections.observableArrayList(
                "RESPONSABLE_INSCRIPTO", "MONOTRIBUTO", "EXENTO", "CONSUMIDOR_FINAL"
        ));
        cbCondicionIva.setValue("CONSUMIDOR_FINAL");

        // Teléfono con validación
        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("011-1234-5678");

        txtTelefono.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtrado = newVal.replaceAll("[^0-9\\-\\(\\)\\s]", "");
            if (!txtTelefono.getText().equals(filtrado)) {
                txtTelefono.setText(filtrado);
                txtTelefono.positionCaret(filtrado.length());
            }
        });

        // Cambiar placeholder según tipo
        cbTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("EMPRESA".equals(newVal)) {
                txtNombre.setPromptText("Razón Social");
                txtCuit.setPromptText("CUIT (XX-XXXXXXXX-X)");
            } else {
                txtNombre.setPromptText("Nombre Completo");
                txtCuit.setPromptText("CUIT (opcional)");
            }
        });

        // Agregar al grid
        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(cbTipo, 1, 0);
        grid.add(new Label("Nombre: *"), 0, 1);
        grid.add(txtNombre, 1, 1);
        grid.add(new Label("CUIT:"), 0, 2);
        grid.add(txtCuit, 1, 2);
        grid.add(new Label("Condición IVA:"), 0, 3);
        grid.add(cbCondicionIva, 1, 3);
        grid.add(new Label("Teléfono:"), 0, 4);
        grid.add(txtTelefono, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Validación al intentar guardar
        dialog.getDialogPane().lookupButton(btnGuardar).addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {
                    String error = validarCamposCliente(
                            cbTipo.getValue(),
                            txtNombre.getText(),
                            txtCuit.getText()
                    );

                    if (error != null) {
                        event.consume(); // NO cerrar dialog
                        mostrarAlerta("Error de Validación", error);
                    }
                }
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                Cliente cliente = new Cliente();
                cliente.setTipoCliente(cbTipo.getValue());
                cliente.setNombre(txtNombre.getText().trim());
                cliente.setCuit(txtCuit.getText().trim());
                cliente.setCondicionIva(cbCondicionIva.getValue());
                cliente.setTelefono(txtTelefono.getText().trim());
                return cliente;
            }
            return null;
        });

        // Mostrar y procesar resultado
        Optional<Cliente> resultado = dialog.showAndWait();
        resultado.ifPresent(cliente -> {
            if (clienteDAO.crear(cliente)) {
                mostrarAlerta("Éxito","Cliente creado exitosamente");
                cargarClientes(); // Recargar lista
                cbClientes.getSelectionModel().select(cliente); // Seleccionar el nuevo
            } else {
                mostrarAlerta("Error", "No se pudo crear el cliente");
            }
        });
    }

    private String validarCamposCliente(String tipo, String nombre, String cuit) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre es obligatorio";
        }

        if ("EMPRESA".equals(tipo)) {
            if (cuit == null || cuit.trim().isEmpty()) {
                return "El CUIT es obligatorio para empresas";
            }

            if (!cuit.matches("\\d{2}-\\d{8}-\\d")) {
                return "CUIT inválido. Formato correcto: XX-XXXXXXXX-X";
            }
        }

        if ("PERSONA".equals(tipo) && cuit != null && !cuit.trim().isEmpty()) {
            if (!cuit.matches("\\d{2}-\\d{8}-\\d")) {
                return "CUIT inválido. Formato correcto: XX-XXXXXXXX-X";
            }
        }

        return null;
    }

    private void limpiarVentaConCliente() {
        chkVentaConCliente.setSelected(false);
        cbClientes.getSelectionModel().clearSelection();
        ventaActual.setCliente(null);
        panelSeleccionCliente.setVisible(false);
        panelSeleccionCliente.setManaged(false);
        seccionVentaCliente.setVisible(false);
        seccionVentaCliente.setManaged(false);
    }

    @FXML
    private void volverHistorial() {
        // Verificar si hay venta en proceso sin completar
        if (ventaActual != null && !ventaActual.estaVacio() &&
                ventaActual.getEstado().equals("EN_PROCESO")) {

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Venta en Proceso");
            confirmacion.setHeaderText("Hay una venta sin completar");
            confirmacion.setContentText("¿Desea cancelar la venta actual y volver al historial?");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Anular venta en proceso
                    ventaDAO.anularVenta(ventaActual.getIdVenta());
                    abrirHistorial();
                }
            });
        } else {
            // No hay venta o ya está completada
            abrirHistorial();
        }
    }

    private void abrirHistorial() {
        try {
            System.out.println("📊 Abriendo historial de ventas...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/libreria/view/historial-ventas.fxml"));
            javafx.scene.Node contenido = loader.load();

            // Obtener controller y pasarle la referencia también
            HistorialVentasController controller = loader.getController();
            controller.setContenedorPrincipal(contenedorPrincipal);

            // ✅ CAMBIAR SOLO EL CONTENIDO, NO LA SCENE
            contenedorPrincipal.getChildren().clear();
            contenedorPrincipal.getChildren().add(contenido);

            System.out.println("✅ Historial cargado");

        } catch (IOException e) {
            System.err.println("Error al cargar historial-ventas.fxml: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el historial");
        }
    }


    // ===== UTILIDADES =====

    private void actualizarEstado(String mensaje) {
        lblEstado.setText("📱 " + mensaje);
        System.out.println("ℹ️  " + mensaje);
    }

    private void actualizarFecha() {
        lblFecha.setText(LocalDateTime.now().format(timeFormatter));

        // Actualizar cada minuto
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.minutes(1),
                        e -> lblFecha.setText(LocalDateTime.now().format(timeFormatter))
                )
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ===== GETTERS PARA USO EXTERNO =====

    public ObservableList<Producto> getCarritoData() {
        return carritoData;
    }

    public Venta getVentaActual() {
        return ventaActual;
    }

    public double getTotal() {
        return ventaActual != null ? ventaActual.getTotal() : 0.0;
    }

    public int getVentaActualId() {
        return ventaActual != null ? ventaActual.getIdVenta() : -1;
    }

    public void cleanup() {
        if (ventaActual != null && ventaActual.getIdVenta() > 0) {
            String estado = ventaDAO.obtenerEstadoVenta(ventaActual.getIdVenta());
            if ("EN_PROCESO".equals(estado)) {
                System.out.println("🧹 Limpiando venta en proceso al cerrar aplicación");
                ventaDAO.anularVenta(ventaActual.getIdVenta());
            }
        }
    }
}