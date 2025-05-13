package srangeldev.repository

import org.lighthousegames.logging.logging
import srangeldev.database.DataBaseManager
import srangeldev.models.User
import srangeldev.utils.BCryptUtil
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

    override fun getByUsername(username: String): User? {
        logger.debug { "Obteniendo usuario por nombre de usuario: $username" }

        // Primero buscamos en la caché
        if (users.containsKey(username)) {
            return users[username]
        }

        // Si no está en la caché, buscamos en la base de datos
        var user: User? = null

        val sql = "SELECT * FROM Usuarios WHERE username = ?"

        DataBaseManager.use { db ->
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

        val timeStamp = LocalDateTime.now()

        // Hasheamos la contraseña
        val hashedPassword = BCryptUtil.hashPassword(user.password)

        var generatedId = 0

        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            val sql = """
                INSERT INTO Usuarios (username, password, role, created_at, updated_at) 
                VALUES (?, ?, ?, ?, ?)
            """.trimIndent()

            val preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            preparedStatement.apply {
                setString(1, user.username)
                setString(2, hashedPassword)
                setString(3, user.role.name)
                setString(4, timeStamp.toString())
                setString(5, timeStamp.toString())
            }

            preparedStatement.executeUpdate()

            generatedId = preparedStatement.generatedKeys.let {
                it.next()
                it.getInt(1)
            }
        }

        val savedUser = User(
            id = generatedId,
            username = user.username,
            password = hashedPassword,
            role = user.role,
            createdAt = timeStamp,
            updatedAt = timeStamp
        )

        // Añadimos el usuario a la caché
        users[user.username] = savedUser

        return savedUser
    }

    override fun initDefaultUsers() {
        logger.debug { "Inicializando usuarios por defecto" }

        // Comprobamos si la tabla existe
        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Creamos la tabla si no existe
            val createTableSql = """
                CREATE TABLE IF NOT EXISTS Usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL
                )
            """.trimIndent()

            connection.createStatement().execute(createTableSql)

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
