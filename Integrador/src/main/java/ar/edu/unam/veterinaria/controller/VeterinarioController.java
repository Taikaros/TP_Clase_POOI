package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.dto.EspecialidadDTO;
import ar.edu.unam.veterinaria.dto.VeterinarioDTO;
import ar.edu.unam.veterinaria.service.EspecialidadService;
import ar.edu.unam.veterinaria.service.VeterinarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class VeterinarioController {

    // --- VARIABLES DE ESTADO ---
    private Long idVeterinarioEnEdicion = null;
    private List<VeterinarioDTO> masterDataVeterinarios = new ArrayList<>(); 

    // Contenedores Principales
    @FXML private StackPane overlayEspecialidades;
    @FXML private HBox overlayFormulario;
    @FXML private FlowPane gridVeterinarios;
    @FXML private Label lblContador;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbFiltroEspecialidad;
    
    // Formularios Profesional
    @FXML private Label lblTituloFormulario;
    @FXML private Button btnGuardarFormulario;
    @FXML private TextField txtVetNombre;
    @FXML private TextField txtVetApellido;
    @FXML private TextField txtVetTelefono;
    @FXML private TextField txtVetMatricula;
    @FXML private TextField txtVetEmail;
    @FXML private FlowPane panelEspecialidades;
    
    // Horarios
    @FXML private CheckBox chkLun, chkMar, chkMie, chkJue, chkVie, chkSab, chkDom;
    @FXML private TextField txtHoraLun, txtHoraMar, txtHoraMie, txtHoraJue, txtHoraVie, txtHoraSab, txtHoraDom;

    // Formularios Especialidad
    @FXML private TextField txtNuevaEspNombre;
    @FXML private TextField txtNuevaEspDesc;
    @FXML private TableView<EspecialidadDTO> tablaEspecialidades;
    @FXML private TableColumn<EspecialidadDTO, String> colEspNombre;
    @FXML private TableColumn<EspecialidadDTO, String> colEspDesc;

    private VeterinarioService vetService = new VeterinarioService();
    private EspecialidadService espService = new EspecialidadService();

    @FXML
    public void initialize() {
        // --- 1. FILTROS DE NOMBRES Y TELÉFONO ---
        UnaryOperator<TextFormatter.Change> filtroNombres = change -> {
            if (!change.getControlNewText().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]*")) return null; 
            if (change.isAdded()) {
                String textoInsertado = change.getText(); 
                String textoControl = change.getControlText(); 
                int pos = change.getRangeStart(); 
                StringBuilder modificado = new StringBuilder();
                boolean hacerMayuscula = (pos == 0 || textoControl.charAt(pos - 1) == ' ');

                for (char c : textoInsertado.toCharArray()) {
                    if (c == ' ') {
                        hacerMayuscula = true;
                        modificado.append(c);
                    } else if (hacerMayuscula) {
                        modificado.append(Character.toUpperCase(c));
                        hacerMayuscula = false; 
                    } else {
                        modificado.append(Character.toLowerCase(c));
                    }
                }
                change.setText(modificado.toString());
            }
            return change;
        };

        UnaryOperator<TextFormatter.Change> filtroNumeros = change -> {
            if (change.getControlNewText().matches("[0-9]*")) return change;
            return null;
        };

        txtVetNombre.setTextFormatter(new TextFormatter<>(filtroNombres));
        txtVetApellido.setTextFormatter(new TextFormatter<>(filtroNombres));
        txtNuevaEspNombre.setTextFormatter(new TextFormatter<>(filtroNombres));
        txtVetTelefono.setTextFormatter(new TextFormatter<>(filtroNumeros));

        // --- 2. ASIGNACIÓN DE LA MÁSCARA INTELIGENTE A LOS HORARIOS ---
        aplicarMascaraHora(txtHoraLun); aplicarMascaraHora(txtHoraMar);
        aplicarMascaraHora(txtHoraMie); aplicarMascaraHora(txtHoraJue);
        aplicarMascaraHora(txtHoraVie); aplicarMascaraHora(txtHoraSab);
        aplicarMascaraHora(txtHoraDom);

        // --- 3. BLOQUEO INTELIGENTE DEL BOTÓN "GUARDAR" ---
        // Se bloquea si faltan datos obligatorios O si no hay NINGÚN DÍA seleccionado
        btnGuardarFormulario.disableProperty().bind(
            txtVetNombre.textProperty().isEmpty()
            .or(txtVetApellido.textProperty().isEmpty())
            .or(txtVetMatricula.textProperty().isEmpty())
            .or(
                chkLun.selectedProperty().not()
                .and(chkMar.selectedProperty().not())
                .and(chkMie.selectedProperty().not())
                .and(chkJue.selectedProperty().not())
                .and(chkVie.selectedProperty().not())
                .and(chkSab.selectedProperty().not())
                .and(chkDom.selectedProperty().not())
            )
        );

        // Configuración de tabla y combo
        colEspNombre.setCellValueFactory(new PropertyValueFactory<>("nombreEspecialidad"));
        colEspDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        cbFiltroEspecialidad.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

        cargarDirectorio();
    }

    // ==========================================
    // MÉTODOS DE VALIDACIÓN Y MÁSCARAS DE HORA
    // ==========================================
    
    private void aplicarMascaraHora(TextField txt) {
        // Bloqueo estricto: Solo números, dos puntos, guión y máximo 11 caracteres.
        txt.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 11) return null;
            if (!change.getControlNewText().matches("[0-9:\\-]*")) return null;
            return change;
        }));

        // Inserción automática de caracteres al tipear
        txt.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > oldVal.length()) { // Solo se activa si el usuario está agregando texto
                if (newVal.length() == 2 && !newVal.contains(":")) {
                    txt.setText(newVal + ":");
                } else if (newVal.length() == 5 && !newVal.contains("-")) {
                    txt.setText(newVal + "-");
                } else if (newVal.length() == 8 && newVal.chars().filter(ch -> ch == ':').count() == 1) {
                    txt.setText(newVal + ":");
                }
            }
        });
    }

    private boolean esHorarioValido(String horario) {
        if (horario == null || horario.length() != 11) return false;
        
        // Expresión Regular estricta que impide cosas como 90:78 o 26:88
        if (!horario.matches("^([01][0-9]|2[0-3]):[0-5][0-9]-([01][0-9]|2[0-3]):[0-5][0-9]$")) {
            return false;
        }

        try {
            // Dividimos y comprobamos que la salida sea MAYOR a la entrada
            String[] partes = horario.split("-");
            LocalTime entrada = LocalTime.parse(partes[0]);
            LocalTime salida = LocalTime.parse(partes[1]);
            
            return salida.isAfter(entrada);
        } catch (Exception e) {
            return false;
        }
    }

    // ==========================================

    private void cargarDirectorio() {
        List<String> nombresEsp = new ArrayList<>();
        nombresEsp.add("Todas las especialidades");
        nombresEsp.addAll(espService.obtenerTodas().stream().map(EspecialidadDTO::getNombreEspecialidad).collect(Collectors.toList()));
        
        String seleccionActual = cbFiltroEspecialidad.getValue();
        cbFiltroEspecialidad.setItems(FXCollections.observableArrayList(nombresEsp));
        if (seleccionActual != null && nombresEsp.contains(seleccionActual)) {
            cbFiltroEspecialidad.setValue(seleccionActual);
        } else {
            cbFiltroEspecialidad.getSelectionModel().selectFirst();
        }

        masterDataVeterinarios = vetService.obtenerTodos();
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        gridVeterinarios.getChildren().clear();
        
        String textoBusqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase() : "";
        String espFiltro = cbFiltroEspecialidad.getValue();

        List<VeterinarioDTO> filtrados = masterDataVeterinarios.stream().filter(vet -> {
            boolean coincideTexto = true;
            if (!textoBusqueda.isEmpty()) {
                String nombreCompleto = (vet.getNombre() + " " + vet.getApellido() + " " + vet.getMatricula()).toLowerCase();
                coincideTexto = nombreCompleto.contains(textoBusqueda);
            }
            
            boolean coincideEsp = true;
            if (espFiltro != null && !espFiltro.equals("Todas las especialidades")) {
                coincideEsp = vet.getEspecialidades().stream()
                                 .anyMatch(e -> e.getNombreEspecialidad().equals(espFiltro));
            }
            return coincideTexto && coincideEsp;
        }).collect(Collectors.toList());

        lblContador.setText(filtrados.size() + " profesionales");

        for (VeterinarioDTO vet : filtrados) {
            gridVeterinarios.getChildren().add(crearTarjeta(vet));
        }
    }

    private VBox crearTarjeta(VeterinarioDTO vet) {
        VBox card = new VBox(15);
        card.getStyleClass().add("vet-card");
        card.setPrefWidth(340);

        HBox topBox = new HBox(15);
        topBox.setAlignment(Pos.CENTER_LEFT);

        String iniciales = obtenerIniciales(vet.getNombre(), vet.getApellido());
        String[] temaAvatar = obtenerTemaColorAvatar(vet.getNombre() + vet.getApellido());
        StackPane avatar = new StackPane();
        avatar.getStyleClass().addAll("avatar-circle-large", temaAvatar[0]);
        Label lblIniciales = new Label(iniciales);
        lblIniciales.getStyleClass().addAll("avatar-text", temaAvatar[1]);
        avatar.getChildren().add(lblIniciales);

        VBox infoBox = new VBox(3);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        Label lblNombre = new Label("Dr./Dra. " + vet.getNombre() + " " + vet.getApellido());
        lblNombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: -color-texto-oscuro;");
        
        HBox matriculaBox = new HBox(5);
        matriculaBox.setAlignment(Pos.CENTER_LEFT);
        FontIcon iconMatricula = new FontIcon("fas-award");
        iconMatricula.setIconColor(Color.web("#D2B48C"));
        Label lblMatricula = new Label("MV " + vet.getMatricula());
        lblMatricula.setStyle("-fx-text-fill: -color-texto-gris; -fx-font-size: 12px;");
        matriculaBox.getChildren().addAll(iconMatricula, lblMatricula);
        
        infoBox.getChildren().addAll(lblNombre, matriculaBox);

        MenuButton menu = new MenuButton();
        menu.getStyleClass().add("kebab-menu");
        FontIcon iconMenu = new FontIcon("fas-ellipsis-v");
        iconMenu.setIconColor(Color.web("#94A3B8"));
        menu.setGraphic(iconMenu);
        
        MenuItem itemEditar = new MenuItem("Editar profesional");
        itemEditar.setOnAction(e -> abrirEdicion(vet)); 
        MenuItem itemEliminar = new MenuItem("Eliminar");
        itemEliminar.getStyleClass().add("menu-item-danger");
        itemEliminar.setOnAction(e -> eliminarVeterinario(vet)); 
        menu.getItems().addAll(itemEditar, itemEliminar);

        topBox.getChildren().addAll(avatar, infoBox, menu);

        StackPane divider1 = new StackPane();
        divider1.getStyleClass().add("nav-divider");
        divider1.setMaxHeight(1);

        FlowPane pillsBox = new FlowPane(8, 8);
        if (vet.getEspecialidades() != null && !vet.getEspecialidades().isEmpty()) {
            for (EspecialidadDTO esp : vet.getEspecialidades()) {
                Label pill = new Label(esp.getNombreEspecialidad());
                pill.getStyleClass().addAll("pill", obtenerColorPill(esp.getNombreEspecialidad()));
                pillsBox.getChildren().add(pill);
            }
        } else {
            Label noEsp = new Label("Sin especialidad");
            noEsp.setStyle("-fx-text-fill: #94A3B8; -fx-font-style: italic;");
            pillsBox.getChildren().add(noEsp);
        }

        StackPane divider2 = new StackPane();
        divider2.getStyleClass().add("nav-divider");
        divider2.setMaxHeight(1);

        VBox bottomBox = new VBox(8);
        Label lblTel = new Label(vet.getTelefono() != null && !vet.getTelefono().isEmpty() ? vet.getTelefono() : "Sin teléfono registrado");
        lblTel.setStyle("-fx-text-fill: -color-texto-gris;");
        FontIcon iconTel = new FontIcon("fas-phone-alt");
        iconTel.setIconColor(Color.web("#94A3B8"));
        lblTel.setGraphic(iconTel);

        Label lblEmail = new Label(vet.getEmail() != null && !vet.getEmail().isEmpty() ? vet.getEmail() : "Sin email registrado");
        lblEmail.setStyle("-fx-text-fill: -color-texto-gris;");
        FontIcon iconEmail = new FontIcon("fas-envelope");
        iconEmail.setIconColor(Color.web("#94A3B8"));
        lblEmail.setGraphic(iconEmail);
        bottomBox.getChildren().addAll(lblTel, lblEmail);

        card.getChildren().addAll(topBox, divider1, pillsBox, divider2, bottomBox);

        if (vet.getDiasDisponibles() != null && !vet.getDiasDisponibles().isEmpty()) {
            StackPane divider3 = new StackPane();
            divider3.getStyleClass().add("nav-divider");
            divider3.setMaxHeight(1);
            
            VBox horariosBox = new VBox(8);
            Label lblHorarios = new Label("HORARIOS");
            lblHorarios.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: -color-texto-gris;");
            
            FlowPane fpHorarios = new FlowPane(5, 5);
            for (String horario : vet.getDiasDisponibles()) {
                Label lblDia = new Label(horario);
                lblDia.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: -color-texto-oscuro; -fx-padding: 3 6; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");
                fpHorarios.getChildren().add(lblDia);
            }
            horariosBox.getChildren().addAll(lblHorarios, fpHorarios);
            card.getChildren().addAll(divider3, horariosBox);
        }

        return card;
    }

    private void abrirEdicion(VeterinarioDTO vet) {
        idVeterinarioEnEdicion = vet.getId();
        lblTituloFormulario.setText("Editar Profesional");
        btnGuardarFormulario.setText(" Guardar Cambios");

        txtVetNombre.setText(vet.getNombre());
        txtVetApellido.setText(vet.getApellido());
        txtVetMatricula.setText(vet.getMatricula());
        txtVetTelefono.setText(vet.getTelefono());
        txtVetEmail.setText(vet.getEmail());

        cargarEspecialidadesCheckbox();
        for (javafx.scene.Node nodo : panelEspecialidades.getChildren()) {
            if (nodo instanceof ToggleButton) {
                ToggleButton btn = (ToggleButton) nodo;
                Long idBtn = (Long) btn.getUserData();
                boolean tieneEsp = vet.getEspecialidades().stream().anyMatch(e -> e.getId().equals(idBtn));
                btn.setSelected(tieneEsp); 
            }
        }

        limpiarHorarios();
        if (vet.getDiasDisponibles() != null) {
            for (String horario : vet.getDiasDisponibles()) {
                if (horario.startsWith("Lun:")) { chkLun.setSelected(true); txtHoraLun.setText(horario.replace("Lun: ", "").trim()); }
                if (horario.startsWith("Mar:")) { chkMar.setSelected(true); txtHoraMar.setText(horario.replace("Mar: ", "").trim()); }
                if (horario.startsWith("Mié:")) { chkMie.setSelected(true); txtHoraMie.setText(horario.replace("Mié: ", "").trim()); }
                if (horario.startsWith("Jue:")) { chkJue.setSelected(true); txtHoraJue.setText(horario.replace("Jue: ", "").trim()); }
                if (horario.startsWith("Vie:")) { chkVie.setSelected(true); txtHoraVie.setText(horario.replace("Vie: ", "").trim()); }
                if (horario.startsWith("Sáb:")) { chkSab.setSelected(true); txtHoraSab.setText(horario.replace("Sáb: ", "").trim()); }
                if (horario.startsWith("Dom:")) { chkDom.setSelected(true); txtHoraDom.setText(horario.replace("Dom: ", "").trim()); }
            }
        }
        overlayFormulario.setVisible(true);
    }

    @FXML
    private void guardarVeterinario() {
        // Obtenemos los CheckBoxes y los TextFields en arreglos paralelos para validarlos más fácil
        CheckBox[] chks = {chkLun, chkMar, chkMie, chkJue, chkVie, chkSab, chkDom};
        TextField[] txts = {txtHoraLun, txtHoraMar, txtHoraMie, txtHoraJue, txtHoraVie, txtHoraSab, txtHoraDom};
        String[] nombresDias = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};

        List<String> horarios = new ArrayList<>();
        
        // Verificamos si los horarios que marcó son válidos
        for (int i = 0; i < 7; i++) {
            if (chks[i].isSelected()) {
                String horaStr = txts[i].getText().trim();
                
                if (!esHorarioValido(horaStr)) {
                    mostrarAlerta("Horario Inválido", "El horario del día " + nombresDias[i] + " es incorrecto.\nDebe ser en formato 24hs (ej. 09:00-17:00) y la salida debe ser posterior al ingreso.");
                    return; // Aborta el guardado si hay un error en alguna hora
                }
                horarios.add(nombresDias[i] + ": " + horaStr);
            }
        }

        List<Long> idsEspecialidadesSeleccionadas = new ArrayList<>();
        for (javafx.scene.Node nodo : panelEspecialidades.getChildren()) {
            if (nodo instanceof ToggleButton) {
                ToggleButton btn = (ToggleButton) nodo;
                if (btn.isSelected()) idsEspecialidadesSeleccionadas.add((Long) btn.getUserData());
            }
        }

        if (idsEspecialidadesSeleccionadas.isEmpty()) {
            mostrarAlerta("Falta Especialidad", "El profesional debe tener asignada al menos una especialidad.");
            return;
        }

        VeterinarioDTO dto = new VeterinarioDTO(
            idVeterinarioEnEdicion != null ? idVeterinarioEnEdicion : 0L, 
            txtVetNombre.getText().trim(), 
            txtVetApellido.getText().trim(), 
            txtVetTelefono.getText().trim(), 
            txtVetEmail.getText().trim(), 
            txtVetMatricula.getText().trim(), 
            null,
            horarios
        );

        if (idVeterinarioEnEdicion == null) {
            vetService.guardarVeterinario(dto, idsEspecialidadesSeleccionadas);
        } else {
            vetService.actualizarVeterinario(dto, idsEspecialidadesSeleccionadas);
        }
        
        cerrarModales();
        cargarDirectorio();
    }

    private void cargarEspecialidadesCheckbox() {
        panelEspecialidades.getChildren().clear();
        List<EspecialidadDTO> listaEsp = espService.obtenerTodas();
        for (EspecialidadDTO esp : listaEsp) {
            ToggleButton btnEsp = new ToggleButton(esp.getNombreEspecialidad());
            btnEsp.getStyleClass().add("pill-toggle");
            btnEsp.setUserData(esp.getId()); 
            panelEspecialidades.getChildren().add(btnEsp);
        }
    }

    private void cargarTablaEspecialidades() {
        List<EspecialidadDTO> listaEsp = espService.obtenerTodas();
        tablaEspecialidades.setItems(FXCollections.observableArrayList(listaEsp));
    }

    @FXML
    private void guardarEspecialidad() {
        String nombre = txtNuevaEspNombre.getText().trim();
        if (nombre.isEmpty()) return;

        boolean existe = tablaEspecialidades.getItems().stream()
            .anyMatch(e -> e.getNombreEspecialidad().equalsIgnoreCase(nombre));
        
        if (existe) {
            mostrarAlerta("Error", "La especialidad '" + nombre + "' ya existe en el catálogo.");
            return;
        }
        
        EspecialidadDTO dto = new EspecialidadDTO(0L, nombre, txtNuevaEspDesc.getText());
        espService.guardarEspecialidad(dto);
        
        txtNuevaEspNombre.clear();
        txtNuevaEspDesc.clear();
        cargarTablaEspecialidades(); 
        cargarDirectorio(); 
    }

    @FXML
    private void eliminarEspecialidadCatalogo() {
        EspecialidadDTO seleccionada = tablaEspecialidades.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            espService.eliminarEspecialidad(seleccionada.getId());
            cargarTablaEspecialidades();
            cargarDirectorio();
        } else {
            mostrarAlerta("Atención", "Seleccione una especialidad de la tabla para eliminarla.");
        }
    }

    private void eliminarVeterinario(VeterinarioDTO vet) {
        vetService.darDeBaja(vet.getId());
        cargarDirectorio(); 
    }

    @FXML
    private void abrirModalEspecialidades() {
        cargarTablaEspecialidades();
        overlayEspecialidades.setVisible(true);
    }

    @FXML
    private void abrirPanelFormulario() {
        idVeterinarioEnEdicion = null; 
        lblTituloFormulario.setText("Nuevo Profesional");
        btnGuardarFormulario.setText(" Guardar Profesional");
        
        txtVetNombre.clear();
        txtVetApellido.clear();
        txtVetMatricula.clear();
        txtVetTelefono.clear();
        txtVetEmail.clear();
        
        limpiarHorarios();
        cargarEspecialidadesCheckbox();
        overlayFormulario.setVisible(true);
    }

    private void limpiarHorarios() {
        if (chkLun != null) chkLun.setSelected(false);
        if (txtHoraLun != null) txtHoraLun.clear();
        if (chkMar != null) chkMar.setSelected(false);
        if (txtHoraMar != null) txtHoraMar.clear();
        if (chkMie != null) chkMie.setSelected(false);
        if (txtHoraMie != null) txtHoraMie.clear();
        if (chkJue != null) chkJue.setSelected(false);
        if (txtHoraJue != null) txtHoraJue.clear();
        if (chkVie != null) chkVie.setSelected(false);
        if (txtHoraVie != null) txtHoraVie.clear();
        if (chkSab != null) chkSab.setSelected(false);
        if (txtHoraSab != null) txtHoraSab.clear();
        if (chkDom != null) chkDom.setSelected(false);
        if (txtHoraDom != null) txtHoraDom.clear();
    }

    @FXML
    private void cerrarModales() {
        overlayEspecialidades.setVisible(false);
        overlayFormulario.setVisible(false);
        idVeterinarioEnEdicion = null;
    }

    private String obtenerIniciales(String nombre, String apellido) {
        String inicial = "";
        if (nombre != null && !nombre.isEmpty()) inicial += nombre.charAt(0);
        if (apellido != null && !apellido.isEmpty()) inicial += apellido.charAt(0);
        return inicial.toUpperCase();
    }

    private String[] obtenerTemaColorAvatar(String texto) {
        String[][] temas = {
            {"bg-pastel-blue", "text-blue"}, {"bg-pastel-green", "text-green"},
            {"bg-pastel-yellow", "text-yellow"}, {"bg-pastel-purple", "text-purple"},
            {"bg-pastel-red", "text-red"}
        };
        int index = Math.abs(texto.hashCode()) % temas.length;
        return temas[index];
    }

    private String obtenerColorPill(String texto) {
        String[] pills = {"pill-blue", "pill-green", "pill-yellow", "pill-purple", "pill-red"};
        int index = Math.abs(texto.hashCode()) % pills.length;
        return pills[index];
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}