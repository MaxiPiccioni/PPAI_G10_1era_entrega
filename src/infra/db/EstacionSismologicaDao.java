package infra.db;

import Clases.EstacionSismologica;
import java.sql.*;
import java.time.LocalDate;
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

    // Nuevo: busca y devuelve la entidad dominio EstacionSismologica por codigo (PK), o null si no existe
    public EstacionSismologica findByCodigo(int codigoEstacion) {
        String sql = "SELECT * FROM EstacionSismologica WHERE codigoEstacion = ?";
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, codigoEstacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String nombre = getStringSafe(rs, "nombre", "nombreEstacion", "nombre_estacion");
                String documento = getStringSafe(rs, "documentoCertificacionAdq", "documento_certificacion", "documento");
                String fechaSolicitudStr = getStringSafe(rs, "fechaSolicitudCertificacion", "fecha_solicitud_certificacion", "fechaSolicitud");
                Double lat = getDoubleSafe(rs, "latitud", "lat");
                Double lon = getDoubleSafe(rs, "longitud", "lon", "long");
                String identificadorSismografo = getStringSafe(rs, "identificadorSismografo", "identificador_sismografo", "identificador");

                LocalDate fechaSolicitud = (fechaSolicitudStr != null ? LocalDate.parse(fechaSolicitudStr) : null);

                EstacionSismologica estacion;
                try {
                    estacion = new EstacionSismologica(codigoEstacion, nombre);
                } catch (Throwable t) {
                    // fallback: crear instancia vacía y setear por reflexión
                    try {
                        estacion = (EstacionSismologica) EstacionSismologica.class.getDeclaredConstructor().newInstance();
                        try {
                            java.lang.reflect.Method mCod = EstacionSismologica.class.getMethod("setCodigoEstacion", int.class);
                            mCod.invoke(estacion, codigoEstacion);
                        } catch (ReflectiveOperationException ignore) {}
                        try {
                            java.lang.reflect.Method mNom = EstacionSismologica.class.getMethod("setNombre", String.class);
                            mNom.invoke(estacion, nombre);
                        } catch (ReflectiveOperationException ignore) {}
                    } catch (Exception ex) {
                        throw new RuntimeException("No se pudo instanciar EstacionSismologica desde DB", ex);
                    }
                }

                // intentar setear campos adicionales por reflexión (si existen setters)
                try {
                    java.lang.reflect.Method mDoc = EstacionSismologica.class.getMethod("setDocumentoCertificacionAdq", String.class);
                    mDoc.invoke(estacion, documento);
                } catch (ReflectiveOperationException ignore) {}

                try {
                    java.lang.reflect.Method mFecha = EstacionSismologica.class.getMethod("setFechaSolicitudCertificacion", LocalDate.class);
                    mFecha.invoke(estacion, fechaSolicitud);
                } catch (ReflectiveOperationException ignore) {}

                try {
                    java.lang.reflect.Method mLat = EstacionSismologica.class.getMethod("setLatitud", double.class);
                    if (lat != null) mLat.invoke(estacion, lat.doubleValue());
                } catch (ReflectiveOperationException ignore) {}

                try {
                    java.lang.reflect.Method mLon = EstacionSismologica.class.getMethod("setLongitud", double.class);
                    if (lon != null) mLon.invoke(estacion, lon.doubleValue());
                } catch (ReflectiveOperationException ignore) {}

                try {
                    java.lang.reflect.Method mIdent = EstacionSismologica.class.getMethod("setIdentificadorSismografo", String.class);
                    mIdent.invoke(estacion, identificadorSismografo);
                } catch (ReflectiveOperationException ignore) {}

                return estacion;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo EstacionSismologica codigo=" + codigoEstacion, e);
        }
    }

    // Alias con el nombre solicitado por el dominio
    public EstacionSismologica getByCodigo(int codigoEstacion) {
        return findByCodigo(codigoEstacion);
    }

    // Mantener compatibilidad: findById/getById delegan a findByCodigo
    public EstacionSismologica findById(int codigoEstacion) {
        return findByCodigo(codigoEstacion);
    }

    public EstacionSismologica getById(int codigoEstacion) {
        return findByCodigo(codigoEstacion);
    }

    // Utiles
    private static String getStringSafe(ResultSet rs, String... names) throws SQLException {
        for (String n : names) {
            try {
                String v = rs.getString(n);
                if (v != null) return v;
            } catch (SQLException ignored) {}
        }
        return null;
    }

    private static Integer getIntSafe(ResultSet rs, String... names) throws SQLException {
        for (String n : names) {
            try {
                int v = rs.getInt(n);
                if (!rs.wasNull()) return v;
            } catch (SQLException ignored) {}
        }
        return null;
    }

    private static Double getDoubleSafe(ResultSet rs, String... names) throws SQLException {
        for (String n : names) {
            try {
                double v = rs.getDouble(n);
                if (!rs.wasNull()) return Double.valueOf(v);
            } catch (SQLException ignored) {}
        }
        return null;
    }

    // DTO liviano para no depender de tu clase de dominio
    public record EstacionRow(int codigoEstacion, String documentoCertificacionAdq, String fechaSolicitudCertificacion, Integer latitud, Integer longitud, String nombre, Integer nroCertificacionAdquisicion, String identificadorSismografo) {}
}
