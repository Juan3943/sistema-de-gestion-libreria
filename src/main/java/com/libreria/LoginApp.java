package com.libreria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * PUNTO DE ENTRADA DE LA APLICACIÓN
 *
 * Esta clase es el PRIMER código que se ejecuta cuando inicias la aplicación
 *
 * CONCEPTO: Application
 * - Todas las apps JavaFX extienden Application
 * - Tienen un método main() que llama a launch()
 * - Tienen un método start() que JavaFX llama automáticamente
 *
 * FLUJO:
 * 1. JVM ejecuta main()
 * 2. main() llama a launch()
 * 3. JavaFX se inicializa
 * 4. JavaFX llama a start() automáticamente
 * 5. start() carga y muestra la pantalla de login
 */
public class LoginApp extends Application {

    /**
     * MÉTODO PRINCIPAL - Punto de entrada de Java
     * Este es el PRIMER método que se ejecuta
     *
     * @param args Argumentos de línea de comandos (no los usamos)
     */
    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("📚 LIBRERÍA PAPELITOS - Sistema de Gestión");
        System.out.println("=".repeat(50));
        System.out.println("🚀 Iniciando aplicación...");

        // launch() inicializa JavaFX y llama a start() automáticamente
        launch(args);

        System.out.println("👋 Aplicación cerrada");
    }

    /**
     * MÉTODO START - Punto de entrada de JavaFX
     * JavaFX llama a este método AUTOMÁTICAMENTE después de main()
     *
     * CONCEPTO: Stage
     * - Stage = Ventana de la aplicación
     * - JavaFX crea automáticamente el primaryStage
     * - Nosotros le ponemos contenido (Scene) y lo mostramos
     *
     * CONCEPTO: Scene
     * - Scene = Contenido que va dentro del Stage
     * - Se crea a partir de un "root" (nodo raíz)
     * - El root puede venir de FXML o crearse manualmente
     *
     * @param primaryStage La ventana principal (creada por JavaFX)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("🔐 Cargando pantalla de login...");

        try {
            // PASO 1: Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/libreria/view/login-view.fxml")
            );

            Parent root = loader.load();

            System.out.println("✅ Login view cargada");
            System.out.println("✅ LoginController inicializado");

            // PASO 2: Crear Scene con tamaño más grande
            Scene scene = new Scene(root, 600, 500);  // Aumentado de 400x300 a 600x500

            // PASO 3: Configurar el Stage
            primaryStage.setTitle("🔐 Librería Papelitos - Inicio de Sesión");
            primaryStage.setScene(scene);

            // CAMBIO IMPORTANTE: Permitir redimensionar
            primaryStage.setResizable(true);

            // Configurar tamaño mínimo (para que no se achique demasiado)
            primaryStage.setMinWidth(500);
            primaryStage.setMinHeight(400);

            // OPCIONAL: Comenzar maximizada (descomenta si quieres)
            // primaryStage.setMaximized(true);

            // PASO 4: Mostrar la ventana
            primaryStage.show();

            System.out.println("✅ Pantalla de login mostrada");

            // Centrar en pantalla
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("❌ Error al cargar pantalla de login:");
            e.printStackTrace();
            throw e;
        }
    }
}