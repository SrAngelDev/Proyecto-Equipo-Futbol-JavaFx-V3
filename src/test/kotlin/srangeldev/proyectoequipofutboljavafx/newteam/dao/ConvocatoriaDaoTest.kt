package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConvocatoriaDaoTest {
    private lateinit var jdbi: Jdbi
    private lateinit var dao: ConvocatoriaDao

    private val testDate = LocalDate.now()
    private val testDateTime = LocalDateTime.now()

    private val convocatoriaEntity = ConvocatoriaEntity(
        id = 1,
        fecha = testDate,
        descripcion = "Convocatoria para partido de liga",
        equipoId = 1,
        entrenadorId = 1,
        createdAt = testDateTime,
        updatedAt = testDateTime
    )

    @BeforeAll
    fun setUp() {
        jdbi = Jdbi.create("jdbc:sqlite:test.db")
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        // Crear las tablas necesarias
        jdbi.useHandle<Exception> { handle ->
            handle.execute("""
                CREATE TABLE IF NOT EXISTS Equipos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL
                )
            """)
            handle.execute("""
                CREATE TABLE IF NOT EXISTS Personal (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL
                )
            """)
            handle.execute("""
                CREATE TABLE IF NOT EXISTS Convocatorias (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha DATE NOT NULL,
                    descripcion TEXT NOT NULL,
                    equipo_id INTEGER NOT NULL,
                    entrenador_id INTEGER NOT NULL,
                    created_at TIMESTAMP NOT NULL,
                    updated_at TIMESTAMP NOT NULL,
                    FOREIGN KEY (equipo_id) REFERENCES Equipos(id),
                    FOREIGN KEY (entrenador_id) REFERENCES Personal(id)
                )
            """)

            // Insertar datos de prueba
            handle.execute("INSERT INTO Equipos (id, nombre) VALUES (1, 'Equipo Test 1'), (2, 'Equipo Test 2')")
            handle.execute("INSERT INTO Personal (id, nombre) VALUES (1, 'Entrenador 1'), (2, 'Entrenador 2')")
        }

        dao = provideConvocatoriaDao(jdbi)
    }

    @BeforeEach
    fun setUpEach() {
        // Limpiar datos antes de cada prueba
        dao.deleteAll()
    }

    @AfterAll
    fun tearDownAll() {
        jdbi.useHandle<Exception> { handle ->
            handle.execute("DROP TABLE IF EXISTS Convocatorias")
            handle.execute("DROP TABLE IF EXISTS Equipos")
            handle.execute("DROP TABLE IF EXISTS Personal")
        }
    }

    @Nested
    @DisplayName("Casos correctos")
    inner class CasosCorrectos {
        @Test
        @DisplayName("Insertar convocatoria")
        fun saveConvocatoria() {
            jdbi.useTransaction<Exception> { handle ->
                val id = dao.save(convocatoriaEntity)
                val result = dao.findById(id)

                assertNotNull(result, "La convocatoria no debería ser nula")
                assertEquals(convocatoriaEntity.fecha, result!!.fecha, "Las fechas deberían ser iguales")
                assertEquals(convocatoriaEntity.descripcion, result.descripcion, "Las descripciones deberían ser iguales")
                assertEquals(convocatoriaEntity.equipoId, result.equipoId, "Los IDs de equipo deberían ser iguales")
                assertEquals(convocatoriaEntity.entrenadorId, result.entrenadorId, "Los IDs de entrenador deberían ser iguales")
            }
        }


        @Test
        @DisplayName("Actualizar convocatoria")
        fun updateConvocatoria() {
            val id = dao.save(convocatoriaEntity)

            val updatedEntity = convocatoriaEntity.copy(
                id = id,
                descripcion = "Convocatoria actualizada",
                equipoId = 2,
                updatedAt = LocalDateTime.now()
            )

            val updateResult = dao.update(updatedEntity)
            val result = dao.findById(id)

            assertEquals(1, updateResult, "Debería haberse actualizado 1 registro")
            assertNotNull(result, "El resultado no debería ser nulo")
            assertEquals("Convocatoria actualizada", result!!.descripcion, "La descripción debería haberse actualizado")
            assertEquals(2, result.equipoId, "El ID de equipo debería haberse actualizado")
        }

        @Test
        @DisplayName("Eliminar convocatoria")
        fun deleteConvocatoria() {
            val id = dao.save(convocatoriaEntity)

            val deleteResult = dao.delete(id)
            val result = dao.findById(id)

            assertEquals(1, deleteResult, "Debería haberse eliminado 1 registro")
            assertNull(result, "El resultado debería ser nulo después de eliminar")
        }

        @Test
        @DisplayName("Obtener todas las convocatorias")
        fun findAll() {
            // Insertar varias convocatorias
            dao.save(convocatoriaEntity)
            dao.save(convocatoriaEntity.copy(id = 0, descripcion = "Convocatoria para partido de copa"))
            dao.save(convocatoriaEntity.copy(id = 0, descripcion = "Convocatoria para partido amistoso"))

            val result = dao.findAll()

            assertEquals(3, result.size, "Deberían haber 3 convocatorias")
            assertTrue(
                result.any { it.descripcion.contains("liga") },
                "Debería existir una convocatoria para partido de liga"
            )
            assertTrue(
                result.any { it.descripcion.contains("copa") },
                "Debería existir una convocatoria para partido de copa"
            )
            assertTrue(
                result.any { it.descripcion.contains("amistoso") },
                "Debería existir una convocatoria para partido amistoso"
            )
        }

        @Test
        @DisplayName("Buscar por equipo ID")
        fun findByEquipoId() {
            // Insertar varias convocatorias con diferentes equipos
            dao.save(convocatoriaEntity) // equipoId = 1
            dao.save(convocatoriaEntity.copy(id = 0, equipoId = 2))
            dao.save(convocatoriaEntity.copy(id = 0, equipoId = 2))

            val result = dao.findByEquipoId(2)

            assertEquals(2, result.size, "Deberían haber 2 convocatorias para el equipo 2")
        }

        @Test
        @DisplayName("Buscar por entrenador ID")
        fun findByEntrenadorId() {
            // Insertar varias convocatorias con diferentes entrenadores
            dao.save(convocatoriaEntity) // entrenadorId = 1
            dao.save(convocatoriaEntity.copy(id = 0, entrenadorId = 2))
            dao.save(convocatoriaEntity.copy(id = 0, entrenadorId = 3))

            val result = dao.findByEntrenadorId(1)

            assertEquals(1, result.size, "Debería haber 1 convocatoria para el entrenador 1")
        }

        @Test
        @DisplayName("Buscar por fecha")
        fun findByFecha() {
            // Insertar varias convocatorias con diferentes fechas
            val hoy = LocalDate.now()
            val ayer = hoy.minusDays(1)
            val manana = hoy.plusDays(1)

            dao.save(convocatoriaEntity) // fecha = hoy
            dao.save(convocatoriaEntity.copy(id = 0, fecha = ayer))
            dao.save(convocatoriaEntity.copy(id = 0, fecha = manana))

            val result = dao.findByFecha(hoy)

            assertEquals(1, result.size, "Debería haber 1 convocatoria para hoy")
        }

        @Test
        @DisplayName("Contar convocatorias")
        fun count() {
            // Insertar varias convocatorias
            dao.save(convocatoriaEntity)
            dao.save(convocatoriaEntity.copy(id = 0))
            dao.save(convocatoriaEntity.copy(id = 0))

            val result = dao.count()

            assertEquals(3, result, "Deberían haber 3 convocatorias en total")
        }

        @Test
        @DisplayName("Contar por equipo ID")
        fun countByEquipoId() {
            // Insertar varias convocatorias con diferentes equipos
            dao.save(convocatoriaEntity) // equipoId = 1
            dao.save(convocatoriaEntity.copy(id = 0, equipoId = 2))
            dao.save(convocatoriaEntity.copy(id = 0, equipoId = 2))

            val result = dao.countByEquipoId(2)

            assertEquals(2, result, "Deberían haber 2 convocatorias para el equipo 2")
        }

        @Test
        @DisplayName("Contar por entrenador ID")
        fun countByEntrenadorId() {
            // Insertar varias convocatorias con diferentes entrenadores
            dao.save(convocatoriaEntity) // entrenadorId = 1
            dao.save(convocatoriaEntity.copy(id = 0, entrenadorId = 2))
            dao.save(convocatoriaEntity.copy(id = 0, entrenadorId = 2))

            val result = dao.countByEntrenadorId(2)

            assertEquals(2, result, "Deberían haber 2 convocatorias para el entrenador 2")
        }
    }

    @Nested
    @DisplayName("Casos incorrectos")
    inner class CasosIncorrectos {
        @Test
        @DisplayName("Guardar convocatoria con equipo inexistente")
        fun saveWithInvalidEquipoId() {
            val invalidEntity = convocatoriaEntity.copy(equipoId = 999)
            assertThrows<Exception> {
                dao.save(invalidEntity)
            }
        }

        @Test
        @DisplayName("Guardar convocatoria con entrenador inexistente")
        fun saveWithInvalidEntrenadorId() {
            val invalidEntity = convocatoriaEntity.copy(entrenadorId = 999)
            assertThrows<Exception> {
                dao.save(invalidEntity)
            }
        }

        @Test
        @DisplayName("Guardar convocatoria con descripción vacía")
        fun saveWithEmptyDescripcion() {
            val invalidEntity = convocatoriaEntity.copy(descripcion = "")
            assertThrows<Exception> {
                dao.save(invalidEntity)
            }
        }

        @Test
        @DisplayName("Buscar por ID inexistente")
        fun findByIdInexistente() {
            val result = dao.findById(999)
            assertNull(result, "El resultado debería ser nulo para un ID inexistente")
        }

        @Test
        @DisplayName("Actualizar convocatoria inexistente")
        fun updateInexistente() {
            val result = dao.update(convocatoriaEntity.copy(id = 999))
            assertEquals(0, result, "No debería haberse actualizado ningún registro")
        }

        @Test
        @DisplayName("Eliminar convocatoria inexistente")
        fun deleteInexistente() {
            val result = dao.delete(999)
            assertEquals(0, result, "No debería haberse eliminado ningún registro")
        }

        @Test
        @DisplayName("Buscar por equipo ID inexistente")
        fun findByEquipoIdInexistente() {
            val result = dao.findByEquipoId(999)
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }

        @Test
        @DisplayName("Buscar por entrenador ID inexistente")
        fun findByEntrenadorIdInexistente() {
            val result = dao.findByEntrenadorId(999)
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }

        @Test
        @DisplayName("Buscar por fecha inexistente")
        fun findByFechaInexistente() {
            val fechaInexistente = LocalDate.of(1900, 1, 1)
            val result = dao.findByFecha(fechaInexistente)
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }
    }
}
