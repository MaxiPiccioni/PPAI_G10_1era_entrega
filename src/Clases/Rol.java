package Clases;

public class Rol {
    private String descripcionRol;
    private String nombre;

    public Rol(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreRol() {
        return nombre;
    }

    public boolean esResponsableDeReparacion() {
        return this.nombre.equalsIgnoreCase("Responsable de Reparación");
    }

}
