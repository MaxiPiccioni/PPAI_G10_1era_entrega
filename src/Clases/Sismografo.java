package Clases;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sismografo {
    private LocalDate fechaAdquisicion;
    private String identificadorSismografo;
    private String nroSerie;
    private List<CambioEstado> cambiosEstado;
    public EstacionSismologica estacionSismologica;
    private Estado estadoActual;


    public Sismografo(LocalDate fechaAdquisicion, String identificadorSismografo, String nroSerie, EstacionSismologica estacionSismologica, List<CambioEstado> cambioEstado, Estado estadoActual) {
        this.fechaAdquisicion = fechaAdquisicion;
        this.identificadorSismografo = identificadorSismografo;
        this.nroSerie = nroSerie;
        this.estacionSismologica = estacionSismologica;
        this.cambiosEstado = cambioEstado;
        this.estadoActual = estadoActual;
    }


    public String getIdentificadorSismografo() {
        return identificadorSismografo;
    }


    public void setEstadoActual(Estado estadoFueraServicio) {
        this.estadoActual = estadoFueraServicio;
    }




    public void sismografoEnLinea(Estado estadoEnLinea) {
        setEstadoActual(estadoEnLinea);
    }


    /* Correción 2 (Corregir CambioEstado --> obtenerCambioEstadoActual() --> getEstado -No hay
    que pedir estado, hay que pedir fechaHoraFin y debe ser null.
)
    public CambioEstado obtenerCambioEstadoActual() {
        for (CambioEstado cambioEstado : cambiosEstado) {
            if (cambioEstado.esEstadoActual(cambioEstado.getEstado())) {
                return cambioEstado;
            }
        }
        return null;
    }


    public void sismografoEnFueraDeServicio(Estado estadoFueraServicio, Map<MotivoTipo, String> comentariosPorMotivo) {
        CambioEstado estadoActual = obtenerCambioEstadoActual();

        if (estadoActual != null) {
            estadoActual.setFechaHoraFin();
        }
        crearNuevoCambioDeEstado(estadoFueraServicio, comentariosPorMotivo);
    }
     */


    public void sismografoEnFueraDeServicio(Estado estadoFueraServicio,
                                            Map<MotivoTipo, String> comentariosPorMotivo) {
        // 1) cerrar vigente en memoria y en BD
        CambioEstado vigente = obtenerCambioEstadoVigente();

        LocalDateTime ahora = LocalDateTime.now();

        if (vigente != null) {
            // actualizar en memoria
            vigente.setFechaHoraFin(); // si este método pone ahora por defecto
            // intentar cerrar en BD (por identificador textual)
            try {
                infra.db.CambioEstadoDao cambioDao = new infra.db.CambioEstadoDao();
                cambioDao.cerrarVigentePorIdentificador(this.identificadorSismografo, ahora);
            } catch (Exception e) {
                // no rompemos el flujo; sólo log para debug
                System.err.println("No se pudo cerrar cambio vigente en BD para sismógrafo " + this.identificadorSismografo + ": " + e.getMessage());
            }
        }

        // 2) crear nuevo cambio en memoria (y persistir en BD)
        crearNuevoCambioDeEstado(estadoFueraServicio, comentariosPorMotivo);
        CambioEstado nuevo = obtenerUltimoCambioDeEstado();

        // intentar persistir apertura del nuevo cambio en BD
        try {
            // resolver idEstado (si possible) a través de EstadoDao
            Integer idEstado = null;
            try {
                infra.db.EstadoDao ed = new infra.db.EstadoDao();
                String amb = null;
                String nom = null;
                try { java.lang.reflect.Method ma = Estado.class.getMethod("getAmbito"); amb = (String) ma.invoke(estadoFueraServicio); } catch (Exception ignore) {}
                try { java.lang.reflect.Method mn = Estado.class.getMethod("getNombre"); nom = (String) mn.invoke(estadoFueraServicio); } catch (Exception ignore) {}
                idEstado = ed.findIdByAmbitoYNombre(amb, nom);
            } catch (Exception ignore) { /* si falla, idEstado queda null y DAO puede decidir */ }

            infra.db.CambioEstadoDao cambioDao = new infra.db.CambioEstadoDao();
            // idMotivo no manejado aquí -> null; idEmpleado desconocido en este contexto -> null
            cambioDao.abrir(
                nuevo.getFechaHoraInicio(),
                (idEstado != null ? idEstado : 0), // si no hay idEstado, se pasa 0 (o ajustar según esquema)
                null, // idMotivo
                null, // idEmpleado (DAO acepta Integer ahora)
                this.identificadorSismografo
            );
        } catch (Exception e) {
            System.err.println("No se pudo persistir apertura de CambioEstado para sismógrafo " + this.identificadorSismografo + ": " + e.getMessage());
        }
    }


    public CambioEstado obtenerCambioEstadoVigente() {
        for (CambioEstado cambioEstado : cambiosEstado) {
            if (cambioEstado.getFechaHoraFin() == null) {
                return cambioEstado;
            }
        }
        return null;
    }



    public void crearNuevoCambioDeEstado(Estado estadoFueraServicio,Map<MotivoTipo, String> comentariosPorMotivo) {
        CambioEstado nuevoEstado = new CambioEstado(
                LocalDateTime.now(),
                estadoFueraServicio,
                comentariosPorMotivo
        );
        setEstadoActual(estadoFueraServicio);
        cambiosEstado.add(nuevoEstado);
    }


    public CambioEstado obtenerUltimoCambioDeEstado() {
        if (cambiosEstado.isEmpty()) return null;
        return cambiosEstado.get(cambiosEstado.size() - 1);
    }


    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }


    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }


    public void setIdentificadorSismografo(String identificadorSismografo) {
        this.identificadorSismografo = identificadorSismografo;
    }


    public String getNroSerie() {
        return nroSerie;
    }


    public void setNroSerie(String nroSerie) {
        this.nroSerie = nroSerie;
    }


    public List<CambioEstado> getCambiosEstado() {
        return cambiosEstado;
    }


    public void setCambiosEstado(List<CambioEstado> cambiosEstado) {
        this.cambiosEstado = cambiosEstado;
    }


    public EstacionSismologica getEstacionSismologica() {
        return estacionSismologica;
    }


    public void setEstacionSismologica(EstacionSismologica estacionSismologica) {
        this.estacionSismologica = estacionSismologica;
    }


    public Estado getEstadoActual() {
        return estadoActual;
    }
}
