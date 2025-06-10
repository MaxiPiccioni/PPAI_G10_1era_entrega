package Clases;

import java.time.LocalDateTime;
import java.util.*;

public class GestorCierreDeInspeccion {
    private OrdenDeInspeccion ordenSeleccionada;
    private OrdenDeInspeccion observacionIngresada;
    private List<Empleado> empleados;
    private Sesion sesion;
    private Empleado empleadoLogueado;
    private List<OrdenDeInspeccion> ordenes;
    private List<OrdenDeInspeccion> ordenesFiltradas;
    private List<MotivoTipo> motivosTipo;
    private List<MotivoTipo> motivosSeleccionados;
    private Map<MotivoTipo, String> comentariosPorMotivo;
    private boolean confirmacionCierre;
    private Estado estadoCierre;


    public GestorCierreDeInspeccion(List<Empleado> empleados, Sesion sesion, List<OrdenDeInspeccion> ordenes, List<MotivoTipo> motivoTipos ) {
        this.empleados = empleados;
        this.sesion = sesion;
        this.ordenes = ordenes;
        this.ordenesFiltradas = new ArrayList<>();
        this.empleadoLogueado = buscarEmpleado();
        this.motivosTipo = motivoTipos;
        this.motivosSeleccionados = new ArrayList<>();
        this.comentariosPorMotivo = new HashMap<>();
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

    public List<String> buscarTiposMotivo(){
        List<String> descripciones = new ArrayList<>();
        for (MotivoTipo motivo : motivosTipo) {
            descripciones.add(motivo.getDescripcion());
        }
        return descripciones;
    }

    public void tomarTipo(String descripcion) {
        for (MotivoTipo motivo : motivosTipo) {
            if (motivo.getDescripcion().equals(descripcion)) {
                motivosSeleccionados.add(motivo);
                break;
            }
        }
    }

    public void tomarIngresoComentario(String descripcion, String comentario) {
        for (MotivoTipo motivo : motivosSeleccionados) {
            if (motivo.getDescripcion().equals(descripcion)) {
                comentariosPorMotivo.put(motivo, comentario);
                break;
            }
        }
    }

    public void tomarConfirmacionCierre(boolean confirmacion) {
        this.confirmacionCierre = confirmacion;
    }

    public boolean validarDatosCierre() {
        String observacion = ordenSeleccionada.observacionCierre;
        if (observacion == null || observacion.trim().isEmpty()) {
            return false;
        }

        if (motivosSeleccionados == null || motivosSeleccionados.isEmpty()) {
            return false;
        }
        return true;
    }

    public void buscarEstadoCierre() {
       /* boolean encontroAlguna = false;

        for (OrdenDeInspeccion orden : ordenes) {
            Estado estado = orden.getEstado();
            if (estado != null && estado.esAmbitoOrdenDeInspeccion() && estado.esCerrada()) {
                OrdenDeInspeccion ordenACerrar = orden;
            }
        }
        obtenerFechaHoraActual(ordenACerrar);

    }
    public LocalDateTime obtenerFechaHoraActual(OrdenDeInspeccion ordenACerrar) {
       ordenACerrar.cerrar(LocalDateTime.now();
*/
    }
}
