package Clases;

import java.util.List;

public class Usuario {
    private String nombreUsuario;
    private String contraseña;
    private Empleado empleado;
    private List<Perfil> perfiles;


    public Usuario(String nombreUsuario, String contraseña, Empleado empleado) {
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.empleado = empleado;
    }


    public Empleado obtenerEmpleado() {
        return empleado;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }


    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }


    public String getContraseña() {
        return contraseña;
    }


    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }


    public Empleado getEmpleado() {
        return empleado;
    }


    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }


    public List<Perfil> getPerfiles() {
        return perfiles;
    }


    public void setPerfiles(List<Perfil> perfiles) {
        this.perfiles = perfiles;
    }
}
