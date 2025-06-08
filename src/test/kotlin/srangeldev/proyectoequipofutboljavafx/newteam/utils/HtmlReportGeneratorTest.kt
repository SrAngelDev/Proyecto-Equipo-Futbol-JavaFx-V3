package srangeldev.proyectoequipofutboljavafx.newteam.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime

class HtmlReportGeneratorTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `test generateReport creates HTML file with correct content`() {
        // Arrange
        val outputPath = tempDir.resolve("report.html").toString()
        val personal = listOf<Personal>(
            createJugador(1, "Lionel", "Messi", Jugador.Posicion.DELANTERO, 10),
            createJugador(2, "Cristiano", "Ronaldo", Jugador.Posicion.DELANTERO, 7),
            createEntrenador(3, "Pep", "Guardiola", Entrenador.Especializacion.ENTRENADOR_PRINCIPAL)
        )

        // Act
        val result = HtmlReportGenerator.generateReport(personal, outputPath)

        // Assert
        val file = File(result)
        assertTrue(file.exists())
        val content = file.readText()
        assertTrue(content.contains("<!DOCTYPE html>"))
        assertTrue(content.contains("<title>Plantilla del Club de Fútbol New Team</title>"))
        assertTrue(content.contains("Lionel"))
        assertTrue(content.contains("Messi"))
        assertTrue(content.contains("Cristiano"))
        assertTrue(content.contains("Ronaldo"))
        assertTrue(content.contains("Pep"))
        assertTrue(content.contains("Guardiola"))
    }

    @Test
    fun `test generateConvocatoriaReport creates HTML file with correct content`() {
        // Arrange
        val outputPath = tempDir.resolve("convocatoria.html").toString()
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2023, 5, 15),
            descripcion = "Partido amistoso",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13),
            titulares = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val jugadores = listOf(
            createJugador(1, "Lionel", "Messi", Jugador.Posicion.DELANTERO, 10),
            createJugador(2, "Cristiano", "Ronaldo", Jugador.Posicion.DELANTERO, 7),
            createJugador(3, "Sergio", "Ramos", Jugador.Posicion.DEFENSA, 4),
            createJugador(4, "Gerard", "Piqué", Jugador.Posicion.DEFENSA, 3),
            createJugador(5, "Luka", "Modric", Jugador.Posicion.CENTROCAMPISTA, 10),
            createJugador(6, "Toni", "Kroos", Jugador.Posicion.CENTROCAMPISTA, 8),
            createJugador(7, "Casemiro", "", Jugador.Posicion.CENTROCAMPISTA, 14),
            createJugador(8, "Jordi", "Alba", Jugador.Posicion.DEFENSA, 18),
            createJugador(9, "Dani", "Carvajal", Jugador.Posicion.DEFENSA, 2),
            createJugador(10, "Thibaut", "Courtois", Jugador.Posicion.PORTERO, 1),
            createJugador(11, "Karim", "Benzema", Jugador.Posicion.DELANTERO, 9),
            createJugador(12, "Marco", "Asensio", Jugador.Posicion.DELANTERO, 11),
            createJugador(13, "Isco", "", Jugador.Posicion.CENTROCAMPISTA, 22)
        )
        
        val entrenadores = listOf(
            createEntrenador(1, "Pep", "Guardiola", Entrenador.Especializacion.ENTRENADOR_PRINCIPAL),
            createEntrenador(2, "Tito", "Vilanova", Entrenador.Especializacion.ENTRENADOR_ASISTENTE),
            createEntrenador(3, "José", "Mourinho", Entrenador.Especializacion.ENTRENADOR_PORTEROS)
        )

        // Act
        val result = HtmlReportGenerator.generateConvocatoriaReport(convocatoria, jugadores, entrenadores, outputPath)

        // Assert
        val file = File(result)
        assertTrue(file.exists())
        val content = file.readText()
        assertTrue(content.contains("<!DOCTYPE html>"))
        assertTrue(content.contains("<title>Convocatoria - 2023-05-15</title>"))
        assertTrue(content.contains("Partido amistoso"))
        assertTrue(content.contains("Lionel"))
        assertTrue(content.contains("Messi"))
        assertTrue(content.contains("Pep"))
        assertTrue(content.contains("Guardiola"))
        assertTrue(content.contains("Tito"))
        assertTrue(content.contains("Vilanova"))
        assertTrue(content.contains("José"))
        assertTrue(content.contains("Mourinho"))
        assertTrue(content.contains("Entrenador Principal"))
        assertTrue(content.contains("Entrenador Asistente"))
        assertTrue(content.contains("Entrenador de Porteros"))
    }

    private fun createJugador(
        id: Int,
        nombre: String,
        apellidos: String,
        posicion: Jugador.Posicion,
        dorsal: Int
    ): Jugador {
        return Jugador(
            id = id,
            nombre = nombre,
            apellidos = apellidos,
            fechaNacimiento = LocalDate.of(1987, 6, 24),
            fechaIncorporacion = LocalDate.of(2021, 8, 10),
            salario = 100000.0,
            paisOrigen = "Argentina",
            posicion = posicion,
            dorsal = dorsal,
            altura = 1.70,
            peso = 72.0,
            goles = 700,
            partidosJugados = 800,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    private fun createEntrenador(
        id: Int,
        nombre: String,
        apellidos: String,
        especializacion: Entrenador.Especializacion
    ): Entrenador {
        return Entrenador(
            id = id,
            nombre = nombre,
            apellidos = apellidos,
            fechaNacimiento = LocalDate.of(1971, 1, 18),
            fechaIncorporacion = LocalDate.of(2021, 7, 1),
            salario = 200000.0,
            paisOrigen = "España",
            especializacion = especializacion,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    @Test
    fun `test promedio de goles con lista vacía`() {
        // Arrange
        val outputPath = tempDir.resolve("report-empty.html").toString()
        val personal = emptyList<Personal>()

        // Act
        val result = HtmlReportGenerator.generateReport(personal, outputPath)

        // Assert
        val file = File(result)
        val content = file.readText()
        assertTrue(content.contains("Promedio de goles: 0"))
    }

    @Test
    fun `test convocatoria sin entrenadores`() {
        // Arrange
        val outputPath = tempDir.resolve("convocatoria-sin-entrenadores.html").toString()
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2023, 5, 15),
            descripcion = "Partido amistoso",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = listOf(1),
            titulares = listOf(1),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val jugadores = listOf(
            createJugador(1, "Lionel", "Messi", Jugador.Posicion.DELANTERO, 10)
        )
        val entrenadores = emptyList<Entrenador>()

        // Act
        val result = HtmlReportGenerator.generateConvocatoriaReport(convocatoria, jugadores, entrenadores, outputPath)

        // Assert
        val content = File(result).readText()
        assertFalse(content.contains("Entrenador Principal"))
        assertFalse(content.contains("Entrenador Asistente"))
        assertFalse(content.contains("Entrenador de Porteros"))
    }
}