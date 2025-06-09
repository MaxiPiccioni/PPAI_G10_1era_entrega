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

}
