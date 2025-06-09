package Clases;

import java.time.LocalDateTime;

public class OrdenDeInspeccion {
    private LocalDateTime fechaHoraCierre;
    private LocalDateTime fechaHoraFinalizacion;
    private LocalDateTime fechaHoraInicio;
    private Integer numeroOrden;
    private String observacionCierre;
    private Estado estado;
    private Empleado empleado;
    private EstacionSismologica estacionSismologica;

    public OrdenDeInspeccion(LocalDateTime fechaHoraFinalizacion, LocalDateTime fechaHoraInicio, Integer numeroOrden, String observacionCierre) {
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.numeroOrden = numeroOrden;
        this.observacionCierre = observacionCierre;
    }

    public LocalDateTime getFechaHoraFinalizacion() {
        return fechaHoraFinalizacion;
    }

    public Integer getNumeroOrden() {
        return numeroOrden;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setFechaHoraCierre(LocalDateTime fechaHoraCierre) {
        this.fechaHoraCierre = fechaHoraCierre;
    }

}
