package Clases;

import Clases.Interfaces.IObservadorCierreOrdenInspeccion;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;


public class InterfazEmail implements IObservadorCierreOrdenInspeccion{

    // --- Config SMTP ---
    private String smtpHost;
    private int smtpPort = 587; // STARTTLS por defecto
    private String smtpUser;
    private String smtpPass;
    private String fromAddress;

    public void configurarSMTP(String smtpHost, int smtpPort, String smtpUser, String smtpPass, String fromAddress) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpUser = smtpUser;
        this.smtpPass = smtpPass;
        this.fromAddress = fromAddress;
    }
    /*
        public InterfazEmail() {
        }


        public static void notificarCierre(
                List<String> emailsResponsables,// variable solo para mail
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
     */
    @Override
    public void actualizar(String identificadorSismografo, String estado, LocalDateTime fechaHora, List<String> motivosYComentarios, String tituloPantalla, List<String> emailsResponsables) {
        this.configurarSMTP(
                "smtp.gmail.com",         // host
                587,                      // puerto STARTTLS
                "estacionsismologica@gmail.com",   // usuario SMTP
                "etcalykctasmchkr",   // contraseña de aplicación
                "estacionsismologica@gmail.com"    // from address
        );
        try {
            this.enviarEmail(identificadorSismografo, estado, fechaHora, motivosYComentarios, emailsResponsables);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarEmail(String idSismografo,
                            String nombreEstado,
                            LocalDateTime fechaHora,
                            List<String> motivosYComentarios,
                            List<String> emailsResponsables) throws MessagingException {

        if (smtpHost == null || smtpUser == null || smtpPass == null || fromAddress == null) {
            throw new MessagingException("SMTP no configurado. Llamá antes a configurarSMTP(...)");
        }
        if (emailsResponsables == null || emailsResponsables.isEmpty()) {
            throw new MessagingException("No hay destinatarios para el envío.");
        }

        // Propiedades SMTP (STARTTLS - puerto 587)
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // si se usa SSL 465, ver nota abajo

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPass);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromAddress));

        for (String to : emailsResponsables) {
            if (to != null && !to.isBlank()) {
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to.trim()));
            }
        }

        msg.setSubject("[CCR] " + nombreEstado + " - " + idSismografo);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fecha = (fechaHora != null) ? fechaHora.format(formato) : "-";

        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append("El sismógrafo ").append(idSismografo)
                .append(" está en estado: ").append(nombreEstado).append(".\n\n")
                .append("Fecha y hora de registro del nuevo estado: ").append(fecha).append("\n\n")
                .append("Motivos y comentarios asociados:\n");

        if (motivosYComentarios != null && !motivosYComentarios.isEmpty()) {
            for (String linea : motivosYComentarios) {
                cuerpo.append("- ").append(linea).append("\n");
            }
        } else {
            cuerpo.append("(sin motivos)\n");
        }

        msg.setText(cuerpo.toString(), "UTF-8");

        Transport.send(msg);
    }
}
