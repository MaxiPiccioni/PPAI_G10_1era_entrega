package Clases;

// REVISAR ESTA CLASE
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


    public boolean esResponsableDeReparacion() {
        return rol != null && rol.esResponsableDeReparacion();
    }


    public String obtenerEmail() {
        return this.email;
    }


    public Rol getRol() {
        return rol;
    }


    public String getApellido() {
        return apellido;
    }


    public void setApellido(String apellido) {
        this.apellido = apellido;
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getTelefono() {
        return telefono;
    }


    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public void setRol(Rol rol) {
        this.rol = rol;
    }

}
