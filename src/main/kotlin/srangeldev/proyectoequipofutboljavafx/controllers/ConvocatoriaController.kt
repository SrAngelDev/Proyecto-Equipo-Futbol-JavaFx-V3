package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.repository.ConvocatoriaRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRepository
import java.time.LocalDate

/**
 * Controlador para la gestión de convocatorias.
 */
class ConvocatoriaController : KoinComponent {
    private val logger = logging()
    
    // Inyectar los repositorios usando Koin
    private val convocatoriaRepository: ConvocatoriaRepository by inject()
    private val personalRepository: PersonalRepository by inject()
    
    // Lista observable de convocatorias
    private val convocatorias = FXCollections.observableArrayList<Convocatoria>()
    
    // Lista observable de jugadores convocados
    private val jugadoresConvocados = FXCollections.observableArrayList<Jugador>()
    
    // Convocatoria actual
    private var currentConvocatoria: Convocatoria? = null
    
    // Elementos de la interfaz para la lista de convocatorias
    @FXML
    private lateinit var searchConvocatoriaField: TextField
    
    @FXML
    private lateinit var convocatoriasTableView: TableView<Convocatoria>
    
    @FXML
    private lateinit var idConvocatoriaColumn: TableColumn<Convocatoria, Int>
    
    @FXML
    private lateinit var fechaConvocatoriaColumn: TableColumn<Convocatoria, String>
    
    @FXML
    private lateinit var descripcionConvocatoriaColumn: TableColumn<Convocatoria, String>
    
    @FXML
    private lateinit var jugadoresConvocatoriaColumn: TableColumn<Convocatoria, Int>
    
    @FXML
    private lateinit var titularesConvocatoriaColumn: TableColumn<Convocatoria, Int>
    
    // Elementos de la interfaz para los detalles de la convocatoria
    @FXML
    private lateinit var fechaConvocatoriaPicker: DatePicker
    
    @FXML
    private lateinit var entrenadorTextField: TextField
    
    @FXML
    private lateinit var descripcionTextArea: TextArea
    
    @FXML
    private lateinit var jugadoresCountLabel: Label
    
    @FXML
    private lateinit var titularesCountLabel: Label
    
    // Elementos de la interfaz para la tabla de jugadores convocados
    @FXML
    private lateinit var jugadoresConvocadosTableView: TableView<Jugador>
    
    @FXML
    private lateinit var idJugadorColumn: TableColumn<Jugador, Int>
    
    @FXML
    private lateinit var nombreJugadorColumn: TableColumn<Jugador, String>
    
    @FXML
    private lateinit var posicionJugadorColumn: TableColumn<Jugador, String>
    
    @FXML
    private lateinit var dorsalJugadorColumn: TableColumn<Jugador, Int>
    
    @FXML
    private lateinit var titularColumn: TableColumn<Jugador, Boolean>
    
    // Botones
    @FXML
    private lateinit var addConvocatoriaButton: Button
    
    @FXML
    private lateinit var editConvocatoriaButton: Button
    
    @FXML
    private lateinit var deleteConvocatoriaButton: Button
    
    @FXML
    private lateinit var printConvocatoriaButton: Button
    
    @FXML
    private lateinit var selectJugadoresButton: Button
    
    @FXML
    private lateinit var selectTitularesButton: Button
    
    @FXML
    private lateinit var saveConvocatoriaButton: Button
    
    @FXML
    private lateinit var cancelConvocatoriaButton: Button
    
    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     */
    @FXML
    private fun initialize() {
        logger.debug { "Inicializando ConvocatoriaController" }
        
        // Inicializar la tabla de convocatorias
        initializeConvocatoriasTable()
        
        // Inicializar la tabla de jugadores convocados
        initializeJugadoresConvocadosTable()
        
        // Configurar los eventos de los botones
        setupButtonEvents()
        
        // Configurar los eventos de la tabla de convocatorias
        setupTableViewEvents()
        
        // Cargar las convocatorias
        loadConvocatorias()
        
        // Configurar el campo de búsqueda
        setupSearchField()
        
        // Inicialmente, deshabilitar los botones de edición y eliminación
        editConvocatoriaButton.isDisable = true
        deleteConvocatoriaButton.isDisable = true
        printConvocatoriaButton.isDisable = true
        
        // Inicialmente, ocultar el panel de detalles
        clearDetailsPanel()
    }
    
    /**
     * Inicializa la tabla de convocatorias.
     */
    private fun initializeConvocatoriasTable() {
        // Configurar las columnas de la tabla
        idConvocatoriaColumn.cellValueFactory = PropertyValueFactory("id")
        
        fechaConvocatoriaColumn.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.fecha.toString())
        }
        
        descripcionConvocatoriaColumn.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.descripcion)
        }
        
        jugadoresConvocatoriaColumn.setCellValueFactory { cellData ->
            SimpleIntegerProperty(cellData.value.jugadores.size).asObject()
        }
        
        titularesConvocatoriaColumn.setCellValueFactory { cellData ->
            SimpleIntegerProperty(cellData.value.titulares.size).asObject()
        }
        
        // Asignar la lista observable a la tabla
        convocatoriasTableView.items = convocatorias
    }
    
    /**
     * Inicializa la tabla de jugadores convocados.
     */
    private fun initializeJugadoresConvocadosTable() {
        // Configurar las columnas de la tabla
        idJugadorColumn.cellValueFactory = PropertyValueFactory("id")
        
        nombreJugadorColumn.setCellValueFactory { cellData ->
            SimpleStringProperty("${cellData.value.nombre} ${cellData.value.apellidos}")
        }
        
        posicionJugadorColumn.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.posicion.toString())
        }
        
        dorsalJugadorColumn.setCellValueFactory { cellData ->
            SimpleIntegerProperty(cellData.value.dorsal).asObject()
        }
        
        titularColumn.setCellValueFactory { cellData ->
            val jugador = cellData.value
            val esTitular = currentConvocatoria?.titulares?.contains(jugador.id) ?: false
            javafx.beans.property.SimpleBooleanProperty(esTitular)
        }
        
        // Asignar la lista observable a la tabla
        jugadoresConvocadosTableView.items = jugadoresConvocados
    }
    
    /**
     * Configura los eventos de los botones.
     */
    private fun setupButtonEvents() {
        // Botón para añadir una nueva convocatoria
        addConvocatoriaButton.setOnAction {
            logger.debug { "Botón de nueva convocatoria presionado" }
            createNewConvocatoria()
        }
        
        // Botón para editar una convocatoria
        editConvocatoriaButton.setOnAction {
            logger.debug { "Botón de editar convocatoria presionado" }
            editConvocatoria()
        }
        
        // Botón para eliminar una convocatoria
        deleteConvocatoriaButton.setOnAction {
            logger.debug { "Botón de eliminar convocatoria presionado" }
            deleteConvocatoria()
        }
        
        // Botón para imprimir una convocatoria
        printConvocatoriaButton.setOnAction {
            logger.debug { "Botón de imprimir convocatoria presionado" }
            printConvocatoria()
        }
        
        // Botón para seleccionar jugadores
        selectJugadoresButton.setOnAction {
            logger.debug { "Botón de seleccionar jugadores presionado" }
            selectJugadores()
        }
        
        // Botón para seleccionar titulares
        selectTitularesButton.setOnAction {
            logger.debug { "Botón de seleccionar titulares presionado" }
            selectTitulares()
        }
        
        // Botón para guardar la convocatoria
        saveConvocatoriaButton.setOnAction {
            logger.debug { "Botón de guardar convocatoria presionado" }
            saveConvocatoria()
        }
        
        // Botón para cancelar la edición
        cancelConvocatoriaButton.setOnAction {
            logger.debug { "Botón de cancelar presionado" }
            clearDetailsPanel()
        }
    }
    
    /**
     * Configura los eventos de la tabla de convocatorias.
     */
    private fun setupTableViewEvents() {
        // Configurar el evento de selección de la tabla
        convocatoriasTableView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                logger.debug { "Convocatoria seleccionada: ${newValue.id}" }
                showConvocatoriaDetails(newValue)
                editConvocatoriaButton.isDisable = false
                deleteConvocatoriaButton.isDisable = false
                printConvocatoriaButton.isDisable = false
            } else {
                clearDetailsPanel()
                editConvocatoriaButton.isDisable = true
                deleteConvocatoriaButton.isDisable = true
                printConvocatoriaButton.isDisable = true
            }
        }
    }
    
    /**
     * Configura el campo de búsqueda.
     */
    private fun setupSearchField() {
        searchConvocatoriaField.textProperty().addListener { _, _, newValue ->
            filterConvocatorias(newValue)
        }
    }
    
    /**
     * Filtra las convocatorias según el texto de búsqueda.
     */
    private fun filterConvocatorias(searchText: String) {
        if (searchText.isEmpty()) {
            convocatoriasTableView.items = convocatorias
            return
        }
        
        val filteredList = convocatorias.filtered { convocatoria ->
            convocatoria.descripcion.contains(searchText, ignoreCase = true) ||
            convocatoria.fecha.toString().contains(searchText, ignoreCase = true)
        }
        
        convocatoriasTableView.items = filteredList
    }
    
    /**
     * Carga las convocatorias desde el repositorio.
     */
    private fun loadConvocatorias() {
        try {
            logger.debug { "Cargando convocatorias" }
            convocatorias.clear()
            convocatorias.addAll(convocatoriaRepository.getAll())
        } catch (e: Exception) {
            logger.error { "Error al cargar las convocatorias: ${e.message}" }
            showErrorDialog("Error", "Error al cargar las convocatorias: ${e.message}")
        }
    }
    
    /**
     * Muestra los detalles de una convocatoria.
     */
    private fun showConvocatoriaDetails(convocatoria: Convocatoria) {
        currentConvocatoria = convocatoria
        
        // Mostrar los datos de la convocatoria
        fechaConvocatoriaPicker.value = convocatoria.fecha
        descripcionTextArea.text = convocatoria.descripcion
        
        // Mostrar el entrenador
        val entrenador = personalRepository.getById(convocatoria.entrenadorId)
        if (entrenador is Entrenador) {
            entrenadorTextField.text = "${entrenador.nombre} ${entrenador.apellidos}"
        }
        
        // Cargar los jugadores convocados
        loadJugadoresConvocados(convocatoria)
        
        // Actualizar las etiquetas de conteo
        updateCountLabels()
        
        // Deshabilitar la edición
        setFieldsEditable(false)
    }
    
    /**
     * Carga los jugadores convocados para una convocatoria.
     */
    private fun loadJugadoresConvocados(convocatoria: Convocatoria) {
        jugadoresConvocados.clear()
        
        // Obtener los jugadores por sus IDs
        convocatoria.jugadores.forEach { jugadorId ->
            val personal = personalRepository.getById(jugadorId)
            if (personal is Jugador) {
                jugadoresConvocados.add(personal)
            }
        }
        
        // Actualizar la tabla
        jugadoresConvocadosTableView.refresh()
    }
    
    /**
     * Actualiza las etiquetas de conteo de jugadores y titulares.
     */
    private fun updateCountLabels() {
        val convocatoria = currentConvocatoria
        if (convocatoria != null) {
            jugadoresCountLabel.text = "${convocatoria.jugadores.size}/18 jugadores seleccionados"
            titularesCountLabel.text = "${convocatoria.titulares.size}/11 titulares seleccionados"
        } else {
            jugadoresCountLabel.text = "0/18 jugadores seleccionados"
            titularesCountLabel.text = "0/11 titulares seleccionados"
        }
    }
    
    /**
     * Crea una nueva convocatoria.
     */
    private fun createNewConvocatoria() {
        // Limpiar el panel de detalles
        clearDetailsPanel()
        
        // Crear una nueva convocatoria vacía
        val entrenadorPrincipal = personalRepository.getAll()
            .filterIsInstance<Entrenador>()
            .firstOrNull { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PRINCIPAL }
        
        if (entrenadorPrincipal == null) {
            showErrorDialog("Error", "No hay entrenador principal en el sistema")
            return
        }
        
        currentConvocatoria = Convocatoria(
            fecha = LocalDate.now(),
            descripcion = "",
            equipoId = 1, // Asumimos que hay un equipo con ID 1
            entrenadorId = entrenadorPrincipal.id
        )
        
        // Mostrar los datos de la convocatoria
        fechaConvocatoriaPicker.value = LocalDate.now()
        descripcionTextArea.text = ""
        entrenadorTextField.text = "${entrenadorPrincipal.nombre} ${entrenadorPrincipal.apellidos}"
        
        // Habilitar la edición
        setFieldsEditable(true)
        
        // Limpiar la tabla de jugadores
        jugadoresConvocados.clear()
        
        // Actualizar las etiquetas de conteo
        updateCountLabels()
    }
    
    /**
     * Edita una convocatoria existente.
     */
    private fun editConvocatoria() {
        // Habilitar la edición
        setFieldsEditable(true)
    }
    
    /**
     * Elimina una convocatoria.
     */
    private fun deleteConvocatoria() {
        val convocatoria = currentConvocatoria ?: return
        
        // Mostrar diálogo de confirmación
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.title = "Confirmar eliminación"
        alert.headerText = "¿Está seguro de que desea eliminar esta convocatoria?"
        alert.contentText = "Esta acción no se puede deshacer."
        
        val result = alert.showAndWait()
        if (result.isPresent && result.get() == ButtonType.OK) {
            try {
                // Eliminar la convocatoria
                convocatoriaRepository.delete(convocatoria.id)
                
                // Actualizar la lista
                convocatorias.remove(convocatoria)
                
                // Limpiar el panel de detalles
                clearDetailsPanel()
                
                // Mostrar mensaje de éxito
                showInfoDialog("Éxito", "Convocatoria eliminada correctamente")
            } catch (e: Exception) {
                logger.error { "Error al eliminar la convocatoria: ${e.message}" }
                showErrorDialog("Error", "Error al eliminar la convocatoria: ${e.message}")
            }
        }
    }
    
    /**
     * Imprime una convocatoria.
     */
    private fun printConvocatoria() {
        // TODO: Implementar la impresión de la convocatoria
        showInfoDialog("Información", "Funcionalidad de impresión no implementada")
    }
    
    /**
     * Selecciona los jugadores para la convocatoria.
     */
    private fun selectJugadores() {
        // TODO: Implementar la selección de jugadores
        showInfoDialog("Información", "Funcionalidad de selección de jugadores no implementada")
    }
    
    /**
     * Selecciona los titulares para la convocatoria.
     */
    private fun selectTitulares() {
        // TODO: Implementar la selección de titulares
        showInfoDialog("Información", "Funcionalidad de selección de titulares no implementada")
    }
    
    /**
     * Guarda la convocatoria.
     */
    private fun saveConvocatoria() {
        val convocatoria = currentConvocatoria ?: return
        
        // Validar los datos
        if (fechaConvocatoriaPicker.value == null) {
            showErrorDialog("Error", "Debe seleccionar una fecha")
            return
        }
        
        if (descripcionTextArea.text.isBlank()) {
            showErrorDialog("Error", "Debe ingresar una descripción")
            return
        }
        
        try {
            // Actualizar los datos de la convocatoria
            val updatedConvocatoria = convocatoria.copy(
                fecha = fechaConvocatoriaPicker.value,
                descripcion = descripcionTextArea.text
            )
            
            // Guardar la convocatoria
            val savedConvocatoria = if (convocatoria.id == 0) {
                convocatoriaRepository.save(updatedConvocatoria)
            } else {
                convocatoriaRepository.update(convocatoria.id, updatedConvocatoria) ?: return
            }
            
            // Actualizar la lista
            if (convocatoria.id == 0) {
                convocatorias.add(savedConvocatoria)
            } else {
                val index = convocatorias.indexOfFirst { it.id == savedConvocatoria.id }
                if (index >= 0) {
                    convocatorias[index] = savedConvocatoria
                }
            }
            
            // Actualizar la convocatoria actual
            currentConvocatoria = savedConvocatoria
            
            // Deshabilitar la edición
            setFieldsEditable(false)
            
            // Mostrar mensaje de éxito
            showInfoDialog("Éxito", "Convocatoria guardada correctamente")
        } catch (e: Exception) {
            logger.error { "Error al guardar la convocatoria: ${e.message}" }
            showErrorDialog("Error", "Error al guardar la convocatoria: ${e.message}")
        }
    }
    
    /**
     * Limpia el panel de detalles.
     */
    private fun clearDetailsPanel() {
        currentConvocatoria = null
        fechaConvocatoriaPicker.value = null
        entrenadorTextField.text = ""
        descripcionTextArea.text = ""
        jugadoresConvocados.clear()
        updateCountLabels()
        setFieldsEditable(false)
    }
    
    /**
     * Habilita o deshabilita la edición de los campos.
     */
    private fun setFieldsEditable(editable: Boolean) {
        fechaConvocatoriaPicker.isEditable = editable
        fechaConvocatoriaPicker.isDisable = !editable
        descripcionTextArea.isEditable = editable
        selectJugadoresButton.isDisable = !editable
        selectTitularesButton.isDisable = !editable
        saveConvocatoriaButton.isDisable = !editable
    }
    
    /**
     * Muestra un diálogo de información.
     */
    private fun showInfoDialog(title: String, message: String) {
        Alert(Alert.AlertType.INFORMATION).apply {
            this.title = title
            headerText = null
            contentText = message
        }.showAndWait()
    }
    
    /**
     * Muestra un diálogo de error.
     */
    private fun showErrorDialog(title: String, message: String) {
        Alert(Alert.AlertType.ERROR).apply {
            this.title = title
            headerText = null
            contentText = message
        }.showAndWait()
    }
}