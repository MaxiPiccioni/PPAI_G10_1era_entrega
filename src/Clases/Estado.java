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

    public String getNombreEstado() {
        return nombreEstado;
    }

    public String getAmbito() {
        return ambito;
    }

    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public boolean esAmbito(String unAmbito) {
        return ambito.equals(unAmbito);
    }

}
