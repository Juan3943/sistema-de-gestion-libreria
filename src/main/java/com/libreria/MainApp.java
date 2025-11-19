package com.libreria;

import com.libreria.controller.*;
import com.libreria.dao.ProductoDAO;
import com.libreria.dao.StockDAO;
import com.libreria.model.Producto;
import com.libreria.util.ConexionBD;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.libreria.util.SessionManager;
import javafx.scene.control.Alert.AlertType;
import com.libreria.controller.ClienteController;


public class MainApp extends Application {

    // Variables iguales + ProductoDAO para consultas
    private BorderPane layoutPrincipal;
    private StackPane contenidoPrincipal;
    private VentaController ventaController;
    private HistorialVentasController historialVentasController;
    private StockController stockController;
    private ServiciosController serviciosController;
    private Stage primaryStage;
    private ProductoDAO productoDAO = new ProductoDAO();  // ← NOVEDAD: Acceso a BD
    private StockDAO stockDAO = new StockDAO();
    private ProveedorController proveedorController;
    private CompraController compraController;
    private ReportesController reportesController;
    private ClienteController clientesController;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        System.out.println("🚀 Cargando dashboard...");

        layoutPrincipal = new BorderPane();

        HBox menuSuperior = crearMenuConHover();
        layoutPrincipal.setTop(menuSuperior);

        contenidoPrincipal = new StackPane();
        layoutPrincipal.setCenter(contenidoPrincipal);

        mostrarDashboardConDatosReales();

        // CAMBIO: Tamaño más grande y redimensionable
        Scene scene = new Scene(layoutPrincipal, 1400, 850);  // Aumentado de 1200x750

        stage.setTitle("📚 Librería Papelitos - Dashboard");
        stage.setScene(scene);

        // CAMBIO IMPORTANTE: Permitir redimensionar
        stage.setResizable(true);

        // Configurar tamaño mínimo
        stage.setMinWidth(1200);
        stage.setMinHeight(700);

        // OPCIONAL: Maximizar automáticamente
        stage.setMaximized(true);  // ← AGREGADO: Inicia maximizada

        stage.setOnCloseRequest(event -> {
            if (ventaController != null) {
                ventaController.cleanup();
            }
        });

        stage.show();

        System.out.println("✅ Dashboard cargado");
    }


    private HBox crearMenuConHover() {
        System.out.println("🔧 Creando menú con control de permisos...");

        HBox menu = new HBox(15);
        menu.setAlignment(Pos.CENTER_LEFT);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #2c3e50;");

        String estiloBoton = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 3;";

        // Botón Home
        Button btnHome = new Button("📚 Home");
        btnHome.setStyle(estiloBoton);
        btnHome.setOnAction(e -> mostrarDashboardConDatosReales());

        // Espaciador central (empuja los botones de acción a la izquierda)
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        // Botones de módulos
        Button btnVentas = new Button("🛒 Ventas");
        Button btnStock = new Button("Productos");
        Button btnProveedores = new Button("🏢 Proveedores");
        Button btnCompras = new Button("📦 Compras");
        Button btnReportes = new Button("📊 Reportes");
        Button btnClientes = new Button("👥 Clientes");
        Button btnServicios = new Button("Servicios");

        btnVentas.setStyle(estiloBoton);
        btnStock.setStyle(estiloBoton);
        btnProveedores.setStyle(estiloBoton);
        btnCompras.setStyle(estiloBoton);
        btnReportes.setStyle(estiloBoton);
        btnClientes.setStyle(estiloBoton);
        btnServicios.setStyle(estiloBoton);

        agregarHover(btnVentas);
        agregarHover(btnStock);
        agregarHover(btnProveedores);
        agregarHover(btnCompras);
        agregarHover(btnReportes);
        agregarHover(btnClientes);
        agregarHover(btnServicios);

        btnVentas.setOnAction(e -> cargarModuloVentasHistorial());
        btnStock.setOnAction(e -> cargarModuloStock());
        btnProveedores.setOnAction(e -> cargarModuloProveedores());
        btnCompras.setOnAction(e -> cargarModuloCompras());
        btnReportes.setOnAction(e -> cargarModuloReportes());
        btnClientes.setOnAction(e -> cargarModuloClientes());
        btnServicios.setOnAction(e -> cargarModuloServicios());

        // NUEVO: Botón de Usuarios (solo visible para ADMIN)
        Button btnUsuarios = new Button("👥 Usuarios");
        btnUsuarios.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 3;");
        agregarHover(btnUsuarios);
        btnUsuarios.setOnAction(e -> cargarModuloUsuarios());

        // VERIFICAR PERMISOS: mostrar/ocultar según rol
        if (SessionManager.esAdmin()) {
            btnUsuarios.setVisible(true);
            btnUsuarios.setManaged(true);
            System.out.println("✅ Usuario es ADMIN - Botón Usuarios visible");
        } else {
            btnUsuarios.setVisible(false);
            btnUsuarios.setManaged(false);  // No ocupa espacio
            System.out.println("⚠️ Usuario es EMPLEADO - Botón Usuarios oculto");
        }

        // NUEVO: Botón Cerrar Sesión
        Button btnLogout = new Button("🚪 Cerrar Sesión");
        btnLogout.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 3;");
        agregarHover(btnLogout);
        btnLogout.setOnAction(e -> cerrarSesion());

        // Agregar todos los botones al menú
        menu.getChildren().addAll(
                btnHome,
                espaciador,
                btnVentas,
                btnStock,
                btnServicios,
                btnProveedores,
                btnCompras,
                btnClientes,
                btnReportes,
                btnUsuarios,
                btnLogout
        );

        return menu;
    }


    private void agregarHover(Button boton) {
        String estiloOriginal = boton.getStyle();

        // Al ENTRAR el mouse: cambiar a azul
        boton.setOnMouseEntered(e -> {
            boton.setStyle(estiloOriginal + "-fx-background-color: #3498db;");
        });

        // Al SALIR el mouse: volver al color original
        boton.setOnMouseExited(e -> {
            boton.setStyle(estiloOriginal);
        });
    }


    private void mostrarDashboardConDatosReales() {
        System.out.println("🏠 Mostrando dashboard con datos reales de BD...");

        VBox dashboard = new VBox(30);
        dashboard.setAlignment(Pos.TOP_CENTER);
        dashboard.setPadding(new Insets(40));
        dashboard.setStyle("-fx-background-color: #ecf0f1;");

        // MODIFICADO: Mostrar quien está logueado
        Label titulo = new Label("🏠 Panel Principal");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

// NUEVO: Mostrar usuario actual
        String nombreUsuario = SessionManager.getNombreUsuarioActual();
        String tipoUsuario = SessionManager.esAdmin() ? "ADMIN" : "EMPLEADO";
        Label subtitulo = new Label(
                String.format("Bienvenido, %s (%s) - Sistema de Gestión", nombreUsuario, tipoUsuario)
        );
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-font-weight: bold;");

        // NOVEDAD: Métricas con consultas REALES
        HBox metricas = crearMetricasConDatosReales();

        // NOVEDAD: Botones más grandes con hover
        GridPane botones = crearBotonesGrandesConHover();

        // NOVEDAD: Alertas DINÁMICAS desde la BD
        VBox alertas = crearAlertasDinamicas();

        dashboard.getChildren().addAll(titulo, subtitulo, metricas, botones, alertas);

        //Envolver el dashboard en un ScrollPane para permitir scroll
        ScrollPane scrollPane = new ScrollPane(dashboard);
        scrollPane.setFitToWidth(true);  // El contenido se adapta al ancho de la ventana
        scrollPane.setStyle("-fx-background: #ecf0f1; -fx-background-color: #ecf0f1;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);  // Sin scroll horizontal
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  // Scroll vertical cuando sea necesario

        contenidoPrincipal.getChildren().clear();
        contenidoPrincipal.getChildren().add(scrollPane);  // Agregar ScrollPane en lugar de dashboard directo

        primaryStage.setTitle("📚 Librería Papelitos - Panel Principal");
        System.out.println("✅ Dashboard con datos reales mostrado");
    }


    private HBox crearMetricasConDatosReales() {
        System.out.println("📊 Obteniendo métricas desde BD...");

        HBox metricas = new HBox(40);
        metricas.setAlignment(Pos.CENTER);

        // MÉTRICA 1: Ventas del día (consulta SQL)
        String ventasHoy = obtenerVentasDelDia();
        VBox metrica1 = crearUnaMetricaSimple("💰", "Ventas Hoy", ventasHoy);

        // MÉTRICA 2: Total productos (consulta a ProductoDAO)
        String totalProductos = obtenerTotalProductos();
        VBox metrica2 = crearUnaMetricaSimple("📦", "Productos", totalProductos);

        // MÉTRICA 3: Alertas de stock (consulta con filtros)
        String alertasStock = obtenerCantidadAlertas();
        VBox metrica3 = crearUnaMetricaSimple("⚠️", "Alertas Stock", alertasStock);

        metricas.getChildren().addAll(metrica1, metrica2, metrica3);

        System.out.println("✅ Métricas con datos reales creadas");
        return metricas;
    }


    private String obtenerVentasDelDia() {
        System.out.println("💰 Consultando ventas del día...");

        String sql = """
            SELECT COALESCE(SUM(total), 0) as total_dia
            FROM venta 
            WHERE DATE(fecha) = CURDATE() 
              AND estado = 'COMPLETADA'
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                double total = rs.getDouble("total_dia");
                String resultado = String.format("$%.2f", total);
                System.out.println("💰 Ventas del día: " + resultado);
                return resultado;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar ventas: " + e.getMessage());
            return "Error";
        }

        return "$0.00";
    }

    private String obtenerTotalProductos() {
        System.out.println("📦 Consultando total de productos...");

        try {
            List<Producto> productos = productoDAO.obtenerTodos();
            String resultado = String.valueOf(productos.size());
            System.out.println("📦 Total productos: " + resultado);
            return resultado;

        } catch (Exception e) {
            System.err.println("❌ Error al consultar productos: " + e.getMessage());
            return "Error";
        }
    }


    private String obtenerCantidadAlertas() {
        System.out.println("⚠️ Consultando alertas de stock...");

        try {
            List<Producto> productos = productoDAO.obtenerTodos();

            // Filtrar productos con problemas
            long alertas = productos.stream()
                    .filter(p -> p.getStock() <= p.getStockMinimo())  // Stock crítico
                    .count();

            String resultado = String.valueOf(alertas);
            System.out.println("⚠️ Alertas de stock: " + resultado);
            return resultado;

        } catch (Exception e) {
            System.err.println("❌ Error al consultar alertas: " + e.getMessage());
            return "Error";
        }
    }


    private GridPane crearBotonesGrandesConHover() {
        System.out.println("🔲 Creando botones grandes con hover...");

        GridPane tabla = new GridPane();
        tabla.setHgap(25);  // Más espacio entre columnas
        tabla.setVgap(20);  // Más espacio entre filas
        tabla.setAlignment(Pos.CENTER);

        // Título
        Label titulo = new Label("🚀 Accesos Rápidos");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        tabla.add(titulo, 0, 0, 3, 1);

        // NOVEDAD: Botones MÁS GRANDES
        Button btnVentas = crearBotonGrande("🛒", "Realizar Venta", "#27ae60");
        Button btnStock = crearBotonGrande("📦", "Gestionar Productos", "#3498db");
        Button btnReportes = crearBotonGrande("📊", "Ver Reportes", "#9b59b6");
        Button btnNuevo = crearBotonGrande("➕", "Nuevo Producto", "#f39c12");
        Button btnDia = crearBotonGrande("📋", "Ventas del Día", "#e67e22");
        Button btnServicios = crearBotonGrande("📋", "Gestionar Servicios", "#e32e22");

        // Ubicar en tabla
        tabla.add(btnVentas, 0, 1);
        tabla.add(btnStock, 1, 1);
        tabla.add(btnReportes, 2, 1);
        tabla.add(btnNuevo, 0, 2);
        tabla.add(btnDia, 1, 2);
        tabla.add(btnServicios, 2, 2);

        // Eventos
        btnVentas.setOnAction(e -> cargarModuloVentasCarrito());
        btnStock.setOnAction(e -> cargarModuloStock());
        btnReportes.setOnAction(e -> cargarModuloReportes());
        btnNuevo.setOnAction(e -> cargarModuloStockYAbrirNuevo());
        btnDia.setOnAction(e -> cargarModuloReportesVentasDia());
        btnServicios.setOnAction(e -> cargarModuloServicios());

        System.out.println("✅ Botones grandes con hover creados");
        return tabla;
    }

    /**
     * CONCEPTO: Crear botón grande con hover personalizado
     * Tamaño más grande + efecto hover con color específico
     */
    private Button crearBotonGrande(String icono, String texto, String color) {
        Button boton = new Button();

        // NOVEDAD: Tamaño más grande
        boton.setPrefSize(200, 80);  // Era 120x60, ahora 200x80

        // Estilo base
        String estiloBase = String.format(
                "-fx-background-color: %s; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8;",
                color
        );
        boton.setStyle(estiloBase);

        // Contenido del botón
        VBox contenido = new VBox(8);
        contenido.setAlignment(Pos.CENTER);

        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 24px;");  // Icono más grande

        Label lblTexto = new Label(texto);
        lblTexto.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        contenido.getChildren().addAll(lblIcono, lblTexto);
        boton.setGraphic(contenido);

        // NOVEDAD: Hover con color personalizado
        boton.setOnMouseEntered(e -> {
            boton.setStyle(estiloBase + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2); " +
                    "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");  // Efecto sombra + agrandar ligeramente
        });

        boton.setOnMouseExited(e -> {
            boton.setStyle(estiloBase);  // Volver al estado original
        });

        return boton;
    }


    private VBox crearAlertasDinamicas() {
        System.out.println("🔔 Creando alertas dinámicas desde BD...");

        VBox panelAlertas = new VBox(12);  // Espaciado reducido
        panelAlertas.setPadding(new Insets(20));
        panelAlertas.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-border-color: #ddd;");
        panelAlertas.setMaxWidth(800);

        try {
            List<Producto> productosProblema = obtenerProductosConProblemas();
            int totalAlertas = productosProblema.size();

            // TÍTULO con contador
            HBox headerAlertas = new HBox(10);
            headerAlertas.setAlignment(Pos.CENTER_LEFT);

            Label tituloAlertas = new Label("🔔 Alertas de Stock");
            tituloAlertas.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            if (totalAlertas > 0) {
                Label contadorAlertas = new Label("(" + totalAlertas + " pendientes)");
                contadorAlertas.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                headerAlertas.getChildren().addAll(tituloAlertas, contadorAlertas);
            } else {
                headerAlertas.getChildren().add(tituloAlertas);
            }

            Separator separador = new Separator();

            // LISTA DE ALERTAS (máximo 5)
            VBox listaAlertas = new VBox(10);

            if (productosProblema.isEmpty()) {
                // No hay problemas
                Label sinAlertas = new Label("✅ Excelente! No hay alertas de stock pendientes");
                sinAlertas.setStyle("-fx-text-fill: #27ae60; -fx-font-style: italic; -fx-font-size: 12px;");
                listaAlertas.getChildren().add(sinAlertas);

            } else {
                // LIMITAR A 5 ALERTAS
                int limite = Math.min(5, totalAlertas);

                for (int i = 0; i < limite; i++) {
                    Producto producto = productosProblema.get(i);
                    HBox alerta = crearAlertaReal(producto);
                    listaAlertas.getChildren().add(alerta);
                }

                // Si hay más de 5, mostrar botón "Ver todas"
                if (totalAlertas > 5) {
                    HBox footer = new HBox(10);
                    footer.setAlignment(Pos.CENTER);
                    footer.setPadding(new Insets(10, 0, 0, 0));

                    Label masAlertas = new Label("+" + (totalAlertas - 5) + " alertas más");
                    masAlertas.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-style: italic;");

                    Button btnVerTodas = new Button("Ver todas en Stock →");
                    btnVerTodas.setStyle(
                            "-fx-background-color: #3498db; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-size: 11px; " +
                                    "-fx-padding: 5 15; " +
                                    "-fx-cursor: hand;"
                    );
                    btnVerTodas.setOnAction(e -> cargarModuloStock());

                    footer.getChildren().addAll(masAlertas, btnVerTodas);
                    listaAlertas.getChildren().add(footer);
                }
            }

            panelAlertas.getChildren().addAll(headerAlertas, separador, listaAlertas);

            System.out.println("✅ Alertas dinámicas creadas (mostrando " +
                    Math.min(5, totalAlertas) + " de " + totalAlertas + ")");
            return panelAlertas;

        } catch (Exception e) {
            Label error = new Label("❌ Error al cargar alertas de stock");
            error.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

            VBox errorBox = new VBox(error);
            errorBox.setPadding(new Insets(20));

            System.err.println("❌ Error en alertas: " + e.getMessage());
            return errorBox;
        }
    }


private List<Producto> obtenerProductosConProblemas() {
    System.out.println("🔍 Buscando productos con problemas de stock...");

    try {
        List<Producto> todos = productoDAO.obtenerTodos();
        List<Producto> problemas = new ArrayList<>();

        for (Producto producto : todos) {
            if ("FISICO".equals(producto.getTipoProducto())) {
                if (producto.getStock() <= producto.getStockMinimo()) {
                    problemas.add(producto);
                }
            }
        }

        System.out.println("🔍 Encontrados " + problemas.size() + " productos con problemas");
        return problemas;

    } catch (Exception e) {
        System.err.println("❌ Error al buscar problemas: " + e.getMessage());
        return new ArrayList<>();
    }
}

    /**
     * CONCEPTO: Crear alerta individual con datos del producto real
     */
    private HBox crearAlertaReal(Producto producto) {
        HBox alerta = new HBox(10);
        alerta.setAlignment(Pos.CENTER_LEFT);
        alerta.setPadding(new Insets(5));

        String icono;
        String mensaje;
        String color;

        if (producto.getStock() <= 0) {
            // SIN STOCK = Crítico
            icono = "❌";
            mensaje = String.format("SIN STOCK: %s", producto.getNombre());
            color = "#e74c3c";
        } else if (producto.getStock() <= producto.getStockMinimo()) {
            // STOCK BAJO = Advertencia
            icono = "⚠️";
            mensaje = String.format("STOCK BAJO: %s (Quedan: %d)",
                    producto.getNombre(), producto.getStock());
            color = "#f39c12";
        } else {
            // No debería pasar, pero por seguridad
            icono = "ℹ️";
            mensaje = String.format("REVISAR: %s", producto.getNombre());
            color = "#3498db";
        }

        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 12px;");

        Label lblMensaje = new Label(mensaje);
        lblMensaje.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 12px;", color));

        alerta.getChildren().addAll(lblIcono, lblMensaje);

        return alerta;
    }

    /**
     * Método igual que antes - crear métrica individual
     */
    private VBox crearUnaMetricaSimple(String icono, String titulo, String valor) {
        VBox metrica = new VBox(5);
        metrica.setAlignment(Pos.CENTER);
        metrica.setPadding(new Insets(20));
        metrica.setPrefWidth(140);
        metrica.setStyle("-fx-background-color: white; -fx-border-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);");

        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 24px;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d; -fx-font-weight: bold;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        metrica.getChildren().addAll(lblIcono, lblTitulo, lblValor);

        return metrica;
    }

    // ===== NAVEGACIÓN (igual que antes) =====

    private void cargarModuloVentasCarrito() {
        try {
            System.out.println("🛒 Cargando módulo de ventas, pantalla carrito...");

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/libreria/view/carrito-view.fxml")
            );
            javafx.scene.Node contenido = loader.load();

            ventaController = loader.getController();
            ventaController.setContenedorPrincipal(contenidoPrincipal);
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(contenido);

            primaryStage.setTitle("📚 Librería Papelitos - Módulo de Ventas - Carrito");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar ventas: " + e.getMessage());
            mostrarError("No se pudo cargar el módulo de ventas");
        }
    }

    private void cargarModuloVentasHistorial(){
        try {
            System.out.println("🛒 Cargando módulo de ventas, pantalla historial...");

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/libreria/view/historial-ventas.fxml")
            );
            javafx.scene.Node contenido = loader.load();

            historialVentasController = loader.getController();
            historialVentasController.setContenedorPrincipal(contenidoPrincipal);
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(contenido);

            primaryStage.setTitle("📚 Librería Papelitos - Módulo de Ventas - Historial");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar ventas: " + e.getMessage());
            mostrarError("No se pudo cargar el módulo de ventas");
        }
    }

    private void cargarModuloStock() {
        try {
            System.out.println("🛒 Cargando módulo de stock...");

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/libreria/view/stock-view.fxml")
            );
            javafx.scene.Node contenido = loader.load();

            stockController = loader.getController();
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(contenido);

            primaryStage.setTitle("📚 Librería Papelitos - Módulo de Stock");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar stock: " + e.getMessage());
            mostrarError("No se pudo cargar el módulo de stock");
        }
    }

    private void cargarModuloStockYAbrirNuevo() {
        try {
            System.out.println("📦 Cargando módulo de stock y abriendo nuevo producto...");

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/libreria/view/stock-view.fxml")
            );
            javafx.scene.Node contenido = loader.load();

            // CONCEPTO: Obtener el controller después de cargar el FXML
            StockController stockController = loader.getController();

            // Llamar método público del controller para abrir el diálogo
            stockController.abrirDialogoNuevoProducto();

            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(contenido);

            primaryStage.setTitle("📚 Librería Papelitos - Nuevo Producto");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar stock: " + e.getMessage());
            mostrarError("No se pudo cargar el módulo de stock");
        }
    }

    private void cargarModuloServicios() {
        try {
            System.out.println("🛒 Cargando módulo de servicios...");

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/libreria/view/servicios-view.fxml")
            );
            javafx.scene.Node contenido = loader.load();

            serviciosController = loader.getController();
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(contenido);

            primaryStage.setTitle("📚 Librería Papelitos - Módulo de Servicios");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar servicios: " + e.getMessage());
            mostrarError("No se pudo cargar el módulo de servicios");
        }
    }

    private void cargarModuloProveedores() {
        try {
            System.out.println("🏢 Cargando módulo de proveedores...");

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/libreria/view/proveedores-view.fxml")
            );
            javafx.scene.Node contenido = loader.load();

            proveedorController = loader.getController();
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(contenido);

            primaryStage.setTitle("📚 Librería Papelitos - Módulo de Proveedores");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar proveedores: " + e.getMessage());
            mostrarError("No se pudo cargar el módulo de proveedores");
            e.printStackTrace();
        }
    }

    private void cargarModuloCompras() {
        try {
            System.out.println("🏢 Cargando módulo de compras...");

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/libreria/view/compras-view.fxml")
            );
            javafx.scene.Node contenido = loader.load();

            compraController = loader.getController();
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(contenido);

            primaryStage.setTitle("📚 Librería Papelitos - Módulo de Compras");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar compras: " + e.getMessage());
            mostrarError("No se pudo cargar el módulo de compras");
            e.printStackTrace();
        }
    }

    /**
     * Cargar módulo de gestión de usuarios (solo para admin)
     */
    private void cargarModuloUsuarios() {
        // Verificar permisos (doble seguridad)
        if (!SessionManager.esAdmin()) {
            mostrarError("No tiene permisos para acceder a este módulo");
            return;
        }

        try {
            System.out.println("👥 Cargando módulo de usuarios...");

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/libreria/view/usuarios-view.fxml")
            );
            javafx.scene.Node contenido = loader.load();

            // Obtener el controller (lo usaremos en el próximo paso)
            // UsuariosController usuariosController = loader.getController();

            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(contenido);

            primaryStage.setTitle("📚 Librería Papelitos - Gestión de Usuarios");

            System.out.println("✅ Módulo de usuarios cargado");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar módulo de usuarios: " + e.getMessage());
            e.printStackTrace();
            mostrarError("No se pudo cargar el módulo de usuarios");
        }
    }


    private void cerrarSesion() {
        System.out.println("🚪 Cerrando sesión...");

        // Confirmar con el usuario
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar Sesión");
        confirmacion.setHeaderText("¿Desea cerrar sesión?");
        confirmacion.setContentText("Volverá a la pantalla de inicio de sesión");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Limpiar sesión
                    SessionManager.logout();
                    System.out.println("✅ Sesión cerrada");

                    // Obtener Stage actual
                    Stage stage = primaryStage;

                    // Crear y mostrar LoginApp
                    LoginApp loginApp = new LoginApp();
                    loginApp.start(stage);

                    System.out.println("✅ Vuelto al login");

                } catch (Exception e) {
                    System.err.println("❌ Error al cerrar sesión: " + e.getMessage());
                    e.printStackTrace();
                    mostrarError("Error al cerrar sesión");
                }
            }
        });
    }

    /**
     * Cargar módulo de reportes (pestaña general)
     */
    private void cargarModuloReportes() {
        try {
            System.out.println("📊 Cargando módulo de reportes...");

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/libreria/view/reportes-view.fxml")
            );
            javafx.scene.Node contenido = loader.load();

            reportesController = loader.getController();
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(contenido);

            primaryStage.setTitle("📚 Librería Papelitos - Módulo de Reportes");

            System.out.println("✅ Módulo de reportes cargado");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar reportes: " + e.getMessage());
            e.printStackTrace();
            mostrarError("No se pudo cargar el módulo de reportes");
        }
    }

    /**
     * Cargar módulo de reportes directamente en la pestaña de Ventas Diarias
     */
    private void cargarModuloReportesVentasDia() {
        try {
            System.out.println("📊 Cargando módulo de reportes (Ventas del Día)...");

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/libreria/view/reportes-view.fxml")
            );
            javafx.scene.Node contenido = loader.load();

            reportesController = loader.getController();

            // IMPORTANTE: Configurar filtro de HOY automáticamente
            reportesController.configurarFiltroHoy();

            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(contenido);

            primaryStage.setTitle("📚 Librería Papelitos - Ventas del Día");

            System.out.println("✅ Reporte de ventas del día cargado automáticamente");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar reportes: " + e.getMessage());
            e.printStackTrace();
            mostrarError("No se pudo cargar el módulo de reportes");
        }
    }

    private void cargarModuloClientes() {
        try {
            System.out.println("👥 Cargando módulo de clientes...");

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/libreria/view/clientes-view.fxml")
            );
            javafx.scene.Node contenido = loader.load();

            clientesController = loader.getController();
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(contenido);

            primaryStage.setTitle("📚 Librería Papelitos - Gestión de Clientes");

            System.out.println("✅ Módulo de clientes cargado");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar módulo de clientes: " + e.getMessage());
            e.printStackTrace();
            mostrarError("No se pudo cargar el módulo de clientes");
        }
    }


    private void mostrarPlaceholder(String titulo, String mensaje) {
        VBox placeholder = new VBox(20);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setPadding(new Insets(50));
        placeholder.setStyle("-fx-background-color: #ecf0f1;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label lblMensaje = new Label(mensaje);
        lblMensaje.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Button btnVolver = new Button("🏠 Volver al Dashboard");
        btnVolver.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20;");
        btnVolver.setOnAction(e -> mostrarDashboardConDatosReales());  // ← ACTUALIZA datos al volver

        placeholder.getChildren().addAll(lblTitulo, lblMensaje, btnVolver);

        contenidoPrincipal.getChildren().clear();
        contenidoPrincipal.getChildren().add(placeholder);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}