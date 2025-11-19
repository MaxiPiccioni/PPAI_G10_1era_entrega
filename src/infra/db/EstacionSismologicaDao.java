package infra.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EstacionSismologicaDao {
    private static final String SQL_FIND_BY_CODIGO =
            "SELECT codigoEstacion FROM EstacionSismologica WHERE codigoEstacion = ?";

    private static final String SQL_INSERT =
            "INSERT INTO EstacionSismologica(codigoEstacion, documentoCertificacionAdq, fechaSolicitudCertificacion, latitud, longitud, nombre, nroCertificacionAdquisicion, identificadorSismografo) VALUES(?,?,?,?,?,?,?,?)";

    private static final String SQL_FIND_ALL =
            "SELECT codigoEstacion, documentoCertificacionAdq, fechaSolicitudCertificacion, latitud, longitud, nombre, nroCertificacionAdquisicion, identificadorSismografo FROM EstacionSismologica ORDER BY codigoEstacion";

    /** Devuelve el id si existe, sino null */
    public Integer findIdByCodigo(int codigoEstacion) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_CODIGO)) {
            ps.setInt(1, codigoEstacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("codigoEstacion");
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando EstacionSismologica por codigo: " + codigoEstacion, e);
        }
    }

    /** Inserta y devuelve el id generado */
    public int insert(int codigoEstacion, String documentoCertificacionAdq, LocalDateTime fechaSolicitudCertificacion, Integer latitud, Integer longitud, String nombre, Integer nroCertificacionAdquisicion, String identificadorSismografo) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, codigoEstacion);
            ps.setString(2, documentoCertificacionAdq);
            ps.setString(3, fechaSolicitudCertificacion != null ? fechaSolicitudCertificacion.toString() : null);
            if (latitud != null) ps.setInt(4, latitud); else ps.setNull(4, Types.INTEGER);
            if (longitud != null) ps.setInt(5, longitud); else ps.setNull(5, Types.INTEGER);
            ps.setString(6, nombre);
            if (nroCertificacionAdquisicion != null) ps.setInt(7, nroCertificacionAdquisicion); else ps.setNull(7, Types.INTEGER);
            ps.setString(8, identificadorSismografo);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No se obtuvo id generado para EstacionSismologica");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando EstacionSismologica: " + nombre, e);
        }
    }

    /** Crea si no existe. Devuelve siempre el id. */
    public int getOrCreate(int codigoEstacion, String documentoCertificacionAdq, LocalDateTime fechaSolicitudCertificacion, Integer latitud, Integer longitud, String nombre, Integer nroCertificacionAdquisicion, String identificadorSismografo) {
        Integer id = findIdByCodigo(codigoEstacion);
        return (id != null) ? id : insert(codigoEstacion, documentoCertificacionAdq, fechaSolicitudCertificacion, latitud, longitud, nombre, nroCertificacionAdquisicion, identificadorSismografo);
    }

    /** (Opcional) listar estaciones crudas (sin mapear a tu clase) */
    public List<EstacionRow> findAll() {
        List<EstacionRow> out = new ArrayList<>();
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new EstacionRow(
                        rs.getInt("codigoEstacion"),
                        rs.getString("documentoCertificacionAdq"),
                        rs.getString("fechaSolicitudCertificacion"),
                        (Integer) rs.getObject("latitud"),
                        (Integer) rs.getObject("longitud"),
                        rs.getString("nombre"),
                        (Integer) rs.getObject("nroCertificacionAdquisicion"),
                        rs.getString("identificadorSismografo")
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error listando estaciones sismológicas", e);
        }
    }

    // DTO liviano para no depender de tu clase de dominio
    public record EstacionRow(int codigoEstacion, String documentoCertificacionAdq, String fechaSolicitudCertificacion, Integer latitud, Integer longitud, String nombre, Integer nroCertificacionAdquisicion, String identificadorSismografo) {}
}
