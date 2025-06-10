package Clases;

import java.time.LocalDateTime;

public class OrdenDeInspeccion {
    private LocalDateTime fechaHoraCierre;
    private LocalDateTime fechaHoraFinalizacion;
    private LocalDateTime fechaHoraInicio;
    private Integer numeroOrden;
    public String observacionCierre;
    private Estado estado;
    private Empleado empleado;
    private EstacionSismologica estacion;

    public OrdenDeInspeccion(LocalDateTime fechaHoraFinalizacion, LocalDateTime fechaHoraInicio, Integer numeroOrden, String observacionCierre, Empleado empleado, EstacionSismologica estacion) {
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.numeroOrden = numeroOrden;
        this.observacionCierre = observacionCierre;
        this.empleado = empleado;
        this.estacion = estacion;
    }

    public boolean esDeRI(Empleado ri) {
        return this.empleado.equals(ri);
    }

    public boolean esCompletamenteRealizada() {
        return estado != null && estado.esCompletamenteRealizada();
    }

    public Object[] obtenerDatos() {
        String nombreEstacion = (estacion != null) ? estacion.getNombre() : "No asignada";
        String idSismografo = (estacion != null) ? estacion.getIdentificadorSismografo() : "No asignado";

        return new Object[] {
                numeroOrden,
                fechaHoraFinalizacion,
                nombreEstacion,
                idSismografo
        };
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

    public void setObservacion(String observacion) {
        this.observacionCierre = observacion;
    }

    public void setFechaHoraCierre(LocalDateTime fechaHoraCierre) {
        this.fechaHoraCierre = fechaHoraCierre;
    }

    public Estado getEstado() {
        return this.estado = estado;
    }

    public void cerrar() {
        setEstado(estado);
        setFechaHoraCierre(LocalDateTime.now());
    }


}
