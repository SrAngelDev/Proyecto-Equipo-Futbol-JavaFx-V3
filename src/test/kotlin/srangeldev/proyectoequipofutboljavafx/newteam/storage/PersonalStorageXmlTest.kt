package srangeldev.proyectoequipofutboljavafx.newteam.storage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import srangeldev.proyectoequipofutboljavafx.newteam.dto.PersonalXmlDto
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toEntrenador
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toJugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File
import java.nio.file.Files
import java.time.LocalDate
import java.time.LocalDateTime
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify

class PersonalStorageXmlTest {

    @Test
    fun `readFromFile should throw exception for invalid XML format`() {
        val invalidXml = """
        <equipo>
            <personal id="1">
                <tipo>Jugador</tipo>
            </personal>    
        </equipo>
    """.trimIndent()
        val tempFile = createTempFile(suffix = ".xml")
        tempFile.writeText(invalidXml)
        try {
            val personalStorageXml = PersonalStorageXml()
            val exception = assertThrows<PersonalException.PersonalStorageException> {
                personalStorageXml.readFromFile(tempFile)
            }
            println(exception.message)
            assertTrue(exception.message?.contains("Falta información requerida") ?: false)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `readFromFile should throw exception when required elements are missing`() {
        val xmlMissingFields = """
            <equipo>
                <personal>
                    <id>1</id>
                    <tipo>Jugador</tipo>
                    <nombre>Juan</nombre>
                </personal>
            </equipo>
        """.trimIndent()

        val tempFile = createTempFile(suffix = ".xml")
        tempFile.writeText(xmlMissingFields)

        try {
            val personalStorageXml = PersonalStorageXml()
            val exception = assertThrows<PersonalException.PersonalStorageException> {
                personalStorageXml.readFromFile(tempFile)
            }
            assertEquals(
                "Error en el almacenamiento: Error en el almacenamiento: Falta información requerida en el elemento personal",
                exception.message
            )
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `readFromFile should correctly parse a single personal element`() {
        val singlePersonalXml = """
            <equipo>
                <personal>
                    <id>1</id>
                    <tipo>Entrenador</tipo>
                    <nombre>Carlos</nombre>
                    <apellidos>Gómez</apellidos>
                    <fechaNacimiento>1980-05-10</fechaNacimiento>
                    <fechaIncorporacion>2019-09-01</fechaIncorporacion>
                    <salario>70000.0</salario>
                    <pais>Argentina</pais>
                    <especialidad>ENTRENADOR_PRINCIPAL</especialidad>
                </personal>
            </equipo>
        """.trimIndent()

        val tempFile = createTempFile(suffix = ".xml")
        tempFile.writeText(singlePersonalXml)

        try {
            val personalStorageXml = PersonalStorageXml()
            val result = personalStorageXml.readFromFile(tempFile)

            assertEquals(1, result.size)
            assertTrue(result[0] is Entrenador)

            with(result[0] as Entrenador) {
                assertEquals(1, id)
                assertEquals("Carlos", nombre)
                assertEquals("Gómez", apellidos)
                assertEquals(LocalDate.parse("1980-05-10"), fechaNacimiento)
                assertEquals(LocalDate.parse("2019-09-01"), fechaIncorporacion)
                assertEquals(70000.0, salario)
                assertEquals("Argentina", paisOrigen)
                assertEquals(Entrenador.Especializacion.ENTRENADOR_PRINCIPAL, especializacion)
            }
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `readFromFile should throw exception when file does not have XML extension`() {
        val file = File("test.txt")
        val personalStorageXml = PersonalStorageXml()

        // Crear el archivo con algún contenido
        file.writeText("algún contenido")

        try {
            val exception = assertThrows<PersonalException.PersonalStorageException> {
                personalStorageXml.readFromFile(file)
            }
            assertEquals(
                "Error en el almacenamiento: Error en el almacenamiento: El fichero no tiene extensión XML: $file",
                exception.message
            )
        } finally {
            file.delete()
        }
    }

    @Test
    fun `readFromFile should throw exception when file is empty`() {
        val file = File("test.xml")

        // Crear el archivo vacío
        file.createNewFile()

        try {
            val personalStorageXml = PersonalStorageXml()
            val exception = assertThrows<PersonalException.PersonalStorageException> {
                personalStorageXml.readFromFile(file)
            }
            assertEquals("Error en el almacenamiento: El fichero está vacío: test.xml", exception.message)
        } finally {
            // Limpiar: eliminar el archivo después de la prueba
            file.delete()
        }
    }

    @Test
    fun `readFromFile should return correct list of Personal`() {
        val contenidoXml = """
            <equipo>
                <personal>
                    <id>1</id>
                    <tipo>Jugador</tipo>
                    <nombre>Juan</nombre>
                    <apellidos>Pérez</apellidos>
                    <fechaNacimiento>1990-01-01</fechaNacimiento>
                    <fechaIncorporacion>2020-08-15</fechaIncorporacion>
                    <salario>50000.0</salario>
                    <pais>España</pais>
                    <posicion>DELANTERO</posicion>
                    <dorsal>9</dorsal>
                    <altura>1.85</altura>
                    <peso>75.0</peso>
                    <goles>50</goles>
                    <partidosJugados>100</partidosJugados>
                </personal>
                <personal>
                    <id>2</id>
                    <tipo>Entrenador</tipo>
                    <nombre>Carlos</nombre>
                    <apellidos>Gómez</apellidos>
                    <fechaNacimiento>1980-05-10</fechaNacimiento>
                    <fechaIncorporacion>2019-09-01</fechaIncorporacion>
                    <salario>70000.0</salario>
                    <pais>Argentina</pais>
                    <especialidad>ENTRENADOR_PRINCIPAL</especialidad>
                </personal>
            </equipo>
        """.trimIndent()

        val tempFile = createTempFile(suffix = ".xml")
        tempFile.writeText(contenidoXml)

        try {
            val personalStorageXml = PersonalStorageXml()
            val result = personalStorageXml.readFromFile(tempFile)

            assertEquals(2, result.size)
            assertTrue(result[0] is Personal)
            assertTrue(result[0] is Jugador)
            assertTrue(result[1] is Personal)
            assertTrue(result[1] is Entrenador)

            with(result[0] as Jugador) {
                assertEquals(1, id)
                assertEquals("Juan", nombre)
                assertEquals("Pérez", apellidos)
                assertEquals(LocalDate.parse("1990-01-01"), fechaNacimiento)
                assertEquals(LocalDate.parse("2020-08-15"), fechaIncorporacion)
                assertEquals(50000.0, salario)
                assertEquals("España", paisOrigen)
                assertEquals(Jugador.Posicion.valueOf("DELANTERO"), posicion)
                assertEquals(9, dorsal)
                assertEquals(1.85, altura)
                assertEquals(75.0, peso)
                assertEquals(50, goles)
                assertEquals(100, partidosJugados)
            }

            with(result[1] as Entrenador) {
                assertEquals(2, id)
                assertEquals("Carlos", nombre)
                assertEquals("Gómez", apellidos)
                assertEquals(LocalDate.parse("1980-05-10"), fechaNacimiento)
                assertEquals(LocalDate.parse("2019-09-01"), fechaIncorporacion)
                assertEquals(70000.0, salario)
                assertEquals("Argentina", paisOrigen)
                assertEquals(Entrenador.Especializacion.valueOf("ENTRENADOR_PRINCIPAL"), especializacion)
            }
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `readFromFile should throw exception when unknown personal type is found`() {
        val contenidoXml = """
            <equipo>
                <personal>
                    <id>3</id>
                    <tipo>Unknown</tipo>
                    <nombre>Pedro</nombre>
                    <apellidos>López</apellidos>
                </personal>
            </equipo>
        """.trimIndent()

        val tempFile = createTempFile(suffix = ".xml")
        tempFile.writeText(contenidoXml)

        try {
            val personalStorageXml = PersonalStorageXml()
            val exception = assertThrows<PersonalException.PersonalStorageException> {
                personalStorageXml.readFromFile(tempFile)
            }
            assertEquals("Error en el almacenamiento: Tipo de Personal desconocido: Unknown", exception.message)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `readFromFile should throw exception when regex fails to identify personal`() {
        val contenidoXml = """
            <equipo>
                <unrelatedTag>Some content</unrelatedTag>
            </equipo>
        """.trimIndent()

        val tempFile = createTempFile(suffix = ".xml")
        tempFile.writeText(contenidoXml)

        try {
            val personalStorageXml = PersonalStorageXml()
            val exception = assertThrows<PersonalException.PersonalStorageException> {
                personalStorageXml.readFromFile(tempFile)
            }
            assertEquals(
                "Error en el almacenamiento: No se encontraron elementos de personal en el archivo XML",
                exception.message
            )
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `readFromFile should allow mixed order of XML elements within personal`() {
        val mixedOrderXml = """
            <equipo>
                <personal id="1">
                    <fechaIncorporacion>2023-06-01</fechaIncorporacion>
                    <salario>38000.0</salario>
                    <pais>Argentina</pais>
                    <especialidad/>
                    <posicion>DEFENSA</posicion>
                    <dorsal>5</dorsal>
                    <altura>1.80</altura>
                    <peso>75.0</peso>
                    <tipo>Jugador</tipo>
                    <nombre>Diego</nombre>
                    <apellidos>Martínez</apellidos>
                    <fechaNacimiento>1997-11-20</fechaNacimiento>
                    <goles>2</goles>
                    <partidosJugados>50</partidosJugados>
                    <imagenUrl>https://www.example.com/images/players/diego_martinez.jpg</imagenUrl>
                </personal>
            </equipo>
        """.trimIndent()

        val tempFile = createTempFile(suffix = ".xml")
        tempFile.writeText(mixedOrderXml)

        try {
            val personalStorageXml = PersonalStorageXml()
            val result = personalStorageXml.readFromFile(tempFile)

            assertEquals(1, result.size)
            assertTrue(result[0] is Jugador)

            with(result[0] as Jugador) {
                assertEquals(1, id)
                assertEquals("Diego", nombre)
                assertEquals("Martínez", apellidos)
                assertEquals(LocalDate.parse("1997-11-20"), fechaNacimiento)
                assertEquals(LocalDate.parse("2023-06-01"), fechaIncorporacion)
                assertEquals(38000.0, salario)
                assertEquals("Argentina", paisOrigen)
                assertEquals(Jugador.Posicion.DEFENSA, posicion)
                assertEquals(5, dorsal)
            }
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `readFromFile should handle duplicate personal entries`() {
        val duplicateEntriesXml = """
            <equipo>
                <personal id="1">
                    <tipo>Jugador</tipo>
                    <nombre>Diego</nombre>
                    <apellidos>Martínez</apellidos>
                    <fechaNacimiento>1997-11-20</fechaNacimiento>
                    <fechaIncorporacion>2023-06-01</fechaIncorporacion>
                    <salario>38000.0</salario>
                    <pais>Argentina</pais>
                    <especialidad/>
                    <posicion>DEFENSA</posicion>
                    <dorsal>5</dorsal>
                    <altura>1.80</altura>
                    <peso>75.0</peso>
                    <goles>2</goles>
                    <partidosJugados>50</partidosJugados>
                </personal>
                <personal id="1">
                    <tipo>Jugador</tipo>
                    <nombre>Diego</nombre>
                    <apellidos>Martínez</apellidos>
                    <fechaNacimiento>1997-11-20</fechaNacimiento>
                    <fechaIncorporacion>2023-06-01</fechaIncorporacion>
                    <salario>38000.0</salario>
                    <pais>Argentina</pais>
                    <especialidad/>
                    <posicion>DEFENSA</posicion>
                    <dorsal>5</dorsal>
                    <altura>1.80</altura>
                    <peso>75.0</peso>
                    <goles>2</goles>
                    <partidosJugados>50</partidosJugados>
                </personal>
            </equipo>
        """.trimIndent()

        val tempFile = createTempFile(suffix = ".xml")
        tempFile.writeText(duplicateEntriesXml)

        try {
            val personalStorageXml = PersonalStorageXml()
            val result = personalStorageXml.readFromFile(tempFile)
            assertEquals(2, result.size)
            assertTrue(result.all { it is Jugador })
            assertEquals(result[0].id, result[1].id)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `writeToFile should generate correct XML content for a list of Personal`() {
        val personalList = listOf(
            Jugador(
                id = 1,
                nombre = "Juan",
                apellidos = "Pérez",
                fechaNacimiento = LocalDate.parse("1990-01-01"),
                fechaIncorporacion = LocalDate.parse("2020-08-15"),
                salario = 50000.0,
                paisOrigen = "España",
                posicion = Jugador.Posicion.DELANTERO,
                dorsal = 9,
                altura = 1.85,
                peso = 75.0,
                goles = 50,
                partidosJugados = 100,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Entrenador(
                id = 2,
                nombre = "Carlos",
                apellidos = "Gómez",
                fechaNacimiento = LocalDate.parse("1980-05-10"),
                fechaIncorporacion = LocalDate.parse("2019-09-01"),
                salario = 70000.0,
                paisOrigen = "Argentina",
                especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        val tempFile = createTempFile(suffix = ".xml")
        try {
            val personalStorageXml = PersonalStorageXml()
            personalStorageXml.writeToFile(tempFile, personalList)

            val expectedXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <equipo>
                    <personal>
                        <id>1</id>
                        <tipo>Jugador</tipo>
                        <nombre>Juan</nombre>
                        <apellidos>Pérez</apellidos>
                        <fechaNacimiento>1990-01-01</fechaNacimiento>
                        <fechaIncorporacion>2020-08-15</fechaIncorporacion>
                        <salario>50000.0</salario>
                        <pais>España</pais>
                        <posicion>DELANTERO</posicion>
                        <dorsal>9</dorsal>
                        <altura>1.85</altura>
                        <peso>75.0</peso>
                        <goles>50</goles>
                    </personal>
                    <personal>
                        <id>2</id>
                        <tipo>Entrenador</tipo>
                        <nombre>Carlos</nombre>
                        <apellidos>Gómez</apellidos>
                        <fechaNacimiento>1980-05-10</fechaNacimiento>
                        <fechaIncorporacion>2019-09-01</fechaIncorporacion>
                        <salario>70000.0</salario>
                        <pais>Argentina</pais>
                        <especialidad>ENTRENADOR_PRINCIPAL</especialidad>
                    </personal>
                </equipo>
            """.trimIndent()

            val actualXml = tempFile.readText()
            assertEquals(expectedXml, actualXml)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `writeToFile should throw exception for a file without XML extension`() {
        val personalList = listOf(
            Jugador(
                id = 1,
                nombre = "Juan",
                apellidos = "Pérez",
                fechaNacimiento = LocalDate.parse("1990-01-01"),
                fechaIncorporacion = LocalDate.parse("2020-08-15"),
                salario = 50000.0,
                paisOrigen = "España",
                posicion = Jugador.Posicion.DELANTERO,
                dorsal = 9,
                altura = 1.85,
                peso = 75.0,
                goles = 50,
                partidosJugados = 100,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        // Creamos un archivo con una ruta completa
        val testDir = File("build/test-files").apply { mkdirs() }
        val file = File(testDir, "invalid_file.txt")

        val personalStorageXml = PersonalStorageXml()
        val exception = assertThrows<PersonalException.PersonalStorageException> {
            personalStorageXml.writeToFile(file, personalList)
        }
        assertEquals("Error en el almacenamiento: El fichero no tiene extensión XML: $file", exception.message)
    }

    @Test
    fun `writeToFile should handle Personal with image URL`() {
        val personal = listOf(
            Jugador(
                id = 1,
                nombre = "Juan",
                apellidos = "Pérez",
                fechaNacimiento = LocalDate.parse("1990-01-01"),
                fechaIncorporacion = LocalDate.parse("2020-08-15"),
                salario = 50000.0,
                paisOrigen = "España",
                posicion = Jugador.Posicion.DELANTERO,
                dorsal = 9,
                altura = 1.85,
                peso = 75.0,
                goles = 50,
                partidosJugados = 100,
                imagenUrl = "https://ejemplo.com/imagen.jpg",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        val tempFile = createTempFile(suffix = ".xml")
        try {
            val personalStorageXml = PersonalStorageXml()
            personalStorageXml.writeToFile(tempFile, personal)

            val contenido = tempFile.readText()
            assertTrue(contenido.contains("<imagenUrl>https://ejemplo.com/imagen.jpg</imagenUrl>"))
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `readFromFile should throw exception when error occurs during XML parsing`() {
        val xmlMalFormado = """
    <equipo>
        <personal>
            <tipo>Jugador</tipo>
            <nombre>Juan</nombre>
            <apellidos>Pérez</apellidos>
            <fechaNacimiento>1990-01-01</fechaNacimiento>
            <fechaIncorporacion>2020-08-15</fechaIncorporacion>
            <posicion>DEFENSA</posicion>
        </personal>
    </equipo>
""".trimIndent()
        val tempFile = createTempFile(suffix = ".xml")
        tempFile.writeText(xmlMalFormado)
        try {
            val personalStorageXml = PersonalStorageXml()
            val excepcion = assertThrows<PersonalException.PersonalStorageException> {
                personalStorageXml.readFromFile(tempFile)
            }
            assertTrue(excepcion.message?.contains("Falta información requerida") ?: false)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `writeToFile should throw exception when trying to write unknown Personal type`() {
        val personalDesconocido = object : Personal(
            id = 1,
            nombre = "Test",
            apellidos = "Unknown",
            fechaNacimiento = LocalDate.now(),
            fechaIncorporacion = LocalDate.now(),
            salario = 1000.0,
            paisOrigen = "Test",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ) {}

        val tempFile = createTempFile(suffix = ".xml")
        try {
            val personalStorageXml = PersonalStorageXml()
            val excepcion = assertThrows<PersonalException.PersonalStorageException> {
                personalStorageXml.writeToFile(tempFile, listOf(personalDesconocido))
            }
            assertEquals("Error en el almacenamiento: Tipo de Personal desconocido", excepcion.message)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `readFromFile should accept different case variations of XML extension`() {
        // Crear archivos temporales con diferentes variaciones de la extensión
        val variaciones = listOf("test.xml", "test.XML", "test.Xml", "test.xMl")

        for (nombreArchivo in variaciones) {
            val file = createTempFile("test", nombreArchivo.substringAfter("test"))
            try {
                // Escribir contenido XML válido mínimo
                val contenidoXmlValido = """
                <?xml version="1.0" encoding="UTF-8"?>
                <equipo>
                    <personal>
                        <id>1</id>
                        <tipo>Jugador</tipo>
                        <nombre>Juan</nombre>
                        <apellidos>Pérez</apellidos>
                        <fechaNacimiento>1990-01-01</fechaNacimiento>
                        <fechaIncorporacion>2020-08-15</fechaIncorporacion>
                        <salario>50000.0</salario>
                        <pais>España</pais>
                        <posicion>DELANTERO</posicion>
                        <dorsal>9</dorsal>
                        <altura>1.85</altura>
                        <peso>75.0</peso>
                        <goles>50</goles>
                        <partidosJugados>100</partidosJugados>
                    </personal>
                </equipo>
            """.trimIndent()
                file.writeText(contenidoXmlValido)

                val personalStorageXml = PersonalStorageXml()
                val result = personalStorageXml.readFromFile(file)

                // Verificar que el XML se procesó correctamente
                assertFalse(result.isEmpty())
                assertEquals(1, result.size)

                // Verificar que los datos del jugador son correctos
                val jugador = result[0] as Jugador
                assertEquals(1, jugador.id)
                assertEquals("Juan", jugador.nombre)
                assertEquals("Pérez", jugador.apellidos)

            } finally {
                file.delete()
            }
        }
    }

    @Test
    fun `readFromFile should accept XML extension with different case variations`() {
        // Preparar diferentes variaciones de extensiones XML
        val extensiones = listOf(".xml", ".XML", ".Xml", ".xMl")

        for (extension in extensiones) {
            val tempFile = createTempFile(suffix = extension)
            try {
                val contenidoXmlValido = """
                <?xml version="1.0" encoding="UTF-8"?>
                <equipo>
                    <personal>
                        <id>1</id>
                        <tipo>Jugador</tipo>
                        <nombre>Juan</nombre>
                        <apellidos>Pérez</apellidos>
                        <fechaNacimiento>1990-01-01</fechaNacimiento>
                        <fechaIncorporacion>2020-08-15</fechaIncorporacion>
                        <salario>50000.0</salario>
                        <pais>España</pais>
                        <posicion>DELANTERO</posicion>
                        <dorsal>9</dorsal>
                        <altura>1.85</altura>
                        <peso>75.0</peso>
                        <goles>50</goles>
                        <partidosJugados>100</partidosJugados>
                    </personal>
                </equipo>
            """.trimIndent()

                tempFile.writeText(contenidoXmlValido)

                val personalStorageXml = PersonalStorageXml()

                // No debería lanzar excepción para ninguna variación
                assertDoesNotThrow {
                    val result = personalStorageXml.readFromFile(tempFile)
                    assertTrue(result.isNotEmpty())
                }

            } finally {
                tempFile.delete()
            }
        }
    }

    @Test
    fun `readFromFile should throw exception when trying to read TXT file`() {
        // Crear un archivo temporal con extensión .txt
        val tempFile = createTempFile(prefix = "test", suffix = ".txt")
        try {
            // Escribir contenido en el archivo
            tempFile.writeText("angel")

            val personalStorageXml = PersonalStorageXml()

            // Verificar que se lanza la excepción correcta
            val exception = assertThrows<PersonalException.PersonalStorageException> {
                personalStorageXml.readFromFile(tempFile)
            }

            // Verificar el mensaje de la excepción
            assertEquals(
                "Error en el almacenamiento: Error en el almacenamiento: El fichero no tiene extensión XML: ${tempFile}",
                exception.message
            )

            // Verificar que el archivo realmente contiene el texto esperado
            assertEquals("angel", tempFile.readText())

        } finally {
            // Limpiar: eliminar el archivo temporal
            tempFile.delete()
        }
    }

    @Test
    fun `toPersonal should convert PersonalXmlDto to correct type or throw exception`() {
        // Mock de PersonalXmlDto para pruebas
        val dto = PersonalXmlDto(
            id = 1,
            nombre = "Test",
            apellidos = "Usuario",
            fechaNacimiento = LocalDate.now().toString(),
            fechaIncorporacion = LocalDate.now().toString(),
            salario = 1000.0,
            pais = "España"
        )
        
        // Caso 1: Tipo Jugador
        dto.tipo = "Jugador"
        dto.posicion = "DELANTERO"
        dto.dorsal = 9.toString()
        dto.altura = 1.80.toString()
        dto.peso = 75.0.toString()
        dto.goles = 10.toString()
        dto.partidosJugados = 20.toString()
        
        val jugador = dto.toJugador()
        assertTrue(jugador is Jugador)
        
        // Caso 2: Tipo Entrenador
        dto.tipo = "Entrenador"
        dto.especialidad = "ENTRENADOR_PRINCIPAL"
        
        val entrenador = dto.toEntrenador()
        assertTrue(entrenador is Entrenador)
        
        // Caso 3: Tipo desconocido
        dto.tipo = "Arbitro"
        
        val exception = assertThrows<PersonalException.PersonalStorageException> {
            dto.toJugador()
        }
        println(exception.message)
        assertEquals("Error en el almacenamiento: Error en el almacenamiento: Tipo de Personal desconocido: Arbitro", exception.message)
    }

    @Test
    fun `should throw PersonalStorageException with correct message when file is corrupted`() {
        // Arrange
        val xmlCorrupto = """
        <?xml version="1.0" encoding="UTF-8"?>
        <personal>
            <persona>
                <!-- XML mal formado intencionalmente -->
                <id>1</id>
                <nombre>Test</nombre>
            </persona>
        </personal>
    """.trimIndent()

        val tempFile = createTempFile(suffix = ".xml")
        tempFile.writeText(xmlCorrupto)

        // Act & Assert
        val exception = assertThrows<PersonalException.PersonalStorageException> {
            PersonalStorageXml().readFromFile(tempFile)
        }
        println(exception.message)

        // Verificaciones
        assertTrue(exception.message?.startsWith("Error en el almacenamiento: Tipo de Personal desconocido") ?: false)

        // Limpieza
        tempFile.delete()
    }
    

}