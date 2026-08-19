package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.model.Cliente;
import ar.edu.unam.veterinaria.model.Mascota;
import ar.edu.unam.veterinaria.service.ClienteService;
import ar.edu.unam.veterinaria.service.MascotaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class ClienteController {

    //CAMPOS DE TEXTO
    @FXML
    private TextField txtNombreCliente;

    @FXML
    private TextField txtApellidoCliente;

    @FXML
    private TextField txtTelefonoCliente;

    @FXML
    private TextField txtEmailCliente;

    @FXML
    private TextField txtNombreMascota;

    @FXML
    private TextField txtEspecie;

    @FXML
    private TextField txtRaza;

    @FXML
    private DatePicker dpFechaNacimiento;

    @FXML
    private TextField txtNumeroFicha;

    //TABLAS
    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, String> colNombreCliente;
    @FXML
    private TableColumn<Cliente, String> colApellidoCliente;
    @FXML
    private TableColumn<Cliente, String> colTelefonoCliente;
    @FXML
    private TableColumn<Cliente, String> colEmailCliente;

    @FXML
    private TableView<Mascota> tablaMascotas;
    @FXML
    private TableColumn<Mascota, String> colNombreMascota;
    @FXML
    private TableColumn<Mascota, String> colEspecie;
    @FXML
    private TableColumn<Mascota, String> colRaza;
    @FXML
    private TableColumn<Mascota, String> colFechaNacimiento;
    @FXML
    private TableColumn<Mascota, String> colNumeroFicha;

    //INICIALIZACION
    @FXML
    public void initialize() {
    try {
        // Configuraciones de las columnas
        colNombreCliente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidoCliente.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colTelefonoCliente.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmailCliente.setCellValueFactory(new PropertyValueFactory<>("email"));

        colNombreMascota.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        colRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colNumeroFicha.setCellValueFactory(new PropertyValueFactory<>("numeroFicha"));

        // Lógica de listeners
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtNombreCliente.setText(newVal.getNombre());
                txtApellidoCliente.setText(newVal.getApellido());
                txtTelefonoCliente.setText(newVal.getTelefono());
                txtEmailCliente.setText(newVal.getEmail());
                
                List<Mascota> mascotas = newVal.getMascotas();
                if(mascotas != null) {
                    tablaMascotas.setItems(FXCollections.observableArrayList(mascotas));
                }
            } else {
                tablaMascotas.getItems().clear();
            }
        });

        tablaMascotas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtNombreMascota.setText(newVal.getNombreMascota());
                txtEspecie.setText(newVal.getEspecie());
                txtRaza.setText(newVal.getRaza());
                dpFechaNacimiento.setValue(newVal.getFechaNacimiento()); 
                txtNumeroFicha.setText(String.valueOf(newVal.getNumeroFicha())); 
            }
        });

        // Carga de datos
        // Nota: Asegúrate de usar la instancia en minúscula si el método no es estático
        List<Cliente> todosLosClientes = clienteService.obtenerTodos(); 
        if (todosLosClientes != null) {
            tablaClientes.setItems(FXCollections.observableArrayList(todosLosClientes));
        }

    } catch (Exception e) {
        System.err.println("¡Error crítico al inicializar la pantalla de Clientes!");
        e.printStackTrace();
    };
}
    //SERVICIOS
    private ClienteService clienteService = new ClienteService();
    private MascotaService mascotaService = new MascotaService();

    //BOTONES
@FXML
private void guardarCliente() {
        String nombre = txtNombreCliente.getText();
        String apellido = txtApellidoCliente.getText();
        String telefono = txtTelefonoCliente.getText();
        String email = txtEmailCliente.getText();
        Cliente cliente = new Cliente(nombre, apellido, telefono, email);
        clienteService.guardarCliente(cliente);
        tablaClientes.getItems().add(cliente);
}
@FXML
private void agregarMascota() {
    Cliente clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();

    if (clienteSeleccionado == null) {
        mostrarAlerta("Atención", "Debe seleccionar un cliente de la tabla antes de agregar una mascota.");
        return; 
    }
    String nombre = txtNombreMascota.getText();
    String especie = txtEspecie.getText();
    String raza = txtRaza.getText();
    var fechaNac = dpFechaNacimiento.getValue(); 
    String nroFicha = txtNumeroFicha.getText(); 
    Mascota nuevaMascota = new Mascota(nombre, especie, raza, fechaNac, clienteSeleccionado, Long.valueOf(nroFicha));
    clienteSeleccionado.getMascotas().add(nuevaMascota);
    mascotaService.guardarMascota(nuevaMascota); 
    tablaMascotas.getItems().add(nuevaMascota);
    limpiar();
}
@FXML

private void eliminar() {
    Cliente clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
    Mascota mascotaSeleccionada = tablaMascotas.getSelectionModel().getSelectedItem();

    if (mascotaSeleccionada != null) {
        mascotaService.eliminarMascota(mascotaSeleccionada);
        clienteSeleccionado.getMascotas().remove(mascotaSeleccionada);
        tablaMascotas.getItems().remove(mascotaSeleccionada);
        limpiar();
    } 
    else if (clienteSeleccionado != null) {
        clienteService.darDeBaja(clienteSeleccionado); 
        tablaClientes.getItems().remove(clienteSeleccionado);
        mostrarAlerta("Éxito", "El cliente ha sido dado de baja.");
        limpiar();
    } else {
        mostrarAlerta("Atención", "Debe seleccionar un elemento para eliminar.");
    }
}
@FXML
private void modificar() {
        Mascota mascotaSeleccionada = tablaMascotas.getSelectionModel().getSelectedItem();
        Cliente clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            mascotaSeleccionada.setNombreMascota(txtNombreMascota.getText());
            mascotaSeleccionada.setEspecie(txtEspecie.getText());
            mascotaSeleccionada.setRaza(txtRaza.getText());
            mascotaSeleccionada.setFechaNacimiento(dpFechaNacimiento.getValue());   
            mascotaSeleccionada.setNumeroFicha(Long.valueOf(txtNumeroFicha.getText()));
            mascotaService.actualizarMascota(mascotaSeleccionada); 
            tablaMascotas.refresh();
            limpiar();
            mostrarAlerta("Éxito", "Mascota modificada correctamente.");
        } else if (clienteSeleccionado != null) {
            clienteSeleccionado.setNombre(txtNombreCliente.getText());
            clienteSeleccionado.setApellido(txtApellidoCliente.getText());
            clienteSeleccionado.setTelefono(txtTelefonoCliente.getText());
            clienteSeleccionado.setEmail(txtEmailCliente.getText());
            clienteService.actualizarCliente(clienteSeleccionado);
            tablaClientes.refresh();
            limpiar();
            mostrarAlerta("Éxito", "Cliente modificado correctamente.");
        } else {
            mostrarAlerta("Atención", "Seleccione una mascota de la tabla para modificarla.");
        }
    }
@FXML
private void limpiar() {
    txtNombreCliente.clear();
    txtApellidoCliente.clear();
    txtTelefonoCliente.clear();
    txtEmailCliente.clear();

    txtNombreMascota.clear();
    txtEspecie.clear();
    txtRaza.clear();
    dpFechaNacimiento.setValue(null);
    txtNumeroFicha.clear();

    tablaClientes.getSelectionModel().clearSelection();
    tablaMascotas.getSelectionModel().clearSelection();
}
private void mostrarAlerta(String titulo, String mensaje) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    alert.showAndWait();
}
};


