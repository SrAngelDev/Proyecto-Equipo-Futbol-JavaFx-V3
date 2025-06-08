package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import java.sql.ResultSet
import java.time.LocalDateTime

/**
 * DAO para los usuarios
 */
interface UserDao {

    @SqlQuery("SELECT * FROM Usuarios")
    fun findAll(): List<User>

    @SqlQuery("SELECT * FROM Usuarios WHERE id = :id")
    fun findById(@Bind("id") id: Int): User?

    @SqlQuery("SELECT * FROM Usuarios WHERE username = :username")
    fun findByUsername(@Bind("username") username: String): User?

    @SqlUpdate("INSERT INTO Usuarios (username, password, role, created_at, updated_at) VALUES (:username, :password, :role, :createdAt, :updatedAt)")
    @GetGeneratedKeys("id")
    fun save(@BindBean user: User): Int

    @SqlUpdate("UPDATE Usuarios SET username = :username, password = :password, role = :role, updated_at = :updatedAt WHERE id = :id")
    fun update(@BindBean user: User): Int

    @SqlUpdate("DELETE FROM Usuarios WHERE id = :id")
    fun delete(@Bind("id") id: Int): Int

    @SqlQuery("SELECT COUNT(*) FROM Usuarios")
    fun count(): Int

    //Implementamos deleteAll
    @SqlUpdate("DELETE FROM Usuarios")
    fun deleteAll(): Int
}

/**
 * Función para proporcionar una instancia de UserDao
 */
fun provideUserDao(jdbi: Jdbi): UserDao {
    val logger = logging()
    logger.debug { "Proporcionando instancia de UserDao" }

    return jdbi.onDemand(UserDao::class.java)
}
