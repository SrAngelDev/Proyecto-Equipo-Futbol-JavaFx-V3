package srangeldev.models

import java.time.LocalDateTime

/**
 * Clase que representa a un usuario del sistema.
 */
data class User(
    val id: Int = 0,
    val username: String,
    val password: String, // Contraseña cifrada con bcrypt
    val role: Role,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Roles disponibles en el sistema.
     */
    enum class Role {
        ADMIN,
        USER
    }
}