package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.*
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import srangeldev.proyectoequipofutboljavafx.newteam.dao.EntrenadorDao
import srangeldev.proyectoequipofutboljavafx.newteam.dao.EntrenadorEntity
import srangeldev.proyectoequipofutboljavafx.newteam.dao.JugadorDao
import srangeldev.proyectoequipofutboljavafx.newteam.dao.JugadorEntity
import srangeldev.proyectoequipofutboljavafx.newteam.dao.PersonalDao
import srangeldev.proyectoequipofutboljavafx.newteam.dao.PersonalEntity
import srangeldev.proyectoequipofutboljavafx.newteam.database.JdbiManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.nio.file.Path
import java.sql.*
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.*

class PersonalRepositoryImplTest {

    private lateinit var repository: PersonalRepositoryImpl
    private lateinit var originalInstance: JdbiManager
    private lateinit var originalDbUrl: String
    private lateinit var testDbUrl: String
    private lateinit var connection: Connection

    @TempDir
    lateinit var tempDir: Path

    // Mock DAOs
    private lateinit var personalDao: PersonalDao
    private lateinit var entrenadorDao: EntrenadorDao
    private lateinit var jugadorDao: JugadorDao

    @BeforeEach
    fun setUp() {
        // Set up real database connection
        originalInstance = JdbiManager.getInstance()
        originalDbUrl = Config.configProperties.databaseUrl

        val testDbFile = tempDir.resolve("test_equipo.db").toFile().absolutePath
        testDbUrl = "jdbc:sqlite:$testDbFile"

        val urlField = Config.configProperties::class.java.getDeclaredField("databaseUrl")
        urlField.isAccessible = true
        urlField.set(Config.configProperties, testDbUrl)

        val constructor = JdbiManager::class.java.getDeclaredConstructor()
        constructor.isAccessible = true
        val testInstance = constructor.newInstance()

        val instanceField = JdbiManager::class.java.getDeclaredField("instance")
        instanceField.isAccessible = true
        instanceField.set(null, testInstance)

        connection = DriverManager.getConnection(testDbUrl)

        // Create tables for testing
        createTables()

        // Create mocked DAOs
        personalDao = mock()
        entrenadorDao = mock()
        jugadorDao = mock()

        // Initialize repository with mocked DAOs
        repository = PersonalRepositoryImpl(personalDao, entrenadorDao, jugadorDao)
    }

    private fun createTables() {
        // Create Personal table
        connection.createStatement().use { stmt ->
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Personal (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    apellidos TEXT NOT NULL,
                    fecha_nacimiento DATE NOT NULL,
                    fecha_incorporacion DATE NOT NULL,
                    salario REAL NOT NULL,
                    pais_origen TEXT NOT NULL,
                    tipo TEXT NOT NULL,
                    imagen_url TEXT,
                    created_at TIMESTAMP NOT NULL,
                    updated_at TIMESTAMP NOT NULL
                )
            """)
        }

        // Create Entrenadores table
        connection.createStatement().use { stmt ->
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Entrenadores (
                    id INTEGER PRIMARY KEY,
                    especializacion TEXT NOT NULL,
                    FOREIGN KEY (id) REFERENCES Personal(id) ON DELETE CASCADE
                )
            """)
        }

        // Create Jugadores table
        connection.createStatement().use { stmt ->
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Jugadores (
                    id INTEGER PRIMARY KEY,
                    posicion TEXT NOT NULL,
                    dorsal INTEGER NOT NULL,
                    altura REAL NOT NULL,
                    peso REAL NOT NULL,
                    goles INTEGER NOT NULL,
                    partidos_jugados INTEGER NOT NULL,
                    FOREIGN KEY (id) REFERENCES Personal(id) ON DELETE CASCADE
                )
            """)
        }
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

        val instanceField = JdbiManager::class.java.getDeclaredField("instance")
        instanceField.isAccessible = true
        instanceField.set(null, originalInstance)
    }


    @Test
    fun `getAll debería cargar todos los registros correctamente`() {
        // Preparar
        val entrenadorId = 1
        val jugadorId = 2

        // Create PersonalEntity objects
        val personalEntityEntrenador = PersonalEntity(
            id = entrenadorId,
            nombre = "Entrenador1",
            apellidos = "Apellido1",
            fechaNacimiento = LocalDate.now().minusYears(40),
            fechaIncorporacion = LocalDate.now().minusMonths(6),
            salario = 5000.0,
            paisOrigen = "España",
            tipo = "ENTRENADOR",
            imagenUrl = "",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val personalEntityJugador = PersonalEntity(
            id = jugadorId,
            nombre = "Jugador1",
            apellidos = "ApellidoJ1",
            fechaNacimiento = LocalDate.now().minusYears(25),
            fechaIncorporacion = LocalDate.now().minusMonths(3),
            salario = 10000.0,
            paisOrigen = "Brasil",
            tipo = "JUGADOR",
            imagenUrl = "",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Create expected objects
        val expectedEntrenador = Entrenador(
            id = entrenadorId,
            nombre = "Entrenador1",
            apellidos = "Apellido1",
            fechaNacimiento = personalEntityEntrenador.fechaNacimiento,
            fechaIncorporacion = personalEntityEntrenador.fechaIncorporacion,
            salario = personalEntityEntrenador.salario,
            paisOrigen = personalEntityEntrenador.paisOrigen,
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
            createdAt = personalEntityEntrenador.createdAt,
            updatedAt = personalEntityEntrenador.updatedAt,
            imagenUrl = personalEntityEntrenador.imagenUrl
        )

        val expectedJugador = Jugador(
            id = jugadorId,
            nombre = "Jugador1",
            apellidos = "ApellidoJ1",
            fechaNacimiento = personalEntityJugador.fechaNacimiento,
            fechaIncorporacion = personalEntityJugador.fechaIncorporacion,
            salario = personalEntityJugador.salario,
            paisOrigen = personalEntityJugador.paisOrigen,
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 9,
            altura = 1.80,
            peso = 75.0,
            goles = 5,
            partidosJugados = 15,
            createdAt = personalEntityJugador.createdAt,
            updatedAt = personalEntityJugador.updatedAt,
            imagenUrl = personalEntityJugador.imagenUrl
        )

        // Create entity objects
        val entrenadorEntity = EntrenadorEntity(
            id = entrenadorId,
            especializacion = "ENTRENADOR_PRINCIPAL"
        )

        val jugadorEntity = JugadorEntity(
            id = jugadorId,
            posicion = "DELANTERO",
            dorsal = 9,
            altura = 1.80,
            peso = 75.0,
            goles = 5,
            partidosJugados = 15
        )

        // Create spies
        val spyEntrenadorEntity = spy(entrenadorEntity)
        val spyJugadorEntity = spy(jugadorEntity)

        // Configure spies
        doReturn(expectedEntrenador).`when`(spyEntrenadorEntity).toEntrenador(personalEntityEntrenador)
        doReturn(expectedJugador).`when`(spyJugadorEntity).toJugador(personalEntityJugador)

        // Configure mocks
        whenever(personalDao.findByTipo("ENTRENADOR")).thenReturn(listOf(personalEntityEntrenador))
        whenever(personalDao.findByTipo("JUGADOR")).thenReturn(listOf(personalEntityJugador))
        whenever(entrenadorDao.findAll()).thenReturn(listOf(spyEntrenadorEntity))
        whenever(jugadorDao.findAll()).thenReturn(listOf(spyJugadorEntity))

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
        val entrenadorId = 1
        val personalEntity = PersonalEntity(
            id = entrenadorId,
            nombre = "Entrenador",
            apellidos = "Apellido",
            fechaNacimiento = LocalDate.now().minusYears(45),
            fechaIncorporacion = LocalDate.now().minusMonths(3),
            salario = 4000.0,
            paisOrigen = "España",
            tipo = "ENTRENADOR",
            imagenUrl = "",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Create expected result
        val expectedEntrenador = Entrenador(
            id = entrenadorId,
            nombre = "Entrenador",
            apellidos = "Apellido",
            fechaNacimiento = personalEntity.fechaNacimiento,
            fechaIncorporacion = personalEntity.fechaIncorporacion,
            salario = personalEntity.salario,
            paisOrigen = personalEntity.paisOrigen,
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
            createdAt = personalEntity.createdAt,
            updatedAt = personalEntity.updatedAt,
            imagenUrl = personalEntity.imagenUrl
        )

        // Configure mocks
        whenever(personalDao.findById(entrenadorId)).thenReturn(personalEntity)

        // Use doReturn..when syntax for the entrenadorDao.findById method
        val entrenadorEntity = EntrenadorEntity(
            id = entrenadorId,
            especializacion = "ENTRENADOR_PRINCIPAL"
        )

        // Create a spy of the entrenadorEntity
        val spyEntrenadorEntity = spy(entrenadorEntity)

        // Configure the spy to return the expected entrenador
        doReturn(expectedEntrenador).`when`(spyEntrenadorEntity).toEntrenador(personalEntity)

        // Configure the entrenadorDao to return the spy
        whenever(entrenadorDao.findById(entrenadorId)).thenReturn(spyEntrenadorEntity)

        // Ejecutar
        val result = repository.getById(entrenadorId)

        // Verificar
        assertNotNull(result)
        assertEquals("Entrenador", result.nombre)
        assertTrue(result is Entrenador)
    }

    @Test
    fun `getById debería devolver un jugador existente`() {
        // Preparar
        val jugadorId = 2
        val personalEntity = PersonalEntity(
            id = jugadorId,
            nombre = "Jugador",
            apellidos = "Apellido",
            fechaNacimiento = LocalDate.now().minusYears(25),
            fechaIncorporacion = LocalDate.now().minusMonths(2),
            salario = 8000.0,
            paisOrigen = "Brasil",
            tipo = "JUGADOR",
            imagenUrl = "",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Create expected result
        val expectedJugador = Jugador(
            id = jugadorId,
            nombre = "Jugador",
            apellidos = "Apellido",
            fechaNacimiento = personalEntity.fechaNacimiento,
            fechaIncorporacion = personalEntity.fechaIncorporacion,
            salario = personalEntity.salario,
            paisOrigen = personalEntity.paisOrigen,
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 9,
            altura = 1.80,
            peso = 75.0,
            goles = 5,
            partidosJugados = 15,
            createdAt = personalEntity.createdAt,
            updatedAt = personalEntity.updatedAt,
            imagenUrl = personalEntity.imagenUrl
        )

        // Configure mocks
        whenever(personalDao.findById(jugadorId)).thenReturn(personalEntity)

        // Use doReturn..when syntax for the jugadorDao.findById method
        val jugadorEntity = JugadorEntity(
            id = jugadorId,
            posicion = "DELANTERO",
            dorsal = 9,
            altura = 1.80,
            peso = 75.0,
            goles = 5,
            partidosJugados = 15
        )

        // Create a spy of the jugadorEntity
        val spyJugadorEntity = spy(jugadorEntity)

        // Configure the spy to return the expected jugador
        doReturn(expectedJugador).`when`(spyJugadorEntity).toJugador(personalEntity)

        // Configure the jugadorDao to return the spy
        whenever(jugadorDao.findById(jugadorId)).thenReturn(spyJugadorEntity)

        // Ejecutar
        val result = repository.getById(jugadorId)

        // Verificar
        assertNotNull(result)
        assertEquals("Jugador", result.nombre)
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
            id = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Mock para personalDao.save
        val generatedId = 1
        whenever(personalDao.save(any())).thenReturn(generatedId)

        // Mock para entrenadorDao.save
        whenever(entrenadorDao.save(any())).thenReturn(1)

        // Mock para getById
        whenever(personalDao.findById(generatedId)).thenReturn(
            PersonalEntity(
                id = generatedId,
                nombre = entrenador.nombre,
                apellidos = entrenador.apellidos,
                fechaNacimiento = entrenador.fechaNacimiento,
                fechaIncorporacion = entrenador.fechaIncorporacion,
                salario = entrenador.salario,
                paisOrigen = entrenador.paisOrigen,
                tipo = "ENTRENADOR",
                imagenUrl = entrenador.imagenUrl,
                createdAt = entrenador.createdAt,
                updatedAt = entrenador.updatedAt
            )
        )

        whenever(entrenadorDao.findById(generatedId)).thenReturn(
            EntrenadorEntity(
                id = generatedId,
                especializacion = entrenador.especializacion.name
            )
        )

        // Ejecutar
        val result = repository.save(entrenador) as Entrenador

        // Verificar
        assertEquals(generatedId, result.id)

        // Verificar que se llamaron los métodos correctos
        verify(personalDao).save(any())
        verify(entrenadorDao).save(any())
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
            id = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Mock para personalDao.save
        val generatedId = 2
        whenever(personalDao.save(any())).thenReturn(generatedId)

        // Mock para jugadorDao.save
        whenever(jugadorDao.save(any())).thenReturn(1)

        // Mock para getById
        whenever(personalDao.findById(generatedId)).thenReturn(
            PersonalEntity(
                id = generatedId,
                nombre = jugador.nombre,
                apellidos = jugador.apellidos,
                fechaNacimiento = jugador.fechaNacimiento,
                fechaIncorporacion = jugador.fechaIncorporacion,
                salario = jugador.salario,
                paisOrigen = jugador.paisOrigen,
                tipo = "JUGADOR",
                imagenUrl = jugador.imagenUrl,
                createdAt = jugador.createdAt,
                updatedAt = jugador.updatedAt
            )
        )

        whenever(jugadorDao.findById(generatedId)).thenReturn(
            JugadorEntity(
                id = generatedId,
                posicion = jugador.posicion.name,
                dorsal = jugador.dorsal,
                altura = jugador.altura,
                peso = jugador.peso,
                goles = jugador.goles,
                partidosJugados = jugador.partidosJugados
            )
        )

        // Ejecutar
        val result = repository.save(jugador) as Jugador

        // Verificar
        assertEquals(generatedId, result.id)

        // Verificar que se llamaron los métodos correctos
        verify(personalDao).save(any())
        verify(jugadorDao).save(any())
    }

    @Test
    fun `update debería actualizar un entrenador existente`() {
        // Preparar
        val id = 1
        val fechaNacimiento = LocalDate.now().minusYears(45)
        val fechaIncorporacion = LocalDate.now().minusMonths(3)
        val createdAt = LocalDateTime.now().minusMonths(1)
        val updatedAt = LocalDateTime.now()

        // Crear entidad original
        val originalPersonalEntity = PersonalEntity(
            id = id,
            nombre = "Original",
            apellidos = "Apellido",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 4000.0,
            paisOrigen = "España",
            tipo = "ENTRENADOR",
            imagenUrl = "",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val originalEntrenadorEntity = EntrenadorEntity(
            id = id,
            especializacion = "ENTRENADOR_PRINCIPAL"
        )

        // Mock para getById (original)
        whenever(personalDao.findById(id)).thenReturn(originalPersonalEntity)
        whenever(entrenadorDao.findById(id)).thenReturn(originalEntrenadorEntity)

        // Crear entrenador actualizado
        val actualizado = Entrenador(
            id = id,
            nombre = "Actualizado",
            apellidos = "Apellido",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 4000.0,
            paisOrigen = "España",
            createdAt = createdAt,
            updatedAt = updatedAt,
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
        )

        // Mock para update
        whenever(personalDao.update(any())).thenReturn(1)
        whenever(entrenadorDao.update(any())).thenReturn(1)

        // Mock para getById (actualizado)
        val updatedPersonalEntity = PersonalEntity(
            id = id,
            nombre = "Actualizado",
            apellidos = "Apellido",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 4000.0,
            paisOrigen = "España",
            tipo = "ENTRENADOR",
            imagenUrl = "",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        // Configurar el mock para devolver la entidad actualizada en la segunda llamada
        whenever(personalDao.findById(id))
            .thenReturn(originalPersonalEntity)
            .thenReturn(updatedPersonalEntity)

        // Ejecutar
        val result = repository.update(id, actualizado) as Entrenador

        // Verificar
        assertEquals("Actualizado", result.nombre)

        // Verificar que se llamaron los métodos correctos
        verify(personalDao).update(any())
        verify(entrenadorDao).update(any())
    }

    @Test
    fun `update debería actualizar un jugador existente`() {
        // Preparar
        val id = 2
        val fechaNacimiento = LocalDate.now().minusYears(25)
        val fechaIncorporacion = LocalDate.now().minusMonths(2)
        val createdAt = LocalDateTime.now().minusMonths(1)
        val updatedAt = LocalDateTime.now()

        // Crear entidad original
        val originalPersonalEntity = PersonalEntity(
            id = id,
            nombre = "Original",
            apellidos = "Apellido",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 8000.0,
            paisOrigen = "Brasil",
            tipo = "JUGADOR",
            imagenUrl = "",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val originalJugadorEntity = JugadorEntity(
            id = id,
            posicion = "DELANTERO",
            dorsal = 9,
            altura = 1.80,
            peso = 75.0,
            goles = 5,
            partidosJugados = 15
        )

        // Mock para getById (original)
        whenever(personalDao.findById(id)).thenReturn(originalPersonalEntity)
        whenever(jugadorDao.findById(id)).thenReturn(originalJugadorEntity)

        // Crear jugador actualizado
        val actualizado = Jugador(
            id = id,
            nombre = "JugadorActualizado",
            apellidos = "Apellido",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 8000.0,
            paisOrigen = "Brasil",
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 7,
            altura = 1.80,
            peso = 75.0,
            goles = 10,
            partidosJugados = 15,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        // Mock para update
        whenever(personalDao.update(any())).thenReturn(1)
        whenever(jugadorDao.update(any())).thenReturn(1)

        // Mock para getById (actualizado)
        val updatedPersonalEntity = PersonalEntity(
            id = id,
            nombre = "JugadorActualizado",
            apellidos = "Apellido",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 8000.0,
            paisOrigen = "Brasil",
            tipo = "JUGADOR",
            imagenUrl = "",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val updatedJugadorEntity = JugadorEntity(
            id = id,
            posicion = "DELANTERO",
            dorsal = 7,
            altura = 1.80,
            peso = 75.0,
            goles = 10,
            partidosJugados = 15
        )

        // Configurar el mock para devolver la entidad actualizada en la segunda llamada
        whenever(personalDao.findById(id))
            .thenReturn(originalPersonalEntity)
            .thenReturn(updatedPersonalEntity)

        whenever(jugadorDao.findById(id))
            .thenReturn(originalJugadorEntity)
            .thenReturn(updatedJugadorEntity)

        // Ejecutar
        val result = repository.update(id, actualizado) as Jugador

        // Verificar
        assertEquals("JugadorActualizado", result.nombre)
        assertEquals(7, result.dorsal)
        assertEquals(10, result.goles)

        // Verificar que se llamaron los métodos correctos
        verify(personalDao).update(any())
        verify(jugadorDao).update(any())
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
        val id = 3
        val fechaNacimiento = LocalDate.now().minusYears(45)
        val fechaIncorporacion = LocalDate.now().minusMonths(3)
        val createdAt = LocalDateTime.now().minusMonths(1)
        val updatedAt = LocalDateTime.now()

        // Crear entidad original
        val personalEntity = PersonalEntity(
            id = id,
            nombre = "Eliminar",
            apellidos = "Apellido",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 4000.0,
            paisOrigen = "España",
            tipo = "ENTRENADOR",
            imagenUrl = "",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val entrenadorEntity = EntrenadorEntity(
            id = id,
            especializacion = "ENTRENADOR_PRINCIPAL"
        )

        // Mock para getById (antes de eliminar)
        whenever(personalDao.findById(id)).thenReturn(personalEntity)
        whenever(entrenadorDao.findById(id)).thenReturn(entrenadorEntity)

        // Mock para delete
        whenever(entrenadorDao.delete(id)).thenReturn(1)
        whenever(personalDao.delete(id)).thenReturn(1)

        // Mock para getById (después de eliminar)
        whenever(personalDao.findById(id))
            .thenReturn(personalEntity)
            .thenReturn(null)

        // Ejecutar
        val result = repository.delete(id) as Entrenador

        // Verificar
        assertEquals("Eliminar", result.nombre)

        // Verificar que se llamaron los métodos correctos
        verify(entrenadorDao).delete(id)
        verify(personalDao).delete(id)
    }

    @Test
    fun `delete debería eliminar un jugador existente`() {
        // Preparar
        val id = 4
        val fechaNacimiento = LocalDate.now().minusYears(25)
        val fechaIncorporacion = LocalDate.now().minusMonths(2)
        val createdAt = LocalDateTime.now().minusMonths(1)
        val updatedAt = LocalDateTime.now()

        // Crear entidad original
        val personalEntity = PersonalEntity(
            id = id,
            nombre = "JugadorEliminar",
            apellidos = "Apellido",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 8000.0,
            paisOrigen = "Brasil",
            tipo = "JUGADOR",
            imagenUrl = "",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val jugadorEntity = JugadorEntity(
            id = id,
            posicion = "DELANTERO",
            dorsal = 9,
            altura = 1.80,
            peso = 75.0,
            goles = 5,
            partidosJugados = 15
        )

        // Mock para getById (antes de eliminar)
        whenever(personalDao.findById(id)).thenReturn(personalEntity)
        whenever(jugadorDao.findById(id)).thenReturn(jugadorEntity)

        // Mock para delete
        whenever(jugadorDao.delete(id)).thenReturn(1)
        whenever(personalDao.delete(id)).thenReturn(1)

        // Mock para getById (después de eliminar)
        whenever(personalDao.findById(id))
            .thenReturn(personalEntity)
            .thenReturn(null)

        // Ejecutar
        val result = repository.delete(id) as Jugador

        // Verificar
        assertEquals("JugadorEliminar", result.nombre)

        // Verificar que se llamaron los métodos correctos
        verify(jugadorDao).delete(id)
        verify(personalDao).delete(id)
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
        val id = 5
        val fechaNacimiento = LocalDate.now().minusYears(45)
        val fechaIncorporacion = LocalDate.now().minusMonths(3)
        val createdAt = LocalDateTime.now().minusMonths(1)
        val updatedAt = LocalDateTime.now()

        // Crear entidad para el entrenador
        val personalEntity = PersonalEntity(
            id = id,
            nombre = "Cache",
            apellidos = "Apellido",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 4000.0,
            paisOrigen = "España",
            tipo = "ENTRENADOR",
            imagenUrl = "",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val entrenadorEntity = EntrenadorEntity(
            id = id,
            especializacion = "ENTRENADOR_PRINCIPAL"
        )

        // Mock para getById
        whenever(personalDao.findById(id)).thenReturn(personalEntity)
        whenever(entrenadorDao.findById(id)).thenReturn(entrenadorEntity)

        // Mock para getAll (entrenadores)
        whenever(personalDao.findByTipo("ENTRENADOR")).thenReturn(listOf(personalEntity))
        whenever(entrenadorDao.findAll()).thenReturn(listOf(entrenadorEntity))

        // Mock para getAll (jugadores) - vacío para simplificar
        whenever(personalDao.findByTipo("JUGADOR")).thenReturn(emptyList())
        whenever(jugadorDao.findAll()).thenReturn(emptyList())

        // Llenar la caché
        val firstCall = repository.getAll()

        // Verificar que la caché se llenó
        assertNotNull(repository.getById(id))

        // Ejecutar
        repository.clearCache()

        // Verificar indirectamente que la caché está vacía
        // Si la caché está vacía, getAll() debería volver a cargar los datos de la base de datos
        val secondCall = repository.getAll()

        // Verificar que los datos se recargaron correctamente
        assertEquals(firstCall.size, secondCall.size)
        assertTrue(secondCall.any { it.nombre == "Cache" && it is Entrenador })

        // Verificar que se llamaron los métodos correctos
        verify(personalDao, times(2)).findByTipo("ENTRENADOR")
        verify(entrenadorDao, times(2)).findAll()
    }

    @Test
    fun `getAllEntrenadores debería devolver solo entrenadores`() {
        // Preparar
        val id1 = 6
        val id2 = 7
        val fechaNacimiento = LocalDate.now().minusYears(45)
        val fechaIncorporacion = LocalDate.now().minusMonths(3)
        val createdAt = LocalDateTime.now().minusMonths(1)
        val updatedAt = LocalDateTime.now()

        // Crear entidades para los entrenadores
        val personalEntity1 = PersonalEntity(
            id = id1,
            nombre = "Entrenador1",
            apellidos = "Apellido1",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 4000.0,
            paisOrigen = "España",
            tipo = "ENTRENADOR",
            imagenUrl = "",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val entrenadorEntity1 = EntrenadorEntity(
            id = id1,
            especializacion = "ENTRENADOR_PRINCIPAL"
        )

        val personalEntity2 = PersonalEntity(
            id = id2,
            nombre = "Entrenador2",
            apellidos = "Apellido2",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 4500.0,
            paisOrigen = "España",
            tipo = "ENTRENADOR",
            imagenUrl = "",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val entrenadorEntity2 = EntrenadorEntity(
            id = id2,
            especializacion = "ENTRENADOR_ASISTENTE"
        )

        // Mock para getAll (entrenadores)
        whenever(personalDao.findByTipo("ENTRENADOR")).thenReturn(listOf(personalEntity1, personalEntity2))
        whenever(entrenadorDao.findAll()).thenReturn(listOf(entrenadorEntity1, entrenadorEntity2))

        // Ejecutar
        val result = repository.getAllEntrenadores()

        // Verificar
        assertEquals(2, result.size)
        assertTrue(result.all { it is Entrenador })
        assertTrue(result.any { it.nombre == "Entrenador1" })
        assertTrue(result.any { it.nombre == "Entrenador2" })

        // Verificar que se llamaron los métodos correctos
        verify(personalDao).findByTipo("ENTRENADOR")
        verify(entrenadorDao).findAll()
    }
}
