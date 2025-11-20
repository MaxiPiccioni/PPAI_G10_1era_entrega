import Clases.*;
import infra.db.*;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1) Inicializar DB (crea ccrs.db y tablas si no existen)
        SQLite.init();

        // 2) Seed de Roles (ANTES de crear el Gestor u otros objetos que los usen)

        
        Rol rolRI = new Rol("Responsable de Inspección");
        Rol rolRR = new Rol("Responsable de Reparación");
        RolDao rolDao = new RolDao();
        int idRI = rolDao.getOrCreate(rolRI.getNombreRol(), rolRI.getDescripcionRol());
        int idRR = rolDao.getOrCreate(rolRR.getNombreRol(), rolRR.getDescripcionRol());



        EmpleadoDao empDao = new EmpleadoDao();

// Tu hardcode actual
        Empleado empleado1 = new Empleado("Matias", "Sanchez", "3513123430", "matiassanchez0762@gmail.com", rolRI);
        Empleado empleado2 = new Empleado("Ignacio", "Linzoain", "35431242443", "ignalinzoain@gmail.com", rolRI);
        Empleado empleado3 = new Empleado("Salvador", "Barbera", "3513489767", "mati@gmail.com", rolRR);
        //Empleado empleado3 = new Empleado("Salvador", "Barbera", "3513489767", "jsbarbera4@gmail.com", rolRR);
        //Empleado empleado4 = new Empleado("Maximo", "Piccioni", "2644575621", "ezeizaguirre02@gmail.com", rolRR);
        Empleado empleado4 = new Empleado("Maximo", "Piccioni", "2644575621", "maxipiccioni@gmail.com", rolRR);

// Seed en DB (mail como clave natural)
        int idEmp1 = empDao.getOrCreateByMail(
                empleado1.getNombre(),
                empleado1.getApellido(),
                empleado1.getTelefono(),
                empleado1.obtenerEmail(),
                idRI
        );
        int idEmp2 = empDao.getOrCreateByMail(
                empleado2.getNombre(),
                empleado2.getApellido(),
                empleado2.getTelefono(),
                empleado2.obtenerEmail(),
                idRI
        );
        int idEmp3 = empDao.getOrCreateByMail(
                empleado3.getNombre(),
                empleado3.getApellido(),
                empleado3.getTelefono(),
                empleado3.obtenerEmail(),
                idRR
        );
        int idEmp4 = empDao.getOrCreateByMail(
                empleado4.getNombre(),
                empleado4.getApellido(),
                empleado4.getTelefono(),
                empleado4.obtenerEmail(),
                idRR
        );
        // Si tu clase Empleado tiene setIdEmpleado(int), puedes asignar el id:
        // empleado1.setIdEmpleado(idEmp1);
        // empleado2.setIdEmpleado(idEmp2);
        // empleado3.setIdEmpleado(idEmp3);
        // empleado4.setIdEmpleado(idEmp4);
        // Crear empleados

        List<Empleado> empleados = Arrays.asList(empleado1, empleado2, empleado3, empleado4);


        // Usuario logueado.
        Usuario usuarioLogueado = new Usuario("juan123", "clave123", empleado1);
        UsuarioDao usuarioDao = new UsuarioDao();
        int idUsuario = usuarioDao.getOrCreate(
                usuarioLogueado.getNombreUsuario(),
                usuarioLogueado.getContraseña(),
                idEmp1,
                null // idPerfil, si no lo usás aún
        );
        // Si tu clase Usuario tiene setIdUsuario(int), puedes asignar el id:
        // usuarioLogueado.setIdUsuario(idUsuario);


        // Sesión activa.
        Sesion sesionActiva = new Sesion(usuarioLogueado);


        // 3) Persistir/obtener usuario
        // (opcional) si tu clase Usuario tiene setIdUsuario(int):
        // usuarioLogueado.setIdUsuario(idUsuario);
        // 4) Sesión activa (como tu hardcode)

        // 5) Persistir apertura de sesión (fin = NULL)
        SesionDao sesionDao = new SesionDao();
        Integer idSesionActiva = sesionDao.findSesionActivaId(idUsuario);
        if (idSesionActiva == null) {
            idSesionActiva = sesionDao.abrirSesion(idUsuario, java.time.LocalDateTime.now());
        }




        // Crear estado: Estados de órdenes.
        Estado completamenteRealizada = new Estado("Orden de Inspección", "Completamente Realizada");
        Estado pendiente = new Estado("Orden de Inspección", "Pendiente de Realizacion");
        Estado cerrada = new Estado("Orden de Inspección", "Cerrada");
        // Crear estados Sismografo.
        Estado fueraServicio = new Estado("Sismografo", "Fuera De Servicio");
        Estado enLinea = new Estado("Sismografo", "En Linea");
        Estado fueraDeLinea = new Estado("Sismografo", "Fuera De Linea");
        List<Estado> estados = Arrays.asList(completamenteRealizada, pendiente, cerrada, fueraServicio, enLinea, fueraDeLinea);

        // Persistencia (o lookup) en DB
        infra.db.EstadoDao estadoDao = new infra.db.EstadoDao();
        int idEstadoCR1 = estadoDao.getOrCreate(
                completamenteRealizada.getAmbito(),
                completamenteRealizada.getNombre()
        );
        int idEstadoCR2 = estadoDao.getOrCreate(
                pendiente.getAmbito(),
                pendiente.getNombre()
        );
        int idEstadoCR3 = estadoDao.getOrCreate(
                cerrada.getAmbito(),
                cerrada.getNombre()
        );
        int idEstadoCR4 = estadoDao.getOrCreate(
                fueraServicio.getAmbito(),
                fueraServicio.getNombre()
        );
        int idEstadoCR5 = estadoDao.getOrCreate(
                enLinea.getAmbito(),
                enLinea.getNombre()
        );
        int idEstadoCR6 = estadoDao.getOrCreate(
                fueraDeLinea.getAmbito(),
                fueraDeLinea.getNombre()
        );




        // Crear cambios de estados
            // Objeto de dominio (tu hardcode)
        CambioEstado cambioEstadoCompletamenteRealizada = new CambioEstado(java.time.LocalDateTime.now(), completamenteRealizada);
        CambioEstado cambioEstadoPendiente = new CambioEstado(LocalDateTime.now(), pendiente);
        CambioEstado cambioEstadoCerrada = new CambioEstado(LocalDateTime.now(), cerrada);
        CambioEstado cambioEstadoFueraServicio = new CambioEstado(LocalDateTime.now(), fueraServicio);
        CambioEstado cambioEstadoEnLinea = new CambioEstado(LocalDateTime.now(), enLinea);
        CambioEstado cambioEstadoFueraDeLinea = new CambioEstado(LocalDateTime.now(), fueraDeLinea);

        

            // si tu clase CambioEstado tiene setIdCambio(int):
            // cambioEstadoCompletamenteRealizada.setIdCambio(idCambio);

        List<CambioEstado> cambiosEstados = Arrays.asList(cambioEstadoCompletamenteRealizada, cambioEstadoPendiente, cambioEstadoCerrada, cambioEstadoFueraServicio, cambioEstadoEnLinea, cambioEstadoFueraDeLinea);


        // Crear estaciones.
        EstacionSismologica estacion1 = new EstacionSismologica(101, "Estación Centro");
        EstacionSismologica estacion2 = new EstacionSismologica(102, "Estación Norte");
        EstacionSismologica estacion3 = new EstacionSismologica(103, "Estación Sur");


        // Asignar sismógrafos a estaciones.
        String idSismografo1 = "SIS-001";
        String idSismografo2 = "SIS-002";
        String idSismografo3 = "SIS-003";
        estacion1.setIdentificadorSismografo(idSismografo1);
        estacion2.setIdentificadorSismografo(idSismografo2);
        estacion3.setIdentificadorSismografo(idSismografo3);
        
        // Persistir estaciones en la base de datos
        EstacionSismologicaDao estacionDao = new EstacionSismologicaDao();
        int idEstacion1 = estacionDao.getOrCreate(
                estacion1.getCodigoEstacion(),
                estacion1.getDocumentoCertificacionAdq(),
                estacion1.getFechaSolicitudCertificacion(),
                estacion1.getLatitud(),
                estacion1.getLongitud(),
                estacion1.getNombre(),
                estacion1.getNroCertificacionAdquisicion(),
                estacion1.getIdentificadorSismografo()
        );
        int idEstacion2 = estacionDao.getOrCreate(
                estacion2.getCodigoEstacion(),
                estacion2.getDocumentoCertificacionAdq(),
                estacion2.getFechaSolicitudCertificacion(),
                estacion2.getLatitud(),
                estacion2.getLongitud(),
                estacion2.getNombre(),
                estacion2.getNroCertificacionAdquisicion(),
                estacion2.getIdentificadorSismografo()
        );
        int idEstacion3 = estacionDao.getOrCreate(
                estacion3.getCodigoEstacion(),
                estacion3.getDocumentoCertificacionAdq(),
                estacion3.getFechaSolicitudCertificacion(),
                estacion3.getLatitud(),
                estacion3.getLongitud(),
                estacion3.getNombre(),
                estacion3.getNroCertificacionAdquisicion(),
                estacion3.getIdentificadorSismografo()
        );


        // Crear sismógrafos y asociarlos a estaciones.
        // Crear listas de cambios independientes (clonar los objetos CambioEstado)
        List<CambioEstado> cambios1 = new ArrayList<>();
        List<CambioEstado> cambios2 = new ArrayList<>();
        List<CambioEstado> cambios3 = new ArrayList<>();
        for (CambioEstado c : cambiosEstados) {
            cambios1.add(new CambioEstado(c.getFechaHoraInicio(), c.getEstado()));
            cambios2.add(new CambioEstado(c.getFechaHoraInicio(), c.getEstado()));
            cambios3.add(new CambioEstado(c.getFechaHoraInicio(), c.getEstado()));
        }

        Sismografo sismografo1 = new Sismografo(LocalDate.of(2023,1,15), idSismografo1, "SN1001", estacion1, cambios1, fueraDeLinea);
        Sismografo sismografo2 = new Sismografo(LocalDate.of(2024,3,10), idSismografo2, "SN1002", estacion2, cambios2, fueraDeLinea);
        Sismografo sismografo3 = new Sismografo(LocalDate.of(2022,6,5), idSismografo3, "SN1003", estacion3, cambios3, fueraDeLinea);

        List<Sismografo> sismografos = new ArrayList<>();
        sismografos.add(sismografo1);
        sismografos.add(sismografo2);
        sismografos.add(sismografo3);
        // Persistir historial de cambios de estado para cada sismógrafo

        SismografoDao sismografoDao = new SismografoDao();

        CambioEstadoDao cambioDao = new CambioEstadoDao();

        for (Sismografo sismografo : sismografos) {
            int idSismografo = sismografoDao.getOrCreate(
                sismografo.getIdentificadorSismografo(),
                sismografo.getFechaAdquisicion(),
                sismografo.getNroSerie(),
                sismografo.getEstacionSismologica().getCodigoEstacion(),
                idEstadoCR6 // o el idEstado que corresponda como estado actual
            );
            for (CambioEstado cambio : sismografo.getCambiosEstado()) {
                int idEstadoCambio = estadoDao.getIdByNombre(cambio.getEstado().getNombre());
                try {
                    int idCambio = cambioDao.abrir(
                        cambio.getFechaHoraInicio(),
                        idEstadoCambio,
                        null, // idMotivo si corresponde
                        idEmp1, // o el empleado correspondiente
                        sismografo.getIdentificadorSismografo() // identificador textual
                    );
                } catch (Exception ex) {
                    System.err.println("[Main] Error persistiendo CambioEstado para sismografoIdentificador=" + sismografo.getIdentificadorSismografo() + ": " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }


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

        // Persistir órdenes de inspección en la base de datos
        OrdenDeInspeccionDao ordenDao = new OrdenDeInspeccionDao();
        for (OrdenDeInspeccion orden : ordenes) {
            int idEstado = estadoDao.getIdByNombre(orden.getEstado().getNombre()); // Debes implementar este método en EstadoDao
            ordenDao.getOrCreate(
                orden.getNumeroOrden(),
                orden.getFechaHoraInicio(),
                orden.getFechaHoraFinalizacion(),
                orden.getEmpleado().obtenerEmail(),
                orden.getEstacion().getCodigoEstacion(),
                idEstado
            );
        }


        // Tipos de motivo para Fuera de Servicio.
        List<MotivoTipo> motivoTipos = new ArrayList<>();
        motivoTipos.add(new MotivoTipo("Mantenimiento"));
        motivoTipos.add(new MotivoTipo("Falla técnica"));
        motivoTipos.add(new MotivoTipo("Inspección periódica"));
        motivoTipos.add(new MotivoTipo("Actualización de software"));

        // Persistir motivos tipo en la base de datos
        MotivoTipoDao motivoTipoDao = new MotivoTipoDao();
        for (MotivoTipo motivo : motivoTipos) {
            motivoTipoDao.getOrCreate(motivo.getDescripcion());
        }

        // Crear el gestor
        GestorCierreDeInspeccion gestor = new GestorCierreDeInspeccion(empleados);


        // GUI
        SwingUtilities.invokeLater(() -> {
            PantallaCierreOrdenInspeccion pantalla = new PantallaCierreOrdenInspeccion(gestor);
            pantalla.seleccionarCierreOrden();
            pantalla.setVisible(true);
        });
    }
}
