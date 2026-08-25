package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.model.TipoServicio;
import ar.edu.unam.veterinaria.service.TipoServicioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ConfiguracionController {

    @FXML private TableView<TipoServicio> tablaServicios;
    @FXML private TableColumn<TipoServicio, String> colNombre;
    @FXML private TableColumn<TipoServicio, Double> colPrecio;
    @FXML private TableColumn<TipoServicio, Double> colDuracion;
    @FXML private TableColumn<TipoServicio, Integer> colCupo;
    
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtCupo;
    @FXML private Label lblTituloFormulario;
    @FXML private Button btnEliminar;
    @FXML private CheckBox chkEsPeluqueria;

    private TipoServicioService service = new TipoServicioService();
    private TipoServicio servicioEnEdicion = null;

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreDescriptivo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioBase"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
        colCupo.setCellValueFactory(new PropertyValueFactory<>("limiteCupoDiario"));
        
        colPrecio.setCellFactory(col -> new TableCell<TipoServicio, Double>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else setText(String.format("$ %.2f", price));
            }
        });

        tablaServicios.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if(newVal != null) {
                cargarDatosEnFormulario(newVal);
            }
        });

        limpiarFormulario();
        cargarTabla();
    }

    private void cargarTabla() {
        tablaServicios.setItems(FXCollections.observableArrayList(service.obtenerTodos()));
    }

    @FXML
    public void limpiarFormulario() {
        servicioEnEdicion = null;
        lblTituloFormulario.setText("Nuevo Servicio");
        txtNombre.clear();
        txtPrecio.clear();
        txtDuracion.clear();
        txtCupo.clear();
        chkEsPeluqueria.setSelected(false);
        btnEliminar.setVisible(false);
        tablaServicios.getSelectionModel().clearSelection();
    }

    private void cargarDatosEnFormulario(TipoServicio ts) {
        servicioEnEdicion = ts;
        lblTituloFormulario.setText("Editar Servicio");
        
        if (ts.getNombreDescriptivo().startsWith("[PELUQUERÍA] ")) {
            txtNombre.setText(ts.getNombreDescriptivo().replace("[PELUQUERÍA] ", ""));
            chkEsPeluqueria.setSelected(true);
        } else {
            txtNombre.setText(ts.getNombreDescriptivo());
            chkEsPeluqueria.setSelected(false);
        }
        
        txtPrecio.setText(String.valueOf(ts.getPrecioBase()));
        txtDuracion.setText(String.valueOf(ts.getDuracion()));
        txtCupo.setText(String.valueOf(ts.getLimiteCupoDiario()));
        btnEliminar.setVisible(true);
    }

    @FXML
    public void guardarServicio() {
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String duracionStr = txtDuracion.getText().trim();
        String cupoStr = txtCupo.getText().trim();

        if (nombre.isEmpty() || precioStr.isEmpty()) {
            mostrarAlerta("Campos Obligatorios", "El nombre y el precio son obligatorios.", Alert.AlertType.WARNING);
            return;
        }
        
        // Lógica de Peluquería: Prefix oculto para poder filtrarlos
        if (chkEsPeluqueria.isSelected() && !nombre.startsWith("[PELUQUERÍA] ")) {
            nombre = "[PELUQUERÍA] " + nombre;
        } else if (!chkEsPeluqueria.isSelected() && nombre.startsWith("[PELUQUERÍA] ")) {
            nombre = nombre.replace("[PELUQUERÍA] ", "");
        }

        try {
            // ... (el resto del método de guardado sigue igual) ...
            Double precio = Double.parseDouble(precioStr.replace(",", "."));
            Double duracion = duracionStr.isEmpty() ? 0.0 : Double.parseDouble(duracionStr);
            Integer cupo = cupoStr.isEmpty() ? 0 : Integer.parseInt(cupoStr);

            if (servicioEnEdicion == null) {
                TipoServicio nuevo = new TipoServicio(nombre, precio, duracion, cupo);
                service.guardar(nuevo);
                mostrarAlerta("Éxito", "Servicio creado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                servicioEnEdicion.setDetalles(nombre, precio, duracion, cupo);
                service.actualizar(servicioEnEdicion);
                mostrarAlerta("Éxito", "Servicio actualizado correctamente.", Alert.AlertType.INFORMATION);
            }
            limpiarFormulario();
            cargarTabla();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "Por favor, ingrese valores numéricos válidos en precio, duración y cupo.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void eliminarServicio() {
        if (servicioEnEdicion != null) {
            service.eliminar(servicioEnEdicion.getId());
            limpiarFormulario();
            cargarTabla();
            mostrarAlerta("Éxito", "Servicio eliminado de forma definitiva.", Alert.AlertType.INFORMATION);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}