package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class JugadorDaoKtTest {

    /**
     * This class contains unit tests for the `provideJugadorDao` function.
     * The `provideJugadorDao` function is responsible for returning an instance of
     * `JugadorDao` using the Jdbi library for database access.
     */

    @Test
    fun `provideJugadorDao returns a JugadorDao instance`() {
        // Arrange: Create a mock Jdbi instance and configure behavior
        val mockJdbi: Jdbi = mock(Jdbi::class.java)
        val mockJugadorDao: JugadorDao = mock(JugadorDao::class.java)
        whenever(mockJdbi.onDemand(JugadorDao::class.java)).thenReturn(mockJugadorDao)

        // Act: Call the function being tested
        val jugadorDao = provideJugadorDao(mockJdbi)

        // Assert: Verify a non-null instance of JugadorDao is returned and onDemand was called
        assertNotNull(jugadorDao)
        verify(mockJdbi).onDemand(JugadorDao::class.java)
    }


    @Test
    fun `provideJugadorDao onDemand call must not return null`() {
        // Arrange: Create a mock Jdbi instance and configure behavior
        val mockJdbi: Jdbi = mock(Jdbi::class.java)
        val mockJugadorDao: JugadorDao = mock(JugadorDao::class.java)
        whenever(mockJdbi.onDemand(JugadorDao::class.java)).thenReturn(mockJugadorDao)

        // Act: Call the function being tested
        val result = provideJugadorDao(mockJdbi)

        // Assert: Verify onDemand returns non-null value
        assertNotNull(result)
        verify(mockJdbi).onDemand(JugadorDao::class.java)
    }
}