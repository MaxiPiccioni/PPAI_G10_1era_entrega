package Clases;

import java.time.LocalDateTime;

public class Sesion {
    private Usuario usuarioLogueado;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    public Sesion(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.fechaHoraInicio = LocalDateTime.now();
        this.fechaHoraFin = null;
    }

    public Empleado obtenerRILogueado() {
        return usuarioLogueado.obtenerEmpleado();

    }


    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }


    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }


    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }


    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }


    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }


    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }
}
