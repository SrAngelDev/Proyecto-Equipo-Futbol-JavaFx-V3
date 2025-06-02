package srangeldev.proyectoequipofutboljavafx.newteam.models

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
    override fun toString(): String {
        return "User(id=$id, username='$username', role=$role, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
    /**
     * Roles disponibles en el sistema.
     */
    enum class Role {
        ADMIN,
        USER
    }
}
