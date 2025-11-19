package com.libreria.controller;

import com.libreria.dao.UsuarioDAO;
import com.libreria.model.TipoUsuario;
import com.libreria.model.Usuario;
import com.libreria.util.PasswordUtil;
import com.libreria.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * CONTROLLER: UsuariosController (VERSIÓN CON SEGURIDAD MEJORADA)
 *
 * REGLAS DE SEGURIDAD IMPLEMENTADAS:
 * 1. Admin puede editar su propia cuenta y gestionar empleados
 * 2. Admin NO puede editar/resetear/desactivar otros admins
 * 3. Debe haber al menos 1 admin activo
 * 4. Contraseñas con confirmación y botón "ojo"
 */
public class UsuariosController {

    // ═══════════════════════════════════════════════════════
    // ELEMENTOS DE LA VISTA
    // ═══════════════════════════════════════════════════════

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colApellido;
    @FXML private TableColumn<Usuario, String> colTipo;
    @FXML private TableColumn<Usuario, String> colActivo;
    @FXML private TableColumn<Usuario, String> colFechaCreacion;

    @FXML private Label lblInfoTabla;
    @FXML private Button btnNuevoUsuario;
    @FXML private Button btnEditar;
    @FXML private Button btnResetPassword;
    @FXML private Button btnCambiarEstado;

    // ═══════════════════════════════════════════════════════
    // VARIABLES INTERNAS
    // ═══════════════════════════════════════════════════════

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    // ═══════════════════════════════════════════════════════
    // INITIALIZE
    // ═══════════════════════════════════════════════════════

    @FXML
    private void initialize() {
        System.out.println("👥 UsuariosController inicializado (con seguridad mejorada)");

        configurarTabla();
        cargarUsuarios();
        configurarSeleccion();
    }

    // ═══════════════════════════════════════════════════════
    // CONFIGURACIÓN DE LA TABLA
    // ═══════════════════════════════════════════════════════

    private void configurarTabla() {
        System.out.println("⚙️ Configurando tabla...");

        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));

        colTipo.setCellValueFactory(cellData -> {
            TipoUsuario tipo = cellData.getValue().getTipoUsuario();
            String nombreTipo = (tipo != null) ? tipo.getNombre() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(nombreTipo);
        });

        colActivo.setCellValueFactory(cellData -> {
            boolean activo = cellData.getValue().isActivo();
            return new javafx.beans.property.SimpleStringProperty(activo ? "✅ Activo" : "🔴 Inactivo");
        });

        colFechaCreacion.setCellValueFactory(cellData -> {
            LocalDateTime fecha = cellData.getValue().getFechaCreacion();
            if (fecha != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return new javafx.beans.property.SimpleStringProperty(fecha.format(formatter));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        // NOVEDAD: Resaltar filas de admins que NO se pueden editar
        tablaUsuarios.setRowFactory(tv -> {
            TableRow<Usuario> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldUser, newUser) -> {
                if (newUser != null) {
                    Usuario actual = SessionManager.getUsuarioActual();

                    // Si es admin Y NO es el usuario actual
                    if (newUser.esAdmin() && newUser.getIdUsuario() != actual.getIdUsuario()) {
                        row.setStyle("-fx-background-color: #f8f9fa; -fx-opacity: 0.7;");
                        row.setTooltip(new Tooltip("🔒 No puede editar otros administradores"));
                    } else if (newUser.getIdUsuario() == actual.getIdUsuario()) {
                        // Resaltar tu propia cuenta
                        row.setStyle("-fx-background-color: #e3f2fd;");
                        row.setTooltip(new Tooltip("👤 Esta es tu cuenta"));
                    } else {
                        row.setStyle("");
                        row.setTooltip(null);
                    }
                }
            });
            return row;
        });

        System.out.println("✅ Tabla configurada");
    }

    // ═══════════════════════════════════════════════════════
    // CARGAR USUARIOS
    // ═══════════════════════════════════════════════════════

    private void cargarUsuarios() {
        System.out.println("📋 Cargando usuarios...");

        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            listaUsuarios.clear();
            listaUsuarios.addAll(usuarios);
            tablaUsuarios.setItems(listaUsuarios);

            // CAMBIO: Obtener estadísticas eficientemente
            long totalAdmins = usuarios.stream().filter(Usuario::esAdmin).count();
            long adminsActivos = usuarioDAO.contarAdminsActivos();  // Desde BD

            lblInfoTabla.setText(String.format(
                    "📊 Total: %d usuarios (%d admins, %d activos)",
                    usuarios.size(), totalAdmins, adminsActivos
            ));

            System.out.println("✅ Cargados " + usuarios.size() + " usuarios");

        } catch (Exception e) {
            System.err.println("❌ Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar usuarios", e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════
    // CONFIGURAR SELECCIÓN
    // ═══════════════════════════════════════════════════════

    private void configurarSeleccion() {
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Usuario actual = SessionManager.getUsuarioActual();
                boolean esOtroAdmin = newSelection.esAdmin() &&
                        newSelection.getIdUsuario() != actual.getIdUsuario();

                // Habilitar botones solo si NO es otro admin
                btnEditar.setDisable(esOtroAdmin);
                btnResetPassword.setDisable(esOtroAdmin);
                btnCambiarEstado.setDisable(esOtroAdmin);

                // Cambiar texto del botón según estado
                if (newSelection.isActivo()) {
                    btnCambiarEstado.setText("🔴 Desactivar Usuario");
                    btnCambiarEstado.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10 20;");
                } else {
                    btnCambiarEstado.setText("✅ Activar Usuario");
                    btnCambiarEstado.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10 20;");
                }
            } else {
                btnEditar.setDisable(true);
                btnResetPassword.setDisable(true);
                btnCambiarEstado.setDisable(true);
            }
        });
    }

    // ═══════════════════════════════════════════════════════
    // HANDLERS
    // ═══════════════════════════════════════════════════════

    @FXML
    private void handleNuevoUsuario() {
        System.out.println("➕ Creando nuevo usuario...");

        Optional<Usuario> resultado = mostrarDialogoUsuario(null);

        resultado.ifPresent(nuevoUsuario -> {
            String passwordHash = PasswordUtil.hashear(nuevoUsuario.getContrasenaHash());
            nuevoUsuario.setContrasenaHash(passwordHash);

            boolean exito = usuarioDAO.crear(nuevoUsuario);

            if (exito) {
                mostrarInfo("Usuario creado", "El usuario se creó exitosamente");
                cargarUsuarios();
            } else {
                mostrarError("Error", "No se pudo crear el usuario");
            }
        });
    }

    @FXML
    private void handleEditarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Sin selección", "Por favor seleccione un usuario");
            return;
        }

        // VALIDACIÓN: No editar otros admins
        if (!puedeEditarUsuario(seleccionado)) {
            return;
        }

        System.out.println("✏️ Editando usuario: " + seleccionado.getNombreUsuario());

        Optional<Usuario> resultado = mostrarDialogoUsuario(seleccionado);

        resultado.ifPresent(usuarioEditado -> {
            boolean exito = usuarioDAO.actualizar(usuarioEditado);

            if (exito) {
                mostrarInfo("Usuario actualizado", "Los cambios se guardaron exitosamente");
                cargarUsuarios();
            } else {
                mostrarError("Error", "No se pudo actualizar el usuario");
            }
        });
    }

    @FXML
    private void handleResetearPassword() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Sin selección", "Por favor seleccione un usuario");
            return;
        }

        // VALIDACIÓN: No resetear password de otros admins
        if (!puedeResetearPassword(seleccionado)) {
            return;
        }

        System.out.println("🔑 Reseteando contraseña de: " + seleccionado.getNombreUsuario());

        // Mostrar diálogo con doble entrada y botón ojo
        Optional<String> resultado = mostrarDialogoPassword(seleccionado.getNombreUsuario());

        resultado.ifPresent(nuevaPassword -> {
            boolean exito = usuarioDAO.resetearPassword(seleccionado.getIdUsuario(), nuevaPassword);

            if (exito) {
                mostrarInfo("Contraseña reseteada",
                        String.format("La contraseña de %s fue actualizada exitosamente",
                                seleccionado.getNombreUsuario()));
            } else {
                mostrarError("Error", "No se pudo resetear la contraseña");
            }
        });
    }

    @FXML
    private void handleCambiarEstado() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Sin selección", "Por favor seleccione un usuario");
            return;
        }

        boolean nuevoEstado = !seleccionado.isActivo();

        // VALIDACIÓN: No desactivar otros admins ni al último admin
        if (!puedeDesactivar(seleccionado)) {
            return;
        }

        String accion = nuevoEstado ? "activar" : "desactivar";

        System.out.println("🔄 Cambiando estado de: " + seleccionado.getNombreUsuario() + " a " + accion);

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar acción");
        confirmacion.setHeaderText("¿" + (nuevoEstado ? "Activar" : "Desactivar") + " usuario?");
        confirmacion.setContentText("Usuario: " + seleccionado.getNombreCompleto());

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean exito = usuarioDAO.cambiarEstado(seleccionado.getIdUsuario(), nuevoEstado);

                if (exito) {
                    mostrarInfo("Estado cambiado", "El usuario fue " + accion + "do exitosamente");
                    cargarUsuarios();
                } else {
                    mostrarError("Error", "No se pudo cambiar el estado del usuario");
                }
            }
        });
    }

    @FXML
    private void handleVolver() {
        System.out.println("🏠 Volviendo al dashboard...");

        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) tablaUsuarios.getScene().getWindow();
            com.libreria.MainApp mainApp = new com.libreria.MainApp();
            mainApp.start(stage);
        } catch (Exception e) {
            System.err.println("❌ Error al volver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════
    // VALIDACIONES DE SEGURIDAD
    // ═══════════════════════════════════════════════════════

    /**
     * Valida si el usuario actual puede editar al usuario seleccionado
     */
    private boolean puedeEditarUsuario(Usuario seleccionado) {
        Usuario actual = SessionManager.getUsuarioActual();

        if (seleccionado.esAdmin() && seleccionado.getIdUsuario() != actual.getIdUsuario()) {
            mostrarError(
                    "Acción no permitida",
                    "No puede editar otros administradores.\nSolo puede editar su propia cuenta."
            );
            return false;
        }

        return true;
    }

    /**
     * Valida si el usuario actual puede resetear la contraseña del usuario seleccionado
     */
    private boolean puedeResetearPassword(Usuario seleccionado) {
        Usuario actual = SessionManager.getUsuarioActual();

        if (seleccionado.esAdmin() && seleccionado.getIdUsuario() != actual.getIdUsuario()) {
            mostrarError(
                    "Acción no permitida",
                    "No puede resetear la contraseña de otros administradores.\nSolo puede cambiar su propia contraseña."
            );
            return false;
        }

        return true;
    }

    /**
     * Valida si el usuario seleccionado puede ser desactivado
     */
    private boolean puedeDesactivar(Usuario seleccionado) {
        Usuario actual = SessionManager.getUsuarioActual();

        // No puede desactivar otros admins
        if (seleccionado.esAdmin() && seleccionado.getIdUsuario() != actual.getIdUsuario()) {
            mostrarError(
                    "Acción no permitida",
                    "No puede desactivar otros administradores."
            );
            return false;
        }

        // No puede desactivar al último admin activo
        if (seleccionado.isActivo() && seleccionado.esAdmin()) {
            // CAMBIO: Usar método del DAO en lugar de filtrar en memoria
            if (usuarioDAO.esUnicoAdminActivo(seleccionado.getIdUsuario())) {
                mostrarError(
                        "Acción no permitida",
                        "No puede desactivar al único administrador activo del sistema.\n" +
                                "Debe haber al menos un administrador activo."
                );
                return false;
            }
        }

        return true;
    }

    /**
     * Valida si se puede cambiar el rol de un usuario
     */
    private boolean puedeCambiarRol(Usuario usuario, TipoUsuario nuevoRol) {
        Usuario actual = SessionManager.getUsuarioActual();

        // Si intenta cambiar el rol de otro admin
        if (usuario != null && usuario.esAdmin() &&
                usuario.getIdUsuario() != actual.getIdUsuario()) {
            mostrarError(
                    "Acción no permitida",
                    "No puede cambiar el rol de otros administradores."
            );
            return false;
        }

        // Si intenta quitarse el rol de admin a sí mismo siendo el único
        if (usuario != null && usuario.esAdmin() &&
                !nuevoRol.getNombre().equals("ADMIN") &&
                usuario.getIdUsuario() == actual.getIdUsuario()) {

            long adminsActivos = listaUsuarios.stream()
                    .filter(u -> u.esAdmin() && u.isActivo())
                    .count();

            if (adminsActivos <= 1) {
                mostrarError(
                        "Acción no permitida",
                        "No puede cambiar su rol siendo el único administrador activo.\n" +
                                "Primero debe crear otro administrador."
                );
                return false;
            }
        }

        return true;
    }

    // ═══════════════════════════════════════════════════════
    // DIÁLOGO DE USUARIO (CON BOTÓN OJO)
    // ═══════════════════════════════════════════════════════

    private Optional<Usuario> mostrarDialogoUsuario(Usuario usuarioExistente) {
        boolean esNuevo = (usuarioExistente == null);

        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle(esNuevo ? "Nuevo Usuario" : "Editar Usuario");
        dialog.setHeaderText(esNuevo ? "Ingrese los datos del nuevo usuario" : "Modifique los datos del usuario");

        ButtonType btnGuardar = new ButtonType(esNuevo ? "Crear" : "Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // Campos básicos
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("nombre.usuario");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre");

        TextField txtApellido = new TextField();
        txtApellido.setPromptText("Apellido");

        ComboBox<TipoUsuario> cboTipo = new ComboBox<>();
        cboTipo.setItems(FXCollections.observableArrayList(usuarioDAO.obtenerTiposUsuario()));
        cboTipo.setPromptText("Seleccione rol");

        // CAMPOS DE CONTRASEÑA CON BOTÓN OJO (solo para nuevo usuario)
        PasswordField pwdPassword1 = new PasswordField();
        TextField txtPassword1 = new TextField();
        PasswordField pwdPassword2 = new PasswordField();
        TextField txtPassword2 = new TextField();

        pwdPassword1.setPromptText("Contraseña");
        txtPassword1.setPromptText("Contraseña");
        pwdPassword2.setPromptText("Confirmar contraseña");
        txtPassword2.setPromptText("Confirmar contraseña");

        // Sincronizar campos
        pwdPassword1.textProperty().addListener((obs, old, newVal) -> txtPassword1.setText(newVal));
        txtPassword1.textProperty().addListener((obs, old, newVal) -> pwdPassword1.setText(newVal));
        pwdPassword2.textProperty().addListener((obs, old, newVal) -> txtPassword2.setText(newVal));
        txtPassword2.textProperty().addListener((obs, old, newVal) -> pwdPassword2.setText(newVal));

        // Inicialmente mostrar PasswordField
        txtPassword1.setVisible(false);
        txtPassword1.setManaged(false);
        txtPassword2.setVisible(false);
        txtPassword2.setManaged(false);

        // Botón Ojo 1
        Button btnOjo1 = new Button("👁");
        btnOjo1.setStyle("-fx-cursor: hand; -fx-padding: 5 10;");
        btnOjo1.setOnAction(e -> {
            boolean mostrar = pwdPassword1.isVisible();
            pwdPassword1.setVisible(!mostrar);
            pwdPassword1.setManaged(!mostrar);
            txtPassword1.setVisible(mostrar);
            txtPassword1.setManaged(mostrar);
            btnOjo1.setText(mostrar ? "🙈" : "👁");
        });

        // Botón Ojo 2
        Button btnOjo2 = new Button("👁");
        btnOjo2.setStyle("-fx-cursor: hand; -fx-padding: 5 10;");
        btnOjo2.setOnAction(e -> {
            boolean mostrar = pwdPassword2.isVisible();
            pwdPassword2.setVisible(!mostrar);
            pwdPassword2.setManaged(!mostrar);
            txtPassword2.setVisible(mostrar);
            txtPassword2.setManaged(mostrar);
            btnOjo2.setText(mostrar ? "🙈" : "👁");
        });

        // Contenedores para password + botón ojo
        HBox hboxPass1 = new HBox(5);
        StackPane stackPass1 = new StackPane();
        stackPass1.getChildren().addAll(pwdPassword1, txtPassword1);
        HBox.setHgrow(stackPass1, Priority.ALWAYS);
        hboxPass1.getChildren().addAll(stackPass1, btnOjo1);

        HBox hboxPass2 = new HBox(5);
        StackPane stackPass2 = new StackPane();
        stackPass2.getChildren().addAll(pwdPassword2, txtPassword2);
        HBox.setHgrow(stackPass2, Priority.ALWAYS);
        hboxPass2.getChildren().addAll(stackPass2, btnOjo2);

        // Si es edición, llenar con datos actuales
        int row = 0;
        if (!esNuevo) {
            txtUsername.setText(usuarioExistente.getNombreUsuario());
            txtNombre.setText(usuarioExistente.getNombre());
            txtApellido.setText(usuarioExistente.getApellido());
            cboTipo.setValue(usuarioExistente.getTipoUsuario());
        }

        // Agregar campos al grid
        grid.add(new Label("Usuario:"), 0, row);
        grid.add(txtUsername, 1, row++);
        grid.add(new Label("Nombre:"), 0, row);
        grid.add(txtNombre, 1, row++);
        grid.add(new Label("Apellido:"), 0, row);
        grid.add(txtApellido, 1, row++);
        grid.add(new Label("Rol:"), 0, row);
        grid.add(cboTipo, 1, row++);

        if (esNuevo) {
            grid.add(new Label("Contraseña:"), 0, row);
            grid.add(hboxPass1, 1, row++);
            grid.add(new Label("Confirmar:"), 0, row);
            grid.add(hboxPass2, 1, row++);
        }

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado a Usuario
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                // Validar campos básicos
                if (txtUsername.getText().trim().isEmpty() ||
                        txtNombre.getText().trim().isEmpty() ||
                        txtApellido.getText().trim().isEmpty() ||
                        cboTipo.getValue() == null) {

                    mostrarAdvertencia("Campos incompletos", "Por favor complete todos los campos");
                    return null;
                }

                // Validar contraseñas (solo para nuevo usuario)
                if (esNuevo) {
                    String pass1 = pwdPassword1.getText().trim();
                    String pass2 = pwdPassword2.getText().trim();

                    if (pass1.isEmpty() || pass2.isEmpty()) {
                        mostrarAdvertencia("Contraseña vacía", "Por favor ingrese una contraseña");
                        return null;
                    }

                    if (pass1.length() < 4) {
                        mostrarAdvertencia("Contraseña corta", "La contraseña debe tener al menos 4 caracteres");
                        return null;
                    }

                    if (!pass1.equals(pass2)) {
                        mostrarAdvertencia("Contraseñas no coinciden",
                                "Las contraseñas ingresadas no coinciden.\nPor favor verifique.");
                        return null;
                    }
                }

                // Validar rol (si puede cambiarlo)
                if (!puedeCambiarRol(usuarioExistente, cboTipo.getValue())) {
                    return null;
                }

                // Verificar username duplicado
                int idExcluir = esNuevo ? -1 : usuarioExistente.getIdUsuario();
                if (usuarioDAO.existeUsername(txtUsername.getText().trim(), idExcluir)) {
                    mostrarAdvertencia("Usuario duplicado", "El nombre de usuario ya existe");
                    return null;
                }

                // Crear objeto Usuario
                Usuario usuario = esNuevo ? new Usuario() : usuarioExistente;
                usuario.setNombreUsuario(txtUsername.getText().trim());
                usuario.setNombre(txtNombre.getText().trim());
                usuario.setApellido(txtApellido.getText().trim());
                usuario.setTipoUsuario(cboTipo.getValue());

                if (esNuevo) {
                    usuario.setContrasenaHash(pwdPassword1.getText());  // Se hasheará después
                    usuario.setActivo(true);
                }

                return usuario;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    // ═══════════════════════════════════════════════════════
    // DIÁLOGO DE PASSWORD (CON CONFIRMACIÓN Y BOTÓN OJO)
    // ═══════════════════════════════════════════════════════

    /**
     * Diálogo para resetear contraseña con doble entrada y botón ojo
     */
    private Optional<String> mostrarDialogoPassword(String nombreUsuario) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Resetear Contraseña");
        dialog.setHeaderText("Resetear contraseña para: " + nombreUsuario);

        ButtonType btnGuardar = new ButtonType("Cambiar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // Campos de contraseña con botón ojo
        PasswordField pwdPassword1 = new PasswordField();
        TextField txtPassword1 = new TextField();
        PasswordField pwdPassword2 = new PasswordField();
        TextField txtPassword2 = new TextField();

        pwdPassword1.setPromptText("Nueva contraseña");
        txtPassword1.setPromptText("Nueva contraseña");
        pwdPassword2.setPromptText("Confirmar nueva contraseña");
        txtPassword2.setPromptText("Confirmar nueva contraseña");

        // Sincronizar
        pwdPassword1.textProperty().addListener((obs, old, newVal) -> txtPassword1.setText(newVal));
        txtPassword1.textProperty().addListener((obs, old, newVal) -> pwdPassword1.setText(newVal));
        pwdPassword2.textProperty().addListener((obs, old, newVal) -> txtPassword2.setText(newVal));
        txtPassword2.textProperty().addListener((obs, old, newVal) -> pwdPassword2.setText(newVal));

        // Inicialmente ocultar TextField
        txtPassword1.setVisible(false);
        txtPassword1.setManaged(false);
        txtPassword2.setVisible(false);
        txtPassword2.setManaged(false);

        // Botones Ojo
        Button btnOjo1 = new Button("👁");
        btnOjo1.setStyle("-fx-cursor: hand; -fx-padding: 5 10;");
        btnOjo1.setOnAction(e -> {
            boolean mostrar = pwdPassword1.isVisible();
            pwdPassword1.setVisible(!mostrar);
            pwdPassword1.setManaged(!mostrar);
            txtPassword1.setVisible(mostrar);
            txtPassword1.setManaged(mostrar);
            btnOjo1.setText(mostrar ? "🙈" : "👁");
        });

        Button btnOjo2 = new Button("👁");
        btnOjo2.setStyle("-fx-cursor: hand; -fx-padding: 5 10;");
        btnOjo2.setOnAction(e -> {
            boolean mostrar = pwdPassword2.isVisible();
            pwdPassword2.setVisible(!mostrar);
            pwdPassword2.setManaged(!mostrar);
            txtPassword2.setVisible(mostrar);
            txtPassword2.setManaged(mostrar);
            btnOjo2.setText(mostrar ? "🙈" : "👁");
        });

        // Contenedores
        HBox hboxPass1 = new HBox(5);
        StackPane stackPass1 = new StackPane();
        stackPass1.getChildren().addAll(pwdPassword1, txtPassword1);
        HBox.setHgrow(stackPass1, Priority.ALWAYS);
        hboxPass1.getChildren().addAll(stackPass1, btnOjo1);

        HBox hboxPass2 = new HBox(5);
        StackPane stackPass2 = new StackPane();
        stackPass2.getChildren().addAll(pwdPassword2, txtPassword2);
        HBox.setHgrow(stackPass2, Priority.ALWAYS);
        hboxPass2.getChildren().addAll(stackPass2, btnOjo2);

        // Agregar al grid
        grid.add(new Label("Nueva contraseña:"), 0, 0);
        grid.add(hboxPass1, 1, 0);
        grid.add(new Label("Confirmar:"), 0, 1);
        grid.add(hboxPass2, 1, 1);

        // Label informativo
        Label lblInfo = new Label("Mínimo 4 caracteres");
        lblInfo.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
        grid.add(lblInfo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                String pass1 = pwdPassword1.getText().trim();
                String pass2 = pwdPassword2.getText().trim();

                // Validar vacíos
                if (pass1.isEmpty() || pass2.isEmpty()) {
                    mostrarAdvertencia("Contraseña vacía", "Por favor ingrese una contraseña");
                    return null;
                }

                // Validar longitud
                if (pass1.length() < 4) {
                    mostrarAdvertencia("Contraseña corta",
                            "La contraseña debe tener al menos 4 caracteres");
                    return null;
                }

                // Validar coincidencia
                if (!pass1.equals(pass2)) {
                    mostrarAdvertencia("Contraseñas no coinciden",
                            "Las contraseñas ingresadas no coinciden.\nPor favor verifique.");
                    return null;
                }

                return pass1;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    // ═══════════════════════════════════════════════════════
    // MÉTODOS AUXILIARES
    // ═══════════════════════════════════════════════════════

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}