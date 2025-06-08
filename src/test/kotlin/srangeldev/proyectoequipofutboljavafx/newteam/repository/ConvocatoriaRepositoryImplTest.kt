package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import srangeldev.proyectoequipofutboljavafx.newteam.dao.ConvocatoriaDao
import srangeldev.proyectoequipofutboljavafx.newteam.dao.ConvocatoriaEntity
import srangeldev.proyectoequipofutboljavafx.newteam.dao.JugadorConvocadoDao
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.time.LocalDate

class ConvocatoriaRepositoryImplTest {

    private val personalRepository: PersonalRepository = mock()
    private val convocatoriaDao: ConvocatoriaDao = mock()
    private val jugadorConvocadoDao: JugadorConvocadoDao = mock()
    private val repository = ConvocatoriaRepositoryImpl(personalRepository, convocatoriaDao, jugadorConvocadoDao)

    @Test
    fun `getAll should return a list of convocatorias when data exists`() {
        val convocatoriaEntities = listOf(
            ConvocatoriaEntity(1, LocalDate.now(), "Descripcion", 1, 1),
            ConvocatoriaEntity(2, LocalDate.now(), "Descripcion 2", 1, 1)
        )
        whenever(convocatoriaDao.findAll()).thenReturn(convocatoriaEntities)
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(any())).thenReturn(emptyList())
        whenever(jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(any())).thenReturn(emptyList())

        val result = repository.getAll()

        assertEquals(2, result.size)
        verify(convocatoriaDao).findAll()
    }

    @Test
    fun `getAll should return empty list when no data exists`() {
        whenever(convocatoriaDao.findAll()).thenReturn(emptyList())

        val result = repository.getAll()

        assertTrue(result.isEmpty())
        verify(convocatoriaDao).findAll()
    }

    @Test
    fun `getById should return the convocatoria when it exists`() {
        val convocatoriaEntity = ConvocatoriaEntity(1, LocalDate.now(), "Descripcion", 1, 1)
        whenever(convocatoriaDao.findById(1)).thenReturn(convocatoriaEntity)
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(1)).thenReturn(emptyList())
        whenever(jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(1)).thenReturn(emptyList())

        val result = repository.getById(1)

        assertNotNull(result)
        assertEquals(1, result?.id)
        verify(convocatoriaDao).findById(1)
    }

    @Test
    fun `getById should return null when convocatoria does not exist`() {
        whenever(convocatoriaDao.findById(1)).thenReturn(null)

        val result = repository.getById(1)

        assertNull(result)
        verify(convocatoriaDao).findById(1)
    }

    @Test
    fun `save should create a new convocatoria and return it`() {
        val convocatoria = Convocatoria(0, LocalDate.now(), "Descripcion", 1, 1, listOf(1, 2), listOf(1))
        val convocatoriaEntity = ConvocatoriaEntity.fromConvocatoria(convocatoria.copy(id = 1))
        whenever(convocatoriaDao.save(any())).thenReturn(1)
        whenever(convocatoriaDao.findById(1)).thenReturn(convocatoriaEntity)
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(1)).thenReturn(listOf(1, 2))
        whenever(jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(1)).thenReturn(listOf(1))

        val result = repository.save(convocatoria)

        assertNotNull(result)
        assertEquals(1, result.id)
        verify(convocatoriaDao).save(any())
        verify(jugadorConvocadoDao, times(2)).save(any())
    }

    @Test
    fun `update should modify existing convocatoria and return updated version`() {
        val convocatoria = Convocatoria(1, LocalDate.now(), "Updated", 1, 1, listOf(1, 2), listOf(1))
        val convocatoriaEntity = ConvocatoriaEntity.fromConvocatoria(convocatoria)
        whenever(convocatoriaDao.findById(1)).thenReturn(convocatoriaEntity)
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(1)).thenReturn(listOf(1, 2))
        whenever(jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(1)).thenReturn(listOf(1))
        whenever(convocatoriaDao.update(any())).thenReturn(1)

        val result = repository.update(1, convocatoria)

        assertNotNull(result)
        assertEquals("Updated", result?.descripcion)
        verify(convocatoriaDao).update(any())
        verify(jugadorConvocadoDao).deleteByConvocatoriaId(1)
        verify(jugadorConvocadoDao, times(2)).save(any())
    }

    @Test
    fun `delete should remove convocatoria and return it`() {
        val convocatoriaEntity = ConvocatoriaEntity(1, LocalDate.now(), "Descripcion", 1, 1)
        whenever(convocatoriaDao.findById(1)).thenReturn(convocatoriaEntity)
        whenever(convocatoriaDao.delete(1)).thenReturn(1)

        val result = repository.delete(1)

        assertNotNull(result)
        assertEquals(1, result?.id)
        verify(convocatoriaDao).delete(1)
        verify(jugadorConvocadoDao).deleteByConvocatoriaId(1)
    }

    @Test
    fun `getJugadoresConvocados should return list of jugadores`() {
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(1)).thenReturn(listOf(1, 2))
        val jugador1 = mock<Jugador> { on { id } doReturn 1 }
        val jugador2 = mock<Jugador> { on { id } doReturn 2 }
        whenever(personalRepository.getById(1)).thenReturn(jugador1)
        whenever(personalRepository.getById(2)).thenReturn(jugador2)

        val result = repository.getJugadoresConvocados(1)

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals(2, result[1].id)
        verify(jugadorConvocadoDao).getJugadoresIdsByConvocatoriaId(1)
    }

    @Test
    fun `getJugadoresTitulares should return list of titulares`() {
        whenever(jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(1)).thenReturn(listOf(1, 2))
        val jugador1 = mock<Jugador> { on { id } doReturn 1 }
        val jugador2 = mock<Jugador> { on { id } doReturn 2 }
        whenever(personalRepository.getById(1)).thenReturn(jugador1)
        whenever(personalRepository.getById(2)).thenReturn(jugador2)

        val result = repository.getJugadoresTitulares(1)

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals(2, result[1].id)
        verify(jugadorConvocadoDao).getTitularesIdsByConvocatoriaId(1)
    }

    @Test
    fun `getJugadoresTitulares should return empty list when no titulares`() {
        whenever(jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(1)).thenReturn(emptyList())

        val result = repository.getJugadoresTitulares(1)

        assertTrue(result.isEmpty())
        verify(jugadorConvocadoDao).getTitularesIdsByConvocatoriaId(1)
    }

    @Test
    fun `getJugadoresTitulares should return empty list on exception`() {
        whenever(jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(any())).thenThrow(RuntimeException("Database error"))

        val result = repository.getJugadoresTitulares(1)

        assertTrue(result.isEmpty())
        verify(jugadorConvocadoDao).getTitularesIdsByConvocatoriaId(1)
    }

    @Test
    fun `getJugadoresSuplentes should return list of suplentes`() {
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(1)).thenReturn(listOf(1, 2, 3))
        whenever(jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(1)).thenReturn(listOf(1, 2))
        val jugador1 = mock<Jugador> { on { id } doReturn 3 }
        whenever(personalRepository.getById(3)).thenReturn(jugador1)

        val result = repository.getJugadoresSuplentes(1)

        assertEquals(1, result.size)
        assertEquals(3, result[0].id)
        verify(jugadorConvocadoDao).getJugadoresIdsByConvocatoriaId(1)
        verify(jugadorConvocadoDao).getTitularesIdsByConvocatoriaId(1)
    }

    @Test
    fun `getJugadoresSuplentes should return empty list when no suplentes`() {
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(1)).thenReturn(listOf(1, 2))
        whenever(jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(1)).thenReturn(listOf(1, 2))

        val result = repository.getJugadoresSuplentes(1)

        assertTrue(result.isEmpty())
        verify(jugadorConvocadoDao).getJugadoresIdsByConvocatoriaId(1)
        verify(jugadorConvocadoDao).getTitularesIdsByConvocatoriaId(1)
    }

    @Test
    fun `getJugadoresSuplentes should return empty list on exception`() {
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(any())).thenThrow(RuntimeException("Database error"))

        val result = repository.getJugadoresSuplentes(1)

        assertTrue(result.isEmpty())
        verify(jugadorConvocadoDao).getJugadoresIdsByConvocatoriaId(1)
    }

    @Test
    fun `getJugadoresNoConvocados should return list of non-convocados jugadores`() {
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(1)).thenReturn(listOf(1, 2))
        val jugador1 = mock<Jugador> { on { id } doReturn 1 }
        val jugador2 = mock<Jugador> { on { id } doReturn 2 }
        val jugador3 = mock<Jugador> { on { id } doReturn 3 } // Not convocado
        whenever(personalRepository.getAll()).thenReturn(listOf(jugador1, jugador2, jugador3))

        val result = repository.getJugadoresNoConvocados(1)

        assertEquals(1, result.size)
        assertEquals(3, result[0].id)
        verify(jugadorConvocadoDao).getJugadoresIdsByConvocatoriaId(1)
        verify(personalRepository).getAll()
    }

    @Test
    fun `getJugadoresNoConvocados should return empty list when all jugadores are convocados`() {
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(1)).thenReturn(listOf(1, 2, 3))
        val jugador1 = mock<Jugador> { on { id } doReturn 1 }
        val jugador2 = mock<Jugador> { on { id } doReturn 2 }
        val jugador3 = mock<Jugador> { on { id } doReturn 3 }
        whenever(personalRepository.getAll()).thenReturn(listOf(jugador1, jugador2, jugador3))

        val result = repository.getJugadoresNoConvocados(1)

        assertTrue(result.isEmpty())
        verify(jugadorConvocadoDao).getJugadoresIdsByConvocatoriaId(1)
        verify(personalRepository).getAll()
    }

    @Test
    fun `getJugadoresNoConvocados should return empty list on exception`() {
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(any())).thenThrow(RuntimeException("Database error"))

        val result = repository.getJugadoresNoConvocados(1)

        assertTrue(result.isEmpty())
        verify(jugadorConvocadoDao).getJugadoresIdsByConvocatoriaId(1)
    }

    @Test
    fun `getByEquipoId should return convocatorias for specific equipoId`() {
        val equipoId = 1
        val convocatoriaEntities = listOf(
            ConvocatoriaEntity(1, LocalDate.now(), "Descripcion 1", equipoId, 1),
            ConvocatoriaEntity(2, LocalDate.now(), "Descripcion 2", equipoId, 1)
        )
        whenever(convocatoriaDao.findByEquipoId(equipoId)).thenReturn(convocatoriaEntities)
        whenever(jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(any())).thenReturn(emptyList())
        whenever(jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(any())).thenReturn(emptyList())

        val result = repository.getByEquipoId(equipoId)

        assertEquals(2, result.size)
        verify(convocatoriaDao).findByEquipoId(equipoId)
    }

    @Test
    fun `getByEquipoId should return empty list when no convocatorias exist for equipoId`() {
        val equipoId = 2
        whenever(convocatoriaDao.findByEquipoId(equipoId)).thenReturn(emptyList())

        val result = repository.getByEquipoId(equipoId)

        assertTrue(result.isEmpty())
        verify(convocatoriaDao).findByEquipoId(equipoId)
    }

    @Test
    fun `getByEquipoId should return empty list on exception`() {
        val equipoId = 3
        whenever(convocatoriaDao.findByEquipoId(equipoId)).thenThrow(RuntimeException("Database error"))

        val result = repository.getByEquipoId(equipoId)

        assertTrue(result.isEmpty())
        verify(convocatoriaDao).findByEquipoId(equipoId)
    }

    @Test
    fun `validarConvocatoria should return true for a valid convocatoria`() {
        val jugadores = (1..18).toList()
        val titulares = jugadores.subList(0, 11)
        whenever(personalRepository.getById(any())).thenAnswer { invocation ->
            val id = invocation.arguments[0] as Int
            mock<Jugador> { on { id } doReturn id; on { posicion } doReturn if (id == 1 || id == 2) Jugador.Posicion.PORTERO else Jugador.Posicion.DEFENSA }
        }

        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now(),
            descripcion = "Valid convocatoria",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = jugadores,
            titulares = titulares
        )

        val result = repository.validarConvocatoria(convocatoria)

        assertTrue(result)
    }

    @Test
    fun `validarConvocatoria should return false when convocatoria has more than 18 jugadores`() {
        val jugadores = (1..19).toList()
        val titulares = jugadores.subList(0, 11)

        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now(),
            descripcion = "Invalid convocatoria",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = jugadores,
            titulares = titulares
        )

        val result = repository.validarConvocatoria(convocatoria)

        assertFalse(result)
    }

    @Test
    fun `validarConvocatoria should return false when convocatoria has less than 11 titulares`() {
        val jugadores = (1..18).toList()
        val titulares = jugadores.subList(0, 10)

        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now(),
            descripcion = "Invalid convocatoria",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = jugadores,
            titulares = titulares
        )

        val result = repository.validarConvocatoria(convocatoria)

        assertFalse(result)
    }

    @Test
    fun `validarConvocatoria should return false when not all titulares are included in jugadores`() {
        val jugadores = (1..18).toList()
        val titulares = (19..29).toList()

        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now(),
            descripcion = "Invalid convocatoria",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = jugadores,
            titulares = titulares
        )

        val result = repository.validarConvocatoria(convocatoria)

        assertFalse(result)
    }

    @Test
    fun `validarConvocatoria should return false when convocatoria has more than 2 porteros`() {
        val jugadores = (1..18).toList()
        val titulares = jugadores.subList(0, 11)
        whenever(personalRepository.getById(any())).thenAnswer { invocation ->
            val id = invocation.arguments[0] as Int
            mock<Jugador> { on { id } doReturn id; on { posicion } doReturn Jugador.Posicion.PORTERO }
        }

        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.now(),
            descripcion = "Invalid convocatoria",
            equipoId = 1,
            entrenadorId = 1,
            jugadores = jugadores,
            titulares = titulares
        )

        val result = repository.validarConvocatoria(convocatoria)

        assertFalse(result)
    }
}