package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class ConvocatoriaDaoKtTest {

    @Test
    fun `provideConvocatoriaDao returns a non-null ConvocatoriaDao instance`() {
        // Arrange
        val mockJdbi = Mockito.mock(Jdbi::class.java)
        Mockito.`when`(mockJdbi.onDemand(ConvocatoriaDao::class.java))
            .thenReturn(Mockito.mock(ConvocatoriaDao::class.java))

        // Act
        val convocatoriaDao = provideConvocatoriaDao(mockJdbi)

        // Assert
        assertNotNull(convocatoriaDao)
    }
}