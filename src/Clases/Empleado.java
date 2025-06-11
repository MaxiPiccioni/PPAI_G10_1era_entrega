package Clases;

public class Empleado {
    private String apellido;
    private String nombre;
    private String telefono;
    private String email;
    private Rol rol;

    public Empleado(String nombre, String apellido, String telefono, String email, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public Rol getRol() { // INCONSISTENCIA!!!!!
        return rol;
    }

    public boolean esResponsableDeReparacion() {
        return rol != null && rol.esResponsableDeReparacion();
    }

    public String obtenerEmail() {
        return this.email;
    }



}
