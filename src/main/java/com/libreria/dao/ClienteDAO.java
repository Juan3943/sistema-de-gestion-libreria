package com.libreria.dao;

import com.libreria.model.Cliente;
import com.libreria.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    /**
     * Obtener todos los clientes activos
     */
    public List<Cliente> obtenerActivos() {
        return obtenerPorEstado(true);
    }

    /**
     * Obtener todos los clientes inactivos
     */
    public List<Cliente> obtenerInactivos() {
        return obtenerPorEstado(false);
    }

    /**
     * Obtener todos los clientes (activos e inactivos)
     */
    public List<Cliente> obtenerTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY nombre";

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Obtener clientes filtrados por estado
     */
    private List<Cliente> obtenerPorEstado(boolean activo) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE activo = ? ORDER BY nombre";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, activo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener clientes por estado: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Buscar clientes por nombre o CUIT
     */
    public List<Cliente> buscar(String query, Boolean soloActivos) {
        List<Cliente> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM cliente WHERE (nombre LIKE ? OR cuit LIKE ?)"
        );

        if (soloActivos != null) {
            sql.append(" AND activo = ?");
        }

        sql.append(" ORDER BY nombre");

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            String parametroBusqueda = "%" + query + "%";
            stmt.setString(1, parametroBusqueda);
            stmt.setString(2, parametroBusqueda);

            if (soloActivos != null) {
                stmt.setBoolean(3, soloActivos);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar clientes: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Obtener cliente por ID
     */
    public Cliente obtenerPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearCliente(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Crear nuevo cliente
     */
    public boolean crear(Cliente cliente) {
        String sql = """
            INSERT INTO cliente (tipo_cliente, nombre, cuit, condicion_iva, 
                               direccion, telefono, email, activo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getTipoCliente());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getCuit());
            stmt.setString(4, cliente.getCondicionIva());
            stmt.setString(5, cliente.getDireccion());
            stmt.setString(6, cliente.getTelefono());
            stmt.setString(7, cliente.getEmail());
            stmt.setBoolean(8, cliente.isActivo());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    cliente.setIdCliente(rs.getInt(1));
                }
                System.out.println("Cliente creado: " + cliente.getNombre());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al crear cliente: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Actualizar cliente existente
     */
    public boolean actualizar(Cliente cliente) {
        String sql = """
            UPDATE cliente 
            SET tipo_cliente = ?, nombre = ?, cuit = ?, condicion_iva = ?,
                direccion = ?, telefono = ?, email = ?, activo = ?
            WHERE id_cliente = ?
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getTipoCliente());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getCuit());
            stmt.setString(4, cliente.getCondicionIva());
            stmt.setString(5, cliente.getDireccion());
            stmt.setString(6, cliente.getTelefono());
            stmt.setString(7, cliente.getEmail());
            stmt.setBoolean(8, cliente.isActivo());
            stmt.setInt(9, cliente.getIdCliente());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Cliente actualizado: " + cliente.getNombre());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Cambiar estado del cliente (activar/desactivar)
     */
    public boolean cambiarEstado(int idCliente, boolean nuevoEstado) {
        String sql = "UPDATE cliente SET activo = ? WHERE id_cliente = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, nuevoEstado);
            stmt.setInt(2, idCliente);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Cliente " + (nuevoEstado ? "activado" : "desactivado"));
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Verificar si el cliente tiene ventas asociadas
     */
    public boolean tieneVentas(int idCliente) {
        String sql = "SELECT COUNT(*) as total FROM venta WHERE id_cliente = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar ventas: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Mapear ResultSet a objeto Cliente
     */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setTipoCliente(rs.getString("tipo_cliente"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setCuit(rs.getString("cuit"));
        cliente.setCondicionIva(rs.getString("condicion_iva"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setEmail(rs.getString("email"));
        cliente.setActivo(rs.getBoolean("activo"));

        Timestamp timestamp = rs.getTimestamp("fecha_creacion");
        if (timestamp != null) {
            cliente.setFechaCreacion(timestamp.toLocalDateTime());
        }

        return cliente;
    }


}