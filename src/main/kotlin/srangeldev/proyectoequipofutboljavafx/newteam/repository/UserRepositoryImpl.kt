package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.dao.UserDao
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toEntity
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toModel
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.utils.BCryptUtil
import java.time.LocalDateTime

/**
 * Implementación del repositorio de usuarios.
 */
class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    private val logger = logging()
    private val users = mutableMapOf<String, User>()

    init {
        logger.debug { "Inicializando repositorio de usuarios" }
    }

    override fun findAll(): List<User> {
        logger.debug { "Obteniendo todos los usuarios" }

        try {
            val usersList = userDao.findAll()
            // Actualizar la caché
            usersList.forEach { user ->
                users[user.username] = user
            }
            return usersList
        } catch (e: Exception) {
            logger.error { "Error al obtener los usuarios: ${e.message}" }
            throw RuntimeException("Error al obtener los usuarios: ${e.message}")
        }
    }

    override fun getByUsername(username: String): User? {
        logger.debug { "Obteniendo usuario por nombre de usuario: $username" }

        // Primero buscamos en la caché
        if (users.containsKey(username)) {
            return users[username]
        }

        // Si no está en la caché, buscamos en la base de datos
        try {
            val user = userDao.findByUsername(username)

            // Añadimos el usuario a la caché si existe
            if (user != null) {
                users[username] = user
            }

            return user
        } catch (e: Exception) {
            logger.error { "Error al obtener el usuario por nombre de usuario: ${e.message}" }
            return null
        }
    }

    override fun verifyCredentials(username: String, password: String): User? {
        logger.debug { "Verificando credenciales para el usuario: $username" }

        val user = getByUsername(username)

        return if (user != null && BCryptUtil.checkPassword(password, user.password)) {
            user
        } else {
            null
        }
    }

    override fun save(user: User): User {
        logger.debug { "Guardando usuario: ${user.username}" }

        // Para nuevos usuarios, siempre hasheamos la contraseña
        // Para usuarios existentes, la actualización se maneja en el método update
        val hashedPassword = if (user.id == 0) {
            // Si es un nuevo usuario (id = 0), hasheamos la contraseña
            logger.debug { "Hasheando contraseña para nuevo usuario: ${user.username}" }
            BCryptUtil.hashPassword(user.password)
        } else {
            // Si es un usuario existente, dejamos que el método update maneje el hashing
            user.password
        }

        val isUpdate = user.id > 0

        if (isUpdate) {
            // Actualizar usuario existente
            // Crear una copia del usuario con la contraseña hasheada si es necesario
            val userToUpdate = if (hashedPassword != user.password) {
                user.copy(password = hashedPassword)
            } else {
                user
            }

            val updated = update(user.id, userToUpdate)
            if (updated != null) {
                return updated
            } else {
                throw IllegalStateException("No se pudo actualizar el usuario")
            }
        } else {
            try {
                // Crear nuevo usuario con la contraseña hasheada
                val userToSave = user.copy(
                    password = hashedPassword,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                // Guardar el usuario y obtener el ID generado
                val userId = userDao.save(userToSave)

                // Obtener el usuario creado
                val newUser = userDao.findById(userId)
                    ?: throw IllegalStateException("No se pudo obtener el usuario creado")

                // Actualizar la caché
                users[newUser.username] = newUser

                return newUser
            } catch (e: Exception) {
                logger.error { "Error al crear el usuario: ${e.message}" }
                throw IllegalStateException("No se pudo crear el usuario: ${e.message}")
            }
        }
    }

    override fun update(id: Int, user: User): User? {
        logger.debug { "Actualizando usuario con ID: $id" }

        // Verificar si el usuario existe
        val existingUser = getById(id)
        if (existingUser == null) {
            logger.debug { "No se encontró ningún usuario con ID: $id" }
            return null
        }

        // Determinar si la contraseña necesita ser hasheada
        // Verificamos si la contraseña es diferente a la almacenada
        // Si es diferente, asumimos que es una nueva contraseña que necesita ser hasheada
        val hashedPassword = if (user.password != existingUser.password) {
            // Si la contraseña es diferente a la almacenada, la hasheamos
            logger.debug { "Hasheando nueva contraseña para el usuario: ${user.username}" }
            BCryptUtil.hashPassword(user.password)
        } else {
            // Si la contraseña es igual a la almacenada, la mantenemos como está
            user.password
        }

        try {
            // Crear una copia del usuario con la contraseña hasheada y la fecha de actualización actualizada
            val userToUpdate = user.copy(
                password = hashedPassword,
                updatedAt = LocalDateTime.now()
            )

            // Actualizar el usuario
            val rowsAffected = userDao.update(userToUpdate)

            if (rowsAffected > 0) {
                // Obtener el usuario actualizado
                val updatedUser = userDao.findById(id)

                if (updatedUser != null) {
                    // Actualizar la caché
                    users[updatedUser.username] = updatedUser
                    return updatedUser
                }
            }

            return null
        } catch (e: Exception) {
            logger.error { "Error al actualizar el usuario: ${e.message}" }
            return null
        }
    }

    override fun delete(id: Int): Boolean {
        logger.debug { "Eliminando usuario con ID: $id" }

        // Verificar si el usuario existe
        val existingUser = getById(id)
        if (existingUser == null) {
            logger.debug { "No se encontró ningún usuario con ID: $id" }
            return false
        }

        try {
            val rowsAffected = userDao.delete(id)
            val success = rowsAffected > 0

            if (success) {
                // Eliminar de la caché
                users.remove(existingUser.username)
                logger.debug { "Usuario eliminado correctamente: ${existingUser.username}" }
            } else {
                logger.debug { "No se eliminó ningún usuario con ID: $id" }
            }

            return success
        } catch (e: Exception) {
            logger.error { "Error al eliminar el usuario: ${e.message}" }
            return false
        }
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id El ID del usuario.
     * @return El usuario con el ID especificado, o null si no se encuentra.
     */
    fun getById(id: Int): User? {
        logger.debug { "Obteniendo usuario por ID: $id" }

        // Buscar en la caché
        val cachedUser = users.values.find { it.id == id }
        if (cachedUser != null) {
            return cachedUser
        }

        // Si no está en la caché, buscamos en la base de datos
        try {
            val user = userDao.findById(id)

            // Añadimos el usuario a la caché si existe
            if (user != null) {
                users[user.username] = user
            }

            return user
        } catch (e: Exception) {
            logger.error { "Error al obtener el usuario por ID: ${e.message}" }
            return null
        }
    }
}
