package infra.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CambioEstadoDao {

    // usar la columna textual 'identificadorSismografo'
    private static final String SQL_INSERT =
            "INSERT INTO CambioEstado(fechaHoraInicio, fechaHoraFin, idEstado, idMotivo, idEmpleado, identificadorSismografo) " +
                    "VALUES(?, NULL, ?, ?, ?, ?)";

    private static final String SQL_CERRAR_POR_ID =
            "UPDATE CambioEstado SET fechaHoraFin = ? WHERE idCambio = ? AND fechaHoraFin IS NULL";

    // nuevo: cerrar vigente por identificador textual
    private static final String SQL_CERRAR_VIGENTE_POR_IDENT =
            "UPDATE CambioEstado SET fechaHoraFin = ? WHERE identificadorSismografo = ? AND fechaHoraFin IS NULL";

    private static final String SQL_SELECT_ALL =
            "SELECT idCambio, fechaHoraInicio, fechaHoraFin, idEstado, idMotivo, idEmpleado, identificadorSismografo " +
                    "FROM CambioEstado ORDER BY idCambio";

    /** Abre un cambio (vigente). Devuelve idCambio generado. */
    public int abrir(LocalDateTime inicio, int idEstado, Integer idMotivo, Integer idEmpleado, String identificadorSismografo) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, (inicio != null ? inicio : LocalDateTime.now()).toString()); // ISO-8601
            ps.setInt(2, idEstado);
            if (idMotivo != null) ps.setInt(3, idMotivo); else ps.setNull(3, Types.INTEGER);
            // idEmpleado puede ser null -> setObject con Types.INTEGER
            if (idEmpleado != null) ps.setObject(4, idEmpleado, Types.INTEGER); else ps.setNull(4, Types.INTEGER);
            ps.setString(5, identificadorSismografo);

            // debug

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return id;
                }
                throw new SQLException("No se obtuvo id generado para CambioEstado");
            }
        } catch (SQLException e) {
            // loguear stacktrace para depuración antes de re-lanzar
            System.err.println("[CambioEstadoDao] Error abriendo CambioEstado: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error abriendo CambioEstado", e);
        }
    }

    /** Cierra el cambio vigente (fechaHoraFin NULL) para el sismógrafo identificado por identificadorSismografo. */
    public void cerrarVigentePorIdentificador(String identificadorSismografo, LocalDateTime fin) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_CERRAR_VIGENTE_POR_IDENT)) {
            ps.setString(1, (fin != null ? fin : LocalDateTime.now()).toString());
            ps.setString(2, identificadorSismografo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error cerrando CambioEstado vigente para identificador=" + identificadorSismografo, e);
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
                        rs.getInt("idEmpleado"),
                        // leer el identificador textual al sismógrafo
                        rs.getString("identificadorSismografo")
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
            int idEmpleado,
            String identificadorSismografo
    ) {}
}
