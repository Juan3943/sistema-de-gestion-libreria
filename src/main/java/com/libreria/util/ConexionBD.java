package com.libreria.util;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Clase para gestionar la conexión a la base de datos MySQL.
 * Lee las credenciales desde el archivo database.properties
 */
public class ConexionBD {

    // Variables para almacenar los datos de conexión
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static String DRIVER;

    // Bloque estático: se ejecuta UNA VEZ cuando se carga la clase
    static {
        cargarConfiguracion();
    }

    /**
     * Carga la configuración desde database.properties
     */
    private static void cargarConfiguracion() {
        Properties properties = new Properties();

        try {
            // Intenta cargar el archivo database.properties
            FileInputStream archivo = new FileInputStream("database.properties");
            properties.load(archivo);

            // Lee los valores del archivo
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");
            DRIVER = properties.getProperty("db.driver");

            // Carga el driver de MySQL
            Class.forName(DRIVER);

            System.out.println("✅ Configuración de base de datos cargada correctamente");

        } catch (IOException e) {
            System.err.println("❌ ERROR: No se encontró el archivo database.properties");
            System.err.println("   Asegúrate de tener el archivo en la raíz del proyecto");
            System.err.println("   Puedes copiar database.properties.example y renombrarlo");
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERROR: No se encontró el driver de MySQL");
            System.err.println("   Verifica que tengas la dependencia en pom.xml");
            e.printStackTrace();
        }
    }

    /**
     * Obtiene una conexión a la base de datos
     * @return Connection objeto de conexión
     * @throws SQLException si hay error al conectar
     */
    public static Connection getConnection() throws SQLException {
        if (URL == null || USER == null || PASSWORD == null) {
            throw new SQLException("La configuración de base de datos no está cargada correctamente");
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Método para probar la conexión (útil para debugging)
     */
    public static void probarConexion() {
        try {
            Connection conn = getConnection();
            System.out.println("✅ Conexión exitosa a: " + URL);
            conn.close();
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la base de datos:");
            System.err.println("   URL: " + URL);
            System.err.println("   Usuario: " + USER);
            e.printStackTrace();
        }
    }
}
