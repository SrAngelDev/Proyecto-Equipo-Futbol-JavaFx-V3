package srangeldev.proyectoequipofutboljavafx.newteam.dao

import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import java.time.LocalDateTime

/**
 * Entidad que representa una fila en la tabla Usuarios
 */
data class UserEntity(
    val id: Int = 0,
    val username: String,
    val password: String,
    val role: String, // "ADMIN" o "USER"
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Convierte la entidad a un objeto de dominio User
     */
    fun toUser(): User {
        return User(
            id = id,
            username = username,
            password = password,
            role = User.Role.valueOf(role),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        /**
         * Crea una entidad a partir de un objeto de dominio User
         */
        fun fromUser(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                username = user.username,
                password = user.password,
                role = user.role.name,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }
}