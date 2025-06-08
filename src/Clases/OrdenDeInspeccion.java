package Clases;

import java.time.LocalDateTime;

public class OrdenDeInspeccion {
    private LocalDateTime fechaHoraCierre;
    private LocalDateTime fechaHoraFinalizacion;
    private LocalDateTime fechaHoraInicio;
    private Integer numeroOrden;
    private String observacionCierre;

    public OrdenDeInspeccion(LocalDateTime fechaHoraCierre, LocalDateTime fechaHoraFinalizacion, LocalDateTime fechaHoraInicio, Integer numeroOrden, String observacionCierre) {
        this.fechaHoraCierre = fechaHoraCierre;
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.numeroOrden = numeroOrden;
        this.observacionCierre = observacionCierre;
    }

    


}
