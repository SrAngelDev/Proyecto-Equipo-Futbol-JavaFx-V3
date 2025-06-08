package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.VBox
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.repository.ConvocatoriaRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRepository
import srangeldev.proyectoequipofutboljavafx.newteam.utils.HtmlReportGenerator
import java.awt.Desktop
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Controlador para la gestión de convocatorias.
 */
class ConvocatoriaController : KoinComponent {
    private val logger = logging()

    // Elemento oculto para tamaños de diálogos
    @FXML
    private lateinit var dialogTableSizes: TableView<*>

    // Inyectar los repositorios usando Koin
    private val convocatoriaRepository: ConvocatoriaRepository by inject()
    private val personalRepository: PersonalRepository by inject()

    // Lista observable de convocatorias
    private val convocatorias = FXCollections.observableArrayList<Convocatoria>()

    // Lista observable de jugadores convocados
    private val jugadoresConvocados = FXCollections.observableArrayList<Jugador>()

    // Lista observable de entrenadores seleccionados
    private val entrenadoresSeleccionados = FXCollections.observableArrayList<Entrenador>()

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
    private lateinit var selectEntrenadoresButton: Button

    @FXML
    private lateinit var entrenadoresCountLabel: Label

    @FXML
    private lateinit var entrenadoresTableView: TableView<Entrenador>

    @FXML
    private lateinit var idEntrenadorColumn: TableColumn<Entrenador, Int>

    @FXML
    private lateinit var nombreEntrenadorColumn: TableColumn<Entrenador, String>

    @FXML
    private lateinit var especializacionEntrenadorColumn: TableColumn<Entrenador, String>

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

        // Limpiar la caché del repositorio para asegurar datos actualizados al iniciar
        personalRepository.clearCache()
        logger.debug { "Caché de personal limpiada para obtener datos frescos al inicializar" }

        // Inicializar la tabla de convocatorias
        initializeConvocatoriasTable()

        // Inicializar la tabla de jugadores convocados
        initializeJugadoresConvocadosTable()

        // Inicializar la tabla de entrenadores
        initializeEntrenadoresTable()

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
            SimpleBooleanProperty(esTitular)
        }

        // Configurar el cell factory para mostrar "Sí" o "No" en lugar de true/false
        titularColumn.setCellFactory { _ ->
            val cell = TableCell<Jugador, Boolean>()
            cell.textProperty().bind(
                javafx.beans.binding.Bindings.createStringBinding(
                    { if (cell.item == true) "Sí" else "No" },
                    cell.itemProperty()
                )
            )
            cell
        }

        // Asignar la lista observable a la tabla
        jugadoresConvocadosTableView.items = jugadoresConvocados
    }

    /**
     * Inicializa la tabla de entrenadores.
     */
    private fun initializeEntrenadoresTable() {
        // Configurar las columnas de la tabla
        idEntrenadorColumn.cellValueFactory = PropertyValueFactory("id")

        nombreEntrenadorColumn.setCellValueFactory { cellData ->
            SimpleStringProperty("${cellData.value.nombre} ${cellData.value.apellidos}")
        }

        especializacionEntrenadorColumn.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.especializacion.toString())
        }

        // Asignar la lista observable a la tabla
        entrenadoresTableView.items = entrenadoresSeleccionados
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

        // Botón para seleccionar entrenadores
        selectEntrenadoresButton.setOnAction {
            logger.debug { "Botón de seleccionar entrenadores presionado" }
            selectEntrenadores()
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
     * Selecciona los entrenadores para la convocatoria.
     */
    private fun selectEntrenadores() {
        logger.debug { "Seleccionando entrenadores para la convocatoria" }

        // Limpiar la caché del repositorio para asegurar datos actualizados
        personalRepository.clearCache()
        logger.debug { "Caché de personal limpiada para obtener datos frescos" }

        // Obtener todos los entrenadores disponibles
        val allEntrenadores = personalRepository.getAllEntrenadores().sortedBy { it.nombre }

        // Crear un mapa para rastrear los entrenadores seleccionados
        val selectedEntrenadores = mutableMapOf<Int, Entrenador>()

        // Inicializar con los entrenadores ya seleccionados
        entrenadoresSeleccionados.forEach { entrenador ->
            selectedEntrenadores[entrenador.id] = entrenador
        }

        // Crear el diálogo
        val dialog = Dialog<List<Entrenador>>()
        dialog.title = "Seleccionar Entrenadores"
        dialog.headerText = "Seleccione exactamente un entrenador de cada tipo:\n- Un Entrenador Principal\n- Un Entrenador Asistente\n- Un Entrenador de Porteros"

        // Botones
        dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        // Crear la tabla de entrenadores
        val tableView = TableView<Entrenador>()
        tableView.prefWidth = dialogTableSizes.prefWidth
        tableView.prefHeight = dialogTableSizes.prefHeight
        tableView.styleClass.add("styled-table-view")

        // Columnas
        val idColumn = TableColumn<Entrenador, Int>("ID")
        idColumn.cellValueFactory = PropertyValueFactory("id")

        val nombreColumn = TableColumn<Entrenador, String>("Nombre")
        nombreColumn.setCellValueFactory { cellData ->
            SimpleStringProperty("${cellData.value.nombre} ${cellData.value.apellidos}")
        }

        val especializacionColumn = TableColumn<Entrenador, String>("Especialización")
        especializacionColumn.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.especializacion.toString())
        }

        val seleccionadoColumn = TableColumn<Entrenador, Boolean>("Seleccionado")
        seleccionadoColumn.setCellFactory { 
            val cell = CheckBoxTableCell<Entrenador, Boolean>()
            cell.setSelectedStateCallback { index -> 
                val entrenador = tableView.items[index]
                val isSelected = selectedEntrenadores.containsKey(entrenador.id)
                SimpleBooleanProperty(isSelected)
            }
            cell
        }

        tableView.columns.addAll(idColumn, nombreColumn, especializacionColumn, seleccionadoColumn)
        tableView.items = FXCollections.observableArrayList(allEntrenadores)

        // Contador de entrenadores seleccionados por tipo
        val countLabel = Label("0 entrenadores seleccionados (0 Principal, 0 Asistente, 0 Porteros)")

        // Actualizar el contador
        fun updateCountLabel() {
            val totalSelected = selectedEntrenadores.size
            val principalCount = selectedEntrenadores.values.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PRINCIPAL }
            val asistenteCount = selectedEntrenadores.values.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_ASISTENTE }
            val porterosCount = selectedEntrenadores.values.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PORTEROS }
            countLabel.text = "$totalSelected entrenadores seleccionados ($principalCount Principal, $asistenteCount Asistente, $porterosCount Porteros)"
        }

        // Manejar la selección de entrenadores
        tableView.setOnMouseClicked { event ->
            if (event.clickCount == 1) {
                val selectedEntrenador = tableView.selectionModel.selectedItem
                if (selectedEntrenador != null) {
                    if (selectedEntrenadores.containsKey(selectedEntrenador.id)) {
                        // Deseleccionar entrenador
                        selectedEntrenadores.remove(selectedEntrenador.id)
                    } else {
                        // Validar antes de seleccionar
                        val entrenadorPrincipal = selectedEntrenadores.values.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PRINCIPAL }
                        val entrenadorAsistente = selectedEntrenadores.values.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_ASISTENTE }
                        val entrenadorPorteros = selectedEntrenadores.values.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PORTEROS }

                        // Verificar si ya hay un entrenador del mismo tipo
                        when (selectedEntrenador.especializacion) {
                            Entrenador.Especializacion.ENTRENADOR_PRINCIPAL -> {
                                if (entrenadorPrincipal >= 1) {
                                    showErrorDialog("Error", "Ya ha seleccionado un Entrenador Principal")
                                    return@setOnMouseClicked
                                }
                            }
                            Entrenador.Especializacion.ENTRENADOR_ASISTENTE -> {
                                if (entrenadorAsistente >= 1) {
                                    showErrorDialog("Error", "Ya ha seleccionado un Entrenador Asistente")
                                    return@setOnMouseClicked
                                }
                            }
                            Entrenador.Especializacion.ENTRENADOR_PORTEROS -> {
                                if (entrenadorPorteros >= 1) {
                                    showErrorDialog("Error", "Ya ha seleccionado un Entrenador de Porteros")
                                    return@setOnMouseClicked
                                }
                            }
                        }

                        // Seleccionar entrenador
                        selectedEntrenadores[selectedEntrenador.id] = selectedEntrenador
                    }

                    // Actualizar la tabla y el contador
                    tableView.refresh()
                    updateCountLabel()
                }
            }
        }

        // Layout
        val vbox = VBox(10.0)
        vbox.children.addAll(tableView, countLabel)
        dialog.dialogPane.content = vbox

        // Actualizar el contador inicial
        updateCountLabel()

        // Configurar el resultado del diálogo
        dialog.setResultConverter { buttonType ->
            if (buttonType == ButtonType.OK) {
                // Verificar que hay exactamente un entrenador de cada tipo
                val entrenadorPrincipal = selectedEntrenadores.values.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PRINCIPAL }
                val entrenadorAsistente = selectedEntrenadores.values.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_ASISTENTE }
                val entrenadorPorteros = selectedEntrenadores.values.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PORTEROS }

                if (entrenadorPrincipal != 1 || entrenadorAsistente != 1 || entrenadorPorteros != 1) {
                    // Mostrar mensaje de error
                    val errorMessage = StringBuilder("Debe seleccionar exactamente un entrenador de cada tipo:\n")
                    if (entrenadorPrincipal != 1) errorMessage.append("- Entrenador Principal: ${if (entrenadorPrincipal == 0) "Falta seleccionar" else "Seleccionados en exceso"}\n")
                    if (entrenadorAsistente != 1) errorMessage.append("- Entrenador Asistente: ${if (entrenadorAsistente == 0) "Falta seleccionar" else "Seleccionados en exceso"}\n")
                    if (entrenadorPorteros != 1) errorMessage.append("- Entrenador de Porteros: ${if (entrenadorPorteros == 0) "Falta seleccionar" else "Seleccionados en exceso"}\n")

                    showErrorDialog("Error en la selección", errorMessage.toString())
                    null
                } else {
                    selectedEntrenadores.values.toList()
                }
            } else {
                null
            }
        }

        // Mostrar el diálogo y procesar el resultado
        val result = dialog.showAndWait()

        if (result.isPresent) {
            val selectedCoaches = result.get()

            // Actualizar la lista de entrenadores seleccionados
            entrenadoresSeleccionados.clear()
            entrenadoresSeleccionados.addAll(selectedCoaches)

            // Actualizar la etiqueta de conteo
            val principalCount = entrenadoresSeleccionados.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PRINCIPAL }
            val asistenteCount = entrenadoresSeleccionados.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_ASISTENTE }
            val porterosCount = entrenadoresSeleccionados.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PORTEROS }
            entrenadoresCountLabel.text = "${entrenadoresSeleccionados.size} entrenadores seleccionados ($principalCount Principal, $asistenteCount Asistente, $porterosCount Porteros)"

            // Refrescar la tabla
            entrenadoresTableView.refresh()
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

        // Limpiar la caché del repositorio para asegurar datos actualizados
        personalRepository.clearCache()
        logger.debug { "Caché de personal limpiada para obtener datos frescos al mostrar detalles" }

        // Mostrar los datos de la convocatoria
        fechaConvocatoriaPicker.value = convocatoria.fecha
        descripcionTextArea.text = convocatoria.descripcion

        // Limpiar la lista de entrenadores seleccionados
        entrenadoresSeleccionados.clear()

        // Obtener todos los entrenadores disponibles
        val allEntrenadores = personalRepository.getAllEntrenadores()

        // Obtener el entrenador principal (el que está guardado en la convocatoria)
        val entrenadorPrincipal = personalRepository.getById(convocatoria.entrenadorId)
        if (entrenadorPrincipal is Entrenador) {
            // Añadir el entrenador principal a la lista
            entrenadoresSeleccionados.add(entrenadorPrincipal)

            // Buscar un entrenador asistente y un entrenador de porteros
            val entrenadorAsistente = allEntrenadores.firstOrNull { 
                it.especializacion == Entrenador.Especializacion.ENTRENADOR_ASISTENTE 
            }
            val entrenadorPorteros = allEntrenadores.firstOrNull { 
                it.especializacion == Entrenador.Especializacion.ENTRENADOR_PORTEROS 
            }

            // Añadir los entrenadores encontrados a la lista
            if (entrenadorAsistente != null) {
                entrenadoresSeleccionados.add(entrenadorAsistente)
            }
            if (entrenadorPorteros != null) {
                entrenadoresSeleccionados.add(entrenadorPorteros)
            }

            // Actualizar la etiqueta de conteo
            val principalCount = entrenadoresSeleccionados.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PRINCIPAL }
            val asistenteCount = entrenadoresSeleccionados.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_ASISTENTE }
            val porterosCount = entrenadoresSeleccionados.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PORTEROS }
            entrenadoresCountLabel.text = "${entrenadoresSeleccionados.size} entrenadores seleccionados ($principalCount Principal, $asistenteCount Asistente, $porterosCount Porteros)"
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

        // Obtener los jugadores por sus ID
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

        // Limpiar la caché del repositorio para asegurar datos actualizados
        personalRepository.clearCache()
        logger.debug { "Caché de personal limpiada para obtener datos frescos" }

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

        // Añadir el entrenador principal a la lista de entrenadores seleccionados
        entrenadoresSeleccionados.clear()
        entrenadoresSeleccionados.add(entrenadorPrincipal)

        // Actualizar la etiqueta de conteo
        entrenadoresCountLabel.text = "${entrenadoresSeleccionados.size} entrenadores seleccionados"

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
        logger.debug { "Imprimiendo convocatoria" }

        val convocatoria = currentConvocatoria ?: return

        try {
            // Usar los entrenadores seleccionados
            if (entrenadoresSeleccionados.isEmpty()) {
                throw IllegalStateException("No hay entrenadores seleccionados para la convocatoria")
            }

            // Obtener los jugadores convocados
            val jugadoresConvocados = convocatoria.jugadores.mapNotNull { jugadorId ->
                personalRepository.getById(jugadorId) as? Jugador
            }

            // Obtener el directorio de informes desde la configuración
            val reportsDir = Config.configProperties.reportsDir
            val reportsDirFile = File(reportsDir)
            if (!reportsDirFile.exists()) {
                reportsDirFile.mkdirs()
            }

            // Generar nombre de archivo con timestamp
            val timestamp = LocalDateTime.now().toString().replace(":", "-").replace(".", "-")
            val outputPath = "$reportsDir/convocatoria_${timestamp}.html"

            // Generar el informe HTML
            val reportPath = HtmlReportGenerator.generateConvocatoriaReport(
                convocatoria = convocatoria,
                jugadores = jugadoresConvocados,
                entrenadores = entrenadoresSeleccionados.toList(),
                outputPath = outputPath
            )

            // Abrir el informe en el navegador predeterminado
            val file = File(reportPath)
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(file.toURI())
                    showInfoDialog(
                        "Informe HTML generado", 
                        "El informe HTML ha sido generado y abierto en su navegador predeterminado.\n\nRuta: $reportPath"
                    )
                } else {
                    logger.error { "No se puede abrir el navegador predeterminado" }
                    showInfoDialog(
                        "Informe HTML generado", 
                        "El informe HTML ha sido generado pero no se pudo abrir automáticamente.\n\nRuta: $reportPath"
                    )
                }
            } catch (e: Exception) {
                logger.error { "No se puede abrir el navegador predeterminado: ${e.message}" }
                showInfoDialog(
                    "Informe HTML generado", 
                    "El informe HTML ha sido generado pero no se pudo abrir automáticamente.\n\nRuta: $reportPath"
                )
            }
        } catch (e: Exception) {
            logger.error { "Error al generar el informe HTML: ${e.message}" }
            showErrorDialog("Error", "No se pudo generar el informe HTML: ${e.message}")
        }
    }

    /**
     * Selecciona los jugadores para la convocatoria.
     */
    private fun selectJugadores() {
        logger.debug { "Seleccionando jugadores para la convocatoria" }

        val convocatoria = currentConvocatoria ?: return

        // Limpiar la caché del repositorio para asegurar datos actualizados
        personalRepository.clearCache()
        logger.debug { "Caché de personal limpiada para obtener datos frescos" }

        // Obtener todos los jugadores disponibles
        val allJugadores = personalRepository.getAll()
            .filterIsInstance<Jugador>()
            .sortedBy { it.dorsal }

        // Crear un mapa para rastrear los jugadores seleccionados
        val selectedJugadores = mutableMapOf<Int, Jugador>()

        // Inicializar con los jugadores ya convocados
        convocatoria.jugadores.forEach { jugadorId ->
            val jugador = allJugadores.find { it.id == jugadorId }
            if (jugador != null) {
                selectedJugadores[jugador.id] = jugador
            }
        }

        // Crear el diálogo
        val dialog = Dialog<List<Jugador>>()
        dialog.title = "Seleccionar Jugadores"
        dialog.headerText = "Seleccione hasta 18 jugadores (máximo 2 porteros)"

        // Botones
        dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        // Crear la tabla de jugadores
        val tableView = TableView<Jugador>()
        tableView.prefWidth = 600.0
        tableView.prefHeight = 400.0
        tableView.styleClass.add("styled-table-view")

        // Columnas
        val idColumn = TableColumn<Jugador, Int>("ID")
        idColumn.cellValueFactory = PropertyValueFactory("id")

        val nombreColumn = TableColumn<Jugador, String>("Nombre")
        nombreColumn.setCellValueFactory { cellData ->
            SimpleStringProperty("${cellData.value.nombre} ${cellData.value.apellidos}")
        }

        val posicionColumn = TableColumn<Jugador, String>("Posición")
        posicionColumn.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.posicion.toString())
        }

        val dorsalColumn = TableColumn<Jugador, Int>("Dorsal")
        dorsalColumn.cellValueFactory = PropertyValueFactory("dorsal")

        val seleccionadoColumn = TableColumn<Jugador, Boolean>("Seleccionado")
        seleccionadoColumn.setCellFactory { 
            val cell = CheckBoxTableCell<Jugador, Boolean>()
            cell.setSelectedStateCallback { index -> 
                val jugador = tableView.items[index]
                val isSelected = selectedJugadores.containsKey(jugador.id)
                SimpleBooleanProperty(isSelected)
            }
            cell
        }

        tableView.columns.addAll(idColumn, nombreColumn, posicionColumn, dorsalColumn, seleccionadoColumn)
        tableView.items = FXCollections.observableArrayList(allJugadores)

        // Contador de jugadores seleccionados
        val countLabel = Label("0/18 jugadores seleccionados (0 porteros)")

        // Actualizar el contador
        fun updateCountLabel() {
            val totalSelected = selectedJugadores.size
            val porterosCount = selectedJugadores.values.count { it.posicion == Jugador.Posicion.PORTERO }
            countLabel.text = "$totalSelected/18 jugadores seleccionados ($porterosCount porteros)"
        }

        // Manejar la selección de jugadores
        tableView.setOnMouseClicked { event ->
            if (event.clickCount == 1) {
                val selectedJugador = tableView.selectionModel.selectedItem
                if (selectedJugador != null) {
                    if (selectedJugadores.containsKey(selectedJugador.id)) {
                        // Deseleccionar jugador
                        selectedJugadores.remove(selectedJugador.id)
                    } else {
                        // Validar antes de seleccionar
                        val totalSelected = selectedJugadores.size
                        val porterosCount = selectedJugadores.values.count { it.posicion == Jugador.Posicion.PORTERO }

                        if (totalSelected >= 18) {
                            showErrorDialog("Error", "No puede seleccionar más de 18 jugadores")
                            return@setOnMouseClicked
                        }

                        if (selectedJugador.posicion == Jugador.Posicion.PORTERO && porterosCount >= 2) {
                            showErrorDialog("Error", "No puede seleccionar más de 2 porteros")
                            return@setOnMouseClicked
                        }

                        // Seleccionar jugador
                        selectedJugadores[selectedJugador.id] = selectedJugador
                    }

                    // Actualizar la tabla y el contador
                    tableView.refresh()
                    updateCountLabel()
                }
            }
        }

        // Layout
        val vbox = VBox(10.0)
        vbox.children.addAll(tableView, countLabel)
        dialog.dialogPane.content = vbox

        // Actualizar el contador inicial
        updateCountLabel()

        // Configurar el resultado del diálogo
        dialog.setResultConverter { buttonType ->
            if (buttonType == ButtonType.OK) {
                selectedJugadores.values.toList()
            } else {
                null
            }
        }

        // Mostrar el diálogo y procesar el resultado
        val result = dialog.showAndWait()

        if (result.isPresent) {
            val selectedPlayers = result.get()

            // Actualizar la convocatoria con los jugadores seleccionados
            val updatedConvocatoria = convocatoria.copy(
                jugadores = selectedPlayers.map { it.id }
            )

            // Actualizar la convocatoria actual
            currentConvocatoria = updatedConvocatoria

            // Actualizar la lista de jugadores convocados
            jugadoresConvocados.clear()
            jugadoresConvocados.addAll(selectedPlayers)

            // Actualizar las etiquetas de conteo
            updateCountLabels()

            // Refrescar la tabla
            jugadoresConvocadosTableView.refresh()
        }
    }

    /**
     * Selecciona los titulares para la convocatoria.
     */
    private fun selectTitulares() {
        logger.debug { "Seleccionando titulares para la convocatoria" }

        val convocatoria = currentConvocatoria ?: return

        // Verificar que hay jugadores convocados
        if (convocatoria.jugadores.isEmpty()) {
            showErrorDialog("Error", "Debe seleccionar jugadores antes de elegir titulares")
            return
        }

        // Limpiar la caché del repositorio para asegurar datos actualizados
        personalRepository.clearCache()
        logger.debug { "Caché de personal limpiada para obtener datos frescos" }

        // Obtener los jugadores convocados
        val convocadosJugadores = jugadoresConvocados.toList()

        // Crear un mapa para rastrear los jugadores titulares
        val titularesJugadores = mutableMapOf<Int, Jugador>()

        // Inicializar con los jugadores ya titulares
        convocatoria.titulares.forEach { jugadorId ->
            val jugador = convocadosJugadores.find { it.id == jugadorId }
            if (jugador != null) {
                titularesJugadores[jugador.id] = jugador
            }
        }

        // Crear el diálogo
        val dialog = Dialog<List<Jugador>>()
        dialog.title = "Seleccionar Titulares"
        dialog.headerText = "Seleccione exactamente 11 jugadores titulares"

        // Botones
        dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        // Crear la tabla de jugadores
        val tableView = TableView<Jugador>()
        tableView.prefWidth = 600.0
        tableView.prefHeight = 400.0
        tableView.styleClass.add("styled-table-view")

        // Columnas
        val idColumn = TableColumn<Jugador, Int>("ID")
        idColumn.cellValueFactory = PropertyValueFactory("id")

        val nombreColumn = TableColumn<Jugador, String>("Nombre")
        nombreColumn.setCellValueFactory { cellData ->
            SimpleStringProperty("${cellData.value.nombre} ${cellData.value.apellidos}")
        }

        val posicionColumn = TableColumn<Jugador, String>("Posición")
        posicionColumn.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.posicion.toString())
        }

        val dorsalColumn = TableColumn<Jugador, Int>("Dorsal")
        dorsalColumn.cellValueFactory = PropertyValueFactory("dorsal")

        val titularColumn = TableColumn<Jugador, Boolean>("Titular")
        titularColumn.setCellFactory { 
            val cell = CheckBoxTableCell<Jugador, Boolean>()
            cell.setSelectedStateCallback { index -> 
                val jugador = tableView.items[index]
                val isTitular = titularesJugadores.containsKey(jugador.id)
                SimpleBooleanProperty(isTitular)
            }
            cell
        }

        tableView.columns.addAll(idColumn, nombreColumn, posicionColumn, dorsalColumn, titularColumn)
        tableView.items = FXCollections.observableArrayList(convocadosJugadores)

        // Contador de jugadores titulares
        val countLabel = Label("0/11 jugadores titulares seleccionados")

        // Actualizar el contador
        fun updateCountLabel() {
            val totalSelected = titularesJugadores.size
            countLabel.text = "$totalSelected/11 jugadores titulares seleccionados"
        }

        // Manejar la selección de jugadores
        tableView.setOnMouseClicked { event ->
            if (event.clickCount == 1) {
                val selectedJugador = tableView.selectionModel.selectedItem
                if (selectedJugador != null) {
                    if (titularesJugadores.containsKey(selectedJugador.id)) {
                        // Deseleccionar jugador
                        titularesJugadores.remove(selectedJugador.id)
                    } else {
                        // Validar antes de seleccionar
                        val totalSelected = titularesJugadores.size

                        if (totalSelected >= 11) {
                            showErrorDialog("Error", "No puede seleccionar más de 11 jugadores titulares")
                            return@setOnMouseClicked
                        }

                        // Validar que solo haya un portero
                        if (selectedJugador.posicion == Jugador.Posicion.PORTERO) {
                            val porterosCount = titularesJugadores.values.count { it.posicion == Jugador.Posicion.PORTERO }
                            if (porterosCount >= 1) {
                                showErrorDialog("Error", "No puede seleccionar más de un portero como titular")
                                return@setOnMouseClicked
                            }
                        }

                        // Seleccionar jugador
                        titularesJugadores[selectedJugador.id] = selectedJugador
                    }

                    // Actualizar la tabla y el contador
                    tableView.refresh()
                    updateCountLabel()
                }
            }
        }

        // Layout
        val vbox = VBox(10.0)
        vbox.children.addAll(tableView, countLabel)
        dialog.dialogPane.content = vbox

        // Actualizar el contador inicial
        updateCountLabel()

        // Configurar el resultado del diálogo
        dialog.setResultConverter { buttonType ->
            if (buttonType == ButtonType.OK) {
                // Validar que hay exactamente 11 jugadores titulares
                if (titularesJugadores.size != 11) {
                    showErrorDialog("Error", "Debe seleccionar exactamente 11 jugadores titulares")
                    null
                } else {
                    titularesJugadores.values.toList()
                }
            } else {
                null
            }
        }

        // Mostrar el diálogo y procesar el resultado
        val result = dialog.showAndWait()

        if (result.isPresent) {
            val selectedTitulares = result.get()

            // Actualizar la convocatoria con los jugadores titulares
            val updatedConvocatoria = convocatoria.copy(
                titulares = selectedTitulares.map { it.id }
            )

            // Actualizar la convocatoria actual
            currentConvocatoria = updatedConvocatoria

            // Actualizar las etiquetas de conteo
            updateCountLabels()

            // Refrescar la tabla
            jugadoresConvocadosTableView.refresh()
        }
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
            // Validar que se hayan seleccionado los tres tipos de entrenadores
            val principalCount = entrenadoresSeleccionados.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PRINCIPAL }
            val asistenteCount = entrenadoresSeleccionados.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_ASISTENTE }
            val porterosCount = entrenadoresSeleccionados.count { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PORTEROS }

            if (principalCount != 1 || asistenteCount != 1 || porterosCount != 1) {
                val errorMessage = StringBuilder("Debe seleccionar exactamente un entrenador de cada tipo:\n")
                if (principalCount != 1) errorMessage.append("- Entrenador Principal: ${if (principalCount == 0) "Falta seleccionar" else "Seleccionados en exceso"}\n")
                if (asistenteCount != 1) errorMessage.append("- Entrenador Asistente: ${if (asistenteCount == 0) "Falta seleccionar" else "Seleccionados en exceso"}\n")
                if (porterosCount != 1) errorMessage.append("- Entrenador de Porteros: ${if (porterosCount == 0) "Falta seleccionar" else "Seleccionados en exceso"}\n")

                showErrorDialog("Error", errorMessage.toString())
                return
            }

            // Obtener el entrenador principal seleccionado
            val entrenadorPrincipal = entrenadoresSeleccionados.first { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PRINCIPAL }

            // Verificar si el entrenador principal es un entrenador por defecto (ID negativo)
            var entrenadorId = entrenadorPrincipal.id
            if (entrenadorId < 0) {
                logger.debug { "Entrenador principal seleccionado es un entrenador por defecto con ID: $entrenadorId" }

                try {
                    // Guardar el entrenador por defecto en la base de datos
                    val entrenadorGuardado = personalRepository.save(
                        Entrenador(
                            id = 0, // El repositorio asignará un ID positivo
                            nombre = entrenadorPrincipal.nombre,
                            apellidos = entrenadorPrincipal.apellidos,
                            fechaNacimiento = entrenadorPrincipal.fechaNacimiento,
                            fechaIncorporacion = entrenadorPrincipal.fechaIncorporacion,
                            salario = entrenadorPrincipal.salario,
                            paisOrigen = entrenadorPrincipal.paisOrigen,
                            especializacion = entrenadorPrincipal.especializacion,
                            createdAt = LocalDateTime.now(),
                            updatedAt = LocalDateTime.now()
                        )
                    )

                    // Usar el ID del entrenador guardado
                    entrenadorId = entrenadorGuardado.id
                    logger.debug { "Entrenador principal por defecto guardado en la base de datos con nuevo ID: $entrenadorId" }
                } catch (e: Exception) {
                    logger.error { "Error al guardar el entrenador principal por defecto: ${e.message}" }
                    showErrorDialog("Error", "No se pudo guardar el entrenador principal por defecto: ${e.message}")
                    return
                }
            }

            // Actualizar los datos de la convocatoria
            val updatedConvocatoria = convocatoria.copy(
                fecha = fechaConvocatoriaPicker.value,
                descripcion = descripcionTextArea.text,
                entrenadorId = entrenadorId
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
        entrenadoresSeleccionados.clear()
        entrenadoresCountLabel.text = "0 entrenadores seleccionados"
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
        selectEntrenadoresButton.isDisable = !editable
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
