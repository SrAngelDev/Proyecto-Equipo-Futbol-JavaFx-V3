package srangeldev.proyectoequipofutboljavafx.newteam.storage


import org.junit.jupiter.api.*
import org.junit.jupiter.api.io.TempDir
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.io.path.*
import kotlin.test.Test
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PersonalStorageJson")
class PersonalStorageJsonTest {

    private val storage = PersonalStorageJson()

    /* ==========   datos base   ========== */

    private val ahora = LocalDateTime.now()

    private val entrenador = Entrenador(
        id = 1,
        nombre = "Pep",
        apellidos = "Guardiola",
        fechaNacimiento = LocalDate.of(1971, 1, 18),
        fechaIncorporacion = LocalDate.of(2023, 7, 1),
        salario = 8_000_000.0,
        paisOrigen = "España",
        createdAt = ahora,
        updatedAt = ahora,
        especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
        imagenUrl = "pep.png"
    )

    private val jugador = Jugador(
        id = 10,
        nombre = "Leo",
        apellidos = "Messi",
        fechaNacimiento = LocalDate.of(1987, 6, 24),
        fechaIncorporacion = LocalDate.of(2023, 7, 1),
        salario = 40_000_000.0,
        paisOrigen = "Argentina",
        createdAt = ahora,
        updatedAt = ahora,
        posicion = Jugador.Posicion.DELANTERO,
        dorsal = 10,
        altura = 1.69,
        peso = 67.0,
        goles = 30,
        partidosJugados = 35,
        imagenUrl = "leo.png"
    )

    /* ==========   utilidades   ========== */

    private fun Path.writeJson(text: String): Path = apply { writeText(text) }

    private fun plantillaJugadorJson() = """
        {
          "id": 10,
          "nombre": "Leo",
          "apellidos": "Messi",
          "fecha_nacimiento": "1987-06-24",
          "fecha_incorporacion": "2023-07-01",
          "salario": 40000000.0,
          "pais": "Argentina",
          "rol": "Jugador",
          "posicion": "DELANTERO", 
          "dorsal": 10,
          "altura": 1.69,
          "peso": 67.0,
          "goles": 30,
          "partidos_jugados": 35,
          "imagen": "leo.png"
        }
    """.trimIndent()

    private fun plantillaEntrenadorJson() = """
        {
          "id": 1,
          "nombre": "Pep",
          "apellidos": "Guardiola",
          "fecha_nacimiento": "1971-01-18",
          "fecha_incorporacion": "2023-07-01",
          "salario": 8000000.0,
          "pais": "España",
          "rol": "Entrenador",
          "especialidad": "ENTRENADOR_PRINCIPAL",
          "imagen": "pep.png"
        }
    """.trimIndent()

    /* *********************************************************************
     *  CASOS DE LECTURA CORRECTA  
     * ******************************************************************* */
    @Nested
    inner class LecturaOK {

        @TempDir
        lateinit var dir: Path

        @Test
        fun `lee entrenador y jugador en json valido`() {
            val file = dir.resolve("plantilla.json").writeJson(
                "[${plantillaEntrenadorJson()}, ${plantillaJugadorJson()}]"
            )

            val lista = storage.readFromFile(file.toFile())

            assertEquals(2, lista.size)
            assertTrue(lista.any { it.nombre == "Pep" })
            assertTrue(lista.any { it.nombre == "Leo" })
        }

        @Test
        fun `usa archivo alternativo con extension json si el solicitado no existe`() {
            val solicitado = dir.resolve("equipo.data").toFile()
            val alternativo = dir.resolve("equipo.json")
                .writeJson("[${plantillaEntrenadorJson()}]")

            val lista = storage.readFromFile(solicitado)

            assertEquals(1, lista.size)
            assertTrue(lista[0] is Entrenador)
            assertTrue(alternativo.readText().isNotEmpty()) // comprobamos que lo leyó
        }

        @Test
        fun `crea archivo vacio y devuelve lista vacia si no existe`() {
            val nuevo = dir.resolve("nuevo.json").toFile()

            val lista = storage.readFromFile(nuevo)

            assertTrue(lista.isEmpty())
            assertTrue(nuevo.exists())
            assertEquals("[]", nuevo.readText())
        }
    }

    /* *********************************************************************
     *  CAMINOS DE ERROR EN LECTURA
     * ******************************************************************* */
    @Nested
    inner class LecturaErrores {

        @TempDir
        lateinit var dir: Path

        @Test
        fun `lanza PersonalStorageException cuando es directorio`() {
            val file = dir.resolve("directorio").toFile().apply { mkdirs() }
            val exception = assertThrows<PersonalException.PersonalStorageException> {
                storage.readFromFile(file)
            }
            assertTrue(exception.message?.contains("No es un fichero") == true)
        }

        @Test
        fun `lanza PersonalStorageException cuando archivo vacio`() {
            val file = dir.resolve("vacio.json").writeJson("").toFile()
            val exception = assertThrows<PersonalException.PersonalStorageException> {
                storage.readFromFile(file)
            }
            assertTrue(exception.message?.contains("vacío") == true)
        }

        @Test
        fun `lanza PersonalStorageException cuando contenido no es JSON`() {
            val file = dir.resolve("malo.txt").writeJson("no-json").toFile()
            val exception = assertThrows<PersonalException.PersonalStorageException> {
                storage.readFromFile(file)
            }
            assertTrue(exception.message?.contains("no parece ser JSON") == true)
        }

        @Test
        fun `lanza PersonalStorageException cuando JSON mal formado`() {
            val file = dir.resolve("mal.json").writeJson("{").toFile()
            val exception = assertThrows<PersonalException.PersonalStorageException> {
                storage.readFromFile(file)
            }
            assertTrue(exception.message?.contains("Error en el almacenamiento") == true)
        }
    }
    /* *********************************************************************
     *  CAMINOS DE ESCRITURA
     * ******************************************************************* */
    @Nested
    inner class Escritura {

        @TempDir
        lateinit var dir: Path

        @Test
        fun `escribe archivo prettyPrint incluso en carpetas inexistentes`() {
            val destino = dir.resolve("deep/nested/personal.JSON").toFile() // mayus-minús test
            storage.writeToFile(destino, listOf(entrenador, jugador))

            assertTrue(destino.exists())
            assertTrue(destino.readText().contains("\"rol\": \"Entrenador\""))
        }

        @Test
        fun `falla si la extension no es json`() {
            val destino = dir.resolve("equipo.cfg").toFile()

            val exception = assertThrows<PersonalException.PersonalStorageException> {
                storage.writeToFile(destino, emptyList())
            }
            assertTrue(exception.message?.contains("extensión JSON") == true)
        }

        @Test
        fun `falla si la lista contiene tipo no soportado`() {
            val objetoRaro = object : srangeldev.proyectoequipofutboljavafx.newteam.models.Personal(
                id = 99,
                nombre = "???",
                apellidos = "???",
                fechaNacimiento = LocalDate.now(),
                fechaIncorporacion = LocalDate.now(),
                salario = 0.0,
                paisOrigen = "Nowhere",
                createdAt = ahora,
                updatedAt = ahora
            ) {}

            val destino = dir.resolve("equipo.json").toFile()

            val exception = assertThrows<PersonalException.PersonalStorageException> {
                storage.writeToFile(destino, listOf(objetoRaro))
            }
            assertTrue(exception.message?.contains("no soportado") == true)
        }

        @Test
        fun `round-trip write-read mantiene datos`() {
            val destino = dir.resolve("equipo.json").toFile()
            val original = listOf(entrenador, jugador)

            storage.writeToFile(destino, original)
            val leido = storage.readFromFile(destino)

            assertEquals(original.size, leido.size)
            for (i in original.indices) {
                assertEquals(original[i].id, leido[i].id)
                assertEquals(original[i].nombre, leido[i].nombre)
                assertEquals(original[i].apellidos, leido[i].apellidos)
                assertEquals(original[i].fechaNacimiento, leido[i].fechaNacimiento)
                assertEquals(original[i].fechaIncorporacion, leido[i].fechaIncorporacion)
                assertEquals(original[i].salario, leido[i].salario)
                assertEquals(original[i].paisOrigen, leido[i].paisOrigen)
            }
        }
    }
}