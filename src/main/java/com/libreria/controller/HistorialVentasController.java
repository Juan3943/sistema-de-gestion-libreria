package com.libreria.controller;

import com.libreria.dao.ClienteDAO;
import com.libreria.dao.VentaDAO;
import com.libreria.model.Cliente;
import com.libreria.model.Producto;
import com.libreria.model.Venta;
import com.libreria.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * CONCEPTO: Controller para el Historial de Ventas
 * Responsabilidades:
 * - Mostrar ventas completadas/anuladas con paginación
 * - Filtrar ventas por fecha, cliente, estado
 * - Ver detalles de una venta
 * - Anular ventas (solo ADMIN)
 * - Navegar a proceso de nueva venta
 */
public class HistorialVentasController {

    // ===== ELEMENTOS DE LA INTERFAZ =====

    // Tabla
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, String> colNumero;
    @FXML private TableColumn<Venta, LocalDateTime> colFecha;
    @FXML private TableColumn<Venta, String> colCliente;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private TableColumn<Venta, String> colEstado;
    @FXML private TableColumn<Venta, String> colVendedor;
    @FXML private TableColumn<Venta, Void> colAcciones;

    // Filtros
    @FXML private TextField txtBuscar;
    @FXML private DatePicker dpFechaDesde;
    @FXML private DatePicker dpFechaHasta;
    @FXML private ComboBox<Cliente> cbClientes;
    @FXML private ComboBox<String> cbEstado;

    // Botones
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiarFiltros;
    @FXML private Button btnVolverCarrito;

    // Paginación
    @FXML private Label lblInfoPaginacion;
    @FXML private Label lblPaginaActual;
    @FXML private Button btnPrimera;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnUltima;

    // Estado
    @FXML private Label lblEstado;

    // ===== DATOS Y CONFIGURACIÓN =====

    private final VentaDAO ventaDAO = new VentaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private StackPane contenedorPrincipal;
    // Paginación
    private static final int REGISTROS_POR_PAGINA = 20;
    private int paginaActual = 1;
    private int totalPaginas = 0;
    private int totalRegistros = 0;

    // ===== INICIALIZACIÓN =====

    @FXML
    public void initialize() {
        System.out.println("📊 Inicializando HistorialVentasController...");

        // Limpiar ventas basura al inicio
        limpiarVentasAbandonadas();

        configurarTabla();
        inicializarFiltros();
        cargarClientes();
        cargarVentas();

        actualizarEstado("Sistema listo - Historial de ventas");
    }

    public void setContenedorPrincipal(StackPane contenedor) {
        this.contenedorPrincipal = contenedor;
    }

    /**
     * CONCEPTO: Limpiar ventas EN_PROCESO antiguas
     * Se ejecuta al abrir el historial para mantener la BD limpia
     */
    private void limpiarVentasAbandonadas() {
        // Eliminar ventas en proceso con más de 1 día de antigüedad
        int eliminadas = ventaDAO.limpiarVentasEnProceso(1);
        if (eliminadas > 0) {
            System.out.println("🧹 Se eliminaron " + eliminadas + " ventas abandonadas");
        }
    }

    // ===== CONFIGURACIÓN DE TABLA =====

    /**
     * CONCEPTO: Configurar columnas de la tabla
     * PropertyValueFactory vincula automáticamente con los getters del modelo
     */
    private void configurarTabla() {
        System.out.println("🗂️ Configurando tabla de ventas...");

        // Vincular columnas con propiedades del modelo
        colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroComprobante"));
        colCliente.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getNombreCliente()
                )
        );
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colVendedor.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getNombreVendedor()
                )
        );

        // Formato de fecha
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setCellFactory(column -> new TableCell<Venta, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(fecha.format(dateFormatter));
                }
            }
        });

        // Formato de precio
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setCellFactory(column -> new TableCell<Venta, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                setText(empty || total == null ? null : "$" + df.format(total));
            }
        });

        // Columna de estado con colores
        colEstado.setCellFactory(column -> new TableCell<Venta, String>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    if (estado.equals("COMPLETADA")) {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    } else if (estado.equals("ANULADA")) {
                        setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                    }
                }
            }
        });

        configurarColumnaAcciones();
    }

    /**
     * CONCEPTO: Columna de acciones con MenuButton
     * Cada fila tiene un menú contextual con Ver Detalle y Anular
     */
    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(column -> new TableCell<Venta, Void>() {

            private final MenuButton btnAcciones = new MenuButton("⋮");
            private final MenuItem itemVerDetalle = new MenuItem("👁️ Ver Detalle");
            private final MenuItem itemAnular = new MenuItem("❌ Anular");

            {
                // Estilo del botón
                btnAcciones.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-font-size: 18px; " +
                                "-fx-padding: 0;"
                );

                // Acción Ver Detalle (todos los usuarios)
                itemVerDetalle.setOnAction(event -> {
                    Venta venta = getTableRow().getItem();
                    if (venta != null) {
                        mostrarDetalleVenta(venta);
                    }
                });

                // Acción Anular (solo ADMIN)
                itemAnular.setOnAction(event -> {
                    Venta venta = getTableRow().getItem();
                    if (venta != null) {
                        confirmarAnulacion(venta);
                    }
                });

                // Agregar items al menú
                btnAcciones.getItems().addAll(itemVerDetalle, itemAnular);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Venta venta = getTableRow().getItem();

                    // Configurar visibilidad según estado y rol
                    boolean esAdmin = esUsuarioAdmin();
                    boolean estaCompletada = venta.getEstado().equals("COMPLETADA");

                    // Solo mostrar "Anular" si es ADMIN y la venta está COMPLETADA
                    itemAnular.setVisible(esAdmin && estaCompletada);

                    // Si está anulada, deshabilitar el botón
                    if (venta.getEstado().equals("ANULADA")) {
                        btnAcciones.setDisable(true);
                        btnAcciones.setText("✓");
                    } else {
                        btnAcciones.setDisable(false);
                        btnAcciones.setText("⋮");
                    }

                    setGraphic(btnAcciones);
                }
            }
        });
    }

    // ===== INICIALIZACIÓN DE FILTROS =====

    private void inicializarFiltros() {
        // Estados disponibles
        cbEstado.setItems(FXCollections.observableArrayList(
                "TODOS", "COMPLETADA", "ANULADA"
        ));
        cbEstado.setValue("TODOS");

        // Configurar DatePickers (opcional: establecer rango predeterminado)
        // dpFechaDesde.setValue(LocalDate.now().minusMonths(1));
        // dpFechaHasta.setValue(LocalDate.now());
    }

    private void cargarClientes() {
        List<Cliente> clientes = clienteDAO.obtenerActivos();

        // Agregar opción "Todos"
        Cliente todos = new Cliente();
        todos.setIdCliente(-1);
        todos.setNombre("Todos (con y sin cliente)");
        clientes.add(0, todos);

        cbClientes.setItems(FXCollections.observableArrayList(clientes));
        cbClientes.setValue(todos);

        System.out.println("👥 Clientes cargados para filtro: " + clientes.size());
    }

    // ===== CARGA DE VENTAS CON PAGINACIÓN =====

    /**
     * CONCEPTO: Cargar ventas usando paginación
     * Solo carga 20 registros por página para mejor performance
     */
    private void cargarVentas() {
        System.out.println("📄 Cargando página " + paginaActual + "...");

        // Obtener valores de filtros
        String busqueda = txtBuscar.getText().trim();
        LocalDate fechaDesde = dpFechaDesde.getValue();
        LocalDate fechaHasta = dpFechaHasta.getValue();

        Integer idCliente = null;
        if (cbClientes.getValue() != null && cbClientes.getValue().getIdCliente() != -1) {
            idCliente = cbClientes.getValue().getIdCliente();
        }

        String estado = cbEstado.getValue();

        // Llamar al DAO con paginación
        VentaDAO.ResultadoPaginado resultado = ventaDAO.obtenerVentasPaginadas(
                paginaActual,
                REGISTROS_POR_PAGINA,
                busqueda,
                fechaDesde,
                fechaHasta,
                idCliente,
                estado
        );

        // Actualizar tabla
        tablaVentas.setItems(FXCollections.observableArrayList(resultado.ventas));

        // Actualizar info de paginación
        totalRegistros = resultado.totalRegistros;
        totalPaginas = (int) Math.ceil((double) totalRegistros / REGISTROS_POR_PAGINA);

        actualizarControlesPaginacion();

        actualizarEstado("Mostrando " + resultado.ventas.size() + " ventas");
    }

    /**
     * CONCEPTO: Actualizar controles de paginación
     * Muestra info y habilita/deshabilita botones según corresponda
     */
    private void actualizarControlesPaginacion() {
        // Texto informativo
        int desde = totalRegistros > 0 ? (paginaActual - 1) * REGISTROS_POR_PAGINA + 1 : 0;
        int hasta = Math.min(paginaActual * REGISTROS_POR_PAGINA, totalRegistros);

        lblInfoPaginacion.setText(
                String.format("Mostrando %d-%d de %d ventas", desde, hasta, totalRegistros)
        );

        lblPaginaActual.setText(
                String.format("Página %d de %d", paginaActual, Math.max(1, totalPaginas))
        );

        // Habilitar/deshabilitar botones
        btnPrimera.setDisable(paginaActual == 1);
        btnAnterior.setDisable(paginaActual == 1);
        btnSiguiente.setDisable(paginaActual >= totalPaginas || totalPaginas == 0);
        btnUltima.setDisable(paginaActual >= totalPaginas || totalPaginas == 0);
    }

    // ===== ACCIONES DE FILTROS =====

    @FXML
    private void aplicarFiltros() {
        paginaActual = 1; // Volver a la primera página al filtrar
        cargarVentas();
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        dpFechaDesde.setValue(null);
        dpFechaHasta.setValue(null);
        cbClientes.getSelectionModel().selectFirst();
        cbEstado.setValue("TODOS");

        paginaActual = 1;
        cargarVentas();

        actualizarEstado("Filtros limpiados");
    }

    // ===== NAVEGACIÓN DE PÁGINAS =====

    @FXML
    private void irPrimera() {
        paginaActual = 1;
        cargarVentas();
    }

    @FXML
    private void irAnterior() {
        if (paginaActual > 1) {
            paginaActual--;
            cargarVentas();
        }
    }

    @FXML
    private void irSiguiente() {
        if (paginaActual < totalPaginas) {
            paginaActual++;
            cargarVentas();
        }
    }

    @FXML
    private void irUltima() {
        paginaActual = totalPaginas;
        cargarVentas();
    }

    // ===== ACCIONES DE VENTAS =====

    /**
     * CONCEPTO: Mostrar detalle completo de una venta
     * Abre un diálogo con todos los productos y datos de la venta
     */
    private void mostrarDetalleVenta(Venta venta) {
        System.out.println("👁️ Mostrando detalle de venta ID: " + venta.getIdVenta());

        // Obtener venta completa con productos
        VentaDAO.VentaCompleta ventaCompleta = ventaDAO.obtenerVentaCompleta(venta.getIdVenta());

        if (ventaCompleta == null) {
            mostrarAlerta("Error", "No se pudo cargar el detalle de la venta");
            return;
        }

        // Crear diálogo
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalle de Venta");
        dialog.setHeaderText("Venta #" + ventaCompleta.numeroComprobante);

        // Contenido
        VBox contenido = new VBox(10);
        contenido.setStyle("-fx-padding: 15;");

        // Información de cabecera
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);

        int row = 0;
        grid.add(new Label("Fecha:"), 0, row);
        grid.add(new Label(ventaCompleta.fecha.toLocalDateTime().format(dateFormatter)), 1, row++);

        grid.add(new Label("Cliente:"), 0, row);
        grid.add(new Label(ventaCompleta.cliente != null ? ventaCompleta.cliente : "Sin cliente"), 1, row++);

        grid.add(new Label("Vendedor:"), 0, row);
        grid.add(new Label(ventaCompleta.vendedor), 1, row++);

        grid.add(new Label("Estado:"), 0, row);
        Label lblEstadoVenta = new Label(venta.getEstado());
        if (venta.getEstado().equals("COMPLETADA")) {
            lblEstadoVenta.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        } else {
            lblEstadoVenta.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        }
        grid.add(lblEstadoVenta, 1, row++);

        contenido.getChildren().add(grid);

        // Separador
        contenido.getChildren().add(new Separator());

        // Productos
        Label lblProductos = new Label("Productos:");
        lblProductos.setStyle("-fx-font-weight: bold;");
        contenido.getChildren().add(lblProductos);

        TableView<VentaDAO.DetalleVenta> tablaDetalle = new TableView<>();

        TableColumn<VentaDAO.DetalleVenta, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCodigo.setPrefWidth(100);

        TableColumn<VentaDAO.DetalleVenta, String> colProducto = new TableColumn<>("Producto");
        colProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        colProducto.setPrefWidth(200);

        TableColumn<VentaDAO.DetalleVenta, Integer> colCantidad = new TableColumn<>("Cant.");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setPrefWidth(60);

        TableColumn<VentaDAO.DetalleVenta, Double> colPrecio = new TableColumn<>("Precio Unit.");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecio.setPrefWidth(100);
        colPrecio.setCellFactory(column -> new TableCell<VentaDAO.DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty || precio == null ? null : "$" + df.format(precio));
            }
        });

        TableColumn<VentaDAO.DetalleVenta, Double> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colSubtotal.setPrefWidth(100);
        colSubtotal.setCellFactory(column -> new TableCell<VentaDAO.DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                setText(empty || subtotal == null ? null : "$" + df.format(subtotal));
            }
        });

        tablaDetalle.getColumns().addAll(colCodigo, colProducto, colCantidad, colPrecio, colSubtotal);
        tablaDetalle.setItems(FXCollections.observableArrayList(ventaCompleta.detalles));
        tablaDetalle.setPrefHeight(250);

        contenido.getChildren().add(tablaDetalle);

        // Totales
        contenido.getChildren().add(new Separator());

        GridPane gridTotales = new GridPane();
        gridTotales.setHgap(15);
        gridTotales.setVgap(5);

        Label lblSubtotalTxt = new Label("Subtotal:");
        lblSubtotalTxt.setStyle("-fx-font-size: 14px;");
        Label lblSubtotalVal = new Label("$" + df.format(ventaCompleta.subtotal));
        lblSubtotalVal.setStyle("-fx-font-size: 14px;");

        Label lblTotalTxt = new Label("TOTAL:");
        lblTotalTxt.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label lblTotalVal = new Label("$" + df.format(ventaCompleta.total));
        lblTotalVal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        gridTotales.add(lblSubtotalTxt, 0, 0);
        gridTotales.add(lblSubtotalVal, 1, 0);
        gridTotales.add(lblTotalTxt, 0, 1);
        gridTotales.add(lblTotalVal, 1, 1);

        contenido.getChildren().add(gridTotales);

        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefWidth(650);

        dialog.showAndWait();
    }

    /**
     * CONCEPTO: Confirmar y anular una venta (solo ADMIN)
     * Restaura el stock de productos físicos automáticamente
     */
    private void confirmarAnulacion(Venta venta) {
        // Verificar permisos
        if (!esUsuarioAdmin()) {
            mostrarAlerta("Acceso Denegado",
                    "Solo administradores pueden anular ventas");
            return;
        }

        // Verificar estado
        if (!venta.getEstado().equals("COMPLETADA")) {
            mostrarAlerta("Error", "Solo se pueden anular ventas completadas");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Anulación");
        confirmacion.setHeaderText("¿Anular venta #" + venta.getNumeroComprobante() + "?");
        confirmacion.setContentText(
                "Cliente: " + venta.getNombreCliente() + "\n" +
                        "Total: $" + df.format(venta.getTotal()) + "\n\n" +
                        "Esta acción restaurará el stock de productos físicos.\n" +
                        "¿Continuar?"
        );

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                anularVenta(venta);
            }
        });
    }

    private void anularVenta(Venta venta) {
        System.out.println("❌ Anulando venta ID: " + venta.getIdVenta());

        boolean success = ventaDAO.anularVenta(venta.getIdVenta());

        if (success) {
            mostrarAlerta("Éxito", "Venta anulada correctamente");
            cargarVentas(); // Recargar tabla
            actualizarEstado("✅ Venta #" + venta.getNumeroComprobante() + " anulada");
        } else {
            mostrarAlerta("Error", "No se pudo anular la venta");
            actualizarEstado("❌ Error al anular venta");
        }
    }

    // ===== NAVEGACIÓN =====

    /**
     * CONCEPTO: Abrir ventana de Nueva Venta (carrito)
     * Carga el FXML del proceso de venta
     */
    @FXML
    private void abrirNuevaVenta() {
        try {
            System.out.println("🛒 Abriendo proceso de nueva venta...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/libreria/view/carrito-view.fxml"));
            javafx.scene.Node contenido = loader.load();

            // Obtener controller y pasarle la referencia
            VentaController controller = loader.getController();
            controller.setContenedorPrincipal(contenedorPrincipal);

            // ✅ CAMBIAR SOLO EL CONTENIDO
            contenedorPrincipal.getChildren().clear();
            contenedorPrincipal.getChildren().add(contenido);

            System.out.println("✅ Carrito cargado");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar carrito-view.fxml: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el módulo de ventas");
        }
    }

    // ===== UTILIDADES =====

    /**
     * CONCEPTO: Verificar si el usuario actual es administrador
     * Usa el SesionManager para obtener el usuario logueado
     */
    private boolean esUsuarioAdmin() {
        // Obtener usuario actual de la sesión
        var usuarioActual = SessionManager.getUsuarioActual();
        return usuarioActual != null &&
                usuarioActual.esAdmin();
    }

    private void actualizarEstado(String mensaje) {
        lblEstado.setText("📊 " + mensaje);
        System.out.println("ℹ️ " + mensaje);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}