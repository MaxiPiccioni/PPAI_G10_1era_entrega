package Clases;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GestorCierreDeInspeccion {
    private OrdenDeInspeccion ordenSeleccionada;
    private OrdenDeInspeccion observacionIngresada;
    private List<Empleado> empleados;
    private Sesion sesion;
    private Empleado empleadoLogueado;
    private List<OrdenDeInspeccion> ordenes;
    private List<OrdenDeInspeccion> ordenesFiltradas;
    private List<MotivoTipo> motivosTipo;

    public GestorCierreDeInspeccion(List<Empleado> empleados, Sesion sesion, List<OrdenDeInspeccion> ordenes) {
        this.empleados = empleados;
        this.sesion = sesion;
        this.ordenes = ordenes;
        this.ordenesFiltradas = new ArrayList<>();
        this.empleadoLogueado = buscarEmpleado();
        this.motivosTipo = new ArrayList<>();
    }

    public Empleado buscarEmpleado() {
        Usuario usuarioLogueado = sesion.obtenerRILogueado();
        if (usuarioLogueado != null) {
            empleadoLogueado = usuarioLogueado.obtenerEmpleado();
        }
        return empleadoLogueado;
    }

    // Buscar y guardar órdenes que correspondan al RI logueado
    public void buscarOrdenes() {
        ordenesFiltradas.clear();
        if (empleadoLogueado == null) {
            return;
        }
        for (OrdenDeInspeccion orden : ordenes) {
            if (orden.esDeRI(empleadoLogueado) && orden.esCompletamenteRealizada()) {
                ordenesFiltradas.add(orden);
            }
        }
        if (ordenesFiltradas.isEmpty()) { //AGREGAR A LA PANTALLA ESA VERIFICACION.
            System.out.println("No se encontraron órdenes completamente realizadas para el Responsable de Inspección logueado.");
        }
    }

    public List<OrdenDeInspeccion> ordenarPorFecha() {
        ordenesFiltradas.sort(Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion));
        return ordenesFiltradas;
    }

    public void tomarSeleccionOrden(int numeroOrden) {
        for (OrdenDeInspeccion orden : this.ordenarPorFecha()) {
            if (orden.getNumeroOrden() == numeroOrden) {
                this.ordenSeleccionada = orden;
                break;
            }
        }
    }

    public void tomarObservacion(String observacion) {
        ordenSeleccionada.setObservacion(observacion);
    }



}
