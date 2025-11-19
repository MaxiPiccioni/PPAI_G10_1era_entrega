package infra.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDao {

    private static final String SQL_FIND_BY_NOMBRE =
            "SELECT idRol, nombre, descripcionRol FROM Rol WHERE nombre = ?";

    private static final String SQL_INSERT =
            "INSERT INTO Rol(nombre, descripcionRol) VALUES(?, ?)";

    private static final String SQL_FIND_ALL =
            "SELECT idRol, nombre, descripcionRol FROM Rol ORDER BY idRol";

    /** Devuelve el id si existe, sino null */
    public Integer findIdByNombre(String nombre) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_NOMBRE)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("idRol");
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando Rol por nombre: " + nombre, e);
        }
    }

    /** Inserta y devuelve el id generado */
    public int insert(String nombre, String descripcionRol) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcionRol);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No se obtuvo id generado para Rol");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando Rol: " + nombre, e);
        }
    }

    /** Crea si no existe. Devuelve siempre el id. */
    public int getOrCreate(String nombre, String descripcionRol) {
        Integer id = findIdByNombre(nombre);
        return (id != null) ? id : insert(nombre, descripcionRol);
    }

    /** (Opcional) listar roles crudos (sin mapear a tu clase) */
    public List<RolRow> findAll() {
        List<RolRow> out = new ArrayList<>();
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new RolRow(
                        rs.getInt("idRol"),
                        rs.getString("nombre"),
                        rs.getString("descripcionRol")
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error listando roles", e);
        }
    }

    // DTO liviano para no depender de tu clase de dominio
    public record RolRow(int idRol, String nombre, String descripcionRol) {}
}
