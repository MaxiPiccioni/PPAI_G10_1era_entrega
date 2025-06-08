package Clases;
import java.time.LocalDate;

public class Sismografo {
    private LocalDate fechaAdquisicion;
    private String identificadorSismografo;
    private String nroSerie;
    private CambioEstado estadoActual;


    public Sismografo(LocalDate fechaAdquisicion, String identificadorSismografo, String nroSerie) {
        this.fechaAdquisicion = fechaAdquisicion;
        this.identificadorSismografo = identificadorSismografo;
        this.nroSerie = nroSerie;
    }

    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public String getIdentificadorSismografo() {
        return identificadorSismografo;
    }

    public String getNroSerie() {
        return nroSerie;
    }

    public CambioEstado getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(CambioEstado estadoActual) {
        this.estadoActual = estadoActual;
    }

}
