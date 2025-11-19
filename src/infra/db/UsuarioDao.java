package infra.db;

import java.sql.*;

public class UsuarioDao {

    private static final String SQL_FIND_ID_BY_NOMBRE =
            "SELECT idUsuario FROM Usuario WHERE nombreUsuario = ?";

    private static final String SQL_INSERT =
            "INSERT INTO Usuario(nombreUsuario, contrasena, idEmpleado, idPerfil) VALUES(?,?,?,?)";

    /** Devuelve id si existe, sino null */
    public Integer findIdByNombreUsuario(String nombreUsuario) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ID_BY_NOMBRE)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("idUsuario") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando Usuario: " + nombreUsuario, e);
        }
    }

    /** Inserta y devuelve id generado */
    public int insert(String nombreUsuario, String contrasena, int idEmpleado, Integer idPerfil) {
        try (Connection c = SQLite.get();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            ps.setInt(3, idEmpleado);
            if (idPerfil != null) ps.setInt(4, idPerfil); else ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No se obtuvo id generado para Usuario");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando Usuario: " + nombreUsuario, e);
        }
    }

    /** Crea si no existe (clave natural: nombreUsuario). Devuelve siempre el id. */
    public int getOrCreate(String nombreUsuario, String contraseña, int idEmpleado, Integer idPerfil) {
        Integer id = findIdByNombreUsuario(nombreUsuario);
        return (id != null) ? id : insert(nombreUsuario, contraseña, idEmpleado, idPerfil);
    }
}
