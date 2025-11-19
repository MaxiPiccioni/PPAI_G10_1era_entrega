package infra.db;

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

    public record EmpleadoRow(
        int idEmpleado,
        String nombre,
        String apellido,
        String telefono,
        String mail,
        int idRol
    ) {}
}
