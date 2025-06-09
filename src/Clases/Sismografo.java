package Clases;
import java.time.LocalDate;

public class Sismografo {
    private LocalDate fechaAdquisicion;
    private String identificadorSismografo;
    private String nroSerie;
    private CambioEstado estadoActual;
    public EstacionSismologica estacionSismologica;
    public Estado estado;


    public Sismografo(LocalDate fechaAdquisicion, String identificadorSismografo, String nroSerie,  CambioEstado estadoActual, EstacionSismologica estacionSismologica) {
        this.fechaAdquisicion = fechaAdquisicion;
        this.identificadorSismografo = identificadorSismografo;
        this.nroSerie = nroSerie;
        this.estadoActual = estadoActual;
        this.estacionSismologica = estacionSismologica;
    }

    public String getIdentificadorSismografo() {
        return identificadorSismografo;
    }

    public void setIdentificadorSismografo() {
        this.estadoActual = estadoActual;
    }

}
