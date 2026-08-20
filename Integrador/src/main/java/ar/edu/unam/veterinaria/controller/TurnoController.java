package ar.edu.unam.veterinaria.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TurnoController {

    // --- Inyección de Componentes Visuales ---
    
    @FXML
    private DatePicker dpFecha;

    @FXML
    private ComboBox<String> cbClienteMascota; 

    @FXML
    private ComboBox<String> cbProfesional; 

    @FXML
    private TableView<?> tvTurnos; 

    @FXML
    private TableColumn<?, ?> colHora;

    @FXML
    private TableColumn<?, ?> colMascota;

    @FXML
    private TableColumn<?, ?> colProfesional;

    @FXML
    private TableColumn<?, ?> colEstado;

    @FXML
    private Button btnAgendar;

    @FXML
    private Button btnCancelar;


    // --- Métodos de Eventos ---

    @FXML
    public void initialize() {
        // Método que se ejecuta al cargar la vista
        cargarTabla();
    }

    @FXML
    public void guardarTurno(ActionEvent event) {
        // Esqueleto para el evento del botón "Agendar Turno"
        System.out.println("Clic registrado");
    }

    @FXML
    public void cancelarTurno(ActionEvent event) {
        // Esqueleto para el evento del botón "Cancelar Turno"
        System.out.println("Clic registrado");
    }

    public void cargarTabla() {
        // Esqueleto para la futura carga de datos en el TableView
        System.out.println("Clic registrado");
    }
}
