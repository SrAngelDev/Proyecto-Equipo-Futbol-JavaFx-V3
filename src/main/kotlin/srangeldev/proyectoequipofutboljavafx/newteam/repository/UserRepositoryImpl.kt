package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.utils.BCryptUtil
import java.sql.Statement
import java.time.LocalDateTime

/**
 * Implementación del repositorio de usuarios.
 */
class UserRepositoryImpl : UserRepository {
    private val logger = logging()
    private val users = mutableMapOf<String, User>()

    init {
        logger.debug { "Inicializando repositorio de usuarios" }
        initDefaultUsers()
    }

    override fun findAll(): List<User> {
        logger.debug { "Obteniendo todos los usuarios" }

        // Limpiar la caché para asegurarnos de obtener datos actualizados
        users.clear()

        val usersList = mutableListOf<User>()

        val sql = "SELECT * FROM Usuarios"

        DataBaseManager.instance.use { db ->
            val statement = db.connection?.createStatement()
            val resultSet = statement?.executeQuery(sql)

            while (resultSet?.next() == true) {
                val user = User(
                    id = resultSet.getInt("id"),
                    username = resultSet.getString("username"),
                    password = resultSet.getString("password"),
                    role = User.Role.valueOf(resultSet.getString("role")),
                    createdAt = LocalDateTime.parse(resultSet.getString("created_at")),
                    updatedAt = LocalDateTime.parse(resultSet.getString("updated_at"))
                )

                // Añadir a la lista y a la caché
                usersList.add(user)
                users[user.username] = user
            }
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
                    createdAt = LocalDateTime.parse(resultSet.getString("created_at")),
                    updatedAt = LocalDateTime.parse(resultSet.getString("updated_at"))
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

        // Hashear la contraseña si no está hasheada
        val hashedPassword = if (!user.password.startsWith("$2a$")) {
            BCryptUtil.hashPassword(user.password)
        } else {
            user.password
        }

        val isUpdate = user.id > 0

        if (isUpdate) {
            // Actualizar usuario existente
            val updated = update(user.id, user)
            if (updated != null) {
                return updated
            } else {
                throw IllegalStateException("No se pudo actualizar el usuario")
            }
        } else {
            // Crear nuevo usuario
            var newUser: User? = null

            DataBaseManager.instance.use { db ->
                val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

                // Insertar el usuario
                val insertSql = """
                    INSERT INTO Usuarios (username, password, role, created_at, updated_at) 
                    VALUES (?, ?, ?, ?, ?)
                """.trimIndent()

                val now = LocalDateTime.now().toString()

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

        // Hashear la contraseña si no está hasheada
        val hashedPassword = if (!user.password.startsWith("$2a$")) {
            BCryptUtil.hashPassword(user.password)
        } else {
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
            preparedStatement.setString(4, LocalDateTime.now().toString())
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
                    createdAt = LocalDateTime.parse(resultSet.getString("created_at")),
                    updatedAt = LocalDateTime.parse(resultSet.getString("updated_at"))
                )

                // Añadimos el usuario a la caché
                users[user!!.username] = user!!
            }
        }

        return user
    }

    override fun initDefaultUsers() {
        logger.debug { "Inicializando usuarios por defecto" }

        // Comprobamos si hay usuarios
        DataBaseManager.instance.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Comprobamos si hay usuarios
            val countSql = "SELECT COUNT(*) FROM Usuarios"
            val resultSet = connection.createStatement().executeQuery(countSql)

            if (resultSet.next() && resultSet.getInt(1) == 0) {
                // No hay usuarios, creamos los usuarios por defecto
                val adminUser = User(
                    username = "admin",
                    password = "admin", // Se hasheará en el método save
                    role = User.Role.ADMIN
                )

                val normalUser = User(
                    username = "user",
                    password = "user", // Se hasheará en el método save
                    role = User.Role.USER
                )

                save(adminUser)
                save(normalUser)

                logger.debug { "Usuarios por defecto creados" }
            } else {
                logger.debug { "Ya existen usuarios en la base de datos" }
            }
        }
    }
}