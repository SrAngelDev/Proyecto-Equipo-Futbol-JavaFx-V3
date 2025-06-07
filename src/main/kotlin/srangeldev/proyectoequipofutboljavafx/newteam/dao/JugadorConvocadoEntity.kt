package srangeldev.proyectoequipofutboljavafx.newteam.dao

/**
 * Entidad que representa una fila en la tabla JugadoresConvocados
 */
data class JugadorConvocadoEntity(
    val convocatoriaId: Int,
    val jugadorId: Int,
    val esTitular: Boolean
)
