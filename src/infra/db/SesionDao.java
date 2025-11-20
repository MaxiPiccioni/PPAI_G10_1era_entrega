package infra.db;

import java.sql.*;
import java.time.LocalDateTime;

public class SesionDao {

    private static final String SQL_ABRIR =
            "INSERT INTO Sesion(fechaYHoraInicio, fechaYHoraFin, idUsuario) VALUES(?,?,?)";

    private static final String SQL_CERRAR_ACTIVA =
            "UPDATE Sesion SET fechaYHoraFin = ? WHERE idUsuario = ? AND fechaYHoraFin IS NULL";

    private static final String SQL_FIND_ACTIVA =
            "SELECT idSesion, fechaYHoraInicio FROM Sesion WHERE idUsuario = ? AND fechaYHoraFin IS NULL ORDER BY idSesion DESC LIMIT 1";

    /** Abre sesión (fin = NULL). Devuelve idSesion generado. */
    public int abrirSesion(int idUsuario, LocalDateTime inicio) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_ABRIR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, (inicio != null ? inicio : LocalDateTime.now()).toString());
            ps.setNull(2, Types.VARCHAR); // fin NULL
            ps.setInt(3, idUsuario);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No se obtuvo id generado para Sesion");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error abriendo sesión para usuario " + idUsuario, e);
        }
    }

    /** Cierra la sesión activa del usuario (si existe). */
    public void cerrarSesionActiva(int idUsuario, LocalDateTime fin) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_CERRAR_ACTIVA)) {
            ps.setString(1, (fin != null ? fin : LocalDateTime.now()).toString());
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error cerrando sesión de usuario " + idUsuario, e);
        }
    }

    /** Devuelve idSesion activa si existe, o null. */
    public Integer findSesionActivaId(int idUsuario) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ACTIVA)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("idSesion") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando sesión activa de usuario " + idUsuario, e);
        }
    }

    // DTO simple para representar una fila de la tabla Sesion.
    public static class SesionDTO {
        private final int idSesion;
        private final LocalDateTime fechaYHoraInicio;
        private final LocalDateTime fechaYHoraFin;
        private final int idUsuario;

        public SesionDTO(int idSesion, LocalDateTime fechaYHoraInicio, LocalDateTime fechaYHoraFin, int idUsuario) {
            this.idSesion = idSesion;
            this.fechaYHoraInicio = fechaYHoraInicio;
            this.fechaYHoraFin = fechaYHoraFin;
            this.idUsuario = idUsuario;
        }

        public int getIdSesion() { return idSesion; }
        public LocalDateTime getFechaYHoraInicio() { return fechaYHoraInicio; }
        public LocalDateTime getFechaYHoraFin() { return fechaYHoraFin; }
        public int getIdUsuario() { return idUsuario; }
    }

    /**
     * Devuelve un DTO con los valores de la sesión identificada por idSesion,
     * o null si no existe.
     */
    public SesionDTO findById(int idSesion) {
        String sql = "SELECT idSesion, fechaYHoraInicio, fechaYHoraFin, idUsuario FROM Sesion WHERE idSesion = ?";
        try (Connection conn = SQLite.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSesion);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                // fechaYHoraInicio/fin se guardaron como texto con LocalDateTime.toString()
                String inicioStr = rs.getString("fechaYHoraInicio");
                String finStr = rs.getString("fechaYHoraFin");
                LocalDateTime inicio = (inicioStr != null ? LocalDateTime.parse(inicioStr) : null);
                LocalDateTime fin = (finStr != null ? LocalDateTime.parse(finStr) : null);
                int idUsuario = rs.getInt("idUsuario");

                return new SesionDTO(rs.getInt("idSesion"), inicio, fin, idUsuario);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo sesión id=" + idSesion, e);
        }
    }

    /** Devuelve la última sesión activa (fechaYHoraFin IS NULL), o null si no existe. */
    public SesionDTO findUltimaSesionActiva() {
        String sql = "SELECT idSesion, fechaYHoraInicio, fechaYHoraFin, idUsuario FROM Sesion WHERE fechaYHoraFin IS NULL ORDER BY idSesion DESC LIMIT 1";
        try (Connection conn = SQLite.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) return null;

            String inicioStr = rs.getString("fechaYHoraInicio");
            String finStr = rs.getString("fechaYHoraFin");
            LocalDateTime inicio = (inicioStr != null ? LocalDateTime.parse(inicioStr) : null);
            LocalDateTime fin = (finStr != null ? LocalDateTime.parse(finStr) : null);
            int idUsuario = rs.getInt("idUsuario");
            int idSesion = rs.getInt("idSesion");
            return new SesionDTO(idSesion, inicio, fin, idUsuario);

        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo última sesión activa", e);
        }
    }
}
