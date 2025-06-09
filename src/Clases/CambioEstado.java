package Clases;
import java.time.LocalDateTime;

public class CambioEstado {
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Estado estado;
    private Empleado empleado;

    public CambioEstado(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, Estado estado) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public boolean esEstadoActual(Estado estado) {
        return this.estado != null && this.estado.equals(estado);
    }
}
