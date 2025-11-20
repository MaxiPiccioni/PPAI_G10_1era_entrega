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

    public OrdenDeInspeccion(LocalDateTime fechaHoraFinalizacion, LocalDateTime fechaHoraInicio, Integer numeroOrden, String observacionCierre, Empleado empleado, EstacionSismologica estacionSismologica) {
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.numeroOrden = numeroOrden;
        this.observacionCierre = observacionCierre;
        this.empleado = empleado;
        this.estacionSismologica = estacionSismologica;
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


    public boolean esDeRI(Empleado riLogueado) {
        // protección básica
        if (riLogueado == null) return false;

        // asumo que la orden tiene un atributo 'empleado' o similar que representa al RI responsable.
        // intentamos acceder primero al campo/propiedad 'empleado' vía getter si existe, sino uso el campo directo.
        Empleado empleadoOrden = null;
        try {
            // intentar getter "getEmpleado"
            java.lang.reflect.Method gm = this.getClass().getMethod("getEmpleado");
            empleadoOrden = (Empleado) gm.invoke(this);
        } catch (Exception ignored) {
            try {
                // intentar acceder campo directo "empleado"
                java.lang.reflect.Field f = this.getClass().getDeclaredField("empleado");
                f.setAccessible(true);
                empleadoOrden = (Empleado) f.get(this);
            } catch (Exception ignored2) {
                // no pudimos obtener empleado de la orden -> no es de RI
                return false;
            }
        }

        if (empleadoOrden == null) return false;

        // 1) comparar por id si existe getIdEmpleado()
        try {
            java.lang.reflect.Method mId = Empleado.class.getMethod("getIdEmpleado");
            Object idOrden = mId.invoke(empleadoOrden);
            Object idLog = mId.invoke(riLogueado);
            if (idOrden != null && idLog != null) {
                return idOrden.equals(idLog);
            }
        } catch (Exception ignored) {
            // si no existe getIdEmpleado, seguimos con el siguiente criterio
        }

        // 2) comparar por email (método obtenerEmail() presente en tu modelo)
        try {
            String emailOrden = empleadoOrden.obtenerEmail();
            String emailLog = riLogueado.obtenerEmail();
            if (emailOrden != null && emailLog != null) {
                return emailOrden.equalsIgnoreCase(emailLog);
            }
        } catch (Exception ignored) {
            // si no existe obtenerEmail o falla, fallback a equals()
        }

        // 3) fallback a equals del objeto Empleado
        return empleadoOrden.equals(riLogueado);
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
        // setear en memoria
        setEstado(estado);
        setObservacion(observacionCierre);
        setFechaHoraCierre(fechaHoraCierre);

        // Delegar persistencia a la DAO (la DAO se encarga de resolver idEstado)
        try {
            infra.db.OrdenDeInspeccionDao ordenDao = new infra.db.OrdenDeInspeccionDao();
            ordenDao.actualizarCierre(this.getNumeroOrden(), estado, fechaHoraCierre, observacionCierre);
        } catch (Exception e) {
            System.err.println("No se pudo persistir cierre de orden " + this.numeroOrden + ": " + e.getMessage());
        }
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

    public String getIdentificadorSismografo(){
        return estacionSismologica.getIdentificadorSismografo();
    }

    public Estado getEstado() {
        return this.estado;
    }
}
