import Clases.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Rol rolRI = new Rol("Responsable de Inspeccion");
        Rol rolAyudante = new Rol("Ayudante");

        Empleado empleado1 = new Empleado("Juan", "Pérez", "123456", "juan@mail.com", rolRI);
        Empleado empleado2 = new Empleado("Ana", "Gómez", "654321", "ana@mail.com", rolRI);
        Empleado empleado3 = new Empleado("Luis", "López", "987654", "luis@mail.com", rolAyudante);

        List<Empleado> empleados = Arrays.asList(empleado1, empleado2, empleado3);
        GestorCierreDeInspeccion gestor = new GestorCierreDeInspeccion(empleados);
        List<Empleado> responsables = gestor.buscarEmpleado();

        System.out.println("Responsables de Inspección:");
        for (Empleado emp : responsables) {
            System.out.println("- " + emp.getNombre());
        }

        // --- Iniciar GUI después de la lógica ---
        SwingUtilities.invokeLater(() -> {
            PantallaCierreOrdenInspeccion pantalla = new PantallaCierreOrdenInspeccion();
            pantalla.setVisible(true);
        });
    }
}