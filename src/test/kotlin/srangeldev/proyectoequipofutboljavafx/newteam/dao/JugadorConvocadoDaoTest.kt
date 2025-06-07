package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JugadorConvocadoDaoTest {
    private lateinit var jdbi: Jdbi
    private lateinit var dao: JugadorConvocadoDao
    private lateinit var convocatoriaDao: ConvocatoriaDao

    val convocatoriaEntity = ConvocatoriaEntity(
        id = 1,
        fecha = LocalDate.now(),
        descripcion = "Convocatoria para partido de liga",
        equipoId = 1,
        entrenadorId = 1,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    val jugadorConvocadoEntity = JugadorConvocadoEntity(
        convocatoriaId = 1,
        jugadorId = 1,
        esTitular = true
    )

    @BeforeAll
    fun setUp() {
        // Configurar JDBI con base de datos en fichero SQLite
        jdbi = Jdbi.create("jdbc:sqlite:test.db")
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        // Crear tablas
        jdbi.useHandle<Exception> { handle ->
            // Crear tabla Convocatorias primero (necesaria para la clave foránea)
            handle.execute("""
                CREATE TABLE IF NOT EXISTS Convocatorias (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha DATE NOT NULL,
                    descripcion TEXT NOT NULL,
                    equipo_id INTEGER NOT NULL,
                    entrenador_id INTEGER NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (equipo_id) REFERENCES Equipos(id) ON DELETE CASCADE,
                    FOREIGN KEY (entrenador_id) REFERENCES Personal(id) ON DELETE CASCADE
                )
            """)

            // Crear tabla JugadoresConvocados
            handle.execute("""
                CREATE TABLE IF NOT EXISTS JugadoresConvocados (
                    convocatoria_id INTEGER NOT NULL,
                    jugador_id INTEGER NOT NULL,
                    es_titular INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (convocatoria_id, jugador_id),
                    FOREIGN KEY (convocatoria_id) REFERENCES Convocatorias(id) ON DELETE CASCADE,
                    FOREIGN KEY (jugador_id) REFERENCES Personal(id) ON DELETE CASCADE
                )
            """)
        }

        // Inicializar DAOs
        dao = jdbi.onDemand(JugadorConvocadoDao::class.java)
        convocatoriaDao = jdbi.onDemand(ConvocatoriaDao::class.java)
    }

    @AfterEach
    fun tearDown() {
        // Limpiar datos después de cada test
        jdbi.useHandle<Exception> { handle ->
            handle.execute("DELETE FROM JugadoresConvocados")
            handle.execute("DELETE FROM Convocatorias")
        }
    }

    private fun insertConvocatoriaAndJugadorConvocado(): Int {
        // Insertar primero en Convocatorias (para satisfacer la clave foránea)
        val convocatoriaId = convocatoriaDao.save(convocatoriaEntity)
        // Insertar en JugadoresConvocados
        dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId))
        return convocatoriaId
    }

    @Nested
    @DisplayName("Casos correctos")
    inner class CasosCorrectos {
        @Test
        @DisplayName("Insertar jugador convocado")
        fun saveJugadorConvocado() {
            val convocatoriaId = convocatoriaDao.save(convocatoriaEntity)
            val jugadorId = jugadorConvocadoEntity.jugadorId
            val rowsAffected = dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId))

            val result = dao.findById(convocatoriaId, jugadorId)

            assertEquals(1, rowsAffected, "Debería haberse insertado 1 registro")
            assertNotNull(result, "El jugador convocado no debería ser nulo")
            assertEquals(convocatoriaId, result!!.convocatoriaId, "Los IDs de convocatoria deberían ser iguales")
            assertEquals(jugadorConvocadoEntity.jugadorId, result.jugadorId, "Los IDs de jugador deberían ser iguales")
            assertEquals(jugadorConvocadoEntity.esTitular, result.esTitular, "El estado de titular debería ser igual")
        }

        @Test
        @DisplayName("Actualizar jugador convocado")
        fun updateJugadorConvocado() {
            val convocatoriaId = insertConvocatoriaAndJugadorConvocado()
            val jugadorConvocado = dao.findByConvocatoriaId(convocatoriaId).first()
            val jugadorId = jugadorConvocado.jugadorId

            val updatedEntity = jugadorConvocado.copy(
                esTitular = false
            )

            val updateResult = dao.update(updatedEntity)
            val result = dao.findById(convocatoriaId, jugadorId)

            assertEquals(1, updateResult, "Debería haberse actualizado 1 registro")
            assertNotNull(result, "El resultado no debería ser nulo")
            assertFalse(result!!.esTitular, "El estado de titular debería haberse actualizado a false")
        }

        @Test
        @DisplayName("Eliminar jugador convocado")
        fun deleteJugadorConvocado() {
            val convocatoriaId = insertConvocatoriaAndJugadorConvocado()
            val jugadorConvocado = dao.findByConvocatoriaId(convocatoriaId).first()
            val jugadorId = jugadorConvocado.jugadorId

            val deleteResult = dao.delete(convocatoriaId, jugadorId)
            val result = dao.findById(convocatoriaId, jugadorId)

            assertEquals(1, deleteResult, "Debería haberse eliminado 1 registro")
            assertNull(result, "El resultado debería ser nulo después de eliminar")
        }

        @Test
        @DisplayName("Eliminar por convocatoria ID")
        fun deleteByConvocatoriaId() {
            val convocatoriaId = convocatoriaDao.save(convocatoriaEntity)

            // Insertar varios jugadores convocados para la misma convocatoria
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 1))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 2))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 3))

            val deleteResult = dao.deleteByConvocatoriaId(convocatoriaId)
            val result = dao.findByConvocatoriaId(convocatoriaId)

            assertEquals(3, deleteResult, "Deberían haberse eliminado 3 registros")
            assertTrue(result.isEmpty(), "La lista debería estar vacía después de eliminar")
        }

        @Test
        @DisplayName("Obtener todos los jugadores convocados")
        fun findAll() {
            val convocatoriaId = convocatoriaDao.save(convocatoriaEntity)

            // Insertar varios jugadores convocados
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 1))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 2))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 3))

            val result = dao.findAll()

            assertEquals(3, result.size, "Deberían haber 3 jugadores convocados")
        }

        @Test
        @DisplayName("Buscar por convocatoria ID")
        fun findByConvocatoriaId() {
            // Crear dos convocatorias
            val convocatoriaId1 = convocatoriaDao.save(convocatoriaEntity)
            val convocatoriaId2 = convocatoriaDao.save(convocatoriaEntity.copy(id = 0))

            // Insertar jugadores convocados para diferentes convocatorias
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId1, jugadorId = 1))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId1, jugadorId = 2))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId2, jugadorId = 3))

            val result = dao.findByConvocatoriaId(convocatoriaId1)

            assertEquals(2, result.size, "Deberían haber 2 jugadores convocados para la convocatoria 1")
        }

        @Test
        @DisplayName("Buscar por jugador ID")
        fun findByJugadorId() {
            // Crear dos convocatorias
            val convocatoriaId1 = convocatoriaDao.save(convocatoriaEntity)
            val convocatoriaId2 = convocatoriaDao.save(convocatoriaEntity.copy(id = 0))

            // Insertar el mismo jugador en diferentes convocatorias
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId1, jugadorId = 1))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId2, jugadorId = 1))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId1, jugadorId = 2))

            val result = dao.findByJugadorId(1)

            assertEquals(2, result.size, "Deberían haber 2 convocatorias para el jugador 1")
        }

        @Test
        @DisplayName("Buscar titulares por convocatoria ID")
        fun findTitularesByConvocatoriaId() {
            val convocatoriaId = convocatoriaDao.save(convocatoriaEntity)

            // Insertar jugadores titulares y suplentes
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 1, esTitular = true))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 2, esTitular = true))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 3, esTitular = false))

            val result = dao.findTitularesByConvocatoriaId(convocatoriaId)

            assertEquals(2, result.size, "Deberían haber 2 jugadores titulares")
        }

        @Test
        @DisplayName("Contar por convocatoria ID")
        fun countByConvocatoriaId() {
            val convocatoriaId = convocatoriaDao.save(convocatoriaEntity)

            // Insertar varios jugadores convocados
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 1))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 2))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 3))

            val result = dao.countByConvocatoriaId(convocatoriaId)

            assertEquals(3, result, "Deberían haber 3 jugadores convocados")
        }

        @Test
        @DisplayName("Contar titulares por convocatoria ID")
        fun countTitularesByConvocatoriaId() {
            val convocatoriaId = convocatoriaDao.save(convocatoriaEntity)

            // Insertar jugadores titulares y suplentes
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 1, esTitular = true))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 2, esTitular = true))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 3, esTitular = false))

            val result = dao.countTitularesByConvocatoriaId(convocatoriaId)

            assertEquals(2, result, "Deberían haber 2 jugadores titulares")
        }

        @Test
        @DisplayName("Obtener IDs de jugadores por convocatoria ID")
        fun getJugadoresIdsByConvocatoriaId() {
            val convocatoriaId = convocatoriaDao.save(convocatoriaEntity)

            // Insertar varios jugadores convocados
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 1))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 2))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 3))

            val result = dao.getJugadoresIdsByConvocatoriaId(convocatoriaId)

            assertEquals(3, result.size, "Deberían haber 3 IDs de jugadores")
            assertTrue(result.contains(1), "Debería contener el ID 1")
            assertTrue(result.contains(2), "Debería contener el ID 2")
            assertTrue(result.contains(3), "Debería contener el ID 3")
        }

        @Test
        @DisplayName("Obtener IDs de titulares por convocatoria ID")
        fun getTitularesIdsByConvocatoriaId() {
            val convocatoriaId = convocatoriaDao.save(convocatoriaEntity)

            // Insertar jugadores titulares y suplentes
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 1, esTitular = true))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 2, esTitular = true))
            dao.save(jugadorConvocadoEntity.copy(convocatoriaId = convocatoriaId, jugadorId = 3, esTitular = false))

            val result = dao.getTitularesIdsByConvocatoriaId(convocatoriaId)

            assertEquals(2, result.size, "Deberían haber 2 IDs de jugadores titulares")
            assertTrue(result.contains(1), "Debería contener el ID 1")
            assertTrue(result.contains(2), "Debería contener el ID 2")
            assertFalse(result.contains(3), "No debería contener el ID 3")
        }
    }

    @Nested
    @DisplayName("Casos incorrectos")
    inner class CasosIncorrectos {
        @Test
        @DisplayName("Buscar por ID inexistente")
        fun findByIdInexistente() {
            val result = dao.findById(999, 999)
            assertNull(result, "El resultado debería ser nulo para un ID inexistente")
        }

        @Test
        @DisplayName("Actualizar jugador convocado inexistente")
        fun updateInexistente() {
            assertThrows<Exception> {
                dao.update(jugadorConvocadoEntity.copy(convocatoriaId = 999, jugadorId = 999))
            }
        }

        @Test
        @DisplayName("Eliminar jugador convocado inexistente")
        fun deleteInexistente() {
            val result = dao.delete(999, 999)
            assertEquals(0, result, "No debería haberse eliminado ningún registro")
        }

        @Test
        @DisplayName("Buscar por convocatoria ID inexistente")
        fun findByConvocatoriaIdInexistente() {
            val result = dao.findByConvocatoriaId(999)
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }

        @Test
        @DisplayName("Buscar por jugador ID inexistente")
        fun findByJugadorIdInexistente() {
            val result = dao.findByJugadorId(999)
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }

        @Test
        @DisplayName("Buscar titulares por convocatoria ID inexistente")
        fun findTitularesByConvocatoriaIdInexistente() {
            val result = dao.findTitularesByConvocatoriaId(999)
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }

        @Test
        @DisplayName("Eliminar por convocatoria ID inexistente")
        fun deleteByConvocatoriaIdInexistente() {
            val result = dao.deleteByConvocatoriaId(999)
            assertEquals(0, result, "No debería haberse eliminado ningún registro")
        }
    }
}
