package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals

class JugadorConvocadoDaoKtTest {

    /**
     * Test class for the `provideJugadorConvocadoDao` function.
     * This function is responsible for providing an instance of `JugadorConvocadoDao`
     * using the Jdbi library's `onDemand` method.
     */

@Test
fun `should return a valid instance of JugadorConvocadoDao`() {
    // Arrange
    val mockJdbi = mock(Jdbi::class.java)
    val mockDao = mock(JugadorConvocadoDao::class.java)
    `when`(mockJdbi.onDemand(JugadorConvocadoDao::class.java)).thenReturn(mockDao)

    // Act
    val result = provideJugadorConvocadoDao(mockJdbi)

    // Assert
    assertNotNull(result)
    verify(mockJdbi).onDemand(JugadorConvocadoDao::class.java)
    assertEquals(mockDao, result)
}
}