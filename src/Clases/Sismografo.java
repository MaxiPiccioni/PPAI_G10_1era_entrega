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
        this.cambiosEstado = new ArrayList<>();
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
        CambioEstado vigente = obtenerCambioEstadoVigente();

        if (vigente != null) {
            // Si tu setter pone "ahora" por defecto, dejalo sin argumento.
            // Si no, pasale LocalDateTime.now().
            vigente.setFechaHoraFin(); // o vigente.setFechaHoraFin(LocalDateTime.now());
        }

        crearNuevoCambioDeEstado(estadoFueraServicio, comentariosPorMotivo);
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
