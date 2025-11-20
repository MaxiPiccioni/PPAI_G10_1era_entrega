package infra.db;

import Clases.Rol;
import java.sql.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RolDao {

    private static final String SQL_FIND_BY_NOMBRE =
            "SELECT idRol, nombre, descripcionRol FROM Rol WHERE nombre = ?";

    private static final String SQL_INSERT =
            "INSERT INTO Rol(nombre, descripcionRol) VALUES(?, ?)";

    private static final String SQL_FIND_ALL =
            "SELECT idRol, nombre, descripcionRol FROM Rol ORDER BY idRol";

    /** Devuelve el id si existe, sino null */
    public Integer findIdByNombre(String nombre) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_NOMBRE)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("idRol");
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando Rol por nombre: " + nombre, e);
        }
    }

    /** Inserta y devuelve el id generado */
    public int insert(String nombre, String descripcionRol) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcionRol);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No se obtuvo id generado para Rol");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando Rol: " + nombre, e);
        }
    }

    /** Crea si no existe. Devuelve siempre el id. */
    public int getOrCreate(String nombre, String descripcionRol) {
        Integer id = findIdByNombre(nombre);
        return (id != null) ? id : insert(nombre, descripcionRol);
    }

    /** (Opcional) listar roles crudos (sin mapear a tu clase) */
    public List<RolRow> findAll() {
        List<RolRow> out = new ArrayList<>();
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new RolRow(
                        rs.getInt("idRol"),
                        rs.getString("nombre"),
                        rs.getString("descripcionRol")
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error listando roles", e);
        }
    }

    // Nuevo: busca y devuelve la entidad dominio Rol por idRol (o null si no existe)
    public Rol findById(int idRol) {
        String sql = "SELECT * FROM Rol WHERE idRol = ?";
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String nombre = getStringSafe(rs, "nombreRol", "nombre", "nombre_rol", "nombrerol");
                String descripcion = getStringSafe(rs, "descripcionRol", "descripcion", "descripcion_rol", "descripcionrol");

                Rol rol;
                try {
                    rol = new Rol(nombre);
                } catch (Throwable t) {
                    try {
                        rol = (Rol) Rol.class.getDeclaredConstructor().newInstance();
                        // intentar setear nombre por reflexión (si existe)
                        try {
                            Method m = Rol.class.getMethod("setNombreRol", String.class);
                            m.invoke(rol, nombre);
                        } catch (ReflectiveOperationException ignore) {
                            // si no existe el setter o falla, continuamos sin él
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException("No se pudo instanciar Rol desde DB", ex);
                    }
                }

                // intentar asignar descripcion si existe setter
                try {
                    Method mDesc = Rol.class.getMethod("setDescripcionRol", String.class);
                    mDesc.invoke(rol, descripcion);
                } catch (ReflectiveOperationException ignore) {
                    // no existe o no se puede invocar -> ignorar
                }

                // intentar asignar idRol probando tanto int como Integer
                try {
                    Method mIdPrim = Rol.class.getMethod("setIdRol", int.class);
                    mIdPrim.invoke(rol, idRol);
                } catch (NoSuchMethodException e1) {
                    try {
                        Method mIdObj = Rol.class.getMethod("setIdRol", Integer.class);
                        mIdObj.invoke(rol, Integer.valueOf(idRol));
                    } catch (ReflectiveOperationException ignore) {
                        // no existe setter para id, lo ignoramos
                    }
                } catch (ReflectiveOperationException ignore) {
                    // problemas al invocar, ignoramos para no romper la lectura
                }

                return rol;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo Rol id=" + idRol, e);
        }
    }

    // Alias simple
    public Rol getById(int idRol) {
        return findById(idRol);
    }

    // Util: intenta leer la primera columna de texto presente entre los nombres proporcionados
    private static String getStringSafe(ResultSet rs, String... names) throws SQLException {
        for (String n : names) {
            try {
                String v = rs.getString(n);
                if (v != null) return v;
            } catch (SQLException ignored) {
                // columna no existe -> siguiente
            }
        }
        return null;
    }

    // DTO liviano para no depender de tu clase de dominio
    public record RolRow(int idRol, String nombre, String descripcionRol) {}
}
