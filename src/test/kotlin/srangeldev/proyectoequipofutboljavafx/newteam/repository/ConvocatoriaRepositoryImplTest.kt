package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.sql.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class ConvocatoriaRepositoryImplTest {

    @Mock
    private lateinit var personalRepository: PersonalRepository

    @Mock
    private lateinit var dbManager: DataBaseManager

    @Mock
    private lateinit var connection: Connection

    @Mock
    private lateinit var statement: Statement

    @Mock
    private lateinit var preparedStatement: PreparedStatement

    @Mock
    private lateinit var resultSet: ResultSet

    @InjectMocks
    private lateinit var repository: ConvocatoriaRepositoryImpl

    private lateinit var convocatoria: Convocatoria

    @BeforeEach
    fun setUp() {
        convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Test convocatoria",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = listOf(101, 102),
            titulares = listOf(101),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        whenever(dbManager.connection).thenReturn(connection)
    }

    @Test
    fun getAll() {
        // Configurar ResultSet para convocatorias
        whenever(connection.createStatement()).thenReturn(statement)
        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true, true, false)
        mockConvocatoriaResultSet(resultSet, 1)
        mockConvocatoriaResultSet(resultSet, 2)

        // Configurar ResultSet para jugadores
        val jugadoresResultSet = mock<ResultSet>()
        whenever(connection.createStatement(anyString())).thenReturn(statement)
        whenever(statement.executeQuery(anyString())).thenReturn(jugadoresResultSet)
        whenever(jugadoresResultSet.next()).thenReturn(true, true, false)
        whenever(jugadoresResultSet.getInt("convocatoria_id")).thenReturn(1, 1, 2)
        whenever(jugadoresResultSet.getInt("jugador_id")).thenReturn(101, 102, 103)
        whenever(jugadoresResultSet.getInt("es_titular")).thenReturn(1, 0, 1)

        val result = repository.getAll()
        assertEquals(2, result.size)
        assertEquals(2, result[1].jugadores.size)
    }

    @Test
    fun getById() {
        // Configurar ResultSet para convocatoria
        whenever(connection.createStatement()).thenReturn(statement)
        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true, false)
        mockConvocatoriaResultSet(resultSet, 1)

        // Configurar ResultSet para jugadores
        val jugadoresResultSet = mock<ResultSet>()
        whenever(connection.prepareStatement(anyString())).thenReturn(preparedStatement)
        whenever(preparedStatement.executeQuery()).thenReturn(jugadoresResultSet)
        whenever(jugadoresResultSet.next()).thenReturn(true, false)
        whenever(jugadoresResultSet.getInt("jugador_id")).thenReturn(101)
        whenever(jugadoresResultSet.getInt("es_titular")).thenReturn(1)

        val result = repository.getById(1)
        assertNotNull(result)
        assertEquals(1, result?.id)
        assertEquals(1, result?.jugadores?.size)
    }

    @Test
    fun save_newConvocatoria() {
        // Configurar insert
        whenever(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
            .thenReturn(preparedStatement)

        // Configurar generated keys
        val generatedKeys = mock<ResultSet>()
        whenever(preparedStatement.generatedKeys).thenReturn(generatedKeys)
        whenever(generatedKeys.next()).thenReturn(true)
        whenever(generatedKeys.getInt(1)).thenReturn(100)

        // Configurar getById
        whenever(connection.createStatement()).thenReturn(statement)
        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true)
        mockConvocatoriaResultSet(resultSet, 100)

        // Ejecutar
        val newConvocatoria = convocatoria.copy(id = 0)
        val result = repository.save(newConvocatoria)

        assertEquals(100, result.id)
        verify(preparedStatement, times(1)).executeUpdate()
    }

    @Test
    fun update() {
        // Mockear convocatoria existente
        whenever(connection.createStatement()).thenReturn(statement)
        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true)
        mockConvocatoriaResultSet(resultSet, 1)

        // Ejecutar update
        val updated = repository.update(1, convocatoria)

        assertNotNull(updated)
        verify(preparedStatement, times(1)).executeUpdate()
        verify(preparedStatement, times(1)).executeBatch()
    }

    @Test
    fun delete() {
        // Mockear convocatoria existente
        whenever(connection.createStatement()).thenReturn(statement)
        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true)
        mockConvocatoriaResultSet(resultSet, 1)

        // Ejecutar delete
        val deleted = repository.delete(1)

        assertNotNull(deleted)
        verify(preparedStatement, times(2)).executeUpdate()
    }

    @Test
    fun getJugadoresConvocados() {
        val jugador = Jugador(101, "Nombre", "Apellido", "email@test.com", "123456789",
            LocalDate.now(), "Delantero", 1, 1, LocalDateTime.now(), LocalDateTime.now())

        whenever(personalRepository.getById(101)).thenReturn(jugador)

        // Mockear jugadores convocados
        whenever(connection.prepareStatement(anyString())).thenReturn(preparedStatement)
        whenever(preparedStatement.executeQuery()).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true, false)
        whenever(resultSet.getInt("jugador_id")).thenReturn(101)

        val result = repository.getJugadoresConvocados(1)

        assertEquals(1, result.size)
        assertEquals("Nombre", result[0].nombre)
    }

    @Test
    fun getJugadoresTitulares() {
        val jugador = Jugador(101, "Titular", "Apellido", "email@test.com", "123456789",
            LocalDate.now(), "Delantero", 1, 1, LocalDateTime.now(), LocalDateTime.now())

        whenever(personalRepository.getById(101)).thenReturn(jugador)

        // Mockear titulares
        whenever(connection.prepareStatement(anyString())).thenReturn(preparedStatement)
        whenever(preparedStatement.executeQuery()).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true, false)
        whenever(resultSet.getInt("jugador_id")).thenReturn(101)

        val result = repository.getJugadoresTitulares(1)

        assertEquals(1, result.size)
        assertEquals("Titular", result[0].nombre)
    }

    @Test
    fun getJugadoresSuplentes() {
        val jugador = Jugador(102, "Suplente", "Apellido", "email@test.com", "123456789",
            LocalDate.now(), "Defensa", 1, 1, LocalDateTime.now(), LocalDateTime.now())

        whenever(personalRepository.getById(102)).thenReturn(jugador)

        // Mockear suplentes
        whenever(connection.prepareStatement(anyString())).thenReturn(preparedStatement)
        whenever(preparedStatement.executeQuery()).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true, false)
        whenever(resultSet.getInt("jugador_id")).thenReturn(102)

        val result = repository.getJugadoresSuplentes(1)

        assertEquals(1, result.size)
        assertEquals("Suplente", result[0].nombre)
    }

    @Test
    fun getJugadoresNoConvocados() {
        val convocado = Jugador(101, "Convocado", "Apellido", "email@test.com", "123456789",
            LocalDate.now(), "Delantero", 1, 1, LocalDateTime.now(), LocalDateTime.now())

        val noConvocado = Jugador(102, "Libre", "Apellido", "email2@test.com", "987654321",
            LocalDate.now(), "Portero", 1, 1, LocalDateTime.now(), LocalDateTime.now())

        // Mockear todos los jugadores
        whenever(personalRepository.getAll()).thenReturn(listOf(convocado, noConvocado))

        // Mockear jugadores convocados
        whenever(connection.prepareStatement(anyString())).thenReturn(preparedStatement)
        whenever(preparedStatement.executeQuery()).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true, false)
        whenever(resultSet.getInt("jugador_id")).thenReturn(101)

        val result = repository.getJugadoresNoConvocados(1)

        assertEquals(1, result.size)
        assertEquals("Libre", result[0].nombre)
    }

    @Test
    fun getByEquipoId() {
        // Configurar ResultSet para convocatorias
        whenever(connection.prepareStatement(anyString())).thenReturn(preparedStatement)
        whenever(preparedStatement.executeQuery()).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true, true, false)
        mockConvocatoriaResultSet(resultSet, 1)
        mockConvocatoriaResultSet(resultSet, 2)

        // Configurar ResultSet para jugadores
        val jugadoresResultSet = mock<ResultSet>()
        whenever(connection.createStatement(anyString())).thenReturn(statement)
        whenever(statement.executeQuery(anyString())).thenReturn(jugadoresResultSet)
        whenever(jugadoresResultSet.next()).thenReturn(true, false)
        whenever(jugadoresResultSet.getInt("convocatoria_id")).thenReturn(1)
        whenever(jugadoresResultSet.getInt("jugador_id")).thenReturn(101)
        whenever(jugadoresResultSet.getInt("es_titular")).thenReturn(1)

        val result = repository.getByEquipoId(1)
        assertEquals(2, result.size)
        assertEquals(1, result[0].jugadores.size)
    }

    @Test
    fun validarConvocatoria_valida() {
        val jugador1 = Jugador(101, "Jugador1", "Apellido", "email@test.com", "123",
            LocalDate.now(), "Delantero", 1, 1, LocalDateTime.now(), LocalDateTime.now())

        val jugador2 = Jugador(102, "Jugador2", "Apellido", "email2@test.com", "456",
            LocalDate.now(), "Defensa", 1, 1, LocalDateTime.now(), LocalDateTime.now())

        whenever(personalRepository.getById(101)).thenReturn(jugador1)
        whenever(personalRepository.getById(102)).thenReturn(jugador2)

        val convocatoriaValida = convocatoria.copy(
            fecha = LocalDate.now().plusDays(1),
            jugadores = listOf(101, 102),
            titulares = listOf(101)
        )

        assertTrue(repository.validarConvocatoria(convocatoriaValida))
    }

    @Test
    fun validarConvocatoria_fechaInvalida() {
        val convocatoriaInvalida = convocatoria.copy(
            fecha = LocalDate.now().minusDays(1)
        )
        assertFalse(repository.validarConvocatoria(convocatoriaInvalida))
    }

    @Test
    fun validarConvocatoria_sinJugadores() {
        val convocatoriaInvalida = convocatoria.copy(
            jugadores = emptyList(),
            titulares = emptyList()
        )
        assertFalse(repository.validarConvocatoria(convocatoriaInvalida))
    }

    @Test
    fun validarConvocatoria_sinTitulares() {
        val convocatoriaInvalida = convocatoria.copy(
            titulares = emptyList()
        )
        assertFalse(repository.validarConvocatoria(convocatoriaInvalida))
    }

    @Test
    fun getById_notFound() {
        whenever(connection.createStatement()).thenReturn(statement)
        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(false)

        val result = repository.getById(999)
        assertNull(result)
    }

    @Test
    fun delete_notFound() {
        whenever(connection.createStatement()).thenReturn(statement)
        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(false)

        val result = repository.delete(999)
        assertNull(result)
    }

    @Test
    fun update_notFound() {
        whenever(connection.createStatement()).thenReturn(statement)
        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(false)

        val result = repository.update(999, convocatoria)
        assertNull(result)
    }

    @Test
    fun save_exceptionHandling() {
        whenever(connection.prepareStatement(anyString(), anyInt()))
            .thenThrow(SQLException("Test exception"))

        assertThrows(IllegalStateException::class.java) {
            repository.save(convocatoria.copy(id = 0))
        }
    }

    private fun mockConvocatoriaResultSet(rs: ResultSet, id: Int) {
        whenever(rs.getInt("id")).thenReturn(id)
        whenever(rs.getDate("fecha")).thenReturn(java.sql.Date.valueOf(LocalDate.now()))
        whenever(rs.getString("descripcion")).thenReturn("Descripción $id")
        whenever(rs.getInt("equipo_id")).thenReturn(1)
        whenever(rs.getInt("entrenador_id")).thenReturn(1)
        whenever(rs.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()))
        whenever(rs.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()))
    }
}