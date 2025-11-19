package com.libreria.controller;

import com.libreria.dao.ClienteDAO;
import com.libreria.model.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Optional;

public class ClienteController {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private Button btnNuevoCliente;
    @FXML private Button btnBuscar;
    @FXML private Button btnActualizar;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colTipo;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colCuit;
    @FXML private TableColumn<Cliente, String> colCondicionIva;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEstado;
    @FXML private TableColumn<Cliente, Void> colAcciones;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ObservableList<Cliente> clientesData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        configurarFiltros();
        cargarClientes();
    }

    private void configurarTabla() {
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCuit.setCellValueFactory(new PropertyValueFactory<>("cuit"));
        colCondicionIva.setCellValueFactory(new PropertyValueFactory<>("condicionIva"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoTexto"));

        // Colorear estado
        colEstado.setCellFactory(column -> new TableCell<Cliente, String>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    if ("Activo".equals(estado)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Columna de acciones
        configurarColumnaAcciones();

        tablaClientes.setItems(clientesData);
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(column -> new TableCell<Cliente, Void>() {
            private final HBox botones = new HBox(5);
            private final Button btnEditar = new Button("Editar");
            private final Button btnCambiarEstado = new Button();

            {
                btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                btnEditar.setPrefSize(80, 25);

                btnCambiarEstado.setPrefSize(80, 25);

                btnEditar.setOnAction(event -> {
                    Cliente cliente = getTableRow().getItem();
                    if (cliente != null) {
                        editarCliente(cliente);
                    }
                });

                btnCambiarEstado.setOnAction(event -> {
                    Cliente cliente = getTableRow().getItem();
                    if (cliente != null) {
                        cambiarEstadoCliente(cliente);
                    }
                });

                botones.getChildren().addAll(btnEditar, btnCambiarEstado);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Cliente cliente = getTableRow().getItem();

                    if (cliente.isActivo()) {
                        btnCambiarEstado.setText("Desactivar");
                        btnCambiarEstado.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    } else {
                        btnCambiarEstado.setText("Activar");
                        btnCambiarEstado.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                    }

                    setGraphic(botones);
                }
            }
        });
    }

    private void configurarFiltros() {
        cbFiltroEstado.setItems(FXCollections.observableArrayList("Activos", "Inactivos", "Todos"));
        cbFiltroEstado.setValue("Activos");

        cbFiltroEstado.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (txtBuscar.getText().trim().isEmpty()) {
                cargarClientes();
            } else {
                buscarClientes();
            }
        });
    }

    @FXML
    private void cargarClientes() {
        String filtro = cbFiltroEstado.getValue();
        List<Cliente> clientes;

        switch (filtro) {
            case "Activos" -> clientes = clienteDAO.obtenerActivos();
            case "Inactivos" -> clientes = clienteDAO.obtenerInactivos();
            default -> clientes = clienteDAO.obtenerTodos();
        }

        clientesData.setAll(clientes);
        System.out.println("Clientes cargados: " + clientes.size());
    }

    @FXML
    private void buscarClientes() {
        String query = txtBuscar.getText().trim();

        if (query.isEmpty()) {
            cargarClientes();
            return;
        }

        String filtro = cbFiltroEstado.getValue();
        Boolean soloActivos = null;

        if ("Activos".equals(filtro)) {
            soloActivos = true;
        } else if ("Inactivos".equals(filtro)) {
            soloActivos = false;
        }

        List<Cliente> clientes = clienteDAO.buscar(query, soloActivos);
        clientesData.setAll(clientes);
    }

    @FXML
    private void abrirDialogoNuevoCliente() {
        Dialog<Cliente> dialog = crearDialogoCliente(null);
        Optional<Cliente> resultado = dialog.showAndWait();

        resultado.ifPresent(cliente -> {
            if (clienteDAO.crear(cliente)) {
                mostrarInfo("Cliente creado exitosamente");
                cargarClientes();
            } else {
                mostrarError("No se pudo crear el cliente");
            }
        });
    }

    private void editarCliente(Cliente cliente) {
        Dialog<Cliente> dialog = crearDialogoCliente(cliente);
        Optional<Cliente> resultado = dialog.showAndWait();

        resultado.ifPresent(clienteModificado -> {
            if (clienteDAO.actualizar(clienteModificado)) {
                mostrarInfo("Cliente actualizado exitosamente");
                cargarClientes();
            } else {
                mostrarError("No se pudo actualizar el cliente");
            }
        });
    }

    private void cambiarEstadoCliente(Cliente cliente) {
        String accion = cliente.isActivo() ? "desactivar" : "activar";

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿" + accion.substring(0, 1).toUpperCase() +
                accion.substring(1) + " cliente?");
        confirmacion.setContentText(cliente.getNombre());

        if (cliente.isActivo() && clienteDAO.tieneVentas(cliente.getIdCliente())) {
            confirmacion.setContentText(
                    cliente.getNombre() + "\n\n⚠️ Este cliente tiene ventas registradas"
            );
        }

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean nuevoEstado = !cliente.isActivo();
                if (clienteDAO.cambiarEstado(cliente.getIdCliente(), nuevoEstado)) {
                    mostrarInfo("Cliente " + accion + "do exitosamente");
                    cargarClientes();
                } else {
                    mostrarError("No se pudo cambiar el estado del cliente");
                }
            }
        });
    }

    private Dialog<Cliente> crearDialogoCliente(Cliente clienteExistente) {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle(clienteExistente == null ? "Nuevo Cliente" : "Editar Cliente");
        dialog.setHeaderText(clienteExistente == null ?
                "Ingrese los datos del cliente" : "Modificar datos del cliente");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Tipo de cliente
        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.setItems(FXCollections.observableArrayList("PERSONA", "EMPRESA"));
        cbTipo.setValue(clienteExistente != null ? clienteExistente.getTipoCliente() : "EMPRESA");

        // Campos
        TextField txtNombre = new TextField();
        txtNombre.setPromptText(cbTipo.getValue().equals("EMPRESA") ? "Razón Social" : "Nombre Completo");

        // CUIT con formato automático
        TextField txtCuit = new TextField();
        txtCuit.setPromptText("XX-XXXXXXXX-X");

// Formateo automático del CUIT
        txtCuit.textProperty().addListener((obs, oldVal, newVal) -> {
            // Solo permitir números
            String numeros = newVal.replaceAll("[^0-9]", "");

            // Limitar a 11 dígitos
            if (numeros.length() > 11) {
                numeros = numeros.substring(0, 11);
            }

            // Formatear automáticamente
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

        ComboBox<String> cbCondicionIva = new ComboBox<>();
        cbCondicionIva.setItems(FXCollections.observableArrayList(
                "RESPONSABLE_INSCRIPTO", "MONOTRIBUTO", "EXENTO", "CONSUMIDOR_FINAL"
        ));
        cbCondicionIva.setValue("CONSUMIDOR_FINAL");

        TextField txtDireccion = new TextField();
        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("011-1234-5678");

        txtTelefono.textProperty().addListener((obs, oldVal, newVal) -> {
            // Solo permitir números, guiones, paréntesis y espacios
            String filtrado = newVal.replaceAll("[^0-9\\-\\(\\)\\s]", "");
            if (!txtTelefono.getText().equals(filtrado)) {
                txtTelefono.setText(filtrado);
                txtTelefono.positionCaret(filtrado.length());
            }
        });

        TextField txtEmail = new TextField();

        // Cambiar placeholder según tipo
        cbTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("EMPRESA".equals(newVal)) {
                txtNombre.setPromptText("Razón Social");
                txtCuit.setPromptText("CUIT (XX-XXXXXXXX-X)");
            } else {
                txtNombre.setPromptText("Nombre Completo");
                txtCuit.setPromptText("DNI o CUIT (opcional)");
            }
        });

        // Cargar datos si es edición
        if (clienteExistente != null) {
            txtNombre.setText(clienteExistente.getNombre());
            txtCuit.setText(clienteExistente.getCuit());
            cbCondicionIva.setValue(clienteExistente.getCondicionIva());
            txtDireccion.setText(clienteExistente.getDireccion());
            txtTelefono.setText(clienteExistente.getTelefono());
            txtEmail.setText(clienteExistente.getEmail());
        }

        // Agregar al grid
        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(cbTipo, 1, 0);
        grid.add(new Label("Nombre: *"), 0, 1);
        grid.add(txtNombre, 1, 1);
        grid.add(new Label("CUIT:"), 0, 2);
        grid.add(txtCuit, 1, 2);
        grid.add(new Label("Condición IVA:"), 0, 3);
        grid.add(cbCondicionIva, 1, 3);
        grid.add(new Label("Dirección:"), 0, 4);
        grid.add(txtDireccion, 1, 4);
        grid.add(new Label("Teléfono:"), 0, 5);
        grid.add(txtTelefono, 1, 5);
        grid.add(new Label("Email:"), 0, 6);
        grid.add(txtEmail, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Validación y resultado
        // Deshabilitar botón de cerrar dialog con X
        dialog.getDialogPane().lookupButton(btnGuardar).addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {
                    // Validar campos
                    String error = validarCamposCliente(
                            cbTipo.getValue(),
                            txtNombre.getText(),
                            txtCuit.getText()
                    );

                    if (error != null) {
                        event.consume(); // Prevenir que se cierre
                        mostrarError(error);
                    }
                }
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                Cliente cliente = clienteExistente != null ? clienteExistente : new Cliente();
                cliente.setTipoCliente(cbTipo.getValue());
                cliente.setNombre(txtNombre.getText().trim());
                cliente.setCuit(txtCuit.getText().trim());
                cliente.setCondicionIva(cbCondicionIva.getValue());
                cliente.setDireccion(txtDireccion.getText().trim());
                cliente.setTelefono(txtTelefono.getText().trim());
                cliente.setEmail(txtEmail.getText().trim());
                return cliente;
            }
            return null;
        });

        return dialog;
    }

    // Agregar este método nuevo al ClientesController
    private String validarCamposCliente(String tipo, String nombre, String cuit) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre es obligatorio";
        }

        if ("EMPRESA".equals(tipo)) {
            if (cuit == null || cuit.trim().isEmpty()) {
                return "El CUIT es obligatorio para empresas";
            }

            // Validar formato CUIT
            if (!cuit.matches("\\d{2}-\\d{8}-\\d")) {
                return "CUIT inválido. Formato correcto: XX-XXXXXXXX-X";
            }
        }

        // Si es PERSONA y tiene CUIT, validar formato
        if ("PERSONA".equals(tipo) && cuit != null && !cuit.trim().isEmpty()) {
            if (!cuit.matches("\\d{2}-\\d{8}-\\d")) {
                return "CUIT inválido. Formato correcto: XX-XXXXXXXX-X";
            }
        }

        return null; // Todo OK
    }

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
