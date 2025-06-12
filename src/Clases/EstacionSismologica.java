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
    private Sismografo sismografo;
    private List<CambioEstado> cambiosEstado;


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

    public List<CambioEstado> getCambiosEstado() {
        return cambiosEstado;
    }

    public void setCambiosEstado(List<CambioEstado> cambiosEstado) {
        this.cambiosEstado = cambiosEstado;
    }

    public void ponerSismografoEnLinea(Estado estadoEnLinea) {
        sismografo.sismografoEnLinea(estadoEnLinea);
    }
}
