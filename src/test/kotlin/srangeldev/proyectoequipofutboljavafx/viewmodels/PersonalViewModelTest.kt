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