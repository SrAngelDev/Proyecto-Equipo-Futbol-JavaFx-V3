package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.lighthousegames.logging.logging

/**
 * DAO para los entrenadores
 */
@RegisterKotlinMapper(EntrenadorEntity::class)
interface EntrenadorDao {
    
    @SqlQuery("SELECT * FROM Entrenadores")
    fun findAll(): List<EntrenadorEntity>
    
    @SqlQuery("SELECT * FROM Entrenadores WHERE id = :id")
    fun findById(@Bind("id") id: Int): EntrenadorEntity?
    
    @SqlQuery("SELECT * FROM Entrenadores WHERE especializacion = :especializacion")
    fun findByEspecializacion(@Bind("especializacion") especializacion: String): List<EntrenadorEntity>
    
    @SqlUpdate("INSERT INTO Entrenadores (id, especializacion) VALUES (:id, :especializacion)")
    fun save(@BindBean entrenador: EntrenadorEntity): Int
    
    @SqlUpdate("UPDATE Entrenadores SET especializacion = :especializacion WHERE id = :id")
    fun update(@BindBean entrenador: EntrenadorEntity): Int
    
    @SqlUpdate("DELETE FROM Entrenadores WHERE id = :id")
    fun delete(@Bind("id") id: Int): Int
    
    @SqlQuery("SELECT COUNT(*) FROM Entrenadores")
    fun count(): Int
    
    @SqlQuery("SELECT COUNT(*) FROM Entrenadores WHERE especializacion = :especializacion")
    fun countByEspecializacion(@Bind("especializacion") especializacion: String): Int

    //Implementamos deleteAll
    @SqlUpdate("DELETE FROM Entrenadores")
    fun deleteAll(): Int
}

/**
 * Función para proporcionar una instancia de EntrenadorDao
 */
fun provideEntrenadorDao(jdbi: Jdbi): EntrenadorDao {
    val logger = logging()
    logger.debug { "Proporcionando instancia de EntrenadorDao" }
    return jdbi.onDemand(EntrenadorDao::class.java)
}