package infra.db;

import Clases.EstacionSismologica;
import Clases.Sismografo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DbSeeder {

    // Útil para no duplicar datos si ya corriste el seed
    private boolean tablaVacia(Connection c, String tabla) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(1) AS n FROM " + tabla)) {
            return rs.next() && rs.getInt("n") == 0;
        }
    }

    // Inserta estaciones mínimas (ajustá columnas si tu schema tiene más)
    private void insertEstacion(Connection c, int codigo, String nombre, String identificadorSismografo) throws SQLException {
        String sql = "INSERT OR IGNORE INTO estacion(codigo_estacion, nombre, identificador_sismografo) VALUES(?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            ps.setString(2, nombre);
            ps.setString(3, identificadorSismografo); // puede ser null
            ps.executeUpdate();
        }
    }

    // Inserta sismógrafo (ajustá columnas si difieren)
    private void insertSismografo(Connection c,
                                  String identificador, String nroSerie, LocalDate fechaAdq,
                                  String modelo, String fabricante, Integer codigoEstacion) throws SQLException {
        String sql = """
            INSERT OR IGNORE INTO sismografo
            (identificador, nro_serie, fecha_adquisicion, modelo, fabricante, codigo_estacion)
            VALUES(?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, identificador);
            ps.setString(2, nroSerie);
            ps.setString(3, fechaAdq != null ? fechaAdq.toString() : null); // ISO-8601
            ps.setString(4, modelo);
            ps.setString(5, fabricante);
            if (codigoEstacion != null) ps.setInt(6, codigoEstacion); else ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
        }
    }

    // Inserta el cambio de estado vigente inicial (si querés dejar algo abierto)
    private void insertCambioEstadoInicial(Connection c,
                                           String sismografoId, String estado, LocalDateTime inicio) throws SQLException {
        String sql = "INSERT INTO cambio_estado(sismografo_id, estado, fecha_inicio, fecha_fin) VALUES(?,?,?,NULL)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sismografoId);
            ps.setString(2, estado);
            ps.setString(3, inicio != null ? inicio.toString() : LocalDateTime.now().toString());
            ps.executeUpdate();
        }
    }

    /* Semilla básica a partir de tus listas hardcodeadas
    public void seedDesdeObjetos(List<EstacionSismologica> estaciones, List<Sismografo> sismografos) {
        try (Connection c = SQLite.get()) {
            c.setAutoCommit(false);
            try {
                // 1) Estaciones (mínimo: código, nombre e id actual si ya lo tenés)
                //    Si aún no seteaste identificadores en ES, pasá null en el 3er parámetro.
                if (tablaVacia(c, "estacion") && estaciones != null) {
                    for (EstacionSismologica es : estaciones) {
                        insertEstacion(c, es.getCodigoEstacion(), es.getNombre(), es.getIdentificadorSismografo());
                    }
                }

                // 2) Sismógrafos
                if (tablaVacia(c, "sismografo") && sismografos != null) {
                    for (Sismografo s : sismografos) {
                        // Ajustá getters según tu clase:
                        Integer codigoEst = (s.getEstacion() != null) ? s.getEstacion().getCodigoEstacion() : null;
                        insertSismografo(
                                c,
                                s.getIdentificadorSismografo(),
                                s.getNroSerie(),
                                s.getFechaAdquisicion(),     // LocalDate
                                s.getModelo(),               // si no tenés, poné null
                                s.getFabricante(),           // si no tenés, poné null
                                codigoEst
                        );

                        // 3) Cambio de estado inicial (opcional)
                        //    Si tu Sismografo conoce el estado inicial, úsalo:
                        String estadoInicial = (s.getEstadoActual() != null) ? s.getEstadoActual().getNombre() : "FUERA_DE_LINEA";
                        insertCambioEstadoInicial(c, s.getIdentificadorSismografo(), estadoInicial, LocalDateTime.now());
                    }
                }

                c.commit();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error seeding DB", e);
        }

    }
     */
}
