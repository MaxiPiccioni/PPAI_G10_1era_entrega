package Clases;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CambioEstado {
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Estado estado;
    private Empleado responsableInspeccion;
    private List<MotivoFueraServicio> motivosFueraDeServicio = new ArrayList<>();


    /* Corrección 3 (Al crear motivoFueraServicio, debe estar dentro del inicializador de CambioEstado.)
    public CambioEstado(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, Estado estado) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
    }

    public void crearMotivoFueraDeServicio(Map<MotivoTipo, String> comentarios) {
        for (Map.Entry<MotivoTipo, String> entry : come
        ntarios.entrySet()) {
            MotivoTipo tipo = entry.getKey();
            String comentario = entry.getValue();

            MotivoFueraServicio nuevoMotivo = new MotivoFueraServicio(comentario, tipo);
            motivosFueraDeServicio.add(nuevoMotivo);
        }
    }

     */
    public CambioEstado(LocalDateTime fechaHoraInicio, Estado estado) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.estado = estado;
    }
    public CambioEstado(LocalDateTime fechaHoraInicio,LocalDateTime fechaHoraFin, Estado estado) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
    }

    public CambioEstado(LocalDateTime fechaHoraInicio,
                        Estado estado,
                        Map<MotivoTipo, String> comentariosPorMotivo, Empleado empleado) {
        this.fechaHoraInicio = Objects.requireNonNull(fechaHoraInicio, "inicio requerido");
        this.fechaHoraFin = null; // vigente al crearse
        this.estado = Objects.requireNonNull(estado, "estado requerido");
        this.responsableInspeccion = empleado;

        if (comentariosPorMotivo != null && !comentariosPorMotivo.isEmpty()
                && esFueraDeServicio(estado)) {
            comentariosPorMotivo.forEach((tipo, comentario) ->
                    motivosFueraDeServicio.add(new MotivoFueraServicio(comentario, tipo))
            );
        }


    }

    private boolean esFueraDeServicio(Estado estado) {
        return estado.esFueraDeServicio();
    }



    public Estado getEstado() {
        return estado;
    }


    public void setFechaHoraFin() {
        this.fechaHoraFin = LocalDateTime.now();
    }


    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }


    public List<MotivoFueraServicio> getMotivosFueraDeServicio() {
        return motivosFueraDeServicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Empleado getResponsableInspeccion() {
        return responsableInspeccion;
    }

    public void setResponsableInspeccion(Empleado responsableInspeccion) {
        this.responsableInspeccion = responsableInspeccion;
    }

    public void setMotivosFueraDeServicio(List<MotivoFueraServicio> motivosFueraDeServicio) {
        this.motivosFueraDeServicio = motivosFueraDeServicio;
    }

    // Persistir este CambioEstado en la BD usando los DAOs.
    // Intenta resolver idEstado, idMotivo e idEmpleado y llama a CambioEstadoDao.
    public void persist(String identificadorSismografo) {
        if (identificadorSismografo == null) throw new IllegalArgumentException("identificadorSismografo null");

        try {
            infra.db.CambioEstadoDao cambioDao = new infra.db.CambioEstadoDao();

            // 1) resolver idEstado (por ambito+nombre o por nombre)
            Integer idEstado = null;
            try {
                infra.db.EstadoDao estadoDao = new infra.db.EstadoDao();
                Object estadoObj = null;
                try { estadoObj = this.getEstado(); } catch (Throwable ignored) {}
                String amb = null, nom = null;
                if (estadoObj != null) {
                    try { java.lang.reflect.Method ma = estadoObj.getClass().getMethod("getAmbito"); amb = (String) ma.invoke(estadoObj); } catch (Exception ignored) {}
                    try { java.lang.reflect.Method mn = estadoObj.getClass().getMethod("getNombre"); nom = (String) mn.invoke(estadoObj); } catch (Exception ignored) {}
                    if (amb != null && nom != null) {
                        try { idEstado = estadoDao.findIdByAmbitoYNombre(amb, nom); } catch (Exception ignored) {}
                    }
                    if (idEstado == null && nom != null) {
                        try { idEstado = estadoDao.getIdByNombre(nom); } catch (Exception ignored) {}
                    }
                }
            } catch (Exception ignored) { }

            // 2) resolver idMotivo (tomar el primer MotivoFueraServicio de la lista si existe)
            Integer idMotivo = null;
            try {
                List<MotivoFueraServicio> motivosLista = this.getMotivosFueraDeServicio();
                if (motivosLista != null && !motivosLista.isEmpty()) {
                    MotivoFueraServicio mf = motivosLista.get(0);
                    if (mf != null && mf.getMotivoTipo() != null) {
                        MotivoTipo mt = mf.getMotivoTipo();
                        infra.db.MotivoTipoDao mtDao = new infra.db.MotivoTipoDao();
                        try { idMotivo = mtDao.getOrCreate(mt.getDescripcion()); } catch (Exception ignored) {}
                    }
                }
            } catch (Exception ignored) {}

            // 3) resolver idEmpleado (si existe Empleado asociado al CambioEstado)
            Integer idEmpleado = null;
            try {
                Empleado emp = null;
                try { emp = this.getResponsableInspeccion(); } catch (Throwable ignored) {}
                if (emp != null) {
                    // intentar getter id en Empleado
                    try {
                        java.lang.reflect.Method mId = emp.getClass().getMethod("getIdEmpleado");
                        Object val = mId.invoke(emp);
                        if (val instanceof Number) idEmpleado = ((Number) val).intValue();
                    } catch (Exception ex) {
                        // fallback por mail
                        try {
                            infra.db.EmpleadoDao ed = new infra.db.EmpleadoDao();
                            String mail = null;
                            try { mail = emp.obtenerEmail(); } catch (Throwable ignored2) {}
                            if (mail != null) idEmpleado = ed.findIdByMail(mail);
                        } catch (Exception ignored2) {}
                    }
                }
            } catch (Exception ignored) {}

            // 4) Persistir: si hay fechaHoraFin -> abrirConFin, sino abrir
            java.time.LocalDateTime inicio = null;
            java.time.LocalDateTime fin = null;
            try { inicio = this.getFechaHoraInicio(); } catch (Throwable ignored) {}
            try { fin = this.getFechaHoraFin(); } catch (Throwable ignored) {}

            // Si no resolvimos idEstado, pasamos 0 (o ajustar para permitir null en DAO si preferís)
            int idE = (idEstado != null) ? idEstado : 0;

            if (fin != null) {
                cambioDao.abrirConFin(inicio, fin, idE, idMotivo, idEmpleado, identificadorSismografo);
            } else {
                cambioDao.abrir(inicio, idE, idMotivo, idEmpleado, identificadorSismografo);
            }
        } catch (RuntimeException ex) {
            // Log para depuración, no propagamos hacia la UI
            System.err.println("[CambioEstado.persist] Error persistiendo cambio para identificador=" + identificadorSismografo + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

