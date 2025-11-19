package com.libreria.dao;

import com.libreria.model.TipoUsuario;
import com.libreria.model.Usuario;
import com.libreria.util.ConexionBD;
import com.libreria.util.PasswordUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO: Data Access Object (Objeto de Acceso a Datos)
 * Esta clase maneja TODAS las operaciones de base de datos relacionadas con usuarios
 *
 * CONCEPTO DAO:
 * - Separa la lógica de negocio de la lógica de base de datos
 * - Cada tabla tiene su propio DAO
 * - El resto del código NO escribe SQL, solo llama métodos del DAO
 *
 * PATRÓN:
 * Controller → llama → DAO → ejecuta SQL → retorna objetos Java
 */
public class UsuarioDAO {

    // ===== MÉTODO 1: LOGIN =====

    /**
     * Verifica credenciales de login y retorna el usuario si son correctas
     *
     * PROCESO:
     * 1. Buscar usuario por nombre_usuario en la BD
     * 2. Si existe, verificar password con BCrypt
     * 3. Si coincide, actualizar ultimo_acceso y retornar usuario
     * 4. Si no coincide o no existe, retornar null
     *
     * @param nombreUsuario El username ingresado
     * @param passwordPlano La contraseña en texto plano ingresada
     * @return Usuario si login exitoso, null si falla
     */
    public Usuario login(String nombreUsuario, String passwordPlano) {
        System.out.println("🔐 Intentando login: " + nombreUsuario);

        // SQL: buscar usuario activo por nombre_usuario + traer el tipo_usuario con JOIN
        String sql = """
            SELECT 
                u.id_usuario,
                u.nombre_usuario,
                u.contraseña_hash,
                u.nombre,
                u.apellido,
                u.activo,
                u.fecha_creacion,
                u.ultimo_acceso,
                t.id_tipo_usuario,
                t.nombre as tipo_nombre,
                t.descripcion as tipo_descripcion,
                t.activo as tipo_activo
            FROM usuario u
            INNER JOIN tipo_usuario t ON u.id_tipo_usuario = t.id_tipo_usuario
            WHERE u.nombre_usuario = ? 
              AND u.activo = 1
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer el parámetro (reemplaza el ?)
            stmt.setString(1, nombreUsuario);

            // Ejecutar query
            ResultSet rs = stmt.executeQuery();

            // Si encuentra el usuario
            if (rs.next()) {
                String hashGuardado = rs.getString("contraseña_hash");

                // VERIFICAR PASSWORD con BCrypt
                if (PasswordUtil.verificar(passwordPlano, hashGuardado)) {
                    System.out.println("✅ Contraseña correcta");

                    // Crear objeto TipoUsuario desde el ResultSet
                    TipoUsuario tipo = new TipoUsuario(
                            rs.getInt("id_tipo_usuario"),
                            rs.getString("tipo_nombre"),
                            rs.getString("tipo_descripcion"),
                            rs.getBoolean("tipo_activo")
                    );

                    // Crear objeto Usuario desde el ResultSet
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setContrasenaHash(hashGuardado);
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setTipoUsuario(tipo);
                    usuario.setActivo(rs.getBoolean("activo"));

                    // Manejar fechas (pueden ser null)
                    Timestamp fechaCreacionTs = rs.getTimestamp("fecha_creacion");
                    if (fechaCreacionTs != null) {
                        usuario.setFechaCreacion(fechaCreacionTs.toLocalDateTime());
                    }

                    Timestamp ultimoAccesoTs = rs.getTimestamp("ultimo_acceso");
                    if (ultimoAccesoTs != null) {
                        usuario.setUltimoAcceso(ultimoAccesoTs.toLocalDateTime());
                    }

                    // ACTUALIZAR ultimo_acceso en la BD
                    actualizarUltimoAcceso(usuario.getIdUsuario());

                    System.out.println("✅ Login exitoso: " + usuario.getNombreCompleto());
                    return usuario;

                } else {
                    System.out.println("❌ Contraseña incorrecta");
                    return null;
                }
            } else {
                System.out.println("❌ Usuario no encontrado");
                return null;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error en login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ===== MÉTODO 2: OBTENER TODOS LOS USUARIOS =====

    /**
     * Obtiene lista completa de usuarios (para tabla en UsuariosView)
     *
     * @return Lista de todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        System.out.println("📋 Obteniendo todos los usuarios...");

        List<Usuario> usuarios = new ArrayList<>();

        String sql = """
            SELECT 
                u.id_usuario,
                u.nombre_usuario,
                u.contraseña_hash,
                u.nombre,
                u.apellido,
                u.activo,
                u.fecha_creacion,
                u.ultimo_acceso,
                t.id_tipo_usuario,
                t.nombre as tipo_nombre,
                t.descripcion as tipo_descripcion,
                t.activo as tipo_activo
            FROM usuario u
            INNER JOIN tipo_usuario t ON u.id_tipo_usuario = t.id_tipo_usuario
            ORDER BY u.id_usuario
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Crear TipoUsuario
                TipoUsuario tipo = new TipoUsuario(
                        rs.getInt("id_tipo_usuario"),
                        rs.getString("tipo_nombre"),
                        rs.getString("tipo_descripcion"),
                        rs.getBoolean("tipo_activo")
                );

                // Crear Usuario
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setContrasenaHash(rs.getString("contraseña_hash"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setTipoUsuario(tipo);
                usuario.setActivo(rs.getBoolean("activo"));

                // Fechas
                Timestamp fechaCreacionTs = rs.getTimestamp("fecha_creacion");
                if (fechaCreacionTs != null) {
                    usuario.setFechaCreacion(fechaCreacionTs.toLocalDateTime());
                }

                Timestamp ultimoAccesoTs = rs.getTimestamp("ultimo_acceso");
                if (ultimoAccesoTs != null) {
                    usuario.setUltimoAcceso(ultimoAccesoTs.toLocalDateTime());
                }

                usuarios.add(usuario);
            }

            System.out.println("✅ Obtenidos " + usuarios.size() + " usuarios");

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener usuarios: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    // ===== MÉTODO 3: CREAR NUEVO USUARIO =====

    /**
     * Crea un nuevo usuario en la base de datos
     * La contraseña se hashea automáticamente
     *
     * @param usuario El usuario a crear (sin ID)
     * @return true si se creó exitosamente, false si hubo error
     */
    public boolean crear(Usuario usuario) {
        System.out.println("➕ Creando usuario: " + usuario.getNombreUsuario());

        String sql = """
            INSERT INTO usuario (
                nombre_usuario,
                contraseña_hash,
                salt,
                nombre,
                apellido,
                id_tipo_usuario,
                activo
            ) VALUES (?, ?, '', ?, ?, ?, ?)
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Establecer parámetros
            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getContrasenaHash());  // Ya debe venir hasheado
            stmt.setString(3, usuario.getNombre());
            stmt.setString(4, usuario.getApellido());
            stmt.setInt(5, usuario.getTipoUsuario().getIdTipoUsuario());
            stmt.setBoolean(6, usuario.isActivo());

            // Ejecutar INSERT
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                    System.out.println("✅ Usuario creado con ID: " + usuario.getIdUsuario());
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al crear usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // ===== MÉTODO 4: ACTUALIZAR USUARIO =====

    /**
     * Actualiza los datos de un usuario existente
     * NO actualiza la contraseña (usar resetearPassword para eso)
     *
     * @param usuario El usuario con datos actualizados
     * @return true si se actualizó, false si hubo error
     */
    public boolean actualizar(Usuario usuario) {
        System.out.println("✏️ Actualizando usuario ID: " + usuario.getIdUsuario());

        String sql = """
            UPDATE usuario 
            SET nombre_usuario = ?,
                nombre = ?,
                apellido = ?,
                id_tipo_usuario = ?,
                activo = ?
            WHERE id_usuario = ?
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getApellido());
            stmt.setInt(4, usuario.getTipoUsuario().getIdTipoUsuario());
            stmt.setBoolean(5, usuario.isActivo());
            stmt.setInt(6, usuario.getIdUsuario());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Usuario actualizado");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // ===== MÉTODO 5: RESETEAR CONTRASEÑA =====

    /**
     * Cambia la contraseña de un usuario
     *
     * @param idUsuario ID del usuario
     * @param nuevaPasswordPlana Nueva contraseña en texto plano (se hasheará automáticamente)
     * @return true si se cambió, false si hubo error
     */
    public boolean resetearPassword(int idUsuario, String nuevaPasswordPlana) {
        System.out.println("🔑 Reseteando contraseña del usuario ID: " + idUsuario);

        // Hashear la nueva contraseña
        String nuevoHash = PasswordUtil.hashear(nuevaPasswordPlana);

        String sql = """
            UPDATE usuario 
            SET contraseña_hash = ?,
                salt = ''
            WHERE id_usuario = ?
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoHash);
            stmt.setInt(2, idUsuario);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Contraseña reseteada");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al resetear contraseña: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // ===== MÉTODO 6: ACTIVAR/DESACTIVAR USUARIO =====

    /**
     * Activa o desactiva un usuario (no lo elimina, solo cambia el flag)
     *
     * @param idUsuario ID del usuario
     * @param activo true para activar, false para desactivar
     * @return true si se cambió, false si hubo error
     */
    public boolean cambiarEstado(int idUsuario, boolean activo) {
        System.out.println((activo ? "✅ Activando" : "🔴 Desactivando") + " usuario ID: " + idUsuario);

        String sql = "UPDATE usuario SET activo = ? WHERE id_usuario = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, activo);
            stmt.setInt(2, idUsuario);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Estado cambiado");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al cambiar estado: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // ===== MÉTODO 7: VERIFICAR SI USERNAME YA EXISTE =====

    /**
     * Verifica si un nombre de usuario ya está en uso
     * Útil para validar antes de crear o editar
     *
     * @param nombreUsuario Username a verificar
     * @param idUsuarioExcluir ID del usuario actual (para ignorarlo al editar), o -1
     * @return true si ya existe, false si está disponible
     */
    public boolean existeUsername(String nombreUsuario, int idUsuarioExcluir) {
        String sql = """
            SELECT COUNT(*) as total 
            FROM usuario 
            WHERE nombre_usuario = ? 
              AND id_usuario != ?
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            stmt.setInt(2, idUsuarioExcluir);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar username: " + e.getMessage());
        }

        return false;
    }

    // ===== MÉTODO 8: OBTENER TIPOS DE USUARIO =====

    /**
     * Obtiene todos los tipos de usuario (para ComboBox)
     *
     * @return Lista de TipoUsuario
     */
    public List<TipoUsuario> obtenerTiposUsuario() {
        List<TipoUsuario> tipos = new ArrayList<>();

        String sql = """
            SELECT id_tipo_usuario, nombre, descripcion, activo
            FROM tipo_usuario
            WHERE activo = 1
            ORDER BY nombre
            """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TipoUsuario tipo = new TipoUsuario(
                        rs.getInt("id_tipo_usuario"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getBoolean("activo")
                );
                tipos.add(tipo);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener tipos de usuario: " + e.getMessage());
        }

        return tipos;
    }

    // ===== MÉTODO AUXILIAR: ACTUALIZAR ÚLTIMO ACCESO =====

    /**
     * Actualiza la fecha/hora del último acceso de un usuario
     * Se llama automáticamente después de un login exitoso
     *
     * @param idUsuario ID del usuario
     */
    private void actualizarUltimoAcceso(int idUsuario) {
        String sql = "UPDATE usuario SET ultimo_acceso = NOW() WHERE id_usuario = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();

        } catch (SQLException e) {
            // No es crítico si falla, solo log
            System.err.println("⚠️ No se pudo actualizar ultimo_acceso: " + e.getMessage());
        }
    }

    public long contarAdminsActivos() {
        String sql = """
        SELECT COUNT(*) as total
        FROM usuario u
        INNER JOIN tipo_usuario t ON u.id_tipo_usuario = t.id_tipo_usuario
        WHERE t.nombre = 'ADMIN'
          AND u.activo = 1
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long total = rs.getLong("total");
                System.out.println("📊 Admins activos: " + total);
                return total;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar admins activos: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;  // En caso de error, retornar 0 (seguro)
    }

    /**
     * Verifica si un usuario específico es el único admin activo
     *
     * @param idUsuario ID del usuario a verificar
     * @return true si es el único admin activo, false si no
     */
    public boolean esUnicoAdminActivo(int idUsuario) {
        // Primero verificar que el usuario sea admin
        String sqlVerificar = """
        SELECT t.nombre
        FROM usuario u
        INNER JOIN tipo_usuario t ON u.id_tipo_usuario = t.id_tipo_usuario
        WHERE u.id_usuario = ?
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlVerificar)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String rol = rs.getString("nombre");

                // Si NO es admin, no es el único admin (retorna false)
                if (!"ADMIN".equals(rol)) {
                    return false;
                }

                // Es admin, ahora contar cuántos admins activos hay
                long totalAdmins = contarAdminsActivos();
                return totalAdmins <= 1;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar único admin: " + e.getMessage());
            e.printStackTrace();
        }

        return false;  // En caso de error, asumir que NO es único (más seguro)
    }
}