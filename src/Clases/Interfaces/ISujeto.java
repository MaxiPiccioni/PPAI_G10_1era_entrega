package Clases.Interfaces;
import java.util.Collection;

import java.util.List;

public interface ISujeto {

    void suscribir(List <IObservadorCierreOrdenInspeccion > observadores);
    void quitar(List<IObservadorCierreOrdenInspeccion> observadores);
    void notificar();

}
