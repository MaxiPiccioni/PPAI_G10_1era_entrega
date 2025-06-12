package Clases;

import java.util.List;

public class Perfil {
    private String descripcion;
    private String nombre;
    private List<Permiso> permisos;


    public Perfil(String descripcion, String nombre, List<Permiso> permisos) {
        this.descripcion = descripcion;
        this.nombre = nombre;
        this.permisos = permisos;
    }


    public String getDescripcion() {
        return descripcion;
    }


    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public List<Permiso> getPermisos() {
        return permisos;
    }


    public void setPermisos(List<Permiso> permisos) {
        this.permisos = permisos;
    }
}
