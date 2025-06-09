package Clases;

import java.util.ArrayList;
import java.util.List;

public class GestorCierreDeInspeccion {
    private OrdenDeInspeccion ordenSeleccionada;
    private List<Empleado> empleados;
    private Sesion sesion;

    public GestorCierreDeInspeccion(List<Empleado> empleados) {
        this.empleados = empleados;
    }


    public List<Empleado> buscarEmpleado() {
        List<Empleado> empleadosRI = new ArrayList<>();
        for (Empleado empleado : empleados) {
            if (empleado.getRol().getNombreRol().equals("Responsable de Inspeccion")) {
                empleadosRI.add(empleado);
            }
        }
        return empleadosRI;
    }


}
