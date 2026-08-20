package ar.edu.unam.veterinaria.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainLayoutController {

    private static final Logger LOGGER = Logger.getLogger(MainLayoutController.class.getName());

    @FXML
    private VBox sidebar;

    @FXML
    private StackPane contentArea; // Este es el panel central que vamos a modificar

    /**
     * Método genérico para cargar cualquier vista FXML dentro del contentArea
     */
    private void cargarVista(String archivoFxml) {
        try {
            // NOTA: Ajusta la ruta según la estructura de tu carpeta 'resources'. 
            // Si tus FXML están en una carpeta 'vistas', usa "/vistas/" + archivoFxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + archivoFxml));
            Parent nuevaVista = loader.load();
            
            // Limpiamos el área central y agregamos la nueva vista
            contentArea.getChildren().clear();
            contentArea.getChildren().add(nuevaVista);
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la vista: " + archivoFxml, e);
        }
    }

    // --- Métodos vinculados a los botones ---

    @FXML
    public void cargarVistaClientes(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Clientes...");
        // Llama al método pasándole el nombre exacto de tu archivo FXML de clientes
        cargarVista("clientes.fxml"); 
    }

    @FXML
    public void cargarVistaTurnos(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Turnos...");
        cargarVista("turnos.fxml"); 
    }

    @FXML
    public void cargarVistaHistorial(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Historial...");
        cargarVista("Historial.fxml"); // Reemplazar con el nombre real
    }

    @FXML
    public void cargarVistaVacunaciones(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Vacunaciones...");
        cargarVista("Vacunaciones.fxml"); // Reemplazar con el nombre real
    }

    @FXML
    public void cargarVistaVeterinarios(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Veterinarios...");
        cargarVista("Veterinarios.fxml"); // Reemplazar con el nombre real
    }

    @FXML
    public void cargarVistaGuarderia(ActionEvent event) {
        LOGGER.info("Cargando pantalla de Guardería...");
        cargarVista("Guarderia.fxml"); // Reemplazar con el nombre real
    }
}