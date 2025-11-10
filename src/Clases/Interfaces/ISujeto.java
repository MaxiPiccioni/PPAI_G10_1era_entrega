package Clases.Interfaces;

public interface ISujeto {

    void suscribir(IObservadorCierreOrdenInspeccion o);
    void quitar(IObservadorCierreOrdenInspeccion o);
    void notificar();

}
