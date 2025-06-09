package Clases;

public class Usuario {
    private String nombreUsuario;
    private String contraseña;
    private Empleado empleado;

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

}
