package Clases;

public class Estado {
    private String ambito;
    private String nombreEstado;

    public Estado(String ambito, String nombreEstado) {
        this.ambito = ambito;
        this.nombreEstado = nombreEstado;
    }


    public boolean esCompletamenteRealizada() {
        return this.nombreEstado.equalsIgnoreCase("Completamente realizada") && this.ambito.equals("Orden de Inspección");
    }


    public boolean esAmbitoOrdenDeInspeccion() {
        return this.ambito.equals("Orden de Inspección");
    }


    public boolean esCerrada() {
        return nombreEstado.equals("Cerrada");
    }


    public boolean esAmbitoSismografo() {
        return this.ambito.equals("Sismografo");
    }


    public boolean esFueraDeServicio() {
        return nombreEstado.equals("Fuera De Servicio");
    }


    public boolean esEnLinea() {
        return nombreEstado.equals("En Linea");
    }


    public String getNombre() {
        return nombreEstado;
    }


    public String getAmbito() {
        return ambito;
    }


    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }


    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }


    public String getNombreEstado() {
        return nombreEstado;
    }
}
