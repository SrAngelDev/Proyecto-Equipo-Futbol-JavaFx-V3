package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonalDaoTest {
    private lateinit var jdbi: Jdbi
    private lateinit var dao: PersonalDao

    val personalEntity = PersonalEntity(
        id = 1,
        nombre = "Test",
        apellidos = "User",
        fechaNacimiento = LocalDate.parse("2000-01-01"),
        fechaIncorporacion = LocalDate.parse("2020-01-01"),
        salario = 3000.0,
        paisOrigen = "España",
        tipo = "ENTRENADOR",
        imagenUrl = "test.jpg",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @BeforeAll
    fun setUp() {
        // Configurar JDBI con base de datos en fichero SQLite
        jdbi = Jdbi.create("jdbc:sqlite:test.db")
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        // Crear tablas
        jdbi.useHandle<Exception> { handle ->
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
        }

        // Inicializar DAO
        dao = jdbi.onDemand(PersonalDao::class.java)
    }

    @AfterEach
    fun tearDown() {
        // Limpiar datos después de cada test
        jdbi.useHandle<Exception> { handle ->
            handle.execute("DELETE FROM Personal")
        }
    }

    @Nested
    @DisplayName("Casos correctos")
    inner class CasosCorrectos {
        @Test
        @DisplayName("Insertar personal")
        fun savePersonal() {
            val id = dao.save(personalEntity)
            val result = dao.findById(id)

            assertNotNull(result, "El resultado no debería ser nulo")
            assertEquals(personalEntity.nombre, result!!.nombre, "Los nombres deberían ser iguales")
            assertEquals(personalEntity.apellidos, result.apellidos, "Los apellidos deberían ser iguales")
            assertEquals(personalEntity.tipo, result.tipo, "Los tipos deberían ser iguales")
        }

        @Test
        @DisplayName("Actualizar personal")
        fun updatePersonal() {
            val id = dao.save(personalEntity)
            val updatedEntity = personalEntity.copy(
                nombre = "Updated",
                salario = 4000.0,
                updatedAt = LocalDateTime.now()
            )

            val updateResult = dao.update(updatedEntity)
            val result = dao.findById(id)

            assertEquals(1, updateResult, "Debería haberse actualizado 1 registro")
            assertNotNull(result, "El resultado no debería ser nulo")
            assertEquals("Updated", result!!.nombre, "El nombre debería haberse actualizado")
            assertEquals(4000.0, result.salario, "El salario debería haberse actualizado")
        }

        @Test
        @DisplayName("Eliminar personal")
        fun deletePersonal() {
            val id = dao.save(personalEntity)
            val deleteResult = dao.delete(id)
            val result = dao.findById(id)

            assertEquals(1, deleteResult, "Debería haberse eliminado 1 registro")
            assertNull(result, "El resultado debería ser nulo después de eliminar")
        }

        @Test
        @DisplayName("Obtener todos los registros")
        fun findAll() {
            dao.save(personalEntity)
            dao.save(personalEntity.copy(id = 0, nombre = "Test2", tipo = "JUGADOR"))
            dao.save(personalEntity.copy(id = 0, nombre = "Test3", salario = 5000.0))

            val result = dao.findAll()

            assertEquals(3, result.size, "Deberían haber 3 registros")
            assertTrue(result.any { it.nombre == "Test" }, "Debería existir un registro con nombre Test")
            assertTrue(result.any { it.nombre == "Test2" }, "Debería existir un registro con nombre Test2")
            assertTrue(result.any { it.nombre == "Test3" }, "Debería existir un registro con nombre Test3")
            assertTrue(result.any { it.tipo == "JUGADOR" }, "Debería existir un registro de tipo JUGADOR")
        }

        @Test
        @DisplayName("Buscar por tipo")
        fun findByTipo() {
            dao.save(personalEntity) // ENTRENADOR
            dao.save(personalEntity.copy(id = 0, nombre = "Jugador1", tipo = "JUGADOR"))
            dao.save(personalEntity.copy(id = 0, nombre = "Jugador2", tipo = "JUGADOR"))

            val entrenadores = dao.findByTipo("ENTRENADOR")
            val jugadores = dao.findByTipo("JUGADOR")

            assertEquals(1, entrenadores.size, "Debería haber 1 entrenador")
            assertEquals(2, jugadores.size, "Deberían haber 2 jugadores")
            assertEquals("Test", entrenadores[0].nombre, "El nombre del entrenador debería ser Test")
        }

        @Test
        @DisplayName("Buscar por nombre")
        fun findByNombre() {
            dao.save(personalEntity)
            dao.save(personalEntity.copy(id = 0, nombre = "TestBusqueda"))

            val result = dao.findByNombre("%Test%")

            assertEquals(2, result.size, "Deberían haber 2 registros que contienen 'Test'")
        }

        @Test
        @DisplayName("Buscar por país de origen")
        fun findByPaisOrigen() {
            dao.save(personalEntity) // España
            dao.save(personalEntity.copy(id = 0, paisOrigen = "Portugal"))

            val result = dao.findByPaisOrigen("España")

            assertEquals(1, result.size, "Debería haber 1 registro de España")
            assertEquals("Test", result[0].nombre, "El nombre debería ser Test")
        }

        @Test
        @DisplayName("Contar registros")
        fun count() {
            dao.save(personalEntity)
            dao.save(personalEntity.copy(id = 0))
            dao.save(personalEntity.copy(id = 0))

            val result = dao.count()

            assertEquals(3, result, "Deberían haber 3 registros en total")
        }

        @Test
        @DisplayName("Contar por tipo")
        fun countByTipo() {
            dao.save(personalEntity) // ENTRENADOR
            dao.save(personalEntity.copy(id = 0, tipo = "JUGADOR"))
            dao.save(personalEntity.copy(id = 0, tipo = "JUGADOR"))

            val entrenadores = dao.countByTipo("ENTRENADOR")
            val jugadores = dao.countByTipo("JUGADOR")

            assertEquals(1, entrenadores, "Debería haber 1 entrenador")
            assertEquals(2, jugadores, "Deberían haber 2 jugadores")
        }

        @Test
        @DisplayName("Buscar por fecha de incorporación desde")
        fun findByFechaIncorporacionDesde() {
            dao.save(personalEntity) // 2020-01-01
            dao.save(personalEntity.copy(id = 0, fechaIncorporacion = LocalDate.parse("2021-01-01")))
            dao.save(personalEntity.copy(id = 0, fechaIncorporacion = LocalDate.parse("2022-01-01")))

            val result = dao.findByFechaIncorporacionDesde(LocalDate.parse("2021-01-01"))

            assertEquals(2, result.size, "Deberían haber 2 registros con fecha de incorporación desde 2021-01-01")
            assertTrue(result.all { it.fechaIncorporacion >= LocalDate.parse("2021-01-01") }, 
                "Todos los registros deberían tener fecha de incorporación mayor o igual a 2021-01-01")
        }

        @Test
        @DisplayName("Buscar por salario mayor que")
        fun findBySalarioMayorQue() {
            dao.save(personalEntity) // 3000.0
            dao.save(personalEntity.copy(id = 0, salario = 4000.0))
            dao.save(personalEntity.copy(id = 0, salario = 2000.0))

            val result = dao.findBySalarioMayorQue(3500.0)

            assertEquals(1, result.size, "Debería haber 1 registro con salario mayor que 3500.0")
            assertTrue(result.all { it.salario > 3500.0 }, 
                "Todos los registros deberían tener salario mayor que 3500.0")
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
        @DisplayName("Actualizar registro inexistente")
        fun updateInexistente() {
            val result = dao.update(personalEntity.copy(id = 999))
            assertEquals(0, result, "No debería haberse actualizado ningún registro")
        }

        @Test
        @DisplayName("Eliminar registro inexistente")
        fun deleteInexistente() {
            val result = dao.delete(999)
            assertEquals(0, result, "No debería haberse eliminado ningún registro")
        }

        @Test
        @DisplayName("Buscar por tipo inexistente")
        fun findByTipoInexistente() {
            val result = dao.findByTipo("TIPO_INEXISTENTE")
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }

        @Test
        @DisplayName("Buscar por nombre inexistente")
        fun findByNombreInexistente() {
            val result = dao.findByNombre("%Inexistente%")
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }

        @Test
        @DisplayName("Buscar por país inexistente")
        fun findByPaisInexistente() {
            val result = dao.findByPaisOrigen("PaísInexistente")
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }

        @Test
        @DisplayName("Buscar por fecha de incorporación sin resultados")
        fun findByFechaIncorporacionSinResultados() {
            dao.save(personalEntity.copy(fechaIncorporacion = LocalDate.parse("2020-01-01")))
            dao.save(personalEntity.copy(id = 0, fechaIncorporacion = LocalDate.parse("2021-01-01")))

            val result = dao.findByFechaIncorporacionDesde(LocalDate.parse("2023-01-01"))

            assertTrue(result.isEmpty(), "La lista debería estar vacía para una fecha futura")
        }

        @Test
        @DisplayName("Buscar por salario mayor que sin resultados")
        fun findBySalarioMayorQueSinResultados() {
            dao.save(personalEntity.copy(salario = 3000.0))
            dao.save(personalEntity.copy(id = 0, salario = 4000.0))

            val result = dao.findBySalarioMayorQue(5000.0)

            assertTrue(result.isEmpty(), "La lista debería estar vacía para un salario muy alto")
        }
    }
}
