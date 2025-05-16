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
    @FXML private lateinit var passwordColumn: TableColumn<User, String>
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
        try {
            // Verificar si hay un personal seleccionado
            if (selectedPersonal == null) {
                showErrorDialog("Error", "No hay ningún personal seleccionado para editar.")
                return
            }

            // Crear una instancia del servicio
            val service = srangeldev.service.PersonalServiceImpl()

            // Obtener los datos comunes del formulario
            val nombreCompleto = nombreTextField.text.trim().split(" ", limit = 2)
            val nombre = nombreCompleto.getOrElse(0) { "" }
            val apellidos = nombreCompleto.getOrElse(1) { "" }

            // Calcular la fecha de nacimiento a partir de la edad
            val edad = edadSpinner.value
            val fechaNacimiento = LocalDate.now().minusYears(edad.toLong())

            val salario = salarioTextField.text.toDoubleOrNull() ?: 0.0
            val fechaIncorporacion = fechaIncorporacionPicker.value ?: LocalDate.now()

            // Crear el objeto Personal según el tipo
            val updatedPersonal = when (selectedPersonal) {
                is Jugador -> {
                    val posicionStr = posicionComboBox.value ?: "CENTROCAMPISTA"
                    val posicion = try {
                        Jugador.Posicion.valueOf(posicionStr)
                    } catch (e: IllegalArgumentException) {
                        Jugador.Posicion.CENTROCAMPISTA
                    }

                    val dorsal = dorsalTextField.text.toIntOrNull() ?: 0
                    val partidosJugados = partidosTextField.text.toIntOrNull() ?: 0
                    val goles = golesTextField.text.toIntOrNull() ?: 0

                    // Crear un nuevo Jugador con los datos actualizados
                    Jugador(
                        id = selectedPersonal!!.id,
                        nombre = nombre,
                        apellidos = apellidos,
                        fechaNacimiento = fechaNacimiento,
                        fechaIncorporacion = fechaIncorporacion,
                        salario = salario,
                        paisOrigen = selectedPersonal!!.paisOrigen, // Mantener el país de origen original
                        createdAt = selectedPersonal!!.createdAt,
                        updatedAt = LocalDateTime.now(),
                        posicion = posicion,
                        dorsal = dorsal,
                        altura = (selectedPersonal as Jugador).altura,
                        peso = (selectedPersonal as Jugador).peso,
                        goles = goles,
                        partidosJugados = partidosJugados
                    )
                }
                is Entrenador -> {
                    val especializacionStr = especialidadComboBox.value ?: "ENTRENADOR_PRINCIPAL"
                    val especializacion = try {
                        Entrenador.Especializacion.valueOf(especializacionStr)
                    } catch (e: IllegalArgumentException) {
                        Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
                    }

                    // Crear un nuevo Entrenador con los datos actualizados
                    Entrenador(
                        id = selectedPersonal!!.id,
                        nombre = nombre,
                        apellidos = apellidos,
                        fechaNacimiento = fechaNacimiento,
                        fechaIncorporacion = fechaIncorporacion,
                        salario = salario,
                        paisOrigen = selectedPersonal!!.paisOrigen, // Mantener el país de origen original
                        createdAt = selectedPersonal!!.createdAt,
                        updatedAt = LocalDateTime.now(),
                        especializacion = especializacion
                    )
                }
                else -> throw IllegalStateException("Tipo de personal no soportado")
            }

            // Actualizar el personal en la base de datos
            val updatedResult = service.update(selectedPersonal!!.id, updatedPersonal)

            if (updatedResult != null) {
                // Actualizar la lista de personal
                val index = personalList.indexOfFirst { it.id == updatedResult.id }
                if (index >= 0) {
                    personalList[index] = updatedResult
                    playersTableView.refresh()
                    updateStatistics()
                }

                showInfoDialog("Datos guardados", "Los datos se han guardado correctamente.")
            } else {
                showErrorDialog("Error", "No se pudo actualizar el personal en la base de datos.")
            }

            clearDetailsPanel()
        } catch (e: Exception) {
            logger.error { "Error al guardar datos: ${e.message}" }
            showErrorDialog("Error", "No se pudieron guardar los datos: ${e.message}")
        }
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
        passwordColumn.cellValueFactory = PropertyValueFactory("password")
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

        // Cargar todos los usuarios desde la base de datos
        val allUsers = userRepository.findAll()
        usersList.addAll(allUsers)

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

                // Confirmar eliminación
                val alert = Alert(Alert.AlertType.CONFIRMATION)
                alert.title = "Confirmar eliminación"
                alert.headerText = "¿Está seguro de que desea eliminar este usuario?"
                alert.contentText = "Esta acción no se puede deshacer."

                val result = alert.showAndWait()
                if (result.isPresent && result.get() == ButtonType.OK) {
                    try {
                        val success = userRepository.delete(selected.id)
                        if (success) {
                            showInfoDialog("Operación exitosa", "Usuario eliminado correctamente.")
                        } else {
                            showErrorDialog("Error", "No se pudo eliminar el usuario.")
                        }

                        // Recargar usuarios
                        loadUsers()
                        clearUserForm()
                    } catch (e: Exception) {
                        showErrorDialog("Error", "No se pudo eliminar el usuario: ${e.message}")
                    }
                }
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
        passwordTextField.text = user.password // Mostramos la contraseña hasheada
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
                // Actualizar usuario existente
                // Si la contraseña está vacía, mantener la contraseña actual
                val updatedPassword = if (password.isEmpty()) selectedUser!!.password else password

                val updatedUser = User(
                    id = selectedUser!!.id,
                    username = username,
                    password = updatedPassword,
                    role = role,
                    createdAt = selectedUser!!.createdAt,
                    updatedAt = LocalDateTime.now()
                )

                val result = userRepository.update(selectedUser!!.id, updatedUser)
                if (result != null) {
                    showInfoDialog("Operación exitosa", "Usuario actualizado correctamente.")
                } else {
                    showErrorDialog("Error", "No se pudo actualizar el usuario.")
                }
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

    private fun setupMenuItems() {
        // Cargar datos
        loadDataMenuItem.setOnAction {
            try {
                // Crear una instancia del controlador
                val controller = srangeldev.controller.Controller()

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

        // Exportar datos
        exportDataMenuItem.setOnAction {
            try {
                // Crear el directorio de backup si no existe
                val backupDir = File(Config.configProperties.backupDir)
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }

                // Generar nombre de archivo con timestamp
                val timestamp = LocalDateTime.now().toString().replace(":", "-").replace(".", "-")
                val outputPath = "${Config.configProperties.backupDir}/personal_${timestamp}.json"

                // Crear una instancia del servicio
                val service = srangeldev.service.PersonalServiceImpl()

                // Exportar datos a JSON
                service.exportToFile(outputPath, srangeldev.storage.FileFormat.JSON)

                showInfoDialog("Exportar datos", "Datos exportados correctamente a JSON.\n\nRuta: $outputPath")
            } catch (e: Exception) {
                logger.error { "Error al exportar datos: ${e.message}" }
                showErrorDialog("Error", "No se pudieron exportar los datos: ${e.message}")
            }
        }

        // Importar datos
        importDataMenuItem.setOnAction {
            try {
                // Crear un FileChooser para seleccionar el archivo a importar
                val fileChooser = javafx.stage.FileChooser()
                fileChooser.title = "Seleccionar archivo para importar"

                // Configurar filtros para los tipos de archivo soportados
                fileChooser.extensionFilters.addAll(
                    javafx.stage.FileChooser.ExtensionFilter("Archivos CSV", "*.csv"),
                    javafx.stage.FileChooser.ExtensionFilter("Archivos JSON", "*.json"),
                    javafx.stage.FileChooser.ExtensionFilter("Archivos XML", "*.xml")
                )

                // Mostrar el diálogo de selección de archivo
                val selectedFile = fileChooser.showOpenDialog(playerImageView.scene.window as Stage)

                if (selectedFile != null) {
                    // Determinar el formato del archivo según su extensión
                    val fileFormat = when {
                        selectedFile.name.endsWith(".csv", ignoreCase = true) -> srangeldev.storage.FileFormat.CSV
                        selectedFile.name.endsWith(".json", ignoreCase = true) -> srangeldev.storage.FileFormat.JSON
                        selectedFile.name.endsWith(".xml", ignoreCase = true) -> srangeldev.storage.FileFormat.XML
                        else -> throw IllegalArgumentException("Formato de archivo no soportado")
                    }

                    // Crear una instancia del servicio
                    val service = srangeldev.service.PersonalServiceImpl()

                    // Importar datos desde el archivo seleccionado
                    service.importFromFile(selectedFile.absolutePath, fileFormat)

                    // Actualizar la lista de personal con los datos importados
                    loadPersonalFromDatabase()

                    showInfoDialog("Importar datos", "Datos importados correctamente desde ${selectedFile.name}.")
                }
            } catch (e: Exception) {
                logger.error { "Error al importar datos: ${e.message}" }
                showErrorDialog("Error", "No se pudieron importar los datos: ${e.message}")
            }
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
            RoutesManager.onLogout(
                "Cerrar sesión",
                "¿Estás seguro de que quieres cerrar sesión?",
                "Si cierras sesión, volverás a la pantalla de inicio de sesión."
            )
        }

        // Acerca de
        aboutMenuItem.setOnAction {
            showAboutDialog()
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
