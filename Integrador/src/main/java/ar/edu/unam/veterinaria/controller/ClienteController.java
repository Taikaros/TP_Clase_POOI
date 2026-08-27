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

import java.time.LocalDate;
import java.util.function.UnaryOperator;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteController {

    @FXML private TextField txtBuscar;
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtApellidoCliente;
    @FXML private TextField txtDniCliente;
    @FXML private TextField txtTelefonoCliente;
    @FXML private TextField txtEmailCliente;
    @FXML private Button btnGuardarMascota;

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
        // --- 1. FILTRO PARA NÚMEROS (DNI, Teléfono, Ficha) ---
        UnaryOperator<TextFormatter.Change> filtroNumeros = change -> {
            if (change.getControlNewText().matches("[0-9]*")) return change;
            return null; 
        };

        txtDniCliente.setTextFormatter(new TextFormatter<>(filtroNumeros));
        txtTelefonoCliente.setTextFormatter(new TextFormatter<>(filtroNumeros));
        txtNumeroFicha.setTextFormatter(new TextFormatter<>(filtroNumeros));
        
        // --- 2. FILTRO PARA NOMBRES (Letras y Autocapitalización) ---
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
        
        txtNombreCliente.setTextFormatter(new TextFormatter<>(filtroNombres));
        txtApellidoCliente.setTextFormatter(new TextFormatter<>(filtroNombres));
        txtNombreMascota.setTextFormatter(new TextFormatter<>(filtroNombres));
        txtRaza.setTextFormatter(new TextFormatter<>(filtroNombres));

        // --- 3. FILTRO Y BLOQUEO PARA FECHAS ---
        UnaryOperator<TextFormatter.Change> filtroFecha = change -> {
            if (change.getControlNewText().matches("[0-9/]*")) return change;
            return null;
        };
        dpFechaNacimiento.getEditor().setTextFormatter(new TextFormatter<>(filtroFecha));

        dpFechaNacimiento.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #9ca3af;");
                }
            }
        });

        dpFechaNacimiento.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isAfter(LocalDate.now())) {
                dpFechaNacimiento.setValue(oldVal); 
                mostrarAlerta("Fecha Inválida", "La fecha de nacimiento no puede ser futura.", Alert.AlertType.WARNING);
            }
        });

        // Formateador visual del DatePicker a dd/MM/yyyy
        dpFechaNacimiento.setConverter(new javafx.util.StringConverter<LocalDate>() {
            private final java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override public String toString(LocalDate date) { return date != null ? formatter.format(date) : ""; }
            @Override public LocalDate fromString(String string) { return (string == null || string.isEmpty()) ? null : LocalDate.parse(string, formatter); }
        });

        btnGuardarMascota.disableProperty().bind(
            txtNombreMascota.textProperty().isEmpty()
            .or(txtEspecie.textProperty().isEmpty())
            .or(txtRaza.textProperty().isEmpty()
            .or(txtNumeroFicha.textProperty().isEmpty()))
            .or(dpFechaNacimiento.valueProperty().isNull())
        );
        // 4. Ejecución normal de la pantalla
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
                    
                    // Novedad: Agregamos el DNI y el Teléfono a la vista rápida de la tarjeta
                    String dni = cliente.getDni() != null && !cliente.getDni().isEmpty() ? cliente.getDni() : "-";
                    String tel = cliente.getTelefono() != null && !cliente.getTelefono().isEmpty() ? cliente.getTelefono() : "-";
                    Label lblDatosInfo = new Label("DNI: " + dni + " | Tel: " + tel);
                    lblDatosInfo.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
                    
                    long cantMascotas = mascotaService.obtenerTodas().stream().filter(m -> m.getIdCliente() != null && m.getIdCliente().equals(cliente.getId())).count();
                    Label lblMascotas = new Label(cantMascotas + (cantMascotas == 1 ? " mascota" : " mascotas"));
                    lblMascotas.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px; -fx-font-weight: bold;");
                    
                    info.getChildren().addAll(lblNombre, lblDatosInfo, lblMascotas);
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
            listaClientes.refresh(); 
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

    // --- NUEVA LÓGICA DE GUARDADO INTELIGENTE (Crea o Edita según el contexto) ---
    @FXML 
    private void guardarCliente() {
        if (txtNombreCliente.getText().trim().isEmpty() || 
            txtApellidoCliente.getText().trim().isEmpty() || 
            txtDniCliente.getText().trim().isEmpty() || 
            txtTelefonoCliente.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Por favor, complete al menos Nombre, Apellido, DNI y Teléfono.", Alert.AlertType.WARNING);
            return;
        }
        ClienteDTO clienteSeleccionado = listaClientes.getSelectionModel().getSelectedItem();

        try {
            if (clienteSeleccionado == null) {
                // --- MODO CREACIÓN ---
                ClienteDTO nuevoCliente = new ClienteDTO(0L, txtNombreCliente.getText().trim(), txtApellidoCliente.getText().trim(), txtDniCliente.getText().trim(), txtTelefonoCliente.getText().trim(), txtEmailCliente.getText().trim());
                ClienteDTO guardado = clienteService.guardarCliente(nuevoCliente);
                masterDataClientes.add(guardado);
                actualizarContadorClientes();
                listaClientes.getSelectionModel().select(guardado);
                mostrarAlerta("Éxito", "Cliente registrado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                // --- MODO EDICIÓN ---
                clienteSeleccionado.setNombre(txtNombreCliente.getText().trim());
                clienteSeleccionado.setApellido(txtApellidoCliente.getText().trim());
                clienteSeleccionado.setDni(txtDniCliente.getText().trim());
                clienteSeleccionado.setTelefono(txtTelefonoCliente.getText().trim());
                clienteSeleccionado.setEmail(txtEmailCliente.getText().trim());

                clienteService.actualizarCliente(clienteSeleccionado);
                
                listaClientes.refresh();
                lblNombrePerfil.setText(clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido());
                lblDniPerfil.setText("DNI: " + clienteSeleccionado.getDni());
                mostrarAlerta("Éxito", "Cliente modificado correctamente.", Alert.AlertType.INFORMATION);
            }
        } catch (IllegalArgumentException e) { 
            mostrarAlerta("Datos Inválidos", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) { 
            mostrarAlerta("Error", "Ocurrió un error en la base de datos.", Alert.AlertType.ERROR); 
        }
    }

    @FXML 
    private void modificarCliente() {
        ar.edu.unam.veterinaria.dto.ClienteDTO clienteSeleccionado = listaClientes.getSelectionModel().getSelectedItem();
        if(listaClientes.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Atención", "Seleccione un cliente de la lista.", Alert.AlertType.WARNING);
            return;
        }
        txtNombreCliente.requestFocus();
        
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
            mostrarAlerta("Éxito", "Cliente modificado correctamente.", javafx.scene.control.Alert.AlertType.INFORMATION);
            
        } catch (IllegalArgumentException e) { 
            mostrarAlerta("Datos Inválidos", e.getMessage(), javafx.scene.control.Alert.AlertType.WARNING);
        } catch (Exception e) { 
            mostrarAlerta("Error", "No se pudo modificar el cliente.", javafx.scene.control.Alert.AlertType.ERROR); 
            e.printStackTrace();
        }
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
            mostrarAlerta("Atención", "Debe seleccionar o guardar un dueño en la lista primero.", Alert.AlertType.WARNING); 
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
        ar.edu.unam.veterinaria.dto.ClienteDTO clienteSeleccionado = listaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) {
            mostrarAlerta("Atención", "Debe seleccionar un dueño de la lista primero.", javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }
        
        try {
            Long nroFicha = null; // Iniciamos en null para activar la autogeneración
            String textoFicha = txtNumeroFicha.getText();
            
            // Si el usuario escribió un número manualmente o está editando, lo usamos
            if (textoFicha != null && !textoFicha.trim().isEmpty()) {
                nroFicha = Long.valueOf(textoFicha.trim()); 
            }
            
            ar.edu.unam.veterinaria.dto.MascotaDTO dto = new ar.edu.unam.veterinaria.dto.MascotaDTO(
                idMascotaEnEdicion != null ? idMascotaEnEdicion : 0L, 
                txtNombreMascota.getText(), txtEspecie.getText(), txtRaza.getText(), 
                dpFechaNacimiento.getValue(), clienteSeleccionado.getId(), "", nroFicha
            );

            if (idMascotaEnEdicion == null) {
                mascotaService.guardarMascota(dto);
                mostrarAlerta("Éxito", "Mascota guardada correctamente.", javafx.scene.control.Alert.AlertType.INFORMATION);
            } else {
                mascotaService.actualizarMascota(dto);
                mostrarAlerta("Éxito", "Mascota modificada correctamente.", javafx.scene.control.Alert.AlertType.INFORMATION);
            }
            cargarMascotasDelCliente(clienteSeleccionado.getId());
            cerrarModalMascota();
            
        } catch (NumberFormatException e) { 
            mostrarAlerta("Error de Formato", "El número de ficha debe contener únicamente números.", javafx.scene.control.Alert.AlertType.WARNING); 
        } catch (IllegalArgumentException e) { 
            mostrarAlerta("Datos Inválidos", e.getMessage(), javafx.scene.control.Alert.AlertType.WARNING);
        } catch (Exception e) { 
            mostrarAlerta("Error", "Ocurrió un error al guardar la mascota.", javafx.scene.control.Alert.AlertType.ERROR); 
            e.printStackTrace();
        }
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