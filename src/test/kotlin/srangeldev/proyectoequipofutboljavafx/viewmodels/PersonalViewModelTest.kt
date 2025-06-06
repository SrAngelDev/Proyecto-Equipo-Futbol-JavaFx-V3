package srangeldev.proyectoequipofutboljavafx.viewmodels

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.*
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepository
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PersonalViewModelTest {

    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: PersonalViewModel

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        viewModel = PersonalViewModel(userRepository)
    }

    @Test
    fun `initialize should clear all fields`() {
        // Given
        viewModel.username.set("testUser")
        viewModel.password.set("testPassword")
        viewModel.loginResult.set(PersonalViewModel.LoginResult.ADMIN_LOGIN)
        viewModel.error.set("Some error")

        // When
        viewModel.initialize()

        // Then
        assertEquals("", viewModel.username.get())
        assertEquals("", viewModel.password.get())
        assertNull(viewModel.loginResult.get())
        assertEquals("", viewModel.error.get())
    }

    @Test
    fun `login should return EMPTY_FIELDS when username is empty`() {
        // Given
        viewModel.username.set("")
        viewModel.password.set("password")

        // When
        viewModel.login()

        // Then
        assertEquals(PersonalViewModel.LoginResult.EMPTY_FIELDS, viewModel.loginResult.get())
    }

    @Test
    fun `login should return EMPTY_FIELDS when password is empty`() {
        // Given
        viewModel.username.set("username")
        viewModel.password.set("")

        // When
        viewModel.login()

        // Then
        assertEquals(PersonalViewModel.LoginResult.EMPTY_FIELDS, viewModel.loginResult.get())
    }

    @Test
    fun `login should return ADMIN_LOGIN when admin credentials are valid`() {
        // Given
        val adminUser = User(
            id = 1,
            username = "admin",
            password = "admin",
            role = User.Role.ADMIN
        )
        viewModel.username.set("admin")
        viewModel.password.set("admin")
        whenever(userRepository.verifyCredentials("admin", "admin")).thenReturn(adminUser)

        // When
        viewModel.login()

        // Then
        assertEquals(PersonalViewModel.LoginResult.ADMIN_LOGIN, viewModel.loginResult.get())
    }

    @Test
    fun `login should return USER_LOGIN when user credentials are valid`() {
        // Given
        val regularUser = User(
            id = 2,
            username = "user",
            password = "user",
            role = User.Role.USER
        )
        viewModel.username.set("user")
        viewModel.password.set("user")
        whenever(userRepository.verifyCredentials("user", "user")).thenReturn(regularUser)

        // When
        viewModel.login()

        // Then
        assertEquals(PersonalViewModel.LoginResult.USER_LOGIN, viewModel.loginResult.get())
    }

    @Test
    fun `login should return INVALID_CREDENTIALS when credentials are invalid`() {
        // Given
        viewModel.username.set("wrongUser")
        viewModel.password.set("wrongPassword")
        whenever(userRepository.verifyCredentials("wrongUser", "wrongPassword")).thenReturn(null)

        // When
        viewModel.login()

        // Then
        assertEquals(PersonalViewModel.LoginResult.INVALID_CREDENTIALS, viewModel.loginResult.get())
    }

    @Test
    fun `login should handle exceptions and set error message`() {
        // Given
        viewModel.username.set("testUser")
        viewModel.password.set("testPassword")
        whenever(userRepository.verifyCredentials(any(), any())).thenThrow(RuntimeException("Test exception"))

        // When
        viewModel.login()

        // Then
        assertEquals(PersonalViewModel.LoginResult.INVALID_CREDENTIALS, viewModel.loginResult.get())
        assertEquals("Error durante el login: Test exception", viewModel.error.get())
    }

    @Test
    fun `login should use fallback logic when repository is null`() {
        // Given
        viewModel = PersonalViewModel(null) // No repository
        viewModel.username.set("admin")
        viewModel.password.set("admin")

        // When
        viewModel.login()

        // Then
        assertEquals(PersonalViewModel.LoginResult.ADMIN_LOGIN, viewModel.loginResult.get())
    }

    @Test
    fun `clearPassword should clear only the password field`() {
        // Given
        viewModel.username.set("testUser")
        viewModel.password.set("testPassword")

        // When
        viewModel.clearPassword()

        // Then
        assertEquals("testUser", viewModel.username.get())
        assertEquals("", viewModel.password.get())
    }
}