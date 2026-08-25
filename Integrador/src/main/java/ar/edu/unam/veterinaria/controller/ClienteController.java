package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.dto.ClienteDTO;
import ar.edu.unam.veterinaria.dto.MascotaDTO;
import ar.edu.unam.veterinaria.service.ClienteService;
import ar.edu.unam.veterinaria.service.MascotaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;

public class ClienteController {

    @FXML private TextField txtBuscar;
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtApellidoCliente;
    @FXML private TextField txtDniCliente;
    @FXML private TextField txtTelefonoCliente;
    @FXML private TextField txtEmailCliente;
    
    @FXML private Label lblCantidadClientes;
    @FXML private Label lblCantidadMascotas;
    @FXML private Label lblNombrePerfil;
    @FXML private Label lblDniPerfil;

    @FXML private ListView<ClienteDTO> listaClientes;
    @FXML private ListView<MascotaDTO> listaMascotas;

    // Elementos del Modal de Mascota
    @FXML private HBox overlayMascota;
    @FXML private Label lblTituloModalMascota;
    @FXML private TextField txtNombreMascota;
    @FXML private TextField txtEspecie;
    @FXML private TextField txtRaza;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtNumeroFicha;

    private ClienteService clienteService = new ClienteService();
    private MascotaService mascotaService = new MascotaService();
    private ObservableList<ClienteDTO> masterDataClientes = FXCollections.observableArrayList();
    
    private Long idMascotaEnEdicion = null;

    @FXML
    public void initialize() {
        configurarListaClientes();
        configurarListaMascotas();
        cargarDatosYFiltro();
    }

    private void configurarListaClientes() {
        listaClientes.setCellFactory(param -> new ListCell<ClienteDTO>() {
            @Override
            protected void updateItem(ClienteDTO cliente, boolean empty) {
                super.updateItem(cliente, empty);
                if (empty || cliente == null) {
                    setText(null); setGraphic(null);
                } else {
                    HBox card = new HBox(15);
                    card.setAlignment(Pos.CENTER_LEFT);
                    card.getStyleClass().add("client-list-card");

                    StackPane avatar = new StackPane();
                    avatar.setStyle("-fx-background-color: " + (isSelected() ? "#2CA871" : "#F1F5F9") + "; -fx-background-radius: 50; -fx-min-width: 45; -fx-min-height: 45;");
                    FontIcon icon = new FontIcon("fas-user");
                    icon.setIconSize(20);
                    icon.setIconColor(Color.web(isSelected() ? "white" : "#94A3B8"));
                    avatar.getChildren().add(icon);

                    VBox info = new VBox(3);
                    Label lblNombre = new Label(cliente.getNombre() + " " + cliente.getApellido());
                    lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B;");
                    
                    // Contamos las mascotas rápido para la UI
                    long cantMascotas = mascotaService.obtenerTodas().stream().filter(m -> m.getIdCliente() != null && m.getIdCliente().equals(cliente.getId())).count();
                    Label lblMascotas = new Label(cantMascotas + (cantMascotas == 1 ? " mascota" : " mascotas"));
                    lblMascotas.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
                    
                    info.getChildren().addAll(lblNombre, lblMascotas);
                    card.getChildren().addAll(avatar, info);
                    setGraphic(card);
                }
            }
        });

        listaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblNombrePerfil.setText(newVal.getNombre() + " " + newVal.getApellido());
                lblDniPerfil.setText("DNI: " + (newVal.getDni() != null ? newVal.getDni() : "Sin registro"));
                txtNombreCliente.setText(newVal.getNombre());
                txtApellidoCliente.setText(newVal.getApellido());
                txtDniCliente.setText(newVal.getDni());
                txtTelefonoCliente.setText(newVal.getTelefono());
                txtEmailCliente.setText(newVal.getEmail());

                cargarMascotasDelCliente(newVal.getId());
            } else {
                limpiarFormularioCliente();
            }
        });
    }

    private void configurarListaMascotas() {
        listaMascotas.setCellFactory(param -> new ListCell<MascotaDTO>() {
            @Override
            protected void updateItem(MascotaDTO mascota, boolean empty) {
                super.updateItem(mascota, empty);
                if (empty || mascota == null) {
                    setText(null); setGraphic(null);
                } else {
                    HBox card = new HBox(15);
                    card.setAlignment(Pos.CENTER_LEFT);
                    card.getStyleClass().add("pet-list-card");

                    // Avatar de mascota
                    StackPane avatar = new StackPane();
                    avatar.setStyle("-fx-background-color: #FFFBEB; -fx-background-radius: 50; -fx-min-width: 50; -fx-min-height: 50;");
                    FontIcon icon = new FontIcon(mascota.getEspecie().toLowerCase().contains("gato") ? "fas-cat" : "fas-dog");
                    icon.setIconSize(24);
                    icon.setIconColor(Color.web("#D2B48C"));
                    avatar.getChildren().add(icon);

                    VBox info = new VBox(5);
                    HBox header = new HBox(8);
                    header.setAlignment(Pos.CENTER_LEFT);
                    Label lblNombre = new Label(mascota.getNombreMascota());
                    lblNombre.setStyle("-fx-font-weight: 900; -fx-font-size: 16px; -fx-text-fill: #1E293B;");
                    
                    Label badgeEspecie = new Label(mascota.getEspecie());
                    badgeEspecie.getStyleClass().add("pill");
                    if (mascota.getEspecie().toLowerCase().contains("perro")) badgeEspecie.getStyleClass().add("badge-especie-perro");
                    else if (mascota.getEspecie().toLowerCase().contains("gato")) badgeEspecie.getStyleClass().add("badge-especie-gato");
                    else badgeEspecie.getStyleClass().add("badge-especie-otro");

                    header.getChildren().addAll(lblNombre, badgeEspecie);

                    String fichaStr = mascota.getNumeroFicha() != null && mascota.getNumeroFicha() > 0 ? "FCH-" + String.format("%03d", mascota.getNumeroFicha()) : "Sin Ficha";
                    Label lblDetalle = new Label(mascota.getRaza() + " • " + fichaStr);
                    lblDetalle.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");
                    
                    info.getChildren().addAll(header, lblDetalle);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Botones de acción directos en la tarjeta
                    Button btnEditar = new Button(" Editar");
                    btnEditar.getStyleClass().addAll("btn-outline", "btn-action-small");
                    btnEditar.setGraphic(new FontIcon("fas-pen"));
                    btnEditar.setOnAction(e -> {
                        listaMascotas.getSelectionModel().select(mascota);
                        abrirModalEdicionMascota(mascota);
                    });

                    Button btnEliminar = new Button(" Eliminar");
                    btnEliminar.getStyleClass().addAll("btn-outline", "btn-action-small");
                    btnEliminar.setStyle("-fx-text-fill: #EF4444; -fx-border-color: #FCA5A5;");
                    FontIcon iconTrash = new FontIcon("fas-trash-alt"); iconTrash.setIconColor(Color.web("#EF4444"));
                    btnEliminar.setGraphic(iconTrash);
                    btnEliminar.setOnAction(e -> eliminarMascotaEspecifica(mascota));

                    HBox actions = new HBox(10, btnEditar, btnEliminar);
                    actions.setAlignment(Pos.CENTER);

                    card.getChildren().addAll(avatar, info, spacer, actions);
                    setGraphic(card);
                }
            }
        });
    }

    private void cargarDatosYFiltro() {
        try {
            List<ClienteDTO> todosLosClientes = clienteService.obtenerTodos();
            if (todosLosClientes != null) masterDataClientes.addAll(todosLosClientes);

            FilteredList<ClienteDTO> filteredData = new FilteredList<>(masterDataClientes, p -> true);
            if (txtBuscar != null) {
                txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredData.setPredicate(cliente -> {
                        if (newValue == null || newValue.isEmpty()) return true;
                        String lower = newValue.toLowerCase();
                        if (cliente.getNombre().toLowerCase().contains(lower)) return true;
                        if (cliente.getApellido().toLowerCase().contains(lower)) return true;
                        if (cliente.getDni() != null && cliente.getDni().toLowerCase().contains(lower)) return true;
                        return false;
                    });
                });
            }
            listaClientes.setItems(filteredData);
            actualizarContadorClientes();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarMascotasDelCliente(Long idCliente) {
        try {
            List<MascotaDTO> mascotas = mascotaService.obtenerTodas().stream()
                    .filter(m -> m.getIdCliente() != null && m.getIdCliente().equals(idCliente))
                    .collect(Collectors.toList());
            listaMascotas.getItems().setAll(mascotas);
            lblCantidadMascotas.setText(String.valueOf(mascotas.size()));
            listaClientes.refresh(); // Refresca la lista para actualizar el contador de "X mascotas" en la tarjeta
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void actualizarContadorClientes() {
        lblCantidadClientes.setText("CLIENTES (" + masterDataClientes.size() + ")");
    }

    @FXML 
    public void limpiarFormularioCliente() {
        listaClientes.getSelectionModel().clearSelection();
        lblNombrePerfil.setText("Nuevo Cliente");
        lblDniPerfil.setText("DNI: -");
        txtNombreCliente.clear();
        txtApellidoCliente.clear();
        txtDniCliente.clear();
        txtTelefonoCliente.clear();
        txtEmailCliente.clear();
        listaMascotas.getItems().clear();
        lblCantidadMascotas.setText("0");
    }

    @FXML 
    private void guardarCliente() {
        ClienteDTO clienteDTO = new ClienteDTO(0L, txtNombreCliente.getText(), txtApellidoCliente.getText(), txtDniCliente.getText(), txtTelefonoCliente.getText(), txtEmailCliente.getText());
        try {
            ClienteDTO guardado = clienteService.guardarCliente(clienteDTO);
            masterDataClientes.add(guardado);
            actualizarContadorClientes();
            listaClientes.getSelectionModel().select(guardado);
            mostrarAlerta("Éxito", "Cliente registrado correctamente.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException e) { mostrarAlerta("Atención", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) { mostrarAlerta("Error", "Ocurrió un error en la base de datos.", Alert.AlertType.ERROR); }
    }

    @FXML 
    private void modificarCliente() {
        ClienteDTO clienteSeleccionado = listaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) { mostrarAlerta("Atención", "Seleccione un cliente de la lista.", Alert.AlertType.WARNING); return; }
        try {
            clienteSeleccionado.setNombre(txtNombreCliente.getText());
            clienteSeleccionado.setApellido(txtApellidoCliente.getText());
            clienteSeleccionado.setDni(txtDniCliente.getText());
            clienteSeleccionado.setTelefono(txtTelefonoCliente.getText());
            clienteSeleccionado.setEmail(txtEmailCliente.getText());

            clienteService.actualizarCliente(clienteSeleccionado);
            listaClientes.refresh();
            lblNombrePerfil.setText(clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido());
            lblDniPerfil.setText("DNI: " + clienteSeleccionado.getDni());
            mostrarAlerta("Éxito", "Cliente modificado.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException e) { mostrarAlerta("Atención", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) { mostrarAlerta("Error", "No se pudo modificar el cliente.", Alert.AlertType.ERROR); }
    }

    @FXML 
    private void eliminarCliente() {
        ClienteDTO clienteSeleccionado = listaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) { mostrarAlerta("Atención", "Seleccione un cliente para eliminar.", Alert.AlertType.WARNING); return; }
        try {
            clienteService.darDeBaja(clienteSeleccionado.getId());
            masterDataClientes.remove(clienteSeleccionado);
            actualizarContadorClientes();
            limpiarFormularioCliente();
            mostrarAlerta("Éxito", "El cliente fue dado de baja.", Alert.AlertType.INFORMATION);
        } catch (Exception e) { mostrarAlerta("Error", "Error al intentar eliminar el cliente.", Alert.AlertType.ERROR); }
    }

    // ==========================================
    // MODAL Y CRUD MASCOTAS
    // ==========================================
    @FXML
    public void abrirModalMascota() {
        ClienteDTO clienteSeleccionado = listaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) { 
            mostrarAlerta("Atención", "Debe seleccionar un dueño de la lista izquierda primero.", Alert.AlertType.WARNING); 
            return; 
        }
        idMascotaEnEdicion = null;
        lblTituloModalMascota.setText("Agregar Mascota");
        txtNombreMascota.clear(); txtEspecie.clear(); txtRaza.clear(); dpFechaNacimiento.setValue(null); txtNumeroFicha.clear();
        overlayMascota.setVisible(true);
    }

    private void abrirModalEdicionMascota(MascotaDTO mascota) {
        idMascotaEnEdicion = mascota.getId();
        lblTituloModalMascota.setText("Editar Mascota");
        txtNombreMascota.setText(mascota.getNombreMascota());
        txtEspecie.setText(mascota.getEspecie());
        txtRaza.setText(mascota.getRaza());
        dpFechaNacimiento.setValue(mascota.getFechaNacimiento());
        txtNumeroFicha.setText(mascota.getNumeroFicha() != null && mascota.getNumeroFicha() > 0 ? String.valueOf(mascota.getNumeroFicha()) : "");
        overlayMascota.setVisible(true);
    }

    @FXML
    public void cerrarModalMascota() {
        overlayMascota.setVisible(false);
        idMascotaEnEdicion = null;
    }

    @FXML 
    private void guardarMascota() {
        ClienteDTO clienteSeleccionado = listaClientes.getSelectionModel().getSelectedItem();
        Long nroFicha = 0L;
        if (txtNumeroFicha.getText() != null && !txtNumeroFicha.getText().trim().isEmpty()) {
            try { nroFicha = Long.valueOf(txtNumeroFicha.getText()); 
            } catch (NumberFormatException e) { mostrarAlerta("Error", "El número de ficha debe contener únicamente números.", Alert.AlertType.WARNING); return; }
        }
        
        MascotaDTO dto = new MascotaDTO(
            idMascotaEnEdicion != null ? idMascotaEnEdicion : 0L, 
            txtNombreMascota.getText(), txtEspecie.getText(), txtRaza.getText(), 
            dpFechaNacimiento.getValue(), clienteSeleccionado.getId(), "", nroFicha
        );

        try {
            if (idMascotaEnEdicion == null) {
                mascotaService.guardarMascota(dto);
                mostrarAlerta("Éxito", "Mascota guardada correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mascotaService.actualizarMascota(dto);
                mostrarAlerta("Éxito", "Mascota modificada correctamente.", Alert.AlertType.INFORMATION);
            }
            cargarMascotasDelCliente(clienteSeleccionado.getId());
            cerrarModalMascota();
        } catch (IllegalArgumentException e) { mostrarAlerta("Atención", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) { mostrarAlerta("Error", "Ocurrió un error al guardar la mascota.", Alert.AlertType.ERROR); }
    }

    private void eliminarMascotaEspecifica(MascotaDTO mascota) {
        try {
            mascotaService.eliminarMascota(mascota.getId());
            ClienteDTO clienteSeleccionado = listaClientes.getSelectionModel().getSelectedItem();
            if (clienteSeleccionado != null) cargarMascotasDelCliente(clienteSeleccionado.getId());
            mostrarAlerta("Éxito", "Mascota eliminada de la base de datos.", Alert.AlertType.INFORMATION);
        } catch (Exception e) { mostrarAlerta("Error", "Error al intentar eliminar la mascota.", Alert.AlertType.ERROR); }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo); alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(mensaje); alert.showAndWait();
    }
}