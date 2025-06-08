package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Test class for the `provideUserDao` function in the `UserDaoKt` class.
 * The `provideUserDao` function is responsible for creating and returning an instance of `UserDao`
 * using the `Jdbi` on-demand API.
 */
class UserDaoKtTest {

    @Test
    fun `test provideUserDao returns non-null instance of UserDao`() {
        // Arrange
        val mockJdbi = mock(Jdbi::class.java)
        val mockUserDao = mock(UserDao::class.java)
        `when`(mockJdbi.onDemand(UserDao::class.java)).thenReturn(mockUserDao)

        // Act
        val userDao = provideUserDao(mockJdbi)

        // Assert
        assertNotNull(userDao, "Expected UserDao instance to be non-null")
    }
}