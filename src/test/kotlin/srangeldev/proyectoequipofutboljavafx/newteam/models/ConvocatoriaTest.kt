package srangeldev.proyectoequipofutboljavafx.newteam.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ConvocatoriaTest {

    private fun createJugadoresMap(ids: List<Int>, posiciones: List<Jugador.Posicion>): Map<Int, Jugador> {
        val map = mutableMapOf<Int, Jugador>()
        for (i in ids.indices) {
            map[ids[i]] = Jugador(
                id = ids[i],
                nombre = "Jugador $i",
                apellidos = "Apellido $i",
                fechaNacimiento = LocalDate.of(1990, 1, 1),
                fechaIncorporacion = LocalDate.of(2020, 1, 1),
                salario = 1000.0,
                paisOrigen = "España",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                posicion = posiciones[i],
                dorsal = i + 1,
                altura = 180.0,
                peso = 75.0,
                goles = 0,
                partidosJugados = 0
            )
        }
        return map
    }

    @Test
    fun `test convocatoria creation`() {
        // Arrange
        val id = 1
        val fecha = LocalDate.of(2023, 5, 15)
        val descripcion = "Partido amistoso"
        val equipoId = 1
        val entrenadorId = 1
        val jugadores = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)
        val titulares = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()

        // Act
        val convocatoria = Convocatoria(
            id = id,
            fecha = fecha,
            descripcion = descripcion,
            equipoId = equipoId,
            entrenadorId = entrenadorId,
            jugadores = jugadores,
            titulares = titulares,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        // Assert
        assertEquals(id, convocatoria.id)
        assertEquals(fecha, convocatoria.fecha)
        assertEquals(descripcion, convocatoria.descripcion)
        assertEquals(equipoId, convocatoria.equipoId)
        assertEquals(entrenadorId, convocatoria.entrenadorId)
        assertEquals(jugadores, convocatoria.jugadores)
        assertEquals(titulares, convocatoria.titulares)
        assertEquals(createdAt, convocatoria.createdAt)
        assertEquals(updatedAt, convocatoria.updatedAt)
    }

    @Test
    fun `test convocatoria copy`() {
        // Arrange
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2023, 5, 15),
            descripcion = "Partido amistoso",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = listOf(1, 2, 3),
            titulares = listOf(1, 2, 3),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Act
        val newFecha = LocalDate.of(2023, 6, 15)
        val newDescripcion = "Partido de liga"
        val newEquipoId = 2
        val newEntrenadorId = 2
        val newJugadores = listOf(4, 5, 6)
        val newTitulares = listOf(4, 5, 6)

        val copiedConvocatoria = convocatoria.copy(
            fecha = newFecha,
            descripcion = newDescripcion,
            equipoId = newEquipoId,
            entrenadorId = newEntrenadorId,
            jugadores = newJugadores,
            titulares = newTitulares
        )

        // Assert
        assertEquals(convocatoria.id, copiedConvocatoria.id)
        assertEquals(newFecha, copiedConvocatoria.fecha)
        assertEquals(newDescripcion, copiedConvocatoria.descripcion)
        assertEquals(newEquipoId, copiedConvocatoria.equipoId)
        assertEquals(newEntrenadorId, copiedConvocatoria.entrenadorId)
        assertEquals(newJugadores, copiedConvocatoria.jugadores)
        assertEquals(newTitulares, copiedConvocatoria.titulares)
        assertEquals(convocatoria.createdAt, copiedConvocatoria.createdAt)
        assertEquals(convocatoria.updatedAt, copiedConvocatoria.updatedAt)
    }

    @Test
    fun `test toString method`() {
        // Arrange
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2023, 5, 15),
            descripcion = "Partido amistoso",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = listOf(1, 2, 3),
            titulares = listOf(1, 2, 3),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Act
        val result = convocatoria.toString()

        // Assert
        assertTrue(result.contains("Convocatoria"))
        assertTrue(result.contains("id=1"))
        assertTrue(result.contains("fecha=2023-05-15"))
        assertTrue(result.contains("descripcion='Partido amistoso'"))
        assertTrue(result.contains("equipoId=1"))
        assertTrue(result.contains("entrenadorId=1"))
        assertTrue(result.contains("jugadores=[1, 2, 3]"))
        assertTrue(result.contains("titulares=[1, 2, 3]"))
    }

    @Test
    fun `test esValida with valid convocatoria`() {
        // Arrange
        val jugadoresIds = (1..18).toList()
        val posiciones = listOf(
            Jugador.Posicion.PORTERO, Jugador.Posicion.PORTERO, // 2 goalkeepers
            Jugador.Posicion.DEFENSA, Jugador.Posicion.DEFENSA, Jugador.Posicion.DEFENSA, Jugador.Posicion.DEFENSA, // 4 defenders
            Jugador.Posicion.CENTROCAMPISTA, Jugador.Posicion.CENTROCAMPISTA, Jugador.Posicion.CENTROCAMPISTA, Jugador.Posicion.CENTROCAMPISTA, // 4 midfielders
            Jugador.Posicion.DELANTERO, Jugador.Posicion.DELANTERO, // 2 forwards
            Jugador.Posicion.DEFENSA, Jugador.Posicion.DEFENSA, // 2 more defenders
            Jugador.Posicion.CENTROCAMPISTA, Jugador.Posicion.CENTROCAMPISTA, // 2 more midfielders
            Jugador.Posicion.DELANTERO, Jugador.Posicion.DELANTERO // 2 more forwards
        )
        val jugadoresMap = createJugadoresMap(jugadoresIds, posiciones)

        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2023, 5, 15),
            descripcion = "Partido amistoso",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = jugadoresIds,
            titulares = jugadoresIds.take(11) // First 11 players as starters
        )

        // Act
        val result = convocatoria.esValida(jugadoresMap)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `test esValida with too many players`() {
        // Arrange
        val jugadoresIds = (1..19).toList() // 19 players (more than 18)
        val posiciones = List(19) { Jugador.Posicion.DEFENSA } // All defenders for simplicity
        val jugadoresMap = createJugadoresMap(jugadoresIds, posiciones)

        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2023, 5, 15),
            descripcion = "Partido amistoso",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = jugadoresIds,
            titulares = jugadoresIds.take(11) // First 11 players as starters
        )

        // Act
        val result = convocatoria.esValida(jugadoresMap)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `test esValida with too many goalkeepers`() {
        // Arrange
        val jugadoresIds = (1..18).toList()
        val posiciones = listOf(
            Jugador.Posicion.PORTERO, Jugador.Posicion.PORTERO, Jugador.Posicion.PORTERO, // 3 goalkeepers (more than 2)
            Jugador.Posicion.DEFENSA, Jugador.Posicion.DEFENSA, Jugador.Posicion.DEFENSA, Jugador.Posicion.DEFENSA, // 4 defenders
            Jugador.Posicion.CENTROCAMPISTA, Jugador.Posicion.CENTROCAMPISTA, Jugador.Posicion.CENTROCAMPISTA, Jugador.Posicion.CENTROCAMPISTA, // 4 midfielders
            Jugador.Posicion.DELANTERO, Jugador.Posicion.DELANTERO, // 2 forwards
            Jugador.Posicion.DEFENSA, Jugador.Posicion.DEFENSA, // 2 more defenders
            Jugador.Posicion.CENTROCAMPISTA, Jugador.Posicion.CENTROCAMPISTA, // 2 more midfielders
            Jugador.Posicion.DELANTERO // 1 more forward
        )
        val jugadoresMap = createJugadoresMap(jugadoresIds, posiciones)

        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2023, 5, 15),
            descripcion = "Partido amistoso",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = jugadoresIds,
            titulares = jugadoresIds.take(11) // First 11 players as starters
        )

        // Act
        val result = convocatoria.esValida(jugadoresMap)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `test esValida with incorrect number of starters`() {
        // Arrange
        val jugadoresIds = (1..18).toList()
        val posiciones = List(18) { Jugador.Posicion.DEFENSA } // All defenders for simplicity
        val jugadoresMap = createJugadoresMap(jugadoresIds, posiciones)

        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2023, 5, 15),
            descripcion = "Partido amistoso",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = jugadoresIds,
            titulares = jugadoresIds.take(10) // Only 10 starters (less than 11)
        )

        // Act
        val result = convocatoria.esValida(jugadoresMap)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `test esValida with starters not in convocatoria`() {
        // Arrange
        val jugadoresIds = (1..18).toList()
        val posiciones = List(18) { Jugador.Posicion.DEFENSA } // All defenders for simplicity
        val jugadoresMap = createJugadoresMap(jugadoresIds + 19, posiciones + Jugador.Posicion.DEFENSA) // Add one more player

        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2023, 5, 15),
            descripcion = "Partido amistoso",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = jugadoresIds,
            titulares = jugadoresIds.take(10) + 19 // 10 players from convocatoria + 1 not in convocatoria
        )

        // Act
        val result = convocatoria.esValida(jugadoresMap)

        // Assert
        assertFalse(result)
    }
}
