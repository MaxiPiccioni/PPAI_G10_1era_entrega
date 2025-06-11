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
            String tituloPantalla
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

        JTextArea textArea = new JTextArea(mensaje.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(null, scrollPane, tituloPantalla, JOptionPane.INFORMATION_MESSAGE);
    }

}
