package Clases;

import java.time.LocalDateTime;

public class Sesion {
    private Usuario usuario;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    public Sesion(Usuario usuario) {
        this.usuario = usuario;
        this.fechaHoraInicio = LocalDateTime.now();
        this.fechaHoraFin = null;
    }

    public Usuario obtenerRILogueado() {
        Empleado empleado = usuario.obtenerEmpleado();
        if (empleado.getRol().getNombreRol().equalsIgnoreCase("Responsable de Inspección")) {
            return usuario;
        }
        return null;
    }
}
