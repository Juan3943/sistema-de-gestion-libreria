package com.libreria.controller;

import com.libreria.MainApp;
import com.libreria.dao.UsuarioDAO;
import com.libreria.model.Usuario;
import com.libreria.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;



public class LoginController {

    // ===== ELEMENTOS DE LA VISTA =====
    // Estos se conectan automáticamente con los fx:id del FXML

    @FXML
    private TextField txtUsuario;  // Campo de usuario

    @FXML
    private PasswordField txtPassword;  // Campo de contraseña

    @FXML
    private Label lblError;  // Label de error (oculto por defecto)

    @FXML
    private Button btnLogin;  // Botón de login

    @FXML private TextField txtPasswordVisible;
    @FXML private Button btnMostrarPassword;

    // ===== DAO =====
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ===== MÉTODO INITIALIZE =====
    /**
     * Este método se ejecuta AUTOMÁTICAMENTE cuando se carga el FXML
     * Es como el "constructor" del controller
     *
     * CONCEPTO: initialize() es llamado por JavaFX después de cargar la vista
     */
    @FXML
    private void initialize() {
        System.out.println("🔐 LoginController inicializado");

        // Sincronizar campos de contraseña
        txtPassword.textProperty().addListener((obs, old, newVal) -> {
            txtPasswordVisible.setText(newVal);
        });
        txtPasswordVisible.textProperty().addListener((obs, old, newVal) -> {
            txtPassword.setText(newVal);
        });

        Platform.runLater(() -> txtUsuario.requestFocus());
    }


    // ===== MÉTODO PRINCIPAL: HANDLE LOGIN =====
    /**
     * Se ejecuta cuando:
     * - El usuario hace click en "Iniciar Sesión"
     * - El usuario presiona ENTER en cualquier campo
     *
     * PROCESO:
     * 1. Validar que los campos no estén vacíos
     * 2. Llamar a UsuarioDAO.login() para verificar credenciales
     * 3. Si es correcto: guardar en SessionManager y abrir MainApp
     * 4. Si es incorrecto: mostrar mensaje de error
     */
    @FXML
    private void handleLogin() {
        System.out.println("🔐 Intentando login...");

        // Ocultar error previo (si había)
        lblError.setVisible(false);

        // 1️⃣ OBTENER VALORES DE LOS CAMPOS
        String usuario = txtUsuario.getText().trim();
        // Usar el campo que esté visible
        String password = txtPassword.isVisible() ?
                txtPassword.getText() :
                txtPasswordVisible.getText();

        // 2️⃣ VALIDAR que no estén vacíos
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor complete todos los campos");
            return;  // Salir del método
        }

        // 3️⃣ DESHABILITAR BOTÓN mientras se procesa (evitar doble click)
        btnLogin.setDisable(true);
        btnLogin.setText("Verificando...");

        // 4️⃣ VERIFICAR CREDENCIALES en la base de datos
        Usuario usuarioEncontrado = usuarioDAO.login(usuario, password);

        // 5️⃣ PROCESAR RESULTADO
        if (usuarioEncontrado != null) {
            // ✅ LOGIN EXITOSO
            System.out.println("✅ Login exitoso: " + usuarioEncontrado.getNombreCompleto());

            // Guardar usuario en SessionManager (memoria global)
            SessionManager.login(usuarioEncontrado);

            // Cerrar ventana de login y abrir MainApp
            abrirMainApp();

        } else {
            // ❌ LOGIN FALLIDO
            System.out.println("❌ Login fallido");
            mostrarError("Usuario o contraseña incorrectos");

            // Limpiar campo de contraseña por seguridad
            txtPassword.clear();
            txtPassword.requestFocus();

            // Rehabilitar botón
            btnLogin.setDisable(false);
            btnLogin.setText("Iniciar Sesión");
        }
    }

    /**
     * Alterna entre mostrar y ocultar la contraseña
     */
    @FXML
    private void handleTogglePassword() {
        boolean mostrar = txtPassword.isVisible();

        // Invertir visibilidad
        txtPassword.setVisible(!mostrar);
        txtPassword.setManaged(!mostrar);
        txtPasswordVisible.setVisible(mostrar);
        txtPasswordVisible.setManaged(mostrar);

        // Cambiar ícono del botón
        btnMostrarPassword.setText(mostrar ? "🙈" : "👁");
    }


    // ===== MÉTODO: ABRIR MAIN APP =====
    /**
     * Cierra la ventana de login y abre el dashboard (MainApp)
     *
     * CONCEPTO: Manejo de Stages (ventanas)
     * - Cada ventana es un Stage
     * - Podemos cerrar el Stage actual y crear uno nuevo
     */
    // En LoginController
    private void abrirMainApp() {
        try {
            System.out.println("🚀 Abriendo dashboard...");

            // PASO 1: Obtener el Stage ACTUAL desde cualquier nodo de la Scene
            // txtUsuario → getScene() → getWindow() → cast a Stage
            Stage stage = (Stage) txtUsuario.getScene().getWindow();

            // PASO 2: Crear instancia de MainApp
            MainApp mainApp = new MainApp();

            // PASO 3: Llamar a mainApp.start() pasándole el MISMO Stage
            // MainApp cambiará el contenido de la ventana existente
            mainApp.start(stage);

            System.out.println("✅ Dashboard cargado en la misma ventana");

        } catch (Exception e) {
            System.err.println("❌ Error al abrir MainApp: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al abrir el sistema");

            // Rehabilitar botón en caso de error
            btnLogin.setDisable(false);
            btnLogin.setText("Iniciar Sesión");
        }
    }

    // ===== MÉTODO: CERRAR APLICACIÓN =====
    /**
     * Cierra completamente la aplicación
     * Se ejecuta al hacer click en "Cerrar"
     */
    @FXML
    private void handleCerrar() {
        System.out.println("👋 Cerrando aplicación...");

        // Confirmar con el usuario
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Cerrar la aplicación?");
        alert.setContentText("¿Está seguro que desea salir?");

        // Esperar respuesta del usuario
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Cerrar toda la aplicación
                Platform.exit();
                System.exit(0);
            }
        });
    }

    // ===== MÉTODO AUXILIAR: MOSTRAR ERROR =====
    /**
     * Muestra un mensaje de error en la interfaz
     *
     * @param mensaje El mensaje a mostrar
     */
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);

        // Opcional: Agregar efecto visual al label de error
        lblError.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-weight: bold;");
    }
}