package com.libreria.controller;

import com.libreria.dao.StockDAO;
import com.libreria.model.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;


public class StockController {

    // ===== ELEMENTOS DE LA INTERFAZ =====

    // Búsqueda
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnlimpiarBusqueda;
    @FXML private Button btnSoloStockBajo;

    // Tabla de productos
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Integer> colStockActual;
    @FXML private TableColumn<Producto, Integer> colStockMinimo;
    @FXML private TableColumn<Producto, Double> colPrecioVenta;
    @FXML private TableColumn<Producto, String> colEstadoStock;
    @FXML private TableColumn<Producto, String> colEstadoActivo;
    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private TableColumn<Producto, Void> colAcciones;

    // Estadísticas
    @FXML private Label lblTotalProductos;
    @FXML private Label lblProductosStockBajo;
    @FXML private Label lblProductosSinStock;

    // Botones principales
    @FXML private Button btnNuevoProducto;
    @FXML private Button btnActualizar;

    // ===== DATOS =====
    private final StockDAO stockDAO = new StockDAO();
    private final ObservableList<Producto> productosData = FXCollections.observableArrayList();
    private final DecimalFormat formatoPrecio = new DecimalFormat("$#,##0.00");

    /**
     * CONCEPTO: initialize() - JavaFX llama este método automáticamente
     */
    @FXML
    public void initialize() {
        System.out.println("Inicializando StockController...");

        configurarTabla();
        cargarProductos();
        actualizarEstadisticas();
        configurarFiltroEstado();
        txtBuscar.setOnAction(event -> buscarProductos());

        System.out.println("StockController inicializado correctamente");
    }

    // ===== CONFIGURACIÓN INICIAL =====

    /**
     * CONCEPTO: PropertyValueFactory conecta columnas con atributos del modelo
     */
    private void configurarTabla() {
        System.out.println("Configurando tabla...");

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // CONCEPTO: CellFactory personalizada para formatear precios
        colPrecioVenta.setCellFactory(column -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(formatoPrecio.format(precio));
                }
            }
        });

        // CONCEPTO: Columna calculada para estado de stock con colores
        colEstadoStock.setCellFactory(column -> new TableCell<Producto, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Producto producto = getTableRow().getItem();

                    if (producto.getStock() <= 0) {
                        setText("SIN STOCK");
                        setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                    } else if (producto.getStock() <= producto.getStockMinimo()) {
                        setText("STOCK BAJO");
                        setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #ef6c00; -fx-font-weight: bold;");
                    } else {
                        setText("NORMAL");
                        setStyle("-fx-background-color: #e8f5e8; -fx-text-fill: #2e7d32;");
                    }
                }
            }
        });

        colEstadoActivo.setCellFactory(column -> new TableCell<Producto, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Producto producto = getTableRow().getItem();

                    if (producto.estaActivo()) {
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
        tablaProductos.setItems(productosData);
    }


    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(column -> new TableCell<Producto, Void>() {
            private final Button btnOpciones = new Button("⋮");
            private final ContextMenu menuOpciones = new ContextMenu();

            {
                // Configurar botón MINIMALISTA
                btnOpciones.setPrefSize(25, 25);  // Más pequeño
                btnOpciones.setStyle(
                        "-fx-background-color: transparent; " +  // Sin fondo
                                "-fx-text-fill: #1a1a1a; " +             // Gris suave
                                "-fx-font-size: 20px; " +                // Icono grande
                                "-fx-cursor: hand; " +                   // Manita al pasar mouse
                                "-fx-padding: 0;"                        // Sin padding
                );

                // Efecto hover (cambiar color al pasar mouse)
                btnOpciones.setOnMouseEntered(e -> {
                    btnOpciones.setStyle(
                            "-fx-background-color: #ecf0f1; " +  // Fondo gris claro al hover
                                    "-fx-text-fill: #000000; " +         // Color más oscuro
                                    "-fx-font-size: 20px; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-padding: 0; " +
                                    "-fx-background-radius: 3;"          // Bordes redondeados
                    );
                });

                btnOpciones.setOnMouseExited(e -> {
                    btnOpciones.setStyle(
                            "-fx-background-color: transparent; " +
                                    "-fx-text-fill: #1a1a1a; " +
                                    "-fx-font-size: 20px; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-padding: 0;"
                    );
                });

                // Crear items del menú
                MenuItem itemEditar = new MenuItem("Editar producto");
                MenuItem itemAjustar = new MenuItem("Ajustar stock");
                MenuItem itemInfo = new MenuItem("Ver información");
                MenuItem itemEstado = new MenuItem("Cambiar Estado");

                // Estilos de los items del menú
                itemEditar.setStyle("-fx-font-size: 12px;");
                itemAjustar.setStyle("-fx-font-size: 12px;");
                itemInfo.setStyle("-fx-font-size: 12px;");
                itemEstado.setStyle("-fx-font-size: 12px;");


                // Eventos de los items
                itemEditar.setOnAction(event -> {
                    Producto producto = getTableRow().getItem();
                    if (producto != null) {
                        mostrarDialogoEditarProducto(producto);
                    }
                });

                itemAjustar.setOnAction(event -> {
                    Producto producto = getTableRow().getItem();
                    if (producto != null) {
                        mostrarDialogoAjusteStock(producto);
                    }
                });

                itemInfo.setOnAction(event -> {
                    Producto producto = getTableRow().getItem();
                    if (producto != null) {
                        mostrarInfoProducto(producto);
                    }
                });

                itemEstado.setOnAction(event -> {
                    Producto producto = getTableRow().getItem();
                    if (producto != null) {
                        cambiarEstadoProducto(producto);
                    }
                });

                // Agregar items al menú
                menuOpciones.getItems().addAll(itemEditar, itemAjustar, itemInfo, itemEstado);

                // Mostrar menú al hacer clic en el botón
                btnOpciones.setOnAction(event -> {
                    // Obtener la posición del botón en pantalla
                    javafx.geometry.Bounds bounds = btnOpciones.localToScreen(btnOpciones.getBoundsInLocal());

                    // Mostrar menú debajo del botón
                    menuOpciones.show(btnOpciones, bounds.getMinX(), bounds.getMaxY());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnOpciones);
            }
        });
    }

    // ===== EVENTOS DE BOTONES =====

    @FXML
    private void buscarProductos() {
        String textoBusqueda = txtBuscar.getText().trim();

        List<Producto> todos = stockDAO.obtenerProductosParaStock();

        // Aplicar AMBOS filtros: búsqueda + estado
        List<Producto> filtrados = todos.stream()
                .filter(p -> {
                    // Filtro de búsqueda
                    boolean coincideBusqueda = textoBusqueda.isEmpty() ||
                            p.getCodigo().toLowerCase().contains(textoBusqueda.toLowerCase()) ||
                            p.getNombre().toLowerCase().contains(textoBusqueda.toLowerCase());

                    // Filtro de estado
                    String filtroEstado = cbFiltroEstado.getValue();
                    boolean coincideEstado;
                    switch (filtroEstado) {
                        case "Solo Activos":
                            coincideEstado = p.estaActivo();
                            break;
                        case "Solo Inactivos":
                            coincideEstado = !p.estaActivo();
                            break;
                        default: // "Todos"
                            coincideEstado = true;
                    }

                    return coincideBusqueda && coincideEstado;
                })
                .collect(java.util.stream.Collectors.toList());

        productosData.setAll(filtrados);
        actualizarEstadisticas();
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
        cargarProductos();
        aplicarFiltros();
    }

    @FXML
    private void mostrarSoloStockBajo() {
        String filtroEstado = cbFiltroEstado.getValue();

        List<Producto> stockBajo = stockDAO.obtenerProductosStockBajo();

        // Aplicar también el filtro de estado activo/inactivo
        List<Producto> filtrados = stockBajo.stream()
                .filter(p -> {
                    switch (filtroEstado) {
                        case "Solo Activos":
                            return p.estaActivo();
                        case "Solo Inactivos":
                            return !p.estaActivo();
                        default: // "Todos"
                            return true;
                    }
                })
                .collect(java.util.stream.Collectors.toList());

        productosData.setAll(filtrados);
        actualizarEstadisticas();
    }

    @FXML
    private void nuevoProducto() {
        mostrarDialogoNuevoProductoSimple();
    }

    @FXML
    private void actualizarDatos() {
        cargarProductos();
        mostrarInfo("Datos actualizados correctamente");
    }

    // ===== LÓGICA DE NEGOCIO =====

    private void cargarProductos() {
        System.out.println("Cargando productos...");

        try {
            List<Producto> productos = stockDAO.obtenerProductosParaStock();
            productosData.setAll(productos);
            actualizarEstadisticas();

            System.out.println(productos.size() + " productos cargados");

        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            mostrarError("Error al cargar productos: " + e.getMessage());
        }
    }

    private void actualizarEstadisticas() {
        int total = productosData.size();

        long stockBajo = productosData.stream()
                .filter(p -> p.getStock() > 0 && p.getStock() <= p.getStockMinimo())
                .count();

        long sinStock = productosData.stream()
                .filter(p -> p.getStock() <= 0)
                .count();

        lblTotalProductos.setText("Total: " + total);
        lblProductosStockBajo.setText("Stock bajo: " + stockBajo);
        lblProductosSinStock.setText("Sin stock: " + sinStock);
    }

    // ===== DIÁLOGOS =====


    private void mostrarDialogoNuevoProductoSimple() {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Nuevo Producto");
        dialogo.setHeaderText("Complete la información del producto");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int fila = 0;

        // Código de barras (obligatorio)
        Label lblCodigo = new Label("* Código:");
        lblCodigo.setStyle("-fx-font-weight: bold;");
        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código de barras único");
        grid.add(lblCodigo, 0, fila);
        grid.add(txtCodigo, 1, fila++);

        // Nombre (obligatorio)
        Label lblNombre = new Label("* Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del producto");
        grid.add(lblNombre, 0, fila);
        grid.add(txtNombre, 1, fila++);

        // Descripción (opcional)
        Label lblDescripcion = new Label("Descripción:");
        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPrefRowCount(2);
        txtDescripcion.setPromptText("Descripción del producto");
        grid.add(lblDescripcion, 0, fila);
        grid.add(txtDescripcion, 1, fila++);

        // CONCEPTO: ComboBox con objetos OpcionCombo
        // Categoría (obligatorio)
        Label lblCategoria = new Label("* Categoría:");
        lblCategoria.setStyle("-fx-font-weight: bold;");
        ComboBox<StockDAO.OpcionCombo> cbCategoria = new ComboBox<>();
        configurarComboBoxCategorias(cbCategoria);
        grid.add(lblCategoria, 0, fila);
        grid.add(cbCategoria, 1, fila++);

        // Marca (opcional)
        Label lblMarca = new Label("Marca:");
        ComboBox<StockDAO.OpcionCombo> cbMarca = new ComboBox<>();
        configurarComboBoxMarcas(cbMarca);
        grid.add(lblMarca, 0, fila);
        grid.add(cbMarca, 1, fila++);

        // Tipo de producto
        Label lblTipo = new Label("* Tipo:");
        lblTipo.setStyle("-fx-font-weight: bold;");
        ComboBox<String> cbTipoProducto = new ComboBox<>();
        cbTipoProducto.getItems().addAll("FISICO", "SERVICIO");
        cbTipoProducto.setValue("FISICO");
        grid.add(lblTipo, 0, fila);
        grid.add(cbTipoProducto, 1, fila++);

        // Precio costo
        Label lblPrecioCosto = new Label("Precio costo:");
        TextField txtPrecioCosto = new TextField("0.00");
        txtPrecioCosto.setPromptText("Precio de compra");
        grid.add(lblPrecioCosto, 0, fila);
        grid.add(txtPrecioCosto, 1, fila++);

        // Precio venta (obligatorio)
        Label lblPrecioVenta = new Label("* Precio venta:");
        lblPrecioVenta.setStyle("-fx-font-weight: bold;");
        TextField txtPrecioVenta = new TextField();
        txtPrecioVenta.setPromptText("Precio de venta");
        grid.add(lblPrecioVenta, 0, fila);
        grid.add(txtPrecioVenta, 1, fila++);

        // Stock inicial
        Label lblStockActual = new Label("Stock inicial:");
        TextField txtStockActual = new TextField("0");
        txtStockActual.setPromptText("Cantidad inicial");
        grid.add(lblStockActual, 0, fila);
        grid.add(txtStockActual, 1, fila++);

        // Stock mínimo
        Label lblStockMinimo = new Label("Stock mínimo:");
        TextField txtStockMinimo = new TextField("5");
        txtStockMinimo.setPromptText("Nivel de alerta");
        grid.add(lblStockMinimo, 0, fila);
        grid.add(txtStockMinimo, 1, fila++);

        // Nota
        Label nota = new Label("* Campos obligatorios");
        nota.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c; -fx-font-style: italic;");
        grid.add(nota, 0, fila, 2, 1);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Validaciones básicas
                if (txtCodigo.getText().trim().isEmpty()) {
                    mostrarError("El código de barras es obligatorio");
                    return;
                }
                if (txtNombre.getText().trim().isEmpty()) {
                    mostrarError("El nombre es obligatorio");
                    return;
                }
                if (cbCategoria.getValue() == null) {
                    mostrarError("Debe seleccionar una categoría");
                    return;
                }
                if (txtPrecioVenta.getText().trim().isEmpty()) {
                    mostrarError("El precio de venta es obligatorio");
                    return;
                }

                // CONCEPTO: Obtener IDs de forma simple
                StockDAO.OpcionCombo categoria = obtenerCategoriaSeleccionada(cbCategoria);
                StockDAO.OpcionCombo marca = obtenerMarcaSeleccionada(cbMarca);

                if (categoria == null) {
                    mostrarError("Debe seleccionar una categoría válida");
                    return;
                }

                int idCategoria = categoria.getId();
                Integer idMarca = (marca != null && marca.getId() != 0) ? marca.getId() : null;

                // Crear producto
                boolean exito = stockDAO.crearProductoSimple(
                        txtCodigo.getText().trim(),
                        txtNombre.getText().trim(),
                        txtDescripcion.getText().trim(),
                        idCategoria,
                        idMarca,
                        cbTipoProducto.getValue(),
                        Double.parseDouble(txtPrecioCosto.getText()),
                        Double.parseDouble(txtPrecioVenta.getText()),
                        Integer.parseInt(txtStockActual.getText()),
                        Integer.parseInt(txtStockMinimo.getText())
                );

                if (exito) {
                    cargarProductos();
                    mostrarInfo("Producto creado correctamente:\n" + txtNombre.getText());
                } else {
                    mostrarError("Error al crear el producto");
                }

            } catch (NumberFormatException e) {
                mostrarError("Verifique que los valores numéricos sean correctos");
            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());
            } catch (Exception e) {
                mostrarError("Error inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void configurarComboBoxCategorias(ComboBox<StockDAO.OpcionCombo> comboBox) {
        try {
            List<StockDAO.OpcionCombo> todasCategorias = stockDAO.obtenerCategorias();

            // AGREGAR StringConverter para manejar conversión String <-> OpcionCombo
            comboBox.setConverter(new javafx.util.StringConverter<StockDAO.OpcionCombo>() {
                @Override
                public String toString(StockDAO.OpcionCombo opcion) {
                    return opcion != null ? opcion.getNombre() : "";
                }

                @Override
                public StockDAO.OpcionCombo fromString(String texto) {
                    if (texto == null || texto.trim().isEmpty()) {
                        return null;
                    }

                    for (StockDAO.OpcionCombo cat : todasCategorias) {
                        if (cat.getNombre().equalsIgnoreCase(texto.trim())) {
                            return cat;
                        }
                    }
                    return null;
                }
            });

            comboBox.setEditable(true);
            comboBox.setItems(FXCollections.observableArrayList(todasCategorias));
            comboBox.setPromptText("Seleccione una categoría");

        } catch (Exception e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
            mostrarError("Error al cargar categorías");
        }
    }


    private void configurarComboBoxMarcas(ComboBox<StockDAO.OpcionCombo> comboBox) {
        try {
            List<StockDAO.OpcionCombo> todasMarcas = stockDAO.obtenerMarcas();
            ObservableList<StockDAO.OpcionCombo> itemsOriginales = FXCollections.observableArrayList(todasMarcas);

            comboBox.setEditable(true);
            comboBox.setItems(itemsOriginales);
            comboBox.setValue(todasMarcas.get(0)); // "Sin marca" por defecto
            comboBox.setPromptText("Escriba para buscar marca...");

            TextField editor = comboBox.getEditor();
            editor.textProperty().addListener((observable, valorAnterior, valorNuevo) -> {

                if (!comboBox.isShowing()) {
                    return;
                }

                String filtro = valorNuevo.toLowerCase().trim();

                if (filtro.isEmpty()) {
                    comboBox.setItems(itemsOriginales);
                } else {
                    ObservableList<StockDAO.OpcionCombo> itemsFiltrados = FXCollections.observableArrayList();

                    for (StockDAO.OpcionCombo marca : todasMarcas) {
                        if (marca.getNombre().toLowerCase().contains(filtro)) {
                            itemsFiltrados.add(marca);
                        }
                    }

                    comboBox.setItems(itemsFiltrados);
                }
            });

            comboBox.setOnAction(event -> {
                StockDAO.OpcionCombo seleccionada = comboBox.getValue();
                if (seleccionada != null) {
                    editor.setText(seleccionada.getNombre());
                    comboBox.hide();
                }
            });

            editor.setOnMouseClicked(event -> {
                if (!comboBox.isShowing()) {
                    comboBox.show();
                }
            });

            System.out.println("ComboBox marcas configurado: " + todasMarcas.size() + " opciones");

        } catch (Exception e) {
            System.err.println("Error al configurar marcas: " + e.getMessage());
            mostrarError("Error al cargar marcas");
            e.printStackTrace();
        }
    }

    private StockDAO.OpcionCombo obtenerCategoriaSeleccionada(ComboBox<StockDAO.OpcionCombo> comboBox) {
        try {
            StockDAO.OpcionCombo seleccionada = comboBox.getValue();

            if (seleccionada != null) {
                return seleccionada;
            }

            // Si no hay selección pero hay texto, buscar coincidencia exacta
            String texto = comboBox.getEditor().getText().trim();
            if (!texto.isEmpty()) {
                List<StockDAO.OpcionCombo> categorias = stockDAO.obtenerCategorias();
                for (StockDAO.OpcionCombo categoria : categorias) {
                    if (categoria.getNombre().equalsIgnoreCase(texto)) {
                        return categoria;
                    }
                }
            }

            return null;

        } catch (ClassCastException e) {
            // Capturar el error de casting String -> OpcionCombo
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener categoría: " + e.getMessage());
            return null;
        }
    }

    private StockDAO.OpcionCombo obtenerMarcaSeleccionada(ComboBox<StockDAO.OpcionCombo> comboBox) {
        try {
            StockDAO.OpcionCombo seleccionada = comboBox.getValue();

            if (seleccionada != null) {
                return seleccionada;
            }

            String texto = comboBox.getEditor().getText().trim();
            if (texto.isEmpty()) {
                // Texto vacío = "Sin marca" está bien
                return stockDAO.obtenerMarcas().get(0);
            }

            // Buscar coincidencia exacta
            List<StockDAO.OpcionCombo> marcas = stockDAO.obtenerMarcas();
            for (StockDAO.OpcionCombo marca : marcas) {
                if (marca.getNombre().equalsIgnoreCase(texto)) {
                    return marca;
                }
            }

            // CAMBIO: Si escribió algo que no existe, retornar null (como categoría)
            return null;

        } catch (Exception e) {
            return null;
        }
    }


    private void configurarComboBoxCategoriasParaEdicion(ComboBox<StockDAO.OpcionCombo> comboBox,
                                                         String categoriaActual) {
        configurarComboBoxCategorias(comboBox);

        if (categoriaActual != null) {
            try {
                List<StockDAO.OpcionCombo> categorias = stockDAO.obtenerCategorias();
                for (StockDAO.OpcionCombo categoria : categorias) {
                    if (categoria.getNombre().equals(categoriaActual)) {
                        comboBox.setValue(categoria);
                        comboBox.getEditor().setText(categoria.getNombre());
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al pre-seleccionar categoría: " + e.getMessage());
            }
        }
    }

    private void configurarComboBoxMarcasParaEdicion(ComboBox<StockDAO.OpcionCombo> comboBox,
                                                     String marcaActual) {
        configurarComboBoxMarcas(comboBox);

        if (marcaActual != null && !marcaActual.trim().isEmpty()) {
            try {
                List<StockDAO.OpcionCombo> marcas = stockDAO.obtenerMarcas();
                for (StockDAO.OpcionCombo marca : marcas) {
                    if (marca.getNombre().equals(marcaActual)) {
                        comboBox.setValue(marca);
                        comboBox.getEditor().setText(marca.getNombre());
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al pre-seleccionar marca: " + e.getMessage());
            }
        }
    }


    private int obtenerIdCategoria(StockDAO.OpcionCombo categoriaSeleccionada) {
        if (categoriaSeleccionada == null) {
            throw new IllegalArgumentException("Debe seleccionar una categoría");
        }
        return categoriaSeleccionada.getId();
    }

    private Integer obtenerIdMarca(StockDAO.OpcionCombo marcaSeleccionada) {
        if (marcaSeleccionada == null || marcaSeleccionada.getId() == 0) {
            return null; // "Sin marca" = NULL en BD
        }
        return marcaSeleccionada.getId();
    }

    private void mostrarDialogoAjusteStock(Producto producto) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Ajustar Stock");
        dialogo.setHeaderText("Producto: " + producto.getNombre() + "\nStock actual: " + producto.getStock());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> cbTipoMovimiento = new ComboBox<>();
        cbTipoMovimiento.getItems().addAll("Entrada (+)", "Salida (-)");
        cbTipoMovimiento.setValue("Entrada (+)");

        TextField txtCantidad = new TextField();
        txtCantidad.setPromptText("Cantidad");

        TextField txtMotivo = new TextField();
        txtMotivo.setPromptText("Motivo del ajuste");

        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(cbTipoMovimiento, 1, 0);
        grid.add(new Label("Cantidad:"), 0, 1);
        grid.add(txtCantidad, 1, 1);
        grid.add(new Label("Motivo:"), 0, 2);
        grid.add(txtMotivo, 1, 2);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                int cantidad = Integer.parseInt(txtCantidad.getText());
                String motivo = txtMotivo.getText().trim();

                if (motivo.isEmpty()) {
                    mostrarError("El motivo es obligatorio");
                    return;
                }

                if (cantidad <= 0) {
                    mostrarError("La cantidad debe ser mayor a 0");
                    return;
                }

                boolean esEntrada = cbTipoMovimiento.getValue().startsWith("Entrada");
                int cantidadFinal = esEntrada ? cantidad : -cantidad;

                // AGREGAR VALIDACIÓN: No permitir stock negativo
                int stockResultante = producto.getStock() + cantidadFinal;
                if (stockResultante < 0) {
                    mostrarError("No se puede realizar la salida.\n" +
                            "Stock actual: " + producto.getStock() + "\n" +
                            "Cantidad solicitada: " + cantidad + "\n" +
                            "El stock quedaría en: " + stockResultante + " (negativo)");
                    return;
                }

                boolean exito = stockDAO.ajustarStock(producto.getCodigo(), cantidadFinal, motivo);

                if (exito) {
                    aplicarFiltros();
                    mostrarInfo("Stock ajustado correctamente");
                } else {
                    mostrarError("Error al ajustar stock");
                }

            } catch (NumberFormatException e) {
                mostrarError("La cantidad debe ser un número válido");
            }
        }
    }

    private void mostrarDialogoEditarProducto(Producto producto) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Editar Producto");
        dialogo.setHeaderText("Modificar información de: " + producto.getNombre());

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int fila = 0;

        // Código (SOLO LECTURA - no se puede cambiar)
        Label lblCodigo = new Label("Código:");
        TextField txtCodigo = new TextField(producto.getCodigo());
        txtCodigo.setDisable(true); // CONCEPTO: Campo no editable
        txtCodigo.setStyle("-fx-background-color: #f0f0f0;");
        grid.add(lblCodigo, 0, fila);
        grid.add(txtCodigo, 1, fila++);

        // Nombre (editable)
        Label lblNombre = new Label("* Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        TextField txtNombre = new TextField(producto.getNombre());
        grid.add(lblNombre, 0, fila);
        grid.add(txtNombre, 1, fila++);

        // Descripción (editable)
        Label lblDescripcion = new Label("Descripción:");
        TextArea txtDescripcion = new TextArea(
                producto.getDescripcion() != null ? producto.getDescripcion() : ""
        );
        txtDescripcion.setPrefRowCount(2);
        grid.add(lblDescripcion, 0, fila);
        grid.add(txtDescripcion, 1, fila++);

        // Categoría (editable)
        Label lblCategoria = new Label("* Categoría:");
        lblCategoria.setStyle("-fx-font-weight: bold;");
        ComboBox<StockDAO.OpcionCombo> cbCategoria = new ComboBox<>();
        configurarComboBoxCategoriasParaEdicion(cbCategoria, producto.getCategoria());
        grid.add(lblCategoria, 0, fila);
        grid.add(cbCategoria, 1, fila++);

        // Marca (editable)
        Label lblMarca = new Label("Marca:");
        ComboBox<StockDAO.OpcionCombo> cbMarca = new ComboBox<>();
        configurarComboBoxMarcasParaEdicion(cbMarca, producto.getMarca());
        grid.add(lblMarca, 0, fila);
        grid.add(cbMarca, 1, fila++);

        // Tipo (editable)
        Label lblTipo = new Label("* Tipo:");
        lblTipo.setStyle("-fx-font-weight: bold;");
        ComboBox<String> cbTipoProducto = new ComboBox<>();
        cbTipoProducto.getItems().addAll("FISICO", "SERVICIO");
        cbTipoProducto.setValue(producto.getTipoProducto());
        grid.add(lblTipo, 0, fila);
        grid.add(cbTipoProducto, 1, fila++);

        // Precio costo (editable)
        Label lblPrecioCosto = new Label("Precio costo:");
        TextField txtPrecioCosto = new TextField(String.valueOf(producto.getPrecioCosto()));
        grid.add(lblPrecioCosto, 0, fila);
        grid.add(txtPrecioCosto, 1, fila++);

        // Precio venta (editable)
        Label lblPrecioVenta = new Label("* Precio venta:");
        lblPrecioVenta.setStyle("-fx-font-weight: bold;");
        TextField txtPrecioVenta = new TextField(String.valueOf(producto.getPrecio()));
        grid.add(lblPrecioVenta, 0, fila);
        grid.add(txtPrecioVenta, 1, fila++);

        // Stock actual (SOLO LECTURA - se maneja con otro botón)
        Label lblStockActual = new Label("Stock actual:");
        TextField txtStockActual = new TextField(String.valueOf(producto.getStock()));
        txtStockActual.setDisable(true);
        txtStockActual.setStyle("-fx-background-color: #f0f0f0;");
        Label lblNota = new Label("(Use el botón 📦 para ajustar stock)");
        lblNota.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        VBox stockContainer = new VBox(2, txtStockActual, lblNota);
        grid.add(lblStockActual, 0, fila);
        grid.add(stockContainer, 1, fila++);

        // Stock mínimo (editable)
        Label lblStockMinimo = new Label("Stock mínimo:");
        TextField txtStockMinimo = new TextField(String.valueOf(producto.getStockMinimo()));
        grid.add(lblStockMinimo, 0, fila);
        grid.add(txtStockMinimo, 1, fila++);

        // Nota
        Label nota = new Label("* Campos obligatorios");
        nota.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c; -fx-font-style: italic;");
        grid.add(nota, 0, fila, 2, 1);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // CONCEPTO: Cambiar texto del botón OK
        Button btnOK = (Button) dialogo.getDialogPane().lookupButton(ButtonType.OK);
        btnOK.setText("Actualizar Producto");

        // Procesar resultado
        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Validaciones
                if (txtNombre.getText().trim().isEmpty()) {
                    mostrarError("El nombre es obligatorio");
                    return;
                }
                if (cbCategoria.getValue() == null) {
                    mostrarError("Debe seleccionar una categoría");
                    return;
                }

                StockDAO.OpcionCombo categoria = obtenerCategoriaSeleccionada(cbCategoria);
                StockDAO.OpcionCombo marca = obtenerMarcaSeleccionada(cbMarca);

                if (categoria == null) {
                    mostrarError("Debe seleccionar una categoría válida");
                    return;
                }

                int idCategoria = categoria.getId();
                Integer idMarca = (marca != null && marca.getId() != 0) ? marca.getId() : null;

                // Actualizar en BD
                boolean exito = stockDAO.actualizarProducto(
                        producto.getCodigo(), // Código original (no cambia)
                        txtNombre.getText().trim(),
                        txtDescripcion.getText().trim(),
                        idCategoria,
                        idMarca,
                        cbTipoProducto.getValue(),
                        Double.parseDouble(txtPrecioCosto.getText()),
                        Double.parseDouble(txtPrecioVenta.getText()),
                        Integer.parseInt(txtStockMinimo.getText())
                );

                if (exito) {
                    cargarProductos(); // Recargar tabla
                    mostrarInfo("Producto actualizado correctamente");
                } else {
                    mostrarError("Error al actualizar el producto");
                }

            } catch (NumberFormatException e) {
                mostrarError("Verifique que los valores numéricos sean correctos");
            } catch (Exception e) {
                mostrarError("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void mostrarInfoProducto(Producto producto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información del Producto");
        alert.setHeaderText(producto.getNombre());

        String info = String.format(
                "Código: %s\n" +
                        "Categoría: %s\n" +
                        "Marca: %s\n" +
                        "Stock actual: %d\n" +
                        "Stock mínimo: %d\n" +
                        "Precio costo: %s\n" +
                        "Precio venta: %s\n" +
                        "Tipo: %s",
                producto.getCodigo(),
                producto.getCategoria() != null ? producto.getCategoria() : "Sin categoría",
                producto.getMarca() != null ? producto.getMarca() : "Sin marca",
                producto.getStock(),
                producto.getStockMinimo(),
                formatoPrecio.format(producto.getPrecioCosto()),
                formatoPrecio.format(producto.getPrecio()),
                producto.getTipoProducto()
        );

        alert.setContentText(info);
        alert.showAndWait();
    }

    public void abrirDialogoNuevoProducto() {
        // Usar Platform.runLater para asegurar que la UI esté cargada
        javafx.application.Platform.runLater(() -> {
            mostrarDialogoNuevoProductoSimple();
        });
    }

    private void configurarFiltroEstado() {
        cbFiltroEstado.setItems(FXCollections.observableArrayList(
                "Todos", "Solo Activos", "Solo Inactivos"
        ));
        cbFiltroEstado.setValue("Solo Activos"); // Por defecto mostrar solo activos

        cbFiltroEstado.setOnAction(event -> aplicarFiltros());

        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String filtroEstado = cbFiltroEstado.getValue();

        List<Producto> todos = stockDAO.obtenerProductosParaStock();
        List<Producto> filtrados = todos.stream()
                .filter(p -> {
                    switch (filtroEstado) {
                        case "Solo Activos":
                            return p.estaActivo();
                        case "Solo Inactivos":
                            return !p.estaActivo();
                        default: // "Todos"
                            return true;
                    }
                })
                .collect(java.util.stream.Collectors.toList());

        productosData.setAll(filtrados);
        actualizarEstadisticas();
    }

    private void cambiarEstadoProducto(Producto producto) {
        boolean nuevoEstado = !producto.estaActivo();
        String accion = nuevoEstado ? "activar" : "desactivar";

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar acción");
        confirmacion.setHeaderText("¿Está seguro?");
        confirmacion.setContentText("¿Desea " + accion + " el producto:\n" + producto.getNombre() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean exito = stockDAO.cambiarEstadoProducto(producto.getCodigo(), nuevoEstado);

            if (exito) {
                aplicarFiltros(); // Recargar con filtros
                mostrarInfo("Producto " + (nuevoEstado ? "activado" : "desactivado") + " correctamente");
            } else {
                mostrarError("Error al cambiar el estado del producto");
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
}