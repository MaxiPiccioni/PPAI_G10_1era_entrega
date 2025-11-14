package Clases;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class EstacionSismologica {
    private int codigoEstacion;
    private String documentoCertificacionAdq;
    private LocalDateTime fechaSolicitudCertificacion;
    private Integer latitud;
    private Integer longitud;
    private String nombre;
    private Integer nroCertificacionAdquisicion;
    /* corrección 1
    private Sismografo sismografo;
     */
    private String identificadorSismografo;


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



    // -----------------------------------------
    // Nuevo modelo: trabajar con el identificador
    // -----------------------------------------
    public String getIdentificadorSismografo() {
        return identificadorSismografo != null ? identificadorSismografo : "";
    }

    public void setIdentificadorSismografo(String identificadorSismografo) {
        this.identificadorSismografo = identificadorSismografo;
    }


    /*
    public void ponerSismografoEnFueraDeServicio(Estado estadoFueraServicio, Map<MotivoTipo, String>comentariosPorMotivo) {
        sismografo.sismografoEnFueraDeServicio(estadoFueraServicio, comentariosPorMotivo);
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


    public Sismografo getSismografo() {
        return sismografo;
    }

     */

    public void setCodigoEstacion(int codigoEstacion) {
        this.codigoEstacion = codigoEstacion;
    }

    public String getDocumentoCertificacionAdq() {
        return documentoCertificacionAdq;
    }

    public void setDocumentoCertificacionAdq(String documentoCertificacionAdq) {
        this.documentoCertificacionAdq = documentoCertificacionAdq;
    }

    public LocalDateTime getFechaSolicitudCertificacion() {
        return fechaSolicitudCertificacion;
    }

    public void setFechaSolicitudCertificacion(LocalDateTime fechaSolicitudCertificacion) {
        this.fechaSolicitudCertificacion = fechaSolicitudCertificacion;
    }

    public Integer getLatitud() {
        return latitud;
    }

    public void setLatitud(Integer latitud) {
        this.latitud = latitud;
    }

    public Integer getLongitud() {
        return longitud;
    }

    public void setLongitud(Integer longitud) {
        this.longitud = longitud;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getNroCertificacionAdquisicion() {
        return nroCertificacionAdquisicion;
    }

    public void setNroCertificacionAdquisicion(Integer nroCertificacionAdquisicion) {
        this.nroCertificacionAdquisicion = nroCertificacionAdquisicion;
    }


}
