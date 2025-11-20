package Clases;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PantallaCierreOrdenInspeccion extends JFrame {
    private JPanel panelPrincipal;
    private JButton botonCerrarOrden;
    private GestorCierreDeInspeccion gestor;
    private List<JCheckBox> checkboxesMotivo = new ArrayList<>();
    private Map<JCheckBox, JTextArea> comentariosPorMotivo = new HashMap<>();
    private JPanel panelMotivos;


    public PantallaCierreOrdenInspeccion(GestorCierreDeInspeccion gestor) {
        setTitle("Gestor de Cierre de Órdenes de Inspección");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.gestor = gestor;
    }


    public void seleccionarCierreOrden(){
        habilitarPantalla();
    }


    private void habilitarPantalla() {
        panelPrincipal = new JPanel(new BorderLayout());
        gestor.nuevoCierre();

        JLabel titulo = new JLabel("Gestor de Cierre de Órdenes de Inspección", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel panelBoton = new JPanel(new GridBagLayout());
        botonCerrarOrden = new JButton("Cerrar Orden de Inspección");
        botonCerrarOrden.setFont(new Font("Arial", Font.BOLD, 14));
        botonCerrarOrden.addActionListener(e -> mostrarOrdenesParaSeleccion());

        panelBoton.add(botonCerrarOrden);
        panelPrincipal.add(panelBoton, BorderLayout.CENTER);

        add(panelPrincipal);
    }


    public void mostrarOrdenesParaSeleccion() {
        dispose();

        boolean hayOrdenes = gestor.buscarOrdenes();

        if (!hayOrdenes) {
            JOptionPane.showMessageDialog(null,
                    "El Responsable de Inspección no tiene órdenes completamente realizadas.",
                    "Sin órdenes disponibles",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame ventanaOrdenes = new JFrame("Gestor de Cierre de Órdenes de Inspección");
        ventanaOrdenes.setSize(700, 400);
        ventanaOrdenes.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaOrdenes.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Gestor de Cierre de Órdenes de Inspección", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        JLabel subtitulo = new JLabel("Órdenes completamente realizadas", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(subtitulo, BorderLayout.BEFORE_FIRST_LINE);

        String[] columnas = {"N° Orden", "Fecha Finalización", "Estación Sismológica", "ID Sismógrafo"};
        DefaultTableModel tablaOrdenes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<OrdenDeInspeccion> ordenes = gestor.ordenarPorFecha();
        for (OrdenDeInspeccion orden : ordenes) {
            Object[] fila = orden.obtenerDatos();
            tablaOrdenes.addRow(fila);
        }

        JTable tabla = new JTable(tablaOrdenes);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowSelectionAllowed(true);
        tabla.setColumnSelectionAllowed(false);

        tabla.setPreferredScrollableViewportSize(tabla.getPreferredSize());
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);

        JLabel mensajeSeleccion = new JLabel("Seleccione una orden de inspección.");
        mensajeSeleccion.setFont(new Font("Arial", Font.ITALIC, 14));
        panel.add(mensajeSeleccion, BorderLayout.SOUTH);

        tomarSeleccionOrden(tabla);

        ventanaOrdenes.setContentPane(panel);
        ventanaOrdenes.pack();
        ventanaOrdenes.setLocationRelativeTo(null);
        ventanaOrdenes.setVisible(true);
    }


    private void tomarSeleccionOrden(JTable tabla) {
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int fila = tabla.getSelectedRow();
                    if (fila != -1) {
                        Object numeroOrdenObj = tabla.getValueAt(fila, 0);
                        if (numeroOrdenObj != null) {
                            try {
                                int numeroOrden = Integer.parseInt(numeroOrdenObj.toString());
                                gestor.tomarSeleccionOrden(numeroOrden);
                                pedirObservacion((JFrame) SwingUtilities.getWindowAncestor(tabla));
                            } catch (NumberFormatException ex) {
                            }
                        }
                    }
                }
            }
        });
    }


    public void pedirObservacion(JFrame ventanaAnterior) {
        ventanaAnterior.dispose();

        JFrame ventanaObservacion = new JFrame("Gestor de Cierre de Órdenes de Inspección");
        ventanaObservacion.setSize(600, 300);
        ventanaObservacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaObservacion.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel subtitulo = new JLabel("Ingrese la observación de cierre a la orden de inspección", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(subtitulo, BorderLayout.NORTH);

        JTextArea campoObservacion = new JTextArea();
        campoObservacion.setLineWrap(true);
        campoObservacion.setWrapStyleWord(true);
        JScrollPane scrollObservacion = new JScrollPane(campoObservacion);
        scrollObservacion.setPreferredSize(new Dimension(550, 150));
        panel.add(scrollObservacion, BorderLayout.CENTER);

        JButton botonConfirmar = new JButton("Confirmar");
        botonConfirmar.setFont(new Font("Arial", Font.BOLD, 14));
        botonConfirmar.addActionListener(e -> {
            String observacion = campoObservacion.getText().trim();

            tomarObservacion(observacion);
            ventanaObservacion.dispose();
            pedirTipos();

        });

        JPanel panelBoton = new JPanel();
        panelBoton.add(botonConfirmar);
        panel.add(panelBoton, BorderLayout.SOUTH);

        ventanaObservacion.setContentPane(panel);
        ventanaObservacion.setVisible(true);
    }


    public void tomarObservacion(String observacion) {
        gestor.tomarObservacion(observacion);
    }


    public void pedirTipos(){
        dispose();
        List<String> motivosTipo = gestor.buscarTiposMotivo();
        tomarTipos(motivosTipo);
    }


    private void tomarTipos(List<String> motivosTipo) {
        checkboxesMotivo = new ArrayList<>();
        comentariosPorMotivo = new HashMap<>();

        panelMotivos = new JPanel();
        panelMotivos.setLayout(new BoxLayout(panelMotivos, BoxLayout.Y_AXIS));
        panelMotivos.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        for (String descripcion : motivosTipo) {
            JCheckBox chk = new JCheckBox(descripcion);
            chk.setFont(new Font("Arial", Font.BOLD, 14));

            JTextArea txtComentario = new JTextArea(2, 30);
            txtComentario.setFont(new Font("Arial", Font.PLAIN, 13));
            txtComentario.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            txtComentario.setEnabled(false);

            chk.addActionListener(e -> {
                txtComentario.setEnabled(chk.isSelected());
            });

            checkboxesMotivo.add(chk);
            comentariosPorMotivo.put(chk, txtComentario);

            JPanel fila = new JPanel(new BorderLayout(5, 5));
            fila.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            fila.add(chk, BorderLayout.NORTH);
            fila.add(new JScrollPane(txtComentario), BorderLayout.CENTER);

            panelMotivos.add(fila);
        }

        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfirmar.addActionListener(e -> tomarIngresoComentario());

        JButton btnEnLinea = new JButton("Poner sismógrafo en línea");
        btnEnLinea.setFont(new Font("Arial", Font.BOLD, 14));
        btnEnLinea.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEnLinea.addActionListener(e -> mostrarMensajeSismografoEnLinea());



        panelMotivos.add(Box.createRigidArea(new Dimension(0, 10)));
        panelMotivos.add(btnConfirmar);

        panelMotivos.add(Box.createRigidArea(new Dimension(0, 10)));
        panelMotivos.add(btnEnLinea);



        JFrame panelMotivosYComentarios = new JFrame("Gestor de Cierre de Órdenes de Inspección");
        panelMotivosYComentarios.setSize(600, 400);
        panelMotivosYComentarios.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelMotivosYComentarios.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Gestor de Cierre de Órdenes de Inspección", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titulo);

        JLabel subtitulo = new JLabel("Seleccione los motivos y agregue una observación si corresponde", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.BOLD, 16));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(panelMotivos);

        panelMotivosYComentarios.setContentPane(panel);
        panelMotivosYComentarios.pack();
        panelMotivosYComentarios.setVisible(true);
    }


    private void tomarIngresoComentario() {
        for (JCheckBox chk : checkboxesMotivo) {
            if (chk.isSelected()) {

                String descripcion = chk.getText();
                String comentario = comentariosPorMotivo.get(chk).getText().trim();

                gestor.tomarTipo(descripcion);
                gestor.tomarIngresoComentario(descripcion, comentario);
            }
        }
        pedirConfirmacionCierre();
    }


    private void pedirConfirmacionCierre() {
        int confirm = JOptionPane.showConfirmDialog(null, "¿Confirmar cierre?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tomarConfirmacionCierre();
        }
        if (confirm == JOptionPane.NO_OPTION) {
            System.exit(0);
        }

        JButton btnEnLinea = new JButton("Poner sismografo en línea");
        btnEnLinea.setFont(new Font("Arial", Font.BOLD, 14));
        btnEnLinea.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEnLinea.addActionListener(e -> gestor.ponerSismografoEnLinea());
    }


    private void tomarConfirmacionCierre() {
        if (!gestor.validarDatosCierre()) {
            JOptionPane.showMessageDialog(null,
                    "Hay datos faltantes: Observación fuera de cierre o no se seleccionaron motivos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null,
                "La orden fue cerrada correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

        Window ventanaActual = SwingUtilities.getWindowAncestor(panelMotivos);
        if (ventanaActual != null) {
            ventanaActual.dispose();
        }
        gestor.tomarConfirmacionCierre();
    }


    public void mostrarMensajeSismografoEnLinea() {
        gestor.ponerSismografoEnLinea();

        JOptionPane.showMessageDialog(
                null,
                "El sismógrafo ha sido puesto en estado 'En Línea'.",
                "Estado actualizado",
                JOptionPane.INFORMATION_MESSAGE
        );

        for (Window window : Window.getWindows()) {
            window.dispose();
        }
    }

}
