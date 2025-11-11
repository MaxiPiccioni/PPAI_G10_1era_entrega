package Clases.Interfaces;

import java.util.List;

public interface ISujeto {

    void suscribir(List<IObservadorCierreOrdenInspeccion> o);

    void quitar(List<IObservadorCierreOrdenInspeccion> o);

    void notificar();

}
