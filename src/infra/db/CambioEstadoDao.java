package infra.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CambioEstadoDao {

    private static final String SQL_INSERT =
            "INSERT INTO CambioEstado(fechaHoraInicio, fechaHoraFin, idEstado, idMotivo, idEmpleado, identificadorSismografo) " +
                    "VALUES(?, NULL, ?, ?, ?, ?)";

    private static final String SQL_CERRAR_POR_ID =
            "UPDATE CambioEstado SET fechaHoraFin = ? WHERE idCambio = ? AND fechaHoraFin IS NULL";

    private static final String SQL_SELECT_ALL =
            "SELECT idCambio, fechaHoraInicio, fechaHoraFin, idEstado, idMotivo, idEmpleado " +
                    "FROM CambioEstado ORDER BY idCambio";

    /** Abre un cambio (vigente). Devuelve idCambio generado. */
    public int abrir(LocalDateTime inicio, int idEstado, Integer idMotivo, int idEmpleado, int identificadorSismografo) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, (inicio != null ? inicio : LocalDateTime.now()).toString()); // ISO-8601
            ps.setInt(2, idEstado);
            if (idMotivo != null) ps.setInt(3, idMotivo); else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, idEmpleado);
            ps.setInt(5, identificadorSismografo);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No se obtuvo id generado para CambioEstado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error abriendo CambioEstado", e);
        }
    }

    /** Cierra un cambio vigente por idCambio. */
    public void cerrar(int idCambio, LocalDateTime fin) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_CERRAR_POR_ID)) {
            ps.setString(1, (fin != null ? fin : LocalDateTime.now()).toString());
            ps.setInt(2, idCambio);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error cerrando CambioEstado id=" + idCambio, e);
        }
    }

    /** Trae TODOS los cambios de la base. */
    public List<CambioEstadoRow> findAll() {
        List<CambioEstadoRow> out = new ArrayList<>();
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new CambioEstadoRow(
                        rs.getInt("idCambio"),
                        parseDT(rs.getString("fechaHoraInicio")),
                        parseDT(rs.getString("fechaHoraFin")),
                        rs.getInt("idEstado"),
                        (Integer) rs.getObject("idMotivo"), // puede ser null
                        rs.getInt("idEmpleado")
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error listando CambioEstado", e);
        }
    }

    private static LocalDateTime parseDT(String s) {
        return (s != null) ? LocalDateTime.parse(s) : null;
        // guardamos/recuperamos en ISO-8601
    }

    /** DTO liviano para no acoplar al dominio */
    public record CambioEstadoRow(
            int idCambio,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            int idEstado,
            Integer idMotivo,
            int idEmpleado
    ) {}
}
