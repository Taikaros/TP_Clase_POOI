package ar.edu.unam.veterinaria.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane; // <-- IMPORTAMOS BORDERPANE
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainLayoutController {
    private static final Logger LOGGER = Logger.getLogger(MainLayoutController.class.getName());
    
    // <-- ACÁ ESTABA EL ERROR: AHORA ES UN BORDERPANE
    @FXML private BorderPane sidebar; 
    @FXML private StackPane contentArea; 

    @FXML private Button btnClientes;
    @FXML private Button btnTurnos;
    @FXML private Button btnHistorial;
    @FXML private Button btnVacunaciones;
    @FXML private Button btnVeterinarios;
    @FXML private Button btnGuarderia;
    @FXML private Button btnConfiguracion;

    @FXML
    public void initialize() {
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
        Button[] botones = {btnClientes, btnTurnos, btnHistorial, btnVacunaciones, btnVeterinarios, btnGuarderia, btnConfiguracion};
        for (Button btn : botones) {
            if (btn != null) btn.getStyleClass().remove("nav-button-active");
        }
        if (botonActivo != null) botonActivo.getStyleClass().add("nav-button-active");
    }

    @FXML public void cargarVistaClientes(ActionEvent event) { activarBoton(btnClientes); cargarVista("clientes.fxml"); }
    @FXML public void cargarVistaTurnos(ActionEvent event) { activarBoton(btnTurnos); cargarVista("turnos.fxml"); }
    @FXML public void cargarVistaHistorial(ActionEvent event) { activarBoton(btnHistorial); cargarVista("Consultorio.fxml"); }
    @FXML public void cargarVistaVacunaciones(ActionEvent event) { activarBoton(btnVacunaciones); cargarVista("Vacunaciones.fxml"); }
    @FXML public void cargarVistaVeterinarios(ActionEvent event) { activarBoton(btnVeterinarios); cargarVista("Veterinarios.fxml"); }
    @FXML public void cargarVistaGuarderia(ActionEvent event) { activarBoton(btnGuarderia); cargarVista("peluqueriaYguarderia.fxml"); }
    @FXML public void cargarVistaConfiguracion(ActionEvent event) { activarBoton(btnConfiguracion); cargarVista("configuracion.fxml"); }
}