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
}
