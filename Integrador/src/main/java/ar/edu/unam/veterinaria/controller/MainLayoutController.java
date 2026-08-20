package ar.edu.unam.veterinaria.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainLayoutController {

    private static final Logger LOGGER = Logger.getLogger(MainLayoutController.class.getName());

    @FXML private VBox sidebar;
    @FXML private StackPane contentArea; 

    @FXML private Button btnClientes;
    @FXML private Button btnTurnos;
    @FXML private Button btnHistorial;
    @FXML private Button btnVacunaciones;
    @FXML private Button btnVeterinarios;
    @FXML private Button btnGuarderia;

    @FXML
    public void initialize() {
        // Cargar Clientes por defecto al abrir el sistema
        cargarVistaClientes(null);
    }

    private void cargarVista(String archivoFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + archivoFxml));
            Parent nuevaVista = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(nuevaVista);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la vista: " + archivoFxml, e);
        }
    }

    private void activarBoton(Button botonActivo) {
        Button[] botones = {btnClientes, btnTurnos, btnHistorial, btnVacunaciones, btnVeterinarios, btnGuarderia};
        for (Button btn : botones) {
            if (btn != null) btn.getStyleClass().remove("nav-button-active");
        }
        if (botonActivo != null) botonActivo.getStyleClass().add("nav-button-active");
    }

    @FXML public void cargarVistaClientes(ActionEvent event) {
        activarBoton(btnClientes); 
        cargarVista("clientes.fxml"); 
    }

    @FXML public void cargarVistaTurnos(ActionEvent event) {
        activarBoton(btnTurnos); 
        cargarVista("turnos.fxml"); 
    }

    @FXML public void cargarVistaHistorial(ActionEvent event) {
        activarBoton(btnHistorial);
        cargarVista("Historial.fxml");
    }

    @FXML public void cargarVistaVacunaciones(ActionEvent event) {
        activarBoton(btnVacunaciones);
        cargarVista("Vacunaciones.fxml");
    }

    @FXML public void cargarVistaVeterinarios(ActionEvent event) {
        activarBoton(btnVeterinarios);
        cargarVista("Veterinarios.fxml");
    }

    @FXML public void cargarVistaGuarderia(ActionEvent event) {
        activarBoton(btnGuarderia);
        cargarVista("Guarderia.fxml");
    }
}