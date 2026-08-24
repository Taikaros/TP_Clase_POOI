package ar.edu.unam.veterinaria.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FichaMedicaController {

    // Mapeo de variables FXML
    @FXML
    private TextField txtBuscarMascota;
    
    @FXML
    private TextField txtCodigoInvestigador;

    @FXML
    private Button btnBuscar;

    @FXML
    private TableView<?> tablaHistorial;

    @FXML
    private TextArea txtDiagnostico;

    @FXML
    private TextArea txtTratamiento;

    
    
    @FXML
    public void buscarHistorial(ActionEvent event) {
        
    }

    @FXML
    public void registrarAtencion(ActionEvent event) {
        
    }
}