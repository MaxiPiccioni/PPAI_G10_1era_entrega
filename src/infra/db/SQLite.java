package infra.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public final class SQLite {
    private static final String URL = "jdbc:sqlite:data/ccrs.db"; // se crea si no existe

    private SQLite() {}

    public static Connection get() {
        try {
            String dbPath = "data/ccrs.db";
            File dbDir = new File("data");
            if (!dbDir.exists()) dbDir.mkdirs();
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (SQLException e) {
            throw new RuntimeException("Error inicializando SQLite", e);
        }
    }

    public static void init() {
        try (Connection c = get(); Statement st = c.createStatement()) {


            st.execute("""
                CREATE TABLE IF NOT EXISTS Estado (
                  idEstado INTEGER PRIMARY KEY AUTOINCREMENT,
                  ambito TEXT,
                  nombreEstado TEXT
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS MotivoTipo (
                  idMotivoTipo INTEGER PRIMARY KEY AUTOINCREMENT,
                  descripcion TEXT
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS MotivoFueraServicio (
                  idMotivo INTEGER PRIMARY KEY AUTOINCREMENT,
                  comentario TEXT,
                  idMotivoTipo INTEGER,
                  FOREIGN KEY (idMotivoTipo) REFERENCES MotivoTipo(idMotivoTipo)
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS Rol (
                  idRol INTEGER PRIMARY KEY AUTOINCREMENT,
                  descripcionRol TEXT,
                  nombre TEXT
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS Empleado (
                  idEmpleado INTEGER PRIMARY KEY AUTOINCREMENT,
                  apellido TEXT,
                  nombre TEXT,
                  mail TEXT,
                  telefono TEXT,
                  idRol INTEGER,
                  FOREIGN KEY (idRol) REFERENCES Rol(idRol)
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS Permiso (
                  idPermiso INTEGER PRIMARY KEY AUTOINCREMENT,
                  descripcion TEXT,
                  nombre TEXT
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS Perfil (
                  idPerfil INTEGER PRIMARY KEY AUTOINCREMENT,
                  descripcion TEXT,
                  nombre TEXT,
                  idPermiso INTEGER,
                  FOREIGN KEY (idPermiso) REFERENCES Permiso(idPermiso)
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS Usuario (
                  idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,
                  nombreUsuario TEXT,
                  contrasena TEXT,
                  idEmpleado INTEGER,
                  idPerfil INTEGER,
                  FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado),
                  FOREIGN KEY (idPerfil) REFERENCES Perfil(idPerfil)
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS EstacionSismologica (
                  codigoEstacion INTEGER PRIMARY KEY AUTOINCREMENT,
                  documentoCertificacionAdq TEXT,
                  fechaSolicitudCertificacion TEXT,
                  latitud REAL,
                  longitud REAL,
                  nombre TEXT,
                  nroCertificacionAdquisicion INTEGER,
                  identificadorSismografo INTEGER
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS CambioEstado (
                  idCambio INTEGER PRIMARY KEY AUTOINCREMENT,
                  fechaHoraInicio TEXT,
                  fechaHoraFin TEXT,
                  idEstado INTEGER,
                  idMotivo INTEGER,
                  idEmpleado INTEGER,
                  identificadorSismografo INTEGER,
                  FOREIGN KEY (idEstado) REFERENCES Estado(idEstado),
                  FOREIGN KEY (idMotivo) REFERENCES MotivoFueraServicio(idMotivo),
                  FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado),
                  FOREIGN KEY (identificadorSismografo) REFERENCES Sismografo(identificadorSismografo)
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS Sismografo (
                  identificadorSismografo TEXT PRIMARY KEY,
                  fechaAdquisicion TEXT,
                  nroSerie INTEGER,
                  codigoEstacion INTEGER,
                  idEstadoActual INTEGER,
                  FOREIGN KEY (codigoEstacion) REFERENCES EstacionSismologica(codigoEstacion),
                  FOREIGN KEY (idEstadoActual) REFERENCES Estado(idEstado)
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS Sesion (
                  idSesion INTEGER PRIMARY KEY AUTOINCREMENT,
                  fechaYHoraInicio TEXT,
                  fechaYHoraFin TEXT,
                  idUsuario INTEGER,
                  FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
                );
            """);

                        st.execute("""
                CREATE TABLE IF NOT EXISTS OrdenDeInspeccion (
                  numeroOrden INTEGER PRIMARY KEY,
                  fechaHoraInicio TEXT,
                  fechaHoraFinalizacion TEXT,
                  fechaHoraCierre TEXT,
                  observacionCierre TEXT,
                  codigoEstacion INTEGER,
                  idEmpleadoResponsable INTEGER,
                  idEstado INTEGER,
                  FOREIGN KEY (codigoEstacion) REFERENCES EstacionSismologica(codigoEstacion),
                  FOREIGN KEY (idEmpleadoResponsable) REFERENCES Empleado(idEmpleado),
                  FOREIGN KEY (idEstado) REFERENCES Estado(idEstado)
                );
            """);



        } catch (SQLException e) {
            throw new RuntimeException("Error inicializando SQLite", e);
        }
    }
}
