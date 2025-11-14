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

    public CambioEstado(LocalDateTime fechaHoraInicio,
                        Estado estado,
                        Map<MotivoTipo, String> comentariosPorMotivo) {
        this.fechaHoraInicio = Objects.requireNonNull(fechaHoraInicio, "inicio requerido");
        this.fechaHoraFin = null; // vigente al crearse
        this.estado = Objects.requireNonNull(estado, "estado requerido");

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
}

