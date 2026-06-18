package ar.edu.unam.veterinaria.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

public class MainLayoutController {

    private static final Logger LOGGER = Logger.getLogger(MainLayoutController.class.getName());

    @FXML
    private VBox sidebar;

    @FXML
    private StackPane contentArea;

    // Métodos vacíos (por ahora) vinculados al onAction de tus botones FXML
    @FXML
    public void cargarVistaClientes(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Clientes...");
    }

    @FXML
    public void cargarVistaTurnos(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Turnos...");
    }

    @FXML
    public void cargarVistaHistorial(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Historial...");
    }

    @FXML
    public void cargarVistaVacunaciones(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Vacunaciones...");
    }

    @FXML
    public void cargarVistaVeterinarios(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Veterinarios...");
    }

    @FXML
    public void cargarVistaGuarderia(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Guardería...");
    }
}