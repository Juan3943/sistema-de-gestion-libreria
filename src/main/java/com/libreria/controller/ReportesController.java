package com.libreria.controller;

import com.libreria.dao.ReportesDAO;
import com.libreria.dao.VentaDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import com.libreria.model.reportes.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * CONTROLLER SIMPLIFICADO
 * Sin genéricos, sin lambdas complejas, sin interfaces funcionales
 * Código más largo pero más fácil de entender
 */
public class ReportesController {

    private ReportesDAO dao = new ReportesDAO();
    private VentaDAO ventaDAO = new VentaDAO();
    private DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DateTimeFormatter formatoFechaCorto = DateTimeFormatter.ofPattern("dd/MM");

    // Elementos UI - Ventas Diarias
    @FXML private DatePicker dp1FechaInicio;
    @FXML private DatePicker dp1FechaFin;
    @FXML private TableView<ReporteVentaDiaria> table1VentasDiarias;
    @FXML private TableColumn<ReporteVentaDiaria, LocalDate> col1Fecha;
    @FXML private TableColumn<ReporteVentaDiaria, Integer> col1CantVentas;
    @FXML private TableColumn<ReporteVentaDiaria, Double> col1TotalVendido;
    @FXML private LineChart<String, Number> chart1VentasDiarias;
    @FXML private TableColumn<ReporteVentaDiaria, Void> col1Acciones;

    // Elementos UI - Ventas Semanales
    @FXML private DatePicker dp2FechaInicio;
    @FXML private DatePicker dp2FechaFin;
    @FXML private TableView<ReporteVentaSemanal> table2VentasSemanales;
    @FXML private TableColumn<ReporteVentaSemanal, String> col2Semana;
    @FXML private TableColumn<ReporteVentaSemanal, LocalDate> col2FechaInicio;
    @FXML private TableColumn<ReporteVentaSemanal, LocalDate> col2FechaFin;
    @FXML private TableColumn<ReporteVentaSemanal, Integer> col2CantVentas;
    @FXML private TableColumn<ReporteVentaSemanal, Double> col2TotalVendido;
    @FXML private BarChart<String, Number> chart2VentasSemanales;

    // Elementos UI - Productos Top
    @FXML private DatePicker dp3FechaInicio;
    @FXML private DatePicker dp3FechaFin;
    @FXML private TableView<ReporteProductoTop> table3ProductosTop;
    @FXML private TableColumn<ReporteProductoTop, String> col3Codigo;
    @FXML private TableColumn<ReporteProductoTop, String> col3Nombre;
    @FXML private TableColumn<ReporteProductoTop, Integer> col3Cantidad;
    @FXML private TableColumn<ReporteProductoTop, Double> col3TotalGenerado;
    @FXML private BarChart<String, Number> chart3ProductosTop;

    // Elementos UI - Corte de Caja
    @FXML private DatePicker dp4Fecha;
    @FXML private Label lbl4TotalVentas;
    @FXML private Label lbl4MontoTotal;
    @FXML private TableView<ReporteCorteCaja> table4CorteCaja;
    @FXML private TableColumn<ReporteCorteCaja, String> col4MetodoPago;
    @FXML private TableColumn<ReporteCorteCaja, Integer> col4CantVentas;
    @FXML private TableColumn<ReporteCorteCaja, Double> col4MontoTotal;
    @FXML private PieChart chart4CorteCaja;

    // Elementos UI - Stock Crítico
    @FXML private Label lbl5CantidadCriticos;
    @FXML private TableView<ReporteStockCritico> table5StockCritico;
    @FXML private TableColumn<ReporteStockCritico, String> col5Codigo;
    @FXML private TableColumn<ReporteStockCritico, String> col5Producto;
    @FXML private TableColumn<ReporteStockCritico, String> col5Categoria;
    @FXML private TableColumn<ReporteStockCritico, Integer> col5StockActual;
    @FXML private TableColumn<ReporteStockCritico, Integer> col5StockMinimo;
    @FXML private TableColumn<ReporteStockCritico, Integer> col5Faltante;

    // Elementos UI - Sin Movimiento
    @FXML private TextField txt6Dias;
    @FXML private Label lbl6CantidadProductos;
    @FXML private Label lbl6ValorInventario;
    @FXML private TableView<com.libreria.model.reportes.ReporteProductoSinMovimiento> table6SinMovimiento;
    @FXML private TableColumn<com.libreria.model.reportes.ReporteProductoSinMovimiento, String> col6Codigo;
    @FXML private TableColumn<com.libreria.model.reportes.ReporteProductoSinMovimiento, String> col6Producto;
    @FXML private TableColumn<com.libreria.model.reportes.ReporteProductoSinMovimiento, String> col6Categoria;
    @FXML private TableColumn<com.libreria.model.reportes.ReporteProductoSinMovimiento, Integer> col6Stock;
    @FXML private TableColumn<com.libreria.model.reportes.ReporteProductoSinMovimiento, Integer> col6Dias;
    @FXML private TableColumn<com.libreria.model.reportes.ReporteProductoSinMovimiento, Double> col6Valor;

    @FXML private Label lblStatus;
    @FXML private Label lblFechaActual;

    @FXML
    public void initialize() {
        configurarFechas();
        configurarTablas();
        lblFechaActual.setText("Fecha: " + LocalDate.now().format(formatoFecha));
        lblStatus.setText("Sistema listo");
    }

    public void configurarFiltroHoy() {
        LocalDate hoy = LocalDate.now();
        dp1FechaInicio.setValue(hoy);
        dp1FechaFin.setValue(hoy);
        generarReporteVentasDiarias();
    }

    private void configurarFechas() {
        LocalDate hoy = LocalDate.now();
        dp1FechaInicio.setValue(hoy.minusDays(30));
        dp1FechaFin.setValue(hoy);
        dp2FechaInicio.setValue(hoy.minusDays(30));
        dp2FechaFin.setValue(hoy);
        dp3FechaInicio.setValue(hoy.minusDays(30));
        dp3FechaFin.setValue(hoy);
        dp4Fecha.setValue(hoy);
    }

    private void configurarTablas() {
        configurarTablaVentasDiarias();
        configurarTablaVentasSemanales();
        configurarTablaProductosTop();
        configurarTablaCorteCaja();
        configurarTablaStockCritico();
        configurarTablaSinMovimiento();
    }

    // ==========================================
    // CONFIGURACIÓN DE TABLAS
    // ==========================================

    private void configurarTablaVentasDiarias() {
        col1Fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        col1CantVentas.setCellValueFactory(new PropertyValueFactory<>("cantidadVentas"));
        col1TotalVendido.setCellValueFactory(new PropertyValueFactory<>("totalVendido"));

        // Formatear fecha CON AÑO
        col1Fecha.setCellFactory(column -> {
            return new TableCell<ReporteVentaDiaria, LocalDate>() {
                @Override
                protected void updateItem(LocalDate fecha, boolean empty) {
                    super.updateItem(fecha, empty);
                    if (empty || fecha == null) {
                        setText(null);
                    } else {
                        setText(fecha.format(formatoFecha));
                    }
                }
            };
        });

        // Formatear dinero
        col1TotalVendido.setCellFactory(column -> {
            return new TableCell<ReporteVentaDiaria, Double>() {
                @Override
                protected void updateItem(Double monto, boolean empty) {
                    super.updateItem(monto, empty);
                    if (empty || monto == null) {
                        setText(null);
                    } else {
                        setText(String.format("$%,.2f", monto));
                    }
                }
            };
        });

        // NUEVA COLUMNA DE ACCIONES
        col1Acciones.setCellFactory(column -> {
            return new TableCell<ReporteVentaDiaria, Void>() {
                private final Button btnVerVentas = new Button("👁️ Ver Ventas");

                {
                    btnVerVentas.setStyle(
                            "-fx-background-color: #3498db; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-size: 11px; " +
                                    "-fx-padding: 5 10;"
                    );

                    btnVerVentas.setOnAction(event -> {
                        ReporteVentaDiaria reporte = getTableView().getItems().get(getIndex());
                        mostrarVentasDelDia(reporte.getFecha());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btnVerVentas);
                    }
                }
            };
        });
    }

    private void configurarTablaVentasSemanales() {
        col2Semana.setCellValueFactory(new PropertyValueFactory<>("semana"));
        col2FechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        col2FechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        col2CantVentas.setCellValueFactory(new PropertyValueFactory<>("cantidadVentas"));
        col2TotalVendido.setCellValueFactory(new PropertyValueFactory<>("totalVendido"));

        // Formatear fechas
        col2FechaInicio.setCellFactory(column -> {
            return new TableCell<ReporteVentaSemanal, LocalDate>() {
                @Override
                protected void updateItem(LocalDate fecha, boolean empty) {
                    super.updateItem(fecha, empty);
                    setText(empty || fecha == null ? null : fecha.format(formatoFecha));
                }
            };
        });

        col2FechaFin.setCellFactory(column -> {
            return new TableCell<ReporteVentaSemanal, LocalDate>() {
                @Override
                protected void updateItem(LocalDate fecha, boolean empty) {
                    super.updateItem(fecha, empty);
                    setText(empty || fecha == null ? null : fecha.format(formatoFecha));
                }
            };
        });

        // Formatear dinero
        col2TotalVendido.setCellFactory(column -> {
            return new TableCell<ReporteVentaSemanal, Double>() {
                @Override
                protected void updateItem(Double monto, boolean empty) {
                    super.updateItem(monto, empty);
                    setText(empty || monto == null ? null : String.format("$%,.2f", monto));
                }
            };
        });
    }

    private void configurarTablaProductosTop() {
        col3Codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        col3Nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col3Cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadVendida"));
        col3TotalGenerado.setCellValueFactory(new PropertyValueFactory<>("totalGenerado"));

        col3TotalGenerado.setCellFactory(column -> {
            return new TableCell<ReporteProductoTop, Double>() {
                @Override
                protected void updateItem(Double monto, boolean empty) {
                    super.updateItem(monto, empty);
                    setText(empty || monto == null ? null : String.format("$%,.2f", monto));
                }
            };
        });
    }

    private void configurarTablaCorteCaja() {
        col4MetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        col4CantVentas.setCellValueFactory(new PropertyValueFactory<>("cantidadVentas"));
        col4MontoTotal.setCellValueFactory(new PropertyValueFactory<>("montoTotal"));

        col4MontoTotal.setCellFactory(column -> {
            return new TableCell<ReporteCorteCaja, Double>() {
                @Override
                protected void updateItem(Double monto, boolean empty) {
                    super.updateItem(monto, empty);
                    setText(empty || monto == null ? null : String.format("$%,.2f", monto));
                }
            };
        });
    }

    private void configurarTablaStockCritico() {
        col5Codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        col5Producto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        col5Categoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        col5StockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        col5StockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        col5Faltante.setCellValueFactory(new PropertyValueFactory<>("cantidadFaltante"));
    }

    private void configurarTablaSinMovimiento() {
        col6Codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        col6Producto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        col6Categoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        col6Stock.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        col6Dias.setCellValueFactory(new PropertyValueFactory<>("diasSinVenta"));
        col6Valor.setCellValueFactory(new PropertyValueFactory<>("valorInventario"));

        col6Valor.setCellFactory(column -> {
            return new TableCell<com.libreria.model.reportes.ReporteProductoSinMovimiento, Double>() {
                @Override
                protected void updateItem(Double monto, boolean empty) {
                    super.updateItem(monto, empty);
                    setText(empty || monto == null ? null : String.format("$%,.2f", monto));
                }
            };
        });
    }

    // ==========================================
    // EVENTOS: GENERAR REPORTES
    // ==========================================

    @FXML
    public void generarReporteVentasDiarias() {
        if (!validarFechas(dp1FechaInicio, dp1FechaFin)) return;

        lblStatus.setText("Generando reporte...");
        List<ReporteVentaDiaria> reportes = dao.obtenerVentasDiarias(
                dp1FechaInicio.getValue(), dp1FechaFin.getValue()
        );

        table1VentasDiarias.setItems(FXCollections.observableArrayList(reportes));
        generarGraficoVentasDiarias(reportes);
        lblStatus.setText("Reporte generado: " + reportes.size() + " días");
    }

    @FXML
    public void generarReporteVentasSemanales() {
        if (!validarFechas(dp2FechaInicio, dp2FechaFin)) return;

        lblStatus.setText("Generando reporte...");
        List<ReporteVentaSemanal> reportes = dao.obtenerVentasSemanales(
                dp2FechaInicio.getValue(), dp2FechaFin.getValue()
        );

        table2VentasSemanales.setItems(FXCollections.observableArrayList(reportes));
        generarGraficoVentasSemanales(reportes);
        lblStatus.setText("Reporte generado: " + reportes.size() + " semanas");
    }

    @FXML
    public void generarReporteProductosTop() {
        if (!validarFechas(dp3FechaInicio, dp3FechaFin)) return;

        lblStatus.setText("Generando reporte...");
        List<ReporteProductoTop> reportes = dao.obtenerProductosMasVendidos(
                dp3FechaInicio.getValue(), dp3FechaFin.getValue()
        );

        table3ProductosTop.setItems(FXCollections.observableArrayList(reportes));
        generarGraficoProductosTop(reportes);
        lblStatus.setText("Reporte generado: Top " + reportes.size());
    }

    @FXML
    public void generarReporteCorteCaja() {
        if (dp4Fecha.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar una fecha");
            return;
        }

        lblStatus.setText("Generando corte de caja...");
        List<ReporteCorteCaja> reportes = dao.obtenerCorteCaja(dp4Fecha.getValue());

        table4CorteCaja.setItems(FXCollections.observableArrayList(reportes));
        actualizarResumenCorteCaja(reportes);
        generarGraficoCorteCaja(reportes);
        lblStatus.setText("Corte de caja generado");
    }

    @FXML
    public void generarReporteStockCritico() {
        lblStatus.setText("Generando reporte...");
        List<ReporteStockCritico> reportes = dao.obtenerStockCritico();

        table5StockCritico.setItems(FXCollections.observableArrayList(reportes));
        lbl5CantidadCriticos.setText(reportes.size() + " productos en stock crítico");
        lblStatus.setText("Reporte generado: " + reportes.size() + " productos");
    }

    @FXML
    public void generarReporteProductosSinMovimiento() {
        int dias = 30;
        try {
            dias = Integer.parseInt(txt6Dias.getText());
        } catch (NumberFormatException e) {
            txt6Dias.setText("30");
        }

        lblStatus.setText("Generando reporte...");
        List<com.libreria.model.reportes.ReporteProductoSinMovimiento> reportes = dao.obtenerProductosSinMovimiento(dias);

        table6SinMovimiento.setItems(FXCollections.observableArrayList(reportes));
        actualizarResumenSinMovimiento(reportes);
        lblStatus.setText("Reporte generado: " + reportes.size() + " productos");
    }

    // ==========================================
    // GRÁFICOS
    // ==========================================

    private void generarGraficoVentasDiarias(List<ReporteVentaDiaria> reportes) {
        chart1VentasDiarias.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Total Vendido");

        for (ReporteVentaDiaria r : reportes) {
            String fechaCorta = r.getFecha().format(DateTimeFormatter.ofPattern("dd/MM"));
            serie.getData().add(new XYChart.Data<>(fechaCorta, r.getTotalVendido()));
        }

        chart1VentasDiarias.getData().add(serie);
    }

    private void generarGraficoVentasSemanales(List<ReporteVentaSemanal> reportes) {
        chart2VentasSemanales.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Total Vendido");

        for (ReporteVentaSemanal r : reportes) {
            serie.getData().add(new XYChart.Data<>(r.getSemana(), r.getTotalVendido()));
        }

        chart2VentasSemanales.getData().add(serie);
    }

    private void generarGraficoProductosTop(List<ReporteProductoTop> reportes) {
        chart3ProductosTop.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Cantidad Vendida");

        for (ReporteProductoTop r : reportes) {
            String nombreCorto = r.getNombre();
            if (nombreCorto.length() > 20) {
                nombreCorto = nombreCorto.substring(0, 20) + "...";
            }
            serie.getData().add(new XYChart.Data<>(nombreCorto, r.getCantidadVendida()));
        }

        chart3ProductosTop.getData().add(serie);
    }

    private void generarGraficoCorteCaja(List<ReporteCorteCaja> reportes) {
        chart4CorteCaja.getData().clear();

        for (ReporteCorteCaja r : reportes) {
            String etiqueta = r.getMetodoPago() + " ($" + String.format("%,.2f", r.getMontoTotal()) + ")";
            PieChart.Data data = new PieChart.Data(etiqueta, r.getMontoTotal());
            chart4CorteCaja.getData().add(data);
        }
    }

    // ==========================================
    // RESÚMENES
    // ==========================================

    private void actualizarResumenCorteCaja(List<ReporteCorteCaja> reportes) {
        int totalVentas = 0;
        double montoTotal = 0.0;

        for (ReporteCorteCaja r : reportes) {
            totalVentas += r.getCantidadVentas();
            montoTotal += r.getMontoTotal();
        }

        lbl4TotalVentas.setText(String.valueOf(totalVentas));
        lbl4MontoTotal.setText(String.format("$%,.2f", montoTotal));
    }

    private void actualizarResumenSinMovimiento(List<com.libreria.model.reportes.ReporteProductoSinMovimiento> reportes) {
        double valorTotal = 0.0;

        for (com.libreria.model.reportes.ReporteProductoSinMovimiento r : reportes) {
            valorTotal += r.getValorInventario();
        }

        lbl6CantidadProductos.setText(String.valueOf(reportes.size()));
        lbl6ValorInventario.setText(String.format("$%,.2f", valorTotal));
    }



    @FXML
    public void exportarVentasDiarias() {
        List<ReporteVentaDiaria> datos = table1VentasDiarias.getItems();
        if (datos == null || datos.isEmpty()) {
            mostrarAlerta("Advertencia", "No hay datos para exportar");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Ventas Diarias");
        fc.setInitialFileName("ventas_diarias_" + LocalDate.now() + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File archivo = fc.showSaveDialog(table1VentasDiarias.getScene().getWindow());
        if (archivo != null) {
            try {
                FileWriter w = new FileWriter(archivo);
                w.write("Fecha,Cantidad de Ventas,Total Vendido\n");
                for (ReporteVentaDiaria r : datos) {
                    w.write(r.getFecha().format(formatoFecha) + "," + r.getCantidadVentas() + "," +
                            String.format("%.2f", r.getTotalVendido()) + "\n");
                }
                w.close();
                mostrarAlerta("Éxito", "Archivo exportado correctamente");
                lblStatus.setText("Exportado: " + archivo.getName());
            } catch (IOException e) {
                mostrarAlerta("Error", "Error al exportar: " + e.getMessage());
            }
        }
    }

    @FXML
    public void exportarVentasSemanales() {
        List<ReporteVentaSemanal> datos = table2VentasSemanales.getItems();
        if (datos == null || datos.isEmpty()) {
            mostrarAlerta("Advertencia", "No hay datos para exportar");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Ventas Semanales");
        fc.setInitialFileName("ventas_semanales_" + LocalDate.now() + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File archivo = fc.showSaveDialog(table2VentasSemanales.getScene().getWindow());
        if (archivo != null) {
            try {
                FileWriter w = new FileWriter(archivo);
                w.write("Semana,Fecha Inicio,Fecha Fin,Cantidad Ventas,Total Vendido\n");
                for (ReporteVentaSemanal r : datos) {
                    w.write(r.getSemana() + "," + r.getFechaInicio().format(formatoFecha) + "," +
                            r.getFechaFin().format(formatoFecha) + "," + r.getCantidadVentas() + "," +
                            String.format("%.2f", r.getTotalVendido()) + "\n");
                }
                w.close();
                mostrarAlerta("Éxito", "Archivo exportado correctamente");
                lblStatus.setText("Exportado: " + archivo.getName());
            } catch (IOException e) {
                mostrarAlerta("Error", "Error al exportar: " + e.getMessage());
            }
        }
    }

    @FXML
    public void exportarProductosTop() {
        List<ReporteProductoTop> datos = table3ProductosTop.getItems();
        if (datos == null || datos.isEmpty()) {
            mostrarAlerta("Advertencia", "No hay datos para exportar");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Top Productos");
        fc.setInitialFileName("productos_top_" + LocalDate.now() + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File archivo = fc.showSaveDialog(table3ProductosTop.getScene().getWindow());
        if (archivo != null) {
            try {
                FileWriter w = new FileWriter(archivo);
                w.write("Código,Producto,Cantidad Vendida,Total Generado\n");
                for (ReporteProductoTop r : datos) {
                    w.write(r.getCodigo() + "," + r.getNombre() + "," + r.getCantidadVendida() + "," +
                            String.format("%.2f", r.getTotalGenerado()) + "\n");
                }
                w.close();
                mostrarAlerta("Éxito", "Archivo exportado correctamente");
                lblStatus.setText("Exportado: " + archivo.getName());
            } catch (IOException e) {
                mostrarAlerta("Error", "Error al exportar: " + e.getMessage());
            }
        }
    }

    @FXML
    public void exportarCorteCaja() {
        List<ReporteCorteCaja> datos = table4CorteCaja.getItems();
        if (datos == null || datos.isEmpty()) {
            mostrarAlerta("Advertencia", "No hay datos para exportar");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Corte de Caja");
        fc.setInitialFileName("corte_caja_" + LocalDate.now() + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File archivo = fc.showSaveDialog(table4CorteCaja.getScene().getWindow());
        if (archivo != null) {
            try {
                FileWriter w = new FileWriter(archivo);
                w.write("Método de Pago,Cantidad Ventas,Monto Total\n");
                for (ReporteCorteCaja r : datos) {
                    w.write(r.getMetodoPago() + "," + r.getCantidadVentas() + "," +
                            String.format("%.2f", r.getMontoTotal()) + "\n");
                }
                w.close();
                mostrarAlerta("Éxito", "Archivo exportado correctamente");
                lblStatus.setText("Exportado: " + archivo.getName());
            } catch (IOException e) {
                mostrarAlerta("Error", "Error al exportar: " + e.getMessage());
            }
        }
    }

    @FXML
    public void exportarStockCritico() {
        List<ReporteStockCritico> datos = table5StockCritico.getItems();
        if (datos == null || datos.isEmpty()) {
            mostrarAlerta("Advertencia", "No hay datos para exportar");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Stock Crítico");
        fc.setInitialFileName("stock_critico_" + LocalDate.now() + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File archivo = fc.showSaveDialog(table5StockCritico.getScene().getWindow());
        if (archivo != null) {
            try {
                FileWriter w = new FileWriter(archivo);
                w.write("Código,Producto,Categoría,Stock Actual,Stock Mínimo,Faltante\n");
                for (ReporteStockCritico r : datos) {
                    w.write(r.getCodigo() + "," + r.getProducto() + "," + r.getCategoria() + "," +
                            r.getStockActual() + "," + r.getStockMinimo() + "," + r.getCantidadFaltante() + "\n");
                }
                w.close();
                mostrarAlerta("Éxito", "Archivo exportado correctamente");
                lblStatus.setText("Exportado: " + archivo.getName());
            } catch (IOException e) {
                mostrarAlerta("Error", "Error al exportar: " + e.getMessage());
            }
        }
    }

    @FXML
    public void exportarProductosSinMovimiento() {
        List<com.libreria.model.reportes.ReporteProductoSinMovimiento> datos = table6SinMovimiento.getItems();
        if (datos == null || datos.isEmpty()) {
            mostrarAlerta("Advertencia", "No hay datos para exportar");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Productos Sin Movimiento");
        fc.setInitialFileName("productos_sin_movimiento_" + LocalDate.now() + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File archivo = fc.showSaveDialog(table6SinMovimiento.getScene().getWindow());
        if (archivo != null) {
            try {
                FileWriter w = new FileWriter(archivo);
                w.write("Código,Producto,Categoría,Stock,Días sin venta,Valor Inventario\n");
                for (com.libreria.model.reportes.ReporteProductoSinMovimiento r : datos) {
                    w.write(r.getCodigo() + "," + r.getProducto() + "," + r.getCategoria() + "," +
                            r.getStockActual() + "," + r.getDiasSinVenta() + "," +
                            String.format("%.2f", r.getValorInventario()) + "\n");
                }
                w.close();
                mostrarAlerta("Éxito", "Archivo exportado correctamente");
                lblStatus.setText("Exportado: " + archivo.getName());
            } catch (IOException e) {
                mostrarAlerta("Error", "Error al exportar: " + e.getMessage());
            }
        }
    }

    private boolean validarFechas(DatePicker inicio, DatePicker fin) {
        if (inicio.getValue() == null || fin.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar ambas fechas");
            return false;
        }
        if (inicio.getValue().isAfter(fin.getValue())) {
            mostrarAlerta("Error", "La fecha inicial no puede ser posterior a la final");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    private void mostrarVentasDelDia(LocalDate fecha) {
        System.out.println("👁️ Mostrando ventas del día: " + fecha.format(formatoFecha));

        List<VentaResumida> ventas = obtenerVentasDelDia(fecha);

        if (ventas.isEmpty()) {
            mostrarAlerta("Sin ventas", "No hay ventas registradas para el " + fecha.format(formatoFecha));
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Ventas del " + fecha.format(formatoFecha));
        dialog.setHeaderText(ventas.size() + " ventas encontradas");

        TableView<VentaResumida> tablaVentas = new TableView<>();
        tablaVentas.setPrefHeight(400);
        tablaVentas.setPrefWidth(700);

        TableColumn<VentaResumida, String> colNum = new TableColumn<>("Nº Comprobante");
        colNum.setCellValueFactory(new PropertyValueFactory<>("numeroComprobante"));
        colNum.setPrefWidth(120);

        TableColumn<VentaResumida, String> colHora = new TableColumn<>("Hora");
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colHora.setPrefWidth(80);

        TableColumn<VentaResumida, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colCliente.setPrefWidth(150);

        TableColumn<VentaResumida, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setPrefWidth(100);
        colTotal.setCellFactory(column -> new TableCell<VentaResumida, Double>() {
            @Override
            protected void updateItem(Double monto, boolean empty) {
                super.updateItem(monto, empty);
                setText(empty || monto == null ? null : String.format("$%,.2f", monto));
            }
        });

        TableColumn<VentaResumida, Void> colDetalle = new TableColumn<>("Acción");
        colDetalle.setPrefWidth(120);
        colDetalle.setCellFactory(column -> {
            return new TableCell<VentaResumida, Void>() {
                private final Button btnDetalle = new Button("📄 Detalle");

                {
                    btnDetalle.setStyle(
                            "-fx-background-color: #27ae60; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-size: 10px; " +
                                    "-fx-padding: 4 8;"
                    );

                    btnDetalle.setOnAction(event -> {
                        VentaResumida venta = getTableView().getItems().get(getIndex());
                        mostrarDetalleVenta(venta.getIdVenta());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btnDetalle);
                }
            };
        });

        tablaVentas.getColumns().addAll(colNum, colHora, colCliente, colTotal, colDetalle);
        tablaVentas.setItems(FXCollections.observableArrayList(ventas));

        VBox contenido = new VBox(10);
        contenido.getChildren().add(tablaVentas);
        contenido.setAlignment(Pos.CENTER);

        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private List<VentaResumida> obtenerVentasDelDia(LocalDate fecha) {
        List<ReportesDAO.VentaDelDia> ventasDAO = dao.obtenerVentasDelDia(fecha);

        List<VentaResumida> ventas = new java.util.ArrayList<>();
        for (ReportesDAO.VentaDelDia v : ventasDAO) {
            ventas.add(new VentaResumida(
                    v.idVenta,
                    v.numeroComprobante,
                    v.hora,
                    v.cliente,
                    v.total
            ));
        }

        return ventas;
    }

    private void mostrarDetalleVenta(int idVenta) {
        System.out.println("📄 Mostrando detalle de venta #" + idVenta);

        VentaDAO.VentaCompleta venta = ventaDAO.obtenerVentaCompleta(idVenta);

        if (venta == null) {
            mostrarAlerta("Error", "No se pudo obtener el detalle de la venta");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalle Venta - " + venta.numeroComprobante);

        VBox contenido = new VBox(15);
        contenido.setStyle("-fx-padding: 20;");

        Label lblInfo = new Label(String.format(
                "Fecha: %s\n" +
                        "Cliente: %s\n" +
                        "Vendedor: %s",
                venta.fecha,
                venta.cliente != null ? venta.cliente : "General",
                venta.vendedor
        ));
        lblInfo.setStyle("-fx-font-size: 12px;");

        TableView<VentaDAO.DetalleVenta> tablaDetalle = new TableView<>();
        tablaDetalle.setPrefHeight(300);

        TableColumn<VentaDAO.DetalleVenta, String> colCod = new TableColumn<>("Código");
        colCod.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().codigo));
        colCod.setPrefWidth(100);

        TableColumn<VentaDAO.DetalleVenta, String> colProd = new TableColumn<>("Producto");
        colProd.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().producto));
        colProd.setPrefWidth(250);

        TableColumn<VentaDAO.DetalleVenta, Integer> colCant = new TableColumn<>("Cant.");
        colCant.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().cantidad).asObject());
        colCant.setPrefWidth(60);

        TableColumn<VentaDAO.DetalleVenta, Double> colPrec = new TableColumn<>("Precio");
        colPrec.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().precioUnitario).asObject());
        colPrec.setPrefWidth(80);
        colPrec.setCellFactory(column -> new TableCell<VentaDAO.DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty || precio == null ? null : String.format("$%,.2f", precio));
            }
        });

        TableColumn<VentaDAO.DetalleVenta, Double> colSub = new TableColumn<>("Subtotal");
        colSub.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().subtotal).asObject());
        colSub.setPrefWidth(100);
        colSub.setCellFactory(column -> new TableCell<VentaDAO.DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                setText(empty || subtotal == null ? null : String.format("$%,.2f", subtotal));
            }
        });

        tablaDetalle.getColumns().addAll(colCod, colProd, colCant, colPrec, colSub);
        tablaDetalle.setItems(FXCollections.observableArrayList(venta.detalles));

        Label lblTotales = new Label(String.format(
                "Subtotal: $%,.2f\n" +
                        "TOTAL: $%,.2f",
                venta.subtotal,
                venta.total
        ));
        lblTotales.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        contenido.getChildren().addAll(lblInfo, tablaDetalle, lblTotales);

        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    // CLASE AUXILIAR
    public static class VentaResumida {
        private int idVenta;
        private String numeroComprobante;
        private String hora;
        private String cliente;
        private double total;

        public VentaResumida(int idVenta, String numeroComprobante, String hora, String cliente, double total) {
            this.idVenta = idVenta;
            this.numeroComprobante = numeroComprobante;
            this.hora = hora;
            this.cliente = cliente;
            this.total = total;
        }

        public int getIdVenta() { return idVenta; }
        public String getNumeroComprobante() { return numeroComprobante; }
        public String getHora() { return hora; }
        public String getCliente() { return cliente; }
        public double getTotal() { return total; }
    }
}