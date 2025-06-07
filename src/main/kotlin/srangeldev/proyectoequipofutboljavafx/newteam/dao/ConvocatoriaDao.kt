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
 * DAO para las convocatorias
 */
@RegisterKotlinMapper(ConvocatoriaEntity::class)
interface ConvocatoriaDao {
    
    @SqlQuery("SELECT * FROM Convocatorias")
    fun findAll(): List<ConvocatoriaEntity>
    
    @SqlQuery("SELECT * FROM Convocatorias WHERE id = :id")
    fun findById(@Bind("id") id: Int): ConvocatoriaEntity?
    
    @SqlQuery("SELECT * FROM Convocatorias WHERE equipo_id = :equipoId")
    fun findByEquipoId(@Bind("equipoId") equipoId: Int): List<ConvocatoriaEntity>
    
    @SqlQuery("SELECT * FROM Convocatorias WHERE entrenador_id = :entrenadorId")
    fun findByEntrenadorId(@Bind("entrenadorId") entrenadorId: Int): List<ConvocatoriaEntity>
    
    @SqlQuery("SELECT * FROM Convocatorias WHERE fecha = :fecha")
    fun findByFecha(@Bind("fecha") fecha: LocalDate): List<ConvocatoriaEntity>
    
    @SqlUpdate("""
        INSERT INTO Convocatorias (fecha, descripcion, equipo_id, entrenador_id, created_at, updated_at) 
        VALUES (:fecha, :descripcion, :equipoId, :entrenadorId, :createdAt, :updatedAt)
    """)
    @GetGeneratedKeys("id")
    fun save(@BindBean convocatoria: ConvocatoriaEntity): Int
    
    @SqlUpdate("""
        UPDATE Convocatorias 
        SET fecha = :fecha, descripcion = :descripcion, equipo_id = :equipoId, 
        entrenador_id = :entrenadorId, updated_at = :updatedAt 
        WHERE id = :id
    """)
    fun update(@BindBean convocatoria: ConvocatoriaEntity): Int
    
    @SqlUpdate("DELETE FROM Convocatorias WHERE id = :id")
    fun delete(@Bind("id") id: Int): Int
    
    @SqlQuery("SELECT COUNT(*) FROM Convocatorias")
    fun count(): Int
    
    @SqlQuery("SELECT COUNT(*) FROM Convocatorias WHERE equipo_id = :equipoId")
    fun countByEquipoId(@Bind("equipoId") equipoId: Int): Int
    
    @SqlQuery("SELECT COUNT(*) FROM Convocatorias WHERE entrenador_id = :entrenadorId")
    fun countByEntrenadorId(@Bind("entrenadorId") entrenadorId: Int): Int

    //Implementamos deleteAll
    @SqlUpdate("DELETE FROM Convocatorias")
    fun deleteAll(): Int
}

/**
 * Función para proporcionar una instancia de ConvocatoriaDao
 */
fun provideConvocatoriaDao(jdbi: Jdbi): ConvocatoriaDao {
    val logger = logging()
    logger.debug { "Proporcionando instancia de ConvocatoriaDao" }
    return jdbi.onDemand(ConvocatoriaDao::class.java)
}