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

    // nuevo: inserción cuando ya tenemos fechaHoraFin
    private static final String SQL_INSERT_WITH_FIN =
            "INSERT INTO CambioEstado(fechaHoraInicio, fechaHoraFin, idEstado, idMotivo, idEmpleado, identificadorSismografo) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";

    private static final String SQL_CERRAR_POR_ID =
            "UPDATE CambioEstado SET fechaHoraFin = ? WHERE idCambio = ? AND fechaHoraFin IS NULL";

    // nuevo: cerrar vigente por identificador textual
    private static final String SQL_CERRAR_VIGENTE_POR_IDENT =
            "UPDATE CambioEstado SET fechaHoraFin = ? WHERE identificadorSismografo = ? AND fechaHoraFin IS NULL";

    private static final String SQL_SELECT_ALL =
            "SELECT idCambio, fechaHoraInicio, fechaHoraFin, idEstado, idMotivo, idEmpleado, identificadorSismografo " +
                    "FROM CambioEstado ORDER BY idCambio";

    // helper: busca idCambio existente para la pareja (idEstado, identificadorSismografo)
    private Integer findCambioPorEstadoEIdentificador(Connection c, int idEstado, String identificadorSismografo) throws SQLException {
        String sql = "SELECT idCambio FROM CambioEstado WHERE idEstado = ? AND identificadorSismografo = ? LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEstado);
            ps.setString(2, identificadorSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("idCambio") : null;
            }
        }
    }

    /** Abre un cambio (vigente). Si ya existe una fila con mismo idEstado+identificador, la actualiza en lugar de insertar. Devuelve idCambio. */
    public int abrir(LocalDateTime inicio, int idEstado, Integer idMotivo, Integer idEmpleado, String identificadorSismografo) {
        try (Connection c = SQLite.get()) {

            // 1) intentar encontrar duplicado
            Integer existingId = null;
            try {
                existingId = findCambioPorEstadoEIdentificador(c, idEstado, identificadorSismografo);
            } catch (SQLException ignore) { /* seguir a inserción si falla la búsqueda */ }

            if (existingId != null) {
                // actualizar la fila existente
                String sqlUpd = "UPDATE CambioEstado SET fechaHoraInicio = ?, fechaHoraFin = NULL, idMotivo = ?, idEmpleado = ? WHERE idCambio = ?";
                try (PreparedStatement psUpd = c.prepareStatement(sqlUpd)) {
                    psUpd.setString(1, (inicio != null ? inicio : LocalDateTime.now()).toString());
                    if (idMotivo != null) psUpd.setInt(2, idMotivo); else psUpd.setNull(2, Types.INTEGER);
                    if (idEmpleado != null) psUpd.setObject(3, idEmpleado, Types.INTEGER); else psUpd.setNull(3, Types.INTEGER);
                    psUpd.setInt(4, existingId);
                    psUpd.executeUpdate();
                    return existingId;
                } catch (SQLException e) {
                    throw new RuntimeException("Error actualizando CambioEstado existente id=" + existingId, e);
                }
            }

            // 2) si no existe, insertar como antes
            try (PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, (inicio != null ? inicio : LocalDateTime.now()).toString()); // ISO-8601
                ps.setInt(2, idEstado);
                if (idMotivo != null) ps.setInt(3, idMotivo); else ps.setNull(3, Types.INTEGER);
                // idEmpleado puede ser null -> setObject con Types.INTEGER
                if (idEmpleado != null) ps.setObject(4, idEmpleado, Types.INTEGER); else ps.setNull(4, Types.INTEGER);
                ps.setString(5, identificadorSismografo);

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    throw new SQLException("No se obtuvo id generado para CambioEstado");
                }
            } catch (SQLException e) {
                System.err.println("[CambioEstadoDao] Error abriendo CambioEstado: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error abriendo CambioEstado", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en conexión al abrir CambioEstado", e);
        }
    }

    /** Abre un cambio con fechaHoraFin (no vigente). Evita duplicados por idEstado+identificador (actualiza si existe). */
    public int abrirConFin(LocalDateTime inicio, LocalDateTime fin, int idEstado, Integer idMotivo, Integer idEmpleado, String identificadorSismografo) {
        try (Connection c = SQLite.get()) {

            // 1) intentar encontrar duplicado
            Integer existingId = null;
            try {
                existingId = findCambioPorEstadoEIdentificador(c, idEstado, identificadorSismografo);
            } catch (SQLException ignore) { /* seguir a inserción si falla la búsqueda */ }

            if (existingId != null) {
                // actualizar la fila existente con fecha fin
                String sqlUpd = "UPDATE CambioEstado SET fechaHoraInicio = ?, fechaHoraFin = ?, idMotivo = ?, idEmpleado = ? WHERE idCambio = ?";
                try (PreparedStatement psUpd = c.prepareStatement(sqlUpd)) {
                    psUpd.setString(1, (inicio != null ? inicio : LocalDateTime.now()).toString());
                    psUpd.setString(2, (fin != null ? fin : LocalDateTime.now()).toString());
                    if (idMotivo != null) psUpd.setInt(3, idMotivo); else psUpd.setNull(3, Types.INTEGER);
                    if (idEmpleado != null) psUpd.setObject(4, idEmpleado, Types.INTEGER); else psUpd.setNull(4, Types.INTEGER);
                    psUpd.setInt(5, existingId);
                    psUpd.executeUpdate();
                    return existingId;
                } catch (SQLException e) {
                    throw new RuntimeException("Error actualizando (con fin) CambioEstado existente id=" + existingId, e);
                }
            }

            // 2) si no existe, insertar con fin
            try (PreparedStatement ps = c.prepareStatement(SQL_INSERT_WITH_FIN, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, (inicio != null ? inicio : LocalDateTime.now()).toString()); // ISO-8601
                ps.setString(2, (fin != null ? fin : LocalDateTime.now()).toString());
                ps.setInt(3, idEstado);
                if (idMotivo != null) ps.setInt(4, idMotivo); else ps.setNull(4, Types.INTEGER);
                if (idEmpleado != null) ps.setObject(5, idEmpleado, Types.INTEGER); else ps.setNull(5, Types.INTEGER);
                ps.setString(6, identificadorSismografo);

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                    throw new SQLException("No se obtuvo id generado para CambioEstado");
                }
            } catch (SQLException e) {
                System.err.println("[CambioEstadoDao] Error abriendo (con fin) CambioEstado: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error abriendo CambioEstado con fin", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en conexión al abrir (con fin) CambioEstado", e);
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
