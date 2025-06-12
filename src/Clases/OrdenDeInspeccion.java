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
    private EstacionSismologica estacionSismologica;

    public OrdenDeInspeccion(LocalDateTime fechaHoraFinalizacion, LocalDateTime fechaHoraInicio, Integer numeroOrden, String observacionCierre, Empleado empleado, EstacionSismologica estacion) {
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.numeroOrden = numeroOrden;
        this.observacionCierre = observacionCierre;
        this.empleado = empleado;
        this.estacionSismologica = estacion;
    }


    public LocalDateTime getFechaHoraFinalizacion() {
        return fechaHoraFinalizacion;
    }


    public Integer getNumeroOrden() {
        return numeroOrden;
    }


    public EstacionSismologica getEstacion() {
        return estacionSismologica;
    }


    public void setObservacion(String observacion) {
        this.observacionCierre = observacion;
    }


    public void setEstado(Estado estado) {
        this.estado = estado;
    }


    public void setFechaHoraCierre(LocalDateTime fechaHoraCierre) {
        this.fechaHoraCierre = fechaHoraCierre;
    }


    public boolean esDeRI(Empleado ri) {
        return this.empleado.equals(ri);
    }


    public boolean esCompletamenteRealizada() {
        return estado != null && estado.esCompletamenteRealizada();
    }


    public Object[] obtenerDatos() {
        String nombreEstacion = (estacionSismologica != null) ? estacionSismologica.getNombre() : "No asignada";
        String idSismografo = (estacionSismologica != null) ? estacionSismologica.getIdentificadorSismografo() : "No asignado";

        return new Object[] {
                numeroOrden,
                fechaHoraFinalizacion,
                nombreEstacion,
                idSismografo
        };
    }


    public void cerrar(Estado estado, LocalDateTime fechaHoraCierre, String observacionCierre) {
        setEstado(estado);
        setObservacion(observacionCierre);
        setFechaHoraCierre(fechaHoraCierre);
    }


    public LocalDateTime getFechaHoraCierre() {
        return fechaHoraCierre;
    }


    public void setFechaHoraFinalizacion(LocalDateTime fechaHoraFinalizacion) {
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
    }


    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }


    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }


    public void setNumeroOrden(Integer numeroOrden) {
        this.numeroOrden = numeroOrden;
    }


    public String getObservacionCierre() {
        return observacionCierre;
    }


    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }


    public Empleado getEmpleado() {
        return empleado;
    }


    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }


    public EstacionSismologica getEstacionSismologica() {
        return estacionSismologica;
    }


    public void setEstacionSismologica(EstacionSismologica estacionSismologica) {
        this.estacionSismologica = estacionSismologica;
    }


    public Estado getEstado() {
        return this.estado = estado;
    }
}
