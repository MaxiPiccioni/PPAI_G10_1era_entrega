package infra.db;

import java.sql.*;

public class EstadoDao {

    private static final String SQL_FIND_ID =
            "SELECT idEstado FROM Estado WHERE ambito = ? AND nombreEstado = ?";

    private static final String SQL_INSERT =
            "INSERT INTO Estado(ambito, nombreEstado) VALUES(?, ?)";

    /** Devuelve id si existe, sino null */
    public Integer findId(String ambito, String nombreEstado) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ID)) {
            ps.setString(1, ambito);
            ps.setString(2, nombreEstado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("idEstado") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando Estado: " + ambito + " / " + nombreEstado, e);
        }
    }

    /** Inserta y devuelve el id generado */
    public int insert(String ambito, String nombreEstado) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ambito);
            ps.setString(2, nombreEstado);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No se obtuvo id generado para Estado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando Estado: " + ambito + " / " + nombreEstado, e);
        }
    }

    /** Crea si no existe (clave natural: ambito + nombreEstado). Devuelve siempre el id. */
    public int getOrCreate(String ambito, String nombreEstado) {
        Integer id = findId(ambito, nombreEstado);
        return (id != null) ? id : insert(ambito, nombreEstado);
    }

    /** Devuelve el idEstado dado el nombre del estado. */
    public int getIdByNombre(String nombre) {
        String sql = "SELECT idEstado FROM Estado WHERE nombreEstado = ?";
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("idEstado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando idEstado por nombre", e);
        }
        throw new RuntimeException("No existe Estado con nombre: " + nombre);
    }
}
