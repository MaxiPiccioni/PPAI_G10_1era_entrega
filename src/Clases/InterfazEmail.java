package Clases;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;


public class InterfazEmail {

    public static void notificarCierre(
            List<String> emailsResponsables,
            String identificadorSismografo,
            String estado,
            LocalDateTime fechaHora,
            List<String> motivosYComentarios
    ) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Mail enviado a: ")
                .append(String.join(", ", emailsResponsables)).append("\n\n");

        mensaje.append("El sismógrafo ").append(identificadorSismografo)
                .append(" está en estado: ").append(estado).append(".\n");

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = fechaHora.format(formato);

        mensaje.append("Fecha y hora de registro del nuevo estado: ")
                .append(fechaFormateada).append("\n\n");

        mensaje.append("Motivos y comentarios asociados:\n");
        for (String linea : motivosYComentarios) {
            mensaje.append("- ").append(linea).append("\n");
        }

        JTextArea areaTexto = new JTextArea(mensaje.toString());
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane panelEmail = new JScrollPane(areaTexto);
        panelEmail.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(null, panelEmail, "Simulación de envío de Mail", JOptionPane.INFORMATION_MESSAGE);
    }
}
