package com.libreria.controller;

import com.libreria.dao.CompraDAO;
import com.libreria.dao.ProductoDAO;
import com.libreria.dao.ProveedorDAO;
import com.libreria.dao.MetodoDePagoDAO;
import com.libreria.model.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.beans.property.SimpleStringProperty;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class CompraController {

    // FXML Components
    @FXML private TabPane tabPane;
    @FXML private Tab tabPendientes;
    @FXML private Tab tabCompletadas;
    @FXML private Tab tabCanceladas;
    @FXML private Tab tabTodas;

    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, String> colNumero;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colProveedor;
    @FXML private TableColumn<Compra, String> colTotal;
    @FXML private TableColumn<Compra, String> colEstado;
    @FXML private TableColumn<Compra, Void> colAcciones;

    @FXML private ComboBox<Proveedor> cbFiltroProveedor;
    @FXML private DatePicker dpFechaDesde;
    @FXML private DatePicker dpFechaHasta;
    @FXML private Button btnLimpiarFiltros;
    @FXML private Button btnNuevaCompra;

    // DAOs
    private final CompraDAO compraDAO = new CompraDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final MetodoDePagoDAO metodoPagoDAO = new MetodoDePagoDAO();

    // Datos
    private ObservableList<Compra> comprasData = FXCollections.observableArrayList();

    // Formateadores
    private final NumberFormat formatoPrecio = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));

    @FXML
    public void initialize() {
        System.out.println("📦 Inicializando CompraController...");

        configurarTabla();
        configurarTabs();
        cargarFiltros();
        cargarCompras();

        System.out.println("✅ CompraController inicializado");
    }

    /**
     * CONCEPTO: Configurar columnas de la tabla
     */
    private void configurarTabla() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroCompra"));
        colNumero.setPrefWidth(100);

        colFecha.setCellValueFactory(cellData -> {
            String fecha = cellData.getValue().getFechaCompraFormateada();
            if (fecha.contains(" ")) {
                fecha = fecha.split(" ")[0];
            }
            return new SimpleStringProperty(fecha);
        });
        colFecha.setPrefWidth(100);

        colProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        colProveedor.setPrefWidth(200);

        colTotal.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatoPrecio.format(cellData.getValue().getTotal())));
        colTotal.setPrefWidth(120);
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");

        colEstado.setCellValueFactory(cellData -> {
            Compra compra = cellData.getValue();
            return new SimpleStringProperty(compra.getIconoEstado() + " " + compra.getEstado());
        });
        colEstado.setPrefWidth(120);
        colEstado.setStyle("-fx-alignment: CENTER;");

        colEstado.setCellFactory(column -> new TableCell<Compra, String>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);

                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    Compra compra = getTableRow().getItem();
                    if (compra != null) {
                        setStyle("-fx-text-fill: " + compra.getColorEstado() + "; -fx-font-weight: bold;");
                    }
                }
            }
        });

        configurarColumnaAcciones();
        tablaCompras.setItems(comprasData);

        VBox placeholder = new VBox(15);
        placeholder.setAlignment(Pos.CENTER);
        Label lblIcono = new Label("📦");
        lblIcono.setStyle("-fx-font-size: 48px;");
        Label lblTexto1 = new Label("No hay compras para mostrar");
        lblTexto1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label lblTexto2 = new Label("Use el botón 'Nueva Compra' para registrar una compra");
        lblTexto2.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
        placeholder.getChildren().addAll(lblIcono, lblTexto1, lblTexto2);
        tablaCompras.setPlaceholder(placeholder);
    }

    private void configurarColumnaAcciones() {
        colAcciones.setPrefWidth(40);
        colAcciones.setCellFactory(column -> new TableCell<Compra, Void>() {
            private final Button btnOpciones = new Button("⋮");
            private final ContextMenu menuOpciones = new ContextMenu();

            {
                btnOpciones.setPrefSize(25, 25);
                btnOpciones.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-text-fill: #7f8c8d; " +
                                "-fx-font-size: 18px; " +
                                "-fx-cursor: hand; " +
                                "-fx-padding: 0;"
                );

                btnOpciones.setOnMouseEntered(e -> {
                    btnOpciones.setStyle(
                            "-fx-background-color: #ecf0f1; " +
                                    "-fx-text-fill: #2c3e50; " +
                                    "-fx-font-size: 18px; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-padding: 0; " +
                                    "-fx-background-radius: 3;"
                    );
                });

                btnOpciones.setOnMouseExited(e -> {
                    btnOpciones.setStyle(
                            "-fx-background-color: transparent; " +
                                    "-fx-text-fill: #7f8c8d; " +
                                    "-fx-font-size: 18px; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-padding: 0;"
                    );
                });

                btnOpciones.setOnAction(event -> {
                    Compra compra = getTableRow().getItem();
                    if (compra != null) {
                        construirMenuContextual(compra);
                        javafx.geometry.Bounds bounds = btnOpciones.localToScreen(btnOpciones.getBoundsInLocal());
                        menuOpciones.show(btnOpciones, bounds.getMinX(), bounds.getMaxY());
                    }
                });
            }

            private void construirMenuContextual(Compra compra) {
                menuOpciones.getItems().clear();

                MenuItem itemDetalle = new MenuItem("ℹ️ Ver detalle");
                itemDetalle.setStyle("-fx-font-size: 12px; -fx-padding: 6 12;");
                itemDetalle.setOnAction(e -> verDetalleCompra(compra));

                if (compra.isPendiente()) {
                    MenuItem itemEditar = new MenuItem("✏️ Editar compra");
                    MenuItem itemCompletar = new MenuItem("✅ Completar");
                    MenuItem itemCancelar = new MenuItem("❌ Cancelar");

                    itemEditar.setStyle("-fx-font-size: 12px; -fx-padding: 6 12;");
                    itemCompletar.setStyle("-fx-font-size: 12px; -fx-padding: 6 12;");
                    itemCancelar.setStyle("-fx-font-size: 12px; -fx-padding: 6 12;");

                    itemEditar.setOnAction(e -> editarCompra(compra));
                    itemCompletar.setOnAction(e -> completarCompra(compra));
                    itemCancelar.setOnAction(e -> cancelarCompra(compra));

                    menuOpciones.getItems().addAll(itemDetalle, new SeparatorMenuItem(),
                            itemEditar, itemCompletar, itemCancelar);

                } else if (compra.isCompletada()) {
                    MenuItem itemAnular = new MenuItem("🗑️ Anular compra");
                    itemAnular.setStyle("-fx-font-size: 12px; -fx-padding: 6 12;");
                    itemAnular.setOnAction(e -> anularCompra(compra));

                    menuOpciones.getItems().addAll(itemDetalle, new SeparatorMenuItem(), itemAnular);

                } else {
                    menuOpciones.getItems().add(itemDetalle);
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnOpciones);
            }
        });
    }

    private void configurarTabs() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            // ✅ NO limpiar filtros al cambiar de tab
            // Los filtros ahora persisten entre tabs

            // Aplicar filtros según tab seleccionado
            aplicarFiltrosConEstado();
        });

        actualizarContadoresTabs();
    }

    private void aplicarFiltrosConEstado() {
        Tab tabActivo = tabPane.getSelectionModel().getSelectedItem();
        String estadoFiltro = null;

        if (tabActivo == tabPendientes) {
            estadoFiltro = "PENDIENTE";
        } else if (tabActivo == tabCompletadas) {
            estadoFiltro = "COMPLETADA";
        } else if (tabActivo == tabCanceladas) {
            estadoFiltro = "CANCELADA";
        }
        // Si es tabTodas, estadoFiltro queda null

        // Obtener valores de filtros
        Proveedor proveedorSeleccionado = cbFiltroProveedor.getValue();
        java.time.LocalDate fechaDesde = dpFechaDesde.getValue();
        java.time.LocalDate fechaHasta = dpFechaHasta.getValue();

        // Cargar compras según estado
        List<Compra> compras;
        if (estadoFiltro != null) {
            compras = compraDAO.obtenerComprasPorEstado(estadoFiltro);
        } else {
            compras = compraDAO.obtenerCompras();
        }

        // Aplicar filtro de proveedor
        if (proveedorSeleccionado != null && proveedorSeleccionado.getIdProveedor() != -1) {
            final int idProv = proveedorSeleccionado.getIdProveedor();
            compras = compras.stream()
                    .filter(c -> c.getIdProveedor() == idProv)
                    .toList();
        }

        // Aplicar filtro de fecha desde
        if (fechaDesde != null) {
            compras = compras.stream()
                    .filter(c -> {
                        if (c.getFechaCompra() == null) return false;
                        java.time.LocalDate fechaCompra = c.getFechaCompra().toLocalDateTime().toLocalDate();
                        return !fechaCompra.isBefore(fechaDesde);
                    })
                    .toList();
        }

        // Aplicar filtro de fecha hasta
        if (fechaHasta != null) {
            compras = compras.stream()
                    .filter(c -> {
                        if (c.getFechaCompra() == null) return false;
                        java.time.LocalDate fechaCompra = c.getFechaCompra().toLocalDateTime().toLocalDate();
                        return !fechaCompra.isAfter(fechaHasta);
                    })
                    .toList();
        }

        // Actualizar tabla
        comprasData.setAll(compras);

        // ✅ CRÍTICO: Actualizar contadores SIEMPRE (incluso con filtros activos)
        actualizarContadoresTabs();
    }

    /**
     * Actualizar contadores según filtros activos
     */
    private void actualizarContadoresTabs() {
        try {
            // Obtener filtros actuales
            Proveedor proveedorSeleccionado = cbFiltroProveedor.getValue();
            java.time.LocalDate fechaDesde = dpFechaDesde.getValue();
            java.time.LocalDate fechaHasta = dpFechaHasta.getValue();

            // Verificar si hay filtros activos
            boolean hayFiltros = (proveedorSeleccionado != null && proveedorSeleccionado.getIdProveedor() != -1)
                    || fechaDesde != null
                    || fechaHasta != null;

            if (!hayFiltros) {
                // SIN filtros: usar conteo directo de BD
                int pendientes = compraDAO.contarComprasPorEstado("PENDIENTE");
                int completadas = compraDAO.contarComprasPorEstado("COMPLETADA");
                int canceladas = compraDAO.contarComprasPorEstado("CANCELADA");
                int todas = pendientes + completadas + canceladas;

                tabPendientes.setText(String.format("Pendientes (%d)", pendientes));
                tabCompletadas.setText(String.format("Completadas (%d)", completadas));
                tabCanceladas.setText(String.format("Canceladas (%d)", canceladas));
                tabTodas.setText(String.format("Todas (%d)", todas));
            } else {
                // CON filtros: contar aplicando filtros a cada estado
                int pendientes = contarConFiltros("PENDIENTE", proveedorSeleccionado, fechaDesde, fechaHasta);
                int completadas = contarConFiltros("COMPLETADA", proveedorSeleccionado, fechaDesde, fechaHasta);
                int canceladas = contarConFiltros("CANCELADA", proveedorSeleccionado, fechaDesde, fechaHasta);
                int todas = pendientes + completadas + canceladas;

                tabPendientes.setText(String.format("Pendientes (%d)*", pendientes));
                tabCompletadas.setText(String.format("Completadas (%d)*", completadas));
                tabCanceladas.setText(String.format("Canceladas (%d)*", canceladas));
                tabTodas.setText(String.format("Todas (%d)*", todas));
            }

        } catch (Exception e) {
            System.err.println("❌ Error al actualizar contadores: " + e.getMessage());
        }
    }

    /**
     * Contar compras aplicando filtros
     */
    private int contarConFiltros(String estado, Proveedor proveedor,
                                 java.time.LocalDate fechaDesde, java.time.LocalDate fechaHasta) {
        List<Compra> compras;

        if (estado != null) {
            compras = compraDAO.obtenerComprasPorEstado(estado);
        } else {
            compras = compraDAO.obtenerCompras();
        }

        // Aplicar filtro de proveedor
        if (proveedor != null && proveedor.getIdProveedor() != -1) {
            final int idProv = proveedor.getIdProveedor();
            compras = compras.stream()
                    .filter(c -> c.getIdProveedor() == idProv)
                    .toList();
        }

        // Aplicar filtro de fecha desde
        if (fechaDesde != null) {
            compras = compras.stream()
                    .filter(c -> {
                        if (c.getFechaCompra() == null) return false;
                        java.time.LocalDate fechaCompra = c.getFechaCompra().toLocalDateTime().toLocalDate();
                        return !fechaCompra.isBefore(fechaDesde);
                    })
                    .toList();
        }

        // Aplicar filtro de fecha hasta
        if (fechaHasta != null) {
            compras = compras.stream()
                    .filter(c -> {
                        if (c.getFechaCompra() == null) return false;
                        java.time.LocalDate fechaCompra = c.getFechaCompra().toLocalDateTime().toLocalDate();
                        return !fechaCompra.isAfter(fechaHasta);
                    })
                    .toList();
        }

        return compras.size();
    }

    private void cargarFiltros() {
        try {
            List<Proveedor> proveedores = proveedorDAO.obtenerProveedores();

            Proveedor todos = new Proveedor();
            todos.setIdProveedor(-1);
            todos.setNombre("Todos los proveedores");

            proveedores.add(0, todos);

            cbFiltroProveedor.setItems(FXCollections.observableArrayList(proveedores));
            cbFiltroProveedor.getSelectionModel().selectFirst();

            cbFiltroProveedor.setCellFactory(param -> new ListCell<Proveedor>() {
                @Override
                protected void updateItem(Proveedor prov, boolean empty) {
                    super.updateItem(prov, empty);
                    setText(empty || prov == null ? null : prov.getNombre());
                }
            });

            cbFiltroProveedor.setButtonCell(new ListCell<Proveedor>() {
                @Override
                protected void updateItem(Proveedor prov, boolean empty) {
                    super.updateItem(prov, empty);
                    setText(empty || prov == null ? null : prov.getNombre());
                }
            });

            System.out.println("✅ Filtros cargados");

        } catch (Exception e) {
            System.err.println("❌ Error al cargar filtros: " + e.getMessage());
        }
    }

    private void cargarCompras() {
        System.out.println("📦 Cargando compras...");

        try {
            List<Compra> compras = compraDAO.obtenerCompras();
            comprasData.setAll(compras);
            actualizarContadoresTabs();
            System.out.println("✅ " + compras.size() + " compras cargadas");

        } catch (Exception e) {
            System.err.println("❌ Error al cargar compras: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar las compras:\n" + e.getMessage());
        }
    }

    private void filtrarPorEstado(String estado) {
        System.out.println("📋 Filtrando por estado: " + estado);
        List<Compra> comprasFiltradas = compraDAO.obtenerComprasPorEstado(estado);
        comprasData.setAll(comprasFiltradas);
    }

    private void mostrarTodas() {
        System.out.println("📋 Mostrando todas las compras");
        comprasData.setAll(compraDAO.obtenerCompras());
    }

    @FXML
    private void nuevaCompra() {
        System.out.println("➕ Iniciando nueva compra...");

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Nueva Compra");
        dialogo.setHeaderText("Registrar Nueva Compra de Mercadería");

        VBox contenedor = new VBox(20);
        contenedor.setPadding(new Insets(20));
        contenedor.setPrefWidth(800);
        contenedor.setPrefHeight(600);

        // ===== PASO 1: DATOS GENERALES =====
        VBox paso1 = crearPaso1();

        ComboBox<Proveedor> cbProveedor = (ComboBox<Proveedor>)
                ((GridPane) paso1.getChildren().get(1)).getChildren().get(1);
        ComboBox<MetodoDePago> cbMetodoPago = (ComboBox<MetodoDePago>)
                ((GridPane) paso1.getChildren().get(1)).getChildren().get(3);
        TextField txtFactura = (TextField)
                ((GridPane) paso1.getChildren().get(1)).getChildren().get(5);

        // ===== PASO 2: PRODUCTOS =====
        VBox paso2 = new VBox(10);
        paso2.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        Label lblPaso2 = new Label("Paso 2: Productos");
        lblPaso2.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox buscador = new HBox(10);
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar producto por nombre o código...");
        txtBuscar.setPrefWidth(400);

        Button btnBuscar = new Button("🔍 Buscar");
        btnBuscar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        buscador.getChildren().addAll(txtBuscar, btnBuscar);

        TableView<DetalleCompra> tablaCarrito = new TableView<>();
        ObservableList<DetalleCompra> carritoData = FXCollections.observableArrayList();

        // ✅ CRÍTICO: Listener para detectar cambio de proveedor
        cbProveedor.valueProperty().addListener((observable, proveedorAnterior, proveedorNuevo) -> {
            // Si ya había productos en el carrito y cambió el proveedor
            if (!carritoData.isEmpty() && proveedorAnterior != null && proveedorNuevo != null
                    && proveedorAnterior.getIdProveedor() != proveedorNuevo.getIdProveedor()) {

                System.out.println("⚠️ Cambio de proveedor detectado");
                System.out.println("   Anterior: " + proveedorAnterior.getNombre());
                System.out.println("   Nuevo: " + proveedorNuevo.getNombre());

                // Mostrar advertencia
                Alert advertencia = new Alert(Alert.AlertType.WARNING);
                advertencia.setTitle("⚠️ Cambio de Proveedor");
                advertencia.setHeaderText("¿Está seguro de cambiar el proveedor?");
                advertencia.setContentText(
                        "Ya tiene productos en el carrito del proveedor:\n" +
                                "'" + proveedorAnterior.getNombre() + "'\n\n" +
                                "Si cambia a '" + proveedorNuevo.getNombre() + "',\n" +
                                "se eliminarán TODOS los productos del carrito.\n\n" +
                                "¿Desea continuar?"
                );

                ButtonType btnContinuar = new ButtonType("Sí, cambiar proveedor", ButtonBar.ButtonData.OK_DONE);
                ButtonType btnCancelar = new ButtonType("No, mantener proveedor", ButtonBar.ButtonData.CANCEL_CLOSE);
                advertencia.getButtonTypes().setAll(btnContinuar, btnCancelar);

                Optional<ButtonType> resultado = advertencia.showAndWait();

                if (resultado.isPresent() && resultado.get() == btnContinuar) {
                    // Usuario confirmó: limpiar carrito
                    carritoData.clear();
                    System.out.println("✅ Carrito limpiado. Nuevo proveedor: " + proveedorNuevo.getNombre());
                } else {
                    // Usuario canceló: revertir cambio de proveedor
                    cbProveedor.setValue(proveedorAnterior);
                    System.out.println("❌ Cambio cancelado. Proveedor mantiene: " + proveedorAnterior.getNombre());
                }
            }
        });

        TableColumn<DetalleCompra, String> colProd = new TableColumn<>("Producto");
        colProd.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colProd.setPrefWidth(250);

        TableColumn<DetalleCompra, Integer> colCant = new TableColumn<>("Cantidad");
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCant.setPrefWidth(80);
        colCant.setStyle("-fx-alignment: CENTER;");

        TableColumn<DetalleCompra, String> colPrec = new TableColumn<>("Precio Unit.");
        colPrec.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatoPrecio.format(cellData.getValue().getPrecioUnitario())));
        colPrec.setPrefWidth(100);
        colPrec.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<DetalleCompra, String> colSub = new TableColumn<>("Subtotal");
        colSub.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatoPrecio.format(cellData.getValue().getSubtotal())));
        colSub.setPrefWidth(100);
        colSub.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<DetalleCompra, Void> colQuitar = new TableColumn<>("");
        colQuitar.setPrefWidth(50);
        colQuitar.setCellFactory(column -> new TableCell<DetalleCompra, Void>() {
            private final Button btnQuitar = new Button("🗑️");

            {
                btnQuitar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnQuitar.setPrefSize(30, 25);

                btnQuitar.setOnAction(e -> {
                    DetalleCompra detalle = getTableRow().getItem();
                    if (detalle != null) {
                        carritoData.remove(detalle);
                        tablaCarrito.refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnQuitar);
            }
        });

        tablaCarrito.getColumns().addAll(colProd, colCant, colPrec, colSub, colQuitar);
        tablaCarrito.setItems(carritoData);
        tablaCarrito.setPrefHeight(200);

        paso2.getChildren().addAll(lblPaso2, buscador, tablaCarrito);

        // ===== PASO 3: RESUMEN =====
        VBox paso3 = new VBox(10);
        paso3.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        Label lblPaso3 = new Label("Paso 3: Resumen y Observaciones");
        lblPaso3.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane gridResumen = new GridPane();
        gridResumen.setHgap(15);
        gridResumen.setVgap(8);

        Label lblSubtotalTxt = new Label("Subtotal:");
        Label lblSubtotal = new Label("$0,00");
        lblSubtotal.setStyle("-fx-font-size: 14px;");

        Label lblTotalTxt = new Label("TOTAL:");
        lblTotalTxt.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label lblTotal = new Label("$0,00");
        lblTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        gridResumen.add(lblSubtotalTxt, 0, 0);
        gridResumen.add(lblSubtotal, 1, 0);
        gridResumen.add(lblTotalTxt, 0, 1);
        gridResumen.add(lblTotal, 1, 1);

        Label lblObs = new Label("Observaciones:");
        TextArea txtObservaciones = new TextArea();
        txtObservaciones.setPromptText("Notas adicionales sobre la compra (opcional)");
        txtObservaciones.setPrefRowCount(3);
        txtObservaciones.setWrapText(true);

        paso3.getChildren().addAll(lblPaso3, gridResumen, lblObs, txtObservaciones);

        // Listener para actualizar totales
        carritoData.addListener((javafx.collections.ListChangeListener.Change<? extends DetalleCompra> c) -> {
            double total = carritoData.stream().mapToDouble(DetalleCompra::getSubtotal).sum();
            lblSubtotal.setText(formatoPrecio.format(total));
            lblTotal.setText(formatoPrecio.format(total));
        });

        //Evento buscar CON FILTRO DE PROVEEDOR
        btnBuscar.setOnAction(e -> {
            if (cbProveedor.getValue() == null) {
                mostrarError("Primero seleccione un proveedor");
                return;
            }
            buscarYAgregarProductoConFiltro(txtBuscar.getText(), cbProveedor.getValue(), carritoData);
        });

        txtBuscar.setOnAction(e -> btnBuscar.fire());

        contenedor.getChildren().addAll(paso1, paso2, paso3);

        dialogo.getDialogPane().setContent(contenedor);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogo.setResizable(true);

        Button btnOK = (Button) dialogo.getDialogPane().lookupButton(ButtonType.OK);
        btnOK.setText("💾 Guardar como PENDIENTE");
        btnOK.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        //Validar SIN cerrar el diálogo
        btnOK.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            // Validaciones
            if (cbProveedor.getValue() == null) {
                mostrarError("Debe seleccionar un proveedor");
                event.consume(); // ← Cancela el cierre del diálogo
                return;
            }
            if (cbMetodoPago.getValue() == null) {
                mostrarError("Debe seleccionar un método de pago");
                event.consume();
                return;
            }
            if (carritoData.isEmpty()) {
                mostrarError("Debe agregar al menos un producto");
                event.consume();
                return;
            }

            boolean guardado = guardarNuevaCompraSimple(cbProveedor.getValue(), cbMetodoPago.getValue(),
                    txtFactura.getText(), txtObservaciones.getText(), carritoData);

            if (!guardado) {
                event.consume(); // Si falló el guardado, no cerrar el diálogo
            }
            // Si guardado == true, el diálogo se cierra automáticamente
        });

        dialogo.showAndWait();
    }

    private VBox crearPaso1() {
        VBox paso1 = new VBox(10);
        paso1.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        Label lblPaso1 = new Label("Paso 1: Datos Generales");
        lblPaso1.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        Label lblProv = new Label("* Proveedor:");
        lblProv.setStyle("-fx-font-weight: bold;");
        ComboBox<Proveedor> cbProv = new ComboBox<>();
        List<Proveedor> provs = proveedorDAO.obtenerProveedores().stream().filter(Proveedor::isActivo).toList();
        cbProv.setItems(FXCollections.observableArrayList(provs));
        cbProv.setPromptText("Seleccione un proveedor");
        cbProv.setPrefWidth(300);
        cbProv.setCellFactory(param -> new ListCell<Proveedor>() {
            @Override
            protected void updateItem(Proveedor p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getNombre());
            }
        });
        cbProv.setButtonCell(new ListCell<Proveedor>() {
            @Override
            protected void updateItem(Proveedor p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getNombre());
            }
        });

        Label lblMetodo = new Label("* Método de Pago:");
        lblMetodo.setStyle("-fx-font-weight: bold;");
        ComboBox<MetodoDePago> cbMetodo = new ComboBox<>();
        cbMetodo.setItems(FXCollections.observableArrayList(metodoPagoDAO.obtenerMetodosDePago()));
        cbMetodo.setPromptText("Seleccione método");
        cbMetodo.setPrefWidth(300);
        cbMetodo.getSelectionModel().selectFirst();

        Label lblFact = new Label("Nº Factura Proveedor:");
        TextField txtFact = new TextField();
        txtFact.setPromptText("Ej: A-0001-00012345");
        txtFact.setPrefWidth(300);

        grid.add(lblProv, 0, 0);
        grid.add(cbProv, 1, 0);
        grid.add(lblMetodo, 0, 1);
        grid.add(cbMetodo, 1, 1);
        grid.add(lblFact, 0, 2);
        grid.add(txtFact, 1, 2);

        paso1.getChildren().addAll(lblPaso1, grid);
        return paso1;
    }

    @FXML
    private void aplicarFiltros() {
        System.out.println("🔍 Aplicando filtros...");
        aplicarFiltrosConEstado();
        System.out.println("✅ Filtros aplicados");
    }

    @FXML
    private void limpiarFiltros() {
        System.out.println("✖ Limpiando filtros...");

        cbFiltroProveedor.getSelectionModel().selectFirst();
        dpFechaDesde.setValue(null);
        dpFechaHasta.setValue(null);

        // Recargar con filtros limpios
        aplicarFiltrosConEstado();

        System.out.println("✅ Filtros limpiados");
    }

    private void verDetalleCompra(Compra compra) {
        System.out.println("ℹ️ Ver detalle: " + compra.getNumeroCompra());

        Alert dialogo = new Alert(Alert.AlertType.INFORMATION);
        dialogo.setTitle("Detalle de Compra");
        dialogo.setHeaderText("Compra: " + compra.getNumeroCompra());

        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(15));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);

        int fila = 0;
        grid.add(new Label("Proveedor:"), 0, fila);
        grid.add(new Label(compra.getNombreProveedor()), 1, fila++);

        grid.add(new Label("Estado:"), 0, fila);
        Label lblEstado = new Label(compra.getIconoEstado() + " " + compra.getEstado());
        lblEstado.setStyle("-fx-font-weight: bold; -fx-text-fill: " + compra.getColorEstado());
        grid.add(lblEstado, 1, fila++);

        grid.add(new Label("Fecha Compra:"), 0, fila);
        grid.add(new Label(compra.getFechaCompraFormateada()), 1, fila++);

        if (compra.isCompletada()) {
            grid.add(new Label("Fecha Entrega:"), 0, fila);
            grid.add(new Label(compra.getFechaEntregaFormateada()), 1, fila++);
        }

        grid.add(new Label("Método Pago:"), 0, fila);
        grid.add(new Label(compra.getNombreMetodoPago()), 1, fila++);

        if (compra.getNumeroFacturaProveedor() != null) {
            grid.add(new Label("Nº Factura:"), 0, fila);
            grid.add(new Label(compra.getNumeroFacturaProveedor()), 1, fila++);
        }

        contenido.getChildren().add(grid);

        Separator sep = new Separator();
        contenido.getChildren().add(sep);

        Label lblProductos = new Label("Productos:");
        lblProductos.setStyle("-fx-font-weight: bold;");
        contenido.getChildren().add(lblProductos);

        List<Map<String, Object>> detalle = compraDAO.obtenerDetalleCompra(compra.getIdCompra());

        VBox listaProductos = new VBox(5);
        for (Map<String, Object> item : detalle) {
            String linea = String.format("• %s - Cant: %d × %s = %s",
                    item.get("nombreProducto"),
                    item.get("cantidad"),
                    formatoPrecio.format(item.get("precioUnitario")),
                    formatoPrecio.format(item.get("subtotal"))
            );
            listaProductos.getChildren().add(new Label(linea));
        }

        ScrollPane scrollProductos = new ScrollPane(listaProductos);
        scrollProductos.setFitToWidth(true);
        scrollProductos.setPrefHeight(150);
        contenido.getChildren().add(scrollProductos);

        Separator sep2 = new Separator();
        contenido.getChildren().add(sep2);

        GridPane gridTotales = new GridPane();
        gridTotales.setHgap(15);
        gridTotales.setVgap(5);

        gridTotales.add(new Label("Subtotal:"), 0, 0);
        gridTotales.add(new Label(formatoPrecio.format(compra.getSubtotal())), 1, 0);

        if (compra.getDescuentos() > 0) {
            gridTotales.add(new Label("Descuentos:"), 0, 1);
            gridTotales.add(new Label("-" + formatoPrecio.format(compra.getDescuentos())), 1, 1);
        }

        Label lblTotal = new Label("TOTAL:");
        lblTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblTotalValor = new Label(formatoPrecio.format(compra.getTotal()));
        lblTotalValor.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        gridTotales.add(lblTotal, 0, 2);
        gridTotales.add(lblTotalValor, 1, 2);

        contenido.getChildren().add(gridTotales);

        if (compra.getObservaciones() != null && !compra.getObservaciones().trim().isEmpty()) {
            Separator sep3 = new Separator();
            contenido.getChildren().add(sep3);

            Label lblObs = new Label("Observaciones:");
            lblObs.setStyle("-fx-font-weight: bold;");
            contenido.getChildren().add(lblObs);

            TextArea txtObs = new TextArea(compra.getObservaciones());
            txtObs.setEditable(false);
            txtObs.setPrefRowCount(3);
            txtObs.setWrapText(true);
            contenido.getChildren().add(txtObs);
        }

        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().setPrefWidth(500);
        dialogo.showAndWait();
    }

    /**
     * Editar compra pendiente
     */
    private void editarCompra(Compra compra) {
        System.out.println("✏️ Editar compra: " + compra.getNumeroCompra());

        if (!compra.isPendiente()) {
            mostrarError("Solo se pueden editar compras en estado PENDIENTE");
            return;
        }

        // Obtener detalle actual
        List<Map<String, Object>> detalleActual = compraDAO.obtenerDetalleCompra(compra.getIdCompra());

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Editar Compra");
        dialogo.setHeaderText("Editar Compra: " + compra.getNumeroCompra());

        VBox contenedor = new VBox(20);
        contenedor.setPadding(new Insets(20));
        contenedor.setPrefWidth(800);
        contenedor.setPrefHeight(600);

        // ===== PASO 1: DATOS GENERALES (EDICIÓN) =====
        VBox paso1 = new VBox(10);
        paso1.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        Label lblPaso1 = new Label("Paso 1: Datos Generales");
        lblPaso1.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        // Proveedor (NO editable en compra existente)
        Label lblProv = new Label("Proveedor:");
        lblProv.setStyle("-fx-font-weight: bold;");
        Label lblProvValor = new Label(compra.getNombreProveedor());
        lblProvValor.setStyle("-fx-text-fill: #7f8c8d;");

        // Método de pago (EDITABLE)
        Label lblMetodo = new Label("* Método de Pago:");
        lblMetodo.setStyle("-fx-font-weight: bold;");
        ComboBox<MetodoDePago> cbMetodo = new ComboBox<>();
        cbMetodo.setItems(FXCollections.observableArrayList(metodoPagoDAO.obtenerMetodosDePago()));
        cbMetodo.setPrefWidth(300);

        // Seleccionar el método actual
        for (MetodoDePago metodo : cbMetodo.getItems()) {
            if (metodo.getIdMetodoDePago() == compra.getIdMetodoDePago()) {
                cbMetodo.setValue(metodo);
                break;
            }
        }

        // Factura (EDITABLE)
        Label lblFact = new Label("Nº Factura Proveedor:");
        TextField txtFact = new TextField(compra.getNumeroFacturaProveedor() != null ? compra.getNumeroFacturaProveedor() : "");
        txtFact.setPromptText("Ej: A-0001-00012345");
        txtFact.setPrefWidth(300);

        grid.add(lblProv, 0, 0);
        grid.add(lblProvValor, 1, 0);
        grid.add(lblMetodo, 0, 1);
        grid.add(cbMetodo, 1, 1);
        grid.add(lblFact, 0, 2);
        grid.add(txtFact, 1, 2);

        paso1.getChildren().addAll(lblPaso1, grid);

        // ===== PASO 2: PRODUCTOS (EDICIÓN) =====
        VBox paso2 = new VBox(10);
        paso2.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        Label lblPaso2 = new Label("Paso 2: Productos");
        lblPaso2.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox buscador = new HBox(10);
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar producto por nombre o código...");
        txtBuscar.setPrefWidth(400);

        Button btnBuscar = new Button("🔍 Buscar");
        btnBuscar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        buscador.getChildren().addAll(txtBuscar, btnBuscar);

        TableView<DetalleCompra> tablaCarrito = new TableView<>();
        ObservableList<DetalleCompra> carritoData = FXCollections.observableArrayList();

        // Cargar productos actuales
        for (Map<String, Object> item : detalleActual) {
            DetalleCompra detalle = new DetalleCompra(
                    (Integer) item.get("idProducto"),
                    (String) item.get("codigoBarras"),
                    (String) item.get("nombreProducto"),
                    (Integer) item.get("cantidad"),
                    (Double) item.get("precioUnitario")
            );
            carritoData.add(detalle);
        }

        TableColumn<DetalleCompra, String> colProd = new TableColumn<>("Producto");
        colProd.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colProd.setPrefWidth(250);

        TableColumn<DetalleCompra, Integer> colCant = new TableColumn<>("Cantidad");
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCant.setPrefWidth(80);
        colCant.setStyle("-fx-alignment: CENTER;");

        TableColumn<DetalleCompra, String> colPrec = new TableColumn<>("Precio Unit.");
        colPrec.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatoPrecio.format(cellData.getValue().getPrecioUnitario())));
        colPrec.setPrefWidth(100);
        colPrec.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<DetalleCompra, String> colSub = new TableColumn<>("Subtotal");
        colSub.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatoPrecio.format(cellData.getValue().getSubtotal())));
        colSub.setPrefWidth(100);
        colSub.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<DetalleCompra, Void> colQuitar = new TableColumn<>("");
        colQuitar.setPrefWidth(50);
        colQuitar.setCellFactory(column -> new TableCell<DetalleCompra, Void>() {
            private final Button btnQuitar = new Button("🗑️");

            {
                btnQuitar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnQuitar.setPrefSize(30, 25);

                btnQuitar.setOnAction(e -> {
                    DetalleCompra detalle = getTableRow().getItem();
                    if (detalle != null) {
                        carritoData.remove(detalle);
                        tablaCarrito.refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnQuitar);
            }
        });

        tablaCarrito.getColumns().addAll(colProd, colCant, colPrec, colSub, colQuitar);
        tablaCarrito.setItems(carritoData);
        tablaCarrito.setPrefHeight(200);

        paso2.getChildren().addAll(lblPaso2, buscador, tablaCarrito);

        // ===== PASO 3: RESUMEN =====
        VBox paso3 = new VBox(10);
        paso3.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        Label lblPaso3 = new Label("Paso 3: Resumen y Observaciones");
        lblPaso3.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane gridResumen = new GridPane();
        gridResumen.setHgap(15);
        gridResumen.setVgap(8);

        Label lblSubtotalTxt = new Label("Subtotal:");
        Label lblSubtotal = new Label(formatoPrecio.format(compra.getSubtotal()));
        lblSubtotal.setStyle("-fx-font-size: 14px;");

        Label lblTotalTxt = new Label("TOTAL:");
        lblTotalTxt.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label lblTotal = new Label(formatoPrecio.format(compra.getTotal()));
        lblTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        gridResumen.add(lblSubtotalTxt, 0, 0);
        gridResumen.add(lblSubtotal, 1, 0);
        gridResumen.add(lblTotalTxt, 0, 1);
        gridResumen.add(lblTotal, 1, 1);

        Label lblObs = new Label("Observaciones:");
        TextArea txtObservaciones = new TextArea(compra.getObservaciones() != null ? compra.getObservaciones() : "");
        txtObservaciones.setPromptText("Notas adicionales sobre la compra (opcional)");
        txtObservaciones.setPrefRowCount(3);
        txtObservaciones.setWrapText(true);

        paso3.getChildren().addAll(lblPaso3, gridResumen, lblObs, txtObservaciones);

        // Listener para actualizar totales
        carritoData.addListener((javafx.collections.ListChangeListener.Change<? extends DetalleCompra> c) -> {
            double total = carritoData.stream().mapToDouble(DetalleCompra::getSubtotal).sum();
            lblSubtotal.setText(formatoPrecio.format(total));
            lblTotal.setText(formatoPrecio.format(total));
        });

        // Obtener proveedor actual
        Proveedor proveedorActual = proveedorDAO.obtenerProveedores().stream()
                .filter(p -> p.getIdProveedor() == compra.getIdProveedor())
                .findFirst()
                .orElse(null);

        // Evento buscar
        btnBuscar.setOnAction(e -> {
            if (proveedorActual == null) {
                mostrarError("Error al obtener proveedor");
                return;
            }
            buscarYAgregarProductoConFiltro(txtBuscar.getText(), proveedorActual, carritoData);
        });

        txtBuscar.setOnAction(e -> btnBuscar.fire());

        contenedor.getChildren().addAll(paso1, paso2, paso3);

        dialogo.getDialogPane().setContent(contenedor);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogo.setResizable(true);

        Button btnOK = (Button) dialogo.getDialogPane().lookupButton(ButtonType.OK);
        btnOK.setText("💾 Guardar Cambios");
        btnOK.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        //Validar SIN cerrar el diálogo
        btnOK.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (cbMetodo.getValue() == null) {
                mostrarError("Debe seleccionar un método de pago");
                event.consume();
                return;
            }
            if (carritoData.isEmpty()) {
                mostrarError("Debe tener al menos un producto");
                event.consume();
                return;
            }

            // Guardar cambios
            boolean guardado = guardarCambiosCompra(compra, cbMetodo.getValue(), txtFact.getText(),
                    txtObservaciones.getText(), carritoData);

            if (!guardado) {
                event.consume(); // Si falló, no cerrar
            }
        });

        dialogo.showAndWait();
    }

    /**
     * Guardar cambios en una compra existente
     */
    private boolean guardarCambiosCompra(Compra compra, MetodoDePago metodoPago,
                                      String numeroFactura, String observaciones,
                                      ObservableList<DetalleCompra> carritoData) {

        System.out.println("💾 Guardando cambios en compra: " + compra.getNumeroCompra());

        try {
            // 1. Eliminar todos los productos actuales
            List<Map<String, Object>> detalleActual = compraDAO.obtenerDetalleCompra(compra.getIdCompra());
            for (Map<String, Object> item : detalleActual) {
                compraDAO.quitarProductoDeCompra(compra.getIdCompra(), (Integer) item.get("idProducto"));
            }

            // 2. Agregar los nuevos productos
            for (DetalleCompra detalle : carritoData) {
                compraDAO.agregarProductoACompra(
                        compra.getIdCompra(),
                        detalle.getIdProducto(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario()
                );
            }

            // 3. Actualizar datos generales
            compra.setIdMetodoDePago(metodoPago.getIdMetodoDePago());
            compra.setNumeroFacturaProveedor(numeroFactura.isEmpty() ? null : numeroFactura);
            compra.setObservaciones(observaciones.isEmpty() ? null : observaciones);
            compraDAO.actualizarCompra(compra);

            mostrarInfo("✅ Compra actualizada exitosamente");
            cargarCompras();

            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al guardar cambios: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al guardar los cambios:\n" + e.getMessage());

            return false;
        }
    }

    private void completarCompra(Compra compra) {
        System.out.println("✅ Completar compra: " + compra.getNumeroCompra());

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Completar Compra");
        confirmacion.setHeaderText("¿Completar compra " + compra.getNumeroCompra() + "?");
        confirmacion.setContentText(
                "Esta acción:\n\n" +
                        "• Actualizará el stock de los productos\n" +
                        "• Registrará los movimientos de inventario\n" +
                        "• Cambiará el estado a COMPLETADA\n\n" +
                        "Proveedor: " + compra.getNombreProveedor() + "\n" +
                        "Total: " + formatoPrecio.format(compra.getTotal()) + "\n\n" +
                        "¿Confirmar?"
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean exito = compraDAO.completarCompra(compra.getIdCompra());

            if (exito) {
                mostrarInfo("✅ Compra completada exitosamente\n\n" +
                        "• Stock actualizado\n" +
                        "• Movimientos registrados\n" +
                        "• Estado: COMPLETADA");

                cargarCompras();
                tabPane.getSelectionModel().select(tabCompletadas);
            } else {
                mostrarError("Error al completar la compra.\nVerifique los logs.");
            }
        }
    }

    private void cancelarCompra(Compra compra) {
        System.out.println("❌ Cancelar compra: " + compra.getNumeroCompra());

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar Compra");
        confirmacion.setHeaderText("¿Cancelar compra " + compra.getNumeroCompra() + "?");
        confirmacion.setContentText(
                "Proveedor: " + compra.getNombreProveedor() + "\n" +
                        "Total: " + formatoPrecio.format(compra.getTotal()) + "\n\n" +
                        "Como la compra está PENDIENTE, no afectará el stock.\n\n" +
                        "¿Confirmar cancelación?"
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean exito = compraDAO.cancelarCompra(compra.getIdCompra());

            if (exito) {
                mostrarInfo("Compra cancelada correctamente");
                cargarCompras();
                tabPane.getSelectionModel().select(tabCanceladas);
            } else {
                mostrarError("Error al cancelar la compra");
            }
        }
    }

    private void anularCompra(Compra compra) {
        System.out.println("🗑️ Anular compra: " + compra.getNumeroCompra());

        Alert advertencia = new Alert(Alert.AlertType.WARNING);
        advertencia.setTitle("⚠️ Anular Compra Completada");
        advertencia.setHeaderText("ADVERTENCIA");
        advertencia.setContentText(
                "Esta compra ya está COMPLETADA y actualizó el stock.\n\n" +
                        "Al anular:\n" +
                        "• Se REVERTIRÁ el stock automáticamente\n" +
                        "• Se registrarán movimientos de reversión\n" +
                        "• El estado cambiará a CANCELADA\n\n" +
                        "Compra: " + compra.getNumeroCompra() + "\n" +
                        "Proveedor: " + compra.getNombreProveedor() + "\n" +
                        "Total: " + formatoPrecio.format(compra.getTotal()) + "\n\n" +
                        "¿Está seguro de continuar?"
        );

        ButtonType btnAnular = new ButtonType("Anular Compra", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        advertencia.getButtonTypes().setAll(btnAnular, btnCancelar);

        Optional<ButtonType> resultado = advertencia.showAndWait();

        if (resultado.isPresent() && resultado.get() == btnAnular) {
            Alert segunda = new Alert(Alert.AlertType.CONFIRMATION);
            segunda.setTitle("Confirmar Anulación");
            segunda.setHeaderText("¿Está completamente seguro?");
            segunda.setContentText("Esta acción NO se puede deshacer.\n\n¿Continuar?");

            Optional<ButtonType> resultado2 = segunda.showAndWait();

            if (resultado2.isPresent() && resultado2.get() == ButtonType.OK) {
                boolean exito = compraDAO.cancelarCompra(compra.getIdCompra());

                if (exito) {
                    mostrarInfo("✅ Compra anulada exitosamente\n\n" +
                            "• Stock revertido\n" +
                            "• Movimientos registrados\n" +
                            "• Estado: CANCELADA");

                    cargarCompras();
                    tabPane.getSelectionModel().select(tabCanceladas);
                } else {
                    mostrarError("Error al anular la compra");
                }
            }
        }
    }

    /**
     * Buscar productos SOLO del proveedor seleccionado
     */
    private void buscarYAgregarProductoConFiltro(String busqueda, Proveedor proveedor,
                                                 ObservableList<DetalleCompra> carritoData) {

        if (busqueda == null || busqueda.trim().isEmpty()) {
            mostrarError("Ingrese un texto para buscar");
            return;
        }

        System.out.println("🔍 Buscando productos del proveedor: " + proveedor.getNombre());
        System.out.println("   Término de búsqueda: " + busqueda);

        // ✅ USAR EL MÉTODO CON FILTRO DE PROVEEDOR
        List<Producto> productos = productoDAO.buscarProductosDelProveedor(busqueda.trim(), proveedor.getIdProveedor());

        System.out.println("   Productos encontrados: " + productos.size());

        if (productos.isEmpty()) {
            mostrarInfo("No se encontraron productos de este proveedor con: " + busqueda);
            return;
        }

        // Si hay un solo producto, agregarlo directamente
        if (productos.size() == 1) {
            mostrarDialogoAgregarProducto(productos.get(0), proveedor, carritoData);
            return;
        }

        // Si hay varios, mostrar lista de selección
        ChoiceDialog<Producto> dialogoSeleccion = new ChoiceDialog<>(productos.get(0), productos);
        dialogoSeleccion.setTitle("Seleccionar Producto");
        dialogoSeleccion.setHeaderText("Se encontraron " + productos.size() + " productos");
        dialogoSeleccion.setContentText("Seleccione el producto:");
        dialogoSeleccion.getDialogPane().setPrefWidth(600);

        // ✅ SOLUCIÓN: Configurar cómo se muestran los productos en el ComboBox
        ComboBox<Producto> comboProductos = (ComboBox<Producto>)
                dialogoSeleccion.getDialogPane().lookup(".combo-box");

        if (comboProductos != null) {
            comboProductos.setCellFactory(param -> new ListCell<Producto>() {
                @Override
                protected void updateItem(Producto producto, boolean empty) {
                    super.updateItem(producto, empty);
                    if (empty || producto == null) {
                        setText(null);
                    } else {
                        // Formato: "Código - Nombre"
                        setText(producto.getCodigo() + " - " + producto.getNombre());
                    }
                }
            });

            comboProductos.setButtonCell(new ListCell<Producto>() {
                @Override
                protected void updateItem(Producto producto, boolean empty) {
                    super.updateItem(producto, empty);
                    if (empty || producto == null) {
                        setText(null);
                    } else {
                        setText(producto.getCodigo() + " - " + producto.getNombre());
                    }
                }
            });
        }

        Optional<Producto> resultado = dialogoSeleccion.showAndWait();
        resultado.ifPresent(producto -> mostrarDialogoAgregarProducto(producto, proveedor, carritoData));
    }


    private void mostrarDialogoAgregarProducto(Producto producto, Proveedor proveedor,
                                               ObservableList<DetalleCompra> carritoData) {

        System.out.println("➕ Agregando producto: ID=" + producto.getIdProducto() + ", Nombre=" + producto.getNombre());

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Agregar Producto");
        dialogo.setHeaderText("Producto: " + producto.getNombre());

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        int fila = 0;

        grid.add(new Label("Código:"), 0, fila);
        Label lblCodigo = new Label(producto.getCodigo());
        lblCodigo.setStyle("-fx-font-weight: bold;");
        grid.add(lblCodigo, 1, fila++);

        grid.add(new Label("Nombre:"), 0, fila);
        Label lblNombre = new Label(producto.getNombre());
        lblNombre.setWrapText(true);
        lblNombre.setMaxWidth(350);
        grid.add(lblNombre, 1, fila++);

        double precioSugerido = obtenerPrecioProveedor(producto.getCodigo(), proveedor.getIdProveedor());
        if (precioSugerido == 0) {
            precioSugerido = producto.getPrecioCosto();
        }

        grid.add(new Label("Precio sugerido:"), 0, fila);
        Label lblSugerido = new Label(formatoPrecio.format(precioSugerido));
        lblSugerido.setStyle("-fx-text-fill: #7f8c8d;");
        grid.add(lblSugerido, 1, fila++);

        Separator sep = new Separator();
        grid.add(sep, 0, fila++, 2, 1);

        Label lblCantidad = new Label("* Cantidad:");
        lblCantidad.setStyle("-fx-font-weight: bold;");
        TextField txtCantidad = new TextField("1");
        txtCantidad.setPrefWidth(150);

        grid.add(lblCantidad, 0, fila);
        grid.add(txtCantidad, 1, fila++);

        Label lblPrecio = new Label("* Precio Unitario:");
        lblPrecio.setStyle("-fx-font-weight: bold;");
        TextField txtPrecio = new TextField(String.format("%.2f", precioSugerido));
        txtPrecio.setPrefWidth(150);

        Label lblNota = new Label("(Use punto como separador decimal: 2350.00)");
        lblNota.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");

        VBox boxPrecio = new VBox(5, txtPrecio, lblNota);

        grid.add(lblPrecio, 0, fila);
        grid.add(boxPrecio, 1, fila++);

        Label lblSubtotalTxt = new Label("Subtotal:");
        Label lblSubtotalValor = new Label("$0,00");
        lblSubtotalValor.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        grid.add(lblSubtotalTxt, 0, fila);
        grid.add(lblSubtotalValor, 1, fila++);

        Runnable actualizarSubtotal = () -> {
            try {
                int cant = Integer.parseInt(txtCantidad.getText().trim());
                double precio = Double.parseDouble(txtPrecio.getText().trim().replace(",", "."));
                double subtotal = cant * precio;
                lblSubtotalValor.setText(formatoPrecio.format(subtotal));
            } catch (NumberFormatException e) {
                lblSubtotalValor.setText("$0,00");
            }
        };

        txtCantidad.textProperty().addListener((obs, old, newVal) -> actualizarSubtotal.run());
        txtPrecio.textProperty().addListener((obs, old, newVal) -> actualizarSubtotal.run());

        actualizarSubtotal.run();

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button btnOK = (Button) dialogo.getDialogPane().lookupButton(ButtonType.OK);
        btnOK.setText("Agregar al Carrito");
        btnOK.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                String precioTexto = txtPrecio.getText().trim().replace(",", ".");
                double precioUnitario = Double.parseDouble(precioTexto);

                if (cantidad <= 0) {
                    mostrarError("La cantidad debe ser mayor a 0");
                    return;
                }

                if (precioUnitario <= 0) {
                    mostrarError("El precio debe ser mayor a 0");
                    return;
                }

                // ✅ CRÍTICO: Comparar por ID de producto, NO por objeto
                boolean productoExiste = false;

                for (DetalleCompra detalle : carritoData) {
                    // ✅ CORRECCIÓN: Usar getIdProducto() en lugar de comparar objetos
                    if (detalle.getIdProducto() == producto.getIdProducto()) {
                        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmacion.setTitle("Producto ya en el carrito");
                        confirmacion.setHeaderText("Este producto ya está en el carrito");
                        confirmacion.setContentText(
                                "Cantidad actual: " + detalle.getCantidad() + "\n" +
                                        "Precio actual: " + formatoPrecio.format(detalle.getPrecioUnitario()) + "\n\n" +
                                        "¿Qué desea hacer?"
                        );

                        ButtonType btnReemplazar = new ButtonType("Reemplazar");
                        ButtonType btnSumar = new ButtonType("Sumar cantidad");
                        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

                        confirmacion.getButtonTypes().setAll(btnReemplazar, btnSumar, btnCancelar);

                        Optional<ButtonType> respuesta = confirmacion.showAndWait();

                        if (respuesta.isPresent()) {
                            if (respuesta.get() == btnReemplazar) {
                                detalle.setCantidad(cantidad);
                                detalle.setPrecioUnitario(precioUnitario);
                                detalle.recalcularSubtotal();
                                carritoData.set(carritoData.indexOf(detalle), detalle);
                                System.out.println("✅ Producto reemplazado en carrito");
                            } else if (respuesta.get() == btnSumar) {
                                detalle.setCantidad(detalle.getCantidad() + cantidad);
                                detalle.recalcularSubtotal();
                                carritoData.set(carritoData.indexOf(detalle), detalle);
                                System.out.println("✅ Cantidad sumada en carrito");
                            }
                        }

                        productoExiste = true;
                        break;
                    }
                }

                // Si no existe, agregar nuevo
                if (!productoExiste) {
                    DetalleCompra nuevoDetalle = new DetalleCompra(
                            producto.getIdProducto(),
                            producto.getCodigo(),
                            producto.getNombre(),
                            cantidad,
                            precioUnitario
                    );

                    carritoData.add(nuevoDetalle);
                    System.out.println("✅ Producto agregado al carrito: ID=" + producto.getIdProducto() +
                            ", Nombre=" + producto.getNombre() +
                            ", Cantidad=" + cantidad);
                }

            } catch (NumberFormatException e) {
                mostrarError("Cantidad y precio deben ser números válidos.\nUse punto (.) como separador decimal.\nEjemplo: 2350.00");
                System.err.println("Error de formato: " + e.getMessage());
            }
        }
    }

    /**
     * Obtener precio del proveedor para un producto
     */
    private double obtenerPrecioProveedor(String codigoProducto, int idProveedor) {
        try {
            Integer idProducto = proveedorDAO.obtenerIdProductoPorCodigo(codigoProducto);
            if (idProducto != null) {
                List<com.libreria.model.ProductoProveedor> proveedoresProducto =
                        proveedorDAO.obtenerProveedoresDeProducto(idProducto);

                for (com.libreria.model.ProductoProveedor pp : proveedoresProducto) {
                    if (pp.getIdProveedor() == idProveedor) {
                        return pp.getPrecioCompra();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener precio: " + e.getMessage());
        }

        return 0;
    }


    private boolean guardarNuevaCompraSimple(Proveedor proveedor, MetodoDePago metodoPago,
                                          String numeroFactura, String observaciones,
                                          ObservableList<DetalleCompra> carritoData) {

        System.out.println("💾 Guardando nueva compra...");
        System.out.println("   Proveedor: " + proveedor.getNombre());
        System.out.println("   Método: " + metodoPago.getNombre());
        System.out.println("   Productos: " + carritoData.size());

        try {
            // 1. Crear la compra
            int idCompra = compraDAO.crearCompraNueva(
                    proveedor.getIdProveedor(),
                    1,  // TODO: Usuario actual del sistema
                    metodoPago.getIdMetodoDePago()
            );

            if (idCompra <= 0) {
                mostrarError("Error al crear la compra en la base de datos");
                return false;
            }

            System.out.println("✅ Compra creada con ID: " + idCompra);

            // 2. Agregar todos los productos
            int productosAgregados = 0;
            for (DetalleCompra detalle : carritoData) {
                System.out.println("   Agregando producto: ID=" + detalle.getIdProducto() +
                        ", Cantidad=" + detalle.getCantidad() +
                        ", Precio=" + detalle.getPrecioUnitario());

                boolean exito = compraDAO.agregarProductoACompra(
                        idCompra,
                        detalle.getIdProducto(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario()
                );

                if (exito) {
                    productosAgregados++;
                    System.out.println("   ✓ " + detalle.getNombreProducto() + " x" + detalle.getCantidad());
                } else {
                    System.err.println("   ✗ Error al agregar: " + detalle.getNombreProducto());
                }
            }

            if (productosAgregados == 0) {
                mostrarError("No se pudo agregar ningún producto a la compra");
                return false;
            }

            // 3. Actualizar datos adicionales (factura y observaciones)
            Compra compra = compraDAO.obtenerCompraPorId(idCompra);
            if (compra != null) {
                compra.setNumeroFacturaProveedor(numeroFactura.isEmpty() ? null : numeroFactura);
                compra.setObservaciones(observaciones.isEmpty() ? null : observaciones);
                compraDAO.actualizarCompra(compra);
            }

            System.out.println("✅ Compra guardada exitosamente: " + compra.getNumeroCompra());

            // 4. Recargar tabla y mostrar éxito
            cargarCompras();
            tabPane.getSelectionModel().select(tabPendientes);

            // Mensaje de éxito
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Compra Creada");
            alerta.setHeaderText("✅ Compra registrada exitosamente");

            String mensaje = String.format(
                    "Número de Compra: %s\n" +
                            "Proveedor: %s\n" +
                            "Productos: %d\n" +
                            "Total: %s\n" +
                            "Estado: PENDIENTE\n\n" +
                            "Para actualizar el stock, marque la compra como COMPLETADA\n" +
                            "cuando reciba la mercadería.",
                    compra.getNumeroCompra(),
                    proveedor.getNombre(),
                    productosAgregados,
                    formatoPrecio.format(compra.getTotal())
            );

            alerta.setContentText(mensaje);
            alerta.showAndWait();

            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al guardar compra: " + e.getMessage());
            e.printStackTrace();

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("Error al guardar la compra");
            alerta.setContentText("Detalles: " + e.getMessage());
            alerta.showAndWait();

            return false;
        }
    }

    // ===== UTILIDADES =====

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}