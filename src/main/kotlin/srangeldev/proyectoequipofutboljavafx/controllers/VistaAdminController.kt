package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.*
import javafx.scene.control.ButtonBar
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.geometry.Insets
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.controller.Controller
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.NewTeamApplication
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager
import srangeldev.proyectoequipofutboljavafx.newteam.repository.ConvocatoriaRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRepositoryImpl
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepositoryImpl
import srangeldev.proyectoequipofutboljavafx.newteam.session.Session
import srangeldev.proyectoequipofutboljavafx.newteam.service.PersonalServiceImpl
import srangeldev.proyectoequipofutboljavafx.newteam.storage.FileFormat
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorageCsv
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorageJson
import srangeldev.proyectoequipofutboljavafx.newteam.utils.ZipFile
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorageXml
import srangeldev.proyectoequipofutboljavafx.newteam.utils.HtmlReportGenerator
import srangeldev.proyectoequipofutboljavafx.newteam.utils.PdfReportGenerator
import java.awt.Desktop
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period


/**
 * Controlador para la vista de administración
 */
class VistaAdminController : KoinComponent {
    private val logger = logging()
    private val userRepository: UserRepository = UserRepositoryImpl()

    // Elemento oculto para tamaños de diálogos
    @FXML
    private lateinit var dialogTableSizes: TableView<*>

    // Inyectar los repositorios usando Koin para la funcionalidad de convocatorias
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

    // Jugadores Tab
    @FXML
    private lateinit var searchField: TextField
    @FXML
    private lateinit var allToggleButton: ToggleButton
    @FXML
    private lateinit var playerToggleButton: ToggleButton
    @FXML
    private lateinit var coachToggleButton: ToggleButton
    @FXML
    private lateinit var playersTableView: TableView<Personal>
    @FXML
    private lateinit var idColumn: TableColumn<Personal, Int>
    @FXML
    private lateinit var nombreColumn: TableColumn<Personal, String>
    @FXML
    private lateinit var apellidosColumn: TableColumn<Personal, String>
    // Removed avgMinutosLabel as per issue requirements
    @FXML
    private lateinit var avgGolesLabel: Label
    @FXML
    private lateinit var playerImageView: ImageView

    @FXML
    private lateinit var selectImageButton: Button

    // Variable para almacenar la URL de la imagen seleccionada
    private var selectedImageUrl: String = ""
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
    private lateinit var saveButton: Button
    @FXML
    private lateinit var cancelButton: Button
    @FXML
    private lateinit var addPlayerButton: Button
    @FXML
    private lateinit var deletePlayerButton: Button
    @FXML
    private lateinit var deleteAllPlayersButton: Button

    // Menu Items
    @FXML
    private lateinit var loadDataMenuItem: MenuItem
    @FXML
    private lateinit var exportDataMenuItem: MenuItem
    @FXML
    private lateinit var importDataMenuItem: MenuItem
    @FXML
    private lateinit var printHtmlMenuItem: MenuItem
    @FXML
    private lateinit var printPdfMenuItem: MenuItem
    @FXML
    private lateinit var closeMenuItem: MenuItem
    @FXML
    private lateinit var aboutMenuItem: MenuItem

    // Usuarios Tab
    @FXML
    private lateinit var searchUserField: TextField
    @FXML
    private lateinit var usersTableView: TableView<User>
    @FXML
    private lateinit var userIdColumn: TableColumn<User, Int>
    @FXML
    private lateinit var usernameColumn: TableColumn<User, String>
    @FXML
    private lateinit var passwordColumn: TableColumn<User, String>
    @FXML
    private lateinit var roleColumn: TableColumn<User, String>
    @FXML
    private lateinit var usernameTextField: TextField
    @FXML
    private lateinit var passwordTextField: TextField
    @FXML
    private lateinit var roleComboBox: ComboBox<String>
    @FXML
    private lateinit var saveUserButton: Button
    @FXML
    private lateinit var cancelUserButton: Button
    @FXML
    private lateinit var addUserButton: Button
    @FXML
    private lateinit var deleteUserButton: Button

    // Configuración Tab
    @FXML
    private lateinit var dataDirectoryField: TextField
    @FXML
    private lateinit var backupDirectoryField: TextField
    @FXML
    private lateinit var databaseUrlField: TextField
    @FXML
    private lateinit var initTablesYesRadio: RadioButton
    @FXML
    private lateinit var initTablesNoRadio: RadioButton
    @FXML
    private lateinit var initDataYesRadio: RadioButton
    @FXML
    private lateinit var initDataNoRadio: RadioButton
    @FXML
    private lateinit var browseDataDirButton: Button
    @FXML
    private lateinit var browseBackupDirButton: Button
    @FXML
    private lateinit var saveConfigButton: Button
    @FXML
    private lateinit var resetConfigButton: Button

    // Convocatorias Tab - Lista de convocatorias
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

    // Convocatorias Tab - Detalles de la convocatoria
    @FXML
    private lateinit var fechaConvocatoriaPicker: DatePicker
    @FXML
    private lateinit var selectEntrenadoresButton: Button
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

    // Convocatorias Tab - Jugadores convocados
    @FXML
    private lateinit var jugadoresCountLabel: Label
    @FXML
    private lateinit var titularesCountLabel: Label
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

    // Convocatorias Tab - Botones
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

    // Convocatorias Tab - SplitPane
    @FXML
    private lateinit var convocatoriasSplitPane: SplitPane

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

        // Inicializar componentes de la pestaña de convocatorias
        initializeConvocatoriasTab()

        // Configurar eventos del menú
        setupMenuItems()

        // Cargar datos desde la base de datos
        loadPersonalFromDatabase()
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
        // Configurar los ToggleButtons para filtrar entre todos, jugadores y entrenadores
        allToggleButton.setOnAction {
            applyFilters(searchField.text)
        }

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
        especialidadComboBox.items.addAll(
            "ENTRENADOR_PRINCIPAL",
            "ENTRENADOR_ASISTENTE",
            "ENTRENADOR_PORTEROS"
        )

        // Configurar ComboBox de posición para jugadores
        posicionComboBox.items.addAll(
            "PORTERO",
            "DEFENSA",
            "CENTROCAMPISTA",
            "DELANTERO"
        )

        // Configurar DatePicker
        fechaIncorporacionPicker.value = LocalDate.now()

        // Configurar botones
        saveButton.setOnAction {
            savePersonalData()
        }

        cancelButton.setOnAction {
            clearDetailsPanel()
        }

        // Configurar botón para seleccionar imagen
        selectImageButton.setOnAction {
            selectPlayerImage()
        }
    }

    private fun selectPlayerImage() {
        try {
            val fileChooser = javafx.stage.FileChooser()
            fileChooser.title = "Seleccionar Imagen"
            fileChooser.extensionFilters.addAll(
                javafx.stage.FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
            )

            val selectedFile = fileChooser.showOpenDialog(playerImageView.scene.window)
            if (selectedFile != null) {
                // Guardar la URL de la imagen seleccionada
                selectedImageUrl = selectedFile.toURI().toString()

                // Mostrar la imagen seleccionada
                playerImageView.image = Image(selectedImageUrl)

                logger.debug { "Imagen seleccionada: $selectedImageUrl" }
            }
        } catch (e: Exception) {
            logger.error { "Error al seleccionar la imagen: ${e.message}" }
            showErrorDialog("Error", "No se pudo seleccionar la imagen: ${e.message}")
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
            // Mostrar diálogo para elegir entre crear jugador o entrenador
            showCreateMemberDialog()
        }

        deletePlayerButton.setOnAction {
            val selected = playersTableView.selectionModel.selectedItem
            if (selected != null) {
                // Mostrar diálogo de confirmación
                val alert = Alert(Alert.AlertType.CONFIRMATION)
                alert.title = "Confirmar eliminación"
                alert.headerText = "¿Está seguro de que desea eliminar este jugador/entrenador?"
                alert.contentText = "Esta acción eliminará el jugador/entrenador de la base de datos. Esta acción no se puede deshacer."

                val result = alert.showAndWait()
                if (result.isPresent && result.get() == ButtonType.OK) {
                    try {
                        // Crear una instancia del servicio
                        val service = PersonalServiceImpl()

                        // Eliminar el jugador/entrenador de la base de datos
                        service.delete(selected.id)

                        // Eliminar de la lista de UI
                        personalList.remove(selected)
                        clearDetailsPanel()
                        updateStatistics()

                        showInfoDialog("Operación exitosa", "Jugador/entrenador eliminado correctamente.")
                    } catch (e: Exception) {
                        logger.error { "Error al eliminar jugador/entrenador con ID ${selected.id}: ${e.message}" }
                        showErrorDialog("Error", "No se pudo eliminar el jugador/entrenador: ${e.message}")
                    }
                }
            } else {
                showInfoDialog("Selección requerida", "Por favor, seleccione un jugador o entrenador para eliminar.")
            }
        }

        deleteAllPlayersButton.setOnAction {
            // Verificar si hay jugadores para eliminar
            if (personalList.isEmpty()) {
                showInfoDialog("Sin jugadores", "No hay jugadores para eliminar.")
                return@setOnAction
            }

            // Mostrar diálogo de confirmación
            val alert = Alert(Alert.AlertType.CONFIRMATION)
            alert.title = "Confirmar eliminación"
            alert.headerText = "¿Está seguro de que desea eliminar TODOS los jugadores?"
            alert.contentText = "Esta acción eliminará todos los jugadores y entrenadores. Esta acción no se puede deshacer."

            val result = alert.showAndWait()
            if (result.isPresent && result.get() == ButtonType.OK) {
                try {
                    // Crear una instancia del servicio
                    val service = PersonalServiceImpl()

                    // Obtener una copia de la lista de personal para iterar
                    val personalToDelete = ArrayList(personalList)
                    var success = true

                    // Eliminar cada jugador individualmente
                    for (personal in personalToDelete) {
                        try {
                            service.delete(personal.id)
                        } catch (e: Exception) {
                            logger.error { "Error al eliminar jugador con ID ${personal.id}: ${e.message}" }
                            success = false
                        }
                    }

                    if (success) {
                        // Limpiar la lista de personal
                        personalList.clear()
                        clearDetailsPanel()
                        updateStatistics()
                        showInfoDialog("Operación exitosa", "Todos los jugadores y entrenadores han sido eliminados correctamente.")
                    } else {
                        // Recargar la lista para asegurarnos de que refleja el estado actual
                        loadPersonalFromDatabase()
                        showErrorDialog("Error parcial", "Algunos jugadores no pudieron ser eliminados. La lista ha sido actualizada.")
                    }
                } catch (e: Exception) {
                    logger.error { "Error al eliminar todos los jugadores: ${e.message}" }
                    showErrorDialog("Error", "No se pudieron eliminar todos los jugadores: ${e.message}")
                }
            }
        }
    }

    private fun showPersonalDetails(personal: Personal) {
        // Guardar el personal seleccionado
        selectedPersonal = personal

        // Mostrar datos comunes
        nombreTextField.text = "${personal.nombre} ${personal.apellidos}"
        edadSpinner.valueFactory.value = Period.between(personal.fechaNacimiento, LocalDate.now()).years
        salarioTextField.text = personal.salario.toString()
        fechaIncorporacionPicker.value = personal.fechaIncorporacion

        // Guardar la URL de la imagen actual
        selectedImageUrl = personal.imagenUrl

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
        // Mostrar campos específicos según el tipo
        when (personal) {
            is Jugador -> {
                posicionLabel.isVisible = true
                posicionComboBox.isVisible = true
                logger.debug { "Posición del jugador: ${personal.posicion}" }
                logger.debug { "Posición del jugador (name): ${personal.posicion.name}" }
                posicionComboBox.value = personal.posicion.name

                dorsalLabel.isVisible = true
                dorsalTextField.isVisible = true
                dorsalTextField.text = personal.dorsal.toString()

                partidosLabel.isVisible = true
                partidosTextField.isVisible = true
                partidosTextField.text = personal.partidosJugados.toString()

                golesLabel.isVisible = true
                golesTextField.isVisible = true
                golesTextField.text = personal.goles.toString()

            }

            is Entrenador -> {
                especialidadLabel.isVisible = true
                especialidadComboBox.isVisible = true
                logger.debug { "Especialización del entrenador: ${personal.especializacion}" }
                logger.debug { "Especialización del entrenador (name): ${personal.especializacion.name}" }
                especialidadComboBox.value = personal.especializacion.name
            }
        }

        // Cargar imagen
        loadPersonalImage(personal)
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

            // Si no hay URL o hubo error, cargar la imagen por defecto (logo del equipo)
            loadDefaultImage()
        } catch (e: Exception) {
            logger.error { "Error al cargar la imagen: ${e.message}" }
            loadDefaultImage()
        }
    }

    private fun loadDefaultImage() {
        try {
            val imageStream = NewTeamApplication::class.java.getResourceAsStream("icons/newTeamLogo.png")
            if (imageStream != null) {
                playerImageView.image = Image(imageStream)
            } else {
                logger.error { "No se pudo encontrar la imagen por defecto: icons/newTeamLogo.png" }
                // Intentar cargar desde una URL absoluta como último recurso
                try {
                    val imageUrl = NewTeamApplication::class.java.getResource("icons/newTeamLogo.png")
                    if (imageUrl != null) {
                        playerImageView.image = Image(imageUrl.toString())
                    }
                } catch (e: Exception) {
                    logger.error { "Error al cargar la imagen por defecto desde URL: ${e.message}" }
                }
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
            val service = PersonalServiceImpl()

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
                    val goles = golesTextField.text.toIntOrNull() ?: 0
                    val partidosJugados = partidosTextField.text.toIntOrNull() ?: 0

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
                        partidosJugados = partidosJugados,
                        imagenUrl = selectedImageUrl // Usar la URL de la imagen seleccionada
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
                        especializacion = especializacion,
                        imagenUrl = selectedImageUrl // Usar la URL de la imagen seleccionada
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

        loadDefaultImage()
        playersTableView.selectionModel.clearSelection()
        selectedPersonal = null
        setFieldsEditable(false)
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
        val jugadores = filteredPersonalList.filterIsInstance<Jugador>()

        if (jugadores.isNotEmpty()) {
            val avgGoles = jugadores.map { it.goles }.average()

            avgGolesLabel.text = String.format("%.1f", avgGoles)
        } else {
            avgGolesLabel.text = "0"
        }
    }

    private fun initializeUsersTab() {
        // Configurar tabla de usuarios
        userIdColumn.cellValueFactory = PropertyValueFactory("id")
        usernameColumn.cellValueFactory = PropertyValueFactory("username")
        // Mostrar asteriscos en lugar de la contraseña hasheada por seguridad
        passwordColumn.cellValueFactory = javafx.util.Callback { _ -> 
            SimpleStringProperty("********") 
        }
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
        passwordTextField.text = user.password
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
                // Si la contraseña está vacía o contiene asteriscos (contraseña oculta), mantener la contraseña actual
                val updatedPassword = if (password.isEmpty() || password == "********") {
                    selectedUser!!.password
                } else {
                    password
                }

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

    /**
     * Inicializa la pestaña de convocatorias.
     */
    private fun initializeConvocatoriasTab() {
        logger.debug { "Inicializando pestaña de convocatorias" }

        // Inicializar la tabla de convocatorias
        initializeConvocatoriasTable()

        // Inicializar la tabla de jugadores convocados
        initializeJugadoresConvocadosTable()

        // Inicializar la tabla de entrenadores
        initializeEntrenadoresTable()

        // Configurar los eventos de los botones
        setupConvocatoriaButtonEvents()

        // Configurar los eventos de la tabla de convocatorias
        setupConvocatoriaTableViewEvents()

        // Cargar las convocatorias
        loadConvocatorias()

        // Configurar el campo de búsqueda
        setupConvocatoriaSearchField()

        // Inicialmente, deshabilitar los botones de edición y eliminación
        editConvocatoriaButton.isDisable = true
        deleteConvocatoriaButton.isDisable = true
        printConvocatoriaButton.isDisable = true

        // Inicialmente, ocultar el panel de detalles
        clearConvocatoriaDetailsPanel()

        // Fijar la posición del divisor del SplitPane en 0.68
        convocatoriasSplitPane.setDividerPosition(0, 0.68)

        // Añadir un listener para mantener la posición del divisor en 0.68
        convocatoriasSplitPane.dividers[0].positionProperty().addListener { _, _, newValue ->
            if (newValue.toDouble() != 0.68) {
                convocatoriasSplitPane.setDividerPosition(0, 0.68)
            }
        }
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
                Bindings.createStringBinding(
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
     * Configura los eventos de los botones de la pestaña de convocatorias.
     */
    private fun setupConvocatoriaButtonEvents() {
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
            clearConvocatoriaDetailsPanel()
        }
    }

    /**
     * Configura los eventos de la tabla de convocatorias.
     */
    private fun setupConvocatoriaTableViewEvents() {
        // Configurar el evento de selección de la tabla
        convocatoriasTableView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                logger.debug { "Convocatoria seleccionada: ${newValue.id}" }
                showConvocatoriaDetails(newValue)
                editConvocatoriaButton.isDisable = false
                deleteConvocatoriaButton.isDisable = false
                printConvocatoriaButton.isDisable = false
            } else {
                clearConvocatoriaDetailsPanel()
                editConvocatoriaButton.isDisable = true
                deleteConvocatoriaButton.isDisable = true
                printConvocatoriaButton.isDisable = true
            }
        }
    }

    /**
     * Configura el campo de búsqueda de convocatorias.
     */
    private fun setupConvocatoriaSearchField() {
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
     * Limpia el panel de detalles de la convocatoria.
     */
    private fun clearConvocatoriaDetailsPanel() {
        currentConvocatoria = null
        fechaConvocatoriaPicker.value = null
        entrenadoresSeleccionados.clear()
        descripcionTextArea.text = ""
        jugadoresConvocados.clear()
        updateConvocatoriaCountLabels()
        setConvocatoriaFieldsEditable(false)
    }

    /**
     * Actualiza las etiquetas de conteo de jugadores y titulares.
     */
    private fun updateConvocatoriaCountLabels() {
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
     * Habilita o deshabilita la edición de los campos de la convocatoria.
     */
    private fun setConvocatoriaFieldsEditable(editable: Boolean) {
        fechaConvocatoriaPicker.isEditable = editable
        fechaConvocatoriaPicker.isDisable = !editable
        selectEntrenadoresButton.isDisable = !editable
        descripcionTextArea.isEditable = editable
        selectJugadoresButton.isDisable = !editable
        selectTitularesButton.isDisable = !editable
        saveConvocatoriaButton.isDisable = !editable
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
        }

        // Cargar los jugadores convocados
        loadJugadoresConvocados(convocatoria)

        // Actualizar las etiquetas de conteo
        updateConvocatoriaCountLabels()

        // Deshabilitar la edición
        setConvocatoriaFieldsEditable(false)
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
     * Crea una nueva convocatoria.
     */
    private fun createNewConvocatoria() {
        // Limpiar el panel de detalles
        clearConvocatoriaDetailsPanel()

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


        // Habilitar la edición
        setConvocatoriaFieldsEditable(true)

        // Limpiar la tabla de jugadores
        jugadoresConvocados.clear()

        // Actualizar las etiquetas de conteo
        updateConvocatoriaCountLabels()
    }

    /**
     * Edita una convocatoria existente.
     */
    private fun editConvocatoria() {
        // Habilitar la edición
        setConvocatoriaFieldsEditable(true)
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
                clearConvocatoriaDetailsPanel()

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
     * Selecciona los entrenadores para la convocatoria.
     */
    private fun selectEntrenadores() {
        logger.debug { "Seleccionando entrenadores para la convocatoria" }

        val convocatoria = currentConvocatoria ?: return

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
            // Refrescar la tabla
            entrenadoresTableView.refresh()
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
        tableView.prefWidth = dialogTableSizes.prefWidth
        tableView.prefHeight = dialogTableSizes.prefHeight

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
            updateConvocatoriaCountLabels()

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
        tableView.prefWidth = dialogTableSizes.prefWidth
        tableView.prefHeight = dialogTableSizes.prefHeight

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
            updateConvocatoriaCountLabels()

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
            setConvocatoriaFieldsEditable(false)

            // Mostrar mensaje de éxito
            showInfoDialog("Éxito", "Convocatoria guardada correctamente")
        } catch (e: Exception) {
            logger.error { "Error al guardar la convocatoria: ${e.message}" }
            showErrorDialog("Error", "Error al guardar la convocatoria: ${e.message}")
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
            showInfoDialog(
                "Operación no soportada",
                "La actualización de la configuración no está implementada en esta versión.\n\n" +
                        "Para cambiar la configuración, edite manualmente el archivo config.properties."
            )
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

    private fun setupMenuItems() {
        // Cargar datos
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

        // Exportar datos
        exportDataMenuItem.setOnAction {
            try {
                // Crear un FileChooser para seleccionar el tipo de exportación
                val fileChooser = javafx.stage.FileChooser()
                fileChooser.title = "Guardar archivo"

                // Configurar filtros para los tipos de archivo soportados
                fileChooser.extensionFilters.addAll(
                    javafx.stage.FileChooser.ExtensionFilter("Archivos JSON", "*.json"),
                    javafx.stage.FileChooser.ExtensionFilter("Archivos ZIP", "*.zip")
                )

                // Generar nombre de archivo con timestamp
                val timestamp = LocalDateTime.now().toString().replace(":", "-").replace(".", "-")
                fileChooser.initialFileName = "personal_${timestamp}"

                // Mostrar el diálogo de selección de archivo
                val selectedFile = fileChooser.showSaveDialog(playerImageView.scene.window as Stage)

                if (selectedFile != null) {
                    // Crear una instancia del servicio
                    val service = PersonalServiceImpl()

                    // Determinar si es un archivo ZIP o JSON
                    if (selectedFile.name.endsWith(".zip", ignoreCase = true)) {
                        // Crear un directorio temporal para los archivos
                        val tempDir = File("${Config.configProperties.backupDir}/temp_${timestamp}")
                        if (!tempDir.exists()) {
                            tempDir.mkdirs()
                        }

                        // Exportar datos a JSON en el directorio temporal
                        val tempJsonPath = "${tempDir.absolutePath}/personal.json"
                        service.exportToFile(tempJsonPath, FileFormat.JSON)

                        // Crear el archivo ZIP
                        ZipFile.createZipFile(
                            tempDir.absolutePath,
                            selectedFile.absolutePath
                        )

                        // Eliminar el directorio temporal
                        tempDir.deleteRecursively()

                        showInfoDialog(
                            "Exportar datos",
                            "Datos exportados correctamente a ZIP.\n\nRuta: ${selectedFile.absolutePath}"
                        )
                    } else {
                        // Exportar datos a JSON
                        service.exportToFile(selectedFile.absolutePath, FileFormat.JSON)
                        showInfoDialog(
                            "Exportar datos",
                            "Datos exportados correctamente a JSON.\n\nRuta: ${selectedFile.absolutePath}"
                        )
                    }
                }
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
                // Solo permitimos archivos ZIP para importar
                fileChooser.extensionFilters.addAll(
                    javafx.stage.FileChooser.ExtensionFilter("Archivos ZIP", "*.zip")
                )

                // Mostrar el diálogo de selección de archivo
                val selectedFile = fileChooser.showOpenDialog(playerImageView.scene.window as Stage)

                if (selectedFile != null) {
                    // Crear una instancia del servicio
                    val service = PersonalServiceImpl()

                    // Verificar si es un archivo ZIP
                    if (selectedFile.name.endsWith(".zip", ignoreCase = true)) {
                        // Crear un directorio temporal para extraer los archivos
                        val timestamp = LocalDateTime.now().toString().replace(":", "-").replace(".", "-")
                        val tempDir = File("${Config.configProperties.dataDir}/temp_${timestamp}")
                        if (!tempDir.exists()) {
                            tempDir.mkdirs()
                        }

                        // Extraer el archivo ZIP
                        ZipFile.extractFileToPath(
                            selectedFile.absolutePath,
                            tempDir.absolutePath
                        )

                        // Buscar archivos de datos en el directorio extraído
                        var importedAny = false
                        val validFiles = mutableListOf<File>()

                        // Primero verificamos que el ZIP contiene archivos con estructura válida
                        tempDir.walkTopDown().forEach { file ->
                            if (file.isFile) {
                                val fileFormat = when {
                                    file.name.endsWith(".csv", ignoreCase = true) -> FileFormat.CSV
                                    file.name.endsWith(".json", ignoreCase = true) -> FileFormat.JSON
                                    file.name.endsWith(".xml", ignoreCase = true) -> FileFormat.XML
                                    else -> null
                                }

                                if (fileFormat != null) {
                                    try {
                                        // Intentamos leer el archivo para verificar su estructura
                                        val storage = when (fileFormat) {
                                            FileFormat.CSV -> PersonalStorageCsv()
                                            FileFormat.JSON -> PersonalStorageJson()
                                            FileFormat.XML -> PersonalStorageXml()
                                            else -> null
                                        }

                                        if (storage != null) {
                                            // Si no lanza excepción, el archivo tiene estructura válida
                                            storage.readFromFile(file)
                                            validFiles.add(file)
                                        }
                                    } catch (e: Exception) {
                                        logger.error { "Archivo con estructura inválida ${file.name}: ${e.message}" }
                                        // No agregamos el archivo a la lista de válidos
                                    }
                                }
                            }
                        }

                        // Ahora importamos solo los archivos con estructura válida
                        validFiles.forEach { file ->
                            try {
                                val fileFormat = when {
                                    file.name.endsWith(".csv", ignoreCase = true) -> FileFormat.CSV
                                    file.name.endsWith(".json", ignoreCase = true) -> FileFormat.JSON
                                    file.name.endsWith(".xml", ignoreCase = true) -> FileFormat.XML
                                    else -> null
                                }

                                if (fileFormat != null) {
                                    // Importar datos desde el archivo
                                    service.importFromFile(file.absolutePath, fileFormat)
                                    importedAny = true
                                }
                            } catch (e: Exception) {
                                logger.error { "Error al importar archivo ${file.name}: ${e.message}" }
                                // Continuar con el siguiente archivo
                            }
                        }

                        // Eliminar el directorio temporal
                        tempDir.deleteRecursively()

                        if (importedAny) {
                            // Actualizar la lista de personal con los datos importados
                            loadPersonalFromDatabase()
                            showInfoDialog(
                                "Importar datos",
                                "Datos importados correctamente desde el archivo ZIP ${selectedFile.name}."
                            )
                        } else {
                            showErrorDialog(
                                "Error",
                                "No se encontraron archivos con estructura válida en el archivo ZIP. Asegúrese de que el ZIP contiene archivos XML, JSON o CSV con el formato correcto."
                            )
                        }
                    } else {
                        // Determinar el formato del archivo según su extensión
                        val fileFormat = when {
                            selectedFile.name.endsWith(".csv", ignoreCase = true) -> FileFormat.CSV
                            selectedFile.name.endsWith(".json", ignoreCase = true) -> FileFormat.JSON
                            selectedFile.name.endsWith(".xml", ignoreCase = true) -> FileFormat.XML
                            else -> throw IllegalArgumentException("Formato de archivo no soportado")
                        }

                        // Importar datos desde el archivo seleccionado
                        service.importFromFile(selectedFile.absolutePath, fileFormat)

                        // Actualizar la lista de personal con los datos importados
                        loadPersonalFromDatabase()

                        showInfoDialog("Importar datos", "Datos importados correctamente desde ${selectedFile.name}.")
                    }
                }
            } catch (e: Exception) {
                logger.error { "Error al importar datos: ${e.message}" }
                showErrorDialog("Error", "No se pudieron importar los datos: ${e.message}")
            }
        }

        // Imprimir HTML
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

        // Imprimir PDF
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
        try {
            // Load the FXML file
            val loader = FXMLLoader(javaClass.getResource("/srangeldev/proyectoequipofutboljavafx/views/newTeam/about-dialog.fxml"))
            val root = loader.load<GridPane>()

            // Create a dialog
            val dialog = Dialog<ButtonType>()
            dialog.title = "Acerca De"
            dialog.headerText = "Gestor de Jugadores de Fútbol"

            // Set the content
            dialog.dialogPane.content = root

            // Add buttons
            dialog.dialogPane.buttonTypes.add(ButtonType.CLOSE)

            // Show the dialog
            dialog.showAndWait()
        } catch (e: Exception) {
            logger.error { "Error loading about dialog: ${e.message}" }
            showErrorDialog("Error", "No se pudo cargar el diálogo de acerca de: ${e.message}")
        }
    }

    /**
     * Muestra un diálogo para elegir entre crear un jugador o un entrenador
     */
    private fun showCreateMemberDialog() {
        // Crear un diálogo de tipo Alert con botones personalizados
        val dialog = Alert(Alert.AlertType.CONFIRMATION)
        dialog.title = "Crear Miembro"
        dialog.headerText = "¿Qué tipo de miembro desea crear?"
        dialog.contentText = "Seleccione el tipo de miembro que desea añadir al equipo."

        // Personalizar los botones
        val jugadorButton = ButtonType("Jugador")
        val entrenadorButton = ButtonType("Entrenador")
        val cancelarButton = ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE)

        dialog.buttonTypes.setAll(jugadorButton, entrenadorButton, cancelarButton)

        // Mostrar el diálogo y procesar la respuesta
        val result = dialog.showAndWait()

        when {
            result.isPresent && result.get() == jugadorButton -> {
                // Preparar el formulario para crear un jugador
                prepareFormForPlayer()
            }
            result.isPresent && result.get() == entrenadorButton -> {
                // Preparar el formulario para crear un entrenador
                prepareFormForCoach()
            }
            // Si se selecciona Cancelar o se cierra el diálogo, no hacer nada
        }
    }

    /**
     * Prepara el formulario para crear un jugador
     */
    private fun prepareFormForPlayer() {
        clearDetailsPanel()
        setFieldsEditable(true)
        selectedPersonal = null

        // Mostrar campos específicos para jugador
        posicionLabel.isVisible = true
        posicionComboBox.isVisible = true
        dorsalLabel.isVisible = true
        dorsalTextField.isVisible = true
        partidosLabel.isVisible = true
        partidosTextField.isVisible = true
        golesLabel.isVisible = true
        golesTextField.isVisible = true

        // Ocultar campos específicos para entrenador
        especialidadLabel.isVisible = false
        especialidadComboBox.isVisible = false
    }

    /**
     * Prepara el formulario para crear un entrenador
     */
    private fun prepareFormForCoach() {
        clearDetailsPanel()
        setFieldsEditable(true)
        selectedPersonal = null

        // Mostrar campos específicos para entrenador
        especialidadLabel.isVisible = true
        especialidadComboBox.isVisible = true

        // Ocultar campos específicos para jugador
        posicionLabel.isVisible = false
        posicionComboBox.isVisible = false
        dorsalLabel.isVisible = false
        dorsalTextField.isVisible = false
        partidosLabel.isVisible = false
        partidosTextField.isVisible = false
        golesLabel.isVisible = false
        golesTextField.isVisible = false
    }
}
