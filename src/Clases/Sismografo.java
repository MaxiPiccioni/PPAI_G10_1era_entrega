package Clases;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        // actualizar en memoria
        this.estadoActual = estadoFueraServicio;

        // intentar persistir en BD: resolver idEstado y llamar a SismografoDao.updateEstadoActual(...)
        try {
            Integer idEstado = null;
            if (estadoFueraServicio != null) {
                try {
                    infra.db.EstadoDao estadoDao = new infra.db.EstadoDao();
                    String amb = null;
                    String nom = null;
                    try { amb = estadoFueraServicio.getAmbito(); } catch (Exception ignored) {}
                    try { nom = estadoFueraServicio.getNombre(); } catch (Exception ignored) {}
                    if (amb != null && nom != null) {
                        try { idEstado = estadoDao.findIdByAmbitoYNombre(amb, nom); } catch (Exception ignored) {}
                    }
                    if (idEstado == null && nom != null) {
                        try { idEstado = estadoDao.getIdByNombre(nom); } catch (Exception ignored) {}
                    }
                } catch (Exception ignored) {
                    // no pudimos resolver idEstado; idEstado queda null
                }
            }

            // llamar al DAO para actualizar la fila del sismógrafo
            try {
                infra.db.SismografoDao sd = new infra.db.SismografoDao();
                sd.updateEstadoActual(this.identificadorSismografo, idEstado);
            } catch (Exception e) {
                // no interrumpir el flujo de dominio por fallo en persistencia
                System.err.println("No se pudo actualizar estado actual en BD para sismógrafo " + this.identificadorSismografo + ": " + e.getMessage());
            }
        } catch (Throwable t) {
            // guardamos en memoria aunque falle cualquier paso anterior
            System.err.println("Error al persistir estado actual: " + t.getMessage());
        }
    }




    public void sismografoEnLinea(Estado estadoEnLinea, Empleado empleado) {
        CambioEstado vigente = obtenerCambioEstadoVigente();

        LocalDateTime ahora = LocalDateTime.now();

        if (vigente != null) {
            vigente.setFechaHoraFin(); // actualizar en memoria
            try {
                infra.db.CambioEstadoDao cambioDao = new infra.db.CambioEstadoDao();
                cambioDao.cerrarVigentePorIdentificador(this.identificadorSismografo, ahora);
            } catch (Exception e) {
                System.err.println("No se pudo cerrar cambio vigente en BD para sismógrafo " + this.identificadorSismografo + ": " + e.getMessage());
            }
        }
        this.crearNuevoCambioDeEstado(estadoEnLinea, null, empleado);


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
                                            Map<MotivoTipo, String> comentariosPorMotivo, Empleado empleado) {
        // 1) cerrar vigente en memoria y en BD
        CambioEstado vigente = obtenerCambioEstadoVigente();

        LocalDateTime ahora = LocalDateTime.now();

        if (vigente != null) {
            vigente.setFechaHoraFin(); // actualizar en memoria
            try {
                infra.db.CambioEstadoDao cambioDao = new infra.db.CambioEstadoDao();
                cambioDao.cerrarVigentePorIdentificador(this.identificadorSismografo, ahora);
            } catch (Exception e) {
                System.err.println("No se pudo cerrar cambio vigente en BD para sismógrafo " + this.identificadorSismografo + ": " + e.getMessage());
            }
        }

        // 2) crear nuevo cambio en memoria (NO persistir aquí; lo hace el Gestor después)
        crearNuevoCambioDeEstado(estadoFueraServicio, comentariosPorMotivo, empleado);
        // nuevo cambio ya quedó en memoria; persistir será responsabilidad del llamador
    }


    public CambioEstado obtenerCambioEstadoVigente() {
        for (CambioEstado cambioEstado : cambiosEstado) {
            if (cambioEstado.getFechaHoraFin() == null) {
                return cambioEstado;
            }
        }
        return null;
    }



    public void crearNuevoCambioDeEstado(Estado estadoFueraServicio,Map<MotivoTipo, String> comentariosPorMotivo, Empleado empleado) {
        CambioEstado nuevoEstado = new CambioEstado(
                LocalDateTime.now(),
                estadoFueraServicio,
                comentariosPorMotivo,
                empleado
        );

        if (nuevoEstado != null) {
            try {
                nuevoEstado.persist(identificadorSismografo);
            } catch (Exception e) {
                System.err.println("Error persistiendo nuevo CambioEstado para " + identificadorSismografo + ": " + e.getMessage());
            }
        }
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
