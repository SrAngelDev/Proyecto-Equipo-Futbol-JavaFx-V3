package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class EntrenadorDaoKtTest {

    /**
     * Test class for the provideEntrenadorDao function.
     * This class contains unit tests for providing an instance of EntrenadorDao using Jdbi.
     */

@Test
fun `provideEntrenadorDao should return a non-null instance of EntrenadorDao`() {
    // Arrange
    val mockJdbi = mock<Jdbi>()
    val mockDao = mock<EntrenadorDao>()
    whenever(mockJdbi.onDemand(EntrenadorDao::class.java)).thenReturn(mockDao)

    // Act
    val entrenadorDao = provideEntrenadorDao(mockJdbi)

    // Assert
    assertNotNull(entrenadorDao, "EntrenadorDao instance should not be null")
    verify(mockJdbi).onDemand(EntrenadorDao::class.java)
}
}