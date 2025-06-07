package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import srangeldev.proyectoequipofutboljavafx.newteam.models.Equipo
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EquipoDaoTest {
    private lateinit var jdbi: Jdbi
    private lateinit var dao: EquipoDao

    val equipo = Equipo(
        id = 1,
        nombre = "Real Madrid",
        fechaFundacion = LocalDate.parse("1902-03-06"),
        escudoUrl = "real_madrid.png",
        ciudad = "Madrid",
        estadio = "Santiago Bernabéu",
        pais = "España",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @BeforeAll
    fun setUp() {
        // Configurar JDBI con base de datos en fichero SQLite
        jdbi = Jdbi.create("jdbc:sqlite:test.db")
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        // Crear tabla Equipos
        jdbi.useHandle<Exception> { handle ->
            handle.execute("""
                CREATE TABLE IF NOT EXISTS Equipos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    fecha_fundacion DATE NOT NULL,
                    escudo_url TEXT DEFAULT '',
                    ciudad TEXT NOT NULL,
                    estadio TEXT NOT NULL,
                    pais TEXT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """)
        }

        // Inicializar DAO
        dao = jdbi.onDemand(EquipoDao::class.java)
    }

    @AfterEach
    fun tearDown() {
        // Limpiar datos después de cada test
        jdbi.useHandle<Exception> { handle ->
            handle.execute("DELETE FROM Equipos")
        }
    }

    @Nested
    @DisplayName("Casos correctos")
    inner class CasosCorrectos {
        @Test
        @DisplayName("Insertar equipo")
        fun saveEquipo() {
            val id = dao.save(equipo)
            val result = dao.findById(id)
            
            assertNotNull(result, "El equipo no debería ser nulo")
            assertEquals(equipo.nombre, result!!.nombre, "Los nombres deberían ser iguales")
            assertEquals(equipo.ciudad, result.ciudad, "Las ciudades deberían ser iguales")
            assertEquals(equipo.estadio, result.estadio, "Los estadios deberían ser iguales")
        }

        @Test
        @DisplayName("Actualizar equipo")
        fun updateEquipo() {
            val id = dao.save(equipo)
            
            val updatedEquipo = equipo.copy(
                id = id,
                nombre = "Real Madrid CF",
                estadio = "Nuevo Santiago Bernabéu",
                updatedAt = LocalDateTime.now()
            )
            
            val updateResult = dao.update(updatedEquipo)
            val result = dao.findById(id)
            
            assertEquals(1, updateResult, "Debería haberse actualizado 1 registro")
            assertNotNull(result, "El resultado no debería ser nulo")
            assertEquals("Real Madrid CF", result!!.nombre, "El nombre debería haberse actualizado")
            assertEquals("Nuevo Santiago Bernabéu", result.estadio, "El estadio debería haberse actualizado")
        }

        @Test
        @DisplayName("Eliminar equipo")
        fun deleteEquipo() {
            val id = dao.save(equipo)
            
            val deleteResult = dao.delete(id)
            val result = dao.findById(id)
            
            assertEquals(1, deleteResult, "Debería haberse eliminado 1 registro")
            assertNull(result, "El resultado debería ser nulo después de eliminar")
        }

        @Test
        @DisplayName("Obtener todos los equipos")
        fun findAll() {
            // Insertar varios equipos
            dao.save(equipo)
            dao.save(equipo.copy(id = 0, nombre = "FC Barcelona", ciudad = "Barcelona", estadio = "Camp Nou"))
            dao.save(equipo.copy(id = 0, nombre = "Atlético de Madrid", estadio = "Metropolitano"))
            
            val result = dao.findAll()
            
            assertEquals(3, result.size, "Deberían haber 3 equipos")
            assertTrue(result.any { it.nombre == "Real Madrid" }, "Debería existir el Real Madrid")
            assertTrue(result.any { it.nombre == "FC Barcelona" }, "Debería existir el FC Barcelona")
            assertTrue(result.any { it.nombre == "Atlético de Madrid" }, "Debería existir el Atlético de Madrid")
        }

        @Test
        @DisplayName("Buscar por nombre")
        fun findByNombre() {
            // Insertar varios equipos
            dao.save(equipo)
            dao.save(equipo.copy(id = 0, nombre = "FC Barcelona", ciudad = "Barcelona", estadio = "Camp Nou"))
            
            val result = dao.findByNombre("FC Barcelona")
            
            assertNotNull(result, "El resultado no debería ser nulo")
            assertEquals("FC Barcelona", result!!.nombre, "El nombre debería ser FC Barcelona")
            assertEquals("Barcelona", result.ciudad, "La ciudad debería ser Barcelona")
        }

        @Test
        @DisplayName("Buscar por país")
        fun findByPais() {
            // Insertar varios equipos de diferentes países
            dao.save(equipo) // España
            dao.save(equipo.copy(id = 0, nombre = "Manchester United", ciudad = "Manchester", estadio = "Old Trafford", pais = "Inglaterra"))
            dao.save(equipo.copy(id = 0, nombre = "Liverpool FC", ciudad = "Liverpool", estadio = "Anfield", pais = "Inglaterra"))
            
            val result = dao.findByPais("Inglaterra")
            
            assertEquals(2, result.size, "Deberían haber 2 equipos ingleses")
            assertTrue(result.any { it.nombre == "Manchester United" }, "Debería existir el Manchester United")
            assertTrue(result.any { it.nombre == "Liverpool FC" }, "Debería existir el Liverpool FC")
        }

        @Test
        @DisplayName("Buscar por ciudad")
        fun findByCiudad() {
            // Insertar varios equipos de diferentes ciudades
            dao.save(equipo) // Madrid
            dao.save(equipo.copy(id = 0, nombre = "Atlético de Madrid", estadio = "Metropolitano", ciudad = "Madrid"))
            dao.save(equipo.copy(id = 0, nombre = "FC Barcelona", ciudad = "Barcelona", estadio = "Camp Nou"))
            
            val result = dao.findByCiudad("Madrid")
            
            assertEquals(2, result.size, "Deberían haber 2 equipos de Madrid")
            assertTrue(result.any { it.nombre == "Real Madrid" }, "Debería existir el Real Madrid")
            assertTrue(result.any { it.nombre == "Atlético de Madrid" }, "Debería existir el Atlético de Madrid")
        }

        @Test
        @DisplayName("Contar equipos")
        fun count() {
            // Insertar varios equipos
            dao.save(equipo)
            dao.save(equipo.copy(id = 0, nombre = "FC Barcelona"))
            dao.save(equipo.copy(id = 0, nombre = "Atlético de Madrid"))
            
            val result = dao.count()
            
            assertEquals(3, result, "Deberían haber 3 equipos en total")
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
        @DisplayName("Actualizar equipo inexistente")
        fun updateInexistente() {
            val result = dao.update(equipo.copy(id = 999))
            assertEquals(0, result, "No debería haberse actualizado ningún registro")
        }

        @Test
        @DisplayName("Eliminar equipo inexistente")
        fun deleteInexistente() {
            val result = dao.delete(999)
            assertEquals(0, result, "No debería haberse eliminado ningún registro")
        }

        @Test
        @DisplayName("Buscar por nombre inexistente")
        fun findByNombreInexistente() {
            val result = dao.findByNombre("Equipo Inexistente")
            assertNull(result, "El resultado debería ser nulo para un nombre inexistente")
        }

        @Test
        @DisplayName("Buscar por país inexistente")
        fun findByPaisInexistente() {
            val result = dao.findByPais("País Inexistente")
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }

        @Test
        @DisplayName("Buscar por ciudad inexistente")
        fun findByCiudadInexistente() {
            val result = dao.findByCiudad("Ciudad Inexistente")
            assertTrue(result.isEmpty(), "La lista debería estar vacía")
        }
    }
}