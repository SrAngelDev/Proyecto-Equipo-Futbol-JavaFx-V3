package srangeldev.proyectoequipofutboljavafx.newteam.validator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@ExtendWith(MockitoExtension::class)
class FileValidatorTest {

    private lateinit var validator: FileValidator

    @BeforeEach
    fun setUp() {
        validator = FileValidator()
    }

    /* ---------- VALIDACIÓN DE PROPIEDADES BÁSICAS ---------- */

    @Test
    fun `fichero inexistente lanza excepción`() {
        val phantom = File("no_existo.json")
        val ex = assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(phantom)
        }
        assertTrue(ex.message!!.contains("no existe"))
    }

    @Test
    fun `directorio en lugar de fichero lanza excepción`(@TempDir temp: Path) {
        val dir = Files.createDirectory(temp.resolve("carpeta")).toFile()
        val ex = assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(dir)
        }
        println("Mensaje de error actual: ${ex.message}")
        assertTrue(ex.message!!.contains("No es un fichero"))
    }

    @Test
    fun `sin permisos de lectura lanza excepción usando mock`() {
        val unreadable: File = mock {
            on { exists() } doReturn true
            on { isFile } doReturn true
            on { canRead() } doReturn false
        }

        val ex = assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(unreadable)
        }
        assertTrue(ex.message!!.contains("No se puede leer"))
    }

    @Test
    fun `fichero vacío lanza excepción`(@TempDir temp: Path) {
        val empty = temp.resolve("vacío.json").toFile().apply { writeText("") }
        val ex = assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(empty)
        }
        assertTrue(ex.message!!.contains("está vacío"))
    }

    /* ---------------------- JSON ---------------------- */

    @Nested
    inner class Json() {
        @TempDir
        lateinit var temp: Path

        @Test
        fun `JSON válido no lanza excepción`() {
            create(temp, "ok.json", """[{"id":1}]""").also {
                assertDoesNotThrow { validator.validate(it) }
            }
        }

        @Test
        fun `JSON mal formado lanza excepción`() {
            val json = create(temp, "bad.json", "no es json")
            assertThrows(PersonalException.PersonalStorageException::class.java) {
                validator.validate(json)
            }
        }
    }

    /* ---------------------- XML ---------------------- */

    @Nested
    inner class Xml() {
        @TempDir
        lateinit var temp: Path


        @Test
        fun `XML válido no lanza excepción`() {
            create(temp, "ok.xml", """<?xml version="1.0"?><root/>""").also {
                assertDoesNotThrow { validator.validate(it) }
            }
        }

        @Test
        fun `XML mal formado lanza excepción`() {
            val xml = create(temp, "bad.xml", "<root>")
            assertThrows(PersonalException.PersonalStorageException::class.java) {
                validator.validate(xml)
            }
        }
    }

    /* ---------------------- CSV ---------------------- */

    @Nested
    inner class Csv() {
        @TempDir
        lateinit var temp: Path


        @Test
        fun `CSV correcto no lanza excepción`() {
            create(temp, "ok.csv", "a,b\n1,2").also {
                assertDoesNotThrow { validator.validate(it) }
            }
        }

        @Test
        fun `CSV con número de columnas desigual lanza excepción`() {
            val csv = create(temp, "bad.csv", "a,b\n1,2,3")
            assertThrows(PersonalException.PersonalStorageException::class.java) {
                validator.validate(csv)
            }
        }

        @Test
        fun `CSV con línea inicial vacía lanza excepción`() {
            val csv = create(temp, "blank.csv", "\n1,2")
            assertThrows(PersonalException.PersonalStorageException::class.java) {
                validator.validate(csv)
            }
        }
    }

    /* ---------------- EXTENSIÓN NO SOPORTADA --------------- */

    @Test
    fun `extensión desconocida lanza excepción`(@TempDir temp: Path) {
        val file = create(temp, "sinSoporte.txt", "dummy")
        val ex = assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(file)
        }
        assertTrue(ex.message!!.contains("no tiene extensión XML"))
    }

    /* ------------------- HELPERS ------------------- */

    private fun create(dir: Path, fileName: String, content: String): File =
        dir.resolve(fileName).toFile().apply { writeText(content) }
}