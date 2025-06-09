package Clases;

import java.time.LocalDateTime;

public class EstacionSismologica {
    private int codigoEstacion;
    private String documentoCertificacionAdq;
    private LocalDateTime fechaSolicitudCertificacion;
    private Integer latitud;
    private Integer longitud;
    private String nombre;
    private Integer nroCertificacionAdquisicion;
    private Sismografo sismografo;

    public EstacionSismologica(int codigoEstacion, String nombre) {
        this.codigoEstacion = codigoEstacion;
        this.nombre = nombre;
    }

    public int getCodigoEstacion() {
        return codigoEstacion;
    }

    public String getNombre() {
        return nombre;
    }


    public void setSismografo(Sismografo sismografo) {
        this.sismografo = sismografo;
    }

    public String getIdentificadorSismografo() {
        if (sismografo != null) {
            return sismografo.getIdentificadorSismografo();
        }
        return "";
    }



}
