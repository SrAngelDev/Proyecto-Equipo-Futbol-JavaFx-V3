package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.models.Equipo
import java.time.LocalDate

/**
 * DAO para los equipos
 */
@RegisterKotlinMapper(Equipo::class)
interface EquipoDao {

    @SqlQuery("SELECT * FROM Equipos")
    fun findAll(): List<Equipo>

    @SqlQuery("SELECT * FROM Equipos WHERE id = :id")
    fun findById(@Bind("id") id: Int): Equipo?

    @SqlQuery("SELECT * FROM Equipos WHERE nombre = :nombre")
    fun findByNombre(@Bind("nombre") nombre: String): Equipo?

    @SqlUpdate("INSERT INTO Equipos (nombre, fecha_fundacion, escudo_url, ciudad, estadio, pais, created_at, updated_at) VALUES (:nombre, :fechaFundacion, :escudoUrl, :ciudad, :estadio, :pais, :createdAt, :updatedAt)")
    @GetGeneratedKeys("id")
    fun save(@BindBean equipo: Equipo): Int

    @SqlUpdate("UPDATE Equipos SET nombre = :nombre, fecha_fundacion = :fechaFundacion, escudo_url = :escudoUrl, ciudad = :ciudad, estadio = :estadio, pais = :pais, updated_at = :updatedAt WHERE id = :id")
    fun update(@BindBean equipo: Equipo): Int

    @SqlUpdate("DELETE FROM Equipos WHERE id = :id")
    fun delete(@Bind("id") id: Int): Int

    @SqlQuery("SELECT COUNT(*) FROM Equipos")
    fun count(): Int

    @SqlQuery("SELECT * FROM Equipos WHERE pais = :pais")
    fun findByPais(@Bind("pais") pais: String): List<Equipo>

    @SqlQuery("SELECT * FROM Equipos WHERE ciudad = :ciudad")
    fun findByCiudad(@Bind("ciudad") ciudad: String): List<Equipo>

    //Implementamos deleteAll
    @SqlUpdate("DELETE FROM Equipos")
    fun deleteAll(): Int
}

/**
 * Función para proporcionar una instancia de EquipoDao
 */
fun provideEquipoDao(jdbi: Jdbi): EquipoDao {
    val logger = logging()
    logger.debug { "Proporcionando instancia de EquipoDao" }
    return jdbi.onDemand(EquipoDao::class.java)
}
