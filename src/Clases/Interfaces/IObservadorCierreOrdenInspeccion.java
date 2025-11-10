package Clases.Interfaces;

import java.time.LocalDateTime;
import java.util.List;

public interface IObservadorCierreOrdenInspeccion {

    void actualizar(    String identificadorSismografo,
                        String estado,
                        LocalDateTime fechaHora,
                        List<String> motivosYComentarios,
                        String tituloPantalla,
                        List<String> emailsResponsables);

}
