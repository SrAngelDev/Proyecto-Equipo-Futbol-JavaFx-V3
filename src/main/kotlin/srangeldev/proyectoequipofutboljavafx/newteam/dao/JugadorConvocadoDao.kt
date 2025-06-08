package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.lighthousegames.logging.logging

/**
 * DAO para los jugadores convocados
 */
@RegisterKotlinMapper(JugadorConvocadoEntity::class)
interface JugadorConvocadoDao {

    @SqlQuery("SELECT * FROM JugadoresConvocados")
    fun findAll(): List<JugadorConvocadoEntity>

    @SqlQuery("SELECT * FROM JugadoresConvocados WHERE convocatoria_id = :convocatoriaId AND jugador_id = :jugadorId")
    fun findById(@Bind("convocatoriaId") convocatoriaId: Int, @Bind("jugadorId") jugadorId: Int): JugadorConvocadoEntity?

    @SqlQuery("SELECT * FROM JugadoresConvocados WHERE convocatoria_id = :convocatoriaId")
    fun findByConvocatoriaId(@Bind("convocatoriaId") convocatoriaId: Int): List<JugadorConvocadoEntity>

    @SqlQuery("SELECT * FROM JugadoresConvocados WHERE jugador_id = :jugadorId")
    fun findByJugadorId(@Bind("jugadorId") jugadorId: Int): List<JugadorConvocadoEntity>

    @SqlQuery("SELECT * FROM JugadoresConvocados WHERE convocatoria_id = :convocatoriaId AND es_titular = 1")
    fun findTitularesByConvocatoriaId(@Bind("convocatoriaId") convocatoriaId: Int): List<JugadorConvocadoEntity>

    @SqlUpdate("INSERT INTO JugadoresConvocados (convocatoria_id, jugador_id, es_titular) VALUES (:convocatoriaId, :jugadorId, :esTitular)")
    fun save(@BindBean jugadorConvocado: JugadorConvocadoEntity): Int

    @SqlUpdate("UPDATE JugadoresConvocados SET es_titular = :esTitular WHERE convocatoria_id = :convocatoriaId AND jugador_id = :jugadorId")
    fun update(@BindBean jugadorConvocado: JugadorConvocadoEntity): Int

    @SqlUpdate("DELETE FROM JugadoresConvocados WHERE convocatoria_id = :convocatoriaId AND jugador_id = :jugadorId")
    fun delete(@Bind("convocatoriaId") convocatoriaId: Int, @Bind("jugadorId") jugadorId: Int): Int

    @SqlUpdate("DELETE FROM JugadoresConvocados WHERE convocatoria_id = :convocatoriaId")
    fun deleteByConvocatoriaId(@Bind("convocatoriaId") convocatoriaId: Int): Int

    @SqlQuery("SELECT COUNT(*) FROM JugadoresConvocados WHERE convocatoria_id = :convocatoriaId")
    fun countByConvocatoriaId(@Bind("convocatoriaId") convocatoriaId: Int): Int

    @SqlQuery("SELECT COUNT(*) FROM JugadoresConvocados WHERE convocatoria_id = :convocatoriaId AND es_titular = 1")
    fun countTitularesByConvocatoriaId(@Bind("convocatoriaId") convocatoriaId: Int): Int

    @SqlQuery("SELECT jugador_id FROM JugadoresConvocados WHERE convocatoria_id = :convocatoriaId")
    fun getJugadoresIdsByConvocatoriaId(@Bind("convocatoriaId") convocatoriaId: Int): List<Int>

    @SqlQuery("SELECT jugador_id FROM JugadoresConvocados WHERE convocatoria_id = :convocatoriaId AND es_titular = 1")
    fun getTitularesIdsByConvocatoriaId(@Bind("convocatoriaId") convocatoriaId: Int): List<Int>

    //Implementamos deleteAll
    @SqlUpdate("DELETE FROM JugadoresConvocados")
    fun deleteAll(): Int
}

/**
 * Función para proporcionar una instancia de JugadorConvocadoDao
 */
fun provideJugadorConvocadoDao(jdbi: Jdbi): JugadorConvocadoDao {
    val logger = logging()
    logger.debug { "Proporcionando instancia de JugadorConvocadoDao" }
    return jdbi.onDemand(JugadorConvocadoDao::class.java)
}
