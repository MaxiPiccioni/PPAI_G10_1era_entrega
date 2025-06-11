package Clases;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Sismografo {
    private LocalDate fechaAdquisicion;
    private String identificadorSismografo;
    private String nroSerie;
    private List<CambioEstado> cambiosEstado;
    public EstacionSismologica estacionSismologica;


    public Sismografo(LocalDate fechaAdquisicion, String identificadorSismografo, String nroSerie, EstacionSismologica estacionSismologica, List<CambioEstado> cambioEstado) {
        this.fechaAdquisicion = fechaAdquisicion;
        this.identificadorSismografo = identificadorSismografo;
        this.nroSerie = nroSerie;
        this.cambiosEstado = cambioEstado;
        this.estacionSismologica = estacionSismologica;
    }

    public String getIdentificadorSismografo() {
        return identificadorSismografo;
    }

    public void sismografoEnFueraDeServicio(Estado estadoFueraServicio, Map<MotivoTipo, String> comentariosPorMotivo) {
        CambioEstado estadoActual = obtenerCambioEstadoActual();

        if (estadoActual != null) {
            estadoActual.finalizar();
        }
        crearNuevoCambioDeEstado(estadoFueraServicio, comentariosPorMotivo);
    }

    public CambioEstado obtenerCambioEstadoActual() {
        for (CambioEstado cambioEstado : cambiosEstado) {
            if (cambioEstado.esEstadoActual(cambioEstado.getEstado())) {
                return cambioEstado;
            }
        }
        return null;
    }

    public void crearNuevoCambioDeEstado(Estado estadoFueraServicio,Map<MotivoTipo, String> comentariosPorMotivo) {
        CambioEstado nuevoEstado = new CambioEstado(
                LocalDateTime.now(),
                null,
                estadoFueraServicio
        );
        nuevoEstado.crearMotivoFueraDeServicio(comentariosPorMotivo);

        cambiosEstado.add(nuevoEstado);

    }

}