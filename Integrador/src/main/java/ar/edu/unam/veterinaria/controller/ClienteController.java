package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.dto.ClienteDTO;
import ar.edu.unam.veterinaria.dto.MascotaDTO;
import ar.edu.unam.veterinaria.service.ClienteService;
import ar.edu.unam.veterinaria.service.MascotaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteController {
    @FXML private TextField txtBuscar;
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtApellidoCliente;
    @FXML private TextField txtDniCliente;
    @FXML private TextField txtTelefonoCliente;
    @FXML private TextField txtEmailCliente;
    @FXML private TextField txtNombreMascota;
    @FXML private TextField txtEspecie;
    @FXML private TextField txtRaza;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtNumeroFicha;
    @FXML private Label lblCantidadClientes;
    @FXML private Label lblCantidadMascotas;
    @FXML private Label lblDniPerfil;

    @FXML private TableView<ClienteDTO> tablaClientes;
    @FXML private TableColumn<ClienteDTO, String> colDniCliente;
    @FXML private TableColumn<ClienteDTO, String> colNombreCliente;
    @FXML private TableColumn<ClienteDTO, String> colApellidoCliente;
    @FXML private TableColumn<ClienteDTO, String> colTelefonoCliente;

    @FXML private TableView<MascotaDTO> tablaMascotas;
    @FXML private TableColumn<MascotaDTO, String> colNombreMascota;
    @FXML private TableColumn<MascotaDTO, String> colEspecie;
    @FXML private TableColumn<MascotaDTO, String> colRaza;
    @FXML private TableColumn<MascotaDTO, java.time.LocalDate> colFechaNacimiento;
    @FXML private TableColumn<MascotaDTO, Long> colNumeroFicha;

    private ClienteService clienteService = new ClienteService();
    private MascotaService mascotaService = new MascotaService();
    private ObservableList<ClienteDTO> masterDataClientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            colDniCliente.setCellValueFactory(new PropertyValueFactory<>("dni"));
            colNombreCliente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colApellidoCliente.setCellValueFactory(new PropertyValueFactory<>("apellido"));
            colTelefonoCliente.setCellValueFactory(new PropertyValueFactory<>("telefono"));

            colNombreMascota.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombreMascota()));
            colEspecie.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEspecie()));
            colRaza.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getRaza()));
            colFechaNacimiento.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getFechaNacimiento()));
            colNumeroFicha.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getNumeroFicha()));

            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            colFechaNacimiento.setCellFactory(column -> new TableCell<MascotaDTO, java.time.LocalDate>() {
                @Override
                protected void updateItem(java.time.LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) setText(null);
                    else setText(formatoFecha.format(item));
                }
            });

            List<ClienteDTO> todosLosClientes = clienteService.obtenerTodos();
            if (todosLosClientes != null) masterDataClientes.addAll(todosLosClientes);

            FilteredList<ClienteDTO> filteredData = new FilteredList<>(masterDataClientes, p -> true);
            if(txtBuscar != null) {
                txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredData.setPredicate(cliente -> {
                        if (newValue == null || newValue.isEmpty()) return true;
                        String lowerCaseFilter = newValue.toLowerCase();
                        if (cliente.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                        if (cliente.getApellido().toLowerCase().contains(lowerCaseFilter)) return true;
                        if (cliente.getDni() != null && cliente.getDni().toLowerCase().contains(lowerCaseFilter)) return true;
                        return false;
                    });
                });
            }
            SortedList<ClienteDTO> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tablaClientes.comparatorProperty());
            tablaClientes.setItems(sortedData);
            actualizarContadorClientes();

            tablaMascotas.setItems(FXCollections.observableArrayList());

            tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    txtNombreCliente.setText(newVal.getNombre());
                    txtApellidoCliente.setText(newVal.getApellido());
                    txtDniCliente.setText(newVal.getDni());
                    txtTelefonoCliente.setText(newVal.getTelefono());
                    txtEmailCliente.setText(newVal.getEmail());
                    lblDniPerfil.setText("DNI: " + (newVal.getDni() != null ? newVal.getDni() : "Sin registro"));

                    try {
                        List<MascotaDTO> mascotasDelCliente = mascotaService.obtenerTodas().stream()
                                .filter(m -> m.getIdCliente() != null && m.getIdCliente().equals(newVal.getId()))
                                .collect(Collectors.toList());
                        tablaMascotas.getItems().setAll(mascotasDelCliente);
                        lblCantidadMascotas.setText(String.valueOf(mascotasDelCliente.size()));
                    } catch (Exception e) { e.printStackTrace(); }
                } else {
                    tablaMascotas.getItems().clear();
                    lblCantidadMascotas.setText("0");
                    lblDniPerfil.setText("DNI: -");
                }
            });

            tablaMascotas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    txtNombreMascota.setText(newVal.getNombreMascota());
                    txtEspecie.setText(newVal.getEspecie());
                    txtRaza.setText(newVal.getRaza());
                    dpFechaNacimiento.setValue(newVal.getFechaNacimiento());
                    if (newVal.getNumeroFicha() != null && newVal.getNumeroFicha() != 0) {
                        txtNumeroFicha.setText(String.valueOf(newVal.getNumeroFicha()));
                    } else txtNumeroFicha.clear();
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void actualizarContadorClientes() {
        lblCantidadClientes.setText("CLIENTES (" + masterDataClientes.size() + ")");
    }

    @FXML 
    private void guardarCliente() {
        ClienteDTO clienteDTO = new ClienteDTO(0L, txtNombreCliente.getText(), txtApellidoCliente.getText(), txtDniCliente.getText(), txtTelefonoCliente.getText(), txtEmailCliente.getText());
        try {
            ClienteDTO guardado = clienteService.guardarCliente(clienteDTO);
            masterDataClientes.add(guardado);
            actualizarContadorClientes();
            limpiar();
            mostrarAlerta("Éxito", "Cliente registrado correctamente.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException e) { mostrarAlerta("Atención", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) { mostrarAlerta("Error", "Ocurrió un error en la base de datos.", Alert.AlertType.ERROR); }
    }

    @FXML 
    private void modificarCliente() {
        ClienteDTO clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) { mostrarAlerta("Atención", "Seleccione un cliente de la tabla.", Alert.AlertType.WARNING); return; }
        try {
            clienteSeleccionado.setNombre(txtNombreCliente.getText());
            clienteSeleccionado.setApellido(txtApellidoCliente.getText());
            clienteSeleccionado.setDni(txtDniCliente.getText());
            clienteSeleccionado.setTelefono(txtTelefonoCliente.getText());
            clienteSeleccionado.setEmail(txtEmailCliente.getText());

            clienteService.actualizarCliente(clienteSeleccionado);
            tablaClientes.refresh();
            limpiar();
            mostrarAlerta("Éxito", "Cliente modificado.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException e) { mostrarAlerta("Atención", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) { mostrarAlerta("Error", "No se pudo modificar el cliente.", Alert.AlertType.ERROR); }
    }

    @FXML 
    private void eliminarCliente() {
        ClienteDTO clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) { mostrarAlerta("Atención", "Seleccione un cliente para eliminar.", Alert.AlertType.WARNING); return; }
        try {
            clienteService.darDeBaja(clienteSeleccionado.getId());
            masterDataClientes.remove(clienteSeleccionado);
            actualizarContadorClientes();
            tablaMascotas.getItems().clear();
            lblCantidadMascotas.setText("0");
            mostrarAlerta("Éxito", "El cliente fue dado de baja.", Alert.AlertType.INFORMATION);
            limpiar();
        } catch (Exception e) { mostrarAlerta("Error", "Error al intentar eliminar el cliente.", Alert.AlertType.ERROR); }
    }

    @FXML 
    private void agregarMascota() {
        ClienteDTO clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) { mostrarAlerta("Atención", "Debe seleccionar un dueño de la tabla izquierda primero.", Alert.AlertType.WARNING); return; }
        Long nroFicha = 0L;
        if (txtNumeroFicha.getText() != null && !txtNumeroFicha.getText().trim().isEmpty()) {
            try { nroFicha = Long.valueOf(txtNumeroFicha.getText()); 
            } catch (NumberFormatException e) { mostrarAlerta("Error", "El número de ficha debe contener únicamente números.", Alert.AlertType.WARNING); return; }
        }
        MascotaDTO nuevaMascotaDTO = new MascotaDTO(0L, txtNombreMascota.getText(), txtEspecie.getText(), txtRaza.getText(), dpFechaNacimiento.getValue(), clienteSeleccionado.getId(), "", nroFicha);
        try {
            MascotaDTO guardada = mascotaService.guardarMascota(nuevaMascotaDTO);
            tablaMascotas.getItems().add(guardada);
            lblCantidadMascotas.setText(String.valueOf(tablaMascotas.getItems().size()));
            limpiar();
            mostrarAlerta("Éxito", "Mascota guardada correctamente.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException e) { mostrarAlerta("Atención", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) { mostrarAlerta("Error", "Ocurrió un error al guardar la mascota.", Alert.AlertType.ERROR); }
    }

    @FXML 
    private void modificarMascota() {
        MascotaDTO mascotaSeleccionada = tablaMascotas.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada == null) { mostrarAlerta("Atención", "Seleccione una mascota de la tabla para modificarla.", Alert.AlertType.WARNING); return; }
        try {
            mascotaSeleccionada.setNombreMascota(txtNombreMascota.getText());
            mascotaSeleccionada.setEspecie(txtEspecie.getText());
            mascotaSeleccionada.setRaza(txtRaza.getText());
            mascotaSeleccionada.setFechaNacimiento(dpFechaNacimiento.getValue());
            if (txtNumeroFicha.getText() != null && !txtNumeroFicha.getText().trim().isEmpty()) { mascotaSeleccionada.setNumeroFicha(Long.valueOf(txtNumeroFicha.getText()));
            } else { mascotaSeleccionada.setNumeroFicha(0L); }
            mascotaService.actualizarMascota(mascotaSeleccionada);
            tablaMascotas.refresh();
            limpiar();
            mostrarAlerta("Éxito", "Mascota modificada correctamente.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException e) { mostrarAlerta("Atención", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) { mostrarAlerta("Error", "No se pudo modificar la mascota.", Alert.AlertType.ERROR); }
    }

    @FXML 
    private void eliminarMascota() {
        MascotaDTO mascotaSeleccionada = tablaMascotas.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada == null) { mostrarAlerta("Atención", "Seleccione una mascota para eliminar.", Alert.AlertType.WARNING); return; }
        try {
            mascotaService.eliminarMascota(mascotaSeleccionada.getId());
            tablaMascotas.getItems().remove(mascotaSeleccionada);
            lblCantidadMascotas.setText(String.valueOf(tablaMascotas.getItems().size()));
            limpiar();
            mostrarAlerta("Éxito", "Mascota eliminada de la base de datos.", Alert.AlertType.INFORMATION);
        } catch (Exception e) { mostrarAlerta("Error", "Error al intentar eliminar la mascota.", Alert.AlertType.ERROR); }
    }

    @FXML 
    private void limpiar() {
        txtNombreCliente.clear();
        txtApellidoCliente.clear();
        txtDniCliente.clear();
        txtTelefonoCliente.clear();
        txtEmailCliente.clear();
        txtNombreMascota.clear();
        txtEspecie.clear();
        txtRaza.clear();
        dpFechaNacimiento.setValue(null);
        txtNumeroFicha.clear();
        lblDniPerfil.setText("DNI: -");
        tablaClientes.getSelectionModel().clearSelection();
        tablaMascotas.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo); alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(mensaje); alert.showAndWait();
    }
}