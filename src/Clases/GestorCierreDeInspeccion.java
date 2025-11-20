package Clases;

import Clases.Interfaces.IObservadorCierreOrdenInspeccion;
import Clases.Interfaces.ISujeto;
import infra.db.EmpleadoDao;
import infra.db.SesionDao;
import infra.db.OrdenDeInspeccionDao;
import infra.db.MotivoTipoDao; // <-- agregado
import infra.db.SQLite; // para fallback SQL

import java.time.LocalDateTime;
import java.util.*;
import java.sql.*; // para fallback SQL

public class GestorCierreDeInspeccion implements ISujeto {
    private OrdenDeInspeccion ordenSeleccionada;
    private String observacionIngresada;
    private List<Empleado> empleados;
    private SesionDao sesionDao = new SesionDao();
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
    private final List<IObservadorCierreOrdenInspeccion> observadores = new java.util.concurrent.CopyOnWriteArrayList<>();
    private String identificadorSismografo;
    private String nombreEstado;
    private LocalDateTime fechaHora;
    private List<String> motivosYComentarios;


    // constructor existente (con Sesion) -- sin cambios

    // Nuevo constructor: no recibe ids ni Sesion. Recupera la última sesión activa desde la BD y la reconstruye.
    public GestorCierreDeInspeccion() {


        this.ordenesFiltradas = new ArrayList<>();
        this.motivosSeleccionados = new ArrayList<>();
        this.comentariosPorMotivo = new HashMap<>();
        // cargar y fijar estados desde BD al construir el gestor
        cargarEstadosDesdeBD();
        // garantizar que estados no quede null
        if (this.estados == null) this.estados = new ArrayList<>();
    }

    public void nuevoCierre() {
        buscarEmpleado();
    }


    public void buscarEmpleado() {
        // Usar la instancia sesionDao del gestor para traer la última sesión activa
        try {
            SesionDao.SesionDTO dto = sesionDao.findUltimaSesionActiva();
            if (dto != null) {
                infra.db.UsuarioDao usuarioDao = new infra.db.UsuarioDao();
                Usuario usuario = null;
                // Intentar reconstruir Usuario a partir del idUsuario de la sesión
                try {
                    usuario = usuarioDao.findById(dto.getIdUsuario()); // método preferido
                } catch (NoSuchMethodError | RuntimeException ex) {
                    // fallback si la DAO tiene otro nombre de método
                    try { usuario = usuarioDao.getById(dto.getIdUsuario()); } catch (Exception ignore) { usuario = null; }
                }

                if (usuario != null) {
                    this.sesion = new Sesion(usuario);
                    // opcional: asignar idSesion en el objeto Sesion si existe setter
                    // if (this.sesion instanceof SomeSesionWithId) ((SomeSesionWithId)this.sesion).setId(dto.getIdSesion());
                } else {
                    this.sesion = null; // no se pudo reconstruir el usuario
                }
            } else {
                this.sesion = null; // no hay sesión activa en BD
            }
        } catch (Exception e) {
            this.sesion = null; // en caso de error con BD dejamos la sesión en null
        }

        // Protegemos contra NPE si no hay sesión.
        if (this.sesion == null) {
            this.empleadoLogueado = null;
            return;
        }
        this.empleadoLogueado = sesion.obtenerRILogueado();
        if (empleadoLogueado != null) {
        }
    }


    public boolean buscarOrdenes() {
        ordenesFiltradas.clear();

        // Intentar cargar órdenes desde la BD mediante reflexión (evita error de compilación
        // si OrdenDeInspeccionDao no tiene métodos con nombre fijo).
        try {
            OrdenDeInspeccionDao ordenDao = new OrdenDeInspeccionDao();
            List<OrdenDeInspeccion> ordenesBD = null;

            // probar findAll()
            try {
                java.lang.reflect.Method m = ordenDao.getClass().getMethod("findAll");
                Object res = m.invoke(ordenDao);
                if (res instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<OrdenDeInspeccion> tmp = (List<OrdenDeInspeccion>) res;
                    ordenesBD = tmp;
                }
            } catch (NoSuchMethodException ignore) {
                // método no existe -> probar getAll()
            }

            // si no se obtuvo, probar getAll()
            if (ordenesBD == null) {
                try {
                    java.lang.reflect.Method m2 = ordenDao.getClass().getMethod("getAll");
                    Object res2 = m2.invoke(ordenDao);
                    if (res2 instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<OrdenDeInspeccion> tmp2 = (List<OrdenDeInspeccion>) res2;
                        ordenesBD = tmp2;
                    }
                } catch (NoSuchMethodException ignore) {
                    // no existe -> no cargamos desde BD
                }
            }

            // si obtuvimos órdenes desde BD, reemplazamos la lista local
            if (ordenesBD != null) {
                this.ordenes = ordenesBD;
            }
        } catch (Exception e) {
            // si falla el acceso a BD, seguimos con la lista en memoria (no propagamos)
        }

        if (this.ordenes == null){
        };

        for (OrdenDeInspeccion orden : ordenes) {
            // si empleadoLogueado es null, no podemos filtrar por RI -> retornamos false

            if (empleadoLogueado == null) {

                break;
            }
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


    // Reemplazar el método para obtener tipos de motivo desde la BD en lugar de depender solo de la lista pasada por constructor.
    public List<String> buscarTiposMotivo(){
		List<String> descripciones = new ArrayList<>();

		// 1) Intentar usando el DAO y sus métodos comunes
		try {
			MotivoTipoDao dao = new MotivoTipoDao();

			// 1.a) intentar método directo que devuelva descripciones (si existe)
			try {
				java.lang.reflect.Method mDescList = dao.getClass().getMethod("findAllDescriptions");
				Object res = mDescList.invoke(dao);
				if (res instanceof List) {
					@SuppressWarnings("unchecked")
					List<String> lista = (List<String>) res;
					for (String s : lista) {
						if (s != null) {
							descripciones.add(s);
						}
					}
				}
			} catch (NoSuchMethodException ignore) {
				// no existe, continuar
			}

			// 1.b) si no obtuvimos nada, intentar findAll()/getAll() y mapear
			if (descripciones.isEmpty()) {
				List<?> lista = null;
				try {
					java.lang.reflect.Method m = dao.getClass().getMethod("findAll");
					Object res = m.invoke(dao);
					if (res instanceof List) lista = (List<?>) res;
				} catch (NoSuchMethodException | IllegalArgumentException ignored) { }

				if (lista == null) {
					try {
						java.lang.reflect.Method m2 = dao.getClass().getMethod("getAll");
						Object res2 = m2.invoke(dao);
						if (res2 instanceof List) lista = (List<?>) res2;
					} catch (NoSuchMethodException | IllegalArgumentException ignored) { }
				}

				if (lista != null) {
					this.motivosTipo = new ArrayList<>();
					for (Object o : lista) {
						if (o == null) continue;
						// si ya es MotivoTipo
						if (o instanceof MotivoTipo) {
							MotivoTipo mt = (MotivoTipo) o;
							this.motivosTipo.add(mt);
							descripciones.add(mt.getDescripcion());
							continue;
						}
						// si es DTO o tiene getDescripcion()
						try {
							java.lang.reflect.Method gm = o.getClass().getMethod("getDescripcion");
							Object val = gm.invoke(o);
							if (val != null) {
								String s = val.toString();
								this.motivosTipo.add(new MotivoTipo(s));
								descripciones.add(s);
								continue;
							}
						} catch (Exception ignored) { }
						// si viene como String
						if (o instanceof String) {
							String s = (String) o;
							this.motivosTipo.add(new MotivoTipo(s));
							descripciones.add(s);
						}
					}
					if (!descripciones.isEmpty()) return descripciones;
				}
			}
		} catch (Exception e) {
			// ignore y pasar a fallback SQL
		}

		// 2) Fallback directo por SQL sobre la tabla MotivoTipo
		try (Connection c = SQLite.get();
			 PreparedStatement ps = c.prepareStatement("SELECT descripcion FROM MotivoTipo ORDER BY idMotivoTipo");
			 ResultSet rs = ps.executeQuery()) {

			this.motivosTipo = new ArrayList<>();
			while (rs.next()) {
				String s = rs.getString("descripcion");
				if (s != null) {
					descripciones.add(s);
					this.motivosTipo.add(new MotivoTipo(s));
				}
			}
			if (!descripciones.isEmpty()) return descripciones;
		} catch (Exception ignored) {
			// si también falla, caemos al fallback en memoria
		}

		// 3) Fallback: usar la lista que tenga el gestor (si existe)
		if (motivosTipo == null) return descripciones;
		for (MotivoTipo motivo : motivosTipo) {
			if (motivo != null) descripciones.add(motivo.getDescripcion());
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


    // Reemplazado: ahora delega en la lista cargada por cargarEstadosDesdeBD()
    public Estado buscarEstadoCierre() {
        for (Estado estado : this.estados) {
                if (estado != null && estado.esAmbitoOrdenDeInspeccion() && estado.esCerrada()) {
                    return estado;
                }
            }


        return null;
    }

    // Nuevo: carga todos los estados desde EstadoDao o desde SQL y asigna this.estados
    private void cargarEstadosDesdeBD() {
        List<Estado> lista = new ArrayList<>();
        try {
            infra.db.EstadoDao estadoDao = new infra.db.EstadoDao();
            // intentar método concreto findAll() del DAO (más directo y seguro)
            try {
                List<Estado> fromDao = estadoDao.findAll();
                if (fromDao != null && !fromDao.isEmpty()) {
                    lista.addAll(fromDao);
                }
            } catch (NoSuchMethodError | RuntimeException ex) {
                // fallback reflexivo a findAll/getAll si la implementación fuera distinta
                try {
                    Object res = null;
                    try {
                        java.lang.reflect.Method m = estadoDao.getClass().getMethod("findAll");
                        res = m.invoke(estadoDao);
                    } catch (NoSuchMethodException ignore) {
                        try {
                            java.lang.reflect.Method m2 = estadoDao.getClass().getMethod("getAll");
                            res = m2.invoke(estadoDao);
                        } catch (NoSuchMethodException ignore2) { /* seguir a fallback SQL */ }
                    }
                    if (res instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<?> tmp = (List<?>) res;
                        for (Object o : tmp) {
                            if (o instanceof Estado) lista.add((Estado) o);
                        }
                    }
                } catch (Exception ignored) { /* ignore */ }
            }
        } catch (Exception ignored) {
            // ignore and fallback to SQL
        }

        // fallback SQL directo si DAO no devolvió nada
        if (lista.isEmpty()) {
            try (java.sql.Connection c = infra.db.SQLite.get();
                 java.sql.PreparedStatement ps = c.prepareStatement("SELECT ambito, nombre FROM Estado");
                 java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String amb = rs.getString("ambito");
                    String nom = rs.getString("nombre");
                    lista.add(new Estado(amb, nom));
                }
            } catch (Exception ignored) {
                // leave lista empty
            }
        }

        if (!lista.isEmpty()) this.estados = lista;
    }


    public LocalDateTime obtenerFechaHoraActual() {
       return LocalDateTime.now();
    }


    public Estado obtenerEstadoFueraDeServicioSismografo() {

       for (Estado estado : this.estados) {
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

		// Buscar siempre en la base via DAO (no usar la lista local hardcodeada)
		try {
			infra.db.SismografoDao sd = new infra.db.SismografoDao();
			Sismografo sFromDb = sd.findByIdentificador(identificadorSismografo);
			if (sFromDb != null) return sFromDb;
			// no existe en BD
			throw new IllegalArgumentException("Sismógrafo no encontrado: " + identificadorSismografo);
		} catch (RuntimeException ex) {
			// propagar error claro para debugging (evita swallowing)
			throw new RuntimeException("Error buscando Sismografo por identificador: " + identificadorSismografo + " - " + ex.getMessage(), ex);
		}
	}

    public void ponerSismografoEnFueraDeServicio(){
        estadoFueraServicio = obtenerEstadoFueraDeServicioSismografo();
        identificadorSismografo = ordenSeleccionada.getIdentificadorSismografo();
        Sismografo s = this.buscarSismografoPorIdentificador(identificadorSismografo);

        // pasar empleadoLogueado para que el CambioEstado lo contenga
        s.sismografoEnFueraDeServicio(estadoFueraServicio, comentariosPorMotivo, empleadoLogueado);

        // obtener el cambio recién creado y persistirlo (fuera de la clase Sismografo)


        this.obtenerResponsableDeReparacion();
    }


    public void obtenerResponsableDeReparacion() {
        emailsResponsables.clear();

        // Intentar cargar empleados desde BD y filtrar responsables de reparación
        try {
            infra.db.EmpleadoDao empDao = new infra.db.EmpleadoDao();
            List<infra.db.EmpleadoDao.EmpleadoRow> rows = empDao.findAll();
            if (rows != null && !rows.isEmpty()) {
                for (infra.db.EmpleadoDao.EmpleadoRow r : rows) {
                    try {
                        Empleado e = empDao.findById(r.idEmpleado());
                        if (e != null && e.esResponsableDeReparacion()) {
                            String mail = e.obtenerEmail();

                            if (mail != null && !mail.isBlank() && !emailsResponsables.contains(mail)) {
                                emailsResponsables.add(mail);
                            }
                        }
                    } catch (Exception ignoreOne) {
                        // seguir con el siguiente registro
                    }
                }
                notificarCierre();
                return;
            }
        } catch (Exception dbEx) {
            // si falla el acceso a BD, caer al fallback en memoria
        }

        // Fallback: usar la lista en memoria (comportamiento anterior)
        if (this.empleados != null) {
            for (Empleado empleado : empleados) {
                if (empleado != null && empleado.esResponsableDeReparacion()) {
                    String mail = empleado.obtenerEmail();
                    if (mail != null && !mail.isBlank() && !emailsResponsables.contains(mail)) {
                        emailsResponsables.add(mail);
                    }
                }
            }
        }
        notificarCierre();
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
        // asegurar que la lista de estados esté poblada (lazy load)
        if (this.estados == null || this.estados.isEmpty()) {
            cargarEstadosDesdeBD();
        }
        if (this.estados == null) return null;

        for (Estado estado : this.estados) {
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
