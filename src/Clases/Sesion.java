package Clases;

import java.time.LocalDateTime;

public class Sesion {
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Usuario usuarioLogueado;

    public Sesion(Usuario usuario) {
        this.fechaHoraInicio = fechaHoraInicio = LocalDateTime.now();
        this.usuarioLogueado = usuario;
    }

    public Empleado obtenerRILogueado() {
        if (this.fechaHoraInicio != null &&
                this.fechaHoraFin == null &&
                this.usuarioLogueado != null &&
                this.usuarioLogueado.obtenerEmpleado() != null) {

            Empleado empleado = this.usuarioLogueado.obtenerEmpleado();
            if (empleado.getRol().getNombreRol().equals("Responsable de Inspeccion")) {
                return empleado;
            }
        }
        return null;
    }

    public Empleado obtenerEmpleado() {
        return this.obtenerEmpleado();
    }
}
