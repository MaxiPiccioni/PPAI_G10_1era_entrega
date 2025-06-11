import Clases.*;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Crear roles
        Rol rolRI = new Rol("Responsable de Inspección");
        Rol rolRR = new Rol("Responsable de Reparación");

        // Crear empleados
        Empleado empleado1 = new Empleado("Juan", "Pérez", "3513123430", "juan@frcsistemas.com", rolRI);
        Empleado empleado2 = new Empleado("Ana", "Gómez", "35431242443", "ana@frcsistemas.com", rolRI);
        Empleado empleado3 = new Empleado("Luis", "López", "3513489767", "luis@frcsistemas.com", rolRR);
        Empleado empleado4 = new Empleado("Jorge", "Gomez", "2644575621", "jorge@frcsistemas.com", rolRR);

        List<Empleado> empleados = Arrays.asList(empleado1, empleado2, empleado3, empleado4);

        // Usuario logueado.
        Usuario usuarioLogueado = new Usuario("juan123", "clave123", empleado1);

        // Sesión activa.
        Sesion sesionActiva = new Sesion(usuarioLogueado);

        // Crear estado: Estados de órdenes.
        Estado completamenteRealizada = new Estado("Orden de Inspección", "Completamente Realizada");
        Estado pendiente = new Estado("Orden de Inspección", "Pendiente de Realizacion");
        Estado cerrada = new Estado("Orden de Inspección", "Cerrada");

        // Crear estados Sismografo.
        Estado fueraServicio = new Estado("Sismografo", "Fuera De Servicio");
        Estado enLinea = new Estado("Sismografo", "En Linea");
        Estado fueraDeLinea = new Estado("Sismografo", "Fuera De Linea");
        List<Estado> estados = Arrays.asList(completamenteRealizada, pendiente, cerrada, fueraServicio, enLinea, fueraDeLinea);

        // Crear cambios de estados
        CambioEstado cambioEstadoCompletamenteRealizada = new CambioEstado(LocalDateTime.now(), null, completamenteRealizada);
        CambioEstado cambioEstadoPendiente = new CambioEstado(LocalDateTime.now(), null, pendiente);
        CambioEstado cambioEstadoCerrada = new CambioEstado(LocalDateTime.now(), null, cerrada);
        CambioEstado cambioEstadoFueraServicio = new CambioEstado(LocalDateTime.now(), null, fueraServicio);
        CambioEstado cambioEstadoEnLinea = new CambioEstado(LocalDateTime.now(), null, enLinea);
        CambioEstado cambioEstadoFueraDeLinea = new CambioEstado(LocalDateTime.now(), null, fueraDeLinea);

        List<CambioEstado> cambiosEstados = Arrays.asList(cambioEstadoCompletamenteRealizada, cambioEstadoPendiente, cambioEstadoCerrada, cambioEstadoFueraServicio, cambioEstadoEnLinea, cambioEstadoFueraDeLinea);

        // Crear estaciones.
        EstacionSismologica estacion1 = new EstacionSismologica(101, "Estación Centro");
        EstacionSismologica estacion2 = new EstacionSismologica(102, "Estación Norte");
        EstacionSismologica estacion3 = new EstacionSismologica(103, "Estación Sur");

        // Crear sismógrafos y asociarlos a estaciones.
        Sismografo sismografo1 = new Sismografo(LocalDate.of(2023,1,15), "SIS-001", "SN1001", estacion1, cambiosEstados);
        Sismografo sismografo2 = new Sismografo(LocalDate.of(2024,3,10), "SIS-002", "SN1002", estacion2, cambiosEstados);
        Sismografo sismografo3 = new Sismografo(LocalDate.of(2022,6,5), "SIS-003", "SN1003", estacion3, cambiosEstados);

        // Asignar sismógrafos a estaciones.
        estacion1.setSismografo(sismografo1);
        estacion2.setSismografo(sismografo2);
        estacion3.setSismografo(sismografo3);

        // Crear órdenes de inspección asociando estación sismológica.
        OrdenDeInspeccion orden1 = new OrdenDeInspeccion(
                LocalDateTime.of(2025, 6, 4, 10, 0),
                LocalDateTime.of(2025, 6, 4, 9, 0),
                1, "", empleado1, estacion1);
        orden1.setEstado(completamenteRealizada);

        OrdenDeInspeccion orden2 = new OrdenDeInspeccion(
                LocalDateTime.of(2025, 6, 2, 11, 0),
                LocalDateTime.of(2025, 6, 2, 10, 0),
                2, "", empleado2, estacion2);
        orden2.setEstado(pendiente);

        OrdenDeInspeccion orden3 = new OrdenDeInspeccion(
                LocalDateTime.of(2025, 6, 3, 12, 0),
                LocalDateTime.of(2025, 6, 3, 11, 0),
                3, "", empleado1, estacion3);
        orden3.setEstado(completamenteRealizada);

        List<OrdenDeInspeccion> ordenes = Arrays.asList(orden1, orden2, orden3);

        // Tipos de motivo para Fuera de Servicio.
        List<MotivoTipo> motivoTipos = new ArrayList<>();
        motivoTipos.add(new MotivoTipo("Mantenimiento"));
        motivoTipos.add(new MotivoTipo("Falla técnica"));
        motivoTipos.add(new MotivoTipo("Inspección periódica"));
        motivoTipos.add(new MotivoTipo("Actualización de software"));

        // Crear el gestor
        GestorCierreDeInspeccion gestor = new GestorCierreDeInspeccion(empleados, sesionActiva, ordenes, motivoTipos, estados);

        gestor.buscarOrdenes();
        gestor.ordenarPorFecha();

        // GUI
        SwingUtilities.invokeLater(() -> {
            PantallaCierreOrdenInspeccion pantalla = new PantallaCierreOrdenInspeccion(gestor);
            pantalla.setVisible(true);
        });
    }

}
