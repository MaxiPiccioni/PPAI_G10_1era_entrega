package infra.db;

import java.sql.*;

import Clases.CambioEstado;
import Clases.Sismografo;
import Clases.EstacionSismologica;
import Clases.Estado;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    // Devuelve idSismografo (PK) para el identificador lógico (ej. "SIS-001"), o null si no existe.
    // Alias

    public Integer findIdByIdentificador(String identificador) {
        String sql = "SELECT idSismografo FROM Sismografo WHERE identificadorSismografo = ?";
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, identificador);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("idSismografo") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando idSismografo por identificador=" + identificador, e);
        }
    }
    public Integer getIdByIdentificador(String identificador) {
        return findIdByIdentificador(identificador);
    }

    // Devuelve la entidad dominio Sismografo por su identificador (o null si no existe).
    public Sismografo findByIdentificador(String identificador) {
        if (identificador == null) return null;
        String sql = "SELECT * FROM Sismografo WHERE identificadorSismografo = ? LIMIT 1";
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, identificador);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String idf = rs.getString("identificadorSismografo");
                String fechaStr = null;
                try { fechaStr = rs.getString("fechaAdquisicion"); } catch (SQLException ignore) {}
                java.time.LocalDate fecha = (fechaStr != null) ? java.time.LocalDate.parse(fechaStr) : null;
                String nroSerie = null;
                try { nroSerie = rs.getString("nroSerie"); } catch (SQLException ignore) {}

                Integer codigoEstacion = null;
                try { Object o = rs.getObject("codigoEstacion"); if (o != null) codigoEstacion = rs.getInt("codigoEstacion"); } catch (SQLException ignore) {}
                Integer idEstadoActual = null;
                try { Object o2 = rs.getObject("idEstadoActual"); if (o2 != null) idEstadoActual = rs.getInt("idEstadoActual"); } catch (SQLException ignore) {}

                EstacionSismologica estacion = null;
                if (codigoEstacion != null) {
                    EstacionSismologicaDao ed = new EstacionSismologicaDao();
                    try { estacion = ed.findByCodigo(codigoEstacion); } catch (Exception ex1) {
                        try { estacion = ed.findById(codigoEstacion); } catch (Exception ignore) { estacion = null; }
                    }
                }

                Estado estado = null;
                if (idEstadoActual != null) {
                    EstadoDao estDao = new EstadoDao();
                    try { estado = estDao.findById(idEstadoActual); } catch (Exception ignore) { estado = null; }
                }

                // Leer cambios de estado asociados al identificador desde la tabla CambioEstado
                List<CambioEstado> cambios = new ArrayList<>();
                String sqlCambios = "SELECT fechaHoraInicio, fechaHoraFin, idEstado, idMotivo, idEmpleado FROM CambioEstado WHERE identificadorSismografo = ? ORDER BY idCambio";
                try (PreparedStatement ps2 = c.prepareStatement(sqlCambios)) {
                    ps2.setString(1, identificador);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        EstadoDao estadoDao = new EstadoDao();
                        while (rs2.next()) {
                            String inicioStr = rs2.getString("fechaHoraInicio");
                            String finStr = rs2.getString("fechaHoraFin");
                            java.time.LocalDateTime inicio = (inicioStr != null) ? java.time.LocalDateTime.parse(inicioStr) : null;
                            java.time.LocalDateTime fin = (finStr != null) ? java.time.LocalDateTime.parse(finStr) : null;
                            Integer idEstCambio = null;
                            try { Object o = rs2.getObject("idEstado"); if (o != null) idEstCambio = rs2.getInt("idEstado"); } catch (SQLException ignore) {}
                            Estado estCambio = null;
                            if (idEstCambio != null) {
                                try { estCambio = estadoDao.findById(idEstCambio); } catch (Exception ignore) { estCambio = null; }
                            }
                            // construir CambioEstado según disponibilidad de fecha fin
                            try {
                                if (fin != null) {
                                    cambios.add(new CambioEstado(inicio, fin, estCambio));
                                } else {
                                    cambios.add(new CambioEstado(inicio, estCambio));
                                }
                            } catch (Exception e) {
                                // si las firmas del constructor difieren, intentar mediante reflexión o saltar
                                try {
                                    if (fin != null) {
                                        var ctor = CambioEstado.class.getConstructor(java.time.LocalDateTime.class, java.time.LocalDateTime.class, Estado.class);
                                        cambios.add((CambioEstado) ctor.newInstance(inicio, fin, estCambio));
                                    } else {
                                        var ctor = CambioEstado.class.getConstructor(java.time.LocalDateTime.class, Estado.class);
                                        cambios.add((CambioEstado) ctor.newInstance(inicio, estCambio));
                                    }
                                } catch (Exception ignored) { /* skip this cambio if cannot map */ }
                            }
                        }
                    }
                } catch (SQLException e) {
                    // no interrumpir; en caso de error, devolvemos el sismógrafo sin historial
                }

                // construir Sismografo con la lista de cambios poblada
                return new Sismografo(fecha, idf, nroSerie, estacion, cambios, estado);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando Sismografo por identificador=" + identificador, e);
        }
    }

    // Nuevo: actualiza el idEstadoActual del sismógrafo identificado por su identificador lógico.
    public void updateEstadoActual(String identificadorSismografo, Integer idEstadoActual) {
        if (identificadorSismografo == null) throw new IllegalArgumentException("identificadorSismografo null");
        String sql = "UPDATE Sismografo SET idEstadoActual = ? WHERE identificadorSismografo = ?";
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (idEstadoActual != null) ps.setInt(1, idEstadoActual);
            else ps.setNull(1, Types.INTEGER);
            ps.setString(2, identificadorSismografo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando idEstadoActual para sismógrafo=" + identificadorSismografo, e);
        }
    }
}