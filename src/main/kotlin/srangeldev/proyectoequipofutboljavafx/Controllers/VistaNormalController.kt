package srangeldev.proyectoequipofutboljavafx.Controllers

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import org.lighthousegames.logging.logging
import srangeldev.controller.Controller
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.proyectoequipofutboljavafx.NewTeamApplication
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepositoryImpl
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager
import srangeldev.session.Session
import srangeldev.storage.FileFormat
import srangeldev.utils.HtmlReportGenerator
import java.awt.Desktop
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

class VistaNormalController {
    private val logger = logging()

    // Panel izquierdo - Tabla de jugadores
    @FXML
    private lateinit var playersTableView: TableView<Personal>

    @FXML
    private lateinit var idColumn: TableColumn<Personal, Int>

    @FXML
    private lateinit var nombreColumn: TableColumn<Personal, String>

    @FXML
    private lateinit var apellidosColumn: TableColumn<Personal, String>

    @FXML
    private lateinit var searchField: TextField

    @FXML
    private lateinit var allToggleButton: ToggleButton

    @FXML
    private lateinit var playerToggleButton: ToggleButton

    @FXML
    private lateinit var coachToggleButton: ToggleButton

    @FXML
    private lateinit var avgMinutosLabel: Label

    @FXML
    private lateinit var avgGolesLabel: Label

    // Panel derecho - Detalles del jugador
    @FXML
    private lateinit var playerImageView: ImageView

    @FXML
    private lateinit var nombreTextField: TextField

    @FXML
    private lateinit var edadSpinner: Spinner<Int>

    @FXML
    private lateinit var salarioTextField: TextField

    @FXML
    private lateinit var especialidadLabel: Label

    @FXML
    private lateinit var especialidadComboBox: ComboBox<String>

    @FXML
    private lateinit var posicionLabel: Label

    @FXML
    private lateinit var posicionComboBox: ComboBox<String>

    @FXML
    private lateinit var dorsalLabel: Label

    @FXML
    private lateinit var dorsalTextField: TextField

    @FXML
    private lateinit var fechaIncorporacionPicker: DatePicker

    @FXML
    private lateinit var partidosLabel: Label

    @FXML
    private lateinit var partidosTextField: TextField

    @FXML
    private lateinit var golesLabel: Label

    @FXML
    private lateinit var golesTextField: TextField

    @FXML
    private lateinit var minutosLabel: Label

    @FXML
    private lateinit var minutosTextField: TextField

    @FXML
    private lateinit var saveButton: Button

    @FXML
    private lateinit var cancelButton: Button

    // Menú
    @FXML
    private lateinit var loadDataMenuItem: MenuItem

    @FXML
    private lateinit var exportDataMenuItem: MenuItem

    @FXML
    private lateinit var importDataMenuItem: MenuItem

    @FXML
    private lateinit var printHtmlMenuItem: MenuItem

    @FXML
    private lateinit var closeMenuItem: MenuItem

    @FXML
    private lateinit var aboutMenuItem: MenuItem

    @FXML
    private lateinit var toggleThemeMenuItem: MenuItem

    // Datos
    private val personalList = FXCollections.observableArrayList<Personal>()
    private lateinit var filteredPersonal: FilteredList<Personal>
    private var currentPersonal: Personal? = null
    private var isAdmin = false

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

        // Actualizar estadísticas
        updateStatistics()
    }

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

    private fun initializePersonalTable() {
        // Configurar las columnas de la tabla
        idColumn.setCellValueFactory { cellData -> SimpleIntegerProperty(cellData.value.id).asObject() }
        nombreColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.nombre) }
        apellidosColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.apellidos) }

        // Crear una lista filtrada basada en la lista original
        filteredPersonal = FilteredList(personalList) { true }

        // Asignar la lista filtrada a la TableView
        playersTableView.setItems(filteredPersonal)
    }

    /**
     * Carga los datos de personal desde la base de datos y actualiza la UI.
     */
    private fun loadPersonalFromDatabase() {
        try {
            logger.debug { "Cargando datos de personal desde la base de datos" }

            // Crear una instancia del servicio
            val service = srangeldev.service.PersonalServiceImpl()

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
        filteredPersonal.setPredicate(object : java.util.function.Predicate<Personal> {
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
        })
    }

    private fun setupMenuItems() {
        // Configurar el evento del menú Cargar datos
        loadDataMenuItem.setOnAction {
            try {
                // Crear una instancia del controlador
                val controller = Controller()

                // Cargar datos desde los archivos CSV, JSON y XML
                controller.cargarDatos("CSV")
                controller.cargarDatos("JSON")
                controller.cargarDatos("XML")

                // Actualizar la lista de personal con los datos cargados
                loadPersonalFromDatabase()

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
                // Crear directorio de informes si no existe
                val reportsDir = File("reports")
                if (!reportsDir.exists()) {
                    reportsDir.mkdirs()
                }

                // Generar nombre de archivo con timestamp
                val timestamp = LocalDateTime.now().toString().replace(":", "-").replace(".", "-")
                val outputPath = "reports/plantilla_${timestamp}.html"

                // Generar el informe HTML
                val reportPath = HtmlReportGenerator.generateReport(personalList, outputPath)

                // Abrir el informe en el navegador predeterminado
                val file = File(reportPath)
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(file.toURI())
                        showInfoDialog("Informe HTML generado", "El informe HTML ha sido generado y abierto en su navegador predeterminado.\n\nRuta: $reportPath")
                    } else {
                        logger.error { "No se puede abrir el navegador predeterminado" }
                        showInfoDialog("Informe HTML generado", "El informe HTML ha sido generado pero no se pudo abrir automáticamente.\n\nRuta: $reportPath")
                    }
                } catch (e: Exception) {
                    logger.error { "No se puede abrir el navegador predeterminado: ${e.message}" }
                    showInfoDialog("Informe HTML generado", "El informe HTML ha sido generado pero no se pudo abrir automáticamente.\n\nRuta: $reportPath")
                }
            } catch (e: Exception) {
                logger.error { "Error al generar el informe HTML: ${e.message}" }
                showErrorDialog("Error", "No se pudo generar el informe HTML: ${e.message}")
            }
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

        // Configurar los botones
        saveButton.setOnAction {
            savePersonalData()
        }

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
                minutosLabel.isVisible = true
                minutosTextField.isVisible = true

                // Cargar datos específicos de jugador
                posicionComboBox.value = personal.posicion.toString()
                dorsalTextField.text = personal.dorsal.toString()
                partidosTextField.text = personal.partidosJugados.toString()
                golesTextField.text = personal.goles.toString()
                //minutosTextField.text = personal.minutosJugados.toString()
            }
            is Entrenador -> {
                // Mostrar campos de entrenador
                especialidadLabel.isVisible = true
                especialidadComboBox.isVisible = true

                posicionLabel.isVisible = false
                posicionComboBox.isVisible = false
                dorsalLabel.isVisible = false
                dorsalTextField.isVisible = false
                partidosLabel.isVisible = false
                partidosTextField.isVisible = false
                golesLabel.isVisible = false
                golesTextField.isVisible = false
                minutosLabel.isVisible = false
                minutosTextField.isVisible = false

                // Cargar datos específicos de entrenador
                especialidadComboBox.value = personal.especializacion.toString()
            }
        }
    }

    private fun loadPersonalImage(personal: Personal) {
        try {
            // En una implementación real, cargaríamos la imagen desde un archivo o base de datos
            // Por ahora, usamos una imagen de ejemplo
            val imageUrl = NewTeamApplication::class.java.getResource("icons/newTeamLogo.png")
            if (imageUrl != null) {
                playerImageView.image = Image(imageUrl.toString())
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

    private fun savePersonalData() {
        // En una implementación real, guardaríamos los datos en la base de datos
        // Por ahora, solo mostramos un mensaje
        showInfoDialog("Guardar datos", "Esta funcionalidad guardaría los datos en la base de datos.")

        // Limpiar el panel de detalles
        clearDetailsPanel()
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
        minutosTextField.clear()
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
        minutosTextField.isEditable = editable
        saveButton.isDisable = !editable
    }

    private fun updateStatistics() {
        // Calcular estadísticas solo para jugadores
        val jugadores = personalList.filterIsInstance<Jugador>()

        if (jugadores.isNotEmpty()) {
            //val avgMinutos = jugadores.map { it.minutosJugados }.average()
            val avgGoles = jugadores.map { it.goles }.average()

            //avgMinutosLabel.text = String.format("%.2f", avgMinutos)
            avgGolesLabel.text = String.format("%.2f", avgGoles)
        } else {
            avgMinutosLabel.text = "0"
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
                    "- Ángel Sánchez Gasanz" +
                    "- Jorge Morgado Giménez" +
                    "- Antoine López"
        }.showAndWait()
    }
}
