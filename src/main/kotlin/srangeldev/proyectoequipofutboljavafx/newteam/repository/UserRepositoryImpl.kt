package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.utils.BCryptUtil
import java.sql.Statement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementación del repositorio de usuarios.
 */
class UserRepositoryImpl : UserRepository {
    private val logger = logging()
    private val users = mutableMapOf<String, User>()
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init {
        logger.debug { "Inicializando repositorio de usuarios" }
        initDefaultUsers()
    }

    override fun findAll(): List<User> {
        logger.debug { "Obteniendo todos los usuarios" }

        val usersList = mutableListOf<User>()

        try {
            DataBaseManager.instance.use { db ->
                val sql = "SELECT * FROM Usuarios"
                db.connection?.prepareStatement(sql)?.use { statement ->
                    statement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            val user = User(
                                id = resultSet.getInt("id"),
                                username = resultSet.getString("username"),
                                password = resultSet.getString("password"),
                                role = User.Role.valueOf(resultSet.getString("role")),
                                createdAt = LocalDateTime.parse(resultSet.getString("created_at"), dateTimeFormatter),
                                updatedAt = LocalDateTime.parse(resultSet.getString("updated_at"), dateTimeFormatter)
                            )
                            usersList.add(user)
                            users[user.username] = user
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error { "Error al obtener los usuarios: ${e.message}" }
            throw RuntimeException("Error al obtener los usuarios: ${e.message}")
        }

        return usersList
    }

    override fun getByUsername(username: String): User? {
        logger.debug { "Obteniendo usuario por nombre de usuario: $username" }

        // Primero buscamos en la caché
        if (users.containsKey(username)) {
            return users[username]
        }

        // Si no está en la caché, buscamos en la base de datos
        var user: User? = null

        val sql = "SELECT * FROM Usuarios WHERE username = ?"

        DataBaseManager.instance.use { db ->
            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setString(1, username)
            val resultSet = preparedStatement?.executeQuery()

            if (resultSet?.next() == true) {
                user = User(
                    id = resultSet.getInt("id"),
                    username = resultSet.getString("username"),
                    password = resultSet.getString("password"),
                    role = User.Role.valueOf(resultSet.getString("role")),
                    createdAt = LocalDateTime.parse(resultSet.getString("created_at"), dateTimeFormatter),
                    updatedAt = LocalDateTime.parse(resultSet.getString("updated_at"), dateTimeFormatter)
                )

                // Añadimos el usuario a la caché
                users[username] = user!!
            }
        }

        return user
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
            // Crear nuevo usuario
            var newUser: User? = null

            DataBaseManager.instance.use { db ->
                val connection =
                    db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

                // Insertar el usuario
                val insertSql = """
                    INSERT INTO Usuarios (username, password, role, created_at, updated_at) 
                    VALUES (?, ?, ?, ?, ?)
                """.trimIndent()

                val now = LocalDateTime.now().format(dateTimeFormatter)

                val preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)
                preparedStatement.setString(1, user.username)
                preparedStatement.setString(2, hashedPassword)
                preparedStatement.setString(3, user.role.name)
                preparedStatement.setString(4, now)
                preparedStatement.setString(5, now)

                preparedStatement.executeUpdate()

                // Obtener el ID generado
                val generatedKeys = preparedStatement.generatedKeys
                if (generatedKeys.next()) {
                    val userId = generatedKeys.getInt(1)

                    // Obtener el usuario creado
                    newUser = getByUsername(user.username)
                    if (newUser != null) {
                        // Actualizar la caché
                        users[newUser!!.username] = newUser!!
                    } else {
                        throw IllegalStateException("No se pudo obtener el usuario creado")
                    }
                } else {
                    throw IllegalStateException("No se pudo obtener el ID del usuario creado")
                }
            }

            if (newUser != null) {
                return newUser!!
            } else {
                throw IllegalStateException("No se pudo crear el usuario")
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

        DataBaseManager.instance.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Actualizar el usuario
            val updateSql = """
                UPDATE Usuarios 
                SET username = ?, password = ?, role = ?, updated_at = ? 
                WHERE id = ?
            """.trimIndent()

            val preparedStatement = connection.prepareStatement(updateSql)
            preparedStatement.setString(1, user.username)
            preparedStatement.setString(2, hashedPassword)
            preparedStatement.setString(3, user.role.name)
            preparedStatement.setString(4, LocalDateTime.now().format(dateTimeFormatter))
            preparedStatement.setInt(5, id)

            preparedStatement.executeUpdate()
        }

        // Obtener el usuario actualizado
        val updatedUser = getByUsername(user.username)
        if (updatedUser != null) {
            // Actualizar la caché
            users[updatedUser.username] = updatedUser
        }

        return updatedUser
    }

    override fun delete(id: Int): Boolean {
        logger.debug { "Eliminando usuario con ID: $id" }

        // Verificar si el usuario existe
        val existingUser = getById(id)
        if (existingUser == null) {
            logger.debug { "No se encontró ningún usuario con ID: $id" }
            return false
        }

        var success = false

        DataBaseManager.instance.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            val sql = "DELETE FROM Usuarios WHERE id = ?"

            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, id)

            val rowsAffected = preparedStatement.executeUpdate()
            success = rowsAffected > 0

            if (success) {
                // Eliminar de la caché
                users.remove(existingUser.username)
                logger.debug { "Usuario eliminado correctamente: ${existingUser.username}" }
            } else {
                logger.debug { "No se eliminó ningún usuario con ID: $id" }
            }
        }

        return success
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id El ID del usuario.
     * @return El usuario con el ID especificado, o null si no se encuentra.
     */
    private fun getById(id: Int): User? {
        logger.debug { "Obteniendo usuario por ID: $id" }

        // Buscar en la caché
        val cachedUser = users.values.find { it.id == id }
        if (cachedUser != null) {
            return cachedUser
        }

        // Si no está en la caché, buscamos en la base de datos
        var user: User? = null

        val sql = "SELECT * FROM Usuarios WHERE id = ?"

        DataBaseManager.instance.use { db ->
            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, id)
            val resultSet = preparedStatement?.executeQuery()

            if (resultSet?.next() == true) {
                user = User(
                    id = resultSet.getInt("id"),
                    username = resultSet.getString("username"),
                    password = resultSet.getString("password"),
                    role = User.Role.valueOf(resultSet.getString("role")),
                    createdAt = LocalDateTime.parse(resultSet.getString("created_at"), dateTimeFormatter),
                    updatedAt = LocalDateTime.parse(resultSet.getString("updated_at"), dateTimeFormatter)
                )

                // Añadimos el usuario a la caché
                users[user!!.username] = user!!
            }
        }

        return user
    }

    private fun initDefaultUsers() {
        logger.debug { "Los usuarios por defecto se inicializan desde data.sql" }
        // Los usuarios por defecto ahora se crean desde el archivo data.sql
        // No es necesario crearlos programáticamente
    }
}
