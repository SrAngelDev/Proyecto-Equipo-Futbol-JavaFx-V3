package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.lighthousegames.logging.logging

class PersonalDaoKtTest {

    /**
     * This test class verifies the behavior of the `providePersonalDao` function.
     * The function `providePersonalDao` is responsible for obtaining an instance
     * of the `PersonalDao` interface through Jdbi's dynamic binding.
     */

    @Test
    fun `providePersonalDao should return a valid PersonalDao instance`() {
        // Arrange
        val mockJdbi = mock(Jdbi::class.java)
        val mockPersonalDao = mock(PersonalDao::class.java)
        whenever(mockJdbi.onDemand(PersonalDao::class.java)).thenReturn(mockPersonalDao)

        // Act
        val result = providePersonalDao(mockJdbi)

        // Assert
        assertNotNull(result)
    }

    @Test
    fun `providePersonalDao should throw exception when Jdbi fails to provide PersonalDao`() {
        // Arrange
        val mockJdbi = mock(Jdbi::class.java)
        whenever(mockJdbi.onDemand(PersonalDao::class.java)).thenThrow(RuntimeException("Jdbi error"))

        // Act & Assert
        assertThrows(RuntimeException::class.java) {
            providePersonalDao(mockJdbi)
        }
    }
}