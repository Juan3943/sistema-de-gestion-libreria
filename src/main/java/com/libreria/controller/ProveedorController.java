package com.libreria.controller;

import com.libreria.dao.ProveedorDAO;
import com.libreria.dao.ProductoDAO;
import com.libreria.model.Proveedor;
import com.libreria.model.Producto;
import com.libreria.model.ProductoProveedor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import com.libreria.model.ArchivoProveedor;
import com.libreria.util.FileManager;
import javafx.stage.FileChooser;
import java.io.File;

/**
 * CONTROLADOR PARA GESTIÓN DE PROVEEDORES
 * CONCEPTO: Maneja la interfaz de proveedores y su relación con productos
 * Similar a StockController y ServiciosController
 */
public class ProveedorController {

    // ===== ELEMENTOS DE LA INTERFAZ =====

    // Búsqueda
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnMostrarTodos;
    @FXML private Button btnNuevoProveedor;
    @FXML private Button btnActualizar;

    // Tabla de proveedores
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colCuit;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colEmail;
    @FXML private TableColumn<Proveedor, String> colDireccion;
    @FXML private TableColumn<Proveedor, String> colEstado;
    @FXML private TableColumn<Proveedor, Void> colAcciones;

    // Estadísticas
    @FXML private Label lblTotalProveedores;
    @FXML private Label lblProveedoresActivos;

    // ===== DATOS =====
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ObservableList<Proveedor> proveedoresData = FXCollections.observableArrayList();
    private final DecimalFormat formatoPrecio = new DecimalFormat("$#,##0.00");

    /**
     * CONCEPTO: initialize() - JavaFX llama este método automáticamente
     */
    @FXML
    public void initialize() {
        System.out.println("🏢 Inicializando ProveedorController...");

        configurarTabla();
        cargarProveedores();
        actualizarEstadisticas();

        System.out.println("✅ ProveedorController inicializado correctamente");
    }

    // ===== CONFIGURACIÓN DE TABLA =====

    /**
     * CONCEPTO: Configurar columnas de la tabla principal
     */
    private void configurarTabla() {
        System.out.println("🔧 Configurando tabla de proveedores...");

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCuit.setCellValueFactory(new PropertyValueFactory<>("cuit"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        // CONCEPTO: Columna calculada para estado con colores
        colEstado.setCellFactory(column -> new TableCell<Proveedor, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Proveedor proveedor = getTableRow().getItem();

                    if (proveedor.isActivo()) {
                        setText("ACTIVO");
                        setStyle("-fx-background-color: #e8f5e8; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                    } else {
                        setText("INACTIVO");
                        setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828;");
                    }
                }
            }
        });

        configurarColumnaAcciones();
        tablaProveedores.setItems(proveedoresData);
    }

    /**
     * CONCEPTO: Columna de acciones con botones personalizados
     */
    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(column -> new TableCell<Proveedor, Void>() {
            private final HBox contenedorBotones = new HBox(10);
            private final Button btnEditar = new Button("Editar");
            private final Button btnProductos = new Button("Productos");
            private final Button btnGestionarArchivos = new Button("Archivos");
            private final Button btnInfo = new Button("ℹ️");

            {
                // Tamaños de botones
                btnEditar.setPrefWidth(60);
                btnEditar.setPrefHeight(25);

                btnProductos.setPrefWidth(70);
                btnProductos.setPrefHeight(25);

                btnGestionarArchivos.setPrefWidth(70);
                btnGestionarArchivos.setPrefHeight(25);

                btnInfo.setPrefSize(30, 25);

                // Estilos con colores
                btnEditar.setStyle(
                        "-fx-background-color: #27ae60; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 11px; " +
                                "-fx-font-weight: bold;"
                );

                btnProductos.setStyle(
                        "-fx-background-color: #27ae60; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 11px; " +
                                "-fx-font-weight: bold;"
                );

                btnGestionarArchivos.setStyle(
                        "-fx-background-color: #27ae60; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 11px; " +
                                "-fx-font-weight: bold;"
                );



                // Tooltips descriptivos
                btnEditar.setTooltip(new Tooltip("Editar información del proveedor"));
                btnProductos.setTooltip(new Tooltip("Gestionar productos y precios"));
                btnGestionarArchivos.setTooltip(new Tooltip("Gestionar archivos"));
                btnInfo.setTooltip(new Tooltip("Ver datos completos"));

                btnEditar.setOnAction(event -> {
                    Proveedor proveedor = getTableRow().getItem();
                    if (proveedor != null) {
                        mostrarDialogoEditarProveedor(proveedor);
                    }
                });

                btnProductos.setOnAction(event -> {
                    Proveedor proveedor = getTableRow().getItem();
                    if (proveedor != null) {
                        mostrarDialogoProductosProveedor(proveedor);
                    }
                });

                btnGestionarArchivos.setOnAction(event -> {
                    Proveedor proveedor = getTableRow().getItem();
                    if (proveedor != null) {
                        mostrarDialogoArchivosProveedor(proveedor);
                    }
                });

                btnInfo.setOnAction(event -> {
                    Proveedor proveedor = getTableRow().getItem();
                    if (proveedor != null) {
                        mostrarInfoProveedor(proveedor);
                    }
                });

                contenedorBotones.getChildren().addAll(btnEditar, btnProductos, btnGestionarArchivos, btnInfo);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedorBotones);
            }
        });
    }

    // ===== EVENTOS DE BOTONES =====

    @FXML
    private void buscarProveedores() {
        String textoBusqueda = txtBuscar.getText().trim();

        if (textoBusqueda.isEmpty()) {
            cargarProveedores();
        } else {
            List<Proveedor> todos = proveedorDAO.obtenerProveedores();
            List<Proveedor> filtrados = todos.stream()
                    .filter(p ->
                            p.getNombre().toLowerCase().contains(textoBusqueda.toLowerCase()) ||
                                    (p.getCuit() != null && p.getCuit().contains(textoBusqueda))
                    )
                    .collect(java.util.stream.Collectors.toList());

            proveedoresData.setAll(filtrados);
            actualizarEstadisticas();
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cargarProveedores();
    }

    @FXML
    private void nuevoProveedor() {
        mostrarDialogoNuevoProveedor();
    }

    @FXML
    private void actualizarDatos() {
        cargarProveedores();
        mostrarInfo("Datos actualizados correctamente");
    }

    // ===== LÓGICA DE NEGOCIO =====

    private void cargarProveedores() {
        System.out.println("📦 Cargando proveedores...");

        try {
            List<Proveedor> proveedores = proveedorDAO.obtenerProveedores();
            proveedoresData.setAll(proveedores);
            actualizarEstadisticas();

            System.out.println("✅ " + proveedores.size() + " proveedores cargados");

        } catch (Exception e) {
            System.err.println("❌ Error al cargar proveedores: " + e.getMessage());
            mostrarError("Error al cargar proveedores: " + e.getMessage());
        }
    }

    private void actualizarEstadisticas() {
        int total = proveedoresData.size();

        long activos = proveedoresData.stream()
                .filter(Proveedor::isActivo)
                .count();

        lblTotalProveedores.setText("Total: " + total);
        lblProveedoresActivos.setText("Activos: " + activos);
    }

    // ===== DIÁLOGOS CRUD =====

    /**
     * CONCEPTO: Diálogo para crear nuevo proveedor
     * Similar a los diálogos de Stock y Servicios
     */
    private void mostrarDialogoNuevoProveedor() {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Nuevo Proveedor");
        dialogo.setHeaderText("Complete la información del proveedor");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int fila = 0;

        // Nombre (obligatorio)
        Label lblNombre = new Label("* Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre comercial del proveedor");
        grid.add(lblNombre, 0, fila);
        grid.add(txtNombre, 1, fila++);

        // CUIT (opcional)
        Label lblCuit = new Label("CUIT:");
        TextField txtCuit = new TextField();
        txtCuit.setPromptText("XX-XXXXXXXX-X");
        grid.add(lblCuit, 0, fila);
        grid.add(txtCuit, 1, fila++);

        // Teléfono (opcional)
        Label lblTelefono = new Label("Teléfono:");
        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Teléfono de contacto");
        grid.add(lblTelefono, 0, fila);
        grid.add(txtTelefono, 1, fila++);

        // Email (opcional)
        Label lblEmail = new Label("Email:");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("email@ejemplo.com");
        grid.add(lblEmail, 0, fila);
        grid.add(txtEmail, 1, fila++);

        // Dirección (opcional)
        Label lblDireccion = new Label("Dirección:");
        TextArea txtDireccion = new TextArea();
        txtDireccion.setPrefRowCount(2);
        txtDireccion.setPromptText("Dirección completa");
        grid.add(lblDireccion, 0, fila);
        grid.add(txtDireccion, 1, fila++);

        // Nota
        Label nota = new Label("* Campos obligatorios");
        nota.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c; -fx-font-style: italic;");
        grid.add(nota, 0, fila, 2, 1);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Validación
                if (txtNombre.getText().trim().isEmpty()) {
                    mostrarError("El nombre es obligatorio");
                    return;
                }

                // Crear proveedor
                boolean exito = proveedorDAO.crearProveedor(
                        txtNombre.getText().trim(),
                        txtCuit.getText().trim(),
                        txtTelefono.getText().trim(),
                        txtEmail.getText().trim(),
                        txtDireccion.getText().trim()
                );

                if (exito) {
                    cargarProveedores();
                    mostrarInfo("Proveedor creado correctamente:\n" + txtNombre.getText());
                } else {
                    mostrarError("Error al crear el proveedor");
                }

            } catch (Exception e) {
                mostrarError("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * CONCEPTO: Diálogo para editar proveedor existente
     */
    private void mostrarDialogoEditarProveedor(Proveedor proveedor) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Editar Proveedor");
        dialogo.setHeaderText("Modificar información de: " + proveedor.getNombre());

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int fila = 0;

        // Nombre (editable)
        Label lblNombre = new Label("* Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        TextField txtNombre = new TextField(proveedor.getNombre());
        grid.add(lblNombre, 0, fila);
        grid.add(txtNombre, 1, fila++);

        // CUIT (editable)
        Label lblCuit = new Label("CUIT:");
        TextField txtCuit = new TextField(proveedor.getCuit() != null ? proveedor.getCuit() : "");
        grid.add(lblCuit, 0, fila);
        grid.add(txtCuit, 1, fila++);

        // Teléfono (editable)
        Label lblTelefono = new Label("Teléfono:");
        TextField txtTelefono = new TextField(proveedor.getTelefono() != null ? proveedor.getTelefono() : "");
        grid.add(lblTelefono, 0, fila);
        grid.add(txtTelefono, 1, fila++);

        // Email (editable)
        Label lblEmail = new Label("Email:");
        TextField txtEmail = new TextField(proveedor.getEmail() != null ? proveedor.getEmail() : "");
        grid.add(lblEmail, 0, fila);
        grid.add(txtEmail, 1, fila++);

        // Dirección (editable)
        Label lblDireccion = new Label("Dirección:");
        TextArea txtDireccion = new TextArea(proveedor.getDireccion() != null ? proveedor.getDireccion() : "");
        txtDireccion.setPrefRowCount(2);
        grid.add(lblDireccion, 0, fila);
        grid.add(txtDireccion, 1, fila++);

        // Estado (activar/desactivar)
        Label lblEstado = new Label("Estado:");
        CheckBox checkActivo = new CheckBox("Proveedor activo");
        checkActivo.setSelected(proveedor.isActivo());
        grid.add(lblEstado, 0, fila);
        grid.add(checkActivo, 1, fila++);

        // Nota
        Label nota = new Label("* Campos obligatorios");
        nota.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c; -fx-font-style: italic;");
        grid.add(nota, 0, fila, 2, 1);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button btnOK = (Button) dialogo.getDialogPane().lookupButton(ButtonType.OK);
        btnOK.setText("Actualizar Proveedor");

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Validación
                if (txtNombre.getText().trim().isEmpty()) {
                    mostrarError("El nombre es obligatorio");
                    return;
                }

                // Actualizar proveedor
                boolean exito = proveedorDAO.actualizarProveedor(
                        proveedor.getIdProveedor(),
                        txtNombre.getText().trim(),
                        txtCuit.getText().trim(),
                        txtTelefono.getText().trim(),
                        txtEmail.getText().trim(),
                        txtDireccion.getText().trim()
                );

                // Actualizar estado si cambió
                if (checkActivo.isSelected() != proveedor.isActivo()) {
                    proveedorDAO.cambiarEstado(proveedor.getIdProveedor(), checkActivo.isSelected());
                }

                if (exito) {
                    cargarProveedores();
                    mostrarInfo("Proveedor actualizado correctamente");
                } else {
                    mostrarError("Error al actualizar el proveedor");
                }

            } catch (Exception e) {
                mostrarError("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Mostrar información completa del proveedor
     */
    private void mostrarInfoProveedor(Proveedor proveedor) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información del Proveedor");
        alert.setHeaderText(proveedor.getNombre());

        String info = String.format(
                "ID: %d\n" +
                        "CUIT: %s\n" +
                        "Teléfono: %s\n" +
                        "Email: %s\n" +
                        "Dirección: %s\n" +
                        "Estado: %s\n" +
                        "Fecha de creación: %s",
                proveedor.getIdProveedor(),
                proveedor.getCuit() != null ? proveedor.getCuit() : "No especificado",
                proveedor.getTelefono() != null ? proveedor.getTelefono() : "No especificado",
                proveedor.getEmail() != null ? proveedor.getEmail() : "No especificado",
                proveedor.getDireccion() != null ? proveedor.getDireccion() : "No especificada",
                proveedor.getEstadoTexto(),
                proveedor.getFechaCreacion() != null ? proveedor.getFechaCreacion().toString() : "No disponible"
        );

        alert.setContentText(info);
        alert.showAndWait();
    }

    // ===== GESTIÓN DE PRODUCTOS DEL PROVEEDOR =====

    /**
     * CONCEPTO: Diálogo para gestionar productos de un proveedor
     * Muestra tabla de productos vinculados + botones para agregar/editar
     */
    private void mostrarDialogoProductosProveedor(Proveedor proveedor) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Productos del Proveedor");
        dialogo.setHeaderText("Gestión de productos: " + proveedor.getNombre());

        // Contenedor principal
        VBox contenedor = new VBox(15);
        contenedor.setPadding(new Insets(20));
        contenedor.setPrefWidth(700);
        contenedor.setPrefHeight(500);

        // Tabla de productos del proveedor
        TableView<ProductoProveedor> tablaProductos = new TableView<>();
        ObservableList<ProductoProveedor> productosData = FXCollections.observableArrayList();

        // CONCEPTO: Configurar columnas de la tabla
        TableColumn<ProductoProveedor, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
        colCodigo.setPrefWidth(100);

        TableColumn<ProductoProveedor, String> colNombreProducto = new TableColumn<>("Producto");
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colNombreProducto.setPrefWidth(200);

        TableColumn<ProductoProveedor, String> colCodigoProveedor = new TableColumn<>("Cód. Proveedor");
        colCodigoProveedor.setCellValueFactory(new PropertyValueFactory<>("codigoProveedor"));
        colCodigoProveedor.setPrefWidth(100);

        TableColumn<ProductoProveedor, Double> colPrecioCompra = new TableColumn<>("Precio Compra");
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colPrecioCompra.setPrefWidth(100);

        // Formatear precio
        colPrecioCompra.setCellFactory(column -> new TableCell<ProductoProveedor, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty || precio == null ? null : formatoPrecio.format(precio));
            }
        });

        TableColumn<ProductoProveedor, String> colPrincipal = new TableColumn<>("Principal");
        colPrincipal.setCellValueFactory(new PropertyValueFactory<>("iconoPrincipal"));
        colPrincipal.setPrefWidth(70);
        colPrincipal.setStyle("-fx-alignment: CENTER;");

        // Columna de acciones
        TableColumn<ProductoProveedor, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(100);
        colAcciones.setCellFactory(column -> new TableCell<ProductoProveedor, Void>() {
            private final HBox botones = new HBox(5);
            private final Button btnEditar = new Button("✏️");
            private final Button btnEliminar = new Button("🗑️");

            {
                btnEditar.setPrefSize(30, 25);
                btnEliminar.setPrefSize(30, 25);
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnEditar.setTooltip(new Tooltip("Editar precio"));
                btnEliminar.setTooltip(new Tooltip("Desvincular producto"));

                btnEditar.setOnAction(event -> {
                    ProductoProveedor pp = getTableRow().getItem();
                    if (pp != null) {
                        editarProductoProveedor(proveedor, pp, productosData);
                    }
                });

                btnEliminar.setOnAction(event -> {
                    ProductoProveedor pp = getTableRow().getItem();
                    if (pp != null) {
                        desvincularProducto(proveedor, pp, productosData);
                    }
                });

                botones.getChildren().addAll(btnEditar, btnEliminar);
                botones.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : botones);
            }
        });

        tablaProductos.getColumns().addAll(colCodigo, colNombreProducto, colCodigoProveedor,
                colPrecioCompra, colPrincipal, colAcciones);
        tablaProductos.setItems(productosData);

        // Placeholder cuando no hay productos
        VBox placeholder = new VBox(10);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.getChildren().addAll(
                new Label("📦"),
                new Label("No hay productos vinculados"),
                new Label("Use el botón 'Agregar Producto' para vincular el primero")
        );
        tablaProductos.setPlaceholder(placeholder);

        // Botones de acción
        HBox botonesAccion = new HBox(10);
        botonesAccion.setAlignment(Pos.CENTER_RIGHT);

        Button btnAgregarProducto = new Button("➕ Agregar Producto");
        btnAgregarProducto.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnAgregarProducto.setOnAction(e -> {
            agregarProductoProveedor(proveedor, productosData);
        });

        Button btnActualizar = new Button("🔄 Actualizar");
        btnActualizar.setOnAction(e -> {
            cargarProductosProveedor(proveedor, productosData);
        });

        botonesAccion.getChildren().addAll(btnAgregarProducto, btnActualizar);

        // Agregar elementos al contenedor
        contenedor.getChildren().addAll(
                new Label("Productos vinculados a este proveedor:"),
                tablaProductos,
                botonesAccion
        );

        dialogo.getDialogPane().setContent(contenedor);
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Cargar productos al abrir
        cargarProductosProveedor(proveedor, productosData);

        dialogo.showAndWait();
    }

    // ===== GESTIÓN DE ARCHIVOS =====

    /**
     * CONCEPTO: Diálogo para gestionar archivos de un proveedor
     */
    private void mostrarDialogoArchivosProveedor(Proveedor proveedor) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Archivos del Proveedor");
        dialogo.setHeaderText("Gestión de archivos: " + proveedor.getNombre());

        // Contenedor principal
        VBox contenedor = new VBox(15);
        contenedor.setPadding(new Insets(20));
        contenedor.setPrefWidth(700);
        contenedor.setPrefHeight(500);

        // Tabla de archivos
        TableView<ArchivoProveedor> tablaArchivos = new TableView<>();
        ObservableList<ArchivoProveedor> archivosData = FXCollections.observableArrayList();

        // CONCEPTO: Configurar columnas de la tabla
        TableColumn<ArchivoProveedor, String> colNombre = new TableColumn<>("Archivo");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));
        colNombre.setPrefWidth(250);

        TableColumn<ArchivoProveedor, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colDescripcion.setPrefWidth(200);

        // Permitir editar descripción con doble click
        colDescripcion.setCellFactory(column -> new TableCell<ArchivoProveedor, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    setText(item == null || item.isEmpty() ? "(Sin descripción)" : item);

                    // Doble click para editar
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && !isEmpty()) {
                            ArchivoProveedor archivo = getTableRow().getItem();
                            editarDescripcionArchivo(archivo, archivosData);
                        }
                    });
                }
            }
        });

        TableColumn<ArchivoProveedor, String> colTamanio = new TableColumn<>("Tamaño");
        colTamanio.setCellValueFactory(new PropertyValueFactory<>("tamanioFormateado"));
        colTamanio.setPrefWidth(80);

        TableColumn<ArchivoProveedor, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaFormateada"));
        colFecha.setPrefWidth(100);

        // Columna de acciones
        TableColumn<ArchivoProveedor, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(70);
        colAcciones.setCellFactory(column -> new TableCell<ArchivoProveedor, Void>() {
            private final Button btnEliminar = new Button("🗑️");

            {
                btnEliminar.setPrefSize(30, 25);
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnEliminar.setTooltip(new Tooltip("Eliminar archivo"));

                btnEliminar.setOnAction(event -> {
                    ArchivoProveedor archivo = getTableRow().getItem();
                    if (archivo != null) {
                        eliminarArchivo(proveedor, archivo, archivosData);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
            }
        });

        tablaArchivos.getColumns().addAll(colNombre, colDescripcion, colTamanio, colFecha, colAcciones);
        tablaArchivos.setItems(archivosData);

        // Doble click en fila para abrir archivo
        tablaArchivos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ArchivoProveedor archivoSeleccionado = tablaArchivos.getSelectionModel().getSelectedItem();
                if (archivoSeleccionado != null) {
                    abrirArchivo(archivoSeleccionado);
                }
            }
        });

        // Placeholder cuando no hay archivos
        VBox placeholder = new VBox(10);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.getChildren().addAll(
                new Label("📁"),
                new Label("No hay archivos adjuntos"),
                new Label("Use el botón 'Subir Archivo' para agregar documentos")
        );
        tablaArchivos.setPlaceholder(placeholder);

        // Botones de acción
        HBox botonesAccion = new HBox(10);
        botonesAccion.setAlignment(Pos.CENTER_RIGHT);

        Button btnSubir = new Button("➕ Subir Archivo");
        btnSubir.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnSubir.setOnAction(e -> subirArchivo(proveedor, archivosData));

        Button btnActualizar = new Button("🔄 Actualizar");
        btnActualizar.setOnAction(e -> cargarArchivosProveedor(proveedor, archivosData));

        botonesAccion.getChildren().addAll(btnSubir, btnActualizar);

        // Información
        Label lblInfo = new Label("💡 Doble click en un archivo para abrirlo | Doble click en descripción para editarla");
        lblInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");

        // Agregar elementos al contenedor
        contenedor.getChildren().addAll(
                new Label("Archivos adjuntos (PDF, Excel, CSV, Imágenes):"),
                tablaArchivos,
                botonesAccion,
                lblInfo
        );

        dialogo.getDialogPane().setContent(contenedor);
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Cargar archivos al abrir
        cargarArchivosProveedor(proveedor, archivosData);

        dialogo.showAndWait();
    }

    /**
     * Cargar archivos del proveedor
     */
    private void cargarArchivosProveedor(Proveedor proveedor, ObservableList<ArchivoProveedor> archivosData) {
        System.out.println("📁 Cargando archivos del proveedor: " + proveedor.getNombre());

        try {
            List<ArchivoProveedor> archivos = proveedorDAO.obtenerArchivosProveedor(proveedor.getIdProveedor());
            archivosData.setAll(archivos);

            System.out.println("✅ " + archivos.size() + " archivos cargados");

        } catch (Exception e) {
            System.err.println("❌ Error al cargar archivos: " + e.getMessage());
            mostrarError("Error al cargar archivos: " + e.getMessage());
        }
    }

    /**
     * Subir archivo nuevo
     */
    private void subirArchivo(Proveedor proveedor, ObservableList<ArchivoProveedor> archivosData) {
        // Selector de archivos
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo");

        // Filtros de extensión
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Todos los permitidos", "*.pdf", "*.xlsx", "*.xls", "*.csv", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Excel", "*.xlsx", "*.xls"),
                new FileChooser.ExtensionFilter("CSV", "*.csv"),
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(null);

        if (archivoSeleccionado != null) {
            try {
                // Validar archivo
                FileManager.ValidationResult validacion = FileManager.validarArchivo(archivoSeleccionado);

                if (!validacion.valid) {
                    mostrarError(validacion.message);
                    return;
                }

                // Pedir descripción (opcional)
                TextInputDialog dialogoDescripcion = new TextInputDialog();
                dialogoDescripcion.setTitle("Descripción del Archivo");
                dialogoDescripcion.setHeaderText("Archivo: " + archivoSeleccionado.getName());
                dialogoDescripcion.setContentText("Descripción (opcional):");

                Optional<String> resultadoDesc = dialogoDescripcion.showAndWait();
                String descripcion = resultadoDesc.orElse("");

                // Subir archivo físicamente
                String rutaRelativa = FileManager.subirArchivo(archivoSeleccionado, proveedor.getIdProveedor());

                // Crear objeto para BD
                ArchivoProveedor archivo = new ArchivoProveedor(
                        proveedor.getIdProveedor(),
                        archivoSeleccionado.getName(),
                        rutaRelativa,
                        FileManager.obtenerTipoArchivo(archivoSeleccionado.getName()),
                        archivoSeleccionado.length()
                );
                archivo.setDescripcion(descripcion);

                // Registrar en BD
                boolean exito = proveedorDAO.registrarArchivo(archivo);

                if (exito) {
                    cargarArchivosProveedor(proveedor, archivosData);
                    mostrarInfo("Archivo subido correctamente:\n" + archivoSeleccionado.getName());
                } else {
                    mostrarError("Error al registrar el archivo en la base de datos");
                }

            } catch (Exception e) {
                mostrarError("Error al subir archivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Abrir archivo con aplicación predeterminada
     */
    private void abrirArchivo(ArchivoProveedor archivo) {
        boolean exito = FileManager.abrirArchivo(archivo.getRutaArchivo());

        if (!exito) {
            mostrarError("No se pudo abrir el archivo:\n" + archivo.getNombreArchivo());
        }
    }

    /**
     * Eliminar archivo
     */
    private void eliminarArchivo(Proveedor proveedor, ArchivoProveedor archivo,
                                 ObservableList<ArchivoProveedor> archivosData) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar archivo?");
        confirmacion.setContentText(archivo.getNombreArchivo());

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Eliminar de BD (borrado lógico)
            boolean exitoBD = proveedorDAO.eliminarArchivo(archivo.getIdArchivo());

            // Eliminar archivo físico
            boolean exitoFS = FileManager.eliminarArchivo(archivo.getRutaArchivo());

            if (exitoBD) {
                cargarArchivosProveedor(proveedor, archivosData);

                if (!exitoFS) {
                    mostrarInfo("Archivo eliminado del registro, pero no se pudo eliminar físicamente");
                } else {
                    mostrarInfo("Archivo eliminado correctamente");
                }
            } else {
                mostrarError("Error al eliminar el archivo");
            }
        }
    }

    /**
     * Editar descripción de archivo
     */
    private void editarDescripcionArchivo(ArchivoProveedor archivo,
                                          ObservableList<ArchivoProveedor> archivosData) {
        TextInputDialog dialogo = new TextInputDialog(archivo.getDescripcion());
        dialogo.setTitle("Editar Descripción");
        dialogo.setHeaderText("Archivo: " + archivo.getNombreArchivo());
        dialogo.setContentText("Nueva descripción:");

        Optional<String> resultado = dialogo.showAndWait();

        resultado.ifPresent(nuevaDescripcion -> {
            boolean exito = proveedorDAO.actualizarDescripcionArchivo(
                    archivo.getIdArchivo(),
                    nuevaDescripcion
            );

            if (exito) {
                archivo.setDescripcion(nuevaDescripcion);
                archivosData.set(archivosData.indexOf(archivo), archivo); // Refrescar
                mostrarInfo("Descripción actualizada");
            } else {
                mostrarError("Error al actualizar la descripción");
            }
        });
    }

    /**
     * CONCEPTO: Cargar productos del proveedor en la tabla
     */
    private void cargarProductosProveedor(Proveedor proveedor, ObservableList<ProductoProveedor> productosData) {
        System.out.println("📦 Cargando productos del proveedor: " + proveedor.getNombre());

        try {
            List<ProductoProveedor> productos = proveedorDAO.obtenerProductosProveedor(proveedor.getIdProveedor());
            productosData.setAll(productos);

            System.out.println("✅ " + productos.size() + " productos cargados");

        } catch (Exception e) {
            System.err.println("❌ Error al cargar productos: " + e.getMessage());
            mostrarError("Error al cargar productos: " + e.getMessage());
        }
    }

    /**
     * CONCEPTO: Agregar nuevo producto al proveedor
     */
    private void agregarProductoProveedor(Proveedor proveedor, ObservableList<ProductoProveedor> productosData) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Agregar Producto");
        dialogo.setHeaderText("Vincular producto a: " + proveedor.getNombre());

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int fila = 0;

        // ComboBox de productos disponibles
        Label lblProducto = new Label("* Producto:");
        lblProducto.setStyle("-fx-font-weight: bold;");
        ComboBox<Producto> cbProducto = new ComboBox<>();

        // CONCEPTO: Cargar productos que aún no están vinculados
        try {
            List<Producto> todosProductos = productoDAO.obtenerTodos();
            List<ProductoProveedor> productosVinculados = proveedorDAO.obtenerProductosProveedor(proveedor.getIdProveedor());

            // Filtrar productos ya vinculados
            List<Producto> productosDisponibles = todosProductos.stream()
                    .filter(p -> productosVinculados.stream()
                            .noneMatch(pp -> pp.getCodigoProducto().equals(p.getCodigo())))
                    .collect(java.util.stream.Collectors.toList());

            cbProducto.setItems(FXCollections.observableArrayList(productosDisponibles));
            cbProducto.setPromptText("Seleccione un producto");

            // CONCEPTO: Mostrar nombre del producto en el ComboBox
            cbProducto.setCellFactory(param -> new ListCell<Producto>() {
                @Override
                protected void updateItem(Producto producto, boolean empty) {
                    super.updateItem(producto, empty);
                    setText(empty || producto == null ? null : producto.getNombre());
                }
            });
            cbProducto.setButtonCell(new ListCell<Producto>() {
                @Override
                protected void updateItem(Producto producto, boolean empty) {
                    super.updateItem(producto, empty);
                    setText(empty || producto == null ? null : producto.getNombre());
                }
            });

        } catch (Exception e) {
            mostrarError("Error al cargar productos: " + e.getMessage());
            return;
        }

        grid.add(lblProducto, 0, fila);
        grid.add(cbProducto, 1, fila++);

        // Código del proveedor (opcional)
        Label lblCodigoProveedor = new Label("Código Proveedor:");
        TextField txtCodigoProveedor = new TextField();
        txtCodigoProveedor.setPromptText("Código interno del proveedor");
        grid.add(lblCodigoProveedor, 0, fila);
        grid.add(txtCodigoProveedor, 1, fila++);

        // Precio de compra (obligatorio)
        Label lblPrecioCompra = new Label("* Precio Compra:");
        lblPrecioCompra.setStyle("-fx-font-weight: bold;");
        TextField txtPrecioCompra = new TextField();
        txtPrecioCompra.setPromptText("0.00");
        grid.add(lblPrecioCompra, 0, fila);
        grid.add(txtPrecioCompra, 1, fila++);

        // Marcar como proveedor principal
        Label lblPrincipal = new Label("Proveedor Principal:");
        CheckBox checkPrincipal = new CheckBox("Marcar como proveedor principal");
        grid.add(lblPrincipal, 0, fila);
        grid.add(checkPrincipal, 1, fila++);

        // Nota
        Label nota = new Label("* Campos obligatorios");
        nota.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c; -fx-font-style: italic;");
        grid.add(nota, 0, fila, 2, 1);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Validaciones
                if (cbProducto.getValue() == null) {
                    mostrarError("Debe seleccionar un producto");
                    return;
                }
                if (txtPrecioCompra.getText().trim().isEmpty()) {
                    mostrarError("El precio de compra es obligatorio");
                    return;
                }

                Producto productoSeleccionado = cbProducto.getValue();
                Integer idProducto = proveedorDAO.obtenerIdProductoPorCodigo(productoSeleccionado.getCodigo());

                if (idProducto == null) {
                    mostrarError("No se pudo obtener el ID del producto");
                    return;
                }

                // Vincular producto
                boolean exito = proveedorDAO.vincularProducto(
                        idProducto,
                        proveedor.getIdProveedor(),
                        txtCodigoProveedor.getText().trim(),
                        Double.parseDouble(txtPrecioCompra.getText().trim()),
                        checkPrincipal.isSelected()
                );

                if (exito) {
                    cargarProductosProveedor(proveedor, productosData);
                    mostrarInfo("Producto vinculado correctamente:\n" + productoSeleccionado.getNombre());
                } else {
                    mostrarError("Error al vincular el producto");
                }

            } catch (NumberFormatException e) {
                mostrarError("El precio debe ser un número válido");
            } catch (Exception e) {
                mostrarError("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * CONCEPTO: Editar precio y configuración de producto-proveedor
     */
    private void editarProductoProveedor(Proveedor proveedor, ProductoProveedor pp,
                                         ObservableList<ProductoProveedor> productosData) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Editar Producto");
        dialogo.setHeaderText("Producto: " + pp.getNombreProducto());

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int fila = 0;

        // Código del proveedor
        Label lblCodigoProveedor = new Label("Código Proveedor:");
        TextField txtCodigoProveedor = new TextField(
                pp.getCodigoProveedor() != null ? pp.getCodigoProveedor() : ""
        );
        grid.add(lblCodigoProveedor, 0, fila);
        grid.add(txtCodigoProveedor, 1, fila++);

        // Precio actual
        Label lblPrecioActual = new Label("Precio actual:");
        Label lblValorActual = new Label(formatoPrecio.format(pp.getPrecioCompra()));
        lblValorActual.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        grid.add(lblPrecioActual, 0, fila);
        grid.add(lblValorActual, 1, fila++);

        // Nuevo precio
        Label lblPrecioNuevo = new Label("* Nuevo Precio:");
        lblPrecioNuevo.setStyle("-fx-font-weight: bold;");
        TextField txtPrecioNuevo = new TextField(String.valueOf(pp.getPrecioCompra()));
        grid.add(lblPrecioNuevo, 0, fila);
        grid.add(txtPrecioNuevo, 1, fila++);

        // Marcar como principal
        Label lblPrincipal = new Label("Proveedor Principal:");
        CheckBox checkPrincipal = new CheckBox("Es proveedor principal");
        checkPrincipal.setSelected(pp.isEsPrincipal());
        grid.add(lblPrincipal, 0, fila);
        grid.add(checkPrincipal, 1, fila++);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button btnOK = (Button) dialogo.getDialogPane().lookupButton(ButtonType.OK);
        btnOK.setText("Actualizar");

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                double precioNuevo = Double.parseDouble(txtPrecioNuevo.getText().trim());

                // Actualizar precio
                boolean exitoPrecio = proveedorDAO.actualizarPrecioCompra(
                        pp.getIdProducto(),
                        pp.getIdProveedor(),
                        precioNuevo
                );

                // Actualizar si es principal
                if (checkPrincipal.isSelected() != pp.isEsPrincipal()) {
                    if (checkPrincipal.isSelected()) {
                        proveedorDAO.marcarComoPrincipal(pp.getIdProducto(), pp.getIdProveedor());
                    }
                }

                if (exitoPrecio) {
                    cargarProductosProveedor(proveedor, productosData);
                    mostrarInfo("Producto actualizado correctamente");
                } else {
                    mostrarError("Error al actualizar el producto");
                }

            } catch (NumberFormatException e) {
                mostrarError("El precio debe ser un número válido");
            } catch (Exception e) {
                mostrarError("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * CONCEPTO: Desvincular producto de proveedor (borrado lógico)
     */
    private void desvincularProducto(Proveedor proveedor, ProductoProveedor pp,
                                     ObservableList<ProductoProveedor> productosData) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Desvincular producto del proveedor?");
        confirmacion.setContentText("Producto: " + pp.getNombreProducto() +
                "\nProveedor: " + proveedor.getNombre());

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean exito = proveedorDAO.desvincularProducto(pp.getIdProducto(), pp.getIdProveedor());

            if (exito) {
                cargarProductosProveedor(proveedor, productosData);
                mostrarInfo("Producto desvinculado correctamente");
            } else {
                mostrarError("Error al desvincular el producto");
            }
        }
    }

    // ===== UTILIDADES =====

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ===== COMPARACIÓN DE PRECIOS =====

    /**
     * CONCEPTO: Diálogo principal de comparación de precios
     * Paso 1: Buscar y seleccionar producto
     * Paso 2: Mostrar comparación de todos los proveedores
     */
    /**
     * CONCEPTO: Diálogo principal de comparación de precios
     * Paso 1: Buscar y seleccionar producto
     * Paso 2: Mostrar comparación de todos los proveedores
     */
    @FXML
    private void mostrarComparacionPrecios() {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Comparar Precios de Producto");
        dialogo.setHeaderText("Comparación de precios entre proveedores");

        VBox contenedor = new VBox(15);
        contenedor.setPadding(new Insets(20));
        contenedor.setPrefWidth(650);
        contenedor.setPrefHeight(550);  // Altura fija

        // Sección de búsqueda
        Label lblInstruccion = new Label("Buscar producto por nombre o código:");
        lblInstruccion.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        HBox busquedaBox = new HBox(10);
        busquedaBox.setAlignment(Pos.CENTER_LEFT);

        TextField txtBuscarProducto = new TextField();
        txtBuscarProducto.setPromptText("Ej: Cuaderno, Lapicera, 7790001...");
        txtBuscarProducto.setPrefWidth(400);

        Button btnBuscarProducto = new Button("🔍 Buscar");
        btnBuscarProducto.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        busquedaBox.getChildren().addAll(txtBuscarProducto, btnBuscarProducto);

        // Label de resultados
        Label lblResultados = new Label("Resultados de búsqueda:");
        lblResultados.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        lblResultados.setVisible(false);

        // IMPORTANTE: VBox con configuración correcta
        VBox listaResultados = new VBox(10);  // Más espaciado
        listaResultados.setPadding(new Insets(15));
        listaResultados.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 5;"
        );

        // ScrollPane con altura fija
        ScrollPane scrollResultados = new ScrollPane(listaResultados);
        scrollResultados.setFitToWidth(true);
        scrollResultados.setPrefHeight(250);
        scrollResultados.setMaxHeight(250);
        scrollResultados.setVisible(false);
        scrollResultados.setStyle("-fx-background-color: transparent;");

        ToggleGroup grupoProductos = new ToggleGroup();

        // Botón comparar
        Button btnComparar = new Button("💰 Comparar Precios");
        btnComparar.setStyle(
                "-fx-background-color: #27ae60; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20;"
        );
        btnComparar.setDisable(true);
        btnComparar.setVisible(false);

        // Evento de búsqueda
        btnBuscarProducto.setOnAction(e -> {
            String textoBusqueda = txtBuscarProducto.getText().trim();

            if (textoBusqueda.isEmpty()) {
                mostrarError("Ingrese un texto para buscar");
                return;
            }

            System.out.println("🔍 Buscando: " + textoBusqueda);

            // Buscar productos
            List<Producto> productos = productoDAO.buscarPorCodigoONombre(textoBusqueda);

            System.out.println("📦 Productos encontrados: " + productos.size());

            if (productos.isEmpty()) {
                mostrarInfo("No se encontraron productos con: " + textoBusqueda);
                lblResultados.setVisible(false);
                scrollResultados.setVisible(false);
                btnComparar.setVisible(false);
                listaResultados.getChildren().clear();
            } else {
                // Limpiar resultados anteriores
                listaResultados.getChildren().clear();
                grupoProductos.getToggles().clear();

                // IMPORTANTE: Agregar RadioButtons con estilo visible
                for (Producto producto : productos) {
                    RadioButton rbProducto = new RadioButton();

                    // Texto formateado
                    String textoCompleto = String.format("%s - %s ($%,.2f)",
                            producto.getCodigo(),
                            producto.getNombre(),
                            producto.getPrecio()
                    );

                    rbProducto.setText(textoCompleto);
                    rbProducto.setToggleGroup(grupoProductos);
                    rbProducto.setUserData(producto);

                    // Estilo visible
                    rbProducto.setStyle(
                            "-fx-font-size: 13px; " +
                                    "-fx-text-fill: #2c3e50; " +
                                    "-fx-padding: 8 0;"
                    );

                    // Efecto hover
                    rbProducto.setOnMouseEntered(ev -> {
                        if (!rbProducto.isSelected()) {
                            rbProducto.setStyle(
                                    "-fx-font-size: 13px; " +
                                            "-fx-text-fill: #2c3e50; " +
                                            "-fx-padding: 8 0; " +
                                            "-fx-background-color: #f8f9fa;"
                            );
                        }
                    });

                    rbProducto.setOnMouseExited(ev -> {
                        if (!rbProducto.isSelected()) {
                            rbProducto.setStyle(
                                    "-fx-font-size: 13px; " +
                                            "-fx-text-fill: #2c3e50; " +
                                            "-fx-padding: 8 0;"
                            );
                        }
                    });

                    listaResultados.getChildren().add(rbProducto);

                    System.out.println("✅ RadioButton agregado: " + producto.getNombre());
                }

                // Mostrar elementos
                lblResultados.setText("Resultados encontrados (" + productos.size() + "):");
                lblResultados.setVisible(true);
                scrollResultados.setVisible(true);
                btnComparar.setVisible(true);
                btnComparar.setDisable(true);

                // Habilitar botón cuando se seleccione
                grupoProductos.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                    btnComparar.setDisable(newVal == null);

                    // Resaltar seleccionado
                    if (newVal != null) {
                        RadioButton selected = (RadioButton) newVal;
                        selected.setStyle(
                                "-fx-font-size: 13px; " +
                                        "-fx-text-fill: #2c3e50; " +
                                        "-fx-padding: 8 0; " +
                                        "-fx-background-color: #e3f2fd;"
                        );
                    }
                });

                System.out.println("📋 Lista de resultados actualizada. Children: " + listaResultados.getChildren().size());
            }
        });

        // Búsqueda al presionar Enter
        txtBuscarProducto.setOnAction(e -> btnBuscarProducto.fire());

        // Evento del botón comparar
        btnComparar.setOnAction(e -> {
            Toggle seleccionado = grupoProductos.getSelectedToggle();
            if (seleccionado != null) {
                Producto producto = (Producto) seleccionado.getUserData();
                System.out.println("💰 Comparando precios de: " + producto.getNombre());
                dialogo.close();
                mostrarTablaComparacionPrecios(producto);
            }
        });

        // Agregar elementos al contenedor EN EL ORDEN CORRECTO
        contenedor.getChildren().addAll(
                lblInstruccion,
                busquedaBox,
                lblResultados,
                scrollResultados,
                btnComparar
        );

        dialogo.getDialogPane().setContent(contenedor);
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Hacer que el diálogo sea más grande
        dialogo.setResizable(true);
        dialogo.getDialogPane().setPrefSize(700, 600);

        dialogo.showAndWait();
    }

    /**
     * CONCEPTO: Mostrar tabla de comparación de precios
     * Muestra todos los proveedores que tienen el producto seleccionado
     */
    private void mostrarTablaComparacionPrecios(Producto producto) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Comparación de Precios");
        dialogo.setHeaderText(String.format("Producto: %s - %s", producto.getCodigo(), producto.getNombre()));

        VBox contenedor = new VBox(15);
        contenedor.setPadding(new Insets(20));
        contenedor.setPrefWidth(750);
        contenedor.setPrefHeight(500);

        // Checkbox para filtrar solo activos
        CheckBox checkSoloActivos = new CheckBox("Mostrar solo proveedores activos");
        checkSoloActivos.setSelected(true);
        checkSoloActivos.setStyle("-fx-font-size: 12px;");

        // Tabla de comparación
        TableView<ProductoProveedor> tablaComparacion = new TableView<>();
        ObservableList<ProductoProveedor> comparacionData = FXCollections.observableArrayList();

        // Columna: Proveedor
        TableColumn<ProductoProveedor, String> colProveedor = new TableColumn<>("Proveedor");
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        colProveedor.setPrefWidth(180);

        // Columna: Costo de Compra
        TableColumn<ProductoProveedor, Double> colCosto = new TableColumn<>("Costo Compra");
        colCosto.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colCosto.setPrefWidth(120);
        colCosto.setCellFactory(column -> new TableCell<ProductoProveedor, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(formatoPrecio.format(precio));

                    // CONCEPTO: Resaltar el más barato en verde
                    ProductoProveedor item = getTableRow().getItem();
                    if (item != null && esMasBarato(item, comparacionData)) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Columna: Código Proveedor
        TableColumn<ProductoProveedor, String> colCodigoProveedor = new TableColumn<>("Código Proveedor");
        colCodigoProveedor.setCellValueFactory(new PropertyValueFactory<>("codigoProveedor"));
        colCodigoProveedor.setPrefWidth(150);

        // Columna: Principal
        TableColumn<ProductoProveedor, String> colPrincipal = new TableColumn<>("Principal");
        colPrincipal.setCellValueFactory(new PropertyValueFactory<>("iconoPrincipal"));
        colPrincipal.setPrefWidth(80);
        colPrincipal.setStyle("-fx-alignment: CENTER;");

        // Columna: Acciones (menú contextual)
        TableColumn<ProductoProveedor, Void> colAcciones = new TableColumn<>("");
        colAcciones.setPrefWidth(40);
        colAcciones.setCellFactory(column -> new TableCell<ProductoProveedor, Void>() {
            private final Button btnOpciones = new Button("⋮");
            private final ContextMenu menuOpciones = new ContextMenu();

            {
                // Estilo minimalista
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

                // Items del menú
                MenuItem itemMarcarPrincipal = new MenuItem("⭐ Marcar como principal");
                MenuItem itemEditarCosto = new MenuItem("✎ Editar costo");

                itemMarcarPrincipal.setStyle("-fx-font-size: 12px; -fx-padding: 6 12;");
                itemEditarCosto.setStyle("-fx-font-size: 12px; -fx-padding: 6 12;");

                itemMarcarPrincipal.setOnAction(event -> {
                    ProductoProveedor pp = getTableRow().getItem();
                    if (pp != null && !pp.isEsPrincipal()) {
                        marcarComoPrincipalDesdeComparacion(producto, pp, comparacionData, checkSoloActivos.isSelected());
                    }
                });

                itemEditarCosto.setOnAction(event -> {
                    ProductoProveedor pp = getTableRow().getItem();
                    if (pp != null) {
                        editarCostoDesdeComparacion(producto, pp, comparacionData, checkSoloActivos.isSelected());
                    }
                });

                menuOpciones.getItems().addAll(itemMarcarPrincipal, itemEditarCosto);

                btnOpciones.setOnAction(event -> {
                    javafx.geometry.Bounds bounds = btnOpciones.localToScreen(btnOpciones.getBoundsInLocal());
                    menuOpciones.show(btnOpciones, bounds.getMinX(), bounds.getMaxY());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnOpciones);
            }
        });

        tablaComparacion.getColumns().addAll(colProveedor, colCosto, colCodigoProveedor, colPrincipal, colAcciones);
        tablaComparacion.setItems(comparacionData);

        // Placeholder
        VBox placeholder = new VBox(10);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.getChildren().addAll(
                new Label("💰"),
                new Label("No hay proveedores vinculados a este producto"),
                new Label("Use el botón de abajo para vincular un proveedor")
        );
        tablaComparacion.setPlaceholder(placeholder);

        // Botón: Vincular otro proveedor
        HBox botonesAccion = new HBox(10);
        botonesAccion.setAlignment(Pos.CENTER_LEFT);

        Button btnVincular = new Button("➕ Vincular otro proveedor");
        btnVincular.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnVincular.setOnAction(e -> {
            vincularProveedorDesdeComparacion(producto, comparacionData, checkSoloActivos.isSelected());
        });

        botonesAccion.getChildren().add(btnVincular);

        // Evento del checkbox
        checkSoloActivos.selectedProperty().addListener((obs, oldVal, newVal) -> {
            cargarComparacionPrecios(producto, comparacionData, newVal);
        });

        // Información adicional
        Label lblInfo = new Label("💡 El precio más bajo se muestra resaltado en verde | ⭐ = Proveedor principal");
        lblInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");

        // Agregar elementos al contenedor
        contenedor.getChildren().addAll(
                checkSoloActivos,
                tablaComparacion,
                botonesAccion,
                lblInfo
        );

        dialogo.getDialogPane().setContent(contenedor);
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Cargar datos al abrir
        cargarComparacionPrecios(producto, comparacionData, checkSoloActivos.isSelected());

        dialogo.showAndWait();
    }

    /**
     * Cargar datos de comparación
     */
    private void cargarComparacionPrecios(Producto producto, ObservableList<ProductoProveedor> comparacionData,
                                          boolean soloActivos) {
        System.out.println("💰 Cargando comparación de precios para: " + producto.getNombre());

        try {
            Integer idProducto = proveedorDAO.obtenerIdProductoPorCodigo(producto.getCodigo());

            if (idProducto == null) {
                mostrarError("No se pudo obtener el ID del producto");
                return;
            }

            // Obtener todos los proveedores del producto
            List<ProductoProveedor> todosProveedores = proveedorDAO.obtenerProveedoresDeProducto(idProducto);

            // Filtrar solo activos si es necesario
            List<ProductoProveedor> proveedoresFiltrados;
            if (soloActivos) {
                proveedoresFiltrados = todosProveedores.stream()
                        .filter(pp -> esProveedorActivo(pp.getIdProveedor()))
                        .collect(java.util.stream.Collectors.toList());
            } else {
                proveedoresFiltrados = todosProveedores;
            }

            // Ordenar por precio (más barato primero)
            proveedoresFiltrados.sort((p1, p2) ->
                    Double.compare(p1.getPrecioCompra(), p2.getPrecioCompra())
            );

            comparacionData.setAll(proveedoresFiltrados);

            System.out.println("✅ " + proveedoresFiltrados.size() + " proveedores en comparación");

        } catch (Exception e) {
            System.err.println("❌ Error al cargar comparación: " + e.getMessage());
            mostrarError("Error al cargar comparación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Verificar si es el más barato
     */
    private boolean esMasBarato(ProductoProveedor item, ObservableList<ProductoProveedor> lista) {
        if (lista.isEmpty()) return false;

        double precioMinimo = lista.stream()
                .mapToDouble(ProductoProveedor::getPrecioCompra)
                .min()
                .orElse(Double.MAX_VALUE);

        return item.getPrecioCompra() == precioMinimo;
    }

    /**
     * Verificar si proveedor está activo
     */
    private boolean esProveedorActivo(int idProveedor) {
        List<Proveedor> proveedores = proveedorDAO.obtenerProveedores();
        return proveedores.stream()
                .anyMatch(p -> p.getIdProveedor() == idProveedor && p.isActivo());
    }

    /**
     * Marcar como principal desde comparación
     */
    private void marcarComoPrincipalDesdeComparacion(Producto producto, ProductoProveedor pp,
                                                     ObservableList<ProductoProveedor> comparacionData,
                                                     boolean soloActivos) {
        boolean exito = proveedorDAO.marcarComoPrincipal(pp.getIdProducto(), pp.getIdProveedor());

        if (exito) {
            cargarComparacionPrecios(producto, comparacionData, soloActivos);
            mostrarInfo("Proveedor marcado como principal:\n" + pp.getNombreProveedor());
        } else {
            mostrarError("Error al marcar como principal");
        }
    }

    /**
     * Editar costo desde comparación
     */
    private void editarCostoDesdeComparacion(Producto producto, ProductoProveedor pp,
                                             ObservableList<ProductoProveedor> comparacionData,
                                             boolean soloActivos) {
        TextInputDialog dialogo = new TextInputDialog(String.valueOf(pp.getPrecioCompra()));
        dialogo.setTitle("Editar Costo");
        dialogo.setHeaderText("Proveedor: " + pp.getNombreProveedor());
        dialogo.setContentText("Nuevo costo de compra:");

        Optional<String> resultado = dialogo.showAndWait();

        resultado.ifPresent(nuevoCostoStr -> {
            try {
                double nuevoCosto = Double.parseDouble(nuevoCostoStr);

                if (nuevoCosto <= 0) {
                    mostrarError("El costo debe ser mayor a 0");
                    return;
                }

                boolean exito = proveedorDAO.actualizarPrecioCompra(
                        pp.getIdProducto(),
                        pp.getIdProveedor(),
                        nuevoCosto
                );

                if (exito) {
                    cargarComparacionPrecios(producto, comparacionData, soloActivos);
                    mostrarInfo("Costo actualizado correctamente");
                } else {
                    mostrarError("Error al actualizar el costo");
                }

            } catch (NumberFormatException e) {
                mostrarError("Ingrese un número válido");
            }
        });
    }

    /**
     * Vincular proveedor desde comparación
     */
    private void vincularProveedorDesdeComparacion(Producto producto, ObservableList<ProductoProveedor> comparacionData,
                                                   boolean soloActivos) {
        // Reutilizar el diálogo de agregar producto (adaptado)
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Vincular Proveedor");
        dialogo.setHeaderText("Vincular proveedor a: " + producto.getNombre());

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int fila = 0;

        // ComboBox de proveedores
        Label lblProveedor = new Label("* Proveedor:");
        lblProveedor.setStyle("-fx-font-weight: bold;");
        ComboBox<Proveedor> cbProveedor = new ComboBox<>();

        try {
            List<Proveedor> todosProveedores = proveedorDAO.obtenerProveedores();
            Integer idProducto = proveedorDAO.obtenerIdProductoPorCodigo(producto.getCodigo());

            if (idProducto != null) {
                List<ProductoProveedor> yaVinculados = proveedorDAO.obtenerProveedoresDeProducto(idProducto);

                // Filtrar proveedores no vinculados
                List<Proveedor> disponibles = todosProveedores.stream()
                        .filter(prov -> yaVinculados.stream()
                                .noneMatch(pp -> pp.getIdProveedor() == prov.getIdProveedor()))
                        .filter(Proveedor::isActivo)
                        .collect(java.util.stream.Collectors.toList());

                cbProveedor.setItems(FXCollections.observableArrayList(disponibles));
            }

            cbProveedor.setPromptText("Seleccione un proveedor");

            cbProveedor.setCellFactory(param -> new ListCell<Proveedor>() {
                @Override
                protected void updateItem(Proveedor prov, boolean empty) {
                    super.updateItem(prov, empty);
                    setText(empty || prov == null ? null : prov.getNombre());
                }
            });
            cbProveedor.setButtonCell(new ListCell<Proveedor>() {
                @Override
                protected void updateItem(Proveedor prov, boolean empty) {
                    super.updateItem(prov, empty);
                    setText(empty || prov == null ? null : prov.getNombre());
                }
            });

        } catch (Exception e) {
            mostrarError("Error al cargar proveedores: " + e.getMessage());
            return;
        }

        grid.add(lblProveedor, 0, fila);
        grid.add(cbProveedor, 1, fila++);

        // Código del proveedor
        Label lblCodigo = new Label("Código Proveedor:");
        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código interno del proveedor");
        grid.add(lblCodigo, 0, fila);
        grid.add(txtCodigo, 1, fila++);

        // Costo de compra
        Label lblCosto = new Label("* Costo Compra:");
        lblCosto.setStyle("-fx-font-weight: bold;");
        TextField txtCosto = new TextField();
        txtCosto.setPromptText("0.00");
        grid.add(lblCosto, 0, fila);
        grid.add(txtCosto, 1, fila++);

        // Principal
        CheckBox checkPrincipal = new CheckBox("Marcar como proveedor principal");
        grid.add(new Label(""), 0, fila);
        grid.add(checkPrincipal, 1, fila++);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                if (cbProveedor.getValue() == null) {
                    mostrarError("Debe seleccionar un proveedor");
                    return;
                }
                if (txtCosto.getText().trim().isEmpty()) {
                    mostrarError("El costo es obligatorio");
                    return;
                }

                Proveedor provSeleccionado = cbProveedor.getValue();
                Integer idProducto = proveedorDAO.obtenerIdProductoPorCodigo(producto.getCodigo());

                boolean exito = proveedorDAO.vincularProducto(
                        idProducto,
                        provSeleccionado.getIdProveedor(),
                        txtCodigo.getText().trim(),
                        Double.parseDouble(txtCosto.getText().trim()),
                        checkPrincipal.isSelected()
                );

                if (exito) {
                    cargarComparacionPrecios(producto, comparacionData, soloActivos);
                    mostrarInfo("Proveedor vinculado correctamente:\n" + provSeleccionado.getNombre());
                } else {
                    mostrarError("Error al vincular el proveedor");
                }

            } catch (NumberFormatException e) {
                mostrarError("El costo debe ser un número válido");
            } catch (Exception e) {
                mostrarError("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
