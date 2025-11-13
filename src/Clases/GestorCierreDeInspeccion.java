package Clases;

import Clases.Interfaces.IObservadorCierreOrdenInspeccion;
import Clases.Interfaces.ISujeto;

import java.time.LocalDateTime;
import java.util.*;

public class GestorCierreDeInspeccion implements ISujeto {
    private OrdenDeInspeccion ordenSeleccionada;
    private String observacionIngresada;
    private List<Empleado> empleados;
    private Sesion sesion;
    private Empleado empleadoLogueado;
    private List<OrdenDeInspeccion> ordenes;
    private List<OrdenDeInspeccion> ordenesFiltradas;
    private List<MotivoTipo> motivosTipo;
    private List<MotivoTipo> motivosSeleccionados;
    private Map<MotivoTipo, String> comentariosPorMotivo;
    private List<Estado> estados;
    private List<String> emailsResponsables = new ArrayList<>();
    private Estado estadoFueraServicio;
    private final PantallaCCRS pantallaCCRS_Norte = new PantallaCCRS();
    private final PantallaCCRS pantallaCCRS_Sur = new PantallaCCRS();
    private InterfazEmail interfazEmail;
    private List<Sismografo> sismografos;
    private final List<IObservadorCierreOrdenInspeccion> observadores = new java.util.concurrent.CopyOnWriteArrayList<>();
    private String identificadorSismografo;
    private String nombreEstado;
    private LocalDateTime fechaHora;
    private List<String> motivosYComentarios;


    public GestorCierreDeInspeccion(List<Empleado> empleados, Sesion sesion, List<OrdenDeInspeccion> ordenes, List<MotivoTipo> motivoTipos, List<Estado> estados, List<Sismografo> sismografos) {
        this.empleados = empleados;
        this.sesion = sesion;
        this.ordenes = ordenes;
        this.ordenesFiltradas = new ArrayList<>();
        this.motivosTipo = motivoTipos;
        this.motivosSeleccionados = new ArrayList<>();
        this.comentariosPorMotivo = new HashMap<>();
        this.estados = estados;
        this.sismografos = sismografos;
    }

    public void nuevoCierre() {
        buscarEmpleado();
    }


    public void buscarEmpleado() {
        empleadoLogueado = sesion.obtenerRILogueado();
    }


    public boolean buscarOrdenes() {
        ordenesFiltradas.clear();

        for (OrdenDeInspeccion orden : ordenes) {
            if (orden.esDeRI(empleadoLogueado) && orden.esCompletamenteRealizada()) {
                ordenesFiltradas.add(orden);
            }
        }
        return !ordenesFiltradas.isEmpty();
    }


    public List<OrdenDeInspeccion> ordenarPorFecha() {
        ordenesFiltradas.sort(Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion));
        return ordenesFiltradas;
    }


    public void tomarSeleccionOrden(int numeroOrden) {
        for (OrdenDeInspeccion orden : ordenesFiltradas) {
            if (orden.getNumeroOrden() == numeroOrden) {
                this.ordenSeleccionada = orden;
                break;
            }
        }
    }


    public void tomarObservacion(String observacion) {
        this.observacionIngresada = observacion;
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


    public void tomarConfirmacionCierre() {
        Estado estadoCerrada = buscarEstadoCierre();
        LocalDateTime fechaCierre = obtenerFechaHoraActual();
        this.ordenSeleccionada.cerrar(estadoCerrada, fechaCierre, observacionIngresada);

        ponerSismografoEnFueraDeServicio();

    }


    public boolean validarDatosCierre() {
        if (observacionIngresada == null || observacionIngresada.trim().isEmpty()) {
            return false;
        }

        if (motivosSeleccionados == null || motivosSeleccionados.isEmpty()) {
            return false;
        }
        return true;
    }


    public Estado buscarEstadoCierre() {
        for (Estado estado : estados) {
            if (estado != null && estado.esAmbitoOrdenDeInspeccion() && estado.esCerrada()) {
                return estado;
            }
        }
        return null;
    }


    public LocalDateTime obtenerFechaHoraActual() {
       return LocalDateTime.now();
    }


    public Estado obtenerEstadoFueraDeServicioSismografo() {
        for (Estado estado : estados) {
            if (estado != null && estado.esFueraDeServicio() && estado.esAmbitoSismografo()) {
                return estado;
            }
        }
        return null;
    }
    // --- Búsqueda por identificador (simple y segura) ---
    private Sismografo buscarSismografoPorIdentificador(String identificadorSismografo) {
        if (identificadorSismografo == null || identificadorSismografo.isBlank()) {
            throw new IllegalArgumentException("identificadorSismografo vacío o nulo");
        }
        for (Sismografo s : sismografos) {
            if (identificadorSismografo.equals(s.getIdentificadorSismografo())) {
                return s;
            }
        }
        throw new IllegalArgumentException("Sismógrafo no encontrado: " + identificadorSismografo);
    }

    public void ponerSismografoEnFueraDeServicio(){
        estadoFueraServicio = obtenerEstadoFueraDeServicioSismografo();
        identificadorSismografo = ordenSeleccionada.getIdentificadorSismografo();
        Sismografo s = this.buscarSismografoPorIdentificador(identificadorSismografo);
        s.sismografoEnFueraDeServicio(estadoFueraServicio, comentariosPorMotivo);
        this.obtenerResponsableDeReparacion();
    }


    public void obtenerResponsableDeReparacion() {
        for (Empleado empleado : empleados) {
            if (empleado.esResponsableDeReparacion()) {
                emailsResponsables.add(empleado.obtenerEmail());
            }
        }
        notificarCierre( );
    }


    public void finCU(){
        System.out.println("Fin Caso de uso con éxito.");
    }

    public void notificarCierre() {
        Sismografo s = this.buscarSismografoPorIdentificador(identificadorSismografo);
        CambioEstado nuevoCambio = s.obtenerUltimoCambioDeEstado();

        nombreEstado = nuevoCambio.getEstado().getNombre(); //nombreEstado
        fechaHora = nuevoCambio.getFechaHoraInicio(); //fechaHora

        motivosYComentarios = new ArrayList<>();//motivosYcomentarios
        for (MotivoFueraServicio motivo : nuevoCambio.getMotivosFueraDeServicio()) {
            String linea = motivo.getMotivoTipo().getDescripcion() + ": " + motivo.getComentario();
            motivosYComentarios.add(linea);
        }

        interfazEmail = new InterfazEmail();
        List<IObservadorCierreOrdenInspeccion> lista = new ArrayList<>();
        lista.add(interfazEmail);
        lista.add(pantallaCCRS_Sur);
        lista.add(pantallaCCRS_Norte);

        this.suscribir(lista);

        this.notificar();


        /*
        new Thread(() -> {
            InterfazEmail.notificarCierre(emailsResponsables, identificadorSismografo, nombreEstado, fechaHoraInicio, resumenMotivos);
        }).start();

        new Thread(() -> {
            PantallaCCRS.mostrarEnPantalla(identificadorSismografo, nombreEstado, fechaHoraInicio, resumenMotivos, "Pantalla CCRS - Sala Norte");
            PantallaCCRS.mostrarEnPantalla(identificadorSismografo, nombreEstado, fechaHoraInicio, resumenMotivos, "Pantalla CCRS - Sala Sur");
        }).start();
        */
        finCU();


    }


    public Estado obtenerEstadoEnLineaSismografo() {
        for (Estado estado : estados) {
            if (estado != null && estado.esEnLinea() && estado.esAmbitoSismografo()) {
                return estado;
            }
        }
        return null;
    }


    public void ponerSismografoEnLinea(){
        Estado estadoEnLinea = obtenerEstadoEnLineaSismografo();
        identificadorSismografo = ordenSeleccionada.getIdentificadorSismografo();
        Sismografo s = this.buscarSismografoPorIdentificador(identificadorSismografo);
        s.sismografoEnLinea(estadoEnLinea);
    }


    @Override
    public void suscribir(List<IObservadorCierreOrdenInspeccion> o) {
        if (o == null) return;
        for (IObservadorCierreOrdenInspeccion obs : o) {
            if (obs != null && !observadores.contains(obs)) {
                observadores.add(obs);
            }
        }

    }

    @Override
    public void quitar(List<IObservadorCierreOrdenInspeccion> o) {
        if (o == null) return;
        observadores.removeAll(o);

    }

    @Override
    public void notificar() {
        interfazEmail.actualizar(identificadorSismografo, nombreEstado, fechaHora, motivosYComentarios, null, emailsResponsables);
        pantallaCCRS_Norte.actualizar(identificadorSismografo, nombreEstado, fechaHora, motivosYComentarios, "Pantalla CCRS - Sala Norte", emailsResponsables);
        pantallaCCRS_Sur.actualizar(identificadorSismografo, nombreEstado, fechaHora, motivosYComentarios, "Pantalla CCRS - Sala Sur", emailsResponsables);
    }
}
