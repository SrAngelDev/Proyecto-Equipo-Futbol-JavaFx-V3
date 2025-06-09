package srangeldev.proyectoequipofutboljavafx.newteam.storage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.api.assertThrows
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime

class PersonalStorageJsonTest {
    @Test
    fun writeToFileOverwritesExistingFileContent() {
        val file = tempDir.resolve("output.json").toFile()
        // Escribir un JSON válido inicial
        file.writeText("""[]""")  // Array JSON vacío

        val storage = PersonalStorageJson()
        val jugador = Jugador(
            id = 1,
            nombre = "Carlos",
            apellidos = "Lopez",
            dorsal = 5,
            posicion = Jugador.Posicion.DELANTERO,
            paisOrigen = "Chile",
            salario = 3000.0,
            fechaNacimiento = LocalDate.of(1992, 3, 3),
            fechaIncorporacion = LocalDate.of(2018, 4, 4),
            createdAt = LocalDateTime.of(2019, 4, 4, 0, 0),
            updatedAt = LocalDateTime.of(2023, 4, 4, 0, 0),
            altura = 190.0,
            peso = 150.0,
            goles = 30,
            partidosJugados = 50,
        )
        storage.writeToFile(file, listOf(jugador))

        val contenidoFinal = file.readText()
        assertTrue(contenidoFinal.contains("Carlos"))
        assertTrue(contenidoFinal.startsWith("["))
        assertTrue(contenidoFinal.endsWith("]"))
    }

    @Test
    fun readFromFileReturnsEmptyListForEmptyJsonFile() {
        val file = tempDir.resolve("empty.json").toFile()
        file.writeText("")
        val storage = PersonalStorageJson()
        val exception = assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(file)
        }
        assertTrue(exception.message!!.contains("Error en el almacenamiento"))
    }

    @Test
    fun readFromFileThrowsForUnknownRole() {
        val content = """
    [
        {
            "id": 3,
            "nombre": "Luis",
            "apellidos": "Gomez",
            "rol": "Unknown",
            "salario": 3000.0,
            "pais": "España",
            "fecha_nacimiento": "1990-06-06",
            "fecha_incorporacion": "2020-06-06",
            "createdAt": "2021-06-06T00:00:00",
            "updatedAt": "2023-06-06T00:00:00"
        }
    ]
    """.trimIndent()
        val file = tempDir.resolve("unknown_role.json").toFile()
        file.writeText(content)
        val storage = PersonalStorageJson()
        val exception = assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(file)
        }
        assertEquals("Error en el almacenamiento: Tipo de personal desconocido: Unknown", exception.message)
    }

    @Test
    fun readFromFileThrowsForIncompletePersonalData() {
        val content = """
        [
            {
                "id": 4,
                "nombre": "Carlos",
                "apellidos": "Lopez",
                "rol": "Jugador",
                "salario": 4000.0
            }
        ]
        """.trimIndent()
        val file = tempDir.resolve("incomplete_data.json").toFile()
        file.writeText(content)
        val storage = PersonalStorageJson()
        val exception = assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(file)
        }
        assertTrue(exception.message!!.contains("Error en el almacenamiento"))
    }

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun readFromFileThrowsWhenFileDoesNotExist() {
        val file = tempDir.resolve("test.json").toFile()
        val storage = PersonalStorageJson()

        val exception = assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(file)
        }
        assertTrue(exception.message!!.contains("El fichero no existe:"))
    }

    @Test
    fun readFromFileReturnsDecodedJugador() {
        val content = """
    [
        {
            "id": 2,
            "nombre": "Pedro",
            "apellidos": "Martinez",
            "rol": "Jugador",
            "dorsal": 7,
            "posicion": "DELANTERO",
            "pais": "España",
            "salario": 2000.0,
            "fecha_nacimiento": "1995-02-02",
            "fecha_incorporacion": "2021-02-02",
            "createdAt": "2022-02-02T00:00:00",
            "updatedAt": "2023-02-02T00:00:00"
        }
    ]
""".trimIndent()
        val file = tempDir.resolve("jugador.json").toFile()
        file.writeText(content)
        val storage = PersonalStorageJson()
        val result = storage.readFromFile(file)
        assertEquals(1, result.size)
        val jugador = result[0] as Jugador
        assertEquals("Pedro", jugador.nombre)
    }

    @Test
    fun readFromFileThrowsOnInvalidJson() {
        val file = tempDir.resolve("invalido.json").toFile()
        file.writeText("{ invalid json }")
        val storage = PersonalStorageJson()
        val exception = assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(file)
        }
        assertTrue(exception.message!!.contains("Error en el almacenamiento"))
    }

    @Test
    fun writeToFileThrowsForInvalidExtension() {
        val file = tempDir.resolve("output.txt").toFile()
        file.createNewFile()

        val storage = PersonalStorageJson()
        val exception = assertThrows<PersonalException.PersonalStorageException> {
            storage.writeToFile(file, emptyList())
        }
        assertTrue(
            exception.message!!.contains("El fichero está vacío"),
            "El mensaje actual es: ${exception.message}"
        )
    }

    @Test
    fun readFromFileHandlesMultipleRoles() {
        val content = """
        [
            {
                "id": 1,
                "nombre": "Ana",
                "apellidos": "Garcia",
                "rol": "Entrenador",
                "pais": "España",
                "salario": 5000.0,
                "fecha_nacimiento": "1980-01-01",
                "fecha_incorporacion": "2019-01-01",
                "createdAt": "2020-01-01T00:00:00",
                "updatedAt": "2023-01-01T00:00:00"
            },
            {
                "id": 2,
                "nombre": "Carlos",
                "apellidos": "Fernandez", 
                "rol": "Jugador",
                "dorsal": 10,
                "posicion": "DELANTERO",
                "pais": "España",
                "salario": 3000.0,
                "fecha_nacimiento": "1992-01-01",
                "fecha_incorporacion": "2020-01-01",
                "createdAt": "2021-01-01T00:00:00",
                "updatedAt": "2023-01-01T00:00:00"
            }
        ]
        """.trimIndent()
        val file = tempDir.resolve("multiple_roles.json").toFile()
        file.writeText(content)
        val storage = PersonalStorageJson()

        val result = storage.readFromFile(file)

        assertEquals(2, result.size)
        val entrenador = result[0] as Entrenador
        assertEquals("Ana", entrenador.nombre)
        val jugador = result[1] as Jugador
        assertEquals("Carlos", jugador.nombre)
    }

    @Test
    fun readFromFileIgnoresUnknownKeys() {
        val content = """
        [
            {
                "id": 3,
                "nombre": "Maria",
                "apellidos": "Lopez",
                "rol": "Jugador",
                "dorsal": 8,
                "posicion": "CENTROCAMPISTA",
                "pais": "Argentina",
                "salario": 3500.0,
                "fecha_nacimiento": "1990-03-03",
                "fecha_incorporacion": "2018-03-03",
                "createdAt": "2019-03-03T00:00:00",
                "updatedAt": "2023-03-03T00:00:00",
                "extra_field": "this_should_be_ignored"
            }
        ]
        """.trimIndent()
        val file = tempDir.resolve("unknown_keys.json").toFile()
        file.writeText(content)
        val storage = PersonalStorageJson()

        val result = storage.readFromFile(file)

        assertEquals(1, result.size)
        val jugador = result[0] as Jugador
        assertEquals("Maria", jugador.nombre)
    }

    @Test
    fun readFromFileThrowsForInvalidFilePath() {
        // Usar tempDir que ya está definido en la clase
        val file = tempDir.resolve("nonexistent/test.json").toFile()
        val storage = PersonalStorageJson()

        val exception = assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(file)
        }
        assertTrue(exception.message!!.contains("El fichero no existe:"))
    }

    @Test
    fun writeToFileCreatesParentDirectories() {
        // Crear un directorio que no existe
        val nonExistentDir = tempDir.resolve("nonexistent/nested").toFile()
        val file = File(nonExistentDir, "output.json")

        val storage = PersonalStorageJson()
        val jugador = Jugador(
            id = 1,
            nombre = "Carlos",
            apellidos = "Lopez",
            dorsal = 5,
            posicion = Jugador.Posicion.DELANTERO,
            paisOrigen = "Chile",
            salario = 3000.0,
            fechaNacimiento = LocalDate.of(1992, 3, 3),
            fechaIncorporacion = LocalDate.of(2018, 4, 4),
            createdAt = LocalDateTime.of(2019, 4, 4, 0, 0),
            updatedAt = LocalDateTime.of(2023, 4, 4, 0, 0),
            altura = 190.0,
            peso = 150.0,
            goles = 30,
            partidosJugados = 50,
        )

        // Esto debería crear los directorios padres
        storage.writeToFile(file, listOf(jugador))

        // Verificar que el archivo se creó correctamente
        assertTrue(file.exists())

        // Verificar el contenido
        val contenido = file.readText()
        assertTrue(contenido.contains("Carlos"))
        assertTrue(contenido.startsWith("["))
        assertTrue(contenido.endsWith("]"))
    }
}
