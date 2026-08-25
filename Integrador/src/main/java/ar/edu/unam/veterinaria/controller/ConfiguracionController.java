package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.model.TipoServicio;
import ar.edu.unam.veterinaria.model.Vacuna;
import ar.edu.unam.veterinaria.service.TipoServicioService;
import ar.edu.unam.veterinaria.service.VacunaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ConfiguracionController {

    // --- ELEMENTOS DE SERVICIOS GENERALES ---
    @FXML private TableView<TipoServicio> tablaServicios;
    @FXML private TableColumn<TipoServicio, String> colNombre;
    @FXML private TableColumn<TipoServicio, Double> colPrecio;
    @FXML private TableColumn<TipoServicio, Integer> colDuracion, colCupo;
    @FXML private TextField txtNombre, txtPrecio, txtDuracion, txtCupo;
    @FXML private CheckBox chkEsPeluqueria;
    @FXML private Button btnEliminar;
    @FXML private Label lblTituloFormulario;

    private TipoServicioService service = new TipoServicioService();
    private TipoServicio servicioEnEdicion;

    // --- ELEMENTOS DE VACUNAS ---
    @FXML private TableView<Vacuna> tablaVacunas;
    @FXML private TableColumn<Vacuna, String> colVacNombre, colVacEnfermedad;
    @FXML private TableColumn<Vacuna, Integer> colVacMeses;
    @FXML private TextField txtVacNombre, txtVacEnfermedad, txtVacMeses;
    @FXML private Button btnEliminarVacuna;
    @FXML private Label lblTituloFormVacuna;

    private VacunaService vacunaService = new VacunaService();
    private Vacuna vacunaEnEdicion;

    @FXML
    public void initialize() {
        // Init Tabla Servicios
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreDescriptivo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioBase"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
        colCupo.setCellValueFactory(new PropertyValueFactory<>("limiteCupoDiario"));

        colPrecio.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText((empty || price == null) ? null : String.format("$ %.2f", price));
            }
        });

        tablaServicios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) cargarDatosEnFormulario(newVal);
        });

        // Init Tabla Vacunas
        colVacNombre.setCellValueFactory(new PropertyValueFactory<>("nombreComercial"));
        colVacEnfermedad.setCellValueFactory(new PropertyValueFactory<>("enfermedad"));
        colVacMeses.setCellValueFactory(new PropertyValueFactory<>("periodicidad"));

        tablaVacunas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) cargarDatosVacuna(newVal);
        });

        cargarTablaServicios();
        cargarTablaVacunas();
    }

    // ==================== LÓGICA DE SERVICIOS ====================
    private void cargarTablaServicios() {
        tablaServicios.setItems(FXCollections.observableArrayList(service.obtenerTodos()));
    }

    @FXML
    private void limpiarFormulario() {
        servicioEnEdicion = null;
        lblTituloFormulario.setText("Nuevo Servicio");
        txtNombre.clear(); txtPrecio.clear(); txtDuracion.clear(); txtCupo.clear();
        chkEsPeluqueria.setSelected(false);
        btnEliminar.setVisible(false);
        tablaServicios.getSelectionModel().clearSelection();
    }

    private void cargarDatosEnFormulario(TipoServicio ts) {
        servicioEnEdicion = ts;
        lblTituloFormulario.setText("Editar Servicio");
        String nombreReal = ts.getNombreDescriptivo();
        if (nombreReal.startsWith("[PELUQUERÍA] ")) {
            chkEsPeluqueria.setSelected(true);
            nombreReal = nombreReal.replace("[PELUQUERÍA] ", "");
        } else {
            chkEsPeluqueria.setSelected(false);
        }
        txtNombre.setText(nombreReal);
        txtPrecio.setText(String.valueOf(ts.getPrecioBase()));
        txtDuracion.setText(String.valueOf(ts.getDuracion()));
        txtCupo.setText(String.valueOf(ts.getLimiteCupoDiario()));
        btnEliminar.setVisible(true);
    }

    @FXML
    private void guardarServicio() {
        if (txtNombre.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Obligatorios", "El nombre y el precio son obligatorios.", Alert.AlertType.WARNING);
            return;
        }
        try {
            String nombreFinal = chkEsPeluqueria.isSelected() ? "[PELUQUERÍA] " + txtNombre.getText().trim() : txtNombre.getText().trim();
            Double precio = Double.parseDouble(txtPrecio.getText().trim());
            Double duracion = txtDuracion.getText().trim().isEmpty() ? 30.0 : Double.parseDouble(txtDuracion.getText().trim());
            Integer cupo = txtCupo.getText().trim().isEmpty() ? 10 : Integer.parseInt(txtCupo.getText().trim());

            if (servicioEnEdicion == null) {
                service.guardar(new TipoServicio(nombreFinal, precio, duracion, cupo));
                mostrarAlerta("Éxito", "Servicio creado.", Alert.AlertType.INFORMATION);
            } else {
                servicioEnEdicion.setNombreDescriptivo(nombreFinal);
                servicioEnEdicion.setPrecioBase(precio);
                servicioEnEdicion.setDuracion(duracion);
                servicioEnEdicion.setLimiteCupoDiario(cupo);
                service.actualizar(servicioEnEdicion);
            }
            limpiarFormulario();
            cargarTablaServicios();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Valores numéricos inválidos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarServicio() {
        if (servicioEnEdicion != null) {
            service.eliminar(servicioEnEdicion.getId());
            limpiarFormulario();
            cargarTablaServicios();
        }
    }

    // ==================== LÓGICA DE VACUNAS ====================
    private void cargarTablaVacunas() {
        tablaVacunas.setItems(FXCollections.observableArrayList(vacunaService.obtenerTodas()));
    }

    @FXML
    private void limpiarFormularioVacuna() {
        vacunaEnEdicion = null;
        lblTituloFormVacuna.setText("Nueva Vacuna");
        txtVacNombre.clear(); txtVacEnfermedad.clear(); txtVacMeses.clear();
        btnEliminarVacuna.setVisible(false);
        tablaVacunas.getSelectionModel().clearSelection();
    }

    private void cargarDatosVacuna(Vacuna v) {
        vacunaEnEdicion = v;
        lblTituloFormVacuna.setText("Editar Vacuna");
        txtVacNombre.setText(v.getNombreComercial());
        txtVacEnfermedad.setText(v.getEnfermedad());
        txtVacMeses.setText(String.valueOf(v.getPeriodicidad()));
        btnEliminarVacuna.setVisible(true);
    }

    @FXML
    private void guardarVacuna() {
        if (txtVacNombre.getText().trim().isEmpty() || txtVacMeses.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Obligatorios", "Nombre y Periodicidad son obligatorios.", Alert.AlertType.WARNING);
            return;
        }
        try {
            String nombre = txtVacNombre.getText().trim();
            String enfermedad = txtVacEnfermedad.getText().trim();
            Integer meses = Integer.parseInt(txtVacMeses.getText().trim());

            if (vacunaEnEdicion == null) {
                vacunaService.guardar(new Vacuna(nombre, enfermedad, meses));
            } else {
                vacunaEnEdicion.setNombreComercial(nombre);
                vacunaEnEdicion.setEnfermedad(enfermedad);
                vacunaEnEdicion.setPeriodicidad(meses);
                vacunaService.guardar(vacunaEnEdicion);
            }
            limpiarFormularioVacuna();
            cargarTablaVacunas();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La periodicidad debe ser en meses (números enteros).", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarVacuna() {
        if (vacunaEnEdicion != null) {
            vacunaService.eliminar(vacunaEnEdicion.getId());
            limpiarFormularioVacuna();
            cargarTablaVacunas();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    /* Prueba 01 **/
}