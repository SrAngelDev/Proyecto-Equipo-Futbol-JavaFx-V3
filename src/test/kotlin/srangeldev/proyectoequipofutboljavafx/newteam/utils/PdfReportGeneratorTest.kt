package srangeldev.proyectoequipofutboljavafx.newteam.utils

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File
import java.nio.file.Files
import java.time.LocalDate
import java.time.LocalDateTime

class PdfReportGeneratorTest {

    /**
     * Tests the generateReport function in PdfReportGenerator.
     * This function generates a PDF report containing details for the given list of Personal
     * and writes it to the specified output file path.
     */

    @Test
    fun `generateReport generates a valid PDF file with players and coaches`() {
        // Given
        val jugadores = listOf(
            Jugador(
                id = 1,
                nombre = "John",
                apellidos = "Doe",
                fechaNacimiento = LocalDate.of(1995, 5, 10),
                posicion = Jugador.Posicion.DEFENSA,
                dorsal = 5,
                goles = 10,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                fechaIncorporacion = LocalDate.of(2021, 6, 1),
                paisOrigen = "Spain",
                salario = 30000.0,
                altura = 130.0,
                peso = 70.0,
                partidosJugados = 20,
            ),
            Jugador(
                id = 2,
                nombre = "Jane",
                apellidos = "Smith",
                fechaNacimiento = LocalDate.of(1998, 3, 15),
                posicion = Jugador.Posicion.PORTERO,
                dorsal = 1,
                goles = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                fechaIncorporacion = LocalDate.of(2021, 6, 1),
                paisOrigen = "Spain",
                salario = 30000.0,
                altura = 130.0,
                peso = 70.0,
                partidosJugados = 20,
            )
        )

        val entrenadores = listOf(
            Entrenador(
                id = 1,
                nombre = "Bruce",
                apellidos = "Wayne",
                fechaNacimiento = LocalDate.of(1980, 6, 20),
                especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
                paisOrigen = "USA",
                fechaIncorporacion = LocalDate.of(2020, 1, 1),
                salario = 50000.0,
                createdAt = LocalDateTime.now() ,
                updatedAt = LocalDateTime.now(),
            )
        )

        val personal = mutableListOf<Personal>().apply {
            addAll(jugadores)
            addAll(entrenadores)
        }

        val outputPath = Files.createTempFile("test_report", ".pdf").toFile().absolutePath

        // When
        val generatedPath = PdfReportGenerator.generateReport(personal, outputPath)

        // Then
        val generatedFile = File(generatedPath)
        assertTrue(generatedFile.exists(), "The PDF report file should exist")
        assertTrue(generatedFile.length() > 0, "The PDF report file should not be empty")

        // Clean up
        generatedFile.delete()
    }

    @Test
    fun `generateReport creates an empty PDF when no personal is provided`() {
        // Given
        val personal = emptyList<Personal>()
        val outputPath = Files.createTempFile("empty_report", ".pdf").toFile().absolutePath

        // When
        val generatedPath = PdfReportGenerator.generateReport(personal, outputPath)

        // Then
        val generatedFile = File(generatedPath)
        assertTrue(generatedFile.exists(), "The PDF report file should exist")
        assertTrue(generatedFile.length() > 0, "The PDF report file should not be empty")

        // Clean up
        generatedFile.delete()
    }

    @Test
    fun `generateConvocatoriaReport generates a valid PDF for convocatoria details`() {
        // Given
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2025, 6, 10),
            descripcion = "Friendly match against top team",
            equipoId = 100,
            entrenadorId = 1,
            jugadores = listOf(1, 2, 3),
            titulares = listOf(1, 2)
        )

        val jugadores = listOf(
            Jugador(
                id = 1,
                nombre = "Player1",
                apellidos = "LastName1",
                fechaNacimiento = LocalDate.of(1990, 1, 1),
                posicion = Jugador.Posicion.PORTERO,
                dorsal = 1,
                goles = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                fechaIncorporacion = LocalDate.of(2020, 5, 5),
                paisOrigen = "USA",
                salario = 60000.0,
                altura = 185.0,
                peso = 78.0,
                partidosJugados = 50
            ),
            Jugador(
                id = 2,
                nombre = "Player2",
                apellidos = "LastName2",
                fechaNacimiento = LocalDate.of(1992, 6, 15),
                posicion = Jugador.Posicion.DEFENSA,
                dorsal = 5,
                goles = 3,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                fechaIncorporacion = LocalDate.of(2021, 5, 20),
                paisOrigen = "Spain",
                salario = 35000.0,
                altura = 180.0,
                peso = 75.0,
                partidosJugados = 30
            )
        )

        val entrenadores = listOf(
            Entrenador(
                id = 1,
                nombre = "Coach1",
                apellidos = "Wayne",
                fechaNacimiento = LocalDate.of(1980, 6, 20),
                especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
                paisOrigen = "England",
                fechaIncorporacion = LocalDate.of(2020, 1, 10),
                salario = 90000.0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        val outputPath = Files.createTempFile("convocatoria_report", ".pdf").toFile().absolutePath

        // When
        val generatedPath =
            PdfReportGenerator.generateConvocatoriaReport(convocatoria, jugadores, entrenadores, outputPath)

        // Then
        val generatedFile = File(generatedPath)
        assertTrue(generatedFile.exists(), "The convocatoria PDF file should exist")
        assertTrue(generatedFile.length() > 0, "The convocatoria PDF file should not be empty")

        // Clean up
        generatedFile.delete()
    }

    @Test
    fun `generateConvocatoriaReport generates valid PDF for empty jugadores and entrenadores`() {
        // Given
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2025, 6, 10),
            descripcion = "No player or coach available",
            equipoId = 101,
            entrenadorId = 2,
            jugadores = emptyList(),
            titulares = emptyList()
        )

        val jugadores = emptyList<Jugador>()
        val entrenadores = emptyList<Entrenador>()

        val outputPath = Files.createTempFile("empty_convocatoria_report", ".pdf").toFile().absolutePath

        // When
        val generatedPath =
            PdfReportGenerator.generateConvocatoriaReport(convocatoria, jugadores, entrenadores, outputPath)

        // Then
        val generatedFile = File(generatedPath)
        assertTrue(generatedFile.exists(), "The PDF report file for empty convocatoria should exist")
        assertTrue(generatedFile.length() > 0, "The PDF report file for empty convocatoria should not be empty")

        // Clean up
        generatedFile.delete()
    }

    @Test
    fun `generateConvocatoriaReport genera PDF válido con entrenadores especializados y suplentes`() {
        // Given
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2025, 6, 10),
            descripcion = "Partido con staff completo",
            equipoId = 100,
            entrenadorId = 1,
            jugadores = listOf(1, 2, 3, 4),
            titulares = listOf(1, 2) // 3 y 4 serán suplentes
        )

        val jugadores = listOf(
            Jugador(
                id = 1,
                nombre = "Titular1",
                apellidos = "Apellido1",
                fechaNacimiento = LocalDate.of(1990, 1, 1),
                posicion = Jugador.Posicion.PORTERO,
                dorsal = 1,
                goles = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                fechaIncorporacion = LocalDate.of(2020, 5, 5),
                paisOrigen = "España",
                salario = 60000.0,
                altura = 185.0,
                peso = 78.0,
                partidosJugados = 50
            ),
            Jugador(
                id = 2,
                nombre = "Titular2",
                apellidos = "Apellido2",
                fechaNacimiento = LocalDate.of(1992, 6, 15),
                posicion = Jugador.Posicion.DEFENSA,
                dorsal = 2,
                goles = 3,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                fechaIncorporacion = LocalDate.of(2021, 5, 20),
                paisOrigen = "España",
                salario = 35000.0,
                altura = 180.0,
                peso = 75.0,
                partidosJugados = 30
            ),
            Jugador(
                id = 3,
                nombre = "Suplente1",
                apellidos = "Apellido3",
                fechaNacimiento = LocalDate.of(1995, 3, 15),
                posicion = Jugador.Posicion.DELANTERO,
                dorsal = 3,
                goles = 5,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                fechaIncorporacion = LocalDate.of(2022, 1, 10),
                paisOrigen = "España",
                salario = 40000.0,
                altura = 175.0,
                peso = 70.0,
                partidosJugados = 15
            ),
            Jugador(
                id = 4,
                nombre = "Suplente2",
                apellidos = "Apellido4",
                fechaNacimiento = LocalDate.of(1993, 8, 20),
                posicion = Jugador.Posicion.CENTROCAMPISTA,
                dorsal = 4,
                goles = 2,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                fechaIncorporacion = LocalDate.of(2021, 7, 1),
                paisOrigen = "España",
                salario = 45000.0,
                altura = 178.0,
                peso = 72.0,
                partidosJugados = 20
            )
        )

        val entrenadores = listOf(
            Entrenador(
                id = 1,
                nombre = "Principal",
                apellidos = "Apellido",
                fechaNacimiento = LocalDate.of(1975, 6, 20),
                especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
                paisOrigen = "España",
                fechaIncorporacion = LocalDate.of(2020, 1, 10),
                salario = 90000.0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Entrenador(
                id = 2,
                nombre = "Asistente",
                apellidos = "Apellido",
                fechaNacimiento = LocalDate.of(1980, 3, 15),
                especializacion = Entrenador.Especializacion.ENTRENADOR_ASISTENTE,
                paisOrigen = "España",
                fechaIncorporacion = LocalDate.of(2020, 1, 15),
                salario = 60000.0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Entrenador(
                id = 3,
                nombre = "Porteros",
                apellidos = "Apellido",
                fechaNacimiento = LocalDate.of(1982, 9, 10),
                especializacion = Entrenador.Especializacion.ENTRENADOR_PORTEROS,
                paisOrigen = "España",
                fechaIncorporacion = LocalDate.of(2020, 2, 1),
                salario = 50000.0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        val outputPath = Files.createTempFile("convocatoria_completa_report", ".pdf").toFile().absolutePath

        // When
        val generatedPath = PdfReportGenerator.generateConvocatoriaReport(convocatoria, jugadores, entrenadores, outputPath)

        // Then
        val generatedFile = File(generatedPath)
        assertTrue(generatedFile.exists(), "El archivo PDF de la convocatoria debería existir")
        assertTrue(generatedFile.length() > 0, "El archivo PDF de la convocatoria no debería estar vacío")

        // Limpieza
        generatedFile.delete()
    }
}