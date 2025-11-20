package infra.db;

import Clases.Usuario;
import Clases.Empleado;
import java.sql.*;
import java.util.Objects;

public class UsuarioDao {

    private static final String SQL_FIND_ID_BY_NOMBRE =
            "SELECT idUsuario FROM Usuario WHERE nombreUsuario = ?";

    private static final String SQL_INSERT =
            "INSERT INTO Usuario(nombreUsuario, contrasena, idEmpleado, idPerfil) VALUES(?,?,?,?)";

    /** Devuelve id si existe, sino null */
    public Integer findIdByNombreUsuario(String nombreUsuario) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ID_BY_NOMBRE)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("idUsuario") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando Usuario: " + nombreUsuario, e);
        }
    }

    /** Inserta y devuelve id generado */
    public int insert(String nombreUsuario, String contrasena, int idEmpleado, Integer idPerfil) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            ps.setInt(3, idEmpleado);
            if (idPerfil != null) ps.setInt(4, idPerfil); else ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No se obtuvo id generado para Usuario");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando Usuario: " + nombreUsuario, e);
        }
    }

    /** Crea si no existe (clave natural: nombreUsuario). Devuelve siempre el id. */
    public int getOrCreate(String nombreUsuario, String contraseña, int idEmpleado, Integer idPerfil) {
        Integer id = findIdByNombreUsuario(nombreUsuario);
        return (id != null) ? id : insert(nombreUsuario, contraseña, idEmpleado, idPerfil);
    }

    // Nuevo: busca y devuelve la entidad dominio Usuario por idUsuario (o null si no existe)
    public Usuario findById(int idUsuario) {
        String sql = "SELECT * FROM Usuario WHERE idUsuario = ?";
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                // Intentar obtener campos con nombres posibles según distintos esquemas
                String nombreUsuario = getStringSafe(rs, "nombreUsuario", "usuario", "username", "nombre_usuario");
                String contrasena = getStringSafe(rs, "contrasena", "contrasenia", "password", "contraseña", "clave");
                Integer idEmpleado = getIntSafe(rs, "idEmpleado", "id_empleado", "id_emp", "idEmpleadoFk", "idEmpleado");

                Empleado empleado = null;
                if (idEmpleado != null) {
                    EmpleadoDao empDao = new EmpleadoDao();
                    // Intentar métodos comunes del DAO
                    try {
                        empleado = empDao.findById(idEmpleado);
                    } catch (NoSuchMethodError | RuntimeException e1) {
                        try {
                            empleado = empDao.getById(idEmpleado);
                        } catch (Exception ignore) {
                            empleado = null;
                        }
                    }
                }

                Usuario usuario = new Usuario(nombreUsuario, contrasena, empleado);

                // Si la clase Usuario tiene setIdUsuario(int) lo llamamos por reflexión (opcional).
                try {
                    java.lang.reflect.Method m = Usuario.class.getMethod("setIdUsuario", int.class);
                    if (m != null) m.invoke(usuario, idUsuario);
                } catch (NoSuchMethodException ignored) {
                    // no existe setter, está bien
                } catch (Exception ignored) {
                    // si falla la reflexión no detenemos la ejecución
                }

                return usuario;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo Usuario id=" + idUsuario, e);
        }
    }

    // Alias simple
    public Usuario getById(int idUsuario) {
        return findById(idUsuario);
    }

    // Util: intenta leer la primera columna de texto presente entre los nombres proporcionados
    private static String getStringSafe(ResultSet rs, String... names) throws SQLException {
        for (String n : names) {
            try {
                String v = rs.getString(n);
                if (v != null) return v;
            } catch (SQLException ignored) {
                // columna no existe -> siguiente
            }
        }
        return null;
    }

    // Util: intenta leer la primera columna entera presente entre los nombres proporcionados
    private static Integer getIntSafe(ResultSet rs, String... names) throws SQLException {
        for (String n : names) {
            try {
                int v = rs.getInt(n);
                if (!rs.wasNull()) return v;
            } catch (SQLException ignored) {
                // columna no existe -> siguiente
            }
        }
        return null;
    }
}
