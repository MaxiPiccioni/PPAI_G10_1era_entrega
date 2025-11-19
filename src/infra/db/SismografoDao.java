package infra.db;

import java.sql.*;


public class SismografoDao {
    private static final String SQL_INSERT =
            "INSERT OR IGNORE INTO Sismografo (identificadorSismografo, fechaAdquisicion, nroSerie, codigoEstacion, idEstadoActual) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_ID =
            "SELECT identificadorSismografo FROM Sismografo WHERE identificadorSismografo = ?";

    public int getOrCreate(String identificadorSismografo, java.time.LocalDate fechaAdquisicion, String nroSerie, int codigoEstacion, int idEstadoActual) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT)) {
            ps.setString(1, identificadorSismografo);
            ps.setString(2, fechaAdquisicion != null ? fechaAdquisicion.toString() : null);
            ps.setString(3, nroSerie);
            ps.setInt(4, codigoEstacion);
            ps.setInt(5, idEstadoActual);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando Sismografo", e);
        }
        // Buscar el id recién insertado
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_SELECT_ID)) {
            ps.setString(1, identificadorSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando Sismografo", e);
        }
        throw new RuntimeException("No se pudo obtener el Sismografo");
    }
}