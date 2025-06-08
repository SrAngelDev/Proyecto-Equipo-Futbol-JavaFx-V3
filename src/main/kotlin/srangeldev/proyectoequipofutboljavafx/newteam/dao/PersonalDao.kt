package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.lighthousegames.logging.logging
import java.time.LocalDate

/**
 * DAO para el personal (entrenadores y jugadores)
 */
@RegisterKotlinMapper(PersonalEntity::class)
interface PersonalDao {
    
    @SqlQuery("SELECT * FROM Personal")
    fun findAll(): List<PersonalEntity>
    
    @SqlQuery("SELECT * FROM Personal WHERE id = :id")
    fun findById(@Bind("id") id: Int): PersonalEntity?
    
    @SqlQuery("SELECT * FROM Personal WHERE tipo = :tipo")
    fun findByTipo(@Bind("tipo") tipo: String): List<PersonalEntity>
    
    @SqlQuery("SELECT * FROM Personal WHERE nombre LIKE :nombre")
    fun findByNombre(@Bind("nombre") nombre: String): List<PersonalEntity>
    
    @SqlUpdate("""
        INSERT INTO Personal (nombre, apellidos, fecha_nacimiento, fecha_incorporacion, salario, pais_origen, tipo, imagen_url, created_at, updated_at) 
        VALUES (:nombre, :apellidos, :fechaNacimiento, :fechaIncorporacion, :salario, :paisOrigen, :tipo, :imagenUrl, :createdAt, :updatedAt)
    """)
    @GetGeneratedKeys("id")
    fun save(@BindBean personal: PersonalEntity): Int
    
    @SqlUpdate("""
        UPDATE Personal 
        SET nombre = :nombre, apellidos = :apellidos, fecha_nacimiento = :fechaNacimiento, 
        fecha_incorporacion = :fechaIncorporacion, salario = :salario, pais_origen = :paisOrigen, 
        tipo = :tipo, imagen_url = :imagenUrl, updated_at = :updatedAt 
        WHERE id = :id
    """)
    fun update(@BindBean personal: PersonalEntity): Int
    
    @SqlUpdate("DELETE FROM Personal WHERE id = :id")
    fun delete(@Bind("id") id: Int): Int
    
    @SqlQuery("SELECT COUNT(*) FROM Personal")
    fun count(): Int
    
    @SqlQuery("SELECT COUNT(*) FROM Personal WHERE tipo = :tipo")
    fun countByTipo(@Bind("tipo") tipo: String): Int
    
    @SqlQuery("SELECT * FROM Personal WHERE pais_origen = :paisOrigen")
    fun findByPaisOrigen(@Bind("paisOrigen") paisOrigen: String): List<PersonalEntity>
    
    @SqlQuery("SELECT * FROM Personal WHERE fecha_incorporacion >= :fecha")
    fun findByFechaIncorporacionDesde(@Bind("fecha") fecha: LocalDate): List<PersonalEntity>
    
    @SqlQuery("SELECT * FROM Personal WHERE salario > :salario")
    fun findBySalarioMayorQue(@Bind("salario") salario: Double): List<PersonalEntity>

    //Implementamos deleteAll
    @SqlUpdate("DELETE FROM Personal")
    fun deleteAll(): Int
}

/**
 * Función para proporcionar una instancia de PersonalDao
 */
fun providePersonalDao(jdbi: Jdbi): PersonalDao {
    val logger = logging()
    logger.debug { "Proporcionando instancia de PersonalDao" }
    return jdbi.onDemand(PersonalDao::class.java)
}