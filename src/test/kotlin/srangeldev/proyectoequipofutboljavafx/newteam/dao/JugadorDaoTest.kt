package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JugadorDaoTest {
    private lateinit var jdbi: Jdbi
    private lateinit var dao: JugadorDao
    private lateinit var personalDao: PersonalDao

    val personalEntity = PersonalEntity(
        id = 1,
        nombre = "Jugador",
        apellidos = "Test",
        fechaNacimiento = LocalDate.parse("1995-01-01"),
        fechaIncorporacion = LocalDate.parse("2020-01-01"),
        salario = 4000.0,
        paisOrigen = "España",
        tipo = "JUGADOR",
        imagenUrl = "jugador.jpg",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    val jugadorEntity = JugadorEntity(
        id = 1,
        posicion = "DEFENSA",
        dorsal = 4,
        altura = 185.0,
        peso = 80.0,
        goles = 5,
        partidosJugados = 30
    )

    @BeforeAll
    fun setUp() {
        // Configurar JDBI con base de datos en fichero SQLite
        jdbi = Jdbi.create("jdbc:sqlite:test.db")
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        // Crear tablas
        jdbi.useHandle<Exception> { handle ->
            // Crear tabla Personal primero (necesaria para la clave foránea)
            handle.execute("""
                CREATE TABLE IF NOT EXISTS Personal (
                    id INTEGER PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    apellidos TEXT NOT NULL,
                    fecha_nacimiento DATE NOT NULL,
                    fecha_incorporacion DATE NOT NULL,
                    salario REAL NOT NULL,
                    pais_origen TEXT NOT NULL,
                    tipo TEXT NOT NULL CHECK (tipo IN ('ENTRENADOR', 'JUGADOR')),
                    imagen_url TEXT DEFAULT '',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """)

            // Crear tabla Jugadores
            handle.execute("""
                CREATE TABLE IF NOT EXISTS Jugadores (
                    id INTEGER PRIMARY KEY,
                    posicion TEXT NOT NULL CHECK (
                        posicion IN ('PORTERO', 'DEFENSA', 'CENTROCAMPISTA', 'DELANTERO')
                    ),
                    dorsal INTEGER NOT NULL,
                    altura REAL NOT NULL,
                    peso REAL NOT NULL,
                    goles INTEGER NOT NULL DEFAULT 0,
                    partidos_jugados INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (id) REFERENCES Personal(id) ON DELETE CASCADE
                )
            """)
        }

        // Inicializar DAOs
        dao = jdbi.onDemand(JugadorDao::class.java)
        personalDao = jdbi.onDemand(PersonalDao::class.java)
    }

    @AfterEach
    fun tearDown() {
        // Limpiar datos después de cada test
        jdbi.useHandle<Exception> { handle ->
            handle.execute("DELETE FROM Jugadores")
            handle.execute("DELETE FROM Personal")
        }
    }

    private fun insertPersonalAndJugador(): Int {
        // Insertar primero en Personal (para satisfacer la clave foránea)
        val personalId = personalDao.save(personalEntity)
        // Insertar en Jugadores
        dao.save(jugadorEntity.copy(id = personalId))
        return personalId
    }

    @Nested
    @DisplayName("Casos correctos")
    inner class CasosCorrectos {
        @Test
        @DisplayName("Insertar jugador")
        fun saveJugador() {
            val personalId = personalDao.save(personalEntity)
            val result = dao.save(jugadorEntity.copy(id = personalId))

            assertEquals(1, result, "Debería haberse insertado 1 registro")

            val savedJugador = dao.findById(personalId)
            assertNotNull(savedJugador, "El jugador no debería ser nulo")
            assertEquals(jugadorEntity.posicion, savedJugador!!.posicion, "Las posiciones deberían ser iguales")
            assertEquals(jugadorEntity.dorsal, savedJugador.dorsal, "Los dorsales deberían ser iguales")
        }

        @Test
        @DisplayName("Actualizar jugador")
        fun updateJugador() {
            val personalId = insertPersonalAndJugador()

            val updatedEntity = jugadorEntity.copy(
                id = personalId,
                posicion = "CENTROCAMPISTA",
                dorsal = 8,
                goles = 10
            )

            val updateResult = dao.update(updatedEntity)
            val result = dao.findById(personalId)

            assertEquals(1, updateResult, "Debería haberse actualizado 1 registro")
            assertNotNull(result, "El resultado no debería ser nulo")
            assertEquals("CENTROCAMPISTA", result!!.posicion, "La posición debería haberse actualizado")
            assertEquals(8, result.dorsal, "El dorsal debería haberse actualizado")
            assertEquals(10, result.goles, "Los goles deberían haberse actualizado")
        }

        @Test
        @DisplayName("Eliminar jugador")
        fun deleteJugador() {
            val personalId = insertPersonalAndJugador()

            val deleteResult = dao.delete(personalId)
            val result = dao.findById(personalId)

            assertEquals(1, deleteResult, "Debería haberse eliminado 1 registro")
            assertNull(result, "El resultado debería ser nulo después de eliminar")
        }

        @Test
        @DisplayName("Obtener todos los jugadores")
        fun findAll() {
            // Insertar varios jugadores
            val personalId1 = personalDao.save(personalEntity)
            dao.save(jugadorEntity.copy(id = personalId1))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador2"))
            dao.save(jugadorEntity.copy(id = personalId2, posicion = "PORTERO", dorsal = 1))

            val personalId3 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador3"))
            dao.save(jugadorEntity.copy(id = personalId3, posicion = "DELANTERO", dorsal = 9))

            val result = dao.findAll()

            assertEquals(3, result.size, "Deberían haber 3 jugadores")
            assertTrue(result.any { it.posicion == "DEFENSA" }, "Debería existir un defensa")
            assertTrue(result.any { it.posicion == "PORTERO" }, "Debería existir un portero")
            assertTrue(result.any { it.posicion == "DELANTERO" }, "Debería existir un delantero")
        }

        @Test
        @DisplayName("Buscar por posición")
        fun findByPosicion() {
            // Insertar varios jugadores con diferentes posiciones
            val personalId1 = personalDao.save(personalEntity)
            dao.save(jugadorEntity.copy(id = personalId1))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador2"))
            dao.save(jugadorEntity.copy(id = personalId2, posicion = "DEFENSA", dorsal = 2))

            val personalId3 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador3"))
            dao.save(jugadorEntity.copy(id = personalId3, posicion = "CENTROCAMPISTA", dorsal = 8))

            val result = dao.findByPosicion("DEFENSA")

            assertEquals(2, result.size, "Deberían haber 2 defensas")
        }

        @Test
        @DisplayName("Buscar por dorsal")
        fun findByDorsal() {
            // Insertar varios jugadores con diferentes dorsales
            val personalId1 = personalDao.save(personalEntity)
            dao.save(jugadorEntity.copy(id = personalId1))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador2"))
            dao.save(jugadorEntity.copy(id = personalId2, dorsal = 10))

            val result = dao.findByDorsal(10)

            assertNotNull(result, "El resultado no debería ser nulo")
            assertEquals(10, result!!.dorsal, "El dorsal debería ser 10")
        }

        @Test
        @DisplayName("Contar jugadores")
        fun count() {
            // Insertar varios jugadores
            val personalId1 = personalDao.save(personalEntity)
            dao.save(jugadorEntity.copy(id = personalId1))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador2"))
            dao.save(jugadorEntity.copy(id = personalId2))

            val result = dao.count()

            assertEquals(2, result, "Deberían haber 2 jugadores en total")
        }

        @Test
        @DisplayName("Contar por posición")
        fun countByPosicion() {
            // Insertar varios jugadores con diferentes posiciones
            val personalId1 = personalDao.save(personalEntity)
            dao.save(jugadorEntity.copy(id = personalId1))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador2"))
            dao.save(jugadorEntity.copy(id = personalId2, posicion = "DEFENSA"))

            val personalId3 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador3"))
            dao.save(jugadorEntity.copy(id = personalId3, posicion = "CENTROCAMPISTA"))

            val defensas = dao.countByPosicion("DEFENSA")
            val centrocampistas = dao.countByPosicion("CENTROCAMPISTA")

            assertEquals(2, defensas, "Deberían haber 2 defensas")
            assertEquals(1, centrocampistas, "Debería haber 1 centrocampista")
        }

        @Test
        @DisplayName("Encontrar top goleadores")
        fun findTopGoleadores() {
            // Insertar varios jugadores con diferentes goles
            val personalId1 = personalDao.save(personalEntity)
            dao.save(jugadorEntity.copy(id = personalId1, goles = 5))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador2"))
            dao.save(jugadorEntity.copy(id = personalId2, goles = 10))

            val personalId3 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador3"))
            dao.save(jugadorEntity.copy(id = personalId3, goles = 3))

            val result = dao.findTopGoleadores(2)

            assertEquals(2, result.size, "Deberían haber 2 jugadores en el top")
            assertEquals(10, result[0].goles, "El primer jugador debería tener 10 goles")
            assertEquals(5, result[1].goles, "El segundo jugador debería tener 5 goles")
        }

        @Test
        @DisplayName("Encontrar top por partidos jugados")
        fun findTopPartidosJugados() {
            // Insertar varios jugadores con diferentes partidos jugados
            val personalId1 = personalDao.save(personalEntity)
            dao.save(jugadorEntity.copy(id = personalId1, partidosJugados = 30))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador2"))
            dao.save(jugadorEntity.copy(id = personalId2, partidosJugados = 40))

            val personalId3 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador3"))
            dao.save(jugadorEntity.copy(id = personalId3, partidosJugados = 20))

            val result = dao.findTopPartidosJugados(2)

            assertEquals(2, result.size, "Deberían haber 2 jugadores en el top")
            assertEquals(40, result[0].partidosJugados, "El primer jugador debería tener 40 partidos")
            assertEquals(30, result[1].partidosJugados, "El segundo jugador debería tener 30 partidos")
        }

        @Test
        @DisplayName("Sumar goles")
        fun sumGoles() {
            // Insertar varios jugadores con diferentes goles
            val personalId1 = personalDao.save(personalEntity)
            dao.save(jugadorEntity.copy(id = personalId1, goles = 5))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador2"))
            dao.save(jugadorEntity.copy(id = personalId2, goles = 10))

            val personalId3 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador3"))
            dao.save(jugadorEntity.copy(id = personalId3, goles = 3))

            val result = dao.sumGoles()

            assertEquals(18, result, "La suma de goles debería ser 18")
        }

        @Test
        @DisplayName("Promedio de altura")
        fun avgAltura() {
            // Insertar varios jugadores con diferentes alturas
            val personalId1 = personalDao.save(personalEntity)
            dao.save(jugadorEntity.copy(id = personalId1, altura = 185.0))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador2"))
            dao.save(jugadorEntity.copy(id = personalId2, altura = 175.0))

            val personalId3 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador3"))
            dao.save(jugadorEntity.copy(id = personalId3, altura = 190.0))

            val result = dao.avgAltura()

            assertEquals(183.33, result, 0.01, "El promedio de altura debería ser aproximadamente 183.33")
        }

        @Test
        @DisplayName("Promedio de peso")
        fun avgPeso() {
            // Insertar varios jugadores con diferentes pesos
            val personalId1 = personalDao.save(personalEntity)
            dao.save(jugadorEntity.copy(id = personalId1, peso = 80.0))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador2"))
            dao.save(jugadorEntity.copy(id = personalId2, peso = 75.0))

            val personalId3 = personalDao.save(personalEntity.copy(id = 0, nombre = "Jugador3"))
            dao.save(jugadorEntity.copy(id = personalId3, peso = 85.0))

            val result = dao.avgPeso()

            assertEquals(80.0, result, 0.01, "El promedio de peso debería ser 80.0")
        }
    }

    @Nested
    @DisplayName("Casos incorrectos")
    inner class CasosIncorrectos {
        @Test
        @DisplayName("Buscar por ID inexistente")
        fun findByIdInexistente() {
            val result = dao.findById(999)
            assertNull(result, "El resultado debería ser nulo para un ID inexistente")
        }

        @Test
        @DisplayName("Actualizar jugador inexistente")
        fun updateInexistente() {
            val result = dao.update(jugadorEntity.copy(id = 999))
            assertEquals(0, result, "No debería haberse actualizado ningún registro")
        }

        @Test
        @DisplayName("Eliminar jugador inexistente")
        fun deleteInexistente() {
            val result = dao.delete(999)
            assertEquals(0, result, "No debería haberse eliminado ningún registro")
        }

        @Test
        @DisplayName("Buscar por posición inexistente")
        fun findByPosicionInexistente() {
            val result = dao.findByPosicion("POSICIÓN_INEXISTENTE")
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }

        @Test
        @DisplayName("Buscar por dorsal inexistente")
        fun findByDorsalInexistente() {
            val result = dao.findByDorsal(999)
            assertNull(result, "El resultado debería ser nulo para un dorsal inexistente")
        }
    }
}
