package infra.db;

import Clases.Empleado;
import Clases.Rol;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class EmpleadoDao {

    private static final String SQL_FIND_ID_BY_MAIL =
            "SELECT idEmpleado FROM Empleado WHERE mail = ?";

    private static final String SQL_INSERT =
            "INSERT INTO Empleado(nombre, apellido, telefono, mail, idRol) VALUES(?,?,?,?,?)";

    /** Devuelve el id si existe, sino null */
    public Integer findIdByMail(String mail) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ID_BY_MAIL)) {
            ps.setString(1, mail);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("idEmpleado") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando Empleado por mail: " + mail, e);
        }
    }

    /** Inserta y devuelve el id generado */
    public int insert(String nombre, String apellido, String telefono, String mail, int idRol) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, telefono);
            ps.setString(4, mail);
            ps.setInt(5, idRol);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No se obtuvo id generado para Empleado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando Empleado: " + nombre + " " + apellido, e);
        }
    }

    /** Crea si no existe (usa mail como clave natural). Devuelve siempre el id. */
    public int getOrCreateByMail(String nombre, String apellido, String telefono, String mail, int idRol) {
        Integer id = findIdByMail(mail);
        return (id != null) ? id : insert(nombre, apellido, telefono, mail, idRol);
    }

    public List<EmpleadoRow> findAll() {
        List<EmpleadoRow> out = new ArrayList<>();
        String sql = "SELECT idEmpleado, nombre, apellido, telefono, mail, idRol FROM Empleado ORDER BY idEmpleado";
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new EmpleadoRow(
                    rs.getInt("idEmpleado"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("telefono"),
                    rs.getString("mail"),
                    rs.getInt("idRol")
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error listando empleados", e);
        }
    }

    // Nuevo: busca y devuelve la entidad dominio Empleado por idEmpleado (o null si no existe)
    public Empleado findById(int idEmpleado) {
        String sql = "SELECT * FROM Empleado WHERE idEmpleado = ?";
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String nombre = getStringSafe(rs, "nombre", "nombreEmpleado", "nombre_empleado");
                String apellido = getStringSafe(rs, "apellido", "apellidoEmpleado", "apellido_empleado");
                String telefono = getStringSafe(rs, "telefono", "telefonoEmpleado", "telefono_empleado", "tel");
                String email = getStringSafe(rs, "email", "mail", "correo", "correo_electronico");
                Integer idRol = getIntSafe(rs, "idRol", "id_rol", "idRolFk", "idRolEmpleado", "rol_id");

                Rol rol = null;
                if (idRol != null) {
                    RolDao rolDao = new RolDao();
                    try {
                        rol = rolDao.findById(idRol);
                    } catch (NoSuchMethodError | RuntimeException ex) {
                        try { rol = rolDao.getById(idRol); } catch (Exception ignore) { rol = null; }
                    }
                }

                Empleado empleado = new Empleado(nombre, apellido, telefono, email, rol);

                // Si la clase Empleado tiene setIdEmpleado(int) lo asignamos por reflexión (opcional).
                try {
                    java.lang.reflect.Method m = Empleado.class.getMethod("setIdEmpleado", int.class);
                    if (m != null) m.invoke(empleado, idEmpleado);
                } catch (NoSuchMethodException ignored) {
                    // no existe setter, no pasa nada
                } catch (Exception ignored) {
                    // ignorar errores de reflexión
                }

                return empleado;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo Empleado id=" + idEmpleado, e);
        }
    }

    // Alias simple
    public Empleado getById(int idEmpleado) {
        return findById(idEmpleado);
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

    public record EmpleadoRow(
        int idEmpleado,
        String nombre,
        String apellido,
        String telefono,
        String mail,
        int idRol
    ) {}
}
