package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
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
import java.time.LocalDateTime

/**
 * Controlador para la visualización de convocatorias en modo normal (solo lectura).
 */
class ConvocatoriaNormalController : KoinComponent {
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
    private lateinit var printConvocatoriaButton: Button

    @FXML
    private lateinit var cancelButton: Button

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     */
    @FXML
    private fun initialize() {
        logger.debug { "Inicializando ConvocatoriaNormalController" }

        // Limpiar la caché del repositorio para asegurar datos actualizados al iniciar
        personalRepository.clearCache()
        logger.debug { "Caché de personal limpiada para obtener datos frescos al inicializar" }

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

        // Inicialmente, deshabilitar el botón de impresión
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

        // Configurar el cell factory para mostrar "Sí" o "No" en lugar de true/false
        titularColumn.setCellFactory { column ->
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
     * Configura los eventos de los botones.
     */
    private fun setupButtonEvents() {
        // Botón para imprimir una convocatoria
        printConvocatoriaButton.setOnAction {
            logger.debug { "Botón de imprimir convocatoria presionado" }
            printConvocatoria()
        }

        // Botón para volver
        cancelButton.setOnAction {
            logger.debug { "Botón de volver presionado" }
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
                printConvocatoriaButton.isDisable = false
            } else {
                clearDetailsPanel()
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

        // Limpiar la caché del repositorio para asegurar datos actualizados
        personalRepository.clearCache()
        logger.debug { "Caché de personal limpiada para obtener datos frescos al mostrar detalles" }

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
     * Imprime una convocatoria.
     */
    private fun printConvocatoria() {
        logger.debug { "Imprimiendo convocatoria" }

        val convocatoria = currentConvocatoria ?: return

        try {
            // Limpiar la caché para asegurar datos actualizados
            personalRepository.clearCache()

            // Obtener todos los entrenadores
            val entrenadores = personalRepository.getAllEntrenadores()

            // Filtrar para obtener un entrenador de cada tipo
            val entrenadorPrincipal = entrenadores.find { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PRINCIPAL }
            val entrenadorAsistente = entrenadores.find { it.especializacion == Entrenador.Especializacion.ENTRENADOR_ASISTENTE }
            val entrenadorPorteros = entrenadores.find { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PORTEROS }

            // Crear la lista de entrenadores para el informe
            val entrenadoresInforme = listOfNotNull(entrenadorPrincipal, entrenadorAsistente, entrenadorPorteros)

            if (entrenadoresInforme.isEmpty()) {
                throw IllegalStateException("No se encontraron entrenadores para la convocatoria")
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
                entrenadores = entrenadoresInforme,
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
     * Limpia el panel de detalles.
     */
    private fun clearDetailsPanel() {
        currentConvocatoria = null
        fechaConvocatoriaPicker.value = null
        entrenadorTextField.text = ""
        descripcionTextArea.text = ""
        jugadoresConvocados.clear()
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
