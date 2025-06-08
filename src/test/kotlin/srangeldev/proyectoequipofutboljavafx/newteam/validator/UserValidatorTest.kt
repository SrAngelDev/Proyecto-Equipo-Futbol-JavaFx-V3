package srangeldev.proyectoequipofutboljavafx.newteam.validator

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.models.User.Role
import java.time.LocalDateTime

class UserValidatorTest {

    private val validator = UserValidator()

    @Test
    fun `validate should throw PersonalNotFoundException for negative id`() {
        val user = User(
            id = -1,
            username = "validUser",
            password = "password123",
            role = Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        assertThrows(PersonalException.PersonalNotFoundException::class.java) {
            validator.validate(user)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for empty username`() {
        val user = User(
            id = 1,
            username = "",
            password = "password123",
            role = Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(user)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for empty password`() {
        val user = User(
            id = 1,
            username = "validUser",
            password = "",
            role = Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(user)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for short username`() {
        val user = User(
            id = 1,
            username = "ab",
            password = "password123",
            role = Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(user)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for short password`() {
        val user = User(
            id = 1,
            username = "validUser",
            password = "12345",
            role = Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(user)
        }
    }

    @Test
    fun `validate should pass for valid user`() {
        val user = User(
            id = 1,
            username = "validUser",
            password = "password123",
            role = Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        validator.validate(user)
    }
}