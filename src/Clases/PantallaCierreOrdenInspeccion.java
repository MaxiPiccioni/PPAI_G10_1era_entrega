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
    private Map<JCheckBox, JTextArea> comentariosPorCheckbox = new HashMap<>();
    private JPanel panelMotivos;


    public PantallaCierreOrdenInspeccion(GestorCierreDeInspeccion gestor) {
        this.gestor = gestor;
        setTitle("Gestor de Cierre de Órdenes de Inspección");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        habilitarPantalla();
    }

    private void habilitarPantalla() {
        panelPrincipal = new JPanel(new BorderLayout());

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

        JFrame nuevaVentana = new JFrame("Gestor de Cierre de Órdenes de Inspección");
        nuevaVentana.setSize(700, 400);
        nuevaVentana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nuevaVentana.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título arriba
        JLabel titulo = new JLabel("Gestor de Cierre de Órdenes de Inspección", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        // Subtítulo justo debajo del título
        JLabel subtitulo = new JLabel("Órdenes completamente realizadas", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(subtitulo, BorderLayout.BEFORE_FIRST_LINE);

        // Tabla
        String[] columnas = {"N° Orden", "Fecha Finalización", "Estación Sismológica", "ID Sismógrafo"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<OrdenDeInspeccion> ordenes = gestor.ordenarPorFecha();
        for (OrdenDeInspeccion orden : ordenes) {
            Object[] fila = orden.obtenerDatos();
            modeloTabla.addRow(fila);
        }

        JTable tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowSelectionAllowed(true);
        tabla.setColumnSelectionAllowed(false);

        tabla.setPreferredScrollableViewportSize(tabla.getPreferredSize());
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Mensaje de selección debajo de la tabla
        JLabel mensajeSeleccion = new JLabel("Seleccione una orden de inspección.");
        mensajeSeleccion.setFont(new Font("Arial", Font.ITALIC, 14));
        panel.add(mensajeSeleccion, BorderLayout.SOUTH);

        tomarSeleccionOrden(tabla, mensajeSeleccion);

        nuevaVentana.setContentPane(panel);
        nuevaVentana.pack();
        nuevaVentana.setLocationRelativeTo(null);
        nuevaVentana.setVisible(true);
    }

    private void tomarSeleccionOrden(JTable tabla, JLabel mensajeSeleccion) {
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
        List<String> motivosTipo= gestor.buscarTiposMotivo();
        tomarTipos(motivosTipo);
    }

    private void tomarTipos(List<String> motivosTipo) {
        checkboxesMotivo = new ArrayList<>();
        comentariosPorCheckbox = new HashMap<>();

        panelMotivos = new JPanel();
        panelMotivos.setLayout(new BoxLayout(panelMotivos, BoxLayout.Y_AXIS));
        panelMotivos.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        for (String descripcion : motivosTipo) {
            JCheckBox chk = new JCheckBox(descripcion);
            chk.setFont(new Font("Arial", Font.BOLD, 14));

            JTextArea txtComentario = new JTextArea(2, 30);
            txtComentario.setFont(new Font("Arial", Font.PLAIN, 13));
            txtComentario.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            checkboxesMotivo.add(chk);
            comentariosPorCheckbox.put(chk, txtComentario);

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

        panelMotivos.add(Box.createRigidArea(new Dimension(0, 10)));
        panelMotivos.add(btnConfirmar);


        JFrame frame = new JFrame("Gestor de Cierre de Órdenes de Inspección");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

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

        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);

    }

    private void tomarIngresoComentario() {

        for (JCheckBox chk : checkboxesMotivo) {
            if (chk.isSelected()) {

                String descripcion = chk.getText();
                String comentario = comentariosPorCheckbox.get(chk).getText().trim();
                /*
                if (comentario.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Debe ingresar un comentario para el motivo seleccionado: \"" + descripcion + "\".",
                            "Comentario obligatorio",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }*/

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
    }

    private void tomarConfirmacionCierre() {
        if (!gestor.validarDatosCierre()) {
            JOptionPane.showMessageDialog(null,
                    "Hay datos faltantes: Observación fuera de cierre o no se seleccionaron motivos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        gestor.tomarConfirmacionCierre(true);

        JOptionPane.showMessageDialog(null,
                "La orden fue cerrada correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }











}
