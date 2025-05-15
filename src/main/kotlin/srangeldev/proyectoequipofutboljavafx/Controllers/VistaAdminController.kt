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
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.NewTeamApplication
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepositoryImpl
import srangeldev.session.Session
import srangeldev.theme.Theme
import srangeldev.utils.HtmlReportGenerator
import java.awt.Desktop
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period



/**
 * Controlador para la vista de administración
 */
class VistaAdminController {
    private val logger = logging()
    private val userRepository: UserRepository = UserRepositoryImpl()

    // Jugadores Tab
    @FXML private lateinit var searchField: TextField
    @FXML private lateinit var playerToggleButton: ToggleButton
    @FXML private lateinit var coachToggleButton: ToggleButton
    @FXML private lateinit var filterToggleGroup: ToggleGroup
    @FXML private lateinit var playersTableView: TableView<Personal>
    @FXML private lateinit var idColumn: TableColumn<Personal, Int>
    @FXML private lateinit var nombreColumn: TableColumn<Personal, String>
    @FXML private lateinit var apellidosColumn: TableColumn<Personal, String>
    @FXML private lateinit var avgMinutosLabel: Label
    @FXML private lateinit var avgGolesLabel: Label
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
    @FXML private lateinit var minutosLabel: Label
    @FXML private lateinit var minutosTextField: TextField
    @FXML private lateinit var saveButton: Button
    @FXML private lateinit var cancelButton: Button
    @FXML private lateinit var addPlayerButton: Button
    @FXML private lateinit var deletePlayerButton: Button

    // Menu Items
    @FXML private lateinit var loadDataMenuItem: MenuItem
    @FXML private lateinit var exportDataMenuItem: MenuItem
    @FXML private lateinit var importDataMenuItem: MenuItem
    @FXML private lateinit var printHtmlMenuItem: MenuItem
    @FXML private lateinit var closeMenuItem: MenuItem
    @FXML private lateinit var aboutMenuItem: MenuItem
    @FXML private lateinit var toggleThemeMenuItem: MenuItem

    // Usuarios Tab
    @FXML private lateinit var searchUserField: TextField
    @FXML private lateinit var usersTableView: TableView<User>
    @FXML private lateinit var userIdColumn: TableColumn<User, Int>
    @FXML private lateinit var usernameColumn: TableColumn<User, String>
    @FXML private lateinit var roleColumn: TableColumn<User, String>
    @FXML private lateinit var usernameTextField: TextField
    @FXML private lateinit var passwordTextField: TextField
    @FXML private lateinit var roleComboBox: ComboBox<String>
    @FXML private lateinit var saveUserButton: Button
    @FXML private lateinit var cancelUserButton: Button
    @FXML private lateinit var addUserButton: Button
    @FXML private lateinit var deleteUserButton: Button

    // Configuración Tab
    @FXML private lateinit var dataDirectoryField: TextField
    @FXML private lateinit var backupDirectoryField: TextField
    @FXML private lateinit var databaseUrlField: TextField
    @FXML private lateinit var initTablesYesRadio: RadioButton
    @FXML private lateinit var initTablesNoRadio: RadioButton
    @FXML private lateinit var initDataYesRadio: RadioButton
    @FXML private lateinit var initDataNoRadio: RadioButton
    @FXML private lateinit var browseDataDirButton: Button
    @FXML private lateinit var browseBackupDirButton: Button
    @FXML private lateinit var saveConfigButton: Button
    @FXML private lateinit var resetConfigButton: Button

    private val personalList: ObservableList<Personal> = FXCollections.observableArrayList()
    private val filteredPersonalList: FilteredList<Personal> = FilteredList(personalList) { true }
    private val usersList: ObservableList<User> = FXCollections.observableArrayList()
    private val filteredUsersList: FilteredList<User> = FilteredList(usersList) { true }
    private var selectedPersonal: Personal? = null
    private var selectedUser: User? = null
    private var isEditingUser = false

    @FXML
    private fun initialize() {
        logger.debug { "Inicializando VistaAdminController" }

        // Inicializar componentes de la pestaña de jugadores
        initializePersonalTab()

        // Inicializar componentes de la pestaña de usuarios
        initializeUsersTab()

        // Inicializar componentes de la pestaña de configuración
        initializeConfigTab()

        // Configurar eventos del menú
        setupMenuItems()

        // Aplicar el tema actual
        playersTableView.scene?.let { scene ->
            // Inicializar el texto del menú según el tema actual
            toggleThemeMenuItem.text = if (Theme.isDarkTheme()) "Cambiar a tema claro" else "Cambiar a tema oscuro"
            // Aplicar el tema
            Theme.applyTheme(scene)
        }
    }

    private fun initializePersonalTab() {
        // Inicializar la tabla de personal
        initializePersonalTable()

        // Configurar filtros
        setupFilters()

        // Configurar campo de búsqueda
        setupSearchField()

        // Configurar panel de detalles
        setupDetailsPanel()

        // Configurar eventos de la tabla
        setupTableViewEvents()

        // Configurar botones de añadir y eliminar jugador
        setupPlayerButtons()
    }

    private fun initializePersonalTable() {
        idColumn.cellValueFactory = PropertyValueFactory("id")
        nombreColumn.cellValueFactory = PropertyValueFactory("nombre")
        apellidosColumn.cellValueFactory = PropertyValueFactory("apellidos")

        playersTableView.items = filteredPersonalList

        // Actualizar estadísticas cuando cambie la selección
        playersTableView.selectionModel.selectedItemProperty().addListener { _, _, _ ->
            updateStatistics()
        }
    }

    private fun setupFilters() {
        playerToggleButton.setOnAction {
            applyFilters(searchField.text)
        }

        coachToggleButton.setOnAction {
            applyFilters(searchField.text)
        }
    }

    private fun setupSearchField() {
        searchField.textProperty().addListener { _, _, newValue ->
            applyFilters(newValue)
        }
    }

    private fun applyFilters(searchText: String) {
        filteredPersonalList.setPredicate { personal ->
            // Filtrar por tipo (jugador o entrenador)
            val typeMatches = when {
                playerToggleButton.isSelected -> personal is Jugador
                coachToggleButton.isSelected -> personal is Entrenador
                else -> true
            }

            // Filtrar por texto de búsqueda
            val searchMatches = searchText.isEmpty() || 
                    personal.nombre.contains(searchText, ignoreCase = true) ||
                    personal.apellidos.contains(searchText, ignoreCase = true) ||
                    personal.id.toString() == searchText

            typeMatches && searchMatches
        }

        updateStatistics()
    }

    private fun setupDetailsPanel() {
        // Configurar el Spinner de edad
        val valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(16, 70, 25)
        edadSpinner.valueFactory = valueFactory

        // Configurar ComboBox de especialidad para entrenadores
        especialidadComboBox.items.addAll("Físico", "Táctico", "Porteros", "Principal")

        // Configurar ComboBox de posición para jugadores
        posicionComboBox.items.addAll("Portero", "Defensa", "Centrocampista", "Delantero")

        // Configurar DatePicker
        fechaIncorporacionPicker.value = LocalDate.now()

        // Configurar botones
        saveButton.setOnAction {
            savePersonalData()
        }

        cancelButton.setOnAction {
            clearDetailsPanel()
        }
    }

    private fun setupTableViewEvents() {
        playersTableView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                selectedPersonal = newValue
                showPersonalDetails(newValue)
            }
        }
    }

    private fun setupPlayerButtons() {
        addPlayerButton.setOnAction {
            clearDetailsPanel()
            setFieldsEditable(true)
            selectedPersonal = null
        }

        deletePlayerButton.setOnAction {
            val selected = playersTableView.selectionModel.selectedItem
            if (selected != null) {
                personalList.remove(selected)
                clearDetailsPanel()
                updateStatistics()
            } else {
                showInfoDialog("Selección requerida", "Por favor, seleccione un jugador o entrenador para eliminar.")
            }
        }
    }

    private fun showPersonalDetails(personal: Personal) {
        // Mostrar datos comunes
        nombreTextField.text = "${personal.nombre} ${personal.apellidos}"
        edadSpinner.valueFactory.value = Period.between(personal.fechaNacimiento, LocalDate.now()).years
        salarioTextField.text = personal.salario.toString()
        fechaIncorporacionPicker.value = personal.fechaIncorporacion

        // Limpiar y ocultar campos específicos
        especialidadLabel.isVisible = false
        especialidadComboBox.isVisible = false
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

        // Mostrar campos específicos según el tipo
        when (personal) {
            is Jugador -> {
                posicionLabel.isVisible = true
                posicionComboBox.isVisible = true
                posicionComboBox.value = personal.posicion.toString()

                dorsalLabel.isVisible = true
                dorsalTextField.isVisible = true
                dorsalTextField.text = personal.dorsal.toString()

                partidosLabel.isVisible = true
                partidosTextField.isVisible = true
                partidosTextField.text = personal.partidosJugados.toString()

                golesLabel.isVisible = true
                golesTextField.isVisible = true
                golesTextField.text = personal.goles.toString()

                minutosLabel.isVisible = true
                minutosTextField.isVisible = true
                //minutosTextField.text = personal.minutosJugados.toString()
            }
            is Entrenador -> {
                especialidadLabel.isVisible = true
                especialidadComboBox.isVisible = true
                especialidadComboBox.value = personal.especializacion.toString()
            }
        }

        // Cargar imagen
        loadPersonalImage(personal)
    }

    private fun loadPersonalImage(personal: Personal) {
        try {
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
            val imageStream = NewTeamApplication::class.java.getResourceAsStream("icons/person.png")
            if (imageStream != null) {
                playerImageView.image = Image(imageStream)
            }
        } catch (e: Exception) {
            logger.error { "Error al cargar la imagen por defecto: ${e.message}" }
        }
    }

    private fun savePersonalData() {
        // Implementar lógica para guardar datos
        showInfoDialog("Datos guardados", "Los datos se han guardado correctamente.")
        clearDetailsPanel()
    }

    private fun clearDetailsPanel() {
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
        playersTableView.selectionModel.clearSelection()
        selectedPersonal = null
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
    }

    private fun updateStatistics() {
        // Calcular estadísticas solo para jugadores
        val jugadores = filteredPersonalList.filterIsInstance<Jugador>()

        if (jugadores.isNotEmpty()) {
            //val avgMinutos = jugadores.map { it.minutosJugados }.average()
            val avgGoles = jugadores.map { it.goles }.average()

            //avgMinutosLabel.text = String.format("%.1f", avgMinutos)
            avgGolesLabel.text = String.format("%.1f", avgGoles)
        } else {
            avgMinutosLabel.text = "0"
            avgGolesLabel.text = "0"
        }
    }

    private fun initializeUsersTab() {
        // Configurar tabla de usuarios
        userIdColumn.cellValueFactory = PropertyValueFactory("id")
        usernameColumn.cellValueFactory = PropertyValueFactory("username")
        roleColumn.cellValueFactory = PropertyValueFactory("role")

        // Cargar usuarios
        loadUsers()

        // Configurar ComboBox de roles
        roleComboBox.items.addAll("Administrador", "Usuario")

        // Configurar eventos
        setupUserEvents()
    }

    private fun loadUsers() {
        usersList.clear()

        // Cargar usuarios manualmente ya que no hay método findAll
        val adminUser = userRepository.getByUsername("admin")
        val normalUser = userRepository.getByUsername("user")

        if (adminUser != null) usersList.add(adminUser)
        if (normalUser != null) usersList.add(normalUser)

        usersTableView.items = filteredUsersList

        // Configurar búsqueda de usuarios
        searchUserField.textProperty().addListener { _, _, newValue ->
            filteredUsersList.setPredicate { user ->
                newValue.isEmpty() || user.username.contains(newValue, ignoreCase = true)
            }
        }
    }

    private fun setupUserEvents() {
        // Evento de selección de usuario
        usersTableView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                selectedUser = newValue
                showUserDetails(newValue)
            }
        }

        // Botón añadir usuario
        addUserButton.setOnAction {
            clearUserForm()
            isEditingUser = false
            selectedUser = null
        }

        // Botón eliminar usuario
        deleteUserButton.setOnAction {
            val selected = usersTableView.selectionModel.selectedItem
            if (selected != null) {
                // No permitir eliminar al usuario actual
                if (selected.id == Session.getCurrentUser()?.id) {
                    showErrorDialog("Operación no permitida", "No puede eliminar su propio usuario.")
                    return@setOnAction
                }

                // No podemos eliminar usuarios ya que no hay método delete
                showErrorDialog("Operación no soportada", "La eliminación de usuarios no está implementada en esta versión.")

                // Recargar usuarios
                loadUsers()
                clearUserForm()
            } else {
                showInfoDialog("Selección requerida", "Por favor, seleccione un usuario para eliminar.")
            }
        }

        // Botón guardar usuario
        saveUserButton.setOnAction {
            saveUserData()
        }

        // Botón cancelar
        cancelUserButton.setOnAction {
            clearUserForm()
        }
    }

    private fun showUserDetails(user: User) {
        usernameTextField.text = user.username
        passwordTextField.clear() // Por seguridad no mostramos la contraseña
        roleComboBox.value = if (user.role == User.Role.ADMIN) "Administrador" else "Usuario"
        isEditingUser = true
    }

    private fun saveUserData() {
        val username = usernameTextField.text.trim()
        val password = passwordTextField.text.trim()
        val role = if (roleComboBox.value == "Administrador") User.Role.ADMIN else User.Role.USER

        if (username.isEmpty()) {
            showErrorDialog("Datos incompletos", "El nombre de usuario es obligatorio.")
            return
        }

        try {
            if (isEditingUser && selectedUser != null) {
                // No podemos actualizar usuarios ya que no hay método update
                showErrorDialog("Operación no soportada", "La actualización de usuarios no está implementada en esta versión.")
            } else {
                // Crear nuevo usuario
                if (password.isEmpty()) {
                    showErrorDialog("Datos incompletos", "La contraseña es obligatoria para nuevos usuarios.")
                    return
                }

                val newUser = User(
                    id = 0, // El repositorio asignará un ID
                    username = username,
                    password = password,
                    role = role
                )

                userRepository.save(newUser)
                showInfoDialog("Operación exitosa", "Usuario creado correctamente.")
            }

            loadUsers()
            clearUserForm()
        } catch (e: Exception) {
            showErrorDialog("Error", "No se pudo guardar el usuario: ${e.message}")
        }
    }

    private fun clearUserForm() {
        usernameTextField.clear()
        passwordTextField.clear()
        roleComboBox.value = null
        usersTableView.selectionModel.clearSelection()
        selectedUser = null
        isEditingUser = false
    }

    private fun initializeConfigTab() {
        // Cargar configuración actual
        loadCurrentConfig()

        // Configurar botones de examinar directorios
        browseDataDirButton.setOnAction {
            browseDirectory("Seleccionar directorio de datos") { dir ->
                dataDirectoryField.text = dir.absolutePath
            }
        }

        browseBackupDirButton.setOnAction {
            browseDirectory("Seleccionar directorio de respaldo") { dir ->
                backupDirectoryField.text = dir.absolutePath
            }
        }

        // Configurar botones de guardar y restablecer
        saveConfigButton.setOnAction {
            saveConfiguration()
        }

        resetConfigButton.setOnAction {
            loadDefaultConfig()
        }
    }

    private fun loadCurrentConfig() {
        val config = Config.configProperties

        dataDirectoryField.text = config.dataDir
        backupDirectoryField.text = config.backupDir
        databaseUrlField.text = config.databaseUrl

        initTablesYesRadio.isSelected = config.databaseInitTables
        initTablesNoRadio.isSelected = !config.databaseInitTables

        initDataYesRadio.isSelected = config.databaseInitData
        initDataNoRadio.isSelected = !config.databaseInitData
    }

    private fun loadDefaultConfig() {
        dataDirectoryField.text = "data"
        backupDirectoryField.text = "backup"
        databaseUrlField.text = "jdbc:sqlite:equipo.db"
        initTablesYesRadio.isSelected = true
        initDataYesRadio.isSelected = true
    }

    private fun saveConfiguration() {
        try {
            // En lugar de actualizar la configuración (ya que no hay método updateProperties),
            // mostramos un mensaje indicando que la operación no está soportada
            showInfoDialog("Operación no soportada", 
                "La actualización de la configuración no está implementada en esta versión.\n\n" +
                "Para cambiar la configuración, edite manualmente el archivo config.properties.")
        } catch (e: Exception) {
            showErrorDialog("Error", "No se pudo guardar la configuración: ${e.message}")
        }
    }

    private fun browseDirectory(title: String, onSelected: (File) -> Unit) {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = title

        val stage = playerImageView.scene.window as Stage
        val selectedDirectory = directoryChooser.showDialog(stage)

        if (selectedDirectory != null) {
            onSelected(selectedDirectory)
        }
    }

    private fun setupMenuItems() {
        // Cargar datos
        loadDataMenuItem.setOnAction {
           // addSampleData()
        }

        // Exportar datos
        exportDataMenuItem.setOnAction {
            showInfoDialog("Exportar datos", "Funcionalidad de exportación de datos.")
        }

        // Importar datos
        importDataMenuItem.setOnAction {
            showInfoDialog("Importar datos", "Funcionalidad de importación de datos.")
        }

        // Imprimir HTML
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

        // Cerrar sesión
        closeMenuItem.setOnAction {
            RoutesManager.onAppExit(
                "Cerrar sesión",
                "¿Estás seguro de que quieres cerrar sesión?",
                "Se cerrará la sesión actual."
            )
        }

        // Acerca de
        aboutMenuItem.setOnAction {
            showAboutDialog()
        }

        // Cambiar tema
        toggleThemeMenuItem.setOnAction {
            // Obtener la escena actual
            val scene = toggleThemeMenuItem.parentPopup.ownerWindow.scene
            // Cambiar el tema
            Theme.toggleTheme(scene)
            // Actualizar el texto del menú según el tema actual
            toggleThemeMenuItem.text = if (Theme.isDarkTheme()) "Cambiar a tema claro" else "Cambiar a tema oscuro"
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
            title = "Acerca de"
            headerText = "Gestión de Equipo de Fútbol"
            contentText = "Aplicación para la gestión de un equipo de fútbol.\n" +
                    "Versión: 1.0\n" +
                    "Desarrollado por: Equipo de Desarrollo"
        }.showAndWait()
    }
}
