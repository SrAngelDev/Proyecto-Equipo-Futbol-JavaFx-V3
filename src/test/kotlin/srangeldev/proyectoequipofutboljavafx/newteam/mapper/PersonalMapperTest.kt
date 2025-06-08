package srangeldev.proyectoequipofutboljavafx.newteam.mapper

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import srangeldev.proyectoequipofutboljavafx.newteam.dto.PersonalCsvDto
import srangeldev.proyectoequipofutboljavafx.newteam.dto.PersonalJsonDto
import srangeldev.proyectoequipofutboljavafx.newteam.dto.PersonalXmlDto
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

class PersonalMapperTest {

    private val now = LocalDateTime.now()
    private val fechaNacimiento = LocalDate.of(1990, 1, 1)
    private val fechaIncorporacion = LocalDate.of(2020, 1, 1)
    
    private val entrenador = Entrenador(
        id = 1,
        nombre = "Juan",
        apellidos = "Pérez",
        fechaNacimiento = fechaNacimiento,
        fechaIncorporacion = fechaIncorporacion,
        salario = 50000.0,
        paisOrigen = "España",
        createdAt = now,
        updatedAt = now,
        especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
    )
    
    private val jugador = Jugador(
        id = 2,
        nombre = "Pedro",
        apellidos = "García",
        fechaNacimiento = fechaNacimiento,
        fechaIncorporacion = fechaIncorporacion,
        salario = 40000.0,
        paisOrigen = "Argentina",
        createdAt = now,
        updatedAt = now,
        posicion = Jugador.Posicion.DELANTERO,
        dorsal = 9,
        altura = 1.80,
        peso = 75.0,
        goles = 10,
        partidosJugados = 20,
    )
    
    // CSV DTO tests
    
    @Test
    fun `entrenador to CsvDto should map correctly`() {
        // When
        val dto = entrenador.toCsvDto()
        
        // Then
        assertEquals(1, dto.id)
        assertEquals("Juan", dto.nombre)
        assertEquals("Pérez", dto.apellidos)
        assertEquals(fechaNacimiento.toString(), dto.fechaNacimiento)
        assertEquals(fechaIncorporacion.toString(), dto.fechaIncorporacion)
        assertEquals(50000.0, dto.salario)
        assertEquals("España", dto.paisOrigen)
        assertEquals("Entrenador", dto.rol)
        assertEquals("ENTRENADOR_PRINCIPAL", dto.especializacion)
    }
    
    @Test
    fun `jugador to CsvDto should map correctly`() {
        // When
        val dto = jugador.toCsvDto()
        
        // Then
        assertEquals(2, dto.id)
        assertEquals("Pedro", dto.nombre)
        assertEquals("García", dto.apellidos)
        assertEquals(fechaNacimiento.toString(), dto.fechaNacimiento)
        assertEquals(fechaIncorporacion.toString(), dto.fechaIncorporacion)
        assertEquals(40000.0, dto.salario)
        assertEquals("Argentina", dto.paisOrigen)
        assertEquals("Jugador", dto.rol)
        assertEquals("DELANTERO", dto.posicion)
        assertEquals("9", dto.dorsal)
        assertEquals("1.8", dto.altura)
        assertEquals("75.0", dto.peso)
        assertEquals("10", dto.goles)
        assertEquals("20", dto.partidosJugados)
    }
    
    @Test
    fun `CsvDto to entrenador should map correctly`() {
        // Given
        val dto = PersonalCsvDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1990-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            paisOrigen = "España",
            rol = "Entrenador",
            especializacion = "ENTRENADOR_PRINCIPAL"
        )
        
        // When
        val entrenador = dto.toEntrenador()
        
        // Then
        assertEquals(1, entrenador.id)
        assertEquals("Juan", entrenador.nombre)
        assertEquals("Pérez", entrenador.apellidos)
        assertEquals(LocalDate.of(1990, 1, 1), entrenador.fechaNacimiento)
        assertEquals(LocalDate.of(2020, 1, 1), entrenador.fechaIncorporacion)
        assertEquals(50000.0, entrenador.salario)
        assertEquals("España", entrenador.paisOrigen)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_PRINCIPAL, entrenador.especializacion)
    }
    
    @Test
    fun `CsvDto to jugador should map correctly`() {
        // Given
        val dto = PersonalCsvDto(
            id = 2,
            nombre = "Pedro",
            apellidos = "García",
            fechaNacimiento = "1990-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 40000.0,
            paisOrigen = "Argentina",
            rol = "Jugador",
            posicion = "DELANTERO",
            dorsal = "9",
            altura = "1.8",
            peso = "75.0",
            goles = "10",
            partidosJugados = "20"
        )
        
        // When
        val jugador = dto.toJugador()
        
        // Then
        assertEquals(2, jugador.id)
        assertEquals("Pedro", jugador.nombre)
        assertEquals("García", jugador.apellidos)
        assertEquals(LocalDate.of(1990, 1, 1), jugador.fechaNacimiento)
        assertEquals(LocalDate.of(2020, 1, 1), jugador.fechaIncorporacion)
        assertEquals(40000.0, jugador.salario)
        assertEquals("Argentina", jugador.paisOrigen)
        assertEquals(Jugador.Posicion.DELANTERO, jugador.posicion)
        assertEquals(9, jugador.dorsal)
        assertEquals(1.8, jugador.altura)
        assertEquals(75.0, jugador.peso)
        assertEquals(10, jugador.goles)
        assertEquals(20, jugador.partidosJugados)
    }
    
    // JSON DTO tests
    
    @Test
    fun `entrenador to JsonDto should map correctly`() {
        // When
        val dto = entrenador.toJsonDto()
        
        // Then
        assertEquals(1, dto.id)
        assertEquals("Juan", dto.nombre)
        assertEquals("Pérez", dto.apellidos)
        assertEquals(fechaNacimiento.toString(), dto.fechaNacimiento)
        assertEquals(fechaIncorporacion.toString(), dto.fechaIncorporacion)
        assertEquals(50000.0, dto.salario)
        assertEquals("España", dto.pais)
        assertEquals("Entrenador", dto.rol)
        assertEquals("ENTRENADOR_PRINCIPAL", dto.especializacion)
    }
    
    @Test
    fun `jugador to JsonDto should map correctly`() {
        // When
        val dto = jugador.toJsonDto()
        
        // Then
        assertEquals(2, dto.id)
        assertEquals("Pedro", dto.nombre)
        assertEquals("García", dto.apellidos)
        assertEquals(fechaNacimiento.toString(), dto.fechaNacimiento)
        assertEquals(fechaIncorporacion.toString(), dto.fechaIncorporacion)
        assertEquals(40000.0, dto.salario)
        assertEquals("Argentina", dto.pais)
        assertEquals("Jugador", dto.rol)
        assertEquals("DELANTERO", dto.posicion)
        assertEquals(9, dto.dorsal)
        assertEquals(1.8, dto.altura)
        assertEquals(75.0, dto.peso)
        assertEquals(10, dto.goles)
        assertEquals(20, dto.partidosJugados)
    }
    
    @Test
    fun `JsonDto to entrenador should map correctly`() {
        // Given
        val dto = PersonalJsonDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1990-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            pais = "España",
            rol = "Entrenador",
            especializacion = "ENTRENADOR_PRINCIPAL"
        )
        
        // When
        val entrenador = dto.toEntrenador()
        
        // Then
        assertEquals(1, entrenador.id)
        assertEquals("Juan", entrenador.nombre)
        assertEquals("Pérez", entrenador.apellidos)
        assertEquals(LocalDate.of(1990, 1, 1), entrenador.fechaNacimiento)
        assertEquals(LocalDate.of(2020, 1, 1), entrenador.fechaIncorporacion)
        assertEquals(50000.0, entrenador.salario)
        assertEquals("España", entrenador.paisOrigen)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_PRINCIPAL, entrenador.especializacion)
    }
    
    @Test
    fun `JsonDto to jugador should map correctly`() {
        // Given
        val dto = PersonalJsonDto(
            id = 2,
            nombre = "Pedro",
            apellidos = "García",
            fechaNacimiento = "1990-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 40000.0,
            pais = "Argentina",
            rol = "Jugador",
            posicion = "DELANTERO",
            dorsal = 9,
            altura = 1.8,
            peso = 75.0,
            goles = 10,
            partidosJugados = 20
        )
        
        // When
        val jugador = dto.toJugador()
        
        // Then
        assertEquals(2, jugador.id)
        assertEquals("Pedro", jugador.nombre)
        assertEquals("García", jugador.apellidos)
        assertEquals(LocalDate.of(1990, 1, 1), jugador.fechaNacimiento)
        assertEquals(LocalDate.of(2020, 1, 1), jugador.fechaIncorporacion)
        assertEquals(40000.0, jugador.salario)
        assertEquals("Argentina", jugador.paisOrigen)
        assertEquals(Jugador.Posicion.DELANTERO, jugador.posicion)
        assertEquals(9, jugador.dorsal)
        assertEquals(1.8, jugador.altura)
        assertEquals(75.0, jugador.peso)
        assertEquals(10, jugador.goles)
    }
    
    // XML DTO tests
    
    @Test
    fun `entrenador to XmlDto should map correctly`() {
        // When
        val dto = entrenador.toXmlDto()
        
        // Then
        assertEquals(1, dto.id)
        assertEquals("Entrenador", dto.tipo)
        assertEquals("Juan", dto.nombre)
        assertEquals("Pérez", dto.apellidos)
        assertEquals(fechaNacimiento.toString(), dto.fechaNacimiento)
        assertEquals(fechaIncorporacion.toString(), dto.fechaIncorporacion)
        assertEquals(50000.0, dto.salario)
        assertEquals("España", dto.pais)
        assertEquals("ENTRENADOR_PRINCIPAL", dto.especialidad)
    }
    
    @Test
    fun `jugador to XmlDto should map correctly`() {
        // When
        val dto = jugador.toXmlDto()
        
        // Then
        assertEquals(2, dto.id)
        assertEquals("Jugador", dto.tipo)
        assertEquals("Pedro", dto.nombre)
        assertEquals("García", dto.apellidos)
        assertEquals(fechaNacimiento.toString(), dto.fechaNacimiento)
        assertEquals(fechaIncorporacion.toString(), dto.fechaIncorporacion)
        assertEquals(40000.0, dto.salario)
        assertEquals("Argentina", dto.pais)
        assertEquals("DELANTERO", dto.posicion)
        assertEquals("9", dto.dorsal)
        assertEquals("1.8", dto.altura)
        assertEquals("75.0", dto.peso)
        assertEquals("10", dto.goles)
        assertEquals("20", dto.partidosJugados)
    }
    
    @Test
    fun `XmlDto to entrenador should map correctly`() {
        // Given
        val dto = PersonalXmlDto(
            id = 1,
            tipo = "Entrenador",
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1990-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            pais = "España",
            especialidad = "ENTRENADOR_PRINCIPAL"
        )
        
        // When
        val entrenador = dto.toEntrenador()
        
        // Then
        assertEquals(1, entrenador.id)
        assertEquals("Juan", entrenador.nombre)
        assertEquals("Pérez", entrenador.apellidos)
        assertEquals(LocalDate.of(1990, 1, 1), entrenador.fechaNacimiento)
        assertEquals(LocalDate.of(2020, 1, 1), entrenador.fechaIncorporacion)
        assertEquals(50000.0, entrenador.salario)
        assertEquals("España", entrenador.paisOrigen)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_PRINCIPAL, entrenador.especializacion)
    }
    
    @Test
    fun `XmlDto to jugador should map correctly`() {
        // Given
        val dto = PersonalXmlDto(
            id = 2,
            tipo = "Jugador",
            nombre = "Pedro",
            apellidos = "García",
            fechaNacimiento = "1990-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 40000.0,
            pais = "Argentina",
            posicion = "DELANTERO",
            dorsal = "9",
            altura = "1.8",
            peso = "75.0",
            goles = "10",
            partidosJugados = "20"
        )
        
        // When
        val jugador = dto.toJugador()
        
        // Then
        assertEquals(2, jugador.id)
        assertEquals("Pedro", jugador.nombre)
        assertEquals("García", jugador.apellidos)
        assertEquals(LocalDate.of(1990, 1, 1), jugador.fechaNacimiento)
        assertEquals(LocalDate.of(2020, 1, 1), jugador.fechaIncorporacion)
        assertEquals(40000.0, jugador.salario)
        assertEquals("Argentina", jugador.paisOrigen)
        assertEquals(Jugador.Posicion.DELANTERO, jugador.posicion)
        assertEquals(9, jugador.dorsal)
        assertEquals(1.8, jugador.altura)
        assertEquals(75.0, jugador.peso)
        assertEquals(10, jugador.goles)
        assertEquals(20, jugador.partidosJugados)
    }
    
    // Error tests
    
    @Test
    fun `toEntrenador should throw exception for invalid especializacion`() {
        // Given
        val dto = PersonalCsvDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1990-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            paisOrigen = "España",
            rol = "Entrenador",
            especializacion = "INVALID"
        )
        
        // When/Then
        assertThrows<IllegalArgumentException> {
            dto.toEntrenador()
        }
    }
    
    @Test
    fun `toJugador should throw exception for invalid posicion`() {
        // Given
        val dto = PersonalCsvDto(
            id = 2,
            nombre = "Pedro",
            apellidos = "García",
            fechaNacimiento = "1990-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 40000.0,
            paisOrigen = "Argentina",
            rol = "Jugador",
            posicion = "INVALID",
            dorsal = "9",
            altura = "1.8",
            peso = "75.0",
            goles = "10",
            partidosJugados = "20"
        )
        
        // When/Then
        assertThrows<IllegalArgumentException> {
            dto.toJugador()
        }
    }
}