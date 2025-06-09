package Clases;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PantallaCierreOrdenInspeccion extends JFrame {
    private JPanel panelPrincipal;
    private JButton botonCerrarOrden;

    public PantallaCierreOrdenInspeccion() {
        setTitle("Gestor de Cierre de Orden de Inspección");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        habilitarPantalla();
    }

    private void habilitarPantalla() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Gestor de Cierre de Orden de Inspección", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel panelBoton = new JPanel(new GridBagLayout());
        botonCerrarOrden = new JButton("Cerrar Orden de Inspección");
        botonCerrarOrden.setFont(new Font("Arial", Font.BOLD, 14));
        botonCerrarOrden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        panelBoton.add(botonCerrarOrden, gbc);
        panelPrincipal.add(panelBoton, BorderLayout.CENTER);
        add(panelPrincipal);
    }

}