package Clases;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CambioEstado {
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Estado estado;
    private Empleado empleado;
    private List<MotivoFueraServicio> motivosFueraDeServicio = new ArrayList<>();


    public CambioEstado(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, Estado estado) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
    }

    public Estado getEstado() {
        return estado;
    }

    public boolean esEstadoActual(Estado estado) {
        return this.estado != null && this.estado.equals(estado);
    }
    public void finalizar() {
        this.fechaHoraFin = LocalDateTime.now();
    }

    public void crearMotivoFueraDeServicio(Map<MotivoTipo, String> comentarios) {
        for (Map.Entry<MotivoTipo, String> entry : comentarios.entrySet()) {
            MotivoTipo tipo = entry.getKey();
            String comentario = entry.getValue();

            MotivoFueraServicio nuevoMotivo = new MotivoFueraServicio(comentario, tipo);
            motivosFueraDeServicio.add(nuevoMotivo);
        }
    }



}
