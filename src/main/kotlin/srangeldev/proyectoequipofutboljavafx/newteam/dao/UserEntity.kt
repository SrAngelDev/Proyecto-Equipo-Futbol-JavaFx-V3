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
)