package Clases;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CambioEstado {
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Estado estado;
    private Empleado responsableInspeccion;
    private List<MotivoFueraServicio> motivosFueraDeServicio = new ArrayList<>();


    public CambioEstado(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, Estado estado) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
    }


    public boolean esEstadoActual(Estado estado) {
        return this.estado != null && this.estado.equals(estado);
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


    public void crearMotivoFueraDeServicio(Map<MotivoTipo, String> comentarios) {
        for (Map.Entry<MotivoTipo, String> entry : comentarios.entrySet()) {
            MotivoTipo tipo = entry.getKey();
            String comentario = entry.getValue();

            MotivoFueraServicio nuevoMotivo = new MotivoFueraServicio(comentario, tipo);
            motivosFueraDeServicio.add(nuevoMotivo);
        }
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

