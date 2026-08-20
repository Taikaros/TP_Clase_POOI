package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.dto.ClienteDTO;
import ar.edu.unam.veterinaria.dto.MascotaDTO;
import ar.edu.unam.veterinaria.service.ClienteService;
import ar.edu.unam.veterinaria.service.MascotaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ClienteController {
    //CAMPOS DE TEXTO
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtApellidoCliente;
    @FXML private TextField txtTelefonoCliente;
    @FXML private TextField txtEmailCliente;
    @FXML private TextField txtNombreMascota;
    @FXML private TextField txtEspecie;
    @FXML private TextField txtRaza;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtNumeroFicha;

    //TABLAS (Ahora usan DTOs)
    @FXML private TableView<ClienteDTO> tablaClientes;
    @FXML private TableColumn<ClienteDTO, String> colNombreCliente;
    @FXML private TableColumn<ClienteDTO, String> colApellidoCliente;
    @FXML private TableColumn<ClienteDTO, String> colTelefonoCliente;
    @FXML private TableColumn<ClienteDTO, String> colEmailCliente;

    @FXML private TableView<MascotaDTO> tablaMascotas;
    @FXML private TableColumn<MascotaDTO, String> colNombreMascota;
    @FXML private TableColumn<MascotaDTO, String> colEspecie;
    @FXML private TableColumn<MascotaDTO, String> colRaza;
    @FXML private TableColumn<MascotaDTO, String> colFechaNacimiento;
    @FXML private TableColumn<MascotaDTO, Long> colNumeroFicha; // Cambiado a Long

    //SERVICIOS (Usando tu nueva arquitectura)
    private ClienteService clienteService = new ClienteService();
    private MascotaService mascotaService = new MascotaService();

    //INICIALIZACION
    @FXML
    public void initialize() {
        try {
            // Configuraciones de las columnas vinculadas a los atributos de los DTOs
            colNombreCliente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colApellidoCliente.setCellValueFactory(new PropertyValueFactory<>("apellido"));
            colTelefonoCliente.setCellValueFactory(new PropertyValueFactory<>("telefono"));
            colEmailCliente.setCellValueFactory(new PropertyValueFactory<>("email"));

            // Nota: En el DTO le pusiste "nombreMascota", as  que aca va igual
            colNombreMascota.setCellValueFactory(new PropertyValueFactory<>("nombreMascota"));
            colEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
            colRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
            colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
            colNumeroFicha.setCellValueFactory(new PropertyValueFactory<>("numeroFicha"));

            // L gica de listeners
            tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    txtNombreCliente.setText(newVal.getNombre());
                    txtApellidoCliente.setText(newVal.getApellido());
                    txtTelefonoCliente.setText(newVal.getTelefono());
                    txtEmailCliente.setText(newVal.getEmail());
                    
                    // Como el DTO es plano y no trae la lista embebida, filtramos las mascotas por el ID del due o
                    List<MascotaDTO> mascotasDelCliente = mascotaService.obtenerTodas().stream()
                            .filter(m -> m.getIdCliente() == newVal.getId())
                            .collect(Collectors.toList());
                    tablaMascotas.setItems(FXCollections.observableArrayList(mascotasDelCliente));
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
                    
                    // Asegurate de agregar numeroFicha a tu MascotaDTO para que esto no d  error
                    // txtNumeroFicha.setText(String.valueOf(newVal.getNumeroFicha())); 
                }
            });

            // Carga de datos inicial
            List<ClienteDTO> todosLosClientes = clienteService.obtenerTodos();
            if (todosLosClientes != null) {
                tablaClientes.setItems(FXCollections.observableArrayList(todosLosClientes));
            }
        } catch (Exception e) {
            System.err.println(" Error cr tico al inicializar la pantalla de Clientes!");
            e.printStackTrace();
        }
    }

    //BOTONES
    @FXML 
    private void guardarCliente() {
        // Se crea el DTO (con ID 0 porque es nuevo)
        ClienteDTO clienteDTO = new ClienteDTO(0L, txtNombreCliente.getText(), txtApellidoCliente.getText(), txtTelefonoCliente.getText(), txtEmailCliente.getText());
        
        // Se delega al servicio
        ClienteDTO guardado = clienteService.guardarCliente(clienteDTO);
        if (guardado != null) {
            tablaClientes.getItems().add(guardado);
            limpiar();
        }
    }

    @FXML 
    private void agregarMascota() {
        ClienteDTO clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) {
            mostrarAlerta("Atención", "Debe seleccionar un cliente de la tabla antes de agregar una mascota.");
            return; 
        }

        // Se crea el DTO plano pasando ahora los 8 parámetros (incluyendo el numeroFicha al final)
        MascotaDTO nuevaMascotaDTO = new MascotaDTO(
            0L, 
            txtNombreMascota.getText(), 
            txtEspecie.getText(), 
            txtRaza.getText(), 
            dpFechaNacimiento.getValue(), 
            clienteSeleccionado.getId(), 
            clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido(),
            Long.valueOf(txtNumeroFicha.getText()) // <-- ¡Acá está el parámetro que faltaba!
        );

        MascotaDTO guardada = mascotaService.guardarMascota(nuevaMascotaDTO);
        if (guardada != null) {
            tablaMascotas.getItems().add(guardada);
            limpiar();
        }
    }

    @FXML 
    private void modificar() {
        MascotaDTO mascotaSeleccionada = tablaMascotas.getSelectionModel().getSelectedItem();
        ClienteDTO clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (mascotaSeleccionada != null) {
            mascotaSeleccionada.setNombreMascota(txtNombreMascota.getText());
            mascotaSeleccionada.setEspecie(txtEspecie.getText());
            mascotaSeleccionada.setRaza(txtRaza.getText());
            mascotaSeleccionada.setFechaNacimiento(dpFechaNacimiento.getValue());
            // mascotaSeleccionada.setNumeroFicha(Long.valueOf(txtNumeroFicha.getText()));
            
            // mascotaService.actualizarMascota(mascotaSeleccionada);
            tablaMascotas.refresh();
            limpiar();
            mostrarAlerta(" xito", "Mascota modificada correctamente.");
        } else if (clienteSeleccionado != null) {
            clienteSeleccionado.setNombre(txtNombreCliente.getText());
            clienteSeleccionado.setApellido(txtApellidoCliente.getText());
            clienteSeleccionado.setTelefono(txtTelefonoCliente.getText());
            clienteSeleccionado.setEmail(txtEmailCliente.getText());
            
            // clienteService.actualizarCliente(clienteSeleccionado);
            tablaClientes.refresh();
            limpiar();
            mostrarAlerta(" xito", "Cliente modificado correctamente.");
        } else {
            mostrarAlerta("Atenci n", "Seleccione una mascota de la tabla para modificarla.");
        }
    }

    @FXML 
    private void eliminar() {
        ClienteDTO clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        MascotaDTO mascotaSeleccionada = tablaMascotas.getSelectionModel().getSelectedItem();

        if (mascotaSeleccionada != null) {
            // mascotaService.eliminarMascota(mascotaSeleccionada.getId());
            tablaMascotas.getItems().remove(mascotaSeleccionada);
            limpiar();
        } else if (clienteSeleccionado != null) {
            // clienteService.darDeBaja(clienteSeleccionado.getId()); 
            tablaClientes.getItems().remove(clienteSeleccionado);
            mostrarAlerta(" xito", "El cliente ha sido dado de baja.");
            limpiar();
        } else {
            mostrarAlerta("Atenci n", "Debe seleccionar un elemento para eliminar.");
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
}

