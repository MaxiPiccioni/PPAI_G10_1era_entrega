package Clases;

public class Rol {
    private String descripcionRol;
    private String nombre;


    public Rol(String nombre) {
        this.nombre = nombre;
        this.descripcionRol = null;
    }


    public String getNombreRol() {
        return nombre;
    }


    public boolean esResponsableDeReparacion() {
        return this.nombre.equalsIgnoreCase("Responsable de Reparación");
    }


    public String getDescripcionRol() {
        return descripcionRol;
    }


    public void setDescripcionRol(String descripcionRol) {
        this.descripcionRol = descripcionRol;
    }


    public void setNombreRol(String nombre) {
        this.nombre = nombre;
    }
}
