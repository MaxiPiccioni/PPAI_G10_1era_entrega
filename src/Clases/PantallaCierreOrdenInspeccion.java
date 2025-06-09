package Clases;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;

public class PantallaCierreOrdenInspeccion extends JFrame {
    private JPanel panelPrincipal;
    private JButton botonCerrarOrden;
    private GestorCierreDeInspeccion gestor;

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

        // Esto hace que el tamaño del viewport del JScrollPane se ajuste a la tabla
        tabla.setPreferredScrollableViewportSize(tabla.getPreferredSize());

        JScrollPane scrollPane = new JScrollPane(tabla);
        // No se fija tamaño preferido en scrollPane, deja que se adapte

        panel.add(scrollPane, BorderLayout.CENTER);

        // Mensaje de selección debajo de la tabla
        JLabel mensajeSeleccion = new JLabel("Seleccione una orden de inspección.");
        mensajeSeleccion.setFont(new Font("Arial", Font.ITALIC, 14));
        panel.add(mensajeSeleccion, BorderLayout.SOUTH);

        tomarSeleccionOrden(tabla, mensajeSeleccion);

        nuevaVentana.setContentPane(panel);
        nuevaVentana.pack();  // Ajusta ventana al contenido
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
                                mensajeSeleccion.setText("Orden seleccionada: N° " + numeroOrden);
                                gestor.tomarOrdenSeleccionada(numeroOrden);
                                pedirObservacion(); // llamada directa tras la selección
                            } catch (NumberFormatException ex) {
                                mensajeSeleccion.setText("Error al seleccionar orden.");
                            }
                        }
                    }
                }
            }
        });
    }

    public void pedirObservacion() {
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
            if (!observacion.isEmpty()) {
                // Acá podrías llamar al gestor para continuar el flujo
                System.out.println("Observación ingresada: " + observacion);
                // gestor.registrarObservacion(observacion); // si lo implementás
                ventanaObservacion.dispose();
            } else {
                JOptionPane.showMessageDialog(ventanaObservacion, "Debe ingresar una observación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panelBoton = new JPanel();
        panelBoton.add(botonConfirmar);
        panel.add(panelBoton, BorderLayout.SOUTH);

        ventanaObservacion.setContentPane(panel);
        ventanaObservacion.setVisible(true);
    }

}
