package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.transformation.FilteredList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.Stage
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.controller.Controller
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.proyectoequipofutboljavafx.NewTeamApplication
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.repository.ConvocatoriaRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRepositoryImpl
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepositoryImpl
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager
import srangeldev.proyectoequipofutboljavafx.newteam.session.Session
import srangeldev.proyectoequipofutboljavafx.newteam.service.PersonalServiceImpl
import srangeldev.proyectoequipofutboljavafx.newteam.utils.HtmlReportGenerator
import srangeldev.proyectoequipofutboljavafx.newteam.utils.PdfReportGenerator
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import java.awt.Desktop
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

/**
 * Controlador para la vista de usuario normal.
 * Maneja la visualización y edición limitada de datos del personal del equipo.
 * Los usuarios normales tienen acceso restringido a ciertas funcionalidades
 * que están disponibles solo para administradores.
 */
class VistaNormalController : KoinComponent {
    private val logger = logging()

    // Panel izquierdo - Tabla de jugadores
    @FXML private lateinit var playersTableView: TableView<Personal>
    @FXML private lateinit var idColumn: TableColumn<Personal, Int>
    @FXML private lateinit var nombreColumn: TableColumn<Personal, String>
    @FXML private lateinit var apellidosColumn: TableColumn<Personal, String>
    @FXML private lateinit var searchField: TextField
    @FXML private lateinit var allToggleButton: ToggleButton
    @FXML private lateinit var playerToggleButton: ToggleButton
    @FXML private lateinit var coachToggleButton: ToggleButton
    @FXML private lateinit var avgGolesLabel: Label

    // Panel derecho - Detalles del jugador
    @FXML private lateinit var playerImageView: ImageView
    @FXML private lateinit var nombreTextField: TextField
    @FXML private lateinit var edadSpinner: Spinner<Int>
    @FXML private lateinit var salarioTextField: TextField
    @FXML private lateinit var especialidadLabel: Label
    @FXML private lateinit var especialidadComboBox: ComboBox<String>
    @FXML private lateinit var posicionLabel: Label
    @FXML private lateinit var posicionComboBox: ComboBox<String>
    @FXML private lateinit var dorsalLabel: Label
    @FXML private lateinit var dorsalTextField: TextField
    @FXML private lateinit var fechaIncorporacionPicker: DatePicker
    @FXML private lateinit var partidosLabel: Label
    @FXML private lateinit var partidosTextField: TextField
    @FXML private lateinit var golesLabel: Label
    @FXML private lateinit var golesTextField: TextField
    @FXML private lateinit var cancelButton: Button

    // Menú
    @FXML private lateinit var loadDataMenuItem: MenuItem
    @FXML private lateinit var exportDataMenuItem: MenuItem
    @FXML private lateinit var importDataMenuItem: MenuItem
    @FXML private lateinit var printHtmlMenuItem: MenuItem
    @FXML private lateinit var printPdfMenuItem: MenuItem
    @FXML private lateinit var convocatoriasMenuItem: MenuItem
    @FXML private lateinit var closeMenuItem: MenuItem
    @FXML private lateinit var aboutMenuItem: MenuItem

    // Paneles principales
    @FXML private lateinit var jugadoresPane: SplitPane
    @FXML private lateinit var convocatoriasPane: SplitPane

    // Elementos de la vista de convocatorias
    @FXML private lateinit var searchConvocatoriaField: TextField
    @FXML private lateinit var convocatoriasTableView: TableView<Convocatoria>
    @FXML private lateinit var idConvocatoriaColumn: TableColumn<Convocatoria, Int>
    @FXML private lateinit var fechaConvocatoriaColumn: TableColumn<Convocatoria, String>
    @FXML private lateinit var descripcionConvocatoriaColumn: TableColumn<Convocatoria, String>
    @FXML private lateinit var jugadoresConvocatoriaColumn: TableColumn<Convocatoria, Int>
    @FXML private lateinit var titularesConvocatoriaColumn: TableColumn<Convocatoria, Int>
    @FXML private lateinit var fechaConvocatoriaPicker: DatePicker
    @FXML private lateinit var entrenadorTextField: TextField
    @FXML private lateinit var descripcionTextArea: TextArea
    @FXML private lateinit var jugadoresConvocadosTableView: TableView<Jugador>
    @FXML private lateinit var idJugadorColumn: TableColumn<Jugador, Int>
    @FXML private lateinit var nombreJugadorColumn: TableColumn<Jugador, String>
    @FXML private lateinit var posicionJugadorColumn: TableColumn<Jugador, String>
    @FXML private lateinit var dorsalJugadorColumn: TableColumn<Jugador, Int>
    @FXML private lateinit var titularColumn: TableColumn<Jugador, Boolean>
    @FXML private lateinit var printConvocatoriaButton: Button
    @FXML private lateinit var backToPlayersButton: Button
    @FXML private lateinit var cancelConvocatoriaButton: Button

    // Datos
    private val personalList = FXCollections.observableArrayList<Personal>()
    private lateinit var filteredPersonal: FilteredList<Personal>
    private var currentPersonal: Personal? = null
    private var isAdmin = false
    private var dataLoaded = false

    // Datos de convocatorias
    private val convocatoriaRepository: ConvocatoriaRepository by inject()
    private val convocatorias = FXCollections.observableArrayList<Convocatoria>()
    private val jugadoresConvocados = FXCollections.observableArrayList<Jugador>()
    private var currentConvocatoria: Convocatoria? = null

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     * Configura todos los componentes de la interfaz de usuario y carga los datos iniciales.
     */
    @FXML
    private fun initialize() {
        logger.debug { "Inicializando VistaNormalController" }

        // Verificar si el usuario es administrador
        checkUserRole()

        // Inicializar la tabla de personal con datos de ejemplo
        initializePersonalTable()

        // Configurar los filtros
        setupFilters()

        // Configurar el campo de búsqueda
        setupSearchField()

        // Configurar los elementos del menú
        setupMenuItems()

        // Configurar el panel de detalles
        setupDetailsPanel()

        // Configurar eventos de la tabla
        setupTableViewEvents()

        // Cargar datos desde la base de datos
        loadPersonalFromDatabase()

        // Actualizar estadísticas
        updateStatistics()
    }

    /**
     * Verifica el rol del usuario actual y configura la interfaz de usuario en consecuencia.
     * Los administradores tienen acceso a funcionalidades adicionales y pueden editar los datos.
     */
    private fun checkUserRole() {
        logger.debug { "Verificando rol de usuario" }

        // Obtener el rol del usuario desde la sesión
        isAdmin = Session.isAdmin()

        logger.debug { "Usuario es administrador: $isAdmin" }

        // Configurar visibilidad de elementos según el rol
        exportDataMenuItem.isVisible = isAdmin
        importDataMenuItem.isVisible = isAdmin

        // Configurar la edición de campos según el rol
        setFieldsEditable(isAdmin)
    }

    /**
     * Inicializa la tabla de personal configurando las columnas y el sistema de filtrado.
     * Establece las fábricas de valores para cada columna y conecta la lista filtrada a la tabla.
     */
    private fun initializePersonalTable() {
        // Configurar las columnas de la tabla
        idColumn.setCellValueFactory { cellData -> SimpleIntegerProperty(cellData.value.id).asObject() }
        nombreColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.nombre) }
        apellidosColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.apellidos) }

        // Crear una lista filtrada basada en la lista original
        filteredPersonal = FilteredList(personalList) { true }

        // Asignar la lista filtrada a la TableView
        playersTableView.items = filteredPersonal
    }

    /**
     * Carga los datos de personal desde la base de datos y actualiza la UI.
     */
    private fun loadPersonalFromDatabase() {
        try {
            logger.debug { "Cargando datos de personal desde la base de datos" }

            // Limpiar la caché del repositorio para evitar duplicados
            val repository = PersonalRepositoryImpl()
            repository.clearCache()

            // Crear una instancia del servicio
            val service = PersonalServiceImpl()

            // Obtener todos los miembros del personal
            val allPersonal = service.getAll()

            // Limpiar la lista actual
            personalList.clear()

            // Añadir los nuevos datos
            personalList.addAll(allPersonal)

            // Actualizar la tabla
            playersTableView.refresh()

            // Actualizar estadísticas
            updateStatistics()

            logger.debug { "Datos cargados correctamente: ${personalList.size} miembros" }
        } catch (e: Exception) {
            logger.error { "Error al cargar datos desde la base de datos: ${e.message}" }
            showErrorDialog("Error", "No se pudieron cargar los datos desde la base de datos: ${e.message}")
        }
    }

    /**
     * Configura los botones de filtro para permitir al usuario filtrar el personal por tipo.
     * Establece los manejadores de eventos para los botones de alternancia que filtran
     * entre todos los miembros del personal, solo jugadores o solo entrenadores.
     */
    private fun setupFilters() {
        // Configurar los ToggleButtons para filtrar entre todos, jugadores y entrenadores
        allToggleButton.setOnAction {
            applyFilters(searchField.text)
            updateStatistics()
        }

        playerToggleButton.setOnAction {
            applyFilters(searchField.text)
            updateStatistics()
        }

        coachToggleButton.setOnAction {
            applyFilters(searchField.text)
            updateStatistics()
        }
    }

    private fun setupSearchField() {
        // Configurar el evento de cambio de texto
        searchField.textProperty().addListener { _, _, newValue ->
            applyFilters(newValue)
        }
    }

    private fun applyFilters(searchText: String) {
        filteredPersonal.predicate = object : java.util.function.Predicate<Personal> {
            override fun test(personal: Personal): Boolean {
                // Filtrar por tipo (todos, jugador o entrenador)
                val matchesType = when {
                    allToggleButton.isSelected -> true // Mostrar todos
                    playerToggleButton.isSelected -> personal is Jugador
                    coachToggleButton.isSelected -> personal is Entrenador
                    else -> true // Si ninguno está seleccionado, mostrar todos por defecto
                }

                // Si el texto de búsqueda está vacío, solo aplicar filtro de tipo
                if (searchText.isEmpty() || searchText.isBlank()) {
                    return matchesType
                }

                // Filtrar por texto de búsqueda y tipo
                val matchesSearch = personal.nombre.lowercase().contains(searchText.lowercase()) ||
                        personal.apellidos.lowercase().contains(searchText.lowercase()) ||
                        personal.id.toString() == searchText

                return matchesSearch && matchesType
            }
        }
    }

    private fun setupMenuItems() {
        // Configurar el evento del menú Cargar datos
        loadDataMenuItem.setOnAction {
            // Verificar si los datos ya han sido cargados
            if (dataLoaded) {
                showInfoDialog("Cargar datos", "Los datos ya han sido cargados. Para evitar duplicidad, esta acción solo puede realizarse una vez.")
                return@setOnAction
            }

            try {
                // Crear una instancia del controlador
                val controller = Controller()

                // Cargar datos desde los archivos CSV, JSON y XML
                controller.cargarDatos("CSV")
                controller.cargarDatos("JSON")
                controller.cargarDatos("XML")

                // Actualizar la lista de personal con los datos cargados
                loadPersonalFromDatabase()

                // Marcar que los datos han sido cargados y deshabilitar el menú
                dataLoaded = true
                loadDataMenuItem.isDisable = true

                showInfoDialog("Cargar datos", "Datos cargados correctamente desde los archivos CSV, JSON y XML.")
            } catch (e: Exception) {
                logger.error { "Error al cargar datos: ${e.message}" }
                showErrorDialog("Error", "No se pudieron cargar los datos: ${e.message}")
            }
        }

        // Configurar el evento del menú Exportar datos (solo admin)
        exportDataMenuItem.setOnAction {
            if (isAdmin) {
                showInfoDialog("Exportar datos", "Esta funcionalidad exportaría datos a un archivo.")
            } else {
                showLoginDialog("Exportar datos")
            }
        }

        // Configurar el evento del menú Importar datos (solo admin)
        importDataMenuItem.setOnAction {
            if (isAdmin) {
                showInfoDialog("Importar datos", "Esta funcionalidad importaría datos desde un archivo.")
            } else {
                showLoginDialog("Importar datos")
            }
        }

        // Configurar el evento del menú Imprimir HTML
        printHtmlMenuItem.setOnAction {
            try {
                // Crear un FileChooser para seleccionar dónde guardar el HTML
                val fileChooser = javafx.stage.FileChooser()
                fileChooser.title = "Guardar informe HTML"

                // Configurar filtros para archivos HTML
                fileChooser.extensionFilters.add(
                    javafx.stage.FileChooser.ExtensionFilter("Archivos HTML", "*.html")
                )

                // Generar nombre de archivo con timestamp
                val timestamp = LocalDateTime.now().toString().replace(":", "-").replace(".", "-")
                fileChooser.initialFileName = "plantilla_${timestamp}.html"

                // Mostrar el diálogo de selección de archivo
                val selectedFile = fileChooser.showSaveDialog(playerImageView.scene.window as Stage)

                if (selectedFile != null) {
                    // Generar el informe HTML en la ubicación seleccionada
                    val reportPath = HtmlReportGenerator.generateReport(personalList, selectedFile.absolutePath)

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
                }
            } catch (e: Exception) {
                logger.error { "Error al generar el informe HTML: ${e.message}" }
                showErrorDialog("Error", "No se pudo generar el informe HTML: ${e.message}")
            }
        }

        // Configurar el evento del menú Imprimir PDF
        printPdfMenuItem.setOnAction {
            try {
                // Crear un FileChooser para seleccionar dónde guardar el PDF
                val fileChooser = javafx.stage.FileChooser()
                fileChooser.title = "Guardar informe PDF"

                // Configurar filtros para archivos PDF
                fileChooser.extensionFilters.add(
                    javafx.stage.FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
                )

                // Generar nombre de archivo con timestamp
                val timestamp = LocalDateTime.now().toString().replace(":", "-").replace(".", "-")
                fileChooser.initialFileName = "plantilla_${timestamp}.pdf"

                // Mostrar el diálogo de selección de archivo
                val selectedFile = fileChooser.showSaveDialog(playerImageView.scene.window as Stage)

                if (selectedFile != null) {
                    // Generar el informe PDF en la ubicación seleccionada
                    val reportPath = PdfReportGenerator.generateReport(personalList, selectedFile.absolutePath)

                    // Abrir el informe en el visor de PDF predeterminado
                    val file = File(reportPath)
                    try {
                        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                            Desktop.getDesktop().open(file)
                            showInfoDialog(
                                "Informe PDF generado",
                                "El informe PDF ha sido generado y abierto en su visor de PDF predeterminado.\n\nRuta: $reportPath"
                            )
                        } else {
                            logger.error { "No se puede abrir el visor de PDF predeterminado" }
                            showInfoDialog(
                                "Informe PDF generado",
                                "El informe PDF ha sido generado pero no se pudo abrir automáticamente.\n\nRuta: $reportPath"
                            )
                        }
                    } catch (e: Exception) {
                        logger.error { "No se puede abrir el visor de PDF predeterminado: ${e.message}" }
                        showInfoDialog(
                            "Informe PDF generado",
                            "El informe PDF ha sido generado pero no se pudo abrir automáticamente.\n\nRuta: $reportPath"
                        )
                    }
                }
            } catch (e: Exception) {
                logger.error { "Error al generar el informe PDF: ${e.message}" }
                showErrorDialog("Error", "No se pudo generar el informe PDF: ${e.message}")
            }
        }

        // Configurar el evento del menú Ver convocatorias
        convocatoriasMenuItem.setOnAction {
            try {
                logger.debug { "Mostrando vista de convocatorias integrada" }
                // Ocultar la vista de jugadores y mostrar la vista de convocatorias
                jugadoresPane.isVisible = false
                convocatoriasPane.isVisible = true

                // Cargar los datos de convocatorias si es necesario
                loadConvocatorias()
            } catch (e: Exception) {
                logger.error { "Error al mostrar la vista de convocatorias: ${e.message}" }
                logger.error { "Stack trace: ${e.stackTraceToString()}" }
                showErrorDialog("Error", "No se pudo mostrar la vista de convocatorias: ${e.message}")
            }
        }

        // Configurar el botón para volver a la vista de jugadores
        backToPlayersButton.setOnAction {
            logger.debug { "Volviendo a la vista de jugadores" }
            convocatoriasPane.isVisible = false
            jugadoresPane.isVisible = true
        }

        // Configurar el botón cancelar de la vista de convocatorias
        cancelConvocatoriaButton.setOnAction {
            logger.debug { "Volviendo a la vista de jugadores desde cancelar" }
            convocatoriasPane.isVisible = false
            jugadoresPane.isVisible = true
        }

        // Configurar el evento del menú Cerrar
        closeMenuItem.setOnAction {
            RoutesManager.onLogout(
                title = "Cerrar sesión",
                headerText = "¿Estás seguro de que quieres cerrar sesión?",
                contentText = "Si cierras sesión, volverás a la pantalla de inicio de sesión."
            )
        }

        // Configurar el evento del menú Acerca De
        aboutMenuItem.setOnAction {
            showAboutDialog()
        }
    }

    private fun setupDetailsPanel() {
        // Configurar el Spinner de edad
        val valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(16, 60, 25)
        edadSpinner.valueFactory = valueFactory

        // Configurar el ComboBox de posición
        posicionComboBox.items.addAll(
            "PORTERO",
            "DEFENSA",
            "CENTROCAMPISTA",
            "DELANTERO"
        )

        // Configurar el ComboBox de especialización
        especialidadComboBox.items.addAll(
            "ENTRENADOR_PRINCIPAL",
            "ENTRENADOR_ASISTENTE",
            "ENTRENADOR_PORTEROS"
        )

        // Configurar el DatePicker
        fechaIncorporacionPicker.value = LocalDate.now()

        cancelButton.setOnAction {
            clearDetailsPanel()
        }

        // Inicialmente, deshabilitar los campos de edición para usuarios normales
        setFieldsEditable(isAdmin)

        // Cargar imagen por defecto
        loadDefaultImage()
    }

    private fun setupTableViewEvents() {
        // Configurar el evento de selección en la tabla
        playersTableView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                showPersonalDetails(newValue)
            }
        }
    }

    private fun showPersonalDetails(personal: Personal) {
        currentPersonal = personal

        // Cargar datos comunes
        nombreTextField.text = "${personal.nombre} ${personal.apellidos}"
        edadSpinner.valueFactory.value = Period.between(personal.fechaNacimiento, LocalDate.now()).years
        salarioTextField.text = personal.salario.toString()
        fechaIncorporacionPicker.value = personal.fechaIncorporacion

        // Cargar imagen
        loadPersonalImage(personal)

        // Configurar campos específicos según el tipo
        when (personal) {
            is Jugador -> {
                // Mostrar campos de jugador
                especialidadLabel.isVisible = false
                especialidadComboBox.isVisible = false

                posicionLabel.isVisible = true
                posicionComboBox.isVisible = true
                dorsalLabel.isVisible = true
                dorsalTextField.isVisible = true
                partidosLabel.isVisible = true
                partidosTextField.isVisible = true
                golesLabel.isVisible = true
                golesTextField.isVisible = true

                // Cargar datos específicos de jugador
                logger.debug { "Posición del jugador: ${personal.posicion.toString()}" }
                logger.debug { "Posición del jugador (name): ${personal.posicion.name}" }
                posicionComboBox.value = personal.posicion.name
                dorsalTextField.text = personal.dorsal.toString()
                golesTextField.text = personal.goles.toString()
            }
            is Entrenador -> {
                // Mostrar campos de entrenador
                especialidadLabel.isVisible = true
                especialidadComboBox.isVisible = true
                logger.debug { "especialidadLabel.isVisible: ${especialidadLabel.isVisible}" }
                logger.debug { "especialidadComboBox.isVisible: ${especialidadComboBox.isVisible}" }

                posicionLabel.isVisible = false
                posicionComboBox.isVisible = false
                dorsalLabel.isVisible = false
                dorsalTextField.isVisible = false
                partidosLabel.isVisible = false
                partidosTextField.isVisible = false
                golesLabel.isVisible = false
                golesTextField.isVisible = false

                // Cargar datos específicos de entrenador
                logger.debug { "Especialización del entrenador: ${personal.especializacion.toString()}" }
                logger.debug { "Especialización del entrenador (name): ${personal.especializacion.name}" }
                logger.debug { "especialidadComboBox items: ${especialidadComboBox.items}" }

                // Intentar establecer el valor del ComboBox
                try {
                    especialidadComboBox.value = personal.especializacion.name
                    logger.debug { "especialidadComboBox.value después de asignar: ${especialidadComboBox.value}" }
                } catch (e: Exception) {
                    logger.error { "Error al establecer especialidadComboBox.value: ${e.message}" }
                    e.printStackTrace()
                }

                // Verificar si el valor se estableció correctamente
                if (especialidadComboBox.value != personal.especializacion.name) {
                    logger.error { "El valor no se estableció correctamente. Intentando con setValue..." }
                    especialidadComboBox.setValue(personal.especializacion.name)
                    logger.debug { "especialidadComboBox.value después de setValue: ${especialidadComboBox.value}" }
                }
            }
        }
    }

    private fun loadPersonalImage(personal: Personal) {
        try {
            // Si el personal tiene una URL de imagen, intentar cargarla
            if (personal.imagenUrl.isNotEmpty()) {
                try {
                    // Intentar cargar la imagen desde la URL
                    val image = Image(personal.imagenUrl)
                    if (!image.isError) {
                        playerImageView.image = image
                        return
                    }
                } catch (e: Exception) {
                    logger.error { "Error al cargar la imagen desde URL: ${e.message}" }
                    // Si hay error, continuar con la imagen por defecto
                }
            }

            // Si no hay URL o hubo error, cargar imagen por defecto según el tipo
            val imagePath = when (personal) {
                is Jugador -> "icons/player.png"
                is Entrenador -> "icons/coach.png"
                else -> "icons/person.png"
            }

            val imageStream = NewTeamApplication::class.java.getResourceAsStream(imagePath)
            if (imageStream != null) {
                playerImageView.image = Image(imageStream)
            } else {
                loadDefaultImage()
            }
        } catch (e: Exception) {
            logger.error { "Error al cargar la imagen: ${e.message}" }
            loadDefaultImage()
        }
    }

    private fun loadDefaultImage() {
        try {
            val imageUrl = NewTeamApplication::class.java.getResource("icons/newTeamLogo.png")
            if (imageUrl != null) {
                playerImageView.image = Image(imageUrl.toString())
            }
        } catch (e: Exception) {
            logger.error { "Error al cargar la imagen por defecto: ${e.message}" }
        }
    }

    private fun clearDetailsPanel() {
        currentPersonal = null
        nombreTextField.clear()
        edadSpinner.valueFactory.value = 25
        salarioTextField.clear()
        especialidadComboBox.value = null
        posicionComboBox.value = null
        dorsalTextField.clear()
        fechaIncorporacionPicker.value = LocalDate.now()
        partidosTextField.clear()
        golesTextField.clear()
        loadDefaultImage()
    }

    private fun setFieldsEditable(editable: Boolean) {
        nombreTextField.isEditable = editable
        edadSpinner.isDisable = !editable
        salarioTextField.isEditable = editable
        especialidadComboBox.isDisable = !editable
        posicionComboBox.isDisable = !editable
        dorsalTextField.isEditable = editable
        fechaIncorporacionPicker.isDisable = !editable
        partidosTextField.isEditable = editable
        golesTextField.isEditable = editable
    }

    private fun updateStatistics() {
        // Calcular estadísticas solo para jugadores
        val jugadores = personalList.filterIsInstance<Jugador>()

        if (jugadores.isNotEmpty()) {
            val avgGoles = jugadores.map { it.goles }.average()

            avgGolesLabel.text = String.format("%.2f", avgGoles)
        } else {
            avgGolesLabel.text = "0"
        }
    }

    private fun showLoginDialog(action: String) {
        logger.debug { "Mostrando diálogo de login para acción: $action" }

        // Mostrar diálogo de login para acciones de administrador
        val usernameDialog = TextInputDialog()
        usernameDialog.title = "Login de Administrador"
        usernameDialog.headerText = "Se requieren credenciales de administrador para $action"
        usernameDialog.contentText = "Usuario:"

        usernameDialog.showAndWait().ifPresent { username ->
            if (username.isNotBlank()) {
                val passwordDialog = TextInputDialog()
                passwordDialog.title = "Login de Administrador"
                passwordDialog.headerText = "Introduce la contraseña para $username"
                passwordDialog.contentText = "Contraseña:"

                passwordDialog.showAndWait().ifPresent { password ->
                    // Verificar credenciales usando el repositorio de usuarios
                    val userRepository = UserRepositoryImpl()
                    val user = userRepository.verifyCredentials(username, password)

                    if (user != null && user.role == User.Role.ADMIN) {
                        // Actualizar la sesión con el nuevo usuario
                        Session.setCurrentUser(user)
                        isAdmin = true
                        setFieldsEditable(true)
                        showInfoDialog("Acceso concedido", "Ahora tienes acceso de administrador.")
                    } else {
                        showErrorDialog("Acceso denegado", "Credenciales incorrectas o usuario sin permisos de administrador.")
                    }
                }
            }
        }
    }

    private fun showInfoDialog(title: String, message: String) {
        Alert(Alert.AlertType.INFORMATION).apply {
            this.title = title
            headerText = null
            contentText = message
        }.showAndWait()
    }

    private fun showErrorDialog(title: String, message: String) {
        Alert(Alert.AlertType.ERROR).apply {
            this.title = title
            headerText = null
            contentText = message
        }.showAndWait()
    }

    private fun showAboutDialog() {
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Acerca De"
            headerText = "Gestor de Jugadores de Fútbol"
            contentText = "Versión 1.0\n" +
                    "Desarrolladores:\n" +
                    "- Ángel Sánchez Gasanz\n" +
                    "- Jorge Morgado Jimenez"
        }.showAndWait()
    }

    /**
     * Carga las convocatorias desde el repositorio.
     */
    private fun loadConvocatorias() {
        try {
            logger.debug { "Cargando convocatorias" }
            convocatorias.clear()
            convocatorias.addAll(convocatoriaRepository.getAll())

            // Configurar las columnas de la tabla si no se ha hecho antes
            if (idConvocatoriaColumn.cellValueFactory == null) {
                initializeConvocatoriasTable()
            }

            // Asignar la lista observable a la tabla
            convocatoriasTableView.items = convocatorias
        } catch (e: Exception) {
            logger.error { "Error al cargar las convocatorias: ${e.message}" }
            showErrorDialog("Error", "Error al cargar las convocatorias: ${e.message}")
        }
    }

    /**
     * Inicializa la tabla de convocatorias.
     */
    private fun initializeConvocatoriasTable() {
        // Configurar las columnas de la tabla
        idConvocatoriaColumn.cellValueFactory = javafx.scene.control.cell.PropertyValueFactory("id")

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

        // Configurar el evento de selección de la tabla
        convocatoriasTableView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                logger.debug { "Convocatoria seleccionada: ${newValue.id}" }
                showConvocatoriaDetails(newValue)
                printConvocatoriaButton.isDisable = false
            } else {
                clearConvocatoriaDetailsPanel()
                printConvocatoriaButton.isDisable = true
            }
        }

        // Configurar el campo de búsqueda
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
     * Muestra los detalles de una convocatoria.
     */
    private fun showConvocatoriaDetails(convocatoria: Convocatoria) {
        currentConvocatoria = convocatoria

        // Mostrar los datos de la convocatoria
        fechaConvocatoriaPicker.value = convocatoria.fecha
        descripcionTextArea.text = convocatoria.descripcion

        // Mostrar el entrenador
        val personalRepository = PersonalRepositoryImpl()
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

        // Inicializar la tabla de jugadores convocados si no se ha hecho antes
        if (idJugadorColumn.cellValueFactory == null) {
            initializeJugadoresConvocadosTable()
        }

        // Obtener los jugadores por sus IDs
        val personalRepository = PersonalRepositoryImpl()
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
     * Inicializa la tabla de jugadores convocados.
     */
    private fun initializeJugadoresConvocadosTable() {
        // Configurar las columnas de la tabla
        idJugadorColumn.cellValueFactory = javafx.scene.control.cell.PropertyValueFactory("id")

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
     * Limpia el panel de detalles de la convocatoria.
     */
    private fun clearConvocatoriaDetailsPanel() {
        currentConvocatoria = null
        fechaConvocatoriaPicker.value = null
        entrenadorTextField.text = ""
        descripcionTextArea.text = ""
        jugadoresConvocados.clear()
    }
}
