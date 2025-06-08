package srangeldev.proyectoequipofutboljavafx.newteam.validator

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import java.time.LocalDate

internal class ConvocatoriaValidatorTest {

    private val validator = ConvocatoriaValidator()

    @Test
    fun `validate should throw PersonalNotFoundException for negative ID`() {
        val convocatoria = Convocatoria(
            id = -1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Convocatoria válida",
            equipoId = 1,
            entrenadorId = 1
        )
        assertThrows(PersonalException.PersonalNotFoundException::class.java) {
            validator.validate(convocatoria)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for empty description`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "",
            equipoId = 1,
            entrenadorId = 1
        )
        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(convocatoria)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for past date`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().minusDays(1),
            descripcion = "Convocatoria válida",
            equipoId = 1,
            entrenadorId = 1
        )
        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(convocatoria)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for non-positive team ID`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Convocatoria válida",
            equipoId = 0,
            entrenadorId = 1
        )
        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(convocatoria)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for non-positive coach ID`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Convocatoria válida",
            equipoId = 1,
            entrenadorId = 0
        )
        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(convocatoria)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for more than 18 jugadores`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Convocatoria válida",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = List(19) { it + 1 }
        )
        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(convocatoria)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for more than 11 titulares`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Convocatoria válida",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = List(18) { it + 1 },
            titulares = List(12) { it + 1 }
        )
        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(convocatoria)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException if titulares are not in jugadores`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Convocatoria válida",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = List(18) { it + 1 },
            titulares = List(5) { it + 19 }
        )
        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(convocatoria)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for duplicate jugadores`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Convocatoria válida",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = List(17) { it + 1 } + 1
        )
        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(convocatoria)
        }
    }

    @Test
    fun `validate should throw PersonalStorageException for duplicate titulares`() {
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now().plusDays(1),
            descripcion = "Convocatoria válida",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = List(18) { it + 1 },
            titulares = List(10) { it + 1 } + 1
        )
        assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(convocatoria)
        }
    }
}