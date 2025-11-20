package infra.db;

import Clases.Estado;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class EstadoDao {

    private static final String SQL_FIND_ID =
            "SELECT idEstado FROM Estado WHERE ambito = ? AND nombreEstado = ?";

    private static final String SQL_INSERT =
            "INSERT INTO Estado(ambito, nombreEstado) VALUES(?, ?)";

    /** Devuelve id si existe, sino null */
    public Integer findId(String ambito, String nombreEstado) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ID)) {
            ps.setString(1, ambito);
            ps.setString(2, nombreEstado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("idEstado") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando Estado: " + ambito + " / " + nombreEstado, e);
        }
    }

    /** Inserta y devuelve el id generado */
    public int insert(String ambito, String nombreEstado) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ambito);
            ps.setString(2, nombreEstado);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No se obtuvo id generado para Estado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando Estado: " + ambito + " / " + nombreEstado, e);
        }
    }

    /** Crea si no existe (clave natural: ambito + nombreEstado). Devuelve siempre el id. */
    public int getOrCreate(String ambito, String nombreEstado) {
        Integer id = findId(ambito, nombreEstado);
        return (id != null) ? id : insert(ambito, nombreEstado);
    }

    /** Devuelve el idEstado dado el nombre del estado. */
    public int getIdByNombre(String nombre) {
        String sql = "SELECT idEstado FROM Estado WHERE nombreEstado = ?";
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("idEstado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando idEstado por nombre", e);
        }
        throw new RuntimeException("No existe Estado con nombre: " + nombre);
    }

    // Nuevo: devuelve la entidad dominio Estado por idEstado (o null si no existe)
    public Estado findById(int idEstado) {
        String sql = "SELECT * FROM Estado WHERE idEstado = ?";
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idEstado);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String ambito = getStringSafe(rs, "ambito", "ambitoEstado", "ambito_estado");
                String nombre = getStringSafe(rs, "nombre", "nombreEstado", "nombre_estado");

                Estado estado;
                try {
                    estado = new Estado(ambito, nombre);
                } catch (Throwable t) {
                    // fallback: intentar instancia vacía y setear por reflexión
                    try {
                        estado = (Estado) Estado.class.getDeclaredConstructor().newInstance();
                        try {
                            java.lang.reflect.Method mAmb = Estado.class.getMethod("setAmbito", String.class);
                            mAmb.invoke(estado, ambito);
                        } catch (ReflectiveOperationException ignore) {}
                        try {
                            java.lang.reflect.Method mNom = Estado.class.getMethod("setNombre", String.class);
                            mNom.invoke(estado, nombre);
                        } catch (ReflectiveOperationException ignore) {}
                    } catch (Exception ex) {
                        throw new RuntimeException("No se pudo instanciar Estado desde DB", ex);
                    }
                }

                // intentar asignar idEstado si existe setter
                try {
                    try {
                        java.lang.reflect.Method mId = Estado.class.getMethod("setIdEstado", int.class);
                        mId.invoke(estado, idEstado);
                    } catch (NoSuchMethodException ns) {
                        java.lang.reflect.Method mIdObj = Estado.class.getMethod("setIdEstado", Integer.class);
                        mIdObj.invoke(estado, Integer.valueOf(idEstado));
                    }
                } catch (ReflectiveOperationException ignore) { }

                return estado;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo Estado id=" + idEstado, e);
        }
    }

    // Alias simple
    public Estado getById(int idEstado) {
        return findById(idEstado);
    }

    // Nuevo: busca idEstado por ambito + nombre (tolerante a columnas 'nombre' / 'nombreEstado').
    public Integer findIdByAmbitoYNombre(String ambito, String nombre) {
        if (ambito == null || nombre == null) return null;
        String[] candidates = new String[] {
            "SELECT idEstado FROM Estado WHERE ambito = ? AND nombre = ? LIMIT 1",
            "SELECT idEstado FROM Estado WHERE ambito = ? AND nombreEstado = ? LIMIT 1",
            "SELECT idEstado FROM Estado WHERE nombre = ? LIMIT 1",
            "SELECT idEstado FROM Estado WHERE nombreEstado = ? LIMIT 1"
        };
        try (Connection c = SQLite.get()) {
            for (String sql : candidates) {
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    // si la consulta espera ambito,nombre -> seteamos ambos; si sólo nombre -> seteamos 1er param
                    if (sql.contains("ambito")) {
                        ps.setString(1, ambito);
                        ps.setString(2, nombre);
                    } else {
                        ps.setString(1, nombre);
                    }
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) return rs.getInt("idEstado");
                    }
                } catch (SQLException ignore) {
                    // intentar siguiente candidate
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando idEstado por ambito/nombre", e);
        }
        return null;
    }

    // Nuevo: devuelve todos los Estados mapeados a objetos dominio (tolerante a nombres de columna)
    public List<Estado> findAll() {
        // usar SELECT * para tolerar esquemas con nombres distintos de columnas
        String sql = "SELECT * FROM Estado ORDER BY idEstado";
        List<Estado> out = new ArrayList<>();
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String ambito = getStringSafe(rs, "ambito", "ambitoEstado", "ambito_estado");
                String nombre = getStringSafe(rs, "nombre", "nombreEstado", "nombre_estado");
                Integer idFromRs = getIntSafe(rs, "idEstado", "id_estado", "id");

                Estado estado;
                try {
                    estado = new Estado(ambito, nombre);
                } catch (Throwable t) {
                    // fallback: intentar instancia vacía y setear por reflexión
                    try {
                        estado = (Estado) Estado.class.getDeclaredConstructor().newInstance();
                        try {
                            java.lang.reflect.Method mAmb = Estado.class.getMethod("setAmbito", String.class);
                            mAmb.invoke(estado, ambito);
                        } catch (ReflectiveOperationException ignore) {}
                        try {
                            java.lang.reflect.Method mNom = Estado.class.getMethod("setNombre", String.class);
                            mNom.invoke(estado, nombre);
                        } catch (ReflectiveOperationException ignore) {}
                    } catch (Exception ex) {
                        throw new RuntimeException("No se pudo instanciar Estado desde DB", ex);
                    }
                }

                // intentar asignar idEstado si existe setter
                try {
                    if (idFromRs != null) {
                        try {
                            java.lang.reflect.Method mId = Estado.class.getMethod("setIdEstado", int.class);
                            mId.invoke(estado, idFromRs.intValue());
                        } catch (NoSuchMethodException ns) {
                            java.lang.reflect.Method mIdObj = Estado.class.getMethod("setIdEstado", Integer.class);
                            mIdObj.invoke(estado, idFromRs);
                        }
                    }
                } catch (ReflectiveOperationException ignore) { }

                out.add(estado);
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error listando Estados", e);
        }
    }

    // Util: intenta leer la primera columna de texto presente entre los nombres proporcionados
    private static String getStringSafe(ResultSet rs, String... names) throws SQLException {
        for (String n : names) {
            try {
                String v = rs.getString(n);
                if (v != null) return v;
            } catch (SQLException ignored) { }
        }
        return null;
    }
    // Util: intenta leer la primera columna entera presente entre los nombres proporcionados
    private static Integer getIntSafe(ResultSet rs, String... names) throws SQLException {
        for (String n : names) {
            try {
                int v = rs.getInt(n);
                if (!rs.wasNull()) return v;
            } catch (SQLException ignored) { }
        }
        // fallback: intentar leer primera columna (si existe) como id
        try {
            int v = rs.getInt(1);
            if (!rs.wasNull()) return v;
        } catch (SQLException ignored) {}
        return null;
    }

}
