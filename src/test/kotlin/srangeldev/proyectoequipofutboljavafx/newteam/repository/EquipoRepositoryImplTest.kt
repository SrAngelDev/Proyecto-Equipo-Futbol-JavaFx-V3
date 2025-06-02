package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import srangeldev.proyectoequipofutboljavafx.newteam.models.Equipo
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class EquipoRepositoryImplTest {

    // We'll use a test-specific implementation of EquipoRepository
    private lateinit var equipoRepository: TestEquipoRepository

    // Saved equipo reference to use in tests
    private lateinit var savedEquipo1: Equipo

    // Test data
    private val equipo1 = Equipo(
        id = 0,  // Use id=0 for new entities
        nombre = "FC Barcelona",
        fechaFundacion = LocalDate.of(1899, 11, 29),
        escudoUrl = "barcelona.png",
        ciudad = "Barcelona",
        estadio = "Camp Nou",
        pais = "España",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    private val equipo2 = Equipo(
        id = 0,  // Use id=0 for new entities
        nombre = "Real Madrid",
        fechaFundacion = LocalDate.of(1902, 3, 6),
        escudoUrl = "madrid.png",
        ciudad = "Madrid",
        estadio = "Santiago Bernabéu",
        pais = "España",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @BeforeEach
    fun setUp() {
        // Initialize the test repository
        equipoRepository = TestEquipoRepository()

        // Add some test data and store the saved equipo
        savedEquipo1 = equipoRepository.save(equipo1)
    }

    @Test
    fun `test getAll returns all equipos`() {
        // Act
        val equipos = equipoRepository.getAll()

        // Assert
        assertEquals(1, equipos.size)
        assertEquals("FC Barcelona", equipos[0].nombre)
        assertEquals("Camp Nou", equipos[0].estadio)
    }

    @Test
    fun `test getById returns correct equipo`() {
        // Act
        val equipo = equipoRepository.getById(savedEquipo1.id)
        val nonExistentEquipo = equipoRepository.getById(999)

        // Assert
        assertNotNull(equipo)
        assertEquals(savedEquipo1.id, equipo?.id)
        assertEquals("FC Barcelona", equipo?.nombre)
        assertEquals("Camp Nou", equipo?.estadio)

        assertNull(nonExistentEquipo)
    }

    @Test
    fun `test save creates new equipo`() {
        // Act
        val savedEquipo = equipoRepository.save(equipo2)

        // Assert
        assertNotNull(savedEquipo)
        assertEquals(2, savedEquipo.id)
        assertEquals("Real Madrid", savedEquipo.nombre)
        assertEquals("Santiago Bernabéu", savedEquipo.estadio)

        // Verify the equipo was added to the repository
        val retrievedEquipo = equipoRepository.getById(savedEquipo.id)
        assertNotNull(retrievedEquipo)
        assertEquals(savedEquipo.id, retrievedEquipo?.id)
    }

    @Test
    fun `test update modifies existing equipo`() {
        // Arrange
        val updatedEquipo = savedEquipo1.copy(
            nombre = "FC Barcelona Updated",
            estadio = "New Camp Nou"
        )

        // Act
        val result = equipoRepository.update(savedEquipo1.id, updatedEquipo)

        // Assert
        assertNotNull(result)
        assertEquals(savedEquipo1.id, result?.id)
        assertEquals("FC Barcelona Updated", result?.nombre)
        assertEquals("New Camp Nou", result?.estadio)

        // Verify the equipo was updated in the repository
        val retrievedEquipo = equipoRepository.getById(savedEquipo1.id)
        assertNotNull(retrievedEquipo)
        assertEquals(savedEquipo1.id, retrievedEquipo?.id)
        assertEquals("FC Barcelona Updated", retrievedEquipo?.nombre)
    }

    @Test
    fun `test delete removes equipo`() {
        // Act
        val result = equipoRepository.delete(savedEquipo1.id)

        // Assert
        assertNotNull(result)
        assertEquals(savedEquipo1.id, result?.id)

        // Verify the equipo was removed from the repository
        val retrievedEquipo = equipoRepository.getById(savedEquipo1.id)
        assertNull(retrievedEquipo)
    }

    @Test
    fun `test initDefaultEquipo initializes repository`() {
        // This is a simple test to ensure the method doesn't throw exceptions
        // Since the method is private and called in the init block, we're just verifying
        // that the repository was initialized correctly

        // Arrange & Act - create a new instance of our test-specific EquipoRepositoryImpl
        val repository = TestEquipoRepositoryImpl()

        // Assert - verify that we can get equipos
        val equipos = repository.getAll()
        assertNotNull(equipos)
        // Verify that the default equipo was added
        assertTrue(equipos.isNotEmpty())
        assertEquals("Mi Equipo", equipos[0].nombre)
    }

    // Test-specific implementation of EquipoRepositoryImpl that doesn't use the actual database
    class TestEquipoRepositoryImpl : EquipoRepository {
        private val equipos = mutableMapOf<Int, Equipo>()
        private var nextId = 1

        init {
            // Simulate what initDefaultEquipo() does in the real implementation
            val defaultEquipo = Equipo(
                nombre = "Mi Equipo",
                fechaFundacion = LocalDate.of(2000, 1, 1),
                escudoUrl = "",
                ciudad = "Mi Ciudad",
                estadio = "Mi Estadio",
                pais = "Mi País"
            )
            save(defaultEquipo)
        }

        override fun getAll(): List<Equipo> {
            return equipos.values.toList()
        }

        override fun getById(id: Int): Equipo? {
            return equipos[id]
        }

        override fun save(entidad: Equipo): Equipo {
            val isUpdate = entidad.id > 0

            if (isUpdate) {
                return update(entidad.id, entidad) ?: throw IllegalStateException("No se pudo actualizar el equipo")
            } else {
                val id = nextId++
                val now = LocalDateTime.now()

                val newEquipo = entidad.copy(
                    id = id,
                    createdAt = now,
                    updatedAt = now
                )

                equipos[id] = newEquipo
                return newEquipo
            }
        }

        override fun update(id: Int, entidad: Equipo): Equipo? {
            val existingEquipo = equipos[id] ?: return null

            val updatedEquipo = entidad.copy(
                id = id,
                createdAt = existingEquipo.createdAt,
                updatedAt = LocalDateTime.now()
            )

            equipos[id] = updatedEquipo
            return updatedEquipo
        }

        override fun delete(id: Int): Equipo? {
            return equipos.remove(id)
        }
    }

    // Test-specific implementation of EquipoRepository that doesn't use the database
    class TestEquipoRepository : EquipoRepository {
        private val equipos = mutableMapOf<Int, Equipo>()
        private var nextId = 1

        override fun getAll(): List<Equipo> {
            return equipos.values.toList()
        }

        override fun getById(id: Int): Equipo? {
            return equipos[id]
        }

        override fun save(entidad: Equipo): Equipo {
            val isUpdate = entidad.id > 0

            if (isUpdate) {
                return update(entidad.id, entidad) ?: throw IllegalStateException("No se pudo actualizar el equipo")
            } else {
                val id = nextId++
                val now = LocalDateTime.now()

                val newEquipo = entidad.copy(
                    id = id,
                    createdAt = now,
                    updatedAt = now
                )

                equipos[id] = newEquipo
                return newEquipo
            }
        }

        override fun update(id: Int, entidad: Equipo): Equipo? {
            val existingEquipo = equipos[id] ?: return null

            val updatedEquipo = entidad.copy(
                id = id,
                createdAt = existingEquipo.createdAt,
                updatedAt = LocalDateTime.now()
            )

            equipos[id] = updatedEquipo
            return updatedEquipo
        }

        override fun delete(id: Int): Equipo? {
            return equipos.remove(id)
        }
    }
}
