package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntrenadorDaoTest {
    private lateinit var jdbi: Jdbi
    private lateinit var dao: EntrenadorDao
    private lateinit var personalDao: PersonalDao
    private lateinit var mockDao: EntrenadorDao
    private lateinit var mockPersonalDao: PersonalDao

    val personalEntity = PersonalEntity(
        id = 1,
        nombre = "Entrenador",
        apellidos = "Test",
        fechaNacimiento = LocalDate.parse("1980-01-01"),
        fechaIncorporacion = LocalDate.parse("2020-01-01"),
        salario = 5000.0,
        paisOrigen = "España",
        tipo = "ENTRENADOR",
        imagenUrl = "entrenador.jpg",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    val entrenadorEntity = EntrenadorEntity(
        id = 1,
        especializacion = "ENTRENADOR_PRINCIPAL"
    )

    @BeforeAll
    fun setUp() {
        // Inicializar los mock DAOs
        dao = mockDao
        personalDao = mockPersonalDao

        // Mantenemos la inicialización de JDBI por si se necesita para alguna prueba
        jdbi = Jdbi.create("jdbc:sqlite:test.db")
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())
    }

    @AfterEach
    fun tearDown() {
        // Limpiar datos después de cada test usando los métodos clear de los mocks
        mockDao.deleteAll()
        mockPersonalDao.deleteAll()
    }

    private fun insertPersonalAndEntrenador(): Int {
        // Insertar primero en Personal (para satisfacer la clave foránea)
        val personalId = personalDao.save(personalEntity)
        // Insertar en Entrenadores
        dao.save(entrenadorEntity.copy(id = personalId))
        return personalId
    }

    @Nested
    @DisplayName("Casos correctos")
    inner class CasosCorrectos {
        @Test
        @DisplayName("Insertar entrenador")
        fun saveEntrenador() {
            val personalId = personalDao.save(personalEntity)
            val result = dao.save(entrenadorEntity.copy(id = personalId))

            assertEquals(1, result, "Debería haberse insertado 1 registro")

            val savedEntrenador = dao.findById(personalId)
            assertNotNull(savedEntrenador, "El entrenador no debería ser nulo")
            assertEquals(entrenadorEntity.especializacion, savedEntrenador!!.especializacion, "Las especializaciones deberían ser iguales")
        }

        @Test
        @DisplayName("Actualizar entrenador")
        fun updateEntrenador() {
            val personalId = insertPersonalAndEntrenador()

            val updatedEntity = entrenadorEntity.copy(
                id = personalId,
                especializacion = "ENTRENADOR_ASISTENTE"
            )

            val updateResult = dao.update(updatedEntity)
            val result = dao.findById(personalId)

            assertEquals(1, updateResult, "Debería haberse actualizado 1 registro")
            assertNotNull(result, "El resultado no debería ser nulo")
            assertEquals("ENTRENADOR_ASISTENTE", result!!.especializacion, "La especialización debería haberse actualizado")
        }

        @Test
        @DisplayName("Eliminar entrenador")
        fun deleteEntrenador() {
            val personalId = insertPersonalAndEntrenador()

            val deleteResult = dao.delete(personalId)
            val result = dao.findById(personalId)

            assertEquals(1, deleteResult, "Debería haberse eliminado 1 registro")
            assertNull(result, "El resultado debería ser nulo después de eliminar")
        }

        @Test
        @DisplayName("Obtener todos los entrenadores")
        fun findAll() {
            // Insertar varios entrenadores
            val personalId1 = personalDao.save(personalEntity)
            dao.save(entrenadorEntity.copy(id = personalId1))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Entrenador2"))
            dao.save(entrenadorEntity.copy(id = personalId2, especializacion = "ENTRENADOR_ASISTENTE"))

            val personalId3 = personalDao.save(personalEntity.copy(id = 0, nombre = "Entrenador3"))
            dao.save(entrenadorEntity.copy(id = personalId3, especializacion = "ENTRENADOR_PORTEROS"))

            val result = dao.findAll()

            assertEquals(3, result.size, "Deberían haber 3 entrenadores")
            assertTrue(result.any { it.especializacion == "ENTRENADOR_PRINCIPAL" }, "Debería existir un entrenador principal")
            assertTrue(result.any { it.especializacion == "ENTRENADOR_ASISTENTE" }, "Debería existir un entrenador asistente")
            assertTrue(result.any { it.especializacion == "ENTRENADOR_PORTEROS" }, "Debería existir un entrenador de porteros")
        }

        @Test
        @DisplayName("Buscar por especialización")
        fun findByEspecializacion() {
            // Insertar varios entrenadores con diferentes especializaciones
            val personalId1 = personalDao.save(personalEntity)
            dao.save(entrenadorEntity.copy(id = personalId1))

            val personalId2 = personalDao.save(personalEntity.copy(id = 2, nombre = "Entrenador2"))
            dao.save(entrenadorEntity.copy(id = personalId2, especializacion = "ENTRENADOR_ASISTENTE"))

            val personalId3 = personalDao.save(personalEntity.copy(id = 3, nombre = "Entrenador3"))
            dao.save(entrenadorEntity.copy(id = personalId3, especializacion = "ENTRENADOR_ASISTENTE"))

            val result = dao.findByEspecializacion("ENTRENADOR_ASISTENTE")

            assertEquals(2, result.size, "Deberían haber 2 entrenadores asistentes")
        }

        @Test
        @DisplayName("Contar entrenadores")
        fun count() {
            // Insertar varios entrenadores
            val personalId1 = personalDao.save(personalEntity)
            dao.save(entrenadorEntity.copy(id = personalId1))

            val personalId2 = personalDao.save(personalEntity.copy(id = 2, nombre = "Entrenador2"))
            dao.save(entrenadorEntity.copy(id = personalId2))

            val result = dao.count()

            assertEquals(2, result, "Deberían haber 2 entrenadores en total")
        }

        @Test
        @DisplayName("Contar por especialización")
        fun countByEspecializacion() {
            // Insertar varios entrenadores con diferentes especializaciones
            val personalId1 = personalDao.save(personalEntity)
            dao.save(entrenadorEntity.copy(id = personalId1))

            val personalId2 = personalDao.save(personalEntity.copy(id = 0, nombre = "Entrenador2"))
            dao.save(entrenadorEntity.copy(id = personalId2, especializacion = "ENTRENADOR_ASISTENTE"))

            val personalId3 = personalDao.save(personalEntity.copy(id = 0, nombre = "Entrenador3"))
            dao.save(entrenadorEntity.copy(id = personalId3, especializacion = "ENTRENADOR_ASISTENTE"))

            val principales = dao.countByEspecializacion("ENTRENADOR_PRINCIPAL")
            val asistentes = dao.countByEspecializacion("ENTRENADOR_ASISTENTE")

            assertEquals(1, principales, "Debería haber 1 entrenador principal")
            assertEquals(2, asistentes, "Deberían haber 2 entrenadores asistentes")
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
        @DisplayName("Actualizar entrenador inexistente")
        fun updateInexistente() {
            val result = dao.update(entrenadorEntity.copy(id = 999))
            assertEquals(0, result, "No debería haberse actualizado ningún registro")
        }

        @Test
        @DisplayName("Eliminar entrenador inexistente")
        fun deleteInexistente() {
            val result = dao.delete(999)
            assertEquals(0, result, "No debería haberse eliminado ningún registro")
        }

        @Test
        @DisplayName("Buscar por especialización inexistente")
        fun findByEspecializacionInexistente() {
            val result = dao.findByEspecializacion("ESPECIALIZACIÓN_INEXISTENTE")
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }
    }
}
