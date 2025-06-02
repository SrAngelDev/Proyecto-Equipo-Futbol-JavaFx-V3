package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.times
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.sql.*
import java.time.LocalDate
import java.time.LocalDateTime

class ConvocatoriaRepositoryImplTest {

    private val personalRepository = mock<PersonalRepository>()

    private fun configureMockDatabase(resultSetConfig: ResultSet.() -> Unit): Connection {
        val resultSet = mock<ResultSet>()
        resultSetConfig(resultSet)

        val statement = mock<Statement>()
        `when`(statement.executeQuery(any())).thenReturn(resultSet)

        val connection = mock<Connection>()
        `when`(connection.createStatement()).thenReturn(statement)

        val dbManager = mock<DataBaseManager>()
        `when`(dbManager.connection).thenReturn(connection)
        DataBaseManager.instance = dbManager

        return connection
    }

    @Test
    fun `getAll returns empty list when there are no convocatorias in the database`() {
        configureMockDatabase {
            `when`(next()).thenReturn(false)
        }

        val repository = ConvocatoriaRepositoryImpl(personalRepository)
        val convocatorias = repository.getAll()

        assertTrue(convocatorias.isEmpty())
    }

    @Test
    fun `getById returns null when convocatoria does not exist`() {
        // Configure mock for the main query
        val connection = configureMockDatabase {
            `when`(next()).thenReturn(false)
        }

        // Configure mock for prepared statements
        val preparedStatement = mock<PreparedStatement>()
        `when`(connection.prepareStatement(any())).thenReturn(preparedStatement)
        `when`(preparedStatement.executeQuery()).thenReturn(mock())

        val repository = ConvocatoriaRepositoryImpl(personalRepository)
        val convocatoria = repository.getById(999)

        assertNull(convocatoria)
    }

    @Test
    fun `update returns null when convocatoria does not exist`() {
        // Create a convocatoria
        val convocatoria = Convocatoria(
            id = 999,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Non-existent Convocatoria",
            equipoId = 101,
            entrenadorId = 201,
            jugadores = listOf(301, 302),
            titulares = listOf(301)
        )

        // Configure mocks
        val connection = mock<Connection>()
        val getByIdPreparedStatement = mock<PreparedStatement>()
        val getByIdResultSet = mock<ResultSet>()

        // Mock database manager
        val dbManager = mock<DataBaseManager>()
        `when`(dbManager.connection).thenReturn(connection)
        DataBaseManager.instance = dbManager

        // Mock getById call to verify existence (returns false = not found)
        `when`(connection.prepareStatement(contains("SELECT * FROM Convocatorias WHERE id = ?")))
            .thenReturn(getByIdPreparedStatement)
        `when`(getByIdPreparedStatement.executeQuery()).thenReturn(getByIdResultSet)
        `when`(getByIdResultSet.next()).thenReturn(false)

        // Execute the test
        val repository = ConvocatoriaRepositoryImpl(personalRepository)
        val result = repository.update(999, convocatoria)

        // Verify
        assertNull(result)
    }

    @Test
    fun `delete returns null when convocatoria does not exist`() {
        // Configure mocks
        val connection = mock<Connection>()
        val getByIdPreparedStatement = mock<PreparedStatement>()
        val getByIdResultSet = mock<ResultSet>()

        // Mock database manager
        val dbManager = mock<DataBaseManager>()
        `when`(dbManager.connection).thenReturn(connection)
        DataBaseManager.instance = dbManager

        // Mock getById call to verify existence (returns false = not found)
        `when`(connection.prepareStatement(contains("SELECT * FROM Convocatorias WHERE id = ?")))
            .thenReturn(getByIdPreparedStatement)
        `when`(getByIdPreparedStatement.executeQuery()).thenReturn(getByIdResultSet)
        `when`(getByIdResultSet.next()).thenReturn(false)

        // Execute the test
        val repository = ConvocatoriaRepositoryImpl(personalRepository)
        val result = repository.delete(999)

        // Verify
        assertNull(result)
    }

    @Test
    fun `delete returns null when no rows are affected`() {
        // Configure mocks for getById
        val connection = mock<Connection>()
        val getByIdPreparedStatement = mock<PreparedStatement>()
        val getByIdResultSet = mock<ResultSet>()
        val deletePreparedStatement = mock<PreparedStatement>()
        val jugadoresResultSet = mock<ResultSet>()

        // Mock database manager
        val dbManager = mock<DataBaseManager>()
        `when`(dbManager.connection).thenReturn(connection)
        DataBaseManager.instance = dbManager

        // Mock getById call to verify existence
        `when`(connection.prepareStatement(contains("SELECT * FROM Convocatorias WHERE id = ?")))
            .thenReturn(getByIdPreparedStatement)
        `when`(getByIdPreparedStatement.executeQuery()).thenReturn(getByIdResultSet)
        `when`(getByIdResultSet.next()).thenReturn(true, false)
        `when`(getByIdResultSet.getInt("id")).thenReturn(1)
        `when`(getByIdResultSet.getDate("fecha")).thenReturn(java.sql.Date.valueOf(LocalDate.now()))
        `when`(getByIdResultSet.getString("descripcion")).thenReturn("Test Convocatoria")
        `when`(getByIdResultSet.getInt("equipo_id")).thenReturn(101)
        `when`(getByIdResultSet.getInt("entrenador_id")).thenReturn(201)
        `when`(getByIdResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()))
        `when`(getByIdResultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()))

        // Mock jugadores query for getById
        val jugadoresByIdPreparedStatement = mock<PreparedStatement>()
        `when`(connection.prepareStatement(contains("SELECT jugador_id, es_titular FROM JugadoresConvocados")))
            .thenReturn(jugadoresByIdPreparedStatement)
        `when`(jugadoresByIdPreparedStatement.executeQuery()).thenReturn(jugadoresResultSet)
        `when`(jugadoresResultSet.next()).thenReturn(true, true, false)
        `when`(jugadoresResultSet.getInt("jugador_id")).thenReturn(301, 302)
        `when`(jugadoresResultSet.getInt("es_titular")).thenReturn(1, 0)

        // Mock delete statement (returns 0 = no rows affected)
        `when`(connection.prepareStatement(contains("DELETE FROM Convocatorias WHERE id = ?")))
            .thenReturn(deletePreparedStatement)
        `when`(deletePreparedStatement.executeUpdate()).thenReturn(0)

        // Execute the test
        val repository = ConvocatoriaRepositoryImpl(personalRepository)
        val result = repository.delete(1)

        // Verify
        assertNull(result)
    }

    @Test
    fun `validarConvocatoria returns false for convocatorias with a past date`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().minusDays(1),
            descripcion = "Test Convocatoria",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = listOf(1, 2),
            titulares = listOf(1)
        )

        val repository = ConvocatoriaRepositoryImpl(personalRepository)
        val isValid = repository.validarConvocatoria(convocatoria)

        assertFalse(isValid)
    }

    @Test
    fun `validarConvocatoria returns false when no jugadores are present`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Test Convocatoria",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = emptyList(),
            titulares = listOf(1)
        )

        val repository = ConvocatoriaRepositoryImpl(personalRepository)
        val isValid = repository.validarConvocatoria(convocatoria)

        assertFalse(isValid)
    }

    @Test
    fun `validarConvocatoria returns false when no titulares are present`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Test Convocatoria",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = listOf(1, 2),
            titulares = emptyList()
        )

        val repository = ConvocatoriaRepositoryImpl(personalRepository)
        val isValid = repository.validarConvocatoria(convocatoria)

        assertFalse(isValid)
    }

    @Test
    fun `validarConvocatoria returns false when a titular is not part of the jugadores`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Test Convocatoria",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = listOf(1, 2),
            titulares = listOf(3)
        )

        val repository = ConvocatoriaRepositoryImpl(personalRepository)
        val isValid = repository.validarConvocatoria(convocatoria)

        assertFalse(isValid)
    }

    @Test
    fun `getJugadoresNoConvocados returns empty list when no players exist`() {
        val connection = mock<Connection>()

        val dbManager = mock<DataBaseManager>()
        `when`(dbManager.connection).thenReturn(connection)
        DataBaseManager.instance = dbManager

        `when`(personalRepository.getAll()).thenReturn(emptyList())

        val repository = ConvocatoriaRepositoryImpl(personalRepository)
        val result = repository.getJugadoresNoConvocados(1)

        assertTrue(result.isEmpty())
    }
}
