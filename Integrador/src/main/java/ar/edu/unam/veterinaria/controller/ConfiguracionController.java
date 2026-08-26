package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.model.TipoServicio;
import ar.edu.unam.veterinaria.model.Vacuna;
import ar.edu.unam.veterinaria.service.TipoServicioService;
import ar.edu.unam.veterinaria.service.VacunaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.function.UnaryOperator;

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

        UnaryOperator<TextFormatter.Change> filtroEnteros = change -> {
            if (change.getControlNewText().matches("[0-9]*")) return change;
            return null;
        };

        // --- 2. FILTRO PARA NÚMEROS DECIMALES (Precio y Duración) ---
        UnaryOperator<TextFormatter.Change> filtroDecimales = change -> {
            // Permite números y MÁXIMO un solo punto (.)
            if (change.getControlNewText().matches("([0-9]*)?\\.?([0-9]*)?")) return change;
            return null; 
        };
        UnaryOperator<TextFormatter.Change> filtroNombres = change -> {
            if (!change.getControlNewText().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]*")) return null; 
            
            if (change.isAdded()) {
                String textoInsertado = change.getText(); 
                String textoControl = change.getControlText(); 
                int posicion = change.getRangeStart(); 
                StringBuilder textoModificado = new StringBuilder();
                boolean hacerMayuscula = (posicion == 0 || textoControl.charAt(posicion - 1) == ' ');

                for (char c : textoInsertado.toCharArray()) {
                    if (c == ' ') {
                        hacerMayuscula = true;
                        textoModificado.append(c);
                    } else if (hacerMayuscula) {
                        textoModificado.append(Character.toUpperCase(c));
                        hacerMayuscula = false; 
                    } else {
                        textoModificado.append(Character.toLowerCase(c));
                    }
                }
                change.setText(textoModificado.toString());
            }
            return change;
        };
        

        // --- 3. APLICACIÓN DE LOS FILTROS A LAS CAJAS DE TEXTO ---
        // Servicios
        txtPrecio.setTextFormatter(new TextFormatter<>(filtroDecimales));
        txtDuracion.setTextFormatter(new TextFormatter<>(filtroDecimales));
        txtCupo.setTextFormatter(new TextFormatter<>(filtroEnteros));
        txtNombre.setTextFormatter(new TextFormatter<>(filtroNombres));
        // Vacunas
        txtVacMeses.setTextFormatter(new TextFormatter<>(filtroEnteros));
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
    private void guardarServicio(javafx.event.ActionEvent event) {
        try {
            String nombreFinal = chkEsPeluqueria.isSelected() ? "[PELUQUERÍA] " + txtNombre.getText() : txtNombre.getText();
            Double precio = txtPrecio.getText().trim().isEmpty() ? null : Double.valueOf(txtPrecio.getText().trim());
            Double duracion = txtDuracion.getText().trim().isEmpty() ? 30.0 : Double.valueOf(txtDuracion.getText().trim());
            Integer cupo = txtCupo.getText().trim().isEmpty() ? 0 : Integer.valueOf(txtCupo.getText().trim());

            if (servicioEnEdicion == null) {
                ar.edu.unam.veterinaria.model.TipoServicio nuevo = new ar.edu.unam.veterinaria.model.TipoServicio(nombreFinal, precio, duracion, cupo);
                service.guardar(nuevo);
                mostrarAlerta("Éxito", "Servicio creado.", javafx.scene.control.Alert.AlertType.INFORMATION);
            } else {
                servicioEnEdicion.setNombreDescriptivo(nombreFinal);
                servicioEnEdicion.setPrecioBase(precio);
                servicioEnEdicion.setDuracion(duracion);
                servicioEnEdicion.setLimiteCupoDiario(cupo);
                service.actualizar(servicioEnEdicion);
                mostrarAlerta("Éxito", "Servicio actualizado.", javafx.scene.control.Alert.AlertType.INFORMATION);
            }
            limpiarFormulario();
            cargarTablaServicios();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "Los campos Precio, Duración y Cupo deben ser numéricos.", javafx.scene.control.Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            // Atrapa el error del Modelo (Precio negativo, cupo menor a cero)
            mostrarAlerta("Datos Inválidos", e.getMessage(), javafx.scene.control.Alert.AlertType.WARNING);
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un problema al guardar el servicio.", javafx.scene.control.Alert.AlertType.ERROR);
            e.printStackTrace();
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
    private void guardarVacuna(javafx.event.ActionEvent event) {
        try {
            String nombre = txtVacNombre.getText();
            String enfermedad = txtVacEnfermedad.getText();
            Integer meses = txtVacMeses.getText().trim().isEmpty() ? null : Integer.valueOf(txtVacMeses.getText().trim());

            if (vacunaEnEdicion == null) {
                ar.edu.unam.veterinaria.model.Vacuna nueva = new ar.edu.unam.veterinaria.model.Vacuna(nombre, enfermedad, meses);
                vacunaService.guardar(nueva);
                mostrarAlerta("Éxito", "Vacuna creada.", javafx.scene.control.Alert.AlertType.INFORMATION);
            } else {
                vacunaEnEdicion.setNombreComercial(nombre);
                vacunaEnEdicion.setEnfermedad(enfermedad);
                vacunaEnEdicion.setPeriodicidad(meses);
                vacunaService.guardar(vacunaEnEdicion);
                mostrarAlerta("Éxito", "Vacuna actualizada.", javafx.scene.control.Alert.AlertType.INFORMATION);
            }
            limpiarFormularioVacuna();
            cargarTablaVacunas();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "La periodicidad debe ser un número entero (meses).", javafx.scene.control.Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            // Atrapa el error del Modelo (Periodicidad negativa)
            mostrarAlerta("Datos Inválidos", e.getMessage(), javafx.scene.control.Alert.AlertType.WARNING);
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un problema al guardar la vacuna.", javafx.scene.control.Alert.AlertType.ERROR);
            e.printStackTrace();
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