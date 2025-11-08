package Clases;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PantallaCCRS {

    public static void mostrarEnPantalla(
            String identificadorSismografo,
            String estado,
            LocalDateTime fechaHora,
            List<String> motivosYComentarios,
            String tituloPantalla //variable solo para CCRS
    ) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Visualización para el Centro de Coordinación de la Red Sísmica\n\n");

        mensaje.append("Sismógrafo: ").append(identificadorSismografo)
                .append("\nEstado: ").append(estado).append("\n");

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = fechaHora.format(formato);

        mensaje.append("Fecha y hora de registro: ").append(fechaFormateada).append("\n\n");

        mensaje.append("Motivos y comentarios:\n");
        for (String linea : motivosYComentarios) {
            mensaje.append("- ").append(linea).append("\n");
        }

        JTextArea areaTexto = new JTextArea(mensaje.toString());
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane panelPantalla = new JScrollPane(areaTexto);
        panelPantalla.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(null, panelPantalla, tituloPantalla, JOptionPane.INFORMATION_MESSAGE);
    }

}
