package com.libreria.controller;

import com.libreria.dao.ServiciosDAO;
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

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

/**
 * CONTROLADOR PARA GESTIÓN DE SERVICIOS
 * CONCEPTO: Maneja solo productos tipo SERVICIO (sin stock)
 */
public class ServiciosController {

    // Búsqueda
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiarBusqueda;
    @FXML private Button btnNuevoServicio;
    @FXML private Button btnActualizar;

    // Tabla de servicios
    @FXML private TableView<Producto> tablaServicios;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, String> colEstado;
    @FXML private TableColumn<Producto, Void> colAcciones;
    @FXML private ComboBox<String> cbFiltroEstado;

    // Estadísticas
    @FXML private Label lblTotalServicios;
    @FXML private Label lblServiciosActivos;

    // Datos
    private final ServiciosDAO serviciosDAO = new ServiciosDAO();
    private final ObservableList<Producto> serviciosData = FXCollections.observableArrayList();
    private final DecimalFormat formatoPrecio = new DecimalFormat("$#,##0.00");

    @FXML
    public void initialize() {
        System.out.println("Inicializando ServiciosController...");

        configurarTabla();
        cargarServicios();
        actualizarEstadisticas();
        configurarFiltroEstado();
        txtBuscar.setOnAction(event -> buscarServicios());

        System.out.println("ServiciosController inicializado correctamente");
    }

    // ===== CONFIGURACIÓN DE TABLA =====

    private void configurarTabla() {
        System.out.println("Configurando tabla de servicios...");

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Formatear precios
        colPrecio.setCellFactory(column -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty || precio == null ? null : formatoPrecio.format(precio));
            }
        });

        // Estado del servicio (activo/inactivo)
        colEstado.setCellFactory(column -> new TableCell<Producto, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Producto servicio = getTableRow().getItem();

                    if (servicio.estaActivo()) {
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
        tablaServicios.setItems(serviciosData);
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
                MenuItem itemEditar = new MenuItem("Editar servicio");
                MenuItem itemInfo = new MenuItem("Ver información");
                MenuItem itemEstado = new MenuItem("Cambiar Estado");

                // Estilos de los items del menú
                itemEditar.setStyle("-fx-font-size: 12px;");
                itemInfo.setStyle("-fx-font-size: 12px;");
                itemEstado.setStyle("-fx-font-size: 12px;");

                // Eventos de los items
                itemEditar.setOnAction(event -> {
                    Producto servicio = getTableRow().getItem();
                    if (servicio != null) {
                        mostrarDialogoEditarServicio(servicio);
                    }
                });

                itemInfo.setOnAction(event -> {
                    Producto servicio = getTableRow().getItem();
                    if (servicio != null) {
                        mostrarInfoServicio(servicio);
                    }
                });

                itemEstado.setOnAction(event -> {
                    Producto servicio = getTableRow().getItem();
                    if (servicio != null) {
                        cambiarEstadoServicio(servicio);
                    }
                });

                // Agregar items al menú
                menuOpciones.getItems().addAll(itemEditar, itemInfo, itemEstado);

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
    private void buscarServicios() {
        String textoBusqueda = txtBuscar.getText().trim();

        List<Producto> todos = serviciosDAO.obtenerServicios();

        // Aplicar AMBOS filtros: búsqueda + estado
        List<Producto> filtrados = todos.stream()
                .filter(s -> {
                    // Filtro de búsqueda
                    boolean coincideBusqueda = textoBusqueda.isEmpty() ||
                            s.getCodigo().toLowerCase().contains(textoBusqueda.toLowerCase()) ||
                            s.getNombre().toLowerCase().contains(textoBusqueda.toLowerCase());

                    // Filtro de estado
                    String filtroEstado = cbFiltroEstado.getValue();
                    boolean coincideEstado;
                    switch (filtroEstado) {
                        case "Solo Activos":
                            coincideEstado = s.estaActivo();
                            break;
                        case "Solo Inactivos":
                            coincideEstado = !s.estaActivo();
                            break;
                        default: // "Todos"
                            coincideEstado = true;
                    }

                    return coincideBusqueda && coincideEstado;
                })
                .collect(java.util.stream.Collectors.toList());

        serviciosData.setAll(filtrados);
        actualizarEstadisticas();
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
        cargarServicios();
        aplicarFiltros();
    }

    @FXML
    private void nuevoServicio() {
        mostrarDialogoNuevoServicio();
    }

    @FXML
    private void actualizarDatos() {
        cargarServicios();
        mostrarInfo("Servicios actualizados correctamente");
    }

    // ===== LÓGICA DE NEGOCIO =====

    private void cargarServicios() {
        System.out.println("Cargando servicios...");

        try {
            List<Producto> servicios = serviciosDAO.obtenerServicios();
            serviciosData.setAll(servicios);
            actualizarEstadisticas();

            System.out.println(servicios.size() + " servicios cargados");

        } catch (Exception e) {
            System.err.println("Error al cargar servicios: " + e.getMessage());
            mostrarError("Error al cargar servicios: " + e.getMessage());
        }
    }

    private void actualizarEstadisticas() {
        int total = serviciosData.size();

        long activos = serviciosData.stream()
                .filter(Producto::estaDisponible)
                .count();

        lblTotalServicios.setText("Total: " + total);
        lblServiciosActivos.setText("Activos: " + activos);
    }

    // ===== DIÁLOGOS =====

    /**
     * Diálogo para crear nuevo servicio
     */
    private void mostrarDialogoNuevoServicio() {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Nuevo Servicio");
        dialogo.setHeaderText("Complete la información del servicio");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int fila = 0;

        // Código (obligatorio)
        Label lblCodigo = new Label("* Código:");
        lblCodigo.setStyle("-fx-font-weight: bold;");
        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código único del servicio");
        grid.add(lblCodigo, 0, fila);
        grid.add(txtCodigo, 1, fila++);

        // Nombre (obligatorio)
        Label lblNombre = new Label("* Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del servicio");
        grid.add(lblNombre, 0, fila);
        grid.add(txtNombre, 1, fila++);

        // Descripción (opcional)
        Label lblDescripcion = new Label("Descripción:");
        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPrefRowCount(2);
        txtDescripcion.setPromptText("Descripción del servicio");
        grid.add(lblDescripcion, 0, fila);
        grid.add(txtDescripcion, 1, fila++);

        // Categoría (obligatorio)
        Label lblCategoria = new Label("* Categoría:");
        lblCategoria.setStyle("-fx-font-weight: bold;");
        ComboBox<StockDAO.OpcionCombo> cbCategoria = new ComboBox<>();
        configurarComboBoxCategorias(cbCategoria);
        grid.add(lblCategoria, 0, fila);
        grid.add(cbCategoria, 1, fila++);

        // Precio (obligatorio)
        Label lblPrecio = new Label("* Precio:");
        lblPrecio.setStyle("-fx-font-weight: bold;");
        TextField txtPrecio = new TextField();
        txtPrecio.setPromptText("Precio del servicio");
        grid.add(lblPrecio, 0, fila);
        grid.add(txtPrecio, 1, fila++);


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
                if (txtCodigo.getText().trim().isEmpty()) {
                    mostrarError("El código es obligatorio");
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
                if (txtPrecio.getText().trim().isEmpty()) {
                    mostrarError("El precio es obligatorio");
                    return;
                }

                StockDAO.OpcionCombo categoria = obtenerCategoriaSeleccionada(cbCategoria);
                if (categoria == null) {
                    mostrarError("Debe seleccionar una categoría válida");
                    return;
                }

                // Crear servicio
                boolean exito = serviciosDAO.crearServicio(
                        txtCodigo.getText().trim(),
                        txtNombre.getText().trim(),
                        txtDescripcion.getText().trim(),
                        categoria.getId(),
                        Double.parseDouble(txtPrecio.getText())
                );

                if (exito) {
                    cargarServicios();
                    mostrarInfo("Servicio creado correctamente:\n" + txtNombre.getText());
                } else {
                    mostrarError("Error al crear el servicio");
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
     * Diálogo para editar servicio existente
     */
    private void mostrarDialogoEditarServicio(Producto servicio) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Editar Servicio");
        dialogo.setHeaderText("Modificar información de: " + servicio.getNombre());

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int fila = 0;

        // Código (solo lectura)
        Label lblCodigo = new Label("Código:");
        TextField txtCodigo = new TextField(servicio.getCodigo());
        txtCodigo.setDisable(true);
        txtCodigo.setStyle("-fx-background-color: #f0f0f0;");
        grid.add(lblCodigo, 0, fila);
        grid.add(txtCodigo, 1, fila++);

        // Nombre (editable)
        Label lblNombre = new Label("* Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        TextField txtNombre = new TextField(servicio.getNombre());
        grid.add(lblNombre, 0, fila);
        grid.add(txtNombre, 1, fila++);

        // Descripción (editable)
        Label lblDescripcion = new Label("Descripción:");
        TextArea txtDescripcion = new TextArea(
                servicio.getDescripcion() != null ? servicio.getDescripcion() : ""
        );
        txtDescripcion.setPrefRowCount(2);
        grid.add(lblDescripcion, 0, fila);
        grid.add(txtDescripcion, 1, fila++);

        // Categoría (editable)
        Label lblCategoria = new Label("* Categoría:");
        lblCategoria.setStyle("-fx-font-weight: bold;");
        ComboBox<StockDAO.OpcionCombo> cbCategoria = new ComboBox<>();
        configurarComboBoxCategoriasParaEdicion(cbCategoria, servicio.getCategoria());
        grid.add(lblCategoria, 0, fila);
        grid.add(cbCategoria, 1, fila++);

        // Precio (editable)
        Label lblPrecio = new Label("* Precio:");
        lblPrecio.setStyle("-fx-font-weight: bold;");
        TextField txtPrecio = new TextField(String.valueOf(servicio.getPrecio()));
        grid.add(lblPrecio, 0, fila);
        grid.add(txtPrecio, 1, fila++);


        // Nota
        Label nota = new Label("* Campos obligatorios");
        nota.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c; -fx-font-style: italic;");
        grid.add(nota, 0, fila, 2, 1);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button btnOK = (Button) dialogo.getDialogPane().lookupButton(ButtonType.OK);
        btnOK.setText("Actualizar Servicio");

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
                if (categoria == null) {
                    mostrarError("Debe seleccionar una categoría válida");
                    return;
                }

                // Actualizar servicio
                boolean exito = serviciosDAO.actualizarServicio(
                        servicio.getCodigo(),
                        txtNombre.getText().trim(),
                        txtDescripcion.getText().trim(),
                        categoria.getId(),
                        Double.parseDouble(txtPrecio.getText())
                );

                if (exito) {
                    cargarServicios();
                    mostrarInfo("Servicio actualizado correctamente");
                } else {
                    mostrarError("Error al actualizar el servicio");
                }

            } catch (NumberFormatException e) {
                mostrarError("El precio debe ser un número válido");
            } catch (Exception e) {
                mostrarError("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void mostrarInfoServicio(Producto servicio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información del Servicio");
        alert.setHeaderText(servicio.getNombre());

        String info = String.format(
                "Código: %s\n" +
                        "Categoría: %s\n" +
                        "Precio: %s\n" +
                        "Descripción: %s\n" +
                        "Estado: %s",
                servicio.getCodigo(),
                servicio.getCategoria() != null ? servicio.getCategoria() : "Sin categoría",
                formatoPrecio.format(servicio.getPrecio()),
                servicio.getDescripcion() != null ? servicio.getDescripcion() : "Sin descripción",
                servicio.estaDisponible() ? "Activo" : "Inactivo"
        );

        alert.setContentText(info);
        alert.showAndWait();
    }

    // ===== MÉTODOS AUXILIARES COMBOBOX =====

    private void configurarComboBoxCategorias(ComboBox<StockDAO.OpcionCombo> comboBox) {
        try {
            List<StockDAO.OpcionCombo> categorias = serviciosDAO.obtenerCategorias();
            comboBox.setItems(FXCollections.observableArrayList(categorias));
            comboBox.setPromptText("Seleccione una categoría");
        } catch (Exception e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
            mostrarError("Error al cargar categorías");
        }
    }

    private void configurarComboBoxCategoriasParaEdicion(ComboBox<StockDAO.OpcionCombo> comboBox,
                                                         String categoriaActual) {
        configurarComboBoxCategorias(comboBox);

        if (categoriaActual != null) {
            try {
                List<StockDAO.OpcionCombo> categorias = serviciosDAO.obtenerCategorias();
                for (StockDAO.OpcionCombo categoria : categorias) {
                    if (categoria.getNombre().equals(categoriaActual)) {
                        comboBox.setValue(categoria);
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al pre-seleccionar categoría: " + e.getMessage());
            }
        }
    }

    private StockDAO.OpcionCombo obtenerCategoriaSeleccionada(ComboBox<StockDAO.OpcionCombo> comboBox) {
        try {
            return comboBox.getValue();
        } catch (Exception e) {
            return null;
        }
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

        List<Producto> todos = serviciosDAO.obtenerServicios();
        List<Producto> filtrados = todos.stream()
                .filter(s -> {
                    switch (filtroEstado) {
                        case "Solo Activos":
                            return s.estaActivo();
                        case "Solo Inactivos":
                            return !s.estaActivo();
                        default: // "Todos"
                            return true;
                    }
                })
                .collect(java.util.stream.Collectors.toList());

        serviciosData.setAll(filtrados);
        actualizarEstadisticas();
    }

    private void cambiarEstadoServicio(Producto servicio) {
        boolean nuevoEstado = !servicio.estaActivo();
        String accion = nuevoEstado ? "activar" : "desactivar";

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar acción");
        confirmacion.setHeaderText("¿Está seguro?");
        confirmacion.setContentText("¿Desea " + accion + " el servicio:\n" + servicio.getNombre() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean exito = serviciosDAO.cambiarEstadoServicio(servicio.getCodigo(), nuevoEstado);

            if (exito) {
                aplicarFiltros(); // Recargar con filtros
                mostrarInfo("Servicio " + (nuevoEstado ? "activado" : "desactivado") + " correctamente");
            } else {
                mostrarError("Error al cambiar el estado del servicio");
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