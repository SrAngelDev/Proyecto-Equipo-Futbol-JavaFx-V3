package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.*
import org.mockito.Mockito.`when`
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.nio.file.Path
import java.sql.*
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PersonalRepositoryImplTest {

    private lateinit var repository: PersonalRepositoryImpl
    private lateinit var originalInstance: DataBaseManager
    private lateinit var originalDbUrl: String
    private lateinit var testDbUrl: String
    private lateinit var connection: Connection

    // Mock objects
    private lateinit var mockConnection: Connection
    private lateinit var mockPreparedStatement: PreparedStatement
    private lateinit var mockResultSet: ResultSet
    private lateinit var mockGeneratedKeys: ResultSet
    private lateinit var mockDataBaseManager: DataBaseManager

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setUp() {
        // Initialize mocks
        mockConnection = mock()
        mockPreparedStatement = mock()
        mockResultSet = mock()
        mockGeneratedKeys = mock()
        mockDataBaseManager = mock()

        // Set up real database connection
        originalInstance = DataBaseManager.instance
        originalDbUrl = Config.configProperties.databaseUrl

        val testDbFile = tempDir.resolve("test_equipo.db").toFile().absolutePath
        testDbUrl = "jdbc:sqlite:$testDbFile"

        val urlField = Config.configProperties::class.java.getDeclaredField("databaseUrl")
        urlField.isAccessible = true
        urlField.set(Config.configProperties, testDbUrl)

        val constructor = DataBaseManager::class.java.getDeclaredConstructor()
        constructor.isAccessible = true
        val testInstance = constructor.newInstance()

        val instanceField = DataBaseManager::class.java.getDeclaredField("instance")
        instanceField.isAccessible = true
        instanceField.set(null, testInstance)

        connection = DriverManager.getConnection(testDbUrl)


        repository = PersonalRepositoryImpl()

        // Common mock setups
        `when`(mockDataBaseManager.connection).thenReturn(mockConnection)
        `when`(mockConnection.prepareStatement(any(), any<Int>())).thenReturn(mockPreparedStatement)
        `when`(mockConnection.prepareStatement(any())).thenReturn(mockPreparedStatement)
        `when`(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet)
        `when`(mockPreparedStatement.generatedKeys).thenReturn(mockGeneratedKeys)
    }

    @AfterEach
    fun tearDown() {
        // Close the connection to prevent file locks
        if (!connection.isClosed) {
            connection.close()
        }

        // Restore original database configuration
        val urlField = Config.configProperties::class.java.getDeclaredField("databaseUrl")
        urlField.isAccessible = true
        urlField.set(Config.configProperties, originalDbUrl)

        val instanceField = DataBaseManager::class.java.getDeclaredField("instance")
        instanceField.isAccessible = true
        instanceField.set(null, originalInstance)
    }

    @Test
    fun `getAll debería cargar todos los registros correctamente`() {
        // Preparar datos de prueba
        insertEntrenador("Entrenador1", "Apellido1")
        insertJugador("Jugador1", "ApellidoJ1")

        // Ejecutar
        val result = repository.getAll()

        // Verificar
        assertEquals(2, result.size)
        assertTrue(result.any { it.nombre == "Entrenador1" && it is Entrenador })
        assertTrue(result.any { it.nombre == "Jugador1" && it is Jugador })
    }

    @Test
    fun `getById debería devolver un entrenador existente`() {
        // Preparar
        val entrenadorId = insertEntrenador("Entrenador", "Apellido")

        // Ejecutar
        val result = repository.getById(entrenadorId)

        // Verificar
        assertNotNull(result)
        assertEquals("Entrenador", result?.nombre)
        assertTrue(result is Entrenador)
    }

    @Test
    fun `getById debería devolver un jugador existente`() {
        // Preparar
        val jugadorId = insertJugador("Jugador", "Apellido")

        // Ejecutar
        val result = repository.getById(jugadorId)

        // Verificar
        assertNotNull(result)
        assertEquals("Jugador", result?.nombre)
        assertTrue(result is Jugador)
    }

    @Test
    fun `getById debería devolver null cuando no existe el ID`() {
        // Ejecutar
        val result = repository.getById(-1)

        // Verificar
        assertNull(result)
    }

    @Test
    fun `save debería guardar un entrenador correctamente`() {
        // Preparar
        val entrenador = Entrenador(
            nombre = "NuevoEntrenador",
            apellidos = "Apellidos",
            fechaNacimiento = LocalDate.now().minusYears(40),
            fechaIncorporacion = LocalDate.now().minusMonths(6),
            salario = 5000.0,
            paisOrigen = "España",
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
            id = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Ejecutar
        val result = repository.save(entrenador) as Entrenador

        // Verificar
        assertTrue(result.id > 0)
        val recuperado = repository.getById(result.id) as Entrenador
        assertEquals(entrenador.nombre, recuperado.nombre)
        assertEquals(entrenador.especializacion, recuperado.especializacion)
    }

    @Test
    fun `save debería guardar un jugador correctamente`() {
        // Preparar
        val jugador = Jugador(
            nombre = "NuevoJugador",
            apellidos = "Apellidos",
            fechaNacimiento = LocalDate.now().minusYears(25),
            fechaIncorporacion = LocalDate.now().minusMonths(3),
            salario = 10000.0,
            paisOrigen = "Brasil",
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 10,
            altura = 1.80,
            peso = 75.0,
            goles = 5,
            partidosJugados = 15,
            id = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Ejecutar
        val result = repository.save(jugador) as Jugador

        // Verificar
        assertTrue(result.id > 0)
        val recuperado = repository.getById(result.id) as Jugador
        assertEquals(jugador.nombre, recuperado.nombre)
        assertEquals(jugador.dorsal, recuperado.dorsal)
    }

    @Test
    fun `update debería actualizar un entrenador existente`() {
        // Preparar
        val id = insertEntrenador("Original", "Apellido")
        val original = repository.getById(id) as Entrenador
        val actualizado = Entrenador(
            id = original.id,
            nombre = "Actualizado",
            apellidos = original.apellidos,
            fechaNacimiento = original.fechaNacimiento,
            fechaIncorporacion = original.fechaIncorporacion,
            salario = original.salario,
            paisOrigen = original.paisOrigen,
            createdAt = original.createdAt,
            updatedAt = original.updatedAt,
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
        )

        // Ejecutar
        val result = repository.update(id, actualizado) as Entrenador

        // Verificar
        assertEquals("Actualizado", result.nombre)
        val recuperado = repository.getById(id) as Entrenador
        assertEquals(actualizado.especializacion, recuperado.especializacion)
    }

    @Test
    fun `update debería actualizar un jugador existente`() {
        // Preparar
        val id = insertJugador("Original", "Apellido")
        val original = repository.getById(id) as Jugador
        val actualizado = Jugador(
            id = original.id,
            nombre = "JugadorActualizado",
            apellidos = original.apellidos,
            fechaNacimiento = original.fechaNacimiento,
            fechaIncorporacion = original.fechaIncorporacion,
            salario = original.salario,
            paisOrigen = original.paisOrigen,
            posicion = original.posicion,
            dorsal = 7,
            altura = original.altura,
            peso = original.peso,
            goles = 10,
            partidosJugados = original.partidosJugados,
            createdAt = original.createdAt,
            updatedAt = original.updatedAt
        )

        // Ejecutar
        val result = repository.update(id, actualizado) as Jugador

        // Verificar
        assertEquals("JugadorActualizado", result.nombre)
        val recuperado = repository.getById(id) as Jugador
        assertEquals(7, recuperado.dorsal)
        assertEquals(10, recuperado.goles)
    }

    @Test
    fun `update debería devolver null cuando no existe el ID`() {
        // Preparar
        val entrenador = Entrenador(
            id = -1,
            nombre = "NoExistente",
            apellidos = "Apellido",
            fechaNacimiento = LocalDate.now(),
            fechaIncorporacion = LocalDate.now(),
            salario = 1000.0,
            paisOrigen = "ES",
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Ejecutar
        val result = repository.update(-1, entrenador)

        // Verificar
        assertNull(result)
    }

    @Test
    fun `delete debería eliminar un entrenador existente`() {
        // Preparar
        val id = insertEntrenador("Eliminar", "Apellido")

        // Ejecutar
        val result = repository.delete(id) as Entrenador

        // Verificar
        assertEquals("Eliminar", result.nombre)
        assertNull(repository.getById(id))
    }

    @Test
    fun `delete debería eliminar un jugador existente`() {
        // Preparar
        val id = insertJugador("JugadorEliminar", "Apellido")

        // Ejecutar
        val result = repository.delete(id) as Jugador

        // Verificar
        assertEquals("JugadorEliminar", result.nombre)
        assertNull(repository.getById(id))
    }

    @Test
    fun `delete debería devolver null cuando no existe el ID`() {
        // Ejecutar
        val result = repository.delete(-1)

        // Verificar
        assertNull(result)
    }

    @Test
    fun `clearCache debería limpiar la caché interna`() {
        // Preparar
        insertEntrenador("Cache", "Apellido")
        repository.getAll() // Llena la caché

        // Ejecutar
        repository.clearCache()

        // Verificar
        val field = PersonalRepositoryImpl::class.java.getDeclaredField("personal")
        field.isAccessible = true
        val cache = field.get(repository) as MutableMap<*, *>
        assertTrue(cache.isEmpty())
    }

    @Test
    fun `getAllEntrenadores debería devolver solo entrenadores`() {
        // Preparar
        insertEntrenador("Entrenador1", "Apellido1")
        insertEntrenador("Entrenador2", "Apellido2")
        insertJugador("Jugador", "Apellido")

        // Ejecutar
        val result = repository.getAllEntrenadores()

        // Verificar
        assertEquals(2, result.size)
        assertTrue(result.all { it is Entrenador })
    }

    // Funciones auxiliares para insertar datos de prueba
    private fun insertEntrenador(nombre: String, apellidos: String): Int {
        val sql = """
        INSERT INTO Personal (nombre, apellidos, fecha_nacimiento, fecha_incorporacion, 
        salario, pais_origen, tipo, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, 'ENTRENADOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    """.trimIndent()

        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
            stmt.setString(1, nombre)
            stmt.setString(2, apellidos)
            stmt.setDate(3, Date.valueOf(LocalDate.now().minusYears(45)))
            stmt.setDate(4, Date.valueOf(LocalDate.now().minusMonths(3)))
            stmt.setDouble(5, 4000.0)
            stmt.setString(6, "España")
            stmt.executeUpdate()

            val keys = stmt.generatedKeys
            keys.next()
            val id = keys.getInt(1)

            // Insertar en Entrenadores
            connection.prepareStatement("INSERT INTO Entrenadores (id, especializacion) VALUES (?, ?)").use { stmtEnt ->
                stmtEnt.setInt(1, id)
                stmtEnt.setString(2, "ENTRENADOR_PRINCIPAL")
                stmtEnt.executeUpdate()
            }
            return id
        }
    }

    private fun insertJugador(nombre: String, apellidos: String): Int {
        val sql = """
        INSERT INTO Personal (nombre, apellidos, fecha_nacimiento, fecha_incorporacion, 
        salario, pais_origen, tipo, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, 'JUGADOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    """.trimIndent()

        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
            stmt.setString(1, nombre)
            stmt.setString(2, apellidos)
            stmt.setDate(3, Date.valueOf(LocalDate.now().minusYears(25)))
            stmt.setDate(4, Date.valueOf(LocalDate.now().minusMonths(2)))
            stmt.setDouble(5, 8000.0)
            stmt.setString(6, "Brasil")
            stmt.executeUpdate()

            val keys = stmt.generatedKeys
            keys.next()
            val id = keys.getInt(1)

            // Insertar en Jugadores
            connection.prepareStatement("""
            INSERT INTO Jugadores (id, posicion, dorsal, altura, peso, goles, partidos_jugados) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()).use { stmtJug ->
                stmtJug.setInt(1, id)
                stmtJug.setString(2, "DELANTERO")
                stmtJug.setInt(3, 9)
                stmtJug.setDouble(4, 1.80)
                stmtJug.setDouble(5, 75.0)
                stmtJug.setInt(6, 5)
                stmtJug.setInt(7, 15)
                stmtJug.executeUpdate()
            }
            return id
        }
    }
}
