package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.lighthousegames.logging.logging

/**
 * DAO para los jugadores
 */
@RegisterKotlinMapper(JugadorEntity::class)
interface JugadorDao {
    
    @SqlQuery("SELECT * FROM Jugadores")
    fun findAll(): List<JugadorEntity>
    
    @SqlQuery("SELECT * FROM Jugadores WHERE id = :id")
    fun findById(@Bind("id") id: Int): JugadorEntity?
    
    @SqlQuery("SELECT * FROM Jugadores WHERE posicion = :posicion")
    fun findByPosicion(@Bind("posicion") posicion: String): List<JugadorEntity>
    
    @SqlQuery("SELECT * FROM Jugadores WHERE dorsal = :dorsal")
    fun findByDorsal(@Bind("dorsal") dorsal: Int): JugadorEntity?
    
    @SqlUpdate("""
        INSERT INTO Jugadores (id, posicion, dorsal, altura, peso, goles, partidos_jugados) 
        VALUES (:id, :posicion, :dorsal, :altura, :peso, :goles, :partidosJugados)
    """)
    fun save(@BindBean jugador: JugadorEntity): Int
    
    @SqlUpdate("""
        UPDATE Jugadores 
        SET posicion = :posicion, dorsal = :dorsal, altura = :altura, peso = :peso, 
        goles = :goles, partidos_jugados = :partidosJugados 
        WHERE id = :id
    """)
    fun update(@BindBean jugador: JugadorEntity): Int
    
    @SqlUpdate("DELETE FROM Jugadores WHERE id = :id")
    fun delete(@Bind("id") id: Int): Int
    
    @SqlQuery("SELECT COUNT(*) FROM Jugadores")
    fun count(): Int
    
    @SqlQuery("SELECT COUNT(*) FROM Jugadores WHERE posicion = :posicion")
    fun countByPosicion(@Bind("posicion") posicion: String): Int
    
    @SqlQuery("SELECT * FROM Jugadores ORDER BY goles DESC LIMIT :limit")
    fun findTopGoleadores(@Bind("limit") limit: Int): List<JugadorEntity>
    
    @SqlQuery("SELECT * FROM Jugadores ORDER BY partidos_jugados DESC LIMIT :limit")
    fun findTopPartidosJugados(@Bind("limit") limit: Int): List<JugadorEntity>
    
    @SqlQuery("SELECT SUM(goles) FROM Jugadores")
    fun sumGoles(): Int
    
    @SqlQuery("SELECT AVG(altura) FROM Jugadores")
    fun avgAltura(): Double
    
    @SqlQuery("SELECT AVG(peso) FROM Jugadores")
    fun avgPeso(): Double

    //Implementamos deleteAll
    @SqlUpdate("DELETE FROM Jugadores")
    fun deleteAll(): Int
}

/**
 * Función para proporcionar una instancia de JugadorDao
 */
fun provideJugadorDao(jdbi: Jdbi): JugadorDao {
    val logger = logging()
    logger.debug { "Proporcionando instancia de JugadorDao" }
    return jdbi.onDemand(JugadorDao::class.java)
}